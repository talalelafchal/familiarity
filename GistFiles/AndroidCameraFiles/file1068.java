package com.app.mungoi.minhaprimeiraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Implementa o onClickListener para que todos os componentes utilizem a mesma instancia para
 * escutar os eventos de clique.
 */
public class MainActivity extends ActionBarActivity  implements View.OnClickListener {


    private EditText mCampoNome;
    private EditText mCampoEmail;

    private Button mBotaoNome;
    private Button mBotaoEmail;
    private Button mBotaoFoto;

    private ImageView mFotografia;

    private static final String CHAVE_NOME="nome";

    private static final int PEDIDO_TIRAR_FOTO=200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Define o conteudo de uma activity explicitamente a uma view.
        * Ao invocar este metodo tanto a largura como a altura da view sao automaticamnte definidas para o MATCH_PARENT
        * Este metodo sera responsavel por desenhar a interface grafica do ficheiro activity_main.xml
        */
        setContentView(R.layout.activity_main);


        /*
        * Crias instancias de cada objecto e mapea ao componente no xml atraves do id pelo metodo findViewById.
        * O metodo findViewById recebe um inteiro. Ao definir a propriedade android:id do componente no xml , o R gerou
        * uma variavel statica e final que pode ser utilizada fazendo R.id.<identificador>.
        * Note que existe o android.R que contem os resources pre-definidos do android e o <nomePacote>.R que e o R onde escritos os id's dos componentes criados por nos.
        */
        mCampoNome = (EditText)findViewById(R.id.mTextoNome);

        mCampoEmail = (EditText)findViewById(R.id.mTextoEmail);

        mBotaoNome = (Button)findViewById(R.id.mButtonOk);

        mBotaoEmail = (Button)findViewById(R.id.mEnviarEmail);

        mBotaoFoto = (Button)findViewById(R.id.mTirarFotografia);

        mFotografia = (ImageView)findViewById(R.id.img);


        /**
         *
         * Registra a escuta do evento de clique "onClickListener" para que os botoes possam responder ao serem clicados
         */

        mBotaoNome.setOnClickListener(this);

        mBotaoEmail.setOnClickListener(this);

        mBotaoFoto.setOnClickListener(this);

    }

    /**
     * Implementa a resposta que os botoes acima registrados devem ter ao ser clicados.
     * Recebe uma view que pode ser identificada apartir do eu id
     * @param v
     */
    @Override
    public void onClick(View v) {


        switch(v.getId())
        {
            case R.id.mButtonOk:
            {

                iniciarNovaActivity();
                break;
            }
            case R.id.mEnviarEmail:
            {
                enviarEmail();
                break;
            }
            case R.id.mTirarFotografia:
            {
                tiraFotografia();
                break;
            }

        }

    }


    /**
     *  metodos auxiliares que sao invocados pelo clique de cada  um dos botoes
     */



    public void iniciarNovaActivity()
    {
        //Cria um Intent que recebe como parametro o Context e a classe do Componente a ser passado
        Intent intent=new Intent(MainActivity.this,SegundaActivity.class);

        //Adiciona o nome que escrevemos no campo de nome ao intent
        //Os dados no intent sao guardados utilizando um par de chave-valor
        intent.putExtra(CHAVE_NOME,mCampoNome.getText().toString());

        //Instrui o framework para iniciar uma nova activity utilizando o Intent que construimos

        startActivity(intent);
    }


    public void enviarEmail()
    {
        /*
        Cria um Intent e passa como parametro uma ACTIVITY ACTION utilizada pelo Intent para iniciar uma Activity
        O android ja vem com varias ACTIONS por defeito e estao todas documentadas no javadoc
        Link: http://developer.android.com/reference/android/content/Intent.html#Intent(android.content.Context, java.lang.Class<?>)
         */
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        //define o mime type do email
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");

        //define o recipiente e outros dados do email como o assunto e o corpo
        emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{mCampoEmail.getText().toString()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Assunto do e-mail");
        emailIntent.putExtra(Intent.EXTRA_TEXT   , "Corpo do Email");

        /*
        * Instrui o framework para iniciar um chooser que ira mostrar todas as aplicacoes que conseguem enviar e-mails
        * e apos a escolha e o envio do email a activity e fechada.
        * */
        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar um e-mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "Nao existe nenhum cliente de e-mail instalado", Toast.LENGTH_SHORT).show();
        }
    }


    public void tiraFotografia()
    {
        //Cria intent com uma ACTIVITY ACTION para inicializar a camera e capturar uma imagem
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Verifica se existe uma aplicacao capaz de resolver o Intent passado e inicia a Activity da Camera esperando um resultado identificado pelo codigo de pedido passado como parametro
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, PEDIDO_TIRAR_FOTO);
        }
    }


    //espera os resultados que podem ser passados a activity. Cada Resultado vem acompanhado do codigo de pedido que foi passado ao inciar a activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PEDIDO_TIRAR_FOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mFotografia.setImageBitmap(imageBitmap);
        }
    }
}
