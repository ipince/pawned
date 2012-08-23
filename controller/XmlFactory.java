package controller;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import engine.adt.Board;
import engine.adt.Parser;
import engine.adt.Piece;
import engine.adt.Ply;

/**
 * Creates games from XML files and XML files from games.
 */
public class XmlFactory {
	
	/**
	 * Bypass default constructor.
	 */
	private XmlFactory() {
		
	}
	
	/**
	 * Address for default schema to be used.
	 */
	public static final String URL_ADDRESS = "http://web.mit.edu/reipince/www/pawned/schema.xsd";
	
	/**
	 * Validates the given file against the specified schema. 
	 * @param file A string representing the path to the file to
	 * 		  be validated.
	 * @param schema A string representing the path to the file
	 * 				containing the schema=
	 * @return true if the file validates, false otherwise
	 */
	public static boolean validate(String file, String schema) {
		try {
			
			// TODO check schema

			//Document Builder Factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);

			//Get schema
			SchemaFactory constraintFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schemaObj = constraintFactory.newSchema(new File(schema));
			Validator validator = schemaObj.newValidator();

			Source source = new StreamSource(new File(file));

			//Validate XML with Schema 
			validator.validate(source);
			return true;

		}
		catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Validates the given file against the specified schema. 
	 * @param file A string representing the path to the file to
	 * 		  be validated.
	 * @param schema A URL representing the path to the file
	 * 				containing the schema
	 * @return true if the file validates, false otherwise
	 */
	public static boolean validate(String file, URL schema) {
		try {

			//Document Builder Factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);

			//Get schema
			SchemaFactory constraintFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schemaObj = constraintFactory.newSchema(schema);
			Validator validator = schemaObj.newValidator();

			Source source = new StreamSource(new File(file));

			//Validate XML with Schema 
			validator.validate(source);
			return true;

		}
		catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Takes in a board and returns a String representing its XML 
	 * representation as specified by the antichess schema. The
	 * string representation of a board refers to the set of pieces
	 * it contains, along with their color and position. 
	 * 
	 * A sample XML representation of the portion representing the 
	 * board is: 
	 * 
	 * <p>
	 *  <code>
	 * 	&lt;pieces&gt; <br />
	 *  &nbsp;&nbsp;   &lt;square id="c9" side "white" piece="rook" /&gt; </br>
	 *  &nbsp;&nbsp;    &lt;square id="e8" side "black" piece="bishop" /&gt; </br>
	 *  &lt;/pieces&gt;
	 * 
	 * 	</pieces>
	 *  </code>
	 * </p> 
	 * 
	 * @param board The board that should be converted to XML
	 * @return A String representing the XML format or null if the
	 * 		   board is null or if an error occurs.
	 * 
	 * 
	 */
	public static String boardToXml(Board board, 
									Parser parser) {
		
		if (board == null ) 
			return null; 
		
		try {			
			Document xmlBoard = getDocument();
			xmlBoard.appendChild(boardToElement(board, xmlBoard, parser));
			
			return toPartialXML(xmlBoard.getDocumentElement());
			
		}
		catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Takes in the lists representing the history of plies along with
	 * their respective authors and times and constructs an XML
	 * according to the antichess specification. 
	 * 
	 * A sample XML representation of the portion representing the 
	 * ply history is: 
	 * 
	 * <p>
	 *  <code>
	 * 	&lt;moveHistory&gt; <br />
	 *  &nbsp;&nbsp;   &lt;move side="white" value="c6-c5" time="29700" /&gt; </br>
	 *  &nbsp;&nbsp;   &lt;move side="black" value="f2-f3" time="29000" /&gt; </br>
	 *  &lt;/moveHistory&gt;
	 * 
	 * 	</pieces>
	 *  </code>
	 * </p> 
	 * 
	 * @return A String representing the XML format or null if any 
	 *         list is null or their sizes are different 
	 * 
	 * 
	 */
	public static String plyHistoryToXml(List<Ply> plies, List<Boolean> turns,
										 List<Integer> times) {
		if (plies == null || turns == null || times == null) 
			return null; 
		if (plies.size() != turns.size() ||
		    turns.size() != times.size()) 
			return null;
		
		try {			
			Document xmlPly = getDocument();
			xmlPly.appendChild(historyToElement(plies, turns, times, xmlPly));
			
			return toPartialXML(xmlPly.getDocumentElement());
			
		}
		catch (Exception e) {
			return null;
		}
		
	}

	
	
	/**
	 * Returns a section of the XML representing a 
	 * particular tag. For example, 
	 * 
	 * <code>getTag("pieces")</code> would return a similar output 
	 * than <code>boardToXml()</code>
	 * 
	 * @return null if the tag name is not defined in file.
	 * 
	 */
	public static String getTag(String name, File file) {
		
		
		
		try {
			Document document = getDocument(file);
			NodeList list  = document.getElementsByTagName(name);

			String response = ""; 
			for (int i = 0; i < list.getLength(); i++)
				response += toPartialXML(list.item(i));
	
			return response;

			
		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (TransformerException e) {
			return null;
		}
		
	}

	/**
	 * Returns a section of the XML representing a 
	 * particular tag. For example, 
	 * 
	 * <code>getTag("pieces")</code> would return a similar output 
	 * than <code>boardToXml()</code>
	 * 
	 * @return null if the tag name is not defined in xml.
	 * 
	 */
	public static String getTag(String name, String xml) {
		try {
			Document document = getDocument(xml);
			NodeList list  = document.getElementsByTagName(name);

			String response = ""; 
			for (int i = 0; i < list.getLength(); i++)
				response += toPartialXML(list.item(i));
	
			return response;

			
		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (TransformerException e) {
			return null;
		}
				
	}
	
	 /**
	  * Returns an array [T_a, T_b] representing the amount of time left T_n
	  * for each player a and b, taking a File reference to a saved game
	  * xml file . If an error happens, returns an empty array. 
	  * 
	  * @param file The file from which the game is to be retrieved
	  * @param initial true if the desired times are the initial times
	  *                false if the desired times are the remaining times 
	  */
	public static String[] xmlToTimes(File file, boolean initial) {
		String[] times = new String[2];
		Document document;
		try {
			document = getDocument(file);
			NodeList nodes = document.getElementsByTagName("time"); 
			
			if (nodes.getLength() == 0)
				return times;
			else {
				for (int i = 0; i < nodes.getLength(); i++) {
					Element node = (Element) nodes.item(i);
					if (node.getAttribute("timed").equals("false")) {
						times[0] = String.valueOf(Controller.UNTIMED);
						times[1] = String.valueOf(Controller.UNTIMED);
						return times;
					}
					
					times[0] = initial ? node.getAttribute("initWhite") :
										 node.getAttribute("currentWhite");
					times[1] = initial ? node.getAttribute("initBlack") :
										 node.getAttribute("currentBlack");
				}	
		
				
			}
			
			return times;

		} catch (ParserConfigurationException e) {
			return times;
		} catch (SAXException e) {
			return times;
		} catch (IOException e) {
			return times;
		}		
	}
	/**
	 * Returns the history of moves as List of String arrays. 
	 * The array is in the format [color, ply-value, time] 
	 * which correspond to the respective fields in the XML
	 * file 
	 * 
	 * @return A list representation of the move history for
	 * the game 
	 */
	public static List<String[]> xmlToHistory(String string) {
		List<String[]> history = new LinkedList<String[]>();
		
		Document document;
		try {
			document = getDocument(string);
			NodeList nodes = document.getElementsByTagName("moveHistory"); 
			
			if (nodes.getLength() == 0)
				return history;
			else {
				for (int i = 0; i < nodes.getLength(); i++) {
					Element node = (Element) nodes.item(i);
					
					NodeList moves = node.getElementsByTagName("move"); 
					
						for (int j = 0; j < moves.getLength(); j++) {
							Element move = (Element) moves.item(j); 
							
							String side = move.getAttribute("side");
							String value = move.getAttribute("value");
							String time = move.getAttribute("time"); 
							
							String[] values = {side, value, time}; 
							history.add(values); 
						}
				}	
		
				
			}
			
			return history;

		} catch (ParserConfigurationException e) {
			return history;
		} catch (SAXException e) {
			return history;
		} catch (IOException e) {
			return history;
		}
	}
	
	 /**
	  * Returns a String that describes the ruleset of the game in the xml game file. 
	  * If an error happens, returns null. 
	  * 
	  * @param file The file from which the ruleset type is to be retrieved
	  */
	public static String xmlToRuleSet(File file) {
		String ruleSet = null;
		Document document;
		try {
			document = getDocument(file);
			NodeList nodes = document.getElementsByTagName("game"); 
			
			if (nodes.getLength() == 0)
				return ruleSet;
			else {
				for (int i = 0; i < nodes.getLength(); i++) {
					Element node = (Element) nodes.item(i);
					ruleSet = node.getAttribute("ruleset");
				}	
			}
			
			return ruleSet;

		} catch (ParserConfigurationException e) {
			return ruleSet;
		} catch (SAXException e) {
			return ruleSet;
		} catch (IOException e) {
			return ruleSet;
		}		
	}
	
	/**
	 * Constructs an XML representation of a Game 
	 * @param times
	 * @param turns
	 * @param plies
	 * 
	 * If winner == null, then the game is incomplete and should
	 * thus not have a <code> <gameOver> </code> tag.
	 * @throws ParserConfigurationException 
	 */
	public static String gameToXml(
			String ruleset, 
			boolean timed, int initWhite, int initBlack, int currentWhite, int currentBlack,
			List<Integer> times, List<Boolean> turns, List<Ply> plies, 
			Board board, 
			Boolean winner, String winDescription,
			Parser parser) throws ParserConfigurationException {
		
		try {
			Document document = getDocument(); 

			Element timedEl = timeToElement(timed, initWhite, 
					initBlack, currentWhite, currentBlack, document);
			Element boardEl = boardToElement(board, document, parser); 



			Element historyEl = historyToElement(plies, turns, times, document); 


			
			Element overEl = null; 
			if (winner != null) {
				boolean whiteWon = winner;
				overEl = document.createElement("gameOver"); 
				overEl.setAttribute("winner", whiteWon ? "white" : "black" ); 
				overEl.setAttribute("description", winDescription); 
			}
			
			Element game = document.createElement("game"); 

			game.setAttribute("ruleset", ruleset); 
			
			document.appendChild(game); 
			
			game.appendChild(timedEl);
			game.appendChild(historyEl); 
			game.appendChild(boardEl);


			
			if (overEl != null)
				game.appendChild(overEl);
			

			return toXML(document, true);
		}
		catch (Exception e) { 
			return null;
		}
	}
	/*
	 * Returns an object of type Element representing this board.
	 * This board is associated to document
	 */
	private static Element boardToElement(Board board, Document document,
											Parser parser) {  
		
		Element pieces = document.createElement("pieces");
		
		List<Piece> allPieces = new LinkedList<Piece>(board.getPieces(true));
		allPieces.addAll(board.getPieces(false));
		
		for (Piece p : allPieces) {
			Element square = document.createElement("square"); 
			square.setAttribute("id", 
					parser.parseCoord(board.getPosition(p)));
			square.setAttribute("side",
					p.isWhite() ? "white" : "black");
			square.setAttribute("piece", 
					p.getType());
			
			pieces.appendChild(square); 
			
		}
			
		return pieces;
	}
	

	/*
	 * Returns an object of type Element representing a ply history.
	 * This history is associated to document
	 * 
	 * @requires plies.size == turns.size == times.size
	 */
	
	private static Element historyToElement(List<Ply> plies, 
			List<Boolean> turns, List<Integer> times, Document document) {
	
		Element root = document.createElement("moveHistory");
		
		for (int i = 0; i < turns.size(); i++) {
			Ply ply = plies.get(i);
			boolean isWhite = turns.get(i).booleanValue(); 
			Integer time = times.get(i); 
			
			Element move = document.createElement("move"); 

			move.setAttribute("side", isWhite ? "white" : "black"); 
			move.setAttribute("value", ply.toString()); 
			move.setAttribute("time", time.toString()); 
			
			root.appendChild(move);
			
		}
		
		return root;
	}
	
	/*
	 * Returns an object of type Element representing the time information
	 * for game. 
	 */
	private static Element timeToElement(			
			boolean timed, int initWhite, int initBlack, int currentWhite, int currentBlack,
			Document document) {
		
		
		Element root = document.createElement("time");
		
		if (timed) {
			root.setAttribute("timed", "true");
			root.setAttribute("initWhite", "" + initWhite);
			root.setAttribute("initBlack", "" + initBlack);
			root.setAttribute("currentWhite", "" + currentWhite);
			root.setAttribute("currentBlack", "" + currentBlack);
		}
		else {
			root.setAttribute("timed", "false"); 
			root.setAttribute("initWhite", String.valueOf(Controller.UNTIMED));
			root.setAttribute("initBlack", String.valueOf(Controller.UNTIMED));
			root.setAttribute("currentWhite", String.valueOf(Controller.UNTIMED));
			root.setAttribute("currentBlack", String.valueOf(Controller.UNTIMED));
		}
		return root;
		
	}
 	/**
	 * Builds a String representation of an XML Document
	 * @param document
	 * @return
	 * @throws TransformerException 
	 */	
	private static String toXML(Node document, boolean declaration) throws TransformerException {
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.INDENT, "yes"); 
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, 
		declaration? "no" : "yes" ); 
		
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(document); 
		trans.transform(source, result);
		
		return sw.toString(); 		
	}
 	/**
	 * Builds a String representation of an XML Document
	 * @param document
	 * @return
	 * @throws TransformerException 
	 */
	private static String toPartialXML(Node document) throws TransformerException {
		return toXML(document, false);
	}
	
	private static Document getDocument() throws ParserConfigurationException {

		DocumentBuilder builder = getDocumentBuilder();
	
		return builder.newDocument();
	}
	
	
	private static Document getDocument(File source) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilder builder = getDocumentBuilder(); 
		
		return builder.parse(source);
	}
	

	private static Document getDocument(String source) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilder builder = getDocumentBuilder(); 
		return builder.parse(new ByteArrayInputStream(source.getBytes()));
	}



	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		
		return factory.newDocumentBuilder(); 		
	}
}
