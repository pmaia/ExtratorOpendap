package br.edu.ufcg.lsd.seghidro.extratoropendap.model;

import java.text.ParseException;

import static br.edu.ufcg.lsd.seghidro.extratoropendap.util.LocaleUtil.*;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * Bean que representa o resultado de uma extração de um dataset. Basicamente,
 * uma extração é formada pelas informações espaciais e temporal e por um valor.
 * 
 * @author edigley
 * 
 */
public class Extracao {

	/**
	 * A localização espacial do extração.
	 */
	private Coordenadas coordenadas;

	/**
	 * O valor referente à extração.
	 */
	private double valor;

	/**
	 * A localização temporal da extração.
	 */
	private String time;

	private FatorDeConversao fatorDeConversao;

	/**
	 * Construtor para uma extração.
	 * 
	 * @param coordenadas
	 *            A localização espacial da
	 * @param valor
	 *            O valor da Extração.
	 */
	public Extracao(Coordenadas coordenadas, double valor) {
		super();
		this.coordenadas = coordenadas;
		this.valor = valor;
		this.time = formatadorDeDatas.format(coordenadas.getTime());
	}

	/**
	 * 
	 * Um construtor de conveniência, para permitir que as informações sejam
	 * passadas de forma primitiva.
	 * 
	 * @param longitude
	 *            A longitude da coordenada.
	 * @param latitude
	 *            A latitude da coordenada.
	 * @param time
	 *            A localização temporal da extração.
	 * @param valor
	 *            O valor da extração.
	 * @throws ParseException
	 *             Caso haja algum problema no time (informado como String)
	 *             passado como parâmetro.
	 */
	public Extracao(double longitude, double latitude, String time, double valor)
			throws ParseException {
		this(
				new Coordenadas(latitude, longitude,
						formatadorDeDatas.parse(time)), valor);
	}

	/**
	 * Um construtor de conveniência, para permitir que as informações sejam
	 * passadas em forma de String.
	 * 
	 * @param longitude
	 *            A longitude da coordenada.
	 * @param latitude
	 *            A latitude da coordenada.
	 * @param time
	 *            A localização temporal da extração.
	 * @param valor
	 *            O valor da extração.
	 * @throws ParseException
	 *             Caso haja algum problema nos parâmetro.
	 */
	public Extracao(String longitude, String latitude, String time, String valor)
			throws NumberFormatException, ParseException {
		this(new Double(longitude), new Double(latitude), time, new Double(
				valor));
	}

	public Extracao(Coordenadas coordenadas, double valor,
			FatorDeConversao fatorDeConversao) {
		this(coordenadas, valor);
		this.fatorDeConversao = fatorDeConversao;
	}

	// métodos getters and setters

	public String getTime() {
		return time;
	}

	public double getValor() {
		return valor;
	}

	public double getValorConvertido() {
		// return fatorDeConversao != null ? valor * fatorDeConversao.getFator()
		// : valor;
		return fatorDeConversao != null ? fatorDeConversao.converte(valor)
				: valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public Coordenadas getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(Coordenadas coordenadas) {
		this.coordenadas = coordenadas;
	}

	/**
	 * Atribui um fator de conversão a ser utilizado na formatação dos valores
	 * extraídos.
	 * 
	 * @param fatorDeConversao
	 *            O fator de conversão.
	 */
	public void setFatorDeConversao(FatorDeConversao fatorDeConversao) {
		this.fatorDeConversao = fatorDeConversao;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("coordenadas", coordenadas).append("valor", valor)
				.append("time", time).toString();
	}

}
