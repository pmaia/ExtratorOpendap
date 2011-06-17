/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package br.edu.ufcg.lsd.seghidro.extratoropendap.ui;

import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeDatas;
import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatoParaData;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import br.edu.ufcg.lsd.seghidro.extratoropendap.ExtratorDeVariaveisIF;
import br.edu.ufcg.lsd.seghidro.extratoropendap.ExtratorDeVariaveisInterpolado;
import br.edu.ufcg.lsd.seghidro.extratoropendap.ExtratorNoFormatoPMH;
import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.util.MainUtil;

/**
 * 
 * <pre>
 *  usage: java -jar extrator-opendap.jar [-d &lt;arg&gt;] [-f &lt;arg&gt;] [-help] [-i
 *  &lt;arg&gt;] [-o &lt;arg&gt;] [-p &lt;arg&gt;] [-usage]
 *  -d,--dataset &lt;arg&gt;   Caminho para o dataset de origem.
 *  -f,--fim &lt;arg&gt;       Data final da extração.
 *  -help                      Comando de ajuda.
 *  -i,--inicio &lt;arg&gt;    Data inicial da extração.
 *  -o,--output &lt;arg&gt;    Nome do arquivo de saída.
 *  -p,--pontos &lt;arg&gt;    Caminho para o arquivo de pontos.
 *  -usage                     Instruções de uso.
 * </pre>
 * 
 * Exemplo:
 * 
 * <pre>
 *   java -jar extrator-opendap.jar -d &quot;dods://150.165.126.97:8080/thredds/dodsC/test/MIMR_SRB1_1_pr-change_2011-2030.nc&quot; -p ../../resources/arquivos_de_entrada/postoBoqueirao.txt -i &quot;2020-05-16&quot; -f &quot;2020-08-16&quot; -o output.txt
 * </pre>
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 12/02/2009
 * 
 */
public class CLI {

	private static final String DATASET = "d";

	private static final String PONTOS = "p";

	private static final String OUTPUT = "o";

	private static final String INICIO = "i";

	private static final String FIM = "f";

	private static final String HELP = "help";

	private static final String USAGE = "usage";

	private static final String EXECUTION_LINE = "java -jar extrator-opendap.jar";

	/**
	 * Exemplo:
	 * 
	 * <pre>
	 *   java -jar extrator-opendap.jar -d &quot;dods://150.165.126.97:8080/thredds/dodsC/test/MIMR_SRB1_1_pr-change_2011-2030.nc&quot; -p ../../resources/arquivos_de_entrada/postoBoqueirao.txt -i &quot;2020-05-16&quot; -f &quot;2020-08-16&quot; -o output.txt
	 * </pre>
	 * 
	 * @param args
	 * @throws java.text.ParseException
	 * @throws ExtratorOpendapException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException,
			ExtratorOpendapException, java.text.ParseException {
		// System.out.println(args[0]+" "+ args[1] +" "+ args[2]+" "+ args[3]
		// +" "+ args[4]);
		// System.out.println("===========================hsdklhfsllll+======");
		// if (args[3].equals("forMarbs")) {
		// ExtratorNoFormatoPMH extrator = new
		// ExtratorNoFormatoPMH(args[4],args[5],args[6]);
		if (args[0].equals("forMarbs")) {
			ExtratorNoFormatoPMH extrator = new ExtratorNoFormatoPMH(args[1],
					args[2], args[3]);
		} else {
			Options options = new Options();

			options.addOption(PONTOS, "pontos", true,
					"Caminho para o arquivo de pontos.");
			options.addOption(DATASET, "dataset", true,
					"Caminho para o dataset de origem.");
			options.addOption(OUTPUT, "output", true,
					"Nome do arquivo de saída.");
			options.addOption(INICIO, "inicio", true,
					"Data inicial da extração.");
			options.addOption(FIM, "fim", true, "Data final da extração.");
			options.addOption(HELP, false, "Comando de ajuda.");
			options.addOption(USAGE, false, "Instruções de uso.");
			CommandLineParser parser = new PosixParser();
			HelpFormatter formatter = new HelpFormatter();
			CommandLine cmd = null;

			try {
				cmd = parser.parse(options, args);
			} catch (ParseException e) {
				MainUtil.showMessageAndExit(e);
			}

			if (cmd.hasOption(PONTOS) && cmd.hasOption(DATASET)
					&& cmd.hasOption(OUTPUT)) {
				String arquivoNetCDF = cmd.getOptionValue(DATASET);
				String arquivoDePontos = cmd.getOptionValue(PONTOS);
				String outputFileName = cmd.getOptionValue(OUTPUT);
				DataSet dataset;
				try {
					dataset = new DataSet(arquivoNetCDF);
					ExtratorDeVariaveisIF extrator;
					if (cmd.hasOption(INICIO) && cmd.hasOption(FIM)) {
						Date dataInicial = null;
						Date dataFinal = null;
						try {
							dataInicial = formatadorDeDatas.parse(cmd
									.getOptionValue(INICIO));
						} catch (java.text.ParseException e) {
							MainUtil.showMessageAndExit("A data inicial '"
									+ cmd.getOptionValue(INICIO)
									+ "' não está em um formato reconhecido. Informe no formato "
									+ formatoParaData + ". Por Exemplo: "
									+ formatadorDeDatas.format(new Date())
									+ ".");
						}
						try {
							dataFinal = formatadorDeDatas.parse(cmd
									.getOptionValue(FIM));
						} catch (java.text.ParseException e) {
							MainUtil.showMessageAndExit("A data final '"
									+ cmd.getOptionValue(FIM)
									+ "' não está em um formato reconhecido. Informe no formato "
									+ formatoParaData + ". Por Exemplo: "
									+ formatadorDeDatas.format(new Date())
									+ ".");
						}
						extrator = new ExtratorDeVariaveisInterpolado(dataset,
								new File(arquivoDePontos), dataInicial,
								dataFinal, new File(outputFileName));
					} else {
						extrator = new ExtratorDeVariaveisInterpolado(dataset,
								new File(arquivoDePontos), new File(
										outputFileName));
					}
					String extracoes = extrator.extraiValoresDeInteresse();
					System.out.println(extracoes);
					extrator.salvaArquivoComExtracoes();
				} catch (ExtratorOpendapException e) {
					MainUtil.showMessageAndExit(e);
				}
			} else {
				if (cmd.hasOption(HELP)) {
					formatter.printHelp(EXECUTION_LINE, options);
				} else if (cmd.hasOption(USAGE)) {
					formatter.printHelp(EXECUTION_LINE, options, true);
				} else {
					formatter.printHelp(EXECUTION_LINE, options, true);
				}
			}

		}
	}
}
