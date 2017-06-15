package projet.immac.gtc;

/*
*
* Application GTC pour interface cliente déportée
* Projet BTS2 Immaculée Conception Laval
* Auteur : Alex MONNERIE
* Version : V1.0 derniere version pour remise officielle
* Date : 22/05/2017
*
* Activité principale (lancement de l'application)
*   - affichage des entrées préférées
*   - acces à la gestion du site
*
* Attention : Pour une compatibilité optimale, l'application est à utiliser sur terminaux Android en API 10
* Problèmes liés au connecteur HTTP constatés sur d'autres versions.
*
*/


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


/*
* Classe AcceuilActivity
* Objectif : affichage des entrées préférées, des boutons gestion du site et configuration application
*
* Auteur : Alex MONNERIE
* Version : V1.0 derniere version pour remise officielle
* Date : 22/05/2017
*/

public class AccueilActivity extends AppCompatActivity {

    public TextView fav1, infosfav1;
    public TextView fav2, infosfav2;
    public TextView fav3, infosfav3;
    public TextView fav4, infosfav4;
    public TextView fav5, infosfav5;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;

    private Handler myHandler;


    // Permet d'actualiser automatiquement les valeurs des entrées préférées apres avoir effectué un test de connexion au serveur
    private Runnable myRunnable = new Runnable() {

        @Override
        public void run() { // Code à éxécuter de façon périodique

            final TextView debug = (TextView) findViewById(R.id.debug);
            if(CcomREST.Test().equals(-1)) {  // Test de connexion au serveur Webservices

                //Affichage message erreur connexion Webservices
                debug.setText("Connexion au serveur Webservices impossible.");

                //Affichage alertbox puis fermeture de l'application
                AlertDialog alertDialog = new AlertDialog.Builder(AccueilActivity.this).create();
                alertDialog.setTitle("Erreur critique");
                alertDialog.setMessage("Connexion au serveur Webservices impossible.\nFermez l'application.\n\nErrData:CcomREST.Test");
                alertDialog.setButton("Fermer l'application", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Fermeture de GTC liée à une erreur de connexion au serveur Webservices.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                alertDialog.show();


            }else {

                //Affichage message connexion Webservices
                debug.setText("Vous êtes connecté au serveur Webservices.");

                //Actualisation des valeurs affichées aux entrées préférees
                ActualiserEntreesPref();

            }


            myHandler.postDelayed(this,30000);
            //Période avant réactualisation : 30 secondes
        }
    };



    public void onPause() {
        super.onPause();
        if(myHandler != null)
            myHandler.removeCallbacks(myRunnable); // On arrete le callback
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onstart");
        myHandler = new Handler(); //lancement handler au Start de l'Activity
        myHandler.postDelayed(myRunnable,100); // Période lancement timer lors OnStart
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);


        //Textview permettant l'affichage des entrées préférées
        fav1 = (TextView) findViewById(R.id.EntreeFav1);
        infosfav1 = (TextView) findViewById(R.id.ValueEntreeFav1);

        fav2 = (TextView) findViewById(R.id.EntreeFav2);
        infosfav2 = (TextView) findViewById(R.id.ValueEntreeFav2);

        fav3 = (TextView) findViewById(R.id.EntreeFav3);
        infosfav3 = (TextView) findViewById(R.id.ValueEntreeFav3);

        fav4 = (TextView) findViewById(R.id.EntreeFav4);
        infosfav4 = (TextView) findViewById(R.id.ValueEntreeFav4);

        fav5 = (TextView) findViewById(R.id.EntreeFav5);
        infosfav5 = (TextView) findViewById(R.id.ValueEntreeFav5);


        //Récupération des informations liées aux entrées preferée selectionnées via ConfigurationActivity
        preferences = PreferenceManager.getDefaultSharedPreferences(this);



        /*
        favX.setOnLongClickListener() permet de déclancher des actions lorsque l'on appui longtemps sur une entrée préférée
        Objectif : Définir une entrée comme entrée préférée
        Traitement :
                - Creation d'une nouvelle activité Configuration
                - Passage du parametre "id_fav" à l'activité
                - Lancement de l'activité
         */

        fav1.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                //Nom nouvelle activité
                Intent configuration = new Intent(AccueilActivity.this, ConfigurationActivity.class);
                //Parametre = id entree fav (de 1 à 5)
                configuration.putExtra("id_fav", "1");
                //Lancement nouvelle activité
                startActivity(configuration);
                return false;
            }
        });

        fav2.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View v) {
                //Nom nouvelle activité
                Intent configuration = new Intent(AccueilActivity.this, ConfigurationActivity.class);
                //Paramtre = id entree fav (de 1 à 5)
                configuration.putExtra("id_fav", "2");
                //Lancement nouvelle activité
                startActivity(configuration);
                return false;
            }
        });

        fav3.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                //Nom nouvelle activité
                Intent configuration = new Intent(AccueilActivity.this, ConfigurationActivity.class);
                //Paramtre = id entree fav (de 1 à 5)
                configuration.putExtra("id_fav", "3");
                //Lancement nouvelle activité
                startActivity(configuration);
                return false;
            }
        });


        fav4.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                //Nom nouvelle activité
                Intent configuration = new Intent(AccueilActivity.this, ConfigurationActivity.class);
                //Paramtre = id entree fav (de 1 à 5)
                configuration.putExtra("id_fav", "4");
                //Lancement nouvelle activité
                startActivity(configuration);
                return false;
            }
        });

        fav5.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View v) {
                //Nom nouvelle activité
                Intent configuration = new Intent(AccueilActivity.this, ConfigurationActivity.class);
                //Paramtre = id entree fav (de 1 à 5)
                configuration.putExtra("id_fav", "5");
                //Lancement nouvelle activité
                startActivity(configuration);
                return false;
            }
        });


        /*
        Bouton permettant d'acceder à la gestion du site, modification d'une valeur de sortie
        Traitement :
                - Creation d'une nouvelle activité Configuration
                - Passage du parametre "gestsite" à l'activité
                - Lancement de l'activité   */

        final Button gestsite = (Button) findViewById(R.id.gestsite);
        gestsite.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Nouvelle activité
                Intent configuration = new Intent(AccueilActivity.this, ConfigurationActivity.class);
                configuration.putExtra("gestsite", "1");
                startActivity(configuration);

            }

        });



        /*
        Bouton permettant d'acceder aux parametres de configuration interne de l'application
        Traitement :
                - Creation d'une nouvelle activité ConfigurationApp
                - Lancement de l'activité    */

        final Button configapp = (Button) findViewById(R.id.configapp);
        configapp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Nouvelle activité
                Intent configapp = new Intent(AccueilActivity.this, ConfigurationAppActivity.class);
                startActivity(configapp);

            }

        });

    }



    /*
    Fonction : ActualiserEntréesPref()
    Actualisation des informations et valeurs affichées pour chaque entrées préférées

    Récupération nouvelles données via ValueEntree(...)
    Modification des Textview via setText("...")

    Parametre : Aucun
    Retour : Aucun
     */

    void ActualiserEntreesPref(){


        CcomREST getvaluefav = new CcomREST();

        ArrayList<String> valuefav1 = getvaluefav.ValueEntree(preferences.getString("fav1", null));
        fav1.setText(valuefav1.get(0) + "\n" + valuefav1.get(2));
        infosfav1.setText(valuefav1.get(1));


        ArrayList<String> valuefav2 = getvaluefav.ValueEntree(preferences.getString("fav2", null));
        fav2.setText(valuefav2.get(0) + "\n" + valuefav2.get(2));
        infosfav2.setText(valuefav2.get(1));

        ArrayList<String> valuefav3 = getvaluefav.ValueEntree(preferences.getString("fav3", null));
        fav3.setText(valuefav3.get(0) + "\n" + valuefav3.get(2));
        infosfav3.setText(valuefav3.get(1));

        ArrayList<String> valuefav4 = getvaluefav.ValueEntree(preferences.getString("fav4", null));
        fav4.setText(valuefav4.get(0) + "\n" + valuefav4.get(2));
        infosfav4.setText(valuefav4.get(1));

        ArrayList<String> valuefav5 = getvaluefav.ValueEntree(preferences.getString("fav5", null));
        fav5.setText(valuefav5.get(0) + "\n" + valuefav5.get(2));
        infosfav5.setText(valuefav5.get(1));
    }
}



