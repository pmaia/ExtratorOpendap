package br.edu.ufcg.lsd.seghidro.extratoropendap.interpolador;

import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;

/**
 * 
 * Interface que define um Interpolador, a ser utilizado para estimar um valor
 * ausente em um grid por intermédio de seus valores adjacentes.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 13/07/2008
 */
public interface InterpoladorIF {

    /**
     * Realiza a interpolação dos valores vizinhos, para obter uma aproximação
     * razoável para o valor referente às (<tt>coordenadas<tt>) de interesse
     * para a variável informada em <tt></tt>
     * 
     * @param dataSet O dataset que contém os valores a serem interpolados.
     * @param variavel O nome da variável presente no Dataset da qual o valor será interpolado. 
     * Se este valor for null, recupera da primeira e, possivelmente única, variável presente em <tt>dataSet</tt>.
     * @param coordenadas
     *            As coordenadas que representam a localização espacial e
     *            temporal do valor a ser interpolado no dataset.
     * @return O valor interpolado.
     * @throws ExtratorOpendapException Caso <tt>dataSet</tt> não contenha a variável <tt>variavel</tt> ou haja algum problema durante a interpolação.
     */
    public Double interpola(DataSet dataSet, String variavel, Coordenadas coordenadas) throws ExtratorOpendapException;

}
