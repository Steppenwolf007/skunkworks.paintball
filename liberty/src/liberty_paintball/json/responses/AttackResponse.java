package liberty_paintball.json.responses;

import java.util.ArrayList;
import java.util.List;

import liberty_paintball.game.PlayerIdentity;

/**
 * @author Dave Waddling
 * 
 * This response describes the outcome of an attack. It is returned to an attacking player and also 
 * to any participant players (Node-RED does the routing). 
 *
 */
public class AttackResponse {

	/**
	* Type of response:-
	* INVALID_REQUEST...The request was invalid (for example an eliminated player sending an attack request).
	* MISS..............The player missed as no targets were nearby.
	* HIT...............The player hit the single player nearby and eliminated them from the game.
	* OUTGUNNED.........The player was outgunned and thus eliminated from the game.
	*/
	public enum ResponseType {
		INVALID_REQUEST, MISS, HIT, OUTGUNNED
	}

	private ResponseType			responseType;
	private PlayerIdentity			attackerIdentity;
	private String					attackerMessage;
	private List<PlayerIdentity>	participantIdentities;
	private String					participantMessage;


	public AttackResponse(ResponseType responseType, PlayerIdentity attackerIdentity, String attackerMessage, List<PlayerIdentity> participantIdentities, String participantMessage) {
		this.attackerIdentity = attackerIdentity;
		this.responseType = responseType;
		this.attackerMessage = attackerMessage;
		this.participantIdentities = participantIdentities;
		this.participantMessage = participantMessage;
	}


	public ResponseType getResponseType() {
		return responseType;
	}


	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}


	public PlayerIdentity getAttackerIdentity() {
		return attackerIdentity;
	}


	public void setAttackerIdentity(PlayerIdentity attackerIdentity) {
		this.attackerIdentity = attackerIdentity;
	}


	public String getAttackerMessage() {
		return attackerMessage;
	}


	public void setAttackerMessage(String attackerMessage) {
		this.attackerMessage = attackerMessage;
	}


	public List<PlayerIdentity> getParticipantIdentities() {
		if (participantIdentities == null) {
			return new ArrayList<PlayerIdentity>(0);
		} else {
			return participantIdentities;
		}
	}


	public void setParticipantIdentities(List<PlayerIdentity> participantIdentities) {
		this.participantIdentities = participantIdentities;
	}


	public String getParticipantMessage() {
		return participantMessage;
	}


	public void setParticipantMessage(String participantMessage) {
		this.participantMessage = participantMessage;
	}

}
