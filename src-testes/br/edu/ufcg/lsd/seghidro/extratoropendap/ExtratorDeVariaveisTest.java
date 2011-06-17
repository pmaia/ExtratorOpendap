package br.edu.ufcg.lsd.seghidro.extratoropendap;

import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeDatas;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ucar.nc2.dt.grid.GeoGrid;
import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;

public class ExtratorDeVariaveisTest {

    private static final double TOLERANCIA = 0.0001;

    File arquivoDePontos;

    String variavelDeInteresse;

    File arquivoDeExtracoes;

    String arquivoNetCDF;

    Date dataInicial;

    Date dataFinal;

    ExtratorDeVariaveis ncr;

    DataSet ds;

    List<Extracao> extracoesEsperadas;

    static final String FIM_DE_LINHA = System.getProperty("line.separator");

    @Before
    public void setUp() throws Exception {
	arquivoDePontos = new File("tests/arquivos_de_entrada/bananeiras-faz.txt");
	variavelDeInteresse = "precipitation_flux_anomaly";
	arquivoDeExtracoes = new File("MIMR_SRB1_pr_2011-2030e.txt");
	arquivoNetCDF = "tests/nc/MIMR_SRB1_1_pr-change_2011-2030.nc";
	dataInicial = formatadorDeDatas.parse("2011-01-16");
	dataFinal = formatadorDeDatas.parse("2030-12-16");
	ds = new DataSet(arquivoNetCDF);

	extracoesEsperadas = new ArrayList<Extracao>();
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-01-16", -2.269915));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-02-16", -2.446853));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-03-16", -2.095874));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-04-16", -0.320214));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-05-16", -0.193006));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-06-16", -0.253279));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-07-16", -0.062567));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-08-16", -0.086748));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-09-16", -0.005866));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-10-16", -0.011575));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-11-16", -0.117851));
	extracoesEsperadas.add(new Extracao(-36.9631, -7.5069, "2020-12-16", -0.906079));

    }

    @Test
    public void testExtracaoMM1() throws ExtratorOpendapException, ParseException {

	ncr = new ExtratorDeVariaveis(ds, arquivoDePontos, dataInicial, dataFinal, variavelDeInteresse, arquivoDeExtracoes);
	ncr.setFatorDeConversao(getFatorDeConversao());
	ncr.extraiValoresDeInteresse();
	System.out.println(ncr.getExtracoes());
	List<Extracao> extracoes = ncr.getListaDeExtracoes();
	for (int i = 0; i < extracoesEsperadas.size(); i++) {
	    Extracao extracao = extracoes.get(i);
	    Extracao extracaoEsperada = extracoesEsperadas.get(i);
	    assertEquals(extracaoEsperada.getCoordenadas().getLatitude(), extracao.getCoordenadas().getLatitude(), TOLERANCIA);
	    assertEquals(extracaoEsperada.getCoordenadas().getLongitude(), extracao.getCoordenadas().getLongitude(), TOLERANCIA);
	    assertEquals(extracaoEsperada.getTime(), extracao.getTime());
	    assertEquals(extracaoEsperada.getValor(), extracao.getValor(), TOLERANCIA);
	}

    }

    @Test
    public void testExtracaoMM1BB() throws ExtratorOpendapException, ParseException {

	ncr = new ExtratorDeVariaveis(ds, variavelDeInteresse);
	Coordenadas coordLeft = new Coordenadas(-10.2, -36.2);
	Coordenadas coordRight = new Coordenadas(-1, -26);
	ncr.extraiPontosDeGrade(coordLeft, coordRight);
	System.out.println(ncr.getExtracoes());

    }

    @Test
    public void testExtracaoMM1BB_2() throws ExtratorOpendapException, ParseException {
	dataFinal = formatadorDeDatas.parse("2020-05-16");
	ncr = new ExtratorDeVariaveis(ds, dataInicial, dataFinal, variavelDeInteresse);
	Coordenadas coordLeft = new Coordenadas(-10.2, -36.2);
	Coordenadas coordRight = new Coordenadas(-1, -26);
	ncr.extraiPontosDeGrade(coordLeft, coordRight);
	System.out.println(ncr.getExtracoes());
    }

    private Double getFatorDeConversao() {
	return 86400.;
    }

    @Test
    public void testExtracaoViaAcessoHTTP() throws ExtratorOpendapException {

	arquivoNetCDF = "http://www.dsc.ufcg.edu.br/~edigley/MIMR_SRB1_1_pr-change_2011-2030.nc";
	ds = new DataSet(arquivoNetCDF);
	ncr = new ExtratorDeVariaveis(ds, arquivoDePontos, dataInicial, dataFinal, variavelDeInteresse, arquivoDeExtracoes);
	ncr.setFatorDeConversao(getFatorDeConversao());
	ncr.extraiValoresDeInteresse();
	List<Extracao> extracoes = ncr.getListaDeExtracoes();
	for (int i = 0; i < extracoesEsperadas.size(); i++) {
	    Extracao extracao = extracoes.get(i);
	    Extracao extracaoEsperada = extracoesEsperadas.get(i);
	    assertEquals(extracaoEsperada.getCoordenadas().getLatitude(), extracao.getCoordenadas().getLatitude(), TOLERANCIA);
	    assertEquals(extracaoEsperada.getCoordenadas().getLongitude(), extracao.getCoordenadas().getLongitude(), TOLERANCIA);
	    assertEquals(extracaoEsperada.getTime(), extracao.getTime());
	    assertEquals(extracaoEsperada.getValor(), extracao.getValor(), TOLERANCIA);
	}
    }

    @Test
    public void testExtracaoOpenDAPThredds() throws ExtratorOpendapException {

	arquivoNetCDF = "dods://localhost:8080/thredds/dodsC/test/MIMR_SRB1_1_pr-change_2011-2030.nc";
	arquivoNetCDF = "dods://150.165.126.97:8080/thredds/dodsC/test/MIMR_SRB1_1_pr-change_2011-2030.nc";
	Scanner sc = new Scanner("-36.96306 -7.50694 Bananeiras-faz");
	DataSet ds = new DataSet(arquivoNetCDF);
	ncr = new ExtratorDeVariaveis(sc, ds);
	ncr.setFatorDeConversao(getFatorDeConversao());
	ncr.extraiValoresDeInteresse();
	List<Extracao> extracoes = ncr.getListaDeExtracoes();
	for (int i = 0; i < extracoesEsperadas.size(); i++) {
	    Extracao extracao = extracoes.get(i);
	    Extracao extracaoEsperada = extracoesEsperadas.get(i);
	    assertEquals(extracaoEsperada.getCoordenadas().getLatitude(), extracao.getCoordenadas().getLatitude(), TOLERANCIA);
	    assertEquals(extracaoEsperada.getCoordenadas().getLongitude(), extracao.getCoordenadas().getLongitude(), TOLERANCIA);
	    assertEquals(extracaoEsperada.getTime(), extracao.getTime());
	    assertEquals(extracaoEsperada.getValor(), extracao.getValor(), TOLERANCIA);
	}
    }

    @Test
    public void testExtracaoOpenDAPGDS() throws ExtratorOpendapException {

	arquivoNetCDF = "dods://150.165.83.184:8080/dods/ufcg/tempo/brams/brams20km/2009-02-20-1200";
	Scanner sc = new Scanner("-36.96306 -7.50694 Bananeiras-faz");
	DataSet ds = new DataSet(arquivoNetCDF);
	variavelDeInteresse = "precip";
	ncr = new ExtratorDeVariaveis(ds, arquivoDePontos, variavelDeInteresse, arquivoDeExtracoes);
	ncr.setFatorDeConversao(getFatorDeConversao());
	System.out.println(ncr.extraiValoresDeInteresse());
    }

    @Test
    public void testExtracaoOpenDAPGDS2() throws ExtratorOpendapException {

	arquivoNetCDF = "dods://150.165.83.184:8080/dods/ufcg/tempo/brams/brams20km/2009-02-20-1200";
	Scanner sc = new Scanner("-36.96306 -7.50694 Bananeiras-faz");
	DataSet ds = new DataSet(arquivoNetCDF);
	variavelDeInteresse = "tempc";
	GeoGrid grid = ds.getGrid(variavelDeInteresse);
	// System.out.println(grid.getShape());
	// System.out.println(grid.getDataType());
	// System.out.println(grid.getUnitsString());
	// System.out.println(grid.getRunTimeDimensionIndex());
	// System.out.println(grid.getRank());
	// System.out.println(grid.getName());
	// System.out.println(grid.getInfo());
	// System.out.println(grid.getDescription());
	// System.out.println(grid.getEnsembleDimensionIndex());
	ncr = new ExtratorDeVariaveis(ds, arquivoDePontos, variavelDeInteresse, arquivoDeExtracoes);
	ncr.setFatorDeConversao(getFatorDeConversao());
	System.out.println(ncr.extraiValoresDeInteresse());
    }

    @Test
    public void testExtracaoOpenDAPUnlimitedDataSet() throws ExtratorOpendapException, ParseException {

	arquivoNetCDF = "http://dods.ipsl.jussieu.fr/cgi-bin/nph-dods/ipcc/sresa2/inmcm3_0/run1/pr_A1.nc";
	dataFinal = formatadorDeDatas.parse("2011-12-16");
	DataSet ds = new DataSet(arquivoNetCDF, dataInicial, dataFinal);
	ncr = new ExtratorDeVariaveis(ds, arquivoDePontos);
	ncr.setFatorDeConversao(getFatorDeConversao());
	System.out.println(ncr.extraiValoresDeInteresse());
    }

    public static void main(String[] args) throws Exception {
	ExtratorDeVariaveisTest et = new ExtratorDeVariaveisTest();
	et.arquivoNetCDF = "http://150.165.126.97:8080/thredds/dodsC/ipcc/anomalias/air_temp_daily_max/NIESMIROC3_2-MED/1PTO4X/oc30a/MIMR_1PTO4X_1_tasmax-change_o0061-0090.nc";
	et.ds = new DataSet(et.arquivoNetCDF);
	System.out.println(et.ds.getDetalhes());
	
    }

}
