package br.edu.ufcg.lsd.seghidro.extratoropendap;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Classe que monta o arquivo JDF
 * 
 * @author Rodrigo Bruno L. de Sousa
 * 
 */
public class MontaArquivos {

	/**
	 * Variável responsável por finalizar uma linha no arquivo de saída.
	 */
	private static final String FIM_DE_LINHA = System
			.getProperty("line.separator");

	private String fileOut;

	private OutputManager outputManager;

	/**
	 * Construtor
	 * 
	 * @param fileOut
	 *            path do arquivo
	 * @param outputManager
	 */
	public MontaArquivos(String fileOut, OutputManager outputManager) {
		this.fileOut = fileOut;
		this.outputManager = outputManager;
	}

	/**
	 * Construtor da classe
	 * 
	 * @param fileOut
	 *            path do arquivo
	 */
	public MontaArquivos(String fileOut) {
		this.fileOut = fileOut;
		outputManager = new OutputManagerASCII(fileOut);
		try {
			outputManager.createFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Escreve um conjunto de informações no arquivo.
	 * 
	 * @param line
	 */
	public void writeLine(String line) {
		try {
			outputManager.writeLine(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fecha o arquivo criado.
	 */
	public void closeFile() {
		try {
			outputManager.closeFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the fileOut
	 */
	public String getFileOut() {
		return fileOut;
	}

	/**
	 * @param fileOut
	 *            the fileOut to set
	 */
	public void setFileOut(String fileOut) {
		this.fileOut = fileOut;
	}

}
