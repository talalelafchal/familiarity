package com.example.wisatajogja;

public class E_Lokasi
{
	private String	nama;
	private String	alamat;
	private String	gambar;
	private Double	lat;
	private Double	lng;

	public String getGambar()
	{
		return gambar;
	}

	public void setGambar(String gambar)
	{
		this.gambar = gambar;
	}

	public String getNama()
	{
		return nama;
	}

	public void setNama(String nama)
	{
		this.nama = nama;
	}

	public String getAlamat()
	{
		return alamat;
	}

	public void setAlamat(String alamat)
	{
		this.alamat = alamat;
	}

	public Double getLat()
	{
		return lat;
	}

	public void setLat(Double lat)
	{
		this.lat = lat;
	}

	public Double getLng()
	{
		return lng;
	}

	public void setLng(Double lng)
	{
		this.lng = lng;
	}

}
