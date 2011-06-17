package br.edu.ufcg.lsd.seghidro.extratoropendap.model;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * The class <code>TestAll</code> builds a suite that can be used to run all of
 * the tests within its package as well as within any subpackages of its
 * package.
 * 
 * @generatedBy CodePro at 17/03/09 19:18
 * @author edigley
 * @version $Revision: 1.1 $
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ DataSetTest.class, FatorDeConversaoTest.class, })
public class TestAll {

	/**
	 * Launch the test.
	 * 
	 * @param args
	 *            the command line arguments
	 * 
	 * @generatedBy CodePro at 17/03/09 19:18
	 */
	public static void main(String[] args) {
		JUnitCore.runClasses(new Class[] { TestAll.class });
	}
}
