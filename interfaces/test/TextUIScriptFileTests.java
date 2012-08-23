package interfaces.test;

import java.io.*;
import java.net.URISyntaxException;
import interfaces.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class, along with TextUITestDriver, can be used to test the 
 * implementation of TextUI. It will find all script files in the same 
 * directory as this class file (with names ending in
 * <tt>.test</tt>) and execute them.
 */
public class TextUIScriptFileTests extends TestCase {
	
	private final File testDriver;
	
	/**
	 * Creates a new ScriptFileTests case, which runs the given test file.
	 * @param testDriver
	 */
    public TextUIScriptFileTests(File testDriver) {
        super("testTextUIWithScriptFile");
        
        this.testDriver = testDriver;
    }

    /**
     * @throws FileNotFoundException, IOException
     * @requires that the specified File exists
	 * @returns the contents of that file
     */
    private String fileContents(File f) throws IOException {
        if (f == null) {
            throw new RuntimeException("No file specified");
        }
        
        StringBuilder result = new StringBuilder();
        char[] data = new char[4096]; // a reasonable block size
        
        FileReader fr = new FileReader(f);
        int charsRead;
        while ((charsRead = fr.read(data)) != -1) {
        	result.append(data, 0, charsRead);
        }
        
        return result.toString();
    }

    /**
     * @throws IOException 
     * @requires there exists a test file indicated by testDriver
     *
     * @effects runs the test in filename, and output its results to a file in
     * the same directory with name filename+".actual"; if that file already
     * exists, it will be overwritten.     
     * @returns the contents of the output file
     */
    private String runScriptFile() throws IOException {
        if (testDriver == null) {
            throw new RuntimeException("No file specified");
        }
        
        File actual = fileWithSuffix("actual");
        
        Reader r = new FileReader(testDriver);
        Writer w = new FileWriter(actual);
        
        TextUI tui = new TextUI(r, w, true); // debug mode
        tui.interactiveLoop();
        
        return fileContents(actual);
    }
    
    /**
	 * @param newSuffix
	 * @return a File with the same name as testDriver, except that the test
	 *         suffix is replaced by the given suffix
	 */
    private File fileWithSuffix(String newSuffix) {
    	File parent = testDriver.getParentFile();
    	String driverName = testDriver.getName();
    	String baseName = driverName.substring(0, driverName.length() - "test".length());
    	
    	return new File(parent, baseName + newSuffix);
    }
    
    /**
     * The only test that is run: run a script file and test its output.
     * @throws IOException
     */
    public void testTextUIWithScriptFile() throws IOException {
    	File expected = fileWithSuffix("expected");
    	
    	assertEquals(testDriver.getName(), fileContents(expected), runScriptFile());
    }
    
    /**
     * Build a test suite of all of the script files in the directory.
     * @return the test suite
     * @throws URISyntaxException
     */
    public static Test suite()
    { 
        TestSuite suite = new TestSuite(); 
        
        // Hack to get at the directory where the files are: they are in the
		// same directory as the compiled ScriptFileTests class,
        try {
        	File myDirectory = new File(TextUIScriptFileTests.class.getResource("TextUIScriptFileTests.class").toURI()).getParentFile();
        	FileFilter filter = new FileFilter() {
        		public boolean accept(File pathname) {
        			return pathname.getName().equals("script");
        		}
        	};
        	File[] subs = myDirectory.listFiles(filter);
        	for (File f : subs[0].listFiles()) {
        		if (f.getName().endsWith(".test")) {
        			suite.addTest(new TextUIScriptFileTests(f));
        		}
        	}
        	return suite;
        } catch (URISyntaxException e) {
        	throw new RuntimeException(e);
        }
    }

}