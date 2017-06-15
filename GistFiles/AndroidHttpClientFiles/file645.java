package com.ortiz.sangredeportiva;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GoleadoresAdapter extends ArrayAdapter<Goleador>{
	private int resource;
		private LayoutInflater inflater;
		
		public GoleadoresAdapter(Context context,int resource, List<Goleador> objects) {
			super(context, resource, objects);
			this.resource = resource;
			this.inflater = LayoutInflater.from(context);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = (RelativeLayout) inflater.inflate(resource, null);
			Goleador g = getItem(position);
			TextView _nombre = (TextView) convertView.findViewById(R.id.tabla_nombre_goleador);
			_nombre.setText(g.getNombre());
			TextView _goles = (TextView) convertView.findViewById(R.id.tabla_goles);
			_goles.setText(g.getGoles());
			
			

			return convertView;
		}
		
}
