package projet.immac.gtc;

/*
*
* Application GTC pour interface cliente déportée
* Projet BTS2 Immaculée Conception Laval
* Auteur : Alex MONNERIE
* Version : V1.0 derniere version pour remise officielle
* Date : 22/05/2017
*
* Classe Cmodule : contenant les variables indispensables aux modules
* Gestion Objets de type "Cmodule"
*
* Attention : Pour une compatibilité optimale, l'application est à utiliser sur terminaux Android en API 10
* Problèmes liés au connecteur HTTP constatés sur d'autres versions.
*
*/

public class Cmodule {

    private CharSequence nom;
    private CharSequence type;
    private CharSequence description;
    private CharSequence reseau;
    private CharSequence zone;
    private CharSequence adresse_reseau;


            /* Exemple de récupération des données via Webservices :

                "AdresseReseau": "1",
                "DescModule": "Description lambda",
                "NomModule": "Module 1 ",
                "Reseau": "TES",
                "Type": "4050",
                "Zone": "Zone 1"

         */

    public Cmodule(CharSequence _adresse_reseau, CharSequence _description, CharSequence _nom, CharSequence _reseau, CharSequence _type, CharSequence _zone){
        nom = _nom;
        type = _type;
        description = _description;
        reseau = _reseau;
        adresse_reseau = _adresse_reseau;
        zone = _zone;
    }

    public CharSequence getName(){
        return nom.toString();
    }

    public String getAddr(){
        return adresse_reseau.toString();
    }

    public String getType(){ return type.toString(); }

}
