package br.edu.ufcg.lsd.seghidro.extratoropendap;

import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeDatas;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.FatorDeConversao;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.Constantes;

public class ExtratorDeVariaveisPontosDeGradeTest {

    private static final double TOLERANCIA = 0.0001;

    String variavelDeInteresse;

    File arquivoDeExtracoes;

    String arquivoNetCDF;

    Date dataInicial;

    Date dataFinal;

    ExtratorDeVariaveisPontosDeGrade ncr;

    DataSet ds;

    List<Extracao> extracoesEsperadas;

    static final String FIM_DE_LINHA = System.getProperty("line.separator");

    @Before
    public void setUp() throws Exception {
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

	Coordenadas coordLeft = new Coordenadas(-10.2, -36.2);
	Coordenadas coordRight = new Coordenadas(-1, -26);
	ncr = new ExtratorDeVariaveisPontosDeGrade(ds, variavelDeInteresse,coordLeft,coordRight);
	ncr.setFatorDeConversao(getFatorDeConversao());
	ncr.extraiValoresDeInteresse();
	System.out.println(ncr.getExtracoes());

    }

    private FatorDeConversao getFatorDeConversao() {
	return new FatorDeConversao(Constantes.SELF +"* 86400","mm/dia");
    }

    
    @Test
    public void testExtracaoMM1_2() throws ExtratorOpendapException, ParseException {
	dataFinal = formatadorDeDatas.parse("2020-05-16");
	Coordenadas coordLeft = new Coordenadas(-10.2, -36.2);
	Coordenadas coordRight = new Coordenadas(-1, -26);
	ncr = new ExtratorDeVariaveisPontosDeGrade(ds, dataInicial, dataFinal, variavelDeInteresse,coordLeft,coordRight);
	ncr.extraiValoresDeInteresse();
	System.out.println(ncr.getExtracoes());
    }

    @Test
    public void testExtracaoOpenDAPGDS_precip() throws ExtratorOpendapException, ParseException {
	ds = new DataSet("dods://150.165.83.184:8080/dods/ufcg/tempo/brams/brams20km/2009-03-03-1200");
	System.out.println(ds.getDetalhes());
	variavelDeInteresse = "precip";
	Coordenadas coordLeft = new Coordenadas(-7.993957436359008, -35.85888671875);
	Coordenadas coordRight = new Coordenadas(-7.140554782450295, -34.60693359375);
	ncr = new ExtratorDeVariaveisPontosDeGrade(ds, variavelDeInteresse,coordLeft,coordRight);
	ncr.extraiValoresDeInteresse();
	System.out.println(ncr.getExtracoes());
    }
    @Test
    public void testExtracaoOpenDAPGDS_tempc() throws ExtratorOpendapException, ParseException {
	ds = new DataSet("dods://150.165.83.184:8080/dods/ufcg/tempo/brams/brams20km/2009-03-03-1200");
	variavelDeInteresse = "tempc";
	Coordenadas coordLeft = new Coordenadas(-7.993957436359008, -35.85888671875);
	Coordenadas coordRight = new Coordenadas(-7.140554782450295, -34.60693359375);
	ncr = new ExtratorDeVariaveisPontosDeGrade(ds, variavelDeInteresse,coordLeft,coordRight);
	ncr.extraiValoresDeInteresse();
	System.out.println(ncr.getExtracoes());
    }

    @Test
    public void testExtracaoOpenDAPGDS() throws ExtratorOpendapException, ParseException {
	ds = new DataSet("dods://150.165.83.184:8080/dods/ufcg/tempo/brams/brams20km/2009-03-03-1200");
	Coordenadas coordLeft = new Coordenadas(-7.993957436359008, -35.85888671875);
	Coordenadas coordRight = new Coordenadas(-7.140554782450295, -34.60693359375);
	ncr = new ExtratorDeVariaveisPontosDeGrade(ds,coordLeft,coordRight);
	ncr.extraiValoresDeInteresse();
	System.out.println(ncr.getExtracoes());
    }
    
    
}
