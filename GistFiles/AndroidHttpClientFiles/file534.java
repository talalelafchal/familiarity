package com.example.QCM;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Connexion extends Activity
{
    /**
     * Called when the activity is first created.
     */

    private EditText name;
    private EditText pass;
    private TextView err;
    private Button valid;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void valider_Co(View v)
    {
        this.name = (EditText)findViewById(R.id.et_Name);
        this.pass = (EditText)findViewById(R.id.et_Pass);
        this.err = (TextView)findViewById(R.id.tv_Err);
        boolean etatConnect = false;

        if(this.name.length() > 0)
        {
            if(this.pass.length() > 0)
            {
                this.err.setText("");

                ConnexionBDD connection = new ConnexionBDD(this.name.getText().toString(),this.pass.getText().toString());
                etatConnect = connection.setConnection("http://testapp.assocoma.fr/connexion.php");
                if(etatConnect == true)
                {
                    Log.i("tagEtatCo","Oui");
                    this.err.setText("");
                    //Appel vue suivante
                    Intent intent = new Intent(Connexion.this, ChoixMat.class);
                    startActivity(intent);
                }
                else
                {
                    Log.i("ErrorCo", "Problème lors de la connection");
                    this.err.setText("Problème lors de la connection - Veuillez réessayer !");
                }
            }
            else
            {
                this.err.setText("Mot de passe vide !");
            }
        }
        else
        {
            this.err.setText("Mot de passe ou Identifiant vide ! ");
        }
    }
}
