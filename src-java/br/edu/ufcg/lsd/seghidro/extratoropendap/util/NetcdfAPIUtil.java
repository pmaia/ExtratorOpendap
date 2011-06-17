package br.edu.ufcg.lsd.seghidro.extratoropendap.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

import thredds.datatype.DateType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Variable;
import ucar.nc2.VariableIF;
import ucar.nc2.dt.grid.GeoGrid;

/**
 * 
 * Contém métodos utilitários para acesso à API netcdf utilizada na aplicação.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 14/02/2009
 * 
 */
public class NetcdfAPIUtil {

    private static Logger logger = Logger.getLogger(NetcdfAPIUtil.class);

    /**
     * Recupera um único valor de uma variável e o retorna como Float.
     * 
     * @param variable
     *                A variável a partir da qual o valor será lido.
     * @param origem
     *                As coordenadas que identificam o valor dentro da varíavel.
     * @return O valor único.
     */
    public static Float readSingleValueAsFloat(VariableIF variable, int[] origem) {
	return readAsFloat(variable, origem, new int[] { 1 });
    }

    /**
     * Recupera um único valor de uma variável e o retorna como Double.
     * 
     * @param variable
     *                A variável a partir da qual o valor será lido.
     * @param origem
     *                As coordenadas que identificam o valor dentro da varíavel.
     * @return O valor único.
     */
    public static Double readSingleValueAsDouble(VariableIF variable, int[] origem) {
	return readAsDouble(variable, origem, new int[] { 1 });
    }

    /**
     * Recupera um único valor de uma variável.
     * 
     * @param variable
     *                A variável a partir da qual o valor será lido.
     * @param origem
     *                As coordenadas que identificam o valor dentro da varíavel.
     * @return O valor único.
     */
    public static Object readSingleValue(VariableIF variable, int[] origem) {
	return readValue(variable, origem, new int[] { 1 });
    }

    /**
     * 
     * Recupera um valor double específico de uma variável de um dataset.
     * 
     * @param variable
     *                A variável a partir da qual o valor será lido.
     * @param origem
     *                As coordenadas que identificam o valor dentro da varíavel.
     * @param shape
     *                A forma do valor a ser lido.
     * @return O valor.
     */
    public static Object readValue(VariableIF variable, int[] origem, int[] shape) {
	try {
	    logger.debug("Origem: " + Arrays.toString(origem) + " " + "Shape: " + Arrays.toString(shape));
	    Object valor = variable.read(origem, shape).getIndexIterator().next();
	    logger.debug("Valor: " + valor);
	    return valor;
	} catch (IOException e) {
	    logger.error(e.getMessage(), e);
	} catch (InvalidRangeException e) {
	    logger.error(e.getMessage(), e);
	}
	throw new RuntimeException("Não conseguiu ler valores do DataSet.");
    }

    /**
     * 
     * Recupera um valor Float específico de uma variável de um dataset.
     * 
     * @param variable
     *                A variável a partir da qual o valor será lido.
     * @param origem
     *                As coordenadas que identificam o valor dentro da varíavel.
     * @param shape
     *                A forma do valor a ser lido.
     * @return O valor.
     */
    public static Float readAsFloat(VariableIF variable, int[] origem, int[] shape) {
	Object readValue = readValue(variable, origem, shape);

	if (readValue instanceof java.lang.Double) {
	    return new Float((Double) readValue);
	}

	return (Float) readValue;
    }

    /**
     * 
     * Recupera um valor double específico de uma variável de um dataset.
     * 
     * @param variable
     *                A variável a partir da qual o valor será lido.
     * @param origem
     *                As coordenadas que identificam o valor dentro da varíavel.
     * @param shape
     *                A forma do valor a ser lido.
     * @return O valor.
     */
    public static Double readAsDouble(VariableIF variable, int[] origem, int[] shape) {
	Object readValue = readValue(variable, origem, shape);
	if (readValue instanceof java.lang.Float) {
	    return new Double((Float) readValue);
	}
	return (Double) readValue;
    }

    public static Date readTime(Variable timeVariable, int time) {
	Object dateRelative = NetcdfAPIUtil.readSingleValue(timeVariable, new int[] { time });
	DateType data;
	try {
	    data = new DateType(dateRelative + " " + timeVariable.getUnitsString(), null, null);
	    Date d = data.getDate();
	    d.setDate(d.getDate() + 1);
	    data.setDate(d);
	    return d;
	} catch (ParseException e) {
	    logger.error("Problema na definição da Coordenada de tempo.", e);
	    return null;
	}
    }

    public static int[] getUniqueShapeForGrid(GeoGrid grid) {
	int[] shape = new int[grid.getRank()];
	for (int i = 0; i < shape.length; i++) {
	    shape[i] = 1;
	}
	return shape;
    }

}
