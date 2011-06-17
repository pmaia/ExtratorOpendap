package br.edu.ufcg.lsd.seghidro.extratoropendap;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.formatador.FormatadorIF;
import br.edu.ufcg.lsd.seghidro.extratoropendap.formatador.FormatadorPMHLike;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.FatorDeConversao;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Posto;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.Constantes;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil;
import br.edu.ufcg.lsd.seghidro.util.FileUtil;

/**
 * 
 * A classe {@link ExtratorDeVariaveisAbstract} é o elemento ativo deste módulo,
 * responsável por coordenar a extração de valores de interesse presentes em um
 * {@link DataSet} referentes a um conjunto de {@link Posto} que lhe é passado
 * como parâmetro no momento de sua criação através de um arquivo de Postos.
 * Como resultado de sua execução, um objeto da clase
 * {@link ExtratorDeVariaveisAbstract} gera uma seqüência de {@link Extracao},
 * que podem ser recuperadas em forma de objetos (
 * {@link ExtratorDeVariaveisAbstract#getListaDeExtracoes()}) ou em em formato
 * String já ({@link ExtratorDeVariaveisAbstract#getExtracoes()}).
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 12/02/2009
 * 
 */
public abstract class ExtratorDeVariaveisAbstract implements
		ExtratorDeVariaveisIF {

	protected static Logger logger = Logger
			.getLogger(ExtratorDeVariaveisAbstract.class);

	protected String variavelDeInteresse;

	protected DataSet dataSet;

	protected Date dataInicial;

	protected Date dataFinal;

	protected FormatadorIF formatador;

	protected File arquivoDeExtracoes;

	protected StringBuffer sb;

	protected List<Extracao> extracoes;

	protected FatorDeConversao fatorDeConversao;

	/**
	 * Extrator de valores a partir de dados grid acessíveis pelo formato
	 * NetCDF.
	 */
	protected ExtratorDeVariaveisAbstract() {
		sb = new StringBuffer();
		formatador = new FormatadorPMHLike(sb);
		extracoes = new ArrayList<Extracao>();
		this.fatorDeConversao = FatorDeConversao.DEFAULT;
	}

	/**
	 * Extrator de valores a partir de dados grid, acessíveis pelo formato
	 * NetCDF.
	 * 
	 * @param dataSet
	 *            O arquivo de origem dos dados, no formato netcdf.
	 * @throws ExtratorOpendapException
	 *             Caso haja algum problema na leitura do arquivos informado.
	 */
	public ExtratorDeVariaveisAbstract(DataSet dataSet)
			throws ExtratorOpendapException {
		this();
		this.dataSet = dataSet;
		this.fatorDeConversao = new FatorDeConversao(Constantes.SELF,
				dataSet.getUnidade());
	}

	public ExtratorDeVariaveisAbstract(DataSet dataSet,
			String variavelDeInteresse) throws ExtratorOpendapException {
		this(dataSet);
		if (dataSet.contemVariavel(variavelDeInteresse)) {
			this.variavelDeInteresse = variavelDeInteresse;
		} else {
			throw new ExtratorOpendapException("O dataSet '" + dataSet
					+ "' não contém a variável de Interesse '"
					+ variavelDeInteresse + "'.");
		}
	}

	public ExtratorDeVariaveisAbstract(DataSet dataSet,
			String variavelDeInteresse, Date dataInicial, Date dataFinal)
			throws ExtratorOpendapException {
		this(dataSet, variavelDeInteresse);
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
	}

	public ExtratorDeVariaveisAbstract(DataSet dataSet, Date dataInicial,
			Date dataFinal) throws ExtratorOpendapException {
		this(dataSet);
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
	}

	/**
	 * Verifica se <code>data</code> está no intervalo especificado para a
	 * extração de valores.
	 * 
	 * @param data
	 * @return
	 */
	protected boolean dataEstahNoIntervaloEspecificado(Date data) {
		boolean retorno = data.after(dataInicial) && data.before(dataFinal);
		logger.debug("Data esta no intervalo especificado? :"
				+ LocaleUtil.formatadorDeDatas.format(dataInicial) + ".."
				+ LocaleUtil.formatadorDeDatas.format(dataFinal) + ": "
				+ retorno);
		return retorno;
	}

	/**
	 * Verifica se o intervalo de tempo é um fator relevante para a extração de
	 * valores.
	 * 
	 * @return
	 */
	protected boolean intervaloNaoEhRelevante() {
		boolean retorno = dataInicial == null && dataFinal == null;
		logger.debug("Intervalo nao eh relevante? :" + retorno);
		return retorno;
	}

	@Override
	public String getExtracoes() {
		return sb.toString();
	}

	/**
	 * Recupera todas as extrações realizadas.
	 * 
	 * @return todas as extrações realizadas ou <tt>null</tt> se o método
	 *         {@link ExtratorDeVariaveisAbstract#extraiValoresDeInteresse()}
	 *         ainda não tiver sido chamado.
	 */
	public List<Extracao> getListaDeExtracoes() {
		return extracoes;
	}

	@Override
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	@Override
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	@Override
	public boolean salvaArquivoComExtracoes() {
		return FileUtil.salvaArquivoEmDisco(
				arquivoDeExtracoes.getAbsolutePath(), this.getExtracoes());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("variavelDeInteresse", variavelDeInteresse)
				.append("dataInicial", dataInicial)
				.append("dataFinal", dataFinal).toString();
	}

	@Override
	public abstract String extraiValoresDeInteresse()
			throws ExtratorOpendapException;

	@Override
	public void setFatorDeConversao(FatorDeConversao fatorDeConversao) {
		this.fatorDeConversao = fatorDeConversao;
	}

	@Override
	public String getUnidadeAposExtracao() {
		return fatorDeConversao.getUnidadesAposConversao();
	}

	@Override
	public void setFormatador(FormatadorIF formatador) {
		this.formatador = formatador;
		this.formatador.setStringBuffer(sb);
	}

}
