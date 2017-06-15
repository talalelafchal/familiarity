package projet.immac.gtc;

/*
*
* Application GTC pour interface cliente déportée
* Projet BTS2 Immaculée Conception Laval
* Auteur : Alex MONNERIE
* Version : V1.0 derniere version pour remise officielle
* Date : 22/05/2017
*
* Activité secondaire : (non imposé dans le cahier des charges, en cours de développement...)
*   - configuration de l'application
*       - modification de l'adresse IP et du port utilisé pour les communications Webservices
*       - restauration des parametres par défaut pour les entrées préférées
*   - indications destinées à l'utilisateur de l'application (rubrique d'aide)
*
* Attention : Pour une compatibilité optimale, l'application est à utiliser sur terminaux Android en API 10
* Problèmes liés au connecteur HTTP constatés sur d'autres versions.
*
*/

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ConfigurationAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configapp);
    }
}
