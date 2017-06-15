package com.example.mrshaps.awale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlateauView extends LinearLayout implements OnClickListener{
	// definir la vue
	TextView status_J0 = null;
	TextView status_J1 = null;
	TextView status_partie = null;
	TextView graines_j0 = null;
	TextView graines_j1 = null;
	Button plateau[]  = null;
	//Partie p = null;
	Context ctx = null;
	
	public PlateauView(Context context) {
		super(context);
		
		LayoutInflater li = ((Activity)context).getLayoutInflater();
		li.inflate(R.layout.activity_plateau, this);
		// definir la vue
		status_J0 = (TextView) findViewById(R.id.textView1);
		status_J1 = (TextView) findViewById(R.id.textView2);
		status_partie = (TextView) findViewById(R.id.textView3);
		graines_j0 = (TextView) findViewById(R.id.textView4);
		graines_j1 = (TextView) findViewById(R.id.textView5);

		plateau = new Button[12];
		for (int i=0; i< 12 ; i++){
			int resID = getResources().getIdentifier("Button_"+i,"id","fr.iutbm.couchot.android_awale");
			plateau[i] = (Button) this.findViewById(resID);
			plateau[i].setOnClickListener(this);		        			
		}
		ctx = context;
		this.setWillNotDraw(false); // pour que la méthode OnDraw soit invoquee
	}

	@Override
	protected void onDraw(Canvas canvas) {
        /*
		// associer les éléments aux données
		Partie p = ModeleJeu.recup(ctx);
		if (p.getJoueur_actif() == Partie.JOUEUR0){
			status_J0.setText(" A J0 de jouer");
			status_J1.setText("...");
		}else{
			status_J0.setText("...");
			status_J1.setText(" A J1 de jouer");
		}
		
		
		int [] plt = p.getPlateaux();
		for (int i=0; i< 12 ; i++){
			plateau[i].setText(""+plt[i]);
		}
		status_partie.setText(p.getStatus_partie());
		
		graines_j0.setText(""+p.getGraines_joueur_0());
		graines_j1.setText(""+p.getGraines_joueur_1());
*/
    }

	public void onClick(View v) {
        /*
		int casecliquee = 0;
		Partie p = ModeleJeu.recup(ctx);
		for (int i=0; i< 12 ; i++){
			int resID = getResources().getIdentifier("Button_"+i,"id","fr.iutbm.couchot.android_awale");
			if (v.getId() == resID)
				casecliquee = i;
		}
		p.a_joue(casecliquee);
		
		this.invalidate();
		*/
	}
	
	
}
