package engine.exception;

/**
 * Checked exception to indicate that there was an error positioning a Piece in
 * a Board. An InvalidInitialPositionException is thrown when a Piece tries to
 * add itself to a Board at its initial position, but the cell is not usable or
 * it is already occupied.
 */
public class InvalidInitialPositionException extends Exception {
    
    /**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = -3398315159608549799L;

	public InvalidInitialPositionException () {
        super ();
    }
    
    public InvalidInitialPositionException(String s) {
        super(s);
    }
}
