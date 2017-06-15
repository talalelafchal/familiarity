package projet.immac.gtc;

/*
*
* Application GTC pour interface cliente déportée
* Projet BTS2 Immaculée Conception Laval
* Auteur : Alex MONNERIE
* Version : V1.0 derniere version pour remise officielle
* Date : 22/05/2017
*
* Classe Ctes : contenant les variables indispensables aux entrées/sorties
* Gestion Objets de type "Ctes"
*
* Attention : Pour une compatibilité optimale, l'application est à utiliser sur terminaux Android en API 10
* Problèmes liés au connecteur HTTP constatés sur d'autres versions.
*
*/

public class Ctes {

    private CharSequence activation;
    private CharSequence description;
    private CharSequence nom;
    private CharSequence ref_addr;
    private CharSequence valeur;
    private CharSequence voie;
    private CharSequence id;
    private CharSequence type;

        /* Exemple de récupération des données via Webservices :

                "Activation": "1",
                "Description": "No Description",
                "Nom": "Entree 2",
                "RefAdresseReseau": "4",
                "Valeur": "",
                "Voie": "2",
                "id": "23"
                "ES_Type" : "ES", "EA", "SL"
        */


    public Ctes(CharSequence _activation, CharSequence _description, CharSequence _nom, CharSequence _ref_addr, CharSequence _valeur, CharSequence _voie, CharSequence _id, CharSequence _type){

        activation = _activation;
        description = _description;
        nom = _nom;
        ref_addr = _ref_addr;
        valeur = _valeur;
        voie = _voie;
        id = _id;
        type = _type;
    }


    public CharSequence getName(){
        return nom;
    }
    public CharSequence getID(){
        return id;
    }
    public CharSequence getType(){
        return type;
    }
    public CharSequence getActivation(){ return activation; }
}
