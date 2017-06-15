package com.ortiz.sangredeportiva;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PartidosAdapter extends ArrayAdapter <Partido>{

	private LayoutInflater inflater;
	
	public PartidosAdapter(Context context, List<Partido> objects) {
		super(context, 0, objects);
		
		this.inflater = LayoutInflater.from(context);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = (RelativeLayout) inflater.inflate(R.layout.tabla_fixt, null);
		Partido p = getItem(position);

		TextView _numfecha = (TextView) convertView.findViewById(R.id.tv_fixture);
		_numfecha.setText(p.getNombre());
		
		TextView _equipo_l = (TextView) convertView.findViewById(R.id.equipo_l);
		_equipo_l.setText(p.getEquipo_local());
		
		TextView _score_l = (TextView) convertView.findViewById(R.id.score_l);
		_score_l.setText(p.getScore_local());
		
		TextView _date = (TextView) convertView.findViewById(R.id.fecha_date);
		_date.setText(p.getFecha());
		
		TextView _jugado = (TextView) convertView.findViewById(R.id.jugado);
		_jugado.setText(p.getJugado());
		
		TextView _equipo_v = (TextView) convertView.findViewById(R.id.equipo_v);
		_equipo_v.setText(p.getEquipo_visitante());
		
		TextView _score_v = (TextView) convertView.findViewById(R.id.score_v);
		_score_v.setText(p.getScore_visitante());

		return convertView;
	}
	
}
