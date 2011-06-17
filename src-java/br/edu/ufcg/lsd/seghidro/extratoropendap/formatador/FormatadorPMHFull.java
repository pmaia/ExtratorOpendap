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

package br.edu.ufcg.lsd.seghidro.extratoropendap.formatador;

import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.Constantes.FIM_DE_LINHA;
import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeDatas;

import java.util.Formatter;

import org.apache.log4j.Logger;

import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;

/**
 * Classe responsável por montar o arquivo com o formato de PMH.
 * 
 * @author Sávio Canuto de Oliveira Sousa.
 * @since 31/03/2009.
 */
public class FormatadorPMHFull implements FormatadorIF {

	private static Logger logger = Logger.getLogger(FormatadorIF.class);

	private Formatter formatter;
	
	@Override
	public void adicionaExtracao(Coordenadas coordenadas, double valor) {
		
		String dataFormatada = formatadorDeDatas.format(coordenadas.getTime());
		formatter.format("%19.4f %19.4f       %S %18.12f" + FIM_DE_LINHA,
				coordenadas.getLongitude(), coordenadas.getLatitude(),
				dataFormatada, valor);
	}

	@Override
	public void adicionaExtracao(Extracao extracao) {
		
		logger.debug("Adicionando a extração: " + extracao);
		adicionaExtracao(extracao.getCoordenadas(), extracao
				.getValorConvertido());
	}

	@Override
	public void setStringBuffer(StringBuffer sb) {
		// TODO Auto-generated method stub
		
	}

}
