package br.edu.ufcg.lsd.seghidro.extratoropendap.model;

import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeDatas;
import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeNumeros;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import thredds.datatype.DateType;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dt.grid.GeoGrid;
import ucar.nc2.dt.grid.GridDataset;
import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.Constantes;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.NetcdfAPIUtil;

/**
 * 
 * A classe DataSet é responsável por encapsular a complexidade de lidar com os
 * arquivos no formato NetCDF ou com o protocolo OPeNDAP, provendo uma interface
 * pública que representa a essência dos datasets disponibilizados pelo IPCC,
 * conforme pode ser visto nos métodos {@link DataSet#getNomeModeloOrigem()} e
 * {@link DataSet#getNomeCenarioOrigem()}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 10/03/2009
 * 
 */
public class DataSet {

    private static Logger logger = Logger.getLogger(DataSet.class);

    private static final String MODEL_TAG = "model_tag";

    private static final String SCENARIO_TAG = "scenario_tag";

    private GridDataset gridDs;

    private Date[] coordenadasDeTempo;

    private int[] coordenadasDeTempoIndex;

    /**
     * Construtor para um DataSet.
     * 
     * @param path
     *                O caminho que aponta para o dataset real a ser encapsulado
     *                neste DataSet. O caminho pode ser tanto referente à um
     *                arquivo netcdf local quando a uma url opendap, referente a
     *                um dataset acessado remotamente.
     * @throws ExtratorOpendapException
     *                 Caso haja algum problema no acesso ao dataset indicado
     *                 pelo caminho.
     */
    public DataSet(String path, Date dataInicial, Date dataFinal) throws ExtratorOpendapException {
	this(path);
	defineCoordenadasDeTempo(dataInicial, dataFinal);
    }

    public DataSet(String path) throws ExtratorOpendapException {
	if (path == null || path.trim().isEmpty()) {
	    throw new IllegalArgumentException("Informe um caminho de dataset válido. Foi passado: '" + path +"'.");
	}
	try {
	    gridDs = GridDataset.open(path);
	} catch (IOException e1) {
	    throw new ExtratorOpendapException("O dataset '" + path + "' não existe ou não pode ser aberto.", e1);
	}

	if (getTimeVariable() == null) {
	    throw new ExtratorOpendapException("O arquivo de origem não possui uma variável 'time' definida.");
	}

	int timeLength = Math.round(getTimeVariable().getSize());
	if (timeLength > Constantes.MAX_TIME_LENGTH) {
	    throw new ExtratorOpendapException("O Dataset possui coordenada de tempo muito grande: '" + timeLength + "'. O Máximo permitido é: "
		    + Constantes.MAX_TIME_LENGTH);
	}
    }

    public String getNome() {
	try {
	    File file = new File(this.gridDs.getName());
	    String nome = file.getName();
	    if (nome.lastIndexOf(".") != -1) {
		String nomeSemExtencao = nome.substring(0, nome.lastIndexOf("."));
		return nomeSemExtencao;
	    } else {
		return nome;
	    }
	} catch (Exception e) {
	    logger.error("Não conseguiu pegar o nome do dataset.", e);
	    return "";
	}
    }

    /**
     * Recupera o caminho original referente ao dataset.
     * 
     * @return
     */
    private String getPath() {
	return gridDs.getLocationURI();
    }

    public void defineCoordenadasDeTempo(Date dataInicial, Date dataFinal) {
	int timeLength = Math.round(getTimeVariable().getSize());
	int inicio = -1;
	int fim = 0;
	//define o limite inicial e final
	for (int time = 0; time < timeLength; time++) {
	    Date d = NetcdfAPIUtil.readTime(getTimeVariable(), time);
	    if (inicio == -1 && d.after(dataInicial)) {
		inicio = time - 1;
		logger.debug("dataInicial : " + d);
	    }

	    if (fim == 0 && d.after(dataFinal)) {
		fim = time;
		logger.debug("dataFinal : " + d);
		break;
	    }
	}

	//prenche as coordenadas de tempo mantendo um backup com os índices originais
	if (fim - inicio > 0) {
	    coordenadasDeTempo = new Date[fim - inicio + 1];
	    coordenadasDeTempoIndex = new int[fim - inicio + 1];
	    int cont = 0;
	    for (int i = inicio; i <= fim; i++) {
		coordenadasDeTempo[cont] = NetcdfAPIUtil.readTime(getTimeVariable(), i);
		coordenadasDeTempoIndex[cont] = i;
		cont++;
	    }
	} else {
	    coordenadasDeTempo = new Date[0];
	    coordenadasDeTempoIndex = new int[0];
	}

    }

    /**
     * Recupera o período ao qual o dataset ser refere de uma forma
     * user-friendly (p. ex. 2005-2008)
     * 
     * @return O periodo referente ao dataset formatado como um String.
     */
    public String getPeriodo() {
	final String SEP = "-";
	String periodoTemp = getInicioPeriodo() + SEP + getFimPeriodo();
	return periodoTemp.trim().equals(SEP) ? "" : periodoTemp;
    }

    /**
     * Recupera uma listagem de todos os nomes das variáveis presentes no
     * arquivo NetCDF indicado na criação deste Extrator.
     * 
     * @return Uma lista com os nomes das variáveis.
     */
    public List<String> getVariaveis() {
	List<String> variaveis = new ArrayList<String>();
	List<GeoGrid> grids = gridDs.getGrids();
	for (GeoGrid grid : grids) {
	    variaveis.add(grid.getVariable().getName());
	}
	return variaveis;
    }

    /**
     * Verifica se o dataset contém uma variável em particular.
     * 
     * @param nomeVariavel
     *                O nome da variável a ser verificada.
     * @return True se a variável estiver presente, false caso contrário.
     */
    public boolean contemVariavel(String nomeVariavel) {
	return getVariaveis().contains(nomeVariavel);
    }

    /**
     * Recupera o nome simples da variável contida neste dataset.
     * 
     * @return o nome simples da variável contida neste dataset.
     */
    public String getVariavelSimpleName() {
	return getVariaveis().get(0).replace("_anomaly", "");
    }

    /**
     * Recupera o nome completo da variável contida neste dataset.
     * 
     * @return o nome completo da variável contida neste dataset.
     */
    public String getVariavelFullName() {
	return getVariaveis().get(0);
    }

    /**
     * Recupera o nome do modelo que foi utilizado para a geração dos dados
     * presentes no arquivo NetCDF indicado na criação deste Extrator.
     * 
     * @return O nome do modelo de origem. Caso o dado não tenha um modelo de
     *         origem, será retornada "", a String vazia.
     */
    public String getNomeModeloOrigem() {
	for (Object o : this.gridDs.getGlobalAttributes()) {
	    Attribute attribute = (Attribute) o;
	    if (attribute.getName().equalsIgnoreCase(MODEL_TAG) && attribute.isString()) {
		return attribute.getStringValue();
	    }
	}
	logger.warn("Não conseguiu pegar o nome do modelo origem. Vai retornar \"\"");
	return "";
    }

    protected Variable getTimeVariable() {
	return gridDs.getNetcdfDataset().findVariable("time");
    }

    /**
     * Recupera o ano central referente à este dataset.
     * 
     * @return o ano central referente à este dataset.
     */
    public int getAnoCentral() {
	Variable timeVariable = getTimeVariable();
	Object dateRelative = NetcdfAPIUtil.readSingleValue(timeVariable, new int[] { 0 });

	DateType data;
	try {
	    data = new DateType(dateRelative + " " + timeVariable.getUnitsString(), null, null);
	    Date d = data.getDate();
	    int anoCentral = Integer.parseInt(formatadorDeDatas.format(d).substring(0, 4));
	    return anoCentral;
	} catch (ParseException e) {
	    logger.error("Não conseguiu pegar o ano central. Vai retornar 0.", e);
	    return 0;
	}

    }

    /**
     * Recupera informações textuais detalhadas a respeito deste dataset.
     * 
     * @return informações textuais detalhadas a respeito deste dataset.
     */
    public String getDetalhes() {
	return this.gridDs.getDetailInfo();
    }

    /**
     * Recupera a data inicial referente aos dados presentes neste dataset.
     * 
     * @return a data inicial referente aos dados presentes neste dataset.
     */
    public String getDataInicial() {
	return formatadorDeDatas.format(this.gridDs.getStartDate());
    }

    /**
     * Recupera a data final referente aos dados presentes neste dataset.
     * 
     * @return a data final referente aos dados presentes neste dataset.
     */
    public String getDataFinal() {
	return formatadorDeDatas.format(this.gridDs.getEndDate());
    }

    /**
     * Recupera a ano do início do período representado pelos dados presentes no
     * arquivo netCDF indicado na construção deste Extrator. Caso os dados
     * representem um período, será retornada "", a String vazia. TODO Para a
     * extração do ano, foi utilizado o nome do arquivo original. Até o momento,
     * não foi descoberta outra forma de se fazer isto.
     * 
     * @return O ano inicial.
     */
    public String getInicioPeriodo() {
	try {
	    // a estratégia seguinte se baseia na convenção presente nos nomes
	    // dos datasets disponilizados pelo ipcc, portanto, deve retornar
	    // algo inesperado se aplicado para outros datasets.
	    String nameDataSet = this.gridDs.getName();
	    int indexLastHifem = nameDataSet.lastIndexOf("-");
	    int indexInicioPeriodo = nameDataSet.lastIndexOf("_") + 1;
	    String temp = nameDataSet.substring(indexInicioPeriodo, indexLastHifem).replace("o", "");
	    // só retorna se for um número inteiro. isto é a segurança caso a
	    // heurística acima seja aplicada para datasets que não sejam
	    // oriundos do ipcc.
	    return String.format("%04d", Integer.parseInt(temp));
	} catch (Exception e) {
	    logger.warn("Não conseguiu pegar o início do período. Vai retornar \"\" " + e.getMessage());
	    return "";
	}
    }

    /**
     * Recupera a ano do fim do período representado pelos dados presentes no
     * arquivo netCDF indicado na construção deste Extrator. Caso os dados
     * representem um período, será retornada "", a String vazia. TODO Para a
     * extração do ano, foi utilizado o nome do arquivo original. Até o momento,
     * não foi descoberta outra forma de se fazer isto.
     * 
     * @return O ano final.
     */
    public String getFimPeriodo() {
	// a estratégia seguinte se baseia na convenção presente nos nomes
	// dos datasets disponilizados pelo ipcc, portanto, deve retornar
	// algo inesperado se aplicado para outros datasets.

	// não retorna o fim se não houver um início
	if (getInicioPeriodo().trim().equals("")) {
	    return "";
	}
	try {
	    String nameDataSet = this.gridDs.getName();
	    int indexLastHifem = nameDataSet.lastIndexOf("-");
	    int indexFimPeriodo = nameDataSet.lastIndexOf(".");
	    // TODO Tratamento para caso o arquivo venha do opendap. Pensar em
	    // uma solução melhor.
	    if (gridDs.getName().startsWith("dods")) {
		indexFimPeriodo = nameDataSet.length();
	    }
	    String provavelFimPeriodo = nameDataSet.substring(indexLastHifem + 1, indexFimPeriodo);
	    if (provavelFimPeriodo.endsWith(".nc")) {
		provavelFimPeriodo = provavelFimPeriodo.replace(".nc", "");
	    }
	    // só retorna se for um número inteiro. isto é a segurança caso a
	    // heurística acima seja aplicada para datasets que não sejam
	    // oriundos do ipcc.
	    return String.format("%04d", Integer.parseInt(provavelFimPeriodo));
	} catch (Exception e) {
	    logger.error("Não conseguiu pegar o fim do período. Vai retornar \"\" " + e.getMessage());
	    return "";
	}

    }

    /**
     * Recupera o nome do cenário que foi utilizado para a geração dos dados
     * presentes no arquivo NetCDF indicado na criação deste Extrator.
     * 
     * @return O nome do cenário de origem. Caso os dados não tenham um cenário
     *         de origem, será retornado "", a String vazia.
     */
    public String getNomeCenarioOrigem() {
	for (Object o : this.gridDs.getGlobalAttributes()) {
	    Attribute attribute = (Attribute) o;
	    if (attribute.getName().equalsIgnoreCase(SCENARIO_TAG) && attribute.isString()) {
		return attribute.getStringValue();
	    }
	}
	logger.warn("Não conseguiu pegar o nome do cenário origem. Vai retornar \"\"");
	return "";
    }

    /**
     * Recupera a unidade na qual os dados do arquivo netCDF indicado na criação
     * deste Extrator estão representados. TODO Neste caso, há a suposição de
     * haver uma única variável por arquivo. netcdf.
     * 
     * @return A unidade em que os dados estão representados.
     */
    public String getUnidade() {
	try {
	    List grids = gridDs.getGrids();
	    GeoGrid grid = (GeoGrid) grids.get(0);
	    return grid.getVariable().getUnitsString();
	} catch (Exception e) {
	    logger.error("Não conseguiu pegar a unidade. Vai retornar \"\"", e);
	    return "";
	}
    }

    /**
     * Recupera o GridDataset a partir do qual podem ser recuperados os valores,
     * atributos e metadados presentes neste dataset.
     * 
     * @return o GridDataset a partir do qual podem ser recuperados os valores
     *         presentes neste dataset.
     */
    public GridDataset getGridDs() {
	return gridDs;
    }

    /**
     * Recupera o GeoGrid a partir do qual podem ser recuperados os valores
     * presentes neste dataset.
     * 
     * @return o GeoGrid a partir do qual podem ser recuperados os valores
     *         presentes neste dataset.
     */
    public GeoGrid getFirstGrid() {
	List grids = this.getGridDs().getGrids();
	GeoGrid grid = (GeoGrid) grids.get(0);
	return grid;
    }

    public GeoGrid getGrid(String variavelDeInteresse) {
	List grids = this.getGridDs().getGrids();
	for (Object object : grids) {
	    GeoGrid grid = (GeoGrid) object;
	    if (grid.getName().equalsIgnoreCase(variavelDeInteresse.trim())) {
		return grid;
	    }
	}
	return null;
    }

    /**
     * Recupera o indíce no Grid de dados referente a data passada como
     * parâmetro.
     * 
     * @param date
     *                A data referente à qual se deseja determinar o índice.
     * @return o indíce no Grid de dados referente a data passada como
     *         parâmetro.
     */
    public int getIndexFromTime(Date date) {
	for (int i = 0; i < coordenadasDeTempo.length; i++) {
	    if (date.equals(coordenadasDeTempo[i]) || coordenadasDeTempo[i].after(date)) {
		return i;
	    }
	}
	logger.error("Não conseguiu pegar o índice do tempo. Vai retornar -1");
	return -1;
    }

    /**
     * Recupera todas as coordenadas de tempo presente neste dataset.
     * 
     * @return todas as coordenadas de tempo presente neste dataset.
     */
    public Date[] getCoordenadasDeTempo() {
	if (coordenadasDeTempo==null) {
	    defineCoordenadasDeTempo(this.gridDs.getStartDate(), this.gridDs.getEndDate());
	}
	return coordenadasDeTempo;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("Nome da Variável", this.getVariavelFullName()).append("Nome do Cenario",
		this.getNomeCenarioOrigem()).append("Período:", this.getPeriodo()).append("Nome do Modelo:", this.getNomeModeloOrigem()).append("Path:",
		this.getPath()).toString();
    }

    public boolean temNomeDeCenario() {
	return !this.getNomeCenarioOrigem().trim().equals("");
    }

    public boolean temNomeDeModelo() {
	return !this.getNomeModeloOrigem().trim().equals("");
    }

    public boolean temPeriodo() {
	return !this.getPeriodo().trim().equals("");
    }

    public int[] getCoordenadasDeTempoIndex() {
        return coordenadasDeTempoIndex;
    }

}
