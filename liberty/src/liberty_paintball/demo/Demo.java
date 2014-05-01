package liberty_paintball.demo;

import liberty_paintball.game.Game;
import liberty_paintball.json.requests.AttackRequest;
import liberty_paintball.json.requests.JoinGameRequest;
import liberty_paintball.json.requests.UpdateLocationRequest;
import liberty_paintball.json.responses.AttackResponse;
import liberty_paintball.json.responses.JoinResponse;

/**
 * 
 * @author Dave Waddling
 * 
 * Quick and dirty demo "script" for showing the various in-game events. Uses the /demo endpoint
 * to step-through each turn before needing to be reset.
 *
 */
public class Demo {

	private static final String	PLAYER_A_USERNAME	= "PLAYER_A";
	private static final String	PLAYER_B_USERNAME	= "PLAYER_B";
	private static final String	PLAYER_C_USERNAME	= "PLAYER_C";
	private static final String	PLAYER_D_USERNAME	= "PLAYER_D";

	private static Demo			instance			= null;


	public static synchronized Demo getInstance() {
		if (instance == null) {
			instance = new Demo();
		}
		return instance;
	}

	private Game	game		= Game.getInstance();
	private int		currentStep	= 0;


	private Demo() {

	}


	public String doNextStep() {
		switch (currentStep++) {
		case 0:
			return createPlayers();
		case 1:
			return moveAndMiss();
		case 2:
			return moveAndHit();
		case 3:
			return moveAndOutgun();
		default:
			return defaultStep();
		}
	}


	public void reset() {
		currentStep = 0;
	}


	private String defaultStep() {
		return "Demo complete. Use the /reset endpoint to reset the game.";
	}


	private String createPlayers() {
		StringBuilder log = new StringBuilder();
		JoinResponse response = null;

		response = game.handleJoinGameRequest(new JoinGameRequest(PLAYER_A_USERNAME));
		log.append("Player A joined the game and was assigned in-game name '" + response.getPlayerIdentity().getGeneratedName() + "'.\n");

		game.handleUpdateLocationRequest(new UpdateLocationRequest(PLAYER_A_USERNAME, 51.02559, -1.39792));
		log.append("Player A updated their location to 51.02559, -1.39792.\n\n");

		response = game.handleJoinGameRequest(new JoinGameRequest(PLAYER_B_USERNAME));
		log.append("Player B joined the game and was assigned in-game name '" + response.getPlayerIdentity().getGeneratedName() + "'.\n");

		game.handleUpdateLocationRequest(new UpdateLocationRequest(PLAYER_B_USERNAME, 51.02611, -1.3976));
		log.append("Player B updated their location to 51.02611, -1.3976.\n\n");

		response = game.handleJoinGameRequest(new JoinGameRequest(PLAYER_C_USERNAME));
		log.append("Player C joined the game and was assigned in-game name '" + response.getPlayerIdentity().getGeneratedName() + "'.\n");

		game.handleUpdateLocationRequest(new UpdateLocationRequest(PLAYER_C_USERNAME, 51.02633, -1.39704));
		log.append("Player C updated their location to 51.02633, -1.39704.\n\n");

		response = game.handleJoinGameRequest(new JoinGameRequest(PLAYER_D_USERNAME));
		log.append("Player D joined the game and was assigned in-game name '" + response.getPlayerIdentity().getGeneratedName() + "'.\n");

		game.handleUpdateLocationRequest(new UpdateLocationRequest(PLAYER_D_USERNAME, 51.02687, -1.39616));
		log.append("Player D updated their location to 51.02687, -1.39616.\n\n");

		return log.toString();
	}


	private String moveAndMiss() {
		StringBuilder log = new StringBuilder();
		AttackResponse response = null;

		response = game.handleAttackRequest(new AttackRequest(PLAYER_A_USERNAME, 51.02629, -1.39799));
		log.append("Player A updated their location to 51.02629,-1.39799 and attacked.\n");
		log.append("Player A's attack result was of type '" + response.getResponseType() + "' and they received the following message:\n" + response.getAttackerMessage() + "\n\n");

		response = game.handleAttackRequest(new AttackRequest(PLAYER_B_USERNAME, 51.02613, -1.3973));
		log.append("Player B updated their location to 51.02613, -1.3973 and attacked.\n");
		log.append("Player B's attack result was of type '" + response.getResponseType() + "' and they received the following message:\n" + response.getAttackerMessage() + "\n\n");

		// Player C doesn't move.
		response = game.handleAttackRequest(new AttackRequest(PLAYER_C_USERNAME, 51.02633, -1.39704));
		log.append("Player C updated their location to 51.02633, -1.39704 (they did not move) and attacked.\n");
		log.append("Player C's attack result was of type '" + response.getResponseType() + "' and they received the following message:\n" + response.getAttackerMessage() + "\n\n");

		response = game.handleAttackRequest(new AttackRequest(PLAYER_D_USERNAME, 51.02637, -1.39621));
		log.append("Player D updated their location to 51.02637, -1.39621 and attacked.\n");
		log.append("Player D's attack result was of type '" + response.getResponseType() + "' and they received the following message:\n" + response.getAttackerMessage() + "\n\n");

		return log.toString();
	}


	private String moveAndHit() {
		StringBuilder log = new StringBuilder();
		AttackResponse response = null;

		response = game.handleAttackRequest(new AttackRequest(PLAYER_B_USERNAME, 51.02608, -1.39658));
		log.append("Player B updated their location to 51.02608, -1.39658 and attacked.\n");
		log.append("Player B's attack result was of type '" + response.getResponseType() + "' and they received the following message:\n" + response.getAttackerMessage() + "\n\n");
		
		response = game.handleAttackRequest(new AttackRequest(PLAYER_D_USERNAME, 51.02625, -1.39628));
		log.append("Player D updated their location to 51.02625, -1.39628 and attacked.\n");
		log.append("Player D's attack result was of type '" + response.getResponseType() + "' and they received the following message:\n" + response.getAttackerMessage() + "\n\n");
		
		// Player C eliminates Player A.
		game.handleUpdateLocationRequest(new UpdateLocationRequest(PLAYER_A_USERNAME, 51.02613, -1.39734));
		log.append("Player A updated their location to 51.02613, -1.39734.\n\n");

		response = game.handleAttackRequest(new AttackRequest(PLAYER_C_USERNAME, 51.02612, -1.39729));
		log.append("Player C updated their location to 51.02612, -1.39729 and attacked.\n");
		log.append("Player C's attack result was of type '" + response.getResponseType() + "' and they received the following message:\n" + response.getAttackerMessage() + "\n");
		log.append("Player A was eliminated by Player C and they recieved the following message:\n" + response.getParticipantMessage() + "\n\n");



		return log.toString();
	}


	private String moveAndOutgun() {
		StringBuilder log = new StringBuilder();
		AttackResponse response = null;

		log.append("Player A is eliminated. They can take no action.\n\n");

		game.handleUpdateLocationRequest(new UpdateLocationRequest(PLAYER_B_USERNAME, 51.02605, -1.39638));
		log.append("Player B updated their location to 51.02605, -1.39638.\n\n");

		game.handleUpdateLocationRequest(new UpdateLocationRequest(PLAYER_C_USERNAME, 51.02605, -1.39644));
		log.append("Player C updated their location to 51.02605, -1.39644.\n\n");

		response = game.handleAttackRequest(new AttackRequest(PLAYER_D_USERNAME, 51.02604, -1.3964));
		log.append("Player D updated their location to 51.02604, -1.3964 and attacked.\n");
		log.append("Player D's attack result was of type '" + response.getResponseType() + "' and they received the following message:\n" + response.getAttackerMessage() + "\n");
		log.append("Player D was eliminated from the game.\n");
		log.append("Player D was eliminated by Players C and B and the both recieved the following message:\n" + response.getParticipantMessage() + "\n\n");

		return log.toString();
	}

}
