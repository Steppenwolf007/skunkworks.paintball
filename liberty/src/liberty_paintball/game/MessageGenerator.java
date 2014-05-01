package liberty_paintball.game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import liberty_paintball.game.events.Event;
import liberty_paintball.game.events.PlayerHitEvent;
import liberty_paintball.game.events.PlayerMissEvent;
import liberty_paintball.game.events.PlayerOutgunnedEvent;

/**
 * @author Dave Waddling
 * 
 * A utility class for generating the various Strings which are displayed to players.
 *
 */
public class MessageGenerator {



	private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	private static Random random = new Random();

	// @formatter:off
	// These messages go only to the player who attempted an attack and hit an opponent.
	// The victim's name is added as a suffix to the message.
	private static final String[] ATTACKER_HIT_MESSAGES = {
		"Your paintballs fly through the air and burst all over ",
		"Your paintballs hit their mark and splatter paint all over ",
		"Your aim is true and your paintballs explode on "
	};

	// These messages go only to the player who attempted an attack but missed.
	private static final String[] ATTACKER_MISS_MESSAGES = {
		"You don't find any other players in the vicinity.",
		"You search but cannot find any other players in the area.",
		"You watch for other players but none are in this location."
	};
	
	// These messages go only to the player who attempted an attack but was outgunned.
	private static final String[] ATTACKER_OUTGUNNED_MESSAGES = {
		"As you attack you are ambushed by several other players and before you can react you are covered in paint. You have been eliminated from the game.",
		"An ambush! As you attack you are hit by paintballs from all sides! You have been eliminated from the game.",
		"You choose your time to strike but so do several other players! You hit by paintballs from all around! You have been eliminated from the game."
	};

	// These messages go only to the player who was eliminated from the game by an attacking opponent.
	// The attacker's name is added as a prefix to the message.
	private static final String[] PARTICIPANT_HIT_MESSAGES = {
		" ambushes you, covering you with paint! You have been eliminated from the game.",
		"'s paintballs explode all over you, spattering you with paint! You have been eliminated from the game.",
		" splatters you with paint. You have been eliminated from the game."
	};
	
	// These messages only go to the players who outgunned an attacking opponent.
	// The attacker's name is added as a prefix to the message.
	private static final String[] PARTICIPANT_OUTGUNNED_MESSAGES = {
		" was sneaking up on you but was splattered with paint from all around. Other players were nearby!",
		" was about to ambush you but was hit by paintballs from all sides. Other players were nearby!",
		" was about to surprise you but paintballs hit them from all around. Other players were nearby! "
	};
	

	public static String generateAttackerMessage(Event event) {
		switch (event.getEventType()) {
		case PLAYER_HIT:		return generateAttackerHitMessage((PlayerHitEvent) event);
		case PLAYER_MISS:		return generateAttackerMissMessage((PlayerMissEvent) event);
		case PLAYER_OUTGUNNED:	return generateAttackerOutgunnedMessage((PlayerOutgunnedEvent) event);
		default:				return null;
		}
	}

	
	public static String generateParticipantMessage(Event event) {
		switch (event.getEventType()) {
		case PLAYER_HIT:		return generateParticipantHitMessage((PlayerHitEvent) event);
		case PLAYER_MISS:		return null; // No participants if an attacker misses.
		case PLAYER_OUTGUNNED:	return generateParticipantOutgunnedMessage((PlayerOutgunnedEvent) event);
		default:				return null;
		}
	}
	// @formatter:on

	private static String generateAttackerHitMessage(PlayerHitEvent event) {
		String victimUsername = event.getVictim().getGeneratedName();
		return ATTACKER_HIT_MESSAGES[random.nextInt(ATTACKER_HIT_MESSAGES.length)] + victimUsername + ".";
	}


	private static String generateAttackerMissMessage(PlayerMissEvent event) {
		return ATTACKER_MISS_MESSAGES[random.nextInt(ATTACKER_MISS_MESSAGES.length)];
	}


	private static String generateAttackerOutgunnedMessage(PlayerOutgunnedEvent event) {
		return ATTACKER_OUTGUNNED_MESSAGES[random.nextInt(ATTACKER_OUTGUNNED_MESSAGES.length)];
	}


	private static String generateParticipantHitMessage(PlayerHitEvent event) {
		String attackerUsername = event.getAttackerUsername().getGeneratedName();
		return attackerUsername + PARTICIPANT_HIT_MESSAGES[random.nextInt(PARTICIPANT_HIT_MESSAGES.length)];
	}


	private static String generateParticipantOutgunnedMessage(PlayerOutgunnedEvent event) {
		String attackerUsername = event.getAttacker().getGeneratedName();
		return attackerUsername + PARTICIPANT_OUTGUNNED_MESSAGES[random.nextInt(PARTICIPANT_OUTGUNNED_MESSAGES.length)];
	}


	public static String generatePlayersInGameMessage(int playerCount) {
		if (playerCount == 1) {
			return "There is currently " + playerCount + " player in the game.";
		} else {
			return "There are currently " + playerCount + " players in the game.";
		}
	}


	public static String generateTurnsTakenMessage(int totalTurnsTaken) {
		if (totalTurnsTaken == 1) {
			return "Players have taken 1 turn so far.";
		} else {
			return "Players have taken " + totalTurnsTaken + " turns so far.";
		}
	}


	public static String generatePlayersEliminatedMessage(int eliminationCount) {
		if (eliminationCount == 1) {
			return "Currently 1 player has been eliminated from the game.";
		} else {
			return "Currently " + eliminationCount + " players have been eliminated from the game.";
		}
	}


	public static String generateCloseEncounterMessage(Player updatingPlayer, List<Player> playersInRange) {
		if (playersInRange.size() == 1) {
			return updatingPlayer.getGeneratedName() + " moved within striking distance of " + playersInRange.get(0).getGeneratedName() + " at " + DATE_FORMAT.format(new Date()) + ".";
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(updatingPlayer.getGeneratedName());
			stringBuilder.append(" was surrounded by ");
			for (int i = 0; i < playersInRange.size(); i++) {
				stringBuilder.append(playersInRange.get(i).getGeneratedName());
				if (i + 2 == playersInRange.size()) {
					stringBuilder.append(" and ");
				} else {
					stringBuilder.append(", ");
				}
			}
			stringBuilder.append(" at " + DATE_FORMAT.format(new Date()));
			stringBuilder.append(".");
		}
		return null;
	}
}
