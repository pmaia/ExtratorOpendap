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
import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.formatadorDeNumeros;

import java.util.Formatter;
import java.util.Locale;

import org.apache.log4j.Logger;

import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;

public class FormatadorPMHLike extends FormatadorAbstract {

    private static Logger logger = Logger.getLogger(FormatadorPMHLike.class);

	public FormatadorPMHLike(StringBuffer sb) {
		super(sb);
	}

	public void adicionaExtracao(Coordenadas coordenadas, double valor) {
		String dataFormatada = formatadorDeDatas.format(coordenadas.getTime());
		formatter.format("%19.4f %19.4f       %S %18.12f" + FIM_DE_LINHA,
				coordenadas.getLongitude(), coordenadas.getLatitude(),
				dataFormatada, valor);
	}

}
