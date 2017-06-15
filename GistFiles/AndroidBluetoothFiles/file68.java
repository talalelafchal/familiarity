package com.ortiz.sangredeportiva;


import java.util.List;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PosicionesAdapter extends ArrayAdapter<Positions>{
	private int resource;
	private LayoutInflater inflater;
	
	public PosicionesAdapter(Context context, int resource, List<Positions> objects) {
		super(context, resource, objects);
		
		this.resource = resource;
		this.inflater = LayoutInflater.from(context);
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = (RelativeLayout) inflater.inflate(resource, null);
		Positions team = getItem(position);

		TextView _pos = (TextView) convertView.findViewById(R.id.tabla_pos);
		_pos.setText(team.getPos());

		TextView _team = (TextView) convertView.findViewById(R.id.tabla_team);
		_team.setText(team.getName());

		TextView points = (TextView) convertView.findViewById(R.id.tabla_points);
		points.setText(team.getPoints());

		TextView wins = (TextView) convertView.findViewById(R.id.tabla_wins);
		wins.setText(team.getWin());

		TextView draws = (TextView) convertView.findViewById(R.id.tabla_draws);
		draws.setText(team.getDraw());

		TextView losses = (TextView) convertView.findViewById(R.id.tabla_lost);
		losses.setText(team.getLost());

		TextView gf = (TextView) convertView.findViewById(R.id.tabla_gf);
		gf.setText(team.getGoals_score());

		TextView ga = (TextView) convertView.findViewById(R.id.tabla_ga);
		ga.setText(team.getGoals_conc());

		return convertView;
	}
	
	
}
