package br.edu.ufcg.lsd.seghidro.extratoropendap.model;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>FatorDeConversaoTest</code> contains tests for the class
 * <code>{@link FatorDeConversao}</code>.
 * 
 * @generatedBy CodePro at 17/03/09 19:18
 * @author edigley
 * @version $Revision: 1.1 $
 */
public class FatorDeConversaoTest {
	/**
	 * An instance of the class being tested.
	 * 
	 * @see FatorDeConversao
	 * 
	 * @generatedBy CodePro at 17/03/09 19:18
	 */
	private FatorDeConversao fatorDeConversao;

	/**
	 * Run the FatorDeConversao(Double,String) constructor test.
	 * 
	 * @generatedBy CodePro at 17/03/09 19:18
	 */
	@Test
	public void testFatorDeConversao_1() throws Exception {
		String unidadesAposConversao = "Graus Célsius";
		fatorDeConversao = new FatorDeConversao("self-273",
				unidadesAposConversao);

		// add additional test code here
		assertEquals(1, fatorDeConversao.converte(274));
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 17/03/09 19:18
	 */
	@Before
	public void setUp() throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 * 
	 * @generatedBy CodePro at 17/03/09 19:18
	 */
	@After
	public void tearDown() throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 * 
	 * @param args
	 *            the command line arguments
	 * 
	 * @generatedBy CodePro at 17/03/09 19:18
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(FatorDeConversaoTest.class);
	}
}