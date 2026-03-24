package controller.test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import controller.XmlFactory;
import engine.adt.Board;
import engine.adt.Parser;
import engine.adt.Ply;
import engine.adt.RuleSet.PlyFactory;
import ruleset.antichess.StandardAC;
import junit.framework.TestCase;

public class XmlFactTest extends TestCase {

	public void testXmlToBoard() {
		StandardAC rs = new StandardAC();
		Board board = rs.boardFactory().getInitialBoard(); 
		Parser parser = rs.getParser();
		System.out.println(XmlFactory.boardToXml(board, parser));
		}
	
	public void testPlyHistoryToBoard() {
		StandardAC rs = new StandardAC();
		Board board = rs.boardFactory().getInitialBoard(); 
		
		PlyFactory pf = rs.plyFactory(); 
		
		List<Ply> plies = Arrays.asList(pf.getPly("c2-c3", board),
										pf.getPly("c7-c6", board),
										pf.getPly("b2-b3", board),
										pf.getPly("b8-a6", board),
										pf.getPly("b3-b4", board)); 


		List<Boolean> turns = Arrays.asList(true,
											false,
											true,
											false,
											true); 
		List<Integer> times = Arrays.asList(299000,
											299000,
											298000,
											298000,
											29700);
		
		
		System.out.println(XmlFactory.plyHistoryToXml(plies, turns, times));
	}
	
	
	public void testGetPartial() throws URISyntaxException {

		System.out.println(XmlFactory.getTag("cats",new File("controller/test/sample_game.xml")));
		
		System.out.println(XmlFactory.getTag("pieces",new File("controller/test/sample_game.xml")));
	}
	
	
	public void testHistory() {
		List<String[]> history = XmlFactory.xmlToHistory("<moveHistory>" + 
            "<move side=\"white\" value=\"c2-c3\" time=\"299000\" />\n" +
            "<move side=\"black\" value=\"c7-c6\" time=\"299000\" />\n" + 
            "<move side=\"white\" value=\"b8-c8\" time=\"299000\" />\n" +       
            "</moveHistory>");

		
//		System.out.println(XmlFactory.validate("C:\\Documents and Settings\\muniz.WIN\\Desktop\\game.xml", 
//				"controller/test/antichess.xsd"));
		
		for (String[] array : history)
			System.out.println(Arrays.toString(array));
	}
}
