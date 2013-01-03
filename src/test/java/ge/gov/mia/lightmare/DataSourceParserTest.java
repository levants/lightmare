package ge.gov.mia.lightmare;

import ge.gov.mia.lightmare.jpa.datasource.FileParsers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.BeforeClass;
import org.junit.Test;

public class DataSourceParserTest {

	public static final String PATH = "./ds/standalone.xml";

	public static File file;

	@BeforeClass
	public static void start() {

		try {
			file = new File(PATH);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void parseTest() {

		FileParsers parsers = new FileParsers();
		try {
			parsers.parseStandaloneXml(PATH);
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
