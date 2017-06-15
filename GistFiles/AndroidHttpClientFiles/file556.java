package com.ortiz.sangredeportiva;

public class Partido {

	private String fecha, hora, nombre, equipo_local, equipo_visitante, score_local, score_visitante, jugado;

	public Partido(String fecha, String hora, String nombre,
			String equipo_local, String equipo_visitante, String score_local,
			String score_visitante, String jugado) {
		super();
		this.fecha = fecha;
		this.hora = hora;
		this.nombre = nombre;
		this.equipo_local = equipo_local;
		this.equipo_visitante = equipo_visitante;
		this.score_local = score_local;
		this.score_visitante = score_visitante;
		this.jugado = jugado;
	}
	

	public Partido(String equipo_local, String equipo_visitante) {
		super();
		this.equipo_local = equipo_local;
		this.equipo_visitante = equipo_visitante;
	}


	
	public String getFecha() {
		return fecha;
	}

	
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	
	public String getHora() {
		return hora;
	}

	
	public void setHora(String hora) {
		this.hora = hora;
	}

	
	public String getNombre() {
		return nombre;
	}

	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	
	public String getEquipo_local() {
		return equipo_local;
	}

	
	public void setEquipo_local(String equipo_local) {
		this.equipo_local = equipo_local;
	}

	
	public String getEquipo_visitante() {
		return equipo_visitante;
	}

	
	public void setEquipo_visitante(String equipo_visitante) {
		this.equipo_visitante = equipo_visitante;
	}

	
	public String getScore_local() {
		return score_local;
	}

	
	public void setScore_local(String score_local) {
		this.score_local = score_local;
	}

	
	public String getScore_visitante() {
		return score_visitante;
	}

	
	public void setScore_visitante(String score_visitante) {
		this.score_visitante = score_visitante;
	}

	
	public String getJugado() {
		return jugado;
	}

	
	public void setJugado(String jugado) {
		this.jugado = jugado;
	}

	

	
	
	
}
