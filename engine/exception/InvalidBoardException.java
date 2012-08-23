package engine.exception;

/**
 * Unchecked exception to indicate that there was an error associating a Board
 * to a Piece. An InvalidBoardException is thrown when an attempt is made to
 * associate <code>null</code> to a Piece.
 * @see engine.adt.Piece
 */
public class InvalidBoardException extends RuntimeException {

    /**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = -4995556985120633652L;

	public InvalidBoardException () {
        super ();
    }
    
    public InvalidBoardException(String s) {
        super(s);
    }
	
}
