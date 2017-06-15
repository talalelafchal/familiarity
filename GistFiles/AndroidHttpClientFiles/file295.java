package projet.immac.gtc;

/*
*
* Application GTC pour interface cliente déportée
* Projet BTS2 Immaculée Conception Laval
* Auteur : Alex MONNERIE
* Version : V1.0 derniere version pour remise officielle
* Date : 22/05/2017
*
* Classe CcomREST : Gestion des communications avec le serveur Webservices
*
* Attention : Pour une compatibilité optimale, l'application est à utiliser sur terminaux Android en API 10
* Problèmes liés au connecteur HTTP constatés sur d'autres versions.
*
*/


import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.ext.httpclient.HttpClientHelper;

import java.io.IOException;
import java.util.ArrayList;



public class CcomREST {


    // URL Complete du serveur Webservices
    static String BaseURL = "http://192.168.1.58:1157/Service.svc/";


    //Gestion des erreurs
    static String ko = "\"Erreur\"";

    static boolean ErrComm = false;


    /*
    Fonction : Zones()
    Recuperation de la liste des zones, creation objets via zones.add()
    Parametre : aucun
    Retour : ArrayList<Czone> zones
    */

    public ArrayList<Czone> Zones() {

                    //Instanciation de Czone
        ArrayList<Czone> zones = new ArrayList<Czone>();

                    //Parametres de Timeout
        Context context = new Context();
        context.getParameters().add("readTimeout ", "2000");
        context.getParameters().add("socketConnectTimeoutMs", "2000");

                    //Preparation de la requete
        Client client = new Client(context, Protocol.HTTP);
        ClientResource cr_zones = new ClientResource(BaseURL + "GetZones");

                    //Application des parametres
        cr_zones.setNext(client);
        cr_zones.setRetryOnError(false);

                    //Type de réponse autorisée du serveur
        cr_zones.accept(MediaType.APPLICATION_JSON);

        try {
                    //Execution de la requete préparée
            Representation results_zones = cr_zones.get();
            try {

                    //Récupération de la réponse
                String jsonString_zones = results_zones.getText();
                    //Traitement de la réponse qui est sous forme de tableau
                JSONArray jsonArray_zones = new JSONArray(jsonString_zones);

                //Si la réponse est une réponse d'erreur
                if(jsonString_zones.equals(ko)) {

                    zones.add(0, new Czone("empty"));
                    erreurCommunication();

                }else {

                    for (int i = 0; i < jsonArray_zones.length(); i++) {
                        //Création d'objet de type Czone
                        zones.add(i, new Czone(jsonArray_zones.getString(i)));
                    }

                }

                return zones;


            } catch (IOException e) {
                e.printStackTrace();
                erreurCommunication();

            } catch (JSONException e) {
                e.printStackTrace();
                erreurCommunication();
            }

        } catch (ResourceException e) {
            e.printStackTrace();
            erreurCommunication();

        }
        return zones;
    }

    /*
    Fonction : Modules()
    Recuperation de la liste des modules, creation objets via modules.add()
    Parametre : Nom Zone selectionnée
    Retour : ArrayList<Cmodule> modules
     */


    public ArrayList<Cmodule> Modules(String NomZone) {

                    //Instanciation de Cmodule
        ArrayList<Cmodule> modules = new ArrayList<Cmodule>();

                    //Variables utilisées dans la création de l'objet
        String jsonResult_Addr = "empty";
        String jsonResult_Desc = "empty";
        String jsonResult_Nom = "empty";
        String jsonResult_Res = "empty";
        String jsonResult_Type = "empty";
        String jsonResult_Zone = "empty";

                    //Parametres de Timeout
        Context context = new Context();
        context.getParameters().add("readTimeout ", "2000");
        context.getParameters().add("socketConnectTimeoutMs", "2000");

                    //Preparation de la requete
        Client client = new Client(context, Protocol.HTTP);
        ClientResource cr_modules = new ClientResource(BaseURL + "GetModules/" + NomZone);

                    //Application des parametres
        cr_modules.setNext(client);
        cr_modules.setRetryOnError(false);

                    //Type de réponse autorisée du serveur
        cr_modules.accept(MediaType.APPLICATION_JSON);

        try {
                    //Execution de la requete préparée
            Representation results_modules = cr_modules.get();

            try {

                    //Récupération de la réponse
                String jsonString = results_modules.getText();

                    //Traitement de la réponse qui est sous forme de tableau
                JSONArray jsonArray = new JSONArray(jsonString);
                JSONObject jsonObj;

                //Si la réponse est une réponse d'erreur
                if(jsonString.equals(ko)) {

                    modules.add(0, new Cmodule(jsonResult_Addr, jsonResult_Desc, jsonResult_Nom, jsonResult_Res, jsonResult_Type, jsonResult_Zone));
                    erreurCommunication();

                }else {


                    //Parcourir Modules
                    for (int u = 0; u < jsonArray.length(); u++) {

                        //Traitement du tableau sous forme d'objets
                        jsonObj = jsonArray.getJSONObject(u);

                        jsonResult_Addr = jsonObj.getString("AdresseReseau");
                        jsonResult_Desc = jsonObj.getString("DescModule");
                        jsonResult_Nom = jsonObj.getString("NomModule");
                        jsonResult_Res = jsonObj.getString("Reseau");
                        jsonResult_Type = jsonObj.getString("Type");
                        jsonResult_Zone = jsonObj.getString("Zone");

                        //Création d'objet de type Czone
                        modules.add(u, new Cmodule(jsonResult_Addr, jsonResult_Desc, jsonResult_Nom, jsonResult_Res, jsonResult_Type, jsonResult_Zone));

                    }

                }
                return modules;


            } catch (IOException e) {
                e.printStackTrace();
                erreurCommunication();
            } catch (JSONException e) {
                e.printStackTrace();
                erreurCommunication();
            }

        } catch (ResourceException e) {
            e.printStackTrace();
            erreurCommunication();

        }
        return modules;
    }

    /*
    Fonction : ES()
    Recuperation de la liste des entrees/sorties, creation objets via tes.add()
    Parametre : Addresse module
    Retour : ArrayList<Ctes> tes
     */

    public ArrayList<Ctes> ES(String ModuleAddr, String ModuleType) {

                    //Instanciation de Ctes
        ArrayList<Ctes> tes = new ArrayList<Ctes>();

                    //Variables utilisées dans la création de l'objet
        String jsonResult_On = "empty";
        String jsonResult_Desc = "empty";
        String jsonResult_Nom = "empty";
        String jsonResult_RefAddr = "empty";
        String jsonResult_Valeur = "empty";
        String jsonResult_Voie = "empty";
        String jsonResult_Id = "empty";
        String jsonResult_Type = "empty";

                    //Parametres de Timeout
        Context context = new Context();
        context.getParameters().add("readTimeout ", "2000");
        context.getParameters().add("socketConnectTimeoutMs", "2000");

                    //Preparation de la requete
        String ParamEntree = ModuleAddr + "/" + ModuleType;
        Client client = new Client(context, Protocol.HTTP);
        ClientResource cr_es = new ClientResource(BaseURL + "GetES/" + ParamEntree);

                    //Application des parametres
        cr_es.setNext(client);
        cr_es.setRetryOnError(false);

                    //Type de réponse autorisée du serveur
        cr_es.accept(MediaType.APPLICATION_JSON);

        try {
                    //Execution de la requete
            Representation results_es = cr_es.get();

            try {

                    //Récupération de la réponse
                String jsonString = results_es.getText();

                    //Si la réponse est une réponse d'erreur
                if(jsonString.equals(ko)){

                    tes.add(0, new Ctes(jsonResult_On, jsonResult_Desc, jsonResult_Nom, jsonResult_RefAddr, jsonResult_Valeur, jsonResult_Voie, jsonResult_Id, jsonResult_Type));
                    erreurCommunication();

                }else{

                    //Traitement de la réponse
                    JSONArray jsonArray = new JSONArray(jsonString);
                    JSONObject jsonObj;

                    for (int u = 0; u < jsonArray.length(); u++) {

                        //Traitement du tableau sous forme d'objets
                        jsonObj = jsonArray.getJSONObject(u);

                        jsonResult_On = jsonObj.getString("Activation");
                        jsonResult_Desc = jsonObj.getString("DescES");
                        jsonResult_Nom = jsonObj.getString("NomES");
                        jsonResult_RefAddr = jsonObj.getString("RefAdresseReseau");
                        jsonResult_Valeur = jsonObj.getString("Valeur");
                        jsonResult_Voie = jsonObj.getString("Voie");
                        jsonResult_Id = jsonObj.getString("idES");
                        jsonResult_Type = jsonObj.getString("TypeES");

                        //Création d'objet de type Ctes
                        tes.add(u, new Ctes(jsonResult_On, jsonResult_Desc, jsonResult_Nom, jsonResult_RefAddr, jsonResult_Valeur, jsonResult_Voie, jsonResult_Id, jsonResult_Type));

                         }
                    }

                return tes;

            } catch (IOException e) {
                e.printStackTrace();
                erreurCommunication();
            } catch (JSONException e) {
                e.printStackTrace();
                erreurCommunication();
            }

        } catch (ResourceException e) {
            e.printStackTrace();
            erreurCommunication();
        }
        return tes;
    }


    /*
    Fonction : ValueEntree()
    Recuperation de la liste des entrees/sorties, creation objets via tes.add()
    Parametre : Addresse module + ID entrée
    Retour : ArrayList<String>
     */

    public ArrayList<String> ValueEntree(String ParamValueEntree) {

                    //Tableau contenant les informations d'une entrée
        final ArrayList<String> ValueEntree = new ArrayList<String>();

                    //Informations et valeur d'entrée par défaut
        String jsonResult_NomEntree = "Entrée préférée à définir";
        String jsonResult_ValueEntree = "0";
        String jsonResult_DescEntree = "Veuillez définir une entrée.";

                    //Parametres de Timeout
        Context context = new Context();
        context.getParameters().add("readTimeout ", "2000");
        context.getParameters().add("socketConnectTimeoutMs", "2000");

                    //Preparation de la requete
        Client client = new Client(context, Protocol.HTTP);
        ClientResource cr_es = new ClientResource(BaseURL + "GetSingleE/" + ParamValueEntree);

                    //Application des parametres
        cr_es.setNext(client);
        cr_es.setRetryOnError(false);

                    //Type de réponse autorisée du serveur
        cr_es.accept(MediaType.APPLICATION_JSON);


        try {

                    //Execution de la requete préparée
            Representation results_es = cr_es.get();

            try {

                String jsonString = results_es.getText();

                if (jsonString.equals(ko)) { //Si probleme sur serveur Webservices
                    erreurCommunication();

                } else {

                    JSONArray jsonArray = new JSONArray(jsonString);

                    if(jsonArray.getString(0).equals(ko)){ //Si le module contenant l'entree a été supprimé, resultat JSON = Erreur

                        jsonResult_NomEntree = "Erreur (module supprimé)";
                        jsonResult_ValueEntree = "Err";
                        jsonResult_DescEntree = "Veuillez définir une nouvelle entrée.";

                    }else{ //Si la fonction nous retourne des valeurs "logiques"

                        jsonResult_NomEntree = jsonArray.getString(0);
                        jsonResult_ValueEntree = jsonArray.getString(1);
                        jsonResult_DescEntree = jsonArray.getString(2);

                    }
                }

                ValueEntree.add(jsonResult_NomEntree);
                ValueEntree.add(jsonResult_ValueEntree);
                ValueEntree.add(jsonResult_DescEntree);


                return ValueEntree;

            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (ResourceException e) {
            e.printStackTrace();


        }

        ValueEntree.add(jsonResult_NomEntree);
        ValueEntree.add(jsonResult_ValueEntree);
        ValueEntree.add(jsonResult_DescEntree);

        return ValueEntree;
    }



    /*
    Fonction : ValueSortie()
    Recuperation de la liste des Sorties/sorties, creation objets via tes.add()
    Parametre : ID sortie
    Retour : ArrayList<String>
     */

    public ArrayList<String> ValueSortie(String ParamValueSortie) {

        final ArrayList<String> ValueSortie = new ArrayList<String>();

        String jsonResult_NomSortie = "Sortie";
        String jsonResult_ValueSortie = "0";
        String jsonResult_DescSortie = "Description";

        Context context = new Context();

        context.getParameters().add("readTimeout ", "2000");
        context.getParameters().add("socketConnectTimeoutMs", "2000");

        Client client = new Client(context, Protocol.HTTP);
        ClientResource cr_sl = new ClientResource(BaseURL + "GetSingleSL/" + ParamValueSortie);

        cr_sl.setNext(client);
        cr_sl.setRetryOnError(true);

        cr_sl.accept(MediaType.APPLICATION_JSON);


        try {
            Representation results_sl = cr_sl.get();

            try {


                String jsonString = results_sl.getText();

                if (jsonString.equals(ko)) { //Réponse d'erreur. Parametres correspond avec aucune Sortie

                    erreurCommunication();

                }else{

                    JSONArray jsonArray = new JSONArray(jsonString);

                    jsonResult_NomSortie = jsonArray.getString(0);
                    jsonResult_ValueSortie = jsonArray.getString(1);
                    jsonResult_DescSortie = jsonArray.getString(2);

                }

                ValueSortie.add(jsonResult_NomSortie);
                ValueSortie.add(jsonResult_ValueSortie);
                ValueSortie.add(jsonResult_DescSortie);


                return ValueSortie;

            } catch (IOException e) {
                e.printStackTrace();
                erreurCommunication();
            } catch (JSONException e) {
                e.printStackTrace();
                erreurCommunication();
            }

        } catch (ResourceException e) {
            e.printStackTrace();
            erreurCommunication();

        }

        ValueSortie.add(jsonResult_NomSortie);
        ValueSortie.add(jsonResult_ValueSortie);
        ValueSortie.add(jsonResult_DescSortie);

        return ValueSortie;
    }




    /*
    Fonction : PostNewValueSL()
    Modification d'une valeur de sortie
    Parametre : ID Sortie, Nouvelle valeur
    Retour : 1:succes ou -1:echec
     */

    public Integer PostNewValueSL(String SortieID, String Value) {


        Context context = new Context();

        context.getParameters().add("readTimeout ", "2000");
        context.getParameters().add("socketConnectTimeoutMs", "2000");

        Client client = new Client(context, Protocol.HTTP);
        ClientResource cr_putvaluesl = new ClientResource(BaseURL + "PutValue/" + SortieID + "/" + Value);

        cr_putvaluesl.setNext(client);
        cr_putvaluesl.setRetryOnError(true);

        cr_putvaluesl.accept(MediaType.APPLICATION_JSON);



        try {
            Representation result_putvaluesl = cr_putvaluesl.get();

            try {


                String return_putvaluesl = result_putvaluesl.getText();

                if (return_putvaluesl.equals("\"Succes\"")) {  //Sortie modifiée

                    return 1;

                }else {     //Aucune sortie modifiée
                            //Parametres corresponds avec aucune Sortie, erreur de communication, erreur interne sur le serveur

                    erreurCommunication();
                    return -1; //Erreur

                }

            } catch (IOException e) {
                e.printStackTrace();
                erreurCommunication();
                return -1;
            }

        } catch (ResourceException e) {
            e.printStackTrace();
            erreurCommunication();
            return -1;
        }

    }



    /*
    Fonction : test()
    Test de connexion au serveur Webservice
    Pas de parametres
    Retour int : 1 => Connexion reussie
                -1 => Erreur de communication, serv probablement hors ligne
     */


    @NonNull
    public static Integer Test() {


        Context context = new Context();

        context.getParameters().add("readTimeout ", "5000");
        context.getParameters().add("socketConnectTimeoutMs", "5000");

        Client client = new Client(context, Protocol.HTTP);
        ClientResource cr = new ClientResource(BaseURL + "Test");

        cr.setNext(client);
        cr.setRetryOnError(false);

        cr.accept(MediaType.APPLICATION_JSON);



        try {
            Representation results = cr.get();


            try {

                //Retour normal : 1
                String test = results.getText();

                if (test.equals(ko)) { //Si probleme sur serveur Webservices
                    return -1;

                }else {

                    if (test.equals("1")) { //Si retour attendu == retour recu
                        return 1;  //Retour normal, connexion reussie, test reussi
                    } else {
                        return -1; //Retour correspond à "Erreur" >> Erreur test de communication
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }


        } catch (ResourceException e) {
            e.printStackTrace();
            return -1;
        }

    }


    /*
    Fonction : erreurCommunication()
    Modifie la valeur de ErrComm en TRUE affin d'indiquer une erreur de communication
    Permettra l'affichage d'un message indiquant à l'utilisateur une erreur de communication via ConfigurationActivity

    Pas de parametres
    Retour : aucun
     */


    public void erreurCommunication(){

        System.out.println("****ERREUR_COMMUNICATION****");
        
        if(!ErrComm) {
            ErrComm = true;
        }
    }


}