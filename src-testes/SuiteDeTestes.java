import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.edu.ufcg.lsd.seghidro.extratoropendap.ExtratorDeVariaveisInterpoladoTest;
import br.edu.ufcg.lsd.seghidro.extratoropendap.ExtratorDeVariaveisPontosDeGradeTest;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSetTest;
import br.edu.ufcg.lsd.seghidro.extratoropendap.ui.CLITest;

@RunWith(Suite.class)
@SuiteClasses({ DataSetTest.class, ExtratorDeVariaveisInterpoladoTest.class,
		ExtratorDeVariaveisPontosDeGradeTest.class, CLITest.class, })
public class SuiteDeTestes {

	static class Compatibility {

		static Test suite() throws IOException {
			return new JUnit4TestAdapter(SuiteDeTestes.class);
		}
	}

}