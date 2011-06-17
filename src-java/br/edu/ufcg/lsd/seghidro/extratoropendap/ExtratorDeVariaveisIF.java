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

package br.edu.ufcg.lsd.seghidro.extratoropendap;

import java.util.Date;
import java.util.List;

import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.formatador.FormatadorIF;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Extracao;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.FatorDeConversao;

public interface ExtratorDeVariaveisIF {

    /**
     * Recupera as anomalias extraídas em um formato textual. Dentro do padrão SegHidro, trata-se de um formato PMH-like, com as
     * localizações espacial e temporal dos valores extraídos. Um exemplo do formato de saída é:
     * <pre>
     *            -36.9631             -7.5069       0 2055-01-16 00:00:00         -3.2 -
     *            -36.9631             -7.5069       0 2055-02-16 00:00:00          0.0 -
     *            -36.9631             -7.5069       0 2055-03-16 00:00:00          0.8 -
     *            -36.9631             -7.5069       0 2055-04-16 00:00:00          1.6 -
     *            -36.9631             -7.5069       0 2055-05-16 00:00:00          1.1 -
     *            -36.9631             -7.5069       0 2055-06-16 00:00:00          0.2 -
     *            -36.9631             -7.5069       0 2055-07-16 00:00:00          0.2 -
     *            -36.9631             -7.5069       0 2055-08-16 00:00:00          0.0 -
     *            -36.9631             -7.5069       0 2055-09-16 00:00:00          0.1 -
     *            -36.9631             -7.5069       0 2055-10-16 00:00:00          0.0 -
     *            -36.9631             -7.5069       0 2055-11-16 00:00:00          0.0 -
     *            -36.9631             -7.5069       0 2055-12-16 00:00:00          0.0 -
     * </pre>
     * @return A representação das extrações em um formato textual. 
     */
    public String getExtracoes();

    public void setDataFinal(Date dataFinal);

    public void setDataInicial(Date dataInicial);

    public void setFatorDeConversao(FatorDeConversao fatorDeConversao);

    public boolean salvaArquivoComExtracoes();

    /**
     * Realiza a extração das anomalias a partir do arquivo netCDF informado na criação deste construtor. 
     * Este é o método base para este extrator, devendo ser o primeiro chamado caso o objetivo seja acessar algum valor 
     * referente às anomalias extraídas.
     * @return As anomalias já no formato textual padronizado.
     * @throws ExtratorOpendapException 
     */
    public String extraiValoresDeInteresse() throws ExtratorOpendapException;

    public void setFormatador(FormatadorIF formatador);

    String getUnidadeAposExtracao();
    
    public List<Extracao> getListaDeExtracoes();

}
