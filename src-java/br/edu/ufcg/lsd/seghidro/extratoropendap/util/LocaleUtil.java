package br.edu.ufcg.lsd.seghidro.extratoropendap.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Concentra as informações referentes ao Locale utilizado pelo extrator. Para
 * alterar o formato de data ou de números decimais (se separados por vírgula ou
 * ponto), deve-se alterar os formatos presentes nessa interface.
 * 
 * @author edigley
 *
 */
public interface LocaleUtil {

	public static final Locale brasil = new Locale("pt", "BR");

	public static final String formatoParaData = "yyyy-MM-dd";

	public static final SimpleDateFormat formatadorDeDatas = new SimpleDateFormat(formatoParaData, brasil);

	public static DecimalFormat formatadorDeNumeros = new DecimalFormat();

}
