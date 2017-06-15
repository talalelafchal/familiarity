package com.ortiz.sangredeportiva;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PrincipalAdapter extends ArrayAdapter <Partido>{
	/*
	un adaptador conecta los datos con la lista definida en el layout, en este caso los datos vienen de la clase principal,desde
	la cual seteo a traves de una lista un adapter, pasandole los valores. en esta clase los recupero y los asigno a los diferentes
	textview que estan definidos en el layput tablaprincipal
	*/
	
	private LayoutInflater inflater;
	
	public PrincipalAdapter(Context context, List<Partido> objects) {
		super(context,0, objects);
		
		this.inflater = LayoutInflater.from(context);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//especifico en que layout quiero poner estos valores
		convertView = (RelativeLayout) inflater.inflate(R.layout.tabla_principal, null);
		Partido p = getItem(position);
		
		//en el textview con id:local_principal pongo el equipo que recupere de la clase principal 
		TextView _equipo_l = (TextView) convertView.findViewById(R.id.local_principal);
		_equipo_l.setText(p.getEquipo_local());
		
		TextView _equipo_v = (TextView) convertView.findViewById(R.id.visitante_principal);
		_equipo_v.setText(p.getEquipo_visitante());
		return convertView;
	}
	

}
