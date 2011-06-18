package br.edu.ufcg.lsd.seghidro.extratoropendap.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * Representa as coordenadas espaciais e temporais que permitem localizar um
 * dado na superfície da terra no tempo.
 * 
 * @author edigley
 * 
 */
public class Coordenadas {

	/**
	 * O valor referente à latitude da coordenada.
	 */
	private double latitude;

	/**
	 * O valor referente à longitude da coordenada.
	 */
	private double longitude;

	/**
	 * O valor referente à altitude da coordenada.
	 */
	private double nivelPressaoAtmosferica;

	/**
	 * A data que permite localizar temporalmente a coordenada.
	 */
	private Date time;

	/**
	 * Construtor para uma coordenada que desconsidera localização temporal.
	 * 
	 * @param latitude
	 *            A latitude da coordenada.
	 * @param longitude
	 *            A longitude da coordenada.
	 */
	public Coordenadas(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = null;
	}
	
	/**
	 * Construtor para uma coordenada que desconsidera localização temporal.
	 * 
	 * @param latitude
	 * 		A latitude da coordenada
	 * @param longitude
	 * 		A longitude da coordenada
	 * @param nivelPressaoAtmosferica
	 * 		Nível de pressão atmosférica da coordenada. Representa uma altitude.
	 * 		
	 */
	public Coordenadas(double latitude, double longitude, double nivelPressaoAtmosferica) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.nivelPressaoAtmosferica = nivelPressaoAtmosferica;
	}

	/**
	 * Construtor para uma coordenadas que considera tanto as informações
	 * temporais quanto as espaciais.
	 * 
	 * @param latitude
	 *            A latitude da coordenada.
	 * @param longitude
	 *            A longitude da coordenada.
	 * @param time
	 *            A data que permite localizar temporalmente a coordenada.
	 */
	public Coordenadas(double latitude, double longitude, Date time) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
	}

	/**
	 * Construtor para uma coordenadas que considera tanto as informações
	 * temporais quanto as espaciais.
	 * 
	 * @param latitude
	 *            A latitude da coordenada.
	 * @param longitude
	 *            A longitude da coordenada.
	 * @param altitude
	 *            A altitude da coordenada.
	 * @param time
	 *            A data que permite localizar temporalmente a coordenada.
	 */
	public Coordenadas(double latitude, double longitude, double altitude,
			Date time) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.nivelPressaoAtmosferica = altitude;
		this.time = time;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getNivelPressaoAtmosferica() {
		return nivelPressaoAtmosferica;
	}

	public void setNivelPressaoAtmosferica(double nivelPressaoAtmosferica) {
		this.nivelPressaoAtmosferica = nivelPressaoAtmosferica;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("latitude", latitude).append("longitude", longitude)
				.append("nivel de pressao atmosferica", nivelPressaoAtmosferica).append("time", time).toString();
	}

}
