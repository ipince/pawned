package ruleset.board;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ruleset.antichess.StandardAC;

import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.RuleSet;

/**
 * Produces a RectangularBoard from its XML representation.
 */
public class XmlBoardFactory {
	
	// TODO does not handle unused cells yet
	public static Board xmlToBoard(String xmlSettings) {

		// Format [cell, color, type]
		List<String[]> config = new LinkedList<String[]>();
		
		Document document;
		try {
			document = getDocument(xmlSettings);
			NodeList nodes = document.getElementsByTagName("pieces"); 
			for (int i = 0; i < nodes.getLength(); i++) {
				Element node = (Element) nodes.item(i);

				NodeList squares = node.getElementsByTagName("square"); 
				for (int j = 0; j < squares.getLength(); j++) {
					Element move = (Element) squares.item(j);

					String cell = move.getAttribute("id");
					String color = move.getAttribute("side");
					String type = move.getAttribute("piece");

					String[] values = {cell, color, type};
					config.add(values); 
				}
			}
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (IOException e) { }
		
		return makeBoard(config);
	}
	
	private static Board makeBoard(List<String[]> config) {
		RuleSet rs = new StandardAC();
		Board board = new RectangularBoard();
		for (String[] info : config) {
			boolean isWhite;
			if (info[1].equals("white"))
				isWhite = true;
			else if (info[1].equals("black"))
				isWhite = false;
			else 
				throw new RuntimeException("invalid xml");
			Piece toAdd = rs.pieceFactory().getPiece(info[2], board, isWhite);
			board.addPiece(toAdd, CoordinateParser.parseString(info[0]));
		}
		return board;
	}

	private static Document getDocument(String source)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = getDocumentBuilder(); 
		return builder.parse(new ByteArrayInputStream(source.getBytes()));
	}

	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		return factory.newDocumentBuilder(); 		
	}
}
