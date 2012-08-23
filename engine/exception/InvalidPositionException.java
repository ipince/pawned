package engine.exception;

/**
 * Unchecked exception to indicate that there was an error positioning a Piece
 * in a Board. An InvalidPositionException is thrown when a Board tries to add a
 * Piece to a cell that is already occupied.
 */
public class InvalidPositionException extends RuntimeException {
    
    /**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = -4995556985120633652L;

	public InvalidPositionException () {
        super ();
    }
    
    public InvalidPositionException(String s) {
        super(s);
    }

}
