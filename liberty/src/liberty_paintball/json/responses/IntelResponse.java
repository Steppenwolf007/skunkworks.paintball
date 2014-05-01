package liberty_paintball.json.responses;

/**
 * @author Dave Waddling
 *
 */
public class IntelResponse {

	/**
	 * The message to publicize.
	 */
	private String	message;

	/**
	 * Whether the message should be publicized after a short delay for when it may compromise one or more players. 
	 * The delay is implemented in NodeRed.
	 */
	private boolean	shouldDelayPublicizing;


	/**
	 * A POJO which represents an intelligence message which contains a game event or statistic which can be 
	 * publicized on the game Twitter stream. 
	 */
	public IntelResponse() {

	}


	public IntelResponse(String message, boolean shouldDelayPublicizing) {
		this.message = message;
		this.shouldDelayPublicizing = shouldDelayPublicizing;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public boolean isShouldDelayPublicizing() {
		return shouldDelayPublicizing;
	}


	public void setShouldDelayPublicizing(boolean shouldDelayPublicizing) {
		this.shouldDelayPublicizing = shouldDelayPublicizing;
	}
}
