package br.edu.ufcg.lsd.seghidro.extratoropendap;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import br.edu.ufcg.lsd.seghidro.extratoropendap.ExtratorDeVariaveisInterpolado;
import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;

/**
 * Esta classe extrai dados do OpenDap e cria um arquivo de pmh formatado.
 * 
 * @author Melina Mongiovi
 * 
 */
public class ExtratorNoFormatoPMH {

	/**
	 * Path do arquivo de pontos
	 */
	String arquivoPontos;

	/**
	 * Path do arquivo NetCDF
	 */
	String arquivoNetCDf;

	/**
	 * Path do arquivo Pmh
	 */
	String arquivoPmh;

	/**
	 * Conteúdo do arquivo Pmh
	 */
	String dadosPmh;

	/** Variável responsável por finalizar uma linha no arquivo de saída. */
	private static final String FIM_DE_LINHA = System
			.getProperty("line.separator");

	/**
	 * Construtor
	 * 
	 * @param solosFile
	 * @param arquivoPontos
	 * @throws ParseException
	 * @throws ExtratorOpendapException
	 * @throws IOException
	 */
	public ExtratorNoFormatoPMH(String arquivoNetCDF, String arquivoPontos,
			String arquivoPmh) throws IOException, ExtratorOpendapException,
			ParseException {
		this.arquivoPontos = arquivoPontos;
		this.arquivoNetCDf = arquivoNetCDF;
		this.arquivoPmh = arquivoPmh;
		this.setUp();
	}

	/**
	 * 
	 * @throws IOException
	 * @throws ExtratorOpendapException
	 * @throws ParseException
	 */
	private void setUp() throws IOException, ExtratorOpendapException,
			ParseException {

		this.buscarDadosOpenDap();
		this.criarArquivoPMH();

	}

	/**
	 * Este método é responsável por extrair dados do OpenDap
	 * 
	 * @throws ExtratorOpendapException
	 * @throws ParseException
	 */
	private void buscarDadosOpenDap() throws ExtratorOpendapException,
			ParseException {

		DataSet ds = new DataSet(this.arquivoNetCDf);
		File file = new File(this.arquivoPontos);

		ExtratorDeVariaveisInterpolado edv = new ExtratorDeVariaveisInterpolado(
				ds, file);

		MyFormatter myFormatter = new MyFormatter();
		edv.setFormatador(myFormatter);

		File arquivoDePontos = new File(this.arquivoPontos);

		this.dadosPmh = edv.extraiValoresDeInteresse();
		this.dadosPmh = this.dadosPmh.substring(0,
				this.dadosPmh.indexOf(FIM_DE_LINHA, dadosPmh.length() - 2));

	}

	/**
	 * Este método cria o arquivo de pmg a partir do conteúdo já formatado
	 */
	public void criarArquivoPMH() {

		MontaArquivos montaArquivos = new MontaArquivos(this.arquivoPmh);
		montaArquivos.writeLine(this.dadosPmh);
		montaArquivos.closeFile();
	}

	// public static void main(String[] args) throws IOException,
	// ExtratorOpendapException, ParseException {
	//
	// ExtratorNoFormatoPMH efp = new
	// ExtratorNoFormatoPMH("tests/MIMR_SRB1_1_pr-change_2011-2030.nc",
	// "tests/MogeiroPoints.txt", "tests/PMH_RESULT.pmh");
	//
	// }

}