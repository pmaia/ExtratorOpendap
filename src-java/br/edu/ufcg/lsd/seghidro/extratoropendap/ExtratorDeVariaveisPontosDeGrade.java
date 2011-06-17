package br.edu.ufcg.lsd.seghidro.extratoropendap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Posto;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.NetcdfAPIUtil;

/**
 * 
 * A classe {@link ExtratorDeVariaveisPontosDeGrade} é o elemento ativo deste
 * módulo, responsável por coordenar a extração de valores de interesse
 * presentes em um {@link DataSet} referentes a um conjunto de {@link Posto} que
 * lhe é passado como parâmetro no momento de sua criação através de um arquivo
 * de Postos. Como resultado de sua execução, um objeto da clase
 * {@link ExtratorDeVariaveisPontosDeGrade} gera uma seqüência de
 * {@link Extracao}, que podem ser recuperadas em forma de objetos ({@link ExtratorDeVariaveisPontosDeGrade#getListaDeExtracoes()})
 * ou em em formato String já ({@link ExtratorDeVariaveisPontosDeGrade#getExtracoes()}).
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 12/02/2009
 * 
 */
public class ExtratorDeVariaveisPontosDeGrade extends ExtratorDeVariaveisAbstract {

    private static Logger logger = Logger.getLogger(ExtratorDeVariaveisPontosDeGrade.class);

    private Coordenadas coordMinEsquerda;
    private Coordenadas coordMaxDireita;

    public ExtratorDeVariaveisPontosDeGrade(DataSet dataSet, Date dataInicial, Date dataFinal, String variavelDeInteresse, Coordenadas cp1, Coordenadas cp2)
	    throws ExtratorOpendapException {
	super(dataSet, variavelDeInteresse, dataInicial, dataFinal);
	this.coordMinEsquerda = cp1;
	this.coordMaxDireita = cp2;
    }

    public ExtratorDeVariaveisPontosDeGrade(DataSet dataSet, Date dataInicial, Date dataFinal, Coordenadas cp1, Coordenadas cp2)
	    throws ExtratorOpendapException {
	super(dataSet, dataInicial, dataFinal);
	this.coordMinEsquerda = cp1;
	this.coordMaxDireita = cp2;
    }

    public ExtratorDeVariaveisPontosDeGrade(DataSet ds, String variavelDeInteresse, Coordenadas cp1, Coordenadas cp2) throws ExtratorOpendapException {
	super(ds, variavelDeInteresse);
	this.coordMinEsquerda = cp1;
	this.coordMaxDireita = cp2;
    }

    public ExtratorDeVariaveisPontosDeGrade(DataSet ds, Coordenadas cp1, Coordenadas cp2) throws ExtratorOpendapException {
	super(ds);
	this.coordMinEsquerda = cp1;
	this.coordMaxDireita = cp2;
    }

    @Override
    public String extraiValoresDeInteresse() throws ExtratorOpendapException {
	extraiPontosDeGrade();
	return this.getExtracoes();
    }

    /**
     * Extrai os valores que estiverem no retângulo limitado pelos dois pontos
     * (coordenadas passados como parâmetro).
     * 
     * @param coordMinEsquerda
     *                O ponto inferior esquerdo do retângulo.
     * @param coordMaxDireita
     *                O ponto superior direito do retângulo.
     * @throws ExtratorOpendapException
     */
    private void extraiPontosDeGrade() throws ExtratorOpendapException {
	try {
	    logger.debug("Vai realizar a extração dos pontos de grade para as coordenadas \n" + coordMinEsquerda + "\n" + coordMaxDireita);
	    GeoGrid grid;
	    if (variavelDeInteresse == null) {
		grid = dataSet.getFirstGrid();
	    } else {
		grid = dataSet.getGrid(variavelDeInteresse);
	    }
	    GridCoordSystem coordinateSystem = grid.getCoordinateSystem();
	    CoordinateAxis longitudeVariable = coordinateSystem.getXHorizAxis();
	    CoordinateAxis latitudeVariable = coordinateSystem.getYHorizAxis();

	    LatLonPointImpl p1 = new LatLonPointImpl(coordMinEsquerda.getLatitude(), coordMinEsquerda.getLongitude());
	    LatLonPointImpl p2 = new LatLonPointImpl(coordMaxDireita.getLatitude(), coordMaxDireita.getLongitude());
	    LatLonRect latLonRect = new LatLonRect(p1, p2);
	    List rangesList = coordinateSystem.getRangesFromLatLonRect(latLonRect);
	    List ranges = new ArrayList();
	    Range rangeLatitude = (Range) rangesList.get(0);
	    Range rangeLongitude = (Range) rangesList.get(1);
	    // Range rangeTime = new Range(0,
	    // dataSet.getCoordenadasDeTempo().length - 1);
	    int ri = 0;
//	    if (dataInicial != null) {
//		ri = dataSet.getIndexFromTime(dataInicial);
//	    }
	    int rf = dataSet.getCoordenadasDeTempo().length - 1;
//	    if (dataFinal != null) {
//		rf = dataSet.getIndexFromTime(dataFinal);
//	    }
	    Range rangeTime = new Range(ri, rf);
	    ranges.add(rangeTime);
	    ranges.addAll(rangesList);

	    Dimension timeDimension = grid.getTimeDimension();
	    Variable timeVariable = timeDimension.getCoordinateVariables().get(0);

//	    for (int t = rangeTime.first(); t <= rangeTime.last(); t++) {
	    for (int n = 0; n<dataSet.getCoordenadasDeTempo().length; n++) {
		Date d = dataSet.getCoordenadasDeTempo()[n];
		int t = dataSet.getCoordenadasDeTempoIndex()[n];
		logger.debug("t= " + t);
//		logger.debug("t= " + t);
//		Date d = NetcdfAPIUtil.readTime(timeVariable, t);
		logger.debug("date= " + LocaleUtil.formatadorDeDatas.format(d));
		if (dataFinal != null && d.after(dataFinal)) {
		    break;
		}
		if (intervaloNaoEhRelevante() || dataEstahNoIntervaloEspecificado(d)) {
		    for (int lat = rangeLatitude.first(); lat <= rangeLatitude.last(); lat++) {
			for (int lon = rangeLongitude.first(); lon <= rangeLongitude.last(); lon++) {
			    double longi = LatLonPointImpl.lonNormal(NetcdfAPIUtil.readSingleValueAsFloat(longitudeVariable, new int[] { lon }));
			    double lati = NetcdfAPIUtil.readSingleValueAsFloat(latitudeVariable, new int[] { lat });
			    logger.debug("latitude: " + lati);
			    logger.debug("longitude: " + longi);
			    double valor;
			    int[] unicoElemento = NetcdfAPIUtil.getUniqueShapeForGrid(grid);
			    if (grid.getRank() == 3) {
				valor = NetcdfAPIUtil.readAsFloat(grid.getVariable(), new int[] { t, lat, lon }, unicoElemento);
			    } else if (grid.getRank() == 4) {
				valor = NetcdfAPIUtil.readAsFloat(grid.getVariable(), new int[] { t, 0, lat, lon }, unicoElemento);
			    } else {
				logger.error("Não pôde efetuar a leitura do DataSet. Dimensão '" + grid.getRank() + "' não esperada.");
				throw new ExtratorOpendapException("Não pôde efetuar a leitura do DataSet. Dimensão '" + grid.getRank() + "' não esperada.");
			    }

			    Coordenadas coordenadas = new Coordenadas(lati, longi, d);
			    Extracao extracao = new Extracao(coordenadas, valor, fatorDeConversao);
			    logger.debug("Extração da Vez: " + extracao);
			    formatador.adicionaExtracao(extracao);
			}
		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error("Não conseguiu extrair", e);
	    throw new ExtratorOpendapException(e.getMessage());
	}
	logger.debug("Extração de Pontos de Grade Encerrada.");
    }

}
