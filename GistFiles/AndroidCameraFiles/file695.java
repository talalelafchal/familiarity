package projet.immac.gtc;

/*
*
* Application GTC pour interface cliente déportée
* Projet BTS2 Immaculée Conception Laval
* Auteur : Alex MONNERIE
* Version : V1.0 derniere version pour remise officielle
* Date : 22/05/2017
*
* Classe Czone : contenant les variables indispensables aux zones
* Gestion Objets de type "Czone"
*
* Attention : Pour une compatibilité optimale, l'application est à utiliser sur terminaux Android en API 10
* Problèmes liés au connecteur HTTP constatés sur d'autres versions.
*
*/


public class Czone {

    private CharSequence nom;

    public Czone(CharSequence _nom){
        nom = _nom;
    }

    public CharSequence getName(){
        return nom;
    }

}
