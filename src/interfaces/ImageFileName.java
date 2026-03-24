package interfaces;

public enum ImageFileName {
	PAWN_BLACK("pawn", "black", "normal", "pawnBlack.png"), 
	PAWN_WHITE("pawn", "white", "normal", "pawnWhite.png"), 
	BISHOP_BLACK("bishop", "black", "normal", "bishopBlack.png"), 
	BISHOP_WHITE("bishop", "white", "normal", "bishopWhite.png"), 
	KNIGHT_BLACK("knight", "black", "normal", "knightBlack.png"), 
	KNIGHT_WHITE("knight", "white", "normal", "knightWhite.png"), 
	ROOK_BLACK("rook", "black", "normal", "rookBlack.png"), 
	ROOK_WHITE("rook", "white", "normal", "rookWhite.png"), 
	QUEEN_BLACK("queen", "black", "normal", "queenBlack.png"), 
	QUEEN_WHITE("queen", "white", "normal", "queenWhite.png"),
	KING_BLACK("king", "black", "normal", "kingBlack.png"), 
	KING_WHITE("king", "white", "normal", "kingWhite.png"),
	PAWN_BLACK_SMALL("pawn", "black", "small", "pawnBlackSmall.png"), 
	PAWN_WHITE_SMALL("pawn", "white", "small", "pawnWhiteSmall.png"), 
	BISHOP_BLACK_SMALL("bishop", "black", "small", "bishopBlackSmall.png"), 
	BISHOP_WHITE_SMALL("bishop", "white", "small", "bishopWhiteSmall.png"), 
	KNIGHT_BLACK_SMALL("knight", "black", "small", "knightBlackSmall.png"), 
	KNIGHT_WHITE_SMALL("knight", "white", "small", "knightWhiteSmall.png"), 
	ROOK_BLACK_SMALL("rook", "black", "small", "rookBlackSmall.png"), 
	ROOK_WHITE_SMALL("rook", "white", "small", "rookWhiteSmall.png"), 
	QUEEN_BLACK_SMALL("queen", "black", "small", "queenBlackSmall.png"), 
	QUEEN_WHITE_SMALL("queen", "white", "small", "queenWhiteSmall.png"),
	KING_BLACK_SMALL("king", "black", "small", "kingBlackSmall.png"), 
	KING_WHITE_SMALL("king", "white", "small", "kingWhiteSmall.png"),
	RED_CHIP("chip", "white", "normal", "c4SquareRed.png"),
	BLACK_CHIP("chip", "black", "normal", "c4SquareBlack.png");
	
	
	private final String name;
	private final String color;
	private final String size;
	private final String fileName;
	
	ImageFileName(String name, String color, String size, String fileName){
		this.name = name;
		this.color = color;
		this.size = size;
		this.fileName = fileName;
	}
	
    public static ImageFileName parse(String pieceName, String pieceColor, String size){
        for(ImageFileName typ : ImageFileName.values()) {
            if(typ.name.equals(pieceName) && typ.color.equals(pieceColor) 
            		&& typ.size.equals(size)){
                return typ;
            }
        }
        return null;
    }
    
    public String getFileName(){
    	return this.fileName;
    }
    
}
