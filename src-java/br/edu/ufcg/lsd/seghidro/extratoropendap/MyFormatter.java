package br.edu.ufcg.lsd.seghidro.extratoropendap;

import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.Constantes.FIM_DE_LINHA;
import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeDatas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.formatador.FormatadorAbstract;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;

/**
 * Classe que define o formatador utilizado para criar o arquivo PMH.
 * 
 * @author Sávio Canuto de Oliveira Sousa.
 * @since 17/04/2009.
 */
public class MyFormatter extends FormatadorAbstract {

	public void adicionaExtracao(Coordenadas coordenadas, double valor) {

		String time = "00:00:00";
		String altitude = "1000";
		String dataFormatada = formatadorDeDatas.format(coordenadas.getTime());
		String hifen = "-";

		String precipitacao = String.valueOf(valor);
		if (precipitacao.startsWith("-")) {
			precipitacao = precipitacao.substring(1);
		}

		if (precipitacao.contains("."))
			precipitacao = precipitacao.substring(0,
					precipitacao.indexOf(".") + 2);
		valor = Double.valueOf(precipitacao);

		formatter.format("%19.4f %19.4f    %S %S %S %12.1f            %S"
				+ FIM_DE_LINHA, coordenadas.getLongitude(),
				coordenadas.getLatitude(), altitude, dataFormatada, time,
				valor, hifen);
	}

}
