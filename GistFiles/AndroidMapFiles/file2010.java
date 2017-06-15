package com.example.cirugias;

public class cirugias {
private String nombre,nombreC,obraSocial;
private String lugar;
private int dia,mes,a�o,dia1,mes1,a�o1,nroC;
private float cobro;
	public cirugias() {
		// TODO Auto-generated constructor stub
	}
		
	/**
	 * @param args
	 */
     public cirugias(String nombre,String lugar,int dia,int mes,int a�o,int dia1,int mes1,int a�o1,float monto,String nombreC,int nroC,String obraSocial)
     {
    	 this.nombre=nombre;
    	 this.lugar=lugar;
    	 this.dia=dia;
    	 this.mes=mes;
    	 this.a�o=a�o;
    	 this.dia1=dia1;
    	 this.mes1=mes1;
    	 this.a�o1=a�o1;
    	 this.cobro=monto;
    	 this.nombreC=nombreC;
    	 this.nroC=nroC;
    	 this.obraSocial=obraSocial;
     }

	public String getNombreC() {
		return nombreC;
	}

	public void setNombreC(String nombreC) {
		this.nombreC = nombreC;
	}

	public int getNroC() {
		return nroC;
	}

	public void setNroC(int nroC) {
		this.nroC = nroC;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}




	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getObraSocial() {
		return obraSocial;
	}

	public void setObraSocial(String obraSocial) {
		this.obraSocial = obraSocial;
	}

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getA�o() {
		return a�o;
	}

	public void setA�o(int a�o) {
		this.a�o = a�o;
	}

	public int getDia1() {
		return dia1;
	}

	public void setDia1(int dia1) {
		this.dia1 = dia1;
	}

	public int getMes1() {
		return mes1;
	}

	public void setMes1(int mes1) {
		this.mes1 = mes1;
	}

	public int getA�o1() {
		return a�o1;
	}

	public void setA�o1(int a�o1) {
		this.a�o1 = a�o1;
	}

	public float getCobro() {
		return cobro;
	}

	public void setCobro(float cobro) {
		this.cobro = cobro;
	}
      
        
}
