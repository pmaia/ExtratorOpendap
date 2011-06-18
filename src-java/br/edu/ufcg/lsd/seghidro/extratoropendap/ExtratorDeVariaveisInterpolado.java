package br.edu.ufcg.lsd.seghidro.extratoropendap;

import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeDatas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;

import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.interpolador.InterpoladorBilinearOPeNDAP;
import br.edu.ufcg.lsd.seghidro.extratoropendap.interpolador.InterpoladorIF;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Posto;

/**
 * 
 * A classe {@link ExtratorDeVariaveisInterpolado} é o elemento ativo deste
 * módulo, responsável por coordenar a extração de valores de interesse
 * presentes em um {@link DataSet} referentes a um conjunto de {@link Posto} que
 * lhe é passado como parâmetro no momento de sua criação através de um arquivo
 * de Postos. Como resultado de sua execução, um objeto da clase
 * {@link ExtratorDeVariaveisInterpolado} gera uma seqüência de {@link Extracao}
 * , que podem ser recuperadas em forma de objetos (
 * {@link ExtratorDeVariaveisInterpolado#getListaDeExtracoes()}) ou em em
 * formato String já ({@link ExtratorDeVariaveisInterpolado#getExtracoes()}).
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 12/02/2009
 * 
 */
public class ExtratorDeVariaveisInterpolado extends ExtratorDeVariaveisAbstract {

	private static Logger logger = Logger
			.getLogger(ExtratorDeVariaveisInterpolado.class);

	private Scanner scPontos;

	private InterpoladorIF interpolador;

	private InputStream streamDePontos;

	public ExtratorDeVariaveisInterpolado(DataSet dataSet)
			throws ExtratorOpendapException {
		super(dataSet);
		this.interpolador = new InterpoladorBilinearOPeNDAP();
	}

	public ExtratorDeVariaveisInterpolado(DataSet dataSet, File arquivoDePontos)
			throws ExtratorOpendapException {
		this(dataSet);
		try {
			this.streamDePontos = new FileInputStream(arquivoDePontos);
		} catch (FileNotFoundException e) {
			throw new ExtratorOpendapException("O arquivo de pontos '"
					+ arquivoDePontos + "' não existe ou não pode ser aberto.",
					e);
		}
	}

	/**
	 * @param dataSet
	 * @param streamDePontos
	 * @throws ExtratorOpendapException
	 */
	public ExtratorDeVariaveisInterpolado(DataSet dataSet,
			InputStream streamDePontos) throws ExtratorOpendapException {
		this(dataSet);
		this.streamDePontos = streamDePontos;
		scPontos = new Scanner(this.streamDePontos);
	}

	@Deprecated
	public ExtratorDeVariaveisInterpolado(Scanner scPontos, DataSet dataSet)
			throws ExtratorOpendapException {
		this(dataSet);
		this.scPontos = scPontos;
	}

	/**
	 * Extrator de valores a partir de dados grid, acessíveis pelo formato
	 * NetCDF. <pres> latitude longitude NomeDoPosto </pre> Como no exemplo:
	 * 
	 * <pre>
	 * -36.96306 -7.50694 Bananeiras-faz
	 * </pre>
	 * 
	 * @param arquivoDePontos
	 *            Informações de latitude e longitude para extração dos valores.
	 *            O arquivo deve obedecer ao padrão:
	 * @param arquivoDeExtracoes
	 *            O nome do arquivo em que os resultados serão salvos.
	 * @param dataSet
	 *            O arquivo de origem dos dados, no formato netcdf.
	 * @throws ExtratorOpendapException
	 *             Caso haja algum problema na leitura dos arquivos informados
	 *             ou no parser de algum dos valores.
	 */
	public ExtratorDeVariaveisInterpolado(DataSet dataSet,
			File arquivoDePontos, Date dataInicial, Date dataFinal,
			File arquivoDeExtracoes) throws ExtratorOpendapException {
		this(dataSet, arquivoDePontos);
		this.arquivoDeExtracoes = arquivoDeExtracoes;
		this.setDataInicial(dataInicial);
		this.setDataFinal(dataFinal);
	}

	/**
	 * Extrator de valores a partir de dados grid, acessíveis pelo formato
	 * NetCDF.
	 * 
	 * @param arquivoDePontos
	 *            Informações de latitude e longitude para extração dos valores.
	 *            O arquivo deve obedecer ao padrão: <pres> latitude longitude
	 *            NomeDoPosto </pre> Como no exemplo:
	 * 
	 *            <pre>
	 * -36.96306 -7.50694 Bananeiras-faz
	 * </pre>
	 * @param variavelDeInteresse
	 *            O nome da variável que se deseja extrair os valores.
	 * @param dataInicial
	 *            A data inicial para extraçaoo dos valores.
	 * @param dataFinal
	 *            A data final para extraçaoo dos valores.
	 * @param arquivoDeExtracoes
	 *            O nome do arquivo em que os resultados serão salvos.
	 * @param dataSet
	 *            O arquivo de origem dos dados, no formato netcdf.
	 * @throws ExtratorOpendapException
	 *             Caso haja algum problema na leitura dos arquivos informados
	 *             ou no parser de algum dos valores.
	 */
	public ExtratorDeVariaveisInterpolado(DataSet dataSet,
			File arquivoDePontos, Date dataInicial, Date dataFinal,
			String variavelDeInteresse, File arquivoDeExtracoes)
			throws ExtratorOpendapException {
		this(dataSet, arquivoDePontos, dataInicial, dataFinal,
				arquivoDeExtracoes);

		if (dataSet.contemVariavel(variavelDeInteresse)) {
			this.variavelDeInteresse = variavelDeInteresse;
		} else {
			throw new ExtratorOpendapException("O dataSet '" + dataSet
					+ "' não contém a variável de Interesse '"
					+ variavelDeInteresse + "'.");
		}
	}
	
	public ExtratorDeVariaveisInterpolado(DataSet dataSet,
			File arquivoDePontos, Date dataInicial, Date dataFinal)
			throws ExtratorOpendapException {
		this(dataSet, arquivoDePontos);
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
	}

	public ExtratorDeVariaveisInterpolado(DataSet dataSet,
			File arquivoDePontos, Date dataInicial, Date dataFinal,
			String variavelDeInteresse) throws ExtratorOpendapException {
		this(dataSet, arquivoDePontos, dataInicial, dataFinal);
		if (dataSet.contemVariavel(variavelDeInteresse)) {
			this.variavelDeInteresse = variavelDeInteresse;
		} else {
			throw new ExtratorOpendapException("O dataSet '" + dataSet
					+ "' não contém a variável de Interesse '"
					+ variavelDeInteresse + "'.");
		}
	}

	/**
	 * Extrator de valores a partir de dados grid, acessíveis pelo formato
	 * NetCDF. <pres> latitude longitude NomeDoPosto </pre> Como no exemplo:
	 * 
	 * <pre>
	 * -36.96306 -7.50694 Bananeiras-faz
	 * </pre>
	 * 
	 * @param dataSet
	 *            O arquivo de origem dos dados, no formato netcdf.
	 * @param arquivoDePontos
	 *            Informações de latitude e longitude para extração dos valores.
	 *            O arquivo deve obedecer ao padrão:
	 * @param arquivoDeExtracoes
	 *            O nome do arquivo em que os resultados serão salvos.
	 * @throws ExtratorOpendapException
	 *             Caso haja algum problema na leitura dos arquivos informados
	 *             ou no parser de algum dos valores.
	 */
	public ExtratorDeVariaveisInterpolado(DataSet dataSet,
			File arquivoDePontos, File arquivoDeExtracoes)
			throws ExtratorOpendapException {
		this(dataSet, arquivoDePontos);
		this.arquivoDeExtracoes = arquivoDeExtracoes;
	}

	/**
	 * Extrator de valores a partir de dados grid, acessíveis pelo formato
	 * NetCDF. <pres> latitude longitude NomeDoPosto </pre> Como no exemplo:
	 * 
	 * <pre>
	 * -36.96306 -7.50694 Bananeiras-faz
	 * </pre>
	 * 
	 * @param variavelDeInteresse
	 *            O nome da variável que se deseja extrair os valores.
	 * @param dataSet
	 *            O arquivo de origem dos dados, no formato netcdf.
	 * @param arquivoDePontos
	 *            Informações de latitude e longitude para extração dos valores.
	 *            O arquivo deve obedecer ao padrão:
	 * @param arquivoDeExtracoes
	 *            O nome do arquivo em que os resultados serão salvos.
	 * @throws ExtratorOpendapException
	 *             Caso haja algum problema na leitura dos arquivos informados
	 *             ou no parser de algum dos valores.
	 */
	public ExtratorDeVariaveisInterpolado(DataSet dataSet,
			File arquivoDePontos, String variavelDeInteresse,
			File arquivoDeExtracoes) throws ExtratorOpendapException {

		this(dataSet, arquivoDePontos);
		this.arquivoDeExtracoes = arquivoDeExtracoes;
		if (dataSet.contemVariavel(variavelDeInteresse)) {
			this.variavelDeInteresse = variavelDeInteresse;
		} else {
			throw new ExtratorOpendapException("O dataSet '" + dataSet
					+ "' não contém a variável de Interesse '"
					+ variavelDeInteresse + "'.");
		}

	}

	public ExtratorDeVariaveisInterpolado(DataSet dataSet,
			InputStream streamDePontos, Date dataInicial, Date dataFinal)
			throws ExtratorOpendapException {
		this(dataSet);
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
		this.streamDePontos = streamDePontos;
	}

	@Override
	public String extraiValoresDeInteresse() throws ExtratorOpendapException {
		logger.debug("Vai extrair os valores a partir de um conjunto de pontos.");
		Scanner sc = (scPontos != null) ? scPontos
				: new Scanner(streamDePontos);
		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			if(!line.startsWith("#")) {
				Scanner sc2 = new Scanner(line);
				// TODO verificar o caso para o qual o arquivo de pontos não está
				// devidamente definido.
				double longitude = new Double(sc2.next());
				double latitude = new Double(sc2.next());
				double pressureLevel = new Double(sc2.next());
				// String nomePosto = sc2.next();
				Coordenadas coordenadas = new Coordenadas(latitude, longitude, pressureLevel);
				logger.debug("Vai realizar a extração para a coordenada: "
						+ coordenadas);
				extraiValoresDeInteresse(coordenadas);
			}
		}
		return this.getExtracoes();
	}

	/**
	 * Realiza a extração dos valores da variável de interesse com base na
	 * latitude e longitude, realizando uma interpolação caso as coordenadas
	 * representadas não estejam exatamente localizadas no arquivo grid
	 * informado na construção deste extrator.
	 * 
	 * @param longitude
	 *            A longitude da localidade para a qual serão extraídos os
	 *            valores para a variável de interesse.
	 * @param latitude
	 *            A latitude da localidade para a qual serão extraídos os
	 *            valores para a variável de interesse.
	 */
	private void extraiValoresDeInteresse(Coordenadas coordenadas) {
		logger.debug("Vai pegar os dados para cada data.");
		for (Date data : dataSet.getCoordenadasDeTempo()) {
			logger.debug("Data da vez: " + formatadorDeDatas.format(data));
			if (intervaloNaoEhRelevante()
					|| dataEstahNoIntervaloEspecificado(data)) {
				coordenadas.setTime(data);
				logger.debug("Vai interpolar o valor.");
				double valorInterpolado;
				try {
					valorInterpolado = interpolador.interpola(dataSet,
							variavelDeInteresse, coordenadas);
					Extracao extracao = new Extracao(coordenadas,
							valorInterpolado, fatorDeConversao);
					extracoes.add(extracao);
					logger.debug("Extração da Vez: " + extracao);
					formatador.adicionaExtracao(extracao);
				} catch (ExtratorOpendapException e) {
					logger.error(e.getMessage(), e);
				}
			} else {
				logger.debug("Data não está no intervalo desejado.");
			}
			if (dataFinal != null && data.after(dataFinal)) {
				break;
			}
		}
	}

	public InputStream getStreamDePontos() {
		return streamDePontos;
	}

	public void setStreamDePontos(InputStream streamDePontos) {
		this.streamDePontos = streamDePontos;
	}

}
