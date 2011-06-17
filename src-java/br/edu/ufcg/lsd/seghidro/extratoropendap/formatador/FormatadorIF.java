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

import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;

public interface FormatadorIF {

    /**
     * Realiza a adição dos valores extraídos. O objetivo deste método é
     * formatar a saída dos resultados extraídos.
     * 
     * @param longitude
     *                A longitude da localidade para a qual foi extraído o valor
     *                para a variável de interesse.
     * @param latitude
     *                A latitude da localidade para a qual foi extraído o valor
     *                para a variável de interesse.
     * @param timeDate
     *                A data representa a localização temporal do valor de
     *                interesse.
     * @param valor
     *                O valor extraído.
     */
    public void adicionaExtracao(Coordenadas coordenadas, double valor);

    public void adicionaExtracao(Extracao extracao);
    
    public void setStringBuffer(StringBuffer sb);

}
