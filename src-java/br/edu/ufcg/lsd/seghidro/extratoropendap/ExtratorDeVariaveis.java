package br.edu.ufcg.lsd.seghidro.extratoropendap;

import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeDatas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import ucar.ma2.Range;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.grid.GeoGrid;
import ucar.unidata.geoloc.LatLonPointImpl;
import ucar.unidata.geoloc.LatLonRect;
import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.formatador.FormatadorIF;
import br.edu.ufcg.lsd.seghidro.extratoropendap.formatador.FormatadorPMHLike;
import br.edu.ufcg.lsd.seghidro.extratoropendap.interpolador.InterpoladorBilinearOPeNDAP;
import br.edu.ufcg.lsd.seghidro.extratoropendap.interpolador.InterpoladorIF;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Posto;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.NetcdfAPIUtil;
import br.edu.ufcg.lsd.seghidro.util.FileUtil;

/**
 * 
 * A classe {@link ExtratorDeVariaveis} é o elemento ativo deste módulo, responsável por coordenar a 
 * extração de valores de interesse presentes em um {@link DataSet} referentes a um conjunto de {@link Posto} 
 * que lhe é passado como parâmetro no momento de sua criação através de um arquivo de Postos. 
 * Como resultado de sua execução, um objeto da clase {@link ExtratorDeVariaveis} gera uma seqüência de {@link Extracao}, 
 * que podem ser recuperadas em forma de objetos ({@link ExtratorDeVariaveis#getListaDeExtracoes()}) ou em em formato String já 
 * ({@link ExtratorDeVariaveis#getExtracoes()}).
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 12/02/2009
 *
 */
@Deprecated
public class ExtratorDeVariaveis {

    private static Logger logger = Logger.getLogger(ExtratorDeVariaveis.class);

    private String variavelDeInteresse;

    private DataSet dataSet;	

    private Date dataInicial;

    private Date dataFinal;

    private Scanner scPontos;

    private InterpoladorIF interpolador;

    private FormatadorIF formatador;

    private InputStream streamDePontos;

    private File arquivoDeExtracoes;

    private StringBuffer sb;
    
    private List<Extracao> extracoes;

    private double fatorDeConversao = 1;
    
    /**
     * Extrator de valores a partir de dados grid acessíveis pelo formato NetCDF.
     */
    private ExtratorDeVariaveis() {
	sb = new StringBuffer();
	formatador = new FormatadorPMHLike(sb);
	extracoes = new ArrayList<Extracao>();
    }

    /**
     * Extrator de valores a partir de dados grid, acessíveis pelo formato NetCDF.
     * @param dataSet O arquivo de origem dos dados, no formato netcdf. 
     * @throws ExtratorOpendapException Caso haja algum problema na leitura do arquivos informado.
     */
    public ExtratorDeVariaveis(DataSet dataSet) throws ExtratorOpendapException {
	this();
	this.dataSet = dataSet;
	this.interpolador = new InterpoladorBilinearOPeNDAP();
    }

    public ExtratorDeVariaveis(DataSet dataSet, String variavelDeInteresse) throws ExtratorOpendapException {
	this(dataSet);
	if (dataSet.contemVariavel(variavelDeInteresse)) {
	    this.variavelDeInteresse = variavelDeInteresse;
	} else {
	    throw new ExtratorOpendapException("O dataSet '" + dataSet + "' não contém a variável de Interesse '" + variavelDeInteresse + "'.");
	}
    }

    public ExtratorDeVariaveis(DataSet dataSet, Date dataInicial, Date dataFinal, String variavelDeInteresse) throws ExtratorOpendapException {
	this(dataSet,variavelDeInteresse);
	this.dataInicial = dataInicial;
	this.dataFinal = dataFinal;
    }

    public ExtratorDeVariaveis(DataSet dataSet, File arquivoDePontos) throws ExtratorOpendapException {
	this(dataSet);
	try {
	    this.streamDePontos = new FileInputStream(arquivoDePontos);
	} catch (FileNotFoundException e) {
	    throw new ExtratorOpendapException("O arquivo de pontos '" + arquivoDePontos + "' não existe ou não pode ser aberto.",e);
	}
    }

    /**
     * @param dataSet
     * @param streamDePontos
     * @throws ExtratorOpendapException
     */
    public ExtratorDeVariaveis(DataSet dataSet, InputStream streamDePontos) throws ExtratorOpendapException {
	this(dataSet);
	this.streamDePontos = streamDePontos;
	scPontos = new Scanner(this.streamDePontos);
    }

    @Deprecated
    public ExtratorDeVariaveis(Scanner scPontos, DataSet dataSet) throws ExtratorOpendapException {
	this(dataSet);
	this.scPontos = scPontos;
    }

    /**
     * Extrator de valores a partir de dados grid, acessíveis pelo formato NetCDF.
     * <pres> latitude longitude NomeDoPosto
     * </pre>
     * Como no exemplo:
     * <pre>-36.96306 -7.50694 Bananeiras-faz</pre>
     * @param arquivoDePontos Informações de latitude e longitude para extração dos valores. O arquivo deve obedecer ao padrão:
     * @param arquivoDeExtracoes O nome do arquivo em que os resultados serão salvos.
     * @param dataSet O arquivo de origem dos dados, no formato netcdf.
     * @throws ExtratorOpendapException Caso haja algum problema na leitura dos arquivos informados ou no parser de algum dos valores.
     */
    public ExtratorDeVariaveis(DataSet dataSet, File arquivoDePontos, Date dataInicial, Date dataFinal,
	    File arquivoDeExtracoes) throws ExtratorOpendapException {
	this(dataSet,arquivoDePontos);
	this.arquivoDeExtracoes = arquivoDeExtracoes;
	this.setDataInicial(dataInicial);
	this.setDataFinal(dataFinal);
    }	

    /**
     * Extrator de valores a partir de dados grid, acessíveis pelo formato NetCDF.
     * @param arquivoDePontos Informações de latitude e longitude para extração dos valores. O arquivo deve obedecer ao padrão:
     * <pres> latitude longitude NomeDoPosto
     * </pre>
     * Como no exemplo:
     * <pre>-36.96306 -7.50694 Bananeiras-faz</pre>
     * @param variavelDeInteresse O nome da variável que se deseja extrair os valores.
     * @param dataInicial A data inicial para extraçaoo dos valores.
     * @param dataFinal A data final para extraçaoo dos valores.
     * @param arquivoDeExtracoes O nome do arquivo em que os resultados serão salvos.
     * @param dataSet O arquivo de origem dos dados, no formato netcdf.
     * @throws ExtratorOpendapException Caso haja algum problema na leitura dos arquivos informados ou no parser de algum dos valores.
     */
    public ExtratorDeVariaveis(DataSet dataSet, File arquivoDePontos, Date dataInicial, Date dataFinal,String variavelDeInteresse,
	    File arquivoDeExtracoes) throws ExtratorOpendapException {
	this(dataSet,arquivoDePontos, dataInicial, dataFinal, arquivoDeExtracoes);

	if (dataSet.contemVariavel(variavelDeInteresse)) {
	    this.variavelDeInteresse = variavelDeInteresse;
	} else {
	    throw new ExtratorOpendapException("O dataSet '" + dataSet + "' não contém a variável de Interesse '" + variavelDeInteresse + "'.");
	}
    }
    
    
    public ExtratorDeVariaveis(DataSet dataSet, File arquivoDePontos, Date dataInicial, Date dataFinal
    	 ) throws ExtratorOpendapException {
    	this(dataSet,arquivoDePontos);
    	this.setDataInicial(dataInicial);
    	this.setDataFinal(dataFinal);
        }	
    
    public ExtratorDeVariaveis(DataSet dataSet, File arquivoDePontos, Date dataInicial, Date dataFinal,String variavelDeInteresse ) throws ExtratorOpendapException {
    	this(dataSet,arquivoDePontos, dataInicial, dataFinal);

    	if (dataSet.contemVariavel(variavelDeInteresse)) {
    	    this.variavelDeInteresse = variavelDeInteresse;
    	} else {
    	    throw new ExtratorOpendapException("O dataSet '" + dataSet + "' não contém a variável de Interesse '" + variavelDeInteresse + "'.");
    	}
        }

    /**
     * Extrator de valores a partir de dados grid, acessíveis pelo formato NetCDF.
     * <pres> latitude longitude NomeDoPosto
     * </pre>
     * Como no exemplo:
     * <pre>-36.96306 -7.50694 Bananeiras-faz</pre>
     * @param dataSet O arquivo de origem dos dados, no formato netcdf.
     * @param arquivoDePontos Informações de latitude e longitude para extração dos valores. O arquivo deve obedecer ao padrão:
     * @param arquivoDeExtracoes O nome do arquivo em que os resultados serão salvos.
     * @throws ExtratorOpendapException Caso haja algum problema na leitura dos arquivos informados ou no parser de algum dos valores.
     */
    public ExtratorDeVariaveis(DataSet dataSet, File arquivoDePontos, File arquivoDeExtracoes) throws ExtratorOpendapException {
	this(dataSet,arquivoDePontos);
	this.arquivoDeExtracoes = arquivoDeExtracoes;
    }

    /**
     * Extrator de valores a partir de dados grid, acessíveis pelo formato NetCDF.
     * <pres> latitude longitude NomeDoPosto
     * </pre>
     * Como no exemplo:
     * <pre>-36.96306 -7.50694 Bananeiras-faz</pre>
     * @param variavelDeInteresse O nome da variável que se deseja extrair os valores.
     * @param dataSet O arquivo de origem dos dados, no formato netcdf.
     * @param arquivoDePontos Informações de latitude e longitude para extração dos valores. O arquivo deve obedecer ao padrão:
     * @param arquivoDeExtracoes O nome do arquivo em que os resultados serão salvos.
     * @throws ExtratorOpendapException Caso haja algum problema na leitura dos arquivos informados ou no parser de algum dos valores.
     */
    public ExtratorDeVariaveis(DataSet dataSet, File arquivoDePontos, String variavelDeInteresse, File arquivoDeExtracoes) throws ExtratorOpendapException {
	this(dataSet,arquivoDePontos);
	this.arquivoDeExtracoes = arquivoDeExtracoes;
	if (dataSet.contemVariavel(variavelDeInteresse)) {
	    this.variavelDeInteresse = variavelDeInteresse;
	} else {
	    throw new ExtratorOpendapException("O dataSet '" + dataSet + "' não contém a variável de Interesse '" + variavelDeInteresse + "'.");
	}

    }    

  

	/**
     * Realiza a extração das anomalias a partir do arquivo netCDF informado na criação deste construtor. 
     * Este é o método base para este extrator, devendo ser o primeiro chamado caso o objetivo seja acessar algum valor 
     * referente às anomalias extraídas.
     * @return As anomalias já no formato textual padronizado.
     * @throws ExtratorOpendapException 
     */
    public String extraiValoresDeInteresse() throws ExtratorOpendapException {
	logger.debug("Vai extrair os valores a partir de um conjunto de pontos.");
	Scanner sc = ( scPontos!=null ) ? scPontos : new Scanner(streamDePontos);		
	while (sc.hasNextLine()) {
	    Scanner sc2 = new Scanner(sc.nextLine());
	    //TODO verificar o caso para o qual o arquivo de pontos não está devidamente definido.
	    double longitude = new Double(sc2.next());
	    double latitude = new Double(sc2.next());
	    String nomePosto = sc2.next();
	    Coordenadas coordenadas = new Coordenadas(latitude,longitude);
	    logger.debug("Vai realizar a extração para a coordenada: " + coordenadas);
	    extraiValoresDeInteresse(coordenadas);
	}
	return this.getExtracoes();
    }

    /**
     * Extrai valores referentes a todos os pontos de grade presentes em uma região retangular de interesse, sem realizar interpolação dos valores contidos.
     * @return valores referentes a todos os pontos de grade presentes em uma região retangular de interesse, sem realizar interpolação dos valores contidos.
     * @throws ExtratorOpendapException
     */
    public String extraiPontosDeGrade(Coordenadas p1, Coordenadas p2) throws ExtratorOpendapException {
	extraiValoresDeInteresse(p1,p2);
	return this.getExtracoes();
    }

    /**
     * Realiza a extração dos valores da variável de interesse com base na latitude e longitude, 
     * realizando uma interpolação caso as coordenadas representadas não estejam exatamente localizadas 
     * no arquivo grid informado na construção deste extrator.
     * @param longitude A longitude da localidade para a qual serão extraídos os valores para a variável de interesse.
     * @param latitude A latitude da localidade para a qual serão extraídos os valores para a variável de interesse.
     */
    public void extraiValoresDeInteresse(Coordenadas coordenadas) {
	logger.debug("Vai pegar os dados para cada data.");
	for (Date data: dataSet.getCoordenadasDeTempo()) {
	    logger.debug("Data da vez: " + formatadorDeDatas.format(data));
	    if (intervaloNaoEhRelevante() || dataEstahNoIntervaloEspecificado(data)) {
		coordenadas.setTime(data);
		logger.debug("Vai interpolar o valor.");
		double valorInterpolado;
		try {
		    valorInterpolado = interpolador.interpola(dataSet, variavelDeInteresse, coordenadas);
		    logger.debug("ValorInterpolado: " + valorInterpolado);
		    double valorConvertido = valorInterpolado * getFatorDeConversao();
		    logger.debug("ValorConvertido: " + valorConvertido);
		    Extracao extracao = new Extracao(coordenadas, valorConvertido);
		    extracoes.add(extracao);
		    formatador.adicionaExtracao(extracao);
		} catch (ExtratorOpendapException e) {
		    logger.error(e.getMessage(),e);
		}
	    } else {
		logger.debug("Data não está no intervalo desejado.");
	    }
	}
    }

    /**
     * Extrai os valores que estiverem no retângulo limitado pelos dois pontos (coordenadas passados como parâmetro).
     * @param cp1 O ponto inferior esquerdo do retângulo.
     * @param cp2 O ponto superior direito do retângulo.
     * @throws ExtratorOpendapException
     */
    private void extraiValoresDeInteresse(Coordenadas cp1, Coordenadas cp2) throws ExtratorOpendapException {
	try{
	    logger.debug("Vai realizar a extração dos pontos de grade para as coordenadas \n"+cp1+"\n"+cp2);
	    GeoGrid grid;
	    if (variavelDeInteresse == null) {
		grid = dataSet.getFirstGrid();
	    }else{
		grid = dataSet.getGrid(variavelDeInteresse);
	    }
	    GridCoordSystem coordinateSystem = grid.getCoordinateSystem();
	    CoordinateAxis longitudeVariable = coordinateSystem.getXHorizAxis();
	    CoordinateAxis latitudeVariable = coordinateSystem.getYHorizAxis();

	    LatLonPointImpl p1 = new LatLonPointImpl(cp1.getLatitude(), cp1.getLongitude());
	    LatLonPointImpl p2 = new LatLonPointImpl(cp2.getLatitude(), cp2.getLongitude());
	    LatLonRect latLonRect = new LatLonRect(p1, p2);
	    List rangesList = coordinateSystem.getRangesFromLatLonRect(latLonRect);
	    List ranges =  new ArrayList();
	    Range rangeLatitude = (Range)rangesList.get(0);
	    Range rangeLongitude = (Range)rangesList.get(1);
	    Range rangeTime = new Range(0,dataSet.getCoordenadasDeTempo().length-1);
	    ranges.add(rangeTime);
	    ranges.addAll(rangesList);

	    Dimension timeDimension = grid.getTimeDimension();
	    Variable timeVariable = timeDimension.getCoordinateVariables().get(0);

	    for (int t = rangeTime.first(); t <= rangeTime.last(); t++) {
		Date d= NetcdfAPIUtil.readTime(timeVariable, t);
		if (dataEstahNoIntervaloEspecificado(d)) {
		    for (int lat = rangeLatitude.first(); lat <= rangeLatitude.last(); lat++) {
			for (int lon = rangeLongitude.first(); lon <= rangeLongitude.last(); lon++) {
			    double longi = LatLonPointImpl.lonNormal(NetcdfAPIUtil.readSingleValueAsFloat(longitudeVariable, new int[] { lon }));
			    double lati = NetcdfAPIUtil.readSingleValueAsFloat(latitudeVariable, new int[] { lat });
			    int[] unicoElemento = new int[] { 1, 1, 1 };
			    double valor = NetcdfAPIUtil.readAsFloat(grid.getVariable(), new int[] { t, lat, lon }, unicoElemento);
			    formatador.adicionaExtracao(new Coordenadas(lati, longi, d), valor);
			}
		    }
		}
	    }

	} catch (Exception e) {
	    logger.error("Não conseguiu extrair",e);
	}
	logger.debug("Extração de Pontos de Grade Encerrada.");
    }

    /**
     * Verifica se <code>data</code> está no intervalo especificado para a extração de valores.
     * @param data
     * @return
     */
    private boolean dataEstahNoIntervaloEspecificado(Date data) {
	return data.after(dataInicial) && data.before(dataFinal);
    }

    /**
     * Verifica se o intervalo de tempo é um fator relevante para a extração de valores.
     * @return
     */
    private boolean intervaloNaoEhRelevante() {
	return dataInicial == null && dataFinal == null;
    }

    /**
     * Recupera as anomalias extraídas em um formato textual. Dentro do padrão SegHidro, trata-se de um formato PMH-like, com as
     * localizações espacial e temporal dos valores extraídos. Um exemplo do formato de saída é:
     * <pre>
     *            -36.9631             -7.5069       0 2055-01-16 00:00:00         -3.2 -
     *            -36.9631             -7.5069       0 2055-02-16 00:00:00          0.0 -
     *            -36.9631             -7.5069       0 2055-03-16 00:00:00          0.8 -
     *            -36.9631             -7.5069       0 2055-04-16 00:00:00          1.6 -
     *            -36.9631             -7.5069       0 2055-05-16 00:00:00          1.1 -
     *            -36.9631             -7.5069       0 2055-06-16 00:00:00          0.2 -
     *            -36.9631             -7.5069       0 2055-07-16 00:00:00          0.2 -
     *            -36.9631             -7.5069       0 2055-08-16 00:00:00          0.0 -
     *            -36.9631             -7.5069       0 2055-09-16 00:00:00          0.1 -
     *            -36.9631             -7.5069       0 2055-10-16 00:00:00          0.0 -
     *            -36.9631             -7.5069       0 2055-11-16 00:00:00          0.0 -
     *            -36.9631             -7.5069       0 2055-12-16 00:00:00          0.0 -
     * </pre>
     * @return A representação das extrações em um formato textual. 
     */
    public String getExtracoes() {
	return sb.toString();
    }

    /**
     * Recupera todas as extrações realizadas.
     * @return todas as extrações realizadas ou <tt>null</tt> se o método {@link ExtratorDeVariaveis#extraiValoresDeInteresse()} ainda não tiver sido chamado.
     */
    public List<Extracao> getListaDeExtracoes() {
	return extracoes;
    }

    public InputStream getStreamDePontos() {
	return streamDePontos;
    }

    public void setStreamDePontos(InputStream streamDePontos) {
	this.streamDePontos = streamDePontos;
    }

    public void setDataInicial(Date dataInicial) {
	this.dataInicial = dataInicial;
    }

    public void setDataFinal(Date dataFinal) {
	this.dataFinal = dataFinal;
    }

    /**
     * Recupera o fator de conversão a ser utilizado na formatação dos valores extraídos.
     * @return fator de conversão a ser utilizado na formatação dos valores extraídos.
     */
    public double getFatorDeConversao() {
	return fatorDeConversao;
    }

    /**
     * Atribui um fator de conversão a ser utilizado na formatação dos valores extraídos.
     * @param fatorDeConversao O fator de conversão.
     */
    public void setFatorDeConversao(double fatorDeConversao) {
	this.fatorDeConversao = fatorDeConversao;
    }
    
    public boolean writeOutPutFile(){
	return FileUtil.salvaArquivoEmDisco(arquivoDeExtracoes.getAbsolutePath(), this.getExtracoes());
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
	.append("variavelDeInteresse", variavelDeInteresse).append(
		"dataInicial", dataInicial).append("dataFinal",
			dataFinal).toString();
    }
    

}
