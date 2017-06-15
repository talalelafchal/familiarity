/**
* @author Felipe Pereira Homem
*/
package br.com.ucam;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import br.com.ucam.R;

public class VariasTelas extends Activity {
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        chamaMenuPrincipal();
        
       
        
    }
    public void chamaCadastro(){
    	setContentView(R.layout.cadastro);
    	Button btMenuPrincipal = (Button) findViewById(R.id.btMenuPrincipal);
    	btMenuPrincipal.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    	                 chamaMenuPrincipal();
    					
    				}
    			});
    }
    public void chamaConsulta(){
    	setContentView(R.layout.consulta);
        Button btVoltar = (Button) findViewById(R.id.btVoltar);
        
        btVoltar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			   chamaMenuPrincipal();				
			}
		});
    }
    public void chamaMenuPrincipal(){
    	setContentView(R.layout.main);
    	 Button btCadastro = (Button) findViewById(R.id.btCadastro);
         Button btConsulta = (Button) findViewById(R.id.btConsulta);
        
         
         btCadastro.setOnClickListener(new View.OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				chamaCadastro(); //chama a tela de cadastro
 				
 			}
 		});
         
         btConsulta.setOnClickListener(new View.OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				chamaConsulta(); //chama a tela de consulta
 			}
 		});

    }
}