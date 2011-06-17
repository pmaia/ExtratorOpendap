package br.edu.ufcg.lsd.seghidro.extratoropendap.model;

/**
 * 
 * Representa uma localidade na superfície da terra. Em essência, um posto é
 * formada por sua localização espacial e temporal e por um nome que o
 * identifica (não unicamente).
 * 
 * @author edigley
 * 
 */
public class Posto {

	/**
	 * A localização espacial do posto.
	 */
	private Coordenadas coordenadas;

	/**
	 * O nome do posto.
	 */
	private String nome;

	/**
	 * Construtor para um posto.
	 * 
	 * @param coordenadas
	 *            A localização espacial do posto.
	 * @param nome
	 *            O nome do posto.
	 */
	public Posto(Coordenadas coordenadas, String nome) {
		super();
		this.coordenadas = coordenadas;
		this.nome = nome;
	}

	// métodos getters and setters.

	public Coordenadas getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(Coordenadas coordenadas) {
		this.coordenadas = coordenadas;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
