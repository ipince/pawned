package engine.exception;

/**
 * Unchecked exception to indicate that an attempt was made to do something
 * to an unusable cell.
 */
public class UnusableCellException extends RuntimeException {

	/**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = 1422588804897370444L;

	public UnusableCellException(String message) 
	{ 
		super(message);
	}
}
