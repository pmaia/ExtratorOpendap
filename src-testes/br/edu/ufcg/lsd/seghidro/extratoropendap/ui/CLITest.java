package br.edu.ufcg.lsd.seghidro.extratoropendap.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.*;

import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.util.FileCatalog;
import br.edu.ufcg.lsd.seghidro.util.FileUtil;
import static org.junit.Assert.*;

/**
 * The class <code>CLITest</code> contains tests for the class
 * <code>{@link CLI}</code>.
 * 
 * @generatedBy CodePro at 09/03/09 21:33
 * @author edigley
 * @version $Revision: 1.2 $
 */
public class CLITest {

	File testDir;
	File resultsDir;
	File pointsFile;
	File outputFile;
	URL urlDataSet;

	public CLITest() {
		testDir = new File("tests");
		resultsDir = new File(testDir, "resultados");
		pointsFile = new File(testDir, "arquivos_de_entrada/bananeiras-faz.txt");
		outputFile = new File(resultsDir, "output.txt");
		try {
			urlDataSet = new URL(
					"http://150.165.126.97:8080/thredds/dodsC/test/MIMR_SRB1_1_pr-change_2011-2030.nc");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testMain_1() throws Exception {

		String[] args = new String[] { "-d", urlDataSet.toExternalForm(), "-p",
				pointsFile.getAbsolutePath(), "-i", "2020-05-16", "-f",
				"2020-08-16", "-o", outputFile.getAbsolutePath() };

		CLI.main(args);

		assertTrue(outputFile.exists());

	}

	@Test
	public void testMain_2() throws Exception {

		String[] args = new String[] { "-d", urlDataSet.toExternalForm(), "-p",
				pointsFile.getAbsolutePath(), "-o",
				outputFile.getAbsolutePath() };

		CLI.main(args);

		assertTrue(outputFile.exists());

	}

	@Test
	public void testMain_Funceme() throws Exception {

		this.urlDataSet = new URL(
				"http://200.129.31.2:8083/thredds/dodsC/funceme/rsm/psst/mar-2009/rsm-2009PER0404-10.cdf");

		// DataSet d = new DataSet(urlDataSet.toExternalForm());
		//
		// d.getDetalhes();

		String[] args = new String[] {};

		CLI.main(args);

		// assertTrue(outputFile.exists());

	}

	/**
	 * Creation of the objects to the test.
	 */
	@Before
	public void setUp() {
		resultsDir.mkdir();
	}

	@After
	public void tearDown() {
		// limpa todos os arquivos existentes dentro do diret�rio de resultados
		FileUtil.deleteDirectory(resultsDir);
	}

}