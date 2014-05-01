package liberty_paintball.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ArrayBlockingQueue;

import liberty_paintball.demo.Demo;
import liberty_paintball.game.events.Event;
import liberty_paintball.game.events.PlayerHitEvent;
import liberty_paintball.game.events.PlayerMissEvent;
import liberty_paintball.game.events.PlayerOutgunnedEvent;
import liberty_paintball.game.events.Event.EventType;
import liberty_paintball.json.requests.AttackRequest;
import liberty_paintball.json.requests.JoinGameRequest;
import liberty_paintball.json.requests.LeaveGameRequest;
import liberty_paintball.json.requests.UpdateLocationRequest;
import liberty_paintball.json.responses.AttackResponse;
import liberty_paintball.json.responses.IntelResponse;
import liberty_paintball.json.responses.JoinResponse;
import liberty_paintball.mapping.MapPoint;
import static liberty_paintball.json.responses.AttackResponse.ResponseType.HIT;
import static liberty_paintball.json.responses.AttackResponse.ResponseType.INVALID_REQUEST;
import static liberty_paintball.json.responses.AttackResponse.ResponseType.MISS;
import static liberty_paintball.json.responses.AttackResponse.ResponseType.OUTGUNNED;

/**
 * @author Dave Waddling
 * 
 * The Game class is a singleton which persists between calls to the various JAX-RS endpoints. It handles
 * all of the game actions in the sequence in which they arrive and in a thread safe manner.
 *
 */
public final class Game {

	private static final double	ATTACK_RADIUS_METERS		= 12.5d;

	private static final int	GAME_INTEL_ENTRY_COUNT		= 3;

	private static final int	ACTIVITY_INTEL_QUEUE_SIZE	= 3;

	private static Game			instance					= null;


	public static final Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}
		return instance;
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private ConcurrentMap<String, Player>	mapUsernameToPlayer;
	private NameGenerator					nameGenerator;
	private Timeline						timeline;
	private ReentrantLock					lock;
	private Queue<IntelResponse>			activityIntel;
	private int								totalTurnsTaken;
	private int								totalScore;

	private Random							random;


	private Game() {
		// Create a name generator to give players unique names.
		nameGenerator = new NameGenerator();

		// Game state is maintained as map of usernames to data about players.
		mapUsernameToPlayer = new ConcurrentHashMap<String, Player>();

		// All game events are added to a timeline for the generation of news/maps.
		timeline = new Timeline();

		// Track a few stats to provide intel.
		totalTurnsTaken = 0;
		totalScore = 0;

		// List of intel which will be populated from player activities.
		activityIntel = new ArrayBlockingQueue<IntelResponse>(ACTIVITY_INTEL_QUEUE_SIZE);

		// A fair lock to ensure the multi-threaded nature of the game doesn't
		// lead to race conditions.
		lock = new ReentrantLock(true);

		// Create a random number generator for returning random bits of intel.
		random = new Random();
	}


	public JoinResponse handleJoinGameRequest(JoinGameRequest request) {
		lock.lock();
		try {
			String username = request.getUsername();
			System.out.println("***** [JOIN] ***** Player joined: " + username);

			if (mapUsernameToPlayer.containsKey(username)) {
				// TODO Log and ignore, player's already in the game.
			} else {
				// Add the player.
				PlayerIdentity playerIdentity = nameGenerator.createRandomUniqueIdentity(username);
				System.out.println("***** [ASSIGN] ***** '" + username + "' is known in game as '" + playerIdentity.getGeneratedName() + "'.");
				mapUsernameToPlayer.put(username, new Player(playerIdentity));
			}
			return new JoinResponse(mapUsernameToPlayer.get(username).getIdentity());
		} finally {
			lock.unlock();
		}
	}


	public void handleLeaveGameRequest(LeaveGameRequest request) {
		lock.lock();
		try {
			String username = request.getUsername();
			System.out.println("***** [LEAVE] ***** Player left: " + username);

			if (!mapUsernameToPlayer.containsKey(username)) {
				// TODO Log and ignore, player isn't in the game.
			} else {
				// Remove the player.
				mapUsernameToPlayer.remove(username);

				// Don't add an event ATM.
				// timeline.addEvent(new PlayerLeaveEvent(username));
			}
		} finally {
			lock.unlock();
		}
	}


	public void handleUpdateLocationRequest(UpdateLocationRequest request) {
		lock.lock();
		try {
			String username = request.getUsername();
			double latitude = request.getLatitude();
			double longitude = request.getLongitude();

			System.out.println("***** [UPDATE] ***** Received a location update request from " + username + " @ " + latitude + "  " + longitude);

			if (!mapUsernameToPlayer.containsKey(username)) {
				System.out.println("WARNING Updating location for player who doesn't exist. Ignoring.");
				return;
			}

			Player updatingPlayer = mapUsernameToPlayer.get(username);
			updatingPlayer.setLocation(new Location(latitude, longitude));

			List<Player> playersInRange = findPlayersInRange(updatingPlayer);
			if (playersInRange.size() > 0) {
				enqueueActivityIntel(new IntelResponse(MessageGenerator.generateCloseEncounterMessage(updatingPlayer, playersInRange), true));
			}
		} finally {
			lock.unlock();
		}
	}


	public AttackResponse handleAttackRequest(AttackRequest request) {
		lock.lock();
		try {
			String attackingPlayerUsername = request.getUsername();
			double latitude = request.getLatitude();
			double longitude = request.getLongitude();

			System.out.println("***** [ATTACK] ***** Received an attack from: " + attackingPlayerUsername + " @ " + latitude + " | " + longitude);

			// If the player doesn't exist return a response that the request was invalid.
			if (!mapUsernameToPlayer.containsKey(attackingPlayerUsername)) {
				System.out.println("WARNING No such user has joined the game. Returning invalid request response.");
				return new AttackResponse(INVALID_REQUEST, null, null, null, null);
			}

			// Update the attacker's location.
			Player attackingPlayer = mapUsernameToPlayer.get(attackingPlayerUsername);
			attackingPlayer.setLocation(new Location(latitude, longitude));
			System.out.println("INFO Updated attacker's location to: " + attackingPlayer.getLocation());

			// If the player is eliminated from the game then they shouldn't be having a turn.
			if (attackingPlayer.isEliminated()) {
				System.out.println("WARNING Attacker is eliminated.  Returning invalid request response.");
				return new AttackResponse(INVALID_REQUEST, null, null, null, null);
			}

			// It's a valid attack request, so increment the number of turns taken.
			totalTurnsTaken++;

			// Find the players in range of the attacking player.
			List<Player> playersInRange = findPlayersInRange(attackingPlayer);

			// Resolve the attack based on the number of players in range:
			// o Exactly 1 player in range -> The attacker has eliminated the other player from the game.
			// o >1 player in range -> The attacker was outgunned and eliminated from the game by the other players.
			// o 0 players in range -> The attacker has missed as there was nobody to hit.
			switch (playersInRange.size()) {
			case 1:
				return resolveHit(attackingPlayer, playersInRange.get(0));
			case 2:
				return resolveOutgunned(attackingPlayer, playersInRange);
			default:
				return resolveMiss(attackingPlayer);
			}
		} finally {
			lock.unlock();
		}
	}


	public JoinResponse handleIDRequest(JoinGameRequest request) {
		lock.lock();
		try {
			System.out.println("INFO ***** ID REQUEST RECEIVED *****");
			Player player = mapUsernameToPlayer.get(request.getUsername());
			if (player != null) {
				System.out.println("INFO An existing player was found for username '" + request.getUsername() + "', their generated name is '" + player.getGeneratedName() + "'. Returning player's identity.");
				return new JoinResponse(player.getIdentity());
			} else {
				System.out.println("INFO No player exists for username '" + request.getUsername() + "'. Returning null.");
				return new JoinResponse(null);
			}
		} finally {
			lock.unlock();
		}
	}


	private AttackResponse resolveHit(Player attackingPlayer, Player participantPlayer) {
		// Get the player who's been eliminated from the game and update their state.
		participantPlayer.eliminate();
		attackingPlayer.incrementScore();
		totalScore++;

		Event event = new PlayerHitEvent(attackingPlayer, participantPlayer);
		timeline.addEvent(event);

		// The attack response expects a list of participant (i.e. not the attacker) player identities.
		List<PlayerIdentity> playerList = new ArrayList<PlayerIdentity>(1);
		playerList.add(participantPlayer.getIdentity());
		System.out.println("+++ [HIT] +++ " + MessageGenerator.generateAttackerMessage(event));
		return new AttackResponse(HIT, attackingPlayer.getIdentity(), MessageGenerator.generateAttackerMessage(event), playerList, MessageGenerator.generateParticipantMessage(event));
	}


	private AttackResponse resolveOutgunned(Player attackingPlayer, List<Player> playersInRange) {
		// The attacking player is outgunned and thus eliminated from the game.
		attackingPlayer.eliminate();
		// Increment the total score for the game and for each player which outgunned the attacker.
		totalScore++;
		for (Player player : playersInRange) {
			player.incrementScore();
		}

		Event event = new PlayerOutgunnedEvent(attackingPlayer, playersInRange);
		timeline.addEvent(event);

		System.out.println("+++ [OUTGUNNED] +++ " + MessageGenerator.generateAttackerMessage(event));
		List<PlayerIdentity> playersInRangeIdentities = new ArrayList<PlayerIdentity>(playersInRange.size());
		for (Player playerInRange : playersInRange) {
			playersInRangeIdentities.add(playerInRange.getIdentity());
		}
		return new AttackResponse(OUTGUNNED, attackingPlayer.getIdentity(), MessageGenerator.generateAttackerMessage(event), playersInRangeIdentities, MessageGenerator.generateParticipantMessage(event));
	}


	private AttackResponse resolveMiss(Player attackingPlayer) {
		Event event = new PlayerMissEvent(attackingPlayer);
		timeline.addEvent(event);
		System.out.println("+++ [MISS] +++ " + MessageGenerator.generateAttackerMessage(event));
		return new AttackResponse(MISS, attackingPlayer.getIdentity(), MessageGenerator.generateAttackerMessage(event), null, null);
	}


	public IntelResponse handleIntelRequest() {
		lock.lock();
		try {
			System.out.println("***** [INTEL] ***** Request recieved.");

			// Return game activity intel in preference to game stats as it's
			// likely to be more exciting for the players.
			if (activityIntel.size() > 0) {
				return activityIntel.poll();
			} else {
				switch (random.nextInt(GAME_INTEL_ENTRY_COUNT)) {
				case 0:
					return new IntelResponse(MessageGenerator.generatePlayersInGameMessage(mapUsernameToPlayer.size()), false);
				case 1:
					return new IntelResponse(MessageGenerator.generateTurnsTakenMessage(totalTurnsTaken), false);
				case 2:
					return new IntelResponse(MessageGenerator.generatePlayersEliminatedMessage(totalScore), false);
				default:
					return null;
				}
			}
		} finally {
			lock.unlock();
		}
	}


	public List<MapPoint> getMapData(boolean isOmniscient) {
		lock.lock();
		try {
			if (isOmniscient) {
				List<MapPoint> mapData = new ArrayList<MapPoint>();
				mapData.addAll(timeline.getRealtimeMapData());
				// Add the players' latest locations to the map.
				for (Map.Entry<String, Player> entryUsernameToPlayer : mapUsernameToPlayer.entrySet()) {
					long locationUpdateTime = entryUsernameToPlayer.getValue().getLocation().getUpdatedTimeMillis();
					Location location = entryUsernameToPlayer.getValue().getLocation();
					mapData.add(new MapPoint(EventType.LOCATION_UPDATE, locationUpdateTime, location, entryUsernameToPlayer.getValue().getIdentity(), null, null));
				}
				return mapData;
			} else {
				return timeline.getDelayedMapData();
			}
		} finally {
			lock.unlock();
		}
	}


	private List<Player> findPlayersInRange(Player attackingPlayer) {

		List<Player> playersInRange = new ArrayList<Player>();

		// Get the location of the player in question.
		Location attackerLocation = attackingPlayer.getLocation();

		if (attackerLocation == null) {
			System.out.println("WARNING Primary username being used for range finding had no location set. Returning no players in range.");
			return new ArrayList<Player>(0);
		}

		// Iterate over the players in game and build a list of player usernames for players
		// who are in range of the attacking player.
		for (Map.Entry<String, Player> usernameToPlayer : mapUsernameToPlayer.entrySet()) {
			String playerUsername = usernameToPlayer.getKey();

			// A player cannot be in range of themselves, so skip that player.
			if (playerUsername.equals(attackingPlayer.getUsername())) {
				System.out.println("INFO Skipping range finding between primary username and themself.");
				continue;
			}

			// Skip range finding on the other player if they have already been eliminated from the game.
			if (usernameToPlayer.getValue().isEliminated()) {
				System.out.println("INFO Skipping '" + playerUsername + "' because that player has already been eliminated from the game.");
				continue;
			}

			// Get the location of the other player.
			Location playerLocation = usernameToPlayer.getValue().getLocation();
			System.out.println("INFO Player '" + playerUsername + "' found at location: " + playerLocation);

			// Skip range finding on the other player if their location is null (as it can be when joining the game).
			if (playerLocation == null) {
				System.out.println("INFO Player '" + playerUsername + "' has no location set. Did they just join the game? Skipping this player.");
				continue;
			}

			// Calculate the distance between the two players.
			double distanceBetweenPlayers = Location.distanceBetweenLocationsMeters(attackerLocation, playerLocation);

			System.out.println("INFO Player '" + playerUsername + "' was calculated to be " + distanceBetweenPlayers + " meters away from the attacker.");
			// If the player in range, add their username to the list.
			if (distanceBetweenPlayers < ATTACK_RADIUS_METERS) {
				System.out.println("INFO Player '" + playerUsername + "' is in range. Their distance from the primary username is within the range radius of " + ATTACK_RADIUS_METERS + " meters.");
				playersInRange.add(usernameToPlayer.getValue());
			} else {
				System.out.println("INFO Player '" + playerUsername + "' is NOT in range. Their distance from the primary username is outside the range radius of " + ATTACK_RADIUS_METERS + " meters.");
			}
		}

		return playersInRange;
	}


	private void enqueueActivityIntel(IntelResponse intelResponse) {
		// Pop the oldest bit of intel off the queue if it's full before trying
		// to add the new entry.
		if (activityIntel.size() == ACTIVITY_INTEL_QUEUE_SIZE) {
			activityIntel.poll();
		}
		activityIntel.offer(intelResponse);
	}


	public String reset() {
		lock.lock();
		try {
			System.out.println("***** [RESET] ***** Request recieved.");

			Demo.getInstance().reset();

			mapUsernameToPlayer.clear();
			activityIntel.clear();
			nameGenerator = new NameGenerator();
			timeline = new Timeline();
			totalTurnsTaken = 0;
			totalScore = 0;

			return "Game state has been reset.";
		} finally {
			lock.unlock();
		}
	}


	public String handleDemo() {
		return Demo.getInstance().doNextStep();
	}

}
