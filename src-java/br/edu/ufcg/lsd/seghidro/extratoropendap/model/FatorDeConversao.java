package br.edu.ufcg.lsd.seghidro.extratoropendap.model;

import org.apache.log4j.Logger;

import br.edu.ufcg.lsd.seghidro.extratoropendap.ExtratorDeVariaveisAbstract;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.Constantes;

import com.eteks.parser.CalculatorParser;
import com.eteks.parser.CompilationException;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * Representa um fator de conversão de medida para a qual o usuário deseja que
 * os valores sejam convertidos. Até o momento, este valor só pode ser uma fator
 * multiplicativo. Por exemplo, se os valores no dataset forem referentes à
 * precipitação e estiverem na unidade kg m-2 s-1, padrão do SI, e se o objetivo
 * for extraílas em mm, o fator de conversão será 86400. Este fator multiplicará
 * o valor original e o resultado estará em mm. Essa abordagem é limitada, pois
 * não se pode, por exemplo, converter algo de Kelvin (K) para Célsius (C), não
 * se pode, visto que o fator de conversão (C=k-273) não é multiplicativo.
 * 
 * @author edigley
 * 
 */
public class FatorDeConversao {

	protected static Logger logger = Logger.getLogger(FatorDeConversao.class);

	public static final FatorDeConversao DEFAULT = new FatorDeConversao(
			Constantes.SELF, "");

	/**
	 * Expressao que será avaliada para determinar o valor convertido.
	 * Referencias ao valor atual na expressão são feitos através da literal
	 * "self".Por exemplo, se os valores no dataset forem referentes à
	 * precipitação e estiverem na unidade kg m-2 s-1, padrão do SI, e se o
	 * objetivo for extraílas em mm, o fator de conversão será "self * 86400".
	 * Este fator multiplicará o valor original e o resultado estará em mm/dia.
	 * Para converter algo de Kelvin (K) para Célsius (C) a expressão é
	 * "self-273".
	 */
	private String expressao = Constantes.SELF;

	/**
	 * A unidades de medida na qual o usuário deseja que os valores sejam
	 * extraídos.
	 */
	private String unidadesAposConversao;

	public FatorDeConversao(String expressao, String unidadesAposConversao) {
		super();
		this.expressao = expressao;
		this.unidadesAposConversao = unidadesAposConversao;
	}

	public String getUnidadesAposConversao() {
		return unidadesAposConversao;
	}

	public void setUnidadesAposConversao(String unidadesAposConversao) {
		this.unidadesAposConversao = unidadesAposConversao;
	}

	public double converte(double valor) {
		CalculatorParser parser = new CalculatorParser();
		try {
			// Compute expressions with default interpreter
			// DoubleInterpreter
			logger.debug("Valor antes: " + valor);
			double computeExpression = parser.computeExpression(expressao
					.replace(Constantes.SELF, valor + ""));
			logger.debug("Valor depois: " + computeExpression);
			return computeExpression;
		} catch (CompilationException e) {
			logger.warn("A expressão atribuída ao fator de conversão lançou exceção.");
		}
		return valor;
	}

	public String getExpressao() {
		return expressao;
	}

	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("expressao", expressao)
				.append("unidadesAposConversao", unidadesAposConversao)
				.toString();
	}

}
