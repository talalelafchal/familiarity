package com.ortiz.sangredeportiva;

public class Goleador {

	
	@Override
	public String toString() {
		return "Goleador [nombre=" + nombre + ", goles=" + goles + "]";
	}

	private String nombre, goles;

	public Goleador(String nombre, String goles) {
		super();
		this.nombre = nombre;
		this.goles = goles;
	}

	
	public String getNombre() {
		return nombre;
	}

	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	
	public String getGoles() {
		return goles;
	}

	
	public void setGoles(String goles) {
		this.goles = goles;
	}
	
	

}
