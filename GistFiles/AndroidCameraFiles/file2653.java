package projet.immac.gtc;

/*
*
* Application GTC pour interface cliente déportée
* Projet BTS2 Immaculée Conception Laval
* Auteur : Alex MONNERIE
* Version : V1.0 derniere version pour remise officielle
* Date : 22/05/2017
*
* Activité secondaire :
*   - configuration des entrées préférées
*   - gestion du site
*   - modification des valeurs de sortie
*
* Attention : Pour une compatibilité optimale, l'application est à utiliser sur terminaux Android en API 10
* Problèmes liés au connecteur HTTP constatés sur d'autres versions.
*
*/


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.R.id.list;

public class ConfigurationActivity extends AppCompatActivity {


    //Déclaration d'objets de type ArrayList
    ArrayList<Czone> czones;
    ArrayList<Cmodule> cmodules;
    ArrayList<Ctes> ctes;

    static String Fav;      //Utilisé lors d'une définition d'entrée préférée
                            //Permet d'identifier l'entrée préférée que l'on configure
                            //Contient "fav" + IDENTREE séléctionnée

    Boolean gestsite;       //Utilisé et vaut 1 si l'utilisateur est entré sur l'activité via bouton "GERER SITE"

    CheckBox activationSL;  //Chekbox permettant d'activer ou désactiver une sortie
                            //Utilisé si l'utilisateur est entré sur l'activité via bouton "GERER SITE"

    TextView selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final TextView infos_idfav = (TextView) findViewById(R.id.fav);
                            //Permet d'afficher et rappeller à l'utilisateur le numéro d'entrée préférée qu'il souhaite définir
                            //Si l'utilisateur est entré sur l'activité via bouton "GERER SITE", cette Textview lui rappelera qu'il peut modifier une valeur de sortie

        selected = (TextView) findViewById(R.id.selected);
                            //Zone d'affichage des infos liées à l'entrée/sortie selectionnée, et des erreurs de communication rencontrées

        Bundle extras = getIntent().getExtras();
                            //Permet de récuperer les différants parametres passés lors de la création de cette activité

        if(extras.getString("gestsite")!=null){
                            //Permet de savoir si l'utilisateur est arrivé sur cette activité via le bouton "GERER SITE"

            gestsite = true;

            infos_idfav.setText("Gestion du site.\nVisualisation du site et modification de sortie.");

        }else{              //Sinon, il souhaite définir une entrée préférée

            gestsite = false;

            String Val_id_fav = extras.getString("id_fav");
            Fav = "fav" + extras.getString("id_fav");
            infos_idfav.setText("Veuillez choisir l'entrée à ajouter aux préférées.\nEntrée préférée n° " + Val_id_fav);

        }

                            //Permet à l'utilisateur de quitter l'activité Configuration sans confirmer les modifications
                            //Reviens à appuyer sur le bouton physique "Retour" du smartphone
        Button Quitter = (Button) findViewById(R.id.stop);
        Quitter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {  finish(); }
        });


        ListZones();        //Appel de la fonction permettant d'afficher les Zones existantes
    }


    /*
    Fonction : ListZones()
    Permet d'afficher la liste des Zones existantes sous forme de Spinner

    Effectue un appel de la methode Zones() de la classe CcomREST
    Récupere les objets créés puis les affiche
    La fonction appelle ensuite la fonction ListModules(Zone)

    Parametres : aucun
    Retours : aucun
    */

    void ListZones() {

                            //Objet czones de classe CcomREST
        CcomREST communication = new CcomREST();
        czones = communication.Zones();

                            //Vérifier l'absence d'erreur pendant la communication via Webservices
        VerifyErrComm("CcomREST.Zones");

                            //Création Arraylist de type CharSequance : nom Zone
        ArrayList<CharSequence> SpinnerZones = new ArrayList<CharSequence>();

                            //Parcours des objets Czone et ajout à SpinnerZones
        for (Czone _zone : czones) {
            SpinnerZones.add(_zone.getName());
        }

                            //Identification du Spinner
        ArrayAdapter<CharSequence> listZones = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, SpinnerZones);
        final Spinner zones = (Spinner) findViewById(R.id.zones);
        zones.setAdapter(listZones);

                            //Détection selection d'une zone puis appel de ListModules()
        zones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


                                 //Appel de ListModules(NomZoneSelectionnée)
                ListModules(parent.getSelectedItem().toString());

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    /*
    Fonction : ListModules()
    Permet d'afficher la liste des Modules existants (en fonction de la zone séléctionnée) sous forme de Spinner

    Effectue un appel de la methode Modules(Zone) de la classe CcomREST
    Récupere les objets créés puis les affiche
    La fonction appelle ensuite la fonction ListES(ModuleType,ModuleAddr)

    Parametres : String Zone
    Retours : aucun
    */

    void ListModules(String Zone) {


                            //Objet cmodules de classe CcomREST
        CcomREST communication = new CcomREST();
        cmodules = communication.Modules(Zone);

                            //Vérifier l'absence d'erreur pendant la communication via Webservices
        VerifyErrComm("CcomREST.Modules");

                            //Création Arraylist de type CharSequance : Nom module
        final ArrayList<CharSequence> SpinnerModules = new ArrayList<CharSequence>();

                            //Création Arraylist de type String : Adresse et Type du module
        final ArrayList<String> SpinnerModulesAddr = new ArrayList<String>();
        final ArrayList<String> SpinnerModulesType = new ArrayList<String>();

                            //Parcours des objets Cmodule et ajout aux Arraylist
        for (Cmodule _modules : cmodules) {
            SpinnerModules.add(_modules.getName());
            SpinnerModulesAddr.add(_modules.getAddr());
            SpinnerModulesType.add(_modules.getType());
        }

                            //Identification du Spinner
        ArrayAdapter<CharSequence> listModules = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, SpinnerModules);
        final Spinner modules = (Spinner) findViewById(R.id.modules);
        modules.setAdapter(listModules);

                            //Détection selection d'un module puis appel de ListES()
        modules.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                            //Récupération des données neccessaires à l'appel de ListES(...)
                String currentModuleAddr = SpinnerModulesAddr.get(pos);
                String currentModuleType = SpinnerModulesType.get(pos);

                ListES(currentModuleType,currentModuleAddr);

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    /*
    Fonction : ListES()
    Permet d'afficher la liste des Entrées/Sorties existantes (en fonction du module séléctionné) sous forme de Spinner

    Effectue un appel de la methode ES(ModuleType,ModuleAddr) de la classe CcomREST
    Récupere les objets créés puis les affiche
    En fonction de la sélection, la fonction appelle ConfirmES(...)

    Parametres : String ModuleType, final String ModuleAddr
    Retours : aucun
    */

    void ListES(String ModuleType, final String ModuleAddr) {

                            //Objet ctes de classe CcomREST
        CcomREST communication = new CcomREST();
        ctes = communication.ES(ModuleType,ModuleAddr);

                            //Vérifier l'absence d'erreur pendant la communication via Webservices
        VerifyErrComm("CcomREST.ES");

                            //Création Arraylist de type CharSequance : nom ES
        final ArrayList<CharSequence> SpinnerES = new ArrayList<CharSequence>();
                            //Création Arraylist de type String : id, type, état ES
        final ArrayList<String> SpinnerESid = new ArrayList<String>();
        final ArrayList<String> SpinnerEStype = new ArrayList<String>();
        final ArrayList<String> SpinnerESactivation = new ArrayList<String>();

                            //Parcours des objets Ctes et ajout aux Arraylist
        for (Ctes _tes : ctes) {
            SpinnerES.add(_tes.getName());
            SpinnerESid.add(_tes.getID().toString());
            SpinnerEStype.add(_tes.getType().toString());
            SpinnerESactivation.add(_tes.getActivation().toString());
        }

                            //Identification du Spinner
        ArrayAdapter<CharSequence> listES = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, SpinnerES);
        final Spinner es = (Spinner) findViewById(R.id.es);
        es.setAdapter(listES);


                            //Détection selection d'une ES et affichage d'informations en fonction des actions de l'utilisateur
        es.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


                            //Récupération des données necessaires
                String CurrentESid = SpinnerESid.get(pos);
                String CurrentEStype = SpinnerEStype.get(pos);
                String CurrentESactivation = SpinnerESactivation.get(pos);


                            //Assignation Checkbox à activationSL
                activationSL = (CheckBox) findViewById(R.id.ActivationSL);

                            //Nouvel objet de classe CcomREST
                CcomREST showdataselected = new CcomREST();


                if(gestsite){

                    if (CurrentEStype.equals("SL")) {
                            //Si une sortie est sélectionnée, modification de sa valeur

                        activationSL.setVisibility(View.VISIBLE);
                            //Afficher choix état sortie

                        String ParamSortie = CurrentESid;

                        ArrayList<String> selecteddata = showdataselected.ValueSortie(ParamSortie);

                        String valueSortie;

                        if (selecteddata.get(1).equals("1")) {
                            valueSortie = "Sortie logique activée";
                        } else {
                            valueSortie = "Sortie logique désactivée";
                        }

                        selected.setText(selecteddata.get(0) + " sélectionnée.\nDescription: " + selecteddata.get(2) + "\nValeur actuelle: " + selecteddata.get(1) + "\n" + valueSortie);
                            //Affichage infos de la sortie

                    }else{

                        activationSL.setVisibility(View.INVISIBLE);
                            //masquer demande valeur pour sortie

                            //Affichage des infos de l'entrée selectionnée
                        String ParamEntree = ModuleAddr + "/" + CurrentESid;
                        ArrayList<String> selecteddata = showdataselected.ValueEntree(ParamEntree);

                        selected.setText(selecteddata.get(0) + " sélectionnée.\nDescription: " + selecteddata.get(2) + "\nValeur actuelle: " + selecteddata.get(1));

                    }

                }else{
                    activationSL.setVisibility(View.INVISIBLE);
                            //masquer demande valeur pour sortie

                        if (CurrentEStype.equals("SL")) {
                            //Si une sortie est sélectionnée au lieu d'une entree

                            selected.setText("Impossible de sélectionner une sortie ici.\nMerci de séléctionner une entrée à définir en tant qu'entrée préférée.");

                            new AlertDialog.Builder(ConfigurationActivity.this)
                                    .setTitle("Sortie sélectionnée")
                                    .setMessage("Impossible de sélectionner une sortie ici.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }else{

                            //Affichage des infos de l'entrée selectionnée
                            String ParamEntree = ModuleAddr + "/" + CurrentESid;
                            ArrayList<String> selecteddata = showdataselected.ValueEntree(ParamEntree);

                            selected.setText(selecteddata.get(0) + " sélectionnée.\nDescription: " + selecteddata.get(2) + "\nValeur actuelle: " + selecteddata.get(1));


                            //Si l'entrée est marquée en BDD comme désactivée, prevenir l'utilisateur
                            if (CurrentESactivation.equals("0")){

                                new AlertDialog.Builder(ConfigurationActivity.this)
                                        .setTitle("Information")
                                        .setMessage("Pour le moment, cette entrée est désactivée.")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        }
                }
                ConfirmES(ModuleAddr,CurrentESid,CurrentEStype);
            }


            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    /*
    Fonction : ConfirmES()

    En fonction des parametres et de l'utilisateur, elle permet :

        - Si l'utilisateur souhaite définir une entrée préférée ...
            - enregistrer la définition d'une entrée comme entrée préférée
            - afficher un message d'erreur si une sortie est sélectionnée au lieu d'une entrée
            - retourner sur l'activité principale apres l'enregistrement

        - Si l'utilisateur souhaite gérer le site, modifier une sortie ...
            - appeler PostNewValueSL(SortieID,NewValueSL) de la classe CcomREST pour modifier une valeur de sortie
            - afficher un message en fonction du succes ou non de la modification de sortie
            - retourner sur l'activité principale

    Parametres : final String ModuleAddr, final String EntreeID, final String EntreeType
    Retours : aucun
    */

void ConfirmES(final String ModuleAddr, final String EntreeID, final String EntreeType){

    final Button Confirm = (Button) findViewById(R.id.confirm);
    Confirm.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {


            if (gestsite) { //Entrée dans l'activity via bouton gestion du site

                if (EntreeType.equals("SL")){ //Si l'utilisateur est dans la gestion du site et si il souhaite modifier une sortie


                    String SortieID = EntreeID;
                    //Dans le cas d'une selection d'une sortie, EntreeID est considéré comme l'ID de la sortie a modifier


                    String NewValueSL; //Nouvelle valeur de sortie
                    if (activationSL.isChecked()) {
                        NewValueSL = "1";
                    }else{
                        NewValueSL = "0";
                    }


                    CcomREST valueSL = new CcomREST();

                        if(valueSL.PostNewValueSL(SortieID,NewValueSL).equals(-1)){
                            //Modification de la sortie retourne une erreur

                            new AlertDialog.Builder(ConfigurationActivity.this)
                                    .setTitle("Sortie inchangée")
                                    .setMessage("Impossible de modifier la sortie.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();


                        }else{  //Si aucune erreur
                            if(NewValueSL.equals("1")){
                                NewValueSL = "activée";
                            }else{
                                NewValueSL = "désactivée";
                            }

                                //Message de confirmation pour l'utilisateur
                            new AlertDialog.Builder(ConfigurationActivity.this)
                                    .setTitle("Sortie modifiée")
                                    .setMessage("Sortie " + NewValueSL + ".")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            //Retour vers l'activité principale
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                }else{
                    //Si l'utilisateur ne souhaite pas modifier de sortie, Retour vers l'activité principale
                    finish();
                }



            } else { //Si l'utilisateur souhaite définir une entrée préférée

                if (EntreeType.equals("SL")) {  //Si une sortie est sélectionnée au lieu d'une entree

                                                //Affichage message d'erreur
                    new AlertDialog.Builder(ConfigurationActivity.this)
                            .setTitle("Sortie sélectionnée")
                            .setMessage("Impossible de sélectionner une sortie ici.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                } else { //Si une entrée est sélectionnée pour etre définie comme préférée

                    //Enregistrement des parametres de l'entrée dans le cache de l'application
                    String ParamEntree = ModuleAddr + "/" + EntreeID;
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ConfigurationActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Fav, ParamEntree);
                    editor.apply();

                    //Retour sur l'activité principale
                    finish();

                }
            }
        }

        });

    }


     /*
    Fonction : VerifyErrComm()

    Vérifie la présence d'erreur de communication avec le serveur Webservices
    La fonction vérifie la valeur de la variable booléene ErrComm de la classe CcomREST

        - Si ErrComm==TRUE : Erreur de communication rencontrée
            - Ajout message dans Logs (System.out.println)
            - Afficher un message d'erreur pour prévenir l'utilisateur
            - Remettre ErrComm=FALSE
        - Si ErrComm==FALSE : Aucune erreur de communication rencontrée

    Parametres : ErrData (Information liée à la cause de l'erreur)
    Retours : aucun
    */

    void VerifyErrComm(String ErrData){

        if (CcomREST.ErrComm) { //Vérifier si erreur rencontrée pendant communication Webservices

            CcomREST.ErrComm = false; //Désactive l'état d'erreur

                                         //Puis indique l'erreur à l'utilisateur via Textview ET AlertDialog
            selected.setText("Erreur comm. Webservices.\nRéessayez.\n\nErrData:" + ErrData);

            AlertDialog alertDialogErreur = new AlertDialog.Builder(this).create();

            alertDialogErreur.setTitle("Erreur critique");
            alertDialogErreur.setMessage("Erreur de communication avec le serveur Webservices.\nRéessayez.\n\nErrData:" + ErrData);
            alertDialogErreur.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialogErreur.show();

        }
    }
}
