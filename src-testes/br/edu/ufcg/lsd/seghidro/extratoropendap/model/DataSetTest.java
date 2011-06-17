package br.edu.ufcg.lsd.seghidro.extratoropendap.model;

import junit.framework.TestCase;
import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;

public class DataSetTest extends TestCase {

    String arquivoDePontos;
    String variavelDeInteresse;
    String arquivoDeAnomalias;
    String arquivoNetCDF;
    String dataInicial;
    String dataFinal;
    DataSet ds;
    static final String FIM_DE_LINHA = System.getProperty("line.separator");

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	arquivoDePontos = "tests/arquivos_de_entrada/postoBoqueirao.txt";
	variavelDeInteresse = "precipitation_flux_anomaly";
	arquivoDeAnomalias = "MIMR_SRB1_pr_2011-2030e.txt";
	arquivoNetCDF = "tests/nc/MIMR_SRB1_1_pr-change_2011-2030.nc";
	dataInicial = "2011-01-16";
	dataFinal = "2030-12-16";
	ds = new DataSet(arquivoNetCDF);
    }

    public void testMM1() {

	try {
	    ds = new DataSet(arquivoNetCDF);
	    assertEquals("MIMR", ds.getNomeModeloOrigem());
	    assertEquals("SRB1", ds.getNomeCenarioOrigem());
	    assertEquals("kg m-2 s-1", ds.getUnidade());
	    assertEquals("2011", ds.getInicioPeriodo());
	    assertEquals("2030", ds.getFimPeriodo());
	    assertEquals("[precipitation_flux_anomaly]", ds.getVariaveis().toString());
	} catch (ExtratorOpendapException e) {
	    e.printStackTrace();
	    fail();
	}
    }

    public void testAcessoHTTP() {

	arquivoNetCDF = "http://www.dsc.ufcg.edu.br/~edigley/MIMR_SRB1_1_pr-change_2011-2030.nc";

	try {
	    ds = new DataSet(arquivoNetCDF);
	    assertEquals("MIMR", ds.getNomeModeloOrigem());
	    assertEquals("SRB1", ds.getNomeCenarioOrigem());
	    assertEquals("kg m-2 s-1", ds.getUnidade());
	    assertEquals("2011", ds.getInicioPeriodo());
	    assertEquals("2030", ds.getFimPeriodo());
	    assertEquals("[precipitation_flux_anomaly]", ds.getVariaveis().toString());
	} catch (ExtratorOpendapException e) {
	    e.printStackTrace();
	    fail();
	}
    }

    public void testAcessoOpenDAP() {

	arquivoNetCDF = "dods://150.165.126.97:8080/thredds/dodsC/ipcc/anomalias/anomalies_precipitation_flux/BCCRBCM2/SRB1/BCM2_SRB1_1_pr-change_2080-2099.nc";
	arquivoNetCDF = "dods://150.165.126.97:8080/thredds/dodsC/test/MIMR_SRB1_1_pr-change_2011-2030.nc";
	try {
	    ds = new DataSet(arquivoNetCDF);
	    assertEquals("MIMR", ds.getNomeModeloOrigem());
	    assertEquals("SRB1", ds.getNomeCenarioOrigem());
	    assertEquals("kg m-2 s-1", ds.getUnidade());
	    assertEquals("2011", ds.getInicioPeriodo());
	    assertEquals("2030", ds.getFimPeriodo());
	    assertEquals("[precipitation_flux_anomaly]", ds.getVariaveis().toString());

	    // assertEquals("BCM2",ds.getNomeModeloOrigem());
	    // assertEquals("SRB1",ds.getNomeCenarioOrigem());
	    // assertEquals("kg m-2 s-1" , ds.getUnidade());
	    // assertEquals("2080" , ds.getInicioPeriodo());
	    // assertEquals("2099" , ds.getFimPeriodo());
	    // assertEquals("[precipitation_flux_anomaly]" ,
	    // ds.getVariaveis().toString());
	} catch (ExtratorOpendapException e) {
	    e.printStackTrace();
	    fail();
	}
    }

}
