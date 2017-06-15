 package br.com.nutrichat.feed;

 import android.app.Activity;
 import android.app.ProgressDialog;
 import android.content.Context;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.graphics.Color;
 import android.os.Bundle;
 import android.support.v4.widget.DrawerLayout;
 import android.util.Log;
 import android.view.Gravity;
 import android.view.KeyEvent;
 import android.view.View;
 import android.widget.AbsListView;
 import android.widget.AdapterView;
 import android.widget.ListView;
 import android.widget.RelativeLayout;
 import android.widget.TextView;

 import com.android.volley.Request;
 import com.android.volley.Response;
 import com.android.volley.VolleyError;
 import com.android.volley.VolleyLog;
 import com.android.volley.toolbox.JsonObjectRequest;
 import com.melnykov.fab.FloatingActionButton;
 import com.nostra13.universalimageloader.core.DisplayImageOptions;
 import com.nostra13.universalimageloader.core.ImageLoader;
 import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
 import com.pushbots.push.Pushbots;
 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;

 import java.util.ArrayList;
 import java.util.List;

 import br.com.nutrichat.R;
 import br.com.nutrichat.classes.Comentario;
 import br.com.nutrichat.classes.Feed;
 import br.com.nutrichat.classes.Foto;
 import br.com.nutrichat.classes.TipoComida;
 import br.com.nutrichat.classes.UsuarioCadastro;
 import br.com.nutrichat.dao.repositorios.RepositorioUsuario;
 import br.com.nutrichat.menu.MenuClickListener;
 import br.com.nutrichat.menu.MenuItem;
 import br.com.nutrichat.menu.MenuListAdapter;
 import br.com.nutrichat.plano.StatusSubscription;
 import br.com.nutrichat.util.Alertas;
 import br.com.nutrichat.util.Constantes;
 import br.com.nutrichat.util.CustomHandler;
 import br.com.nutrichat.util.Fontes;
 import br.com.nutrichat.util.Validador;
 import br.com.nutrichat.volley.Net.AppController;
 import br.com.nutrichat.volley.conf.Utf8JsonObjectRequest;
 import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 *
 * Nutrichat - 2015
 *
 * Classe AtividadeFeed
 *
 * Esta classe define os métodos de manipulação dos elementos da activity de feed de notícias
 */
public class AtividadeFeed extends Activity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private DrawerLayout layoutMenuLateral;
    private ListView listaFeed; // Componente de lista de nutricionistas
    private AdapterListViewFeed adapterListView;
    private List<Feed> listaDadosFeeds;
    private ProgressDialog caixaDialogoProcesso;
    private UsuarioCadastro usuario;
    private ImageLoader mImageLoader;
    private RelativeLayout badgeFeed;
    private static String cStatusValidade;
    private TextView textoCirculoNotificacoes;
    private FloatingActionButton fab;

    // Nome de preferências do aplicativo
    private static final String PREF_NAME = "LoginActivityPreferences";
    private SweetAlertDialog caixaDeMenssagem;
    private String deviceToken = "";

    private String tag_json_obj = AtividadeFeed.class.getSimpleName();
    private int numPage, totalPage, numRecord;
    private String pageString;
    private MenuItem[] listaMenuItens;
    ListView listaItensMenuLateral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_feed);

        //fabio
        numPage = 1;
        totalPage = 0;
        numRecord = 0;
        pageString = "%s%s/post/list?page=";


        // Define o título da barra de título personalizada
        TextView titulo = (TextView) findViewById(R.id.barra_nutrichat_feed_titulo);
        titulo.setTextSize(Constantes.TAMANHO_FONTE_TITULO_MENU);
        titulo.setText("Meu Diário");

        badgeFeed = (RelativeLayout) findViewById(R.id.view_badge_feed);
        badgeFeed.setVisibility(View.INVISIBLE);
        textoCirculoNotificacoes = (TextView) findViewById(R.id.feed_texto_circulo_notificacoes);

        usuario = new RepositorioUsuario(this).listar().get(0);
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(!usuario.getEmail().isEmpty() && usuario.getEmail() != null && deviceToken.isEmpty() && sp.getBoolean("enviaToken",true)){
            Log.d("FEED SHARED","EXECUTOU 1 VEZ");
            Pushbots.sharedInstance().setAlias(usuario.getEmail());
            Pushbots.sharedInstance().setCustomHandler(CustomHandler.class);
            deviceToken = Pushbots.sharedInstance().regID();
            editor.putString("login", usuario.getEmail());
            editor.putString("deviceToken",deviceToken);
            editor.putBoolean("enviaToken",false);
        }else{
            Log.d("FEED SHARED","NAO EXECUTOU ****");
            deviceToken = sp.getString("deviceToken","");
        }

        if(!usuario.getSenha().isEmpty() || usuario.getSenha() != null) {
            editor.putString("password", usuario.getSenha());
        }

        if(!usuario.getSenha().isEmpty() || usuario.getSenha() != null) {
            editor.putInt("id", usuario.getIdUsuario());
        }

        if(!usuario.getSenha().isEmpty() || usuario.getSenha() != null) {
            editor.putString("customerStatus", usuario.getCustomerStatus());
        }

        editor.putString("type", "Customer");
        editor.commit();

        Fontes.alterarFonte(this, (TextView) findViewById(
                R.id.barra_nutrichat_feed_titulo));

        String[] textoItensMenuLateral = getResources().getStringArray(R.array.nav_drawer_items);
        int[] listaIconesMenuLateral = {
                R.drawable.icone_menu_dashboard,
                R.drawable.icone_menu_feed,
                R.drawable.icone_menu_notificacao,
                R.drawable.ic_discuss,
                R.drawable.icone_menu_meu_perfil,
                R.drawable.icone_menu_meu_plano,
                R.drawable.icone_menu_sobre,
                R.drawable.icone_menu_fale_conosco,
                R.drawable.icone_menu_desconectar
        };
        layoutMenuLateral = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView listaItensMenuLateral = (ListView) findViewById(R.id.atividade_feed_lista_menu);
        MenuItem[] listaMenuItens = new MenuItem[9];// correto é 9
        for (int i = 0; i < textoItensMenuLateral.length; i++) {
            //Correto é + 1. Depois que tiver o dashboard deve ser mudado
            if (i + 1 == MenuListAdapter.TELA_FEED)//
                listaMenuItens[i] = new MenuItem(textoItensMenuLateral[i],
                        listaIconesMenuLateral[i], MenuListAdapter.TELA_FEED);
            else
                listaMenuItens[i] = new MenuItem(textoItensMenuLateral[i],
                        listaIconesMenuLateral[i], 0);
        }

        MenuListAdapter adapter = new MenuListAdapter(this,
                R.layout.menu_principal_nutrichat_item_menu, listaMenuItens);
        listaItensMenuLateral.setAdapter(adapter);
        listaItensMenuLateral.setOnItemClickListener(new MenuClickListener(this));

        listaFeed = (ListView) findViewById(R.id.atividade_feed_lista_feed);
        listaFeed.setOnItemClickListener(this);

        //fabio
        listaFeed.setOnScrollListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);


        String url = String.format(pageString + String.valueOf(numPage), Constantes.URL_SALVAR_OBJETIVOS_PREFERENCIAS, String.valueOf(usuario.getIdUsuario()));
        callJson2(url, true, true);

        listaDadosFeeds = new ArrayList<Feed>();


    }


    // 2.0 and above
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // Before 2.0
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return false;
    }


    /**
     * Método executado quando o ícone de menu da barra de título é pressionado.
     *
     * @param componente recebe um objeto da classe <code>View</code>.
     */
    public void abrirMenu(View componente) {
    /*    layoutMenuLateral = (DrawerLayout) findViewById(R.id.layout_atividade_feed);
        //layoutMenuLateral.
        layoutMenuLateral.openDrawer(Gravity.START);
    */
       /* MenuListAdapter adapter = new MenuListAdapter(this,
                R.layout.menu_principal_nutrichat_item_menu, listaMenuItens);
        listaItensMenuLateral.setAdapter(adapter);
        listaItensMenuLateral.setOnItemClickListener(new MenuClickListener(this));
        */
        layoutMenuLateral = (DrawerLayout) findViewById(R.id.layout_atividade_feed);
        layoutMenuLateral.openDrawer(Gravity.START);
    }

    public void notificacoes(View componente) {
        startActivity(new Intent("lista_notificacoes"));
    }

    public void chat(View componente) {

        startActivity(new Intent("chat_intro"));
    }


    public void novaFoto(View componente) {
        if (usuario.getCustomerStatus().equalsIgnoreCase(StatusSubscription.FREE_TRIAL_EXPIRED.name()) ||
                usuario.getCustomerStatus().equalsIgnoreCase(StatusSubscription.SUBSCRIPTION_CANCELED.name())) {
            Intent intent = new Intent("plano_tag_json_objdo");
            startActivity(intent);
        } else {
            Intent intent = new Intent("novo_post");
            intent.putExtra("post", new Feed());
            startActivity(intent);
        }


    }

    public void obterFeeds() {
       /* if(Conexao.verificaConexao(this)){
            caixaDeMenssagem = Alertas.barraProgresso("Carregando seu Diário",this,caixaDeMenssagem);
            TarefaConexaoDados tarefa = new TarefaConexaoDados(this,
                    TarefaConexaoDados.OBTER_FEEDS);
            tarefa.execute(String.valueOf(usuario.getIdUsuario()));
        }else{
           // caixaDeMenssagem.dismiss();
            Alertas.menssagemDeErro("Não há conexão com a internet", this);
        }*/
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent("visualizar_post");
        intent.putExtra("post", listaDadosFeeds.get(position));
        startActivity(intent);
    }

    /*   @Override
       public void comecouExecucao() {
       }
       @Override
       public void mostrarMensagem(String mensagem) {
         //  caixaDialogoProcesso.setMessage(mensagem);
       }
       @Override
       public void terminouExecucao(ArrayList<Object> listaDados, String mensagemRetorno) {
           Log.d("Filipe", "terminou execurcao");
           if(mensagemRetorno.contains("OK")){
               DadosPaginacaoLista dadosPaginacaoLista = (DadosPaginacaoLista)listaDados.get(0);
               DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                       .showImageForEmptyUri(R.drawable.camera_listagem)
                       .showImageOnFail(R.drawable.error)
                       .showImageOnLoading(R.color.branco)
                       .resetViewBeforeLoading(true)
                       .cacheInMemory(true)
                       .cacheOnDisk(true)
                               //.displayer(new FadeInBitmapDisplayer(1000))
                       .build();
               ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(AtividadeFeed.this)
                       .defaultDisplayImageOptions(mDisplayImageOptions)
                       .memoryCacheSize(2 * 1024 * 1024)
                       .diskCacheSize(2 * 1024 * 1024)
                       .threadPoolSize(5)
                       .writeDebugLogs()
                       .build();
               mImageLoader = ImageLoader.getInstance();
               mImageLoader.init(conf);
               PauseOnScrollListener mPauseOnScrollListener = new PauseOnScrollListener(mImageLoader, true, true);
               for (int i = 1; i <= dadosPaginacaoLista.getQuantidadePaginas(); i++) {
                   if(listaDados.size()>1){
                   Feed feeds[] = (Feed[])listaDados.get(i);
                   for (Object feed: feeds)
                   listaDadosFeeds.add((Feed) feed);
                   //Cria o adapter
                   adapterListView = new AdapterListViewFeed(this, listaDadosFeeds, mImageLoader);
                   //Define o Adapter
                   listaFeed.setAdapter(adapterListView);
                   //Cor quando a lista é selecionada para rolagem.
                   listaFeed.setCacheColorHint(Color.TRANSPARENT);
                   // remove a linha divisória entre os itens da lista
                   listaFeed.setDivider(null);
                   listaFeed.setDividerHeight(0);
                   //
                   }
               // Quando a funcao obterNumNotificacao() nao estiver comentada essa linda deve ficar comentada.
               }
               //configuraFab();
               //obterNumNotificacao();
               caixaDeMenssagem.dismiss();//
           }else if(mensagemRetorno.contains("NUM_NOTIFICACAO")){
            NotificationCountList notificationCountList = (NotificationCountList) listaDados.get(0);
               SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
               SharedPreferences.Editor editor = sp.edit();
               editor.putInt("numNotificacoesPendentes", notificationCountList.getUnreadCount());
               editor.commit();
               if(notificationCountList.getUnreadCount() > 0){
                   badgeFeed.setVisibility(View.VISIBLE);
                   textoCirculoNotificacoes.setText(String.valueOf(notificationCountList.getUnreadCount()));
               }
           }else {
               caixaDeMenssagem.dismiss();
               Alertas.menssagemDeErro(mensagemRetorno, this);
               //caixaDialogoProcesso.dismiss();
               //Toast.makeText(this, mensagemRetorno, Toast.LENGTH_LONG).show();
           }
       }
       public void obterNumNotificacao(){
           TarefaConexaoDados tarefa = new TarefaConexaoDados(this,
                   TarefaConexaoDados.OBTER_NUM_NOTIFICACOES);
           tarefa.execute(String.valueOf(usuario.getIdUsuario()));
       }
   */
    public static void initImageLoader(Context context) {

        ImageLoaderConfiguration.createDefault(context);
        // Initialize ImageLoader with configuration.
        // ImageLoader.getInstance().init(config.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
		/*mImageLoader.clearMemoryCache();
		mImageLoader.clearDiskCache();*/
        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().destroy();
        }

    }

    public void configuraFab() {

        fab.setVisibility(View.VISIBLE);
        fab.setType(FloatingActionButton.TYPE_NORMAL);
        fab.setColorNormal(getResources().getColor(R.color.cor_menu_superior));
        fab.setColorPressed(getResources().getColor(R.color.laranja));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verificaBloqueio(usuario.getEmail());


            }
        });
    }



    private void callJson2(String url, Boolean scroll, Boolean carregaAtividades) {

        if (scroll) {
            caixaDeMenssagem = Alertas.barraProgresso("Carregando seu Diário", this, caixaDeMenssagem);
        }

        if (carregaAtividades) {

            enviaDeviceToken();
            obterNumNoti();
            configuraFab();
        }
        Utf8JsonObjectRequest jsonObjReq = new Utf8JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Filipe", response.toString());
                        getFieldsJson(response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("EE", "Error: " + error.getMessage());
                // hide the progress dialog

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private void getFieldsJson(JSONObject j) {
        JSONObject json = null;
        JSONArray jsonCustomer = null;
        try {
            json = j.getJSONObject("paginationData");
            jsonCustomer = j.getJSONArray("listData");

            totalPage = json.getInt("numPages");


            DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.camera_listagem)
                    .showImageOnFail(R.drawable.error)
                    .showImageOnLoading(R.color.branco)
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                            //.displayer(new FadeInBitmapDisplayer(1000))
                    .build();

            ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(AtividadeFeed.this)
                    .defaultDisplayImageOptions(mDisplayImageOptions)
                    .memoryCacheSize(2 * 1024 * 1024)
                    .diskCacheSize(2 * 1024 * 1024)
                    .threadPoolSize(5)
                    .writeDebugLogs()
                    .build();
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(conf);


            for (int i = 1; i <= jsonCustomer.length(); i++) {

                Feed feed = new Feed();
                TipoComida tipoComida = new TipoComida();
                List<Foto> fotos = new ArrayList<Foto>();
                List<Comentario> comentarios = new ArrayList<Comentario>();

                JSONObject feedObject = (JSONObject) jsonCustomer.get(i-1);

                if(feedObject.length() >= 0) {

                    feed.setIdFeed(feedObject.getInt("code"));
                    feed.setTitulo(feedObject.getString("title"));
                    feed.setDescricao(feedObject.getString("description"));
                    feed.setDataCriacao(feedObject.getString("created_at"));
                    feed.setDataPostagem(feedObject.getString("post_date"));
                    feed.setQuantidadeComentarios(feedObject.getInt("commentsCount"));
                    feed.setQuantidadeFotos(feedObject.getInt("photosCount"));
                    feed.setNumeroMesesPostagem(feedObject.getString("pretty_date"));
                    feed.setStatusCurtida(feedObject.getBoolean("liked"));
                    feed.setCurtidoPor(feedObject.getString("likedAt"));
                    Log.d("FEED FEED", feedObject.getString("description"));
                    JSONObject tpComida = feedObject.getJSONObject("mealType");

                    tipoComida.setDescricao(tpComida.getString("description"));
                    tipoComida.setTag(tpComida.getString("tag"));
                    tipoComida.setNome(tpComida.getString("name"));
                    tipoComida.setIdTipoComida(tpComida.getInt("code"));

                    feed.setTiposComida(tipoComida);

                    if (feed.getQuantidadeFotos() > 0) {
                        for (int f = 1; f <= feed.getQuantidadeFotos(); f++) {
                            JSONObject foto = (JSONObject) feedObject.getJSONArray("photos").get(f - 1);

                            Foto ft = new Foto();
                            ft.setTitulo(foto.getString("title"));
                            ft.setUrlFoto(foto.getString("picture_url"));
                            //ft.setDataCriacaoFoto(foto.getString("created_at"));

                            fotos.add(ft);
                        }
                    }
                    feed.setFotos(fotos);

                    if (feed.getQuantidadeComentarios() > 0) {

                        for (int c = 1; c <= feed.getQuantidadeComentarios(); c++) {
                            JSONObject comentario = (JSONObject) feedObject.getJSONArray("comments").get(c - 1);

                            Comentario comments = new Comentario();
                            comments.setIdComentario(comentario.getInt("code"));
                            comments.setNome(comentario.getString("first_name"));
                            comments.setNumeroMesesPostagem(comentario.getString("pretty_date"));
                            comments.setUrlFoto(comentario.getString("picture_url"));
                            comments.setTextoComentario(comentario.getString("text"));
                            comments.setRemetente(comentario.getString("addedBy"));
                            comments.setClassificacao(comentario.getInt("rating"));
                            //comments.setDataCriacao();
                            comentarios.add(comments);
                        }
                    }
                    feed.setComentarios(comentarios);

                    listaDadosFeeds.add((Feed) feed);
                    //Cria o adapter
                    if (adapterListView == null) {

                        adapterListView = new AdapterListViewFeed(this, listaDadosFeeds, mImageLoader);
                        listaFeed.setAdapter(adapterListView);
                    } else {
                        adapterListView.notifyDataSetChanged();

                    }
                    //Define o Adapter

                    //Cor quando a lista é selecionada para rolagem.
                    listaFeed.setCacheColorHint(Color.TRANSPARENT);
                    // remove a linha divisória entre os itens da lista
                    listaFeed.setDivider(null);
                    listaFeed.setDividerHeight(0);
                    listaFeed.setSelection(numRecord);
                }

            }
        } catch (JSONException e) {
            caixaDeMenssagem.dismiss();
            Log.d("Volley erro - Feed", e.getMessage());
            e.printStackTrace();
        }

        caixaDeMenssagem.dismiss();
    }


    public void obterNumNoti() {

        String url = String.format("%s%s/notification/count", Constantes.URL_SALVAR_OBJETIVOS_PREFERENCIAS,
                usuario.getIdUsuario());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Numero Notificacoes", response.toString());

                        try {
                            JSONObject numNoti = response.getJSONObject("objectData");


                            int notificationCountList = numNoti.getInt("numUnreadRecords");
                            SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();

                            editor.putInt("numNotificacoesPendentes", notificationCountList);
                            editor.commit();

                            if (notificationCountList > 0) {
                                badgeFeed.setVisibility(View.VISIBLE);
                                textoCirculoNotificacoes.setText(String.valueOf(notificationCountList));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("EE", "Error: " + error.getMessage());
                // hide the progress dialog

            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void enviaDeviceToken() {

        if(!deviceToken.isEmpty()) {
            Log.d("FEED","Enviando token");
            Log.d("FEED TOKEN",deviceToken);


            String url = String.format("%s/%s/device", Constantes.URL_CADASTRO_PERFIL, usuario.getIdUsuario());


            // Tag used to cancel the request
            //   String tag_json_obj = "json_obj_req";
            final String jsonObj = "{\"deviceToken\":\" " + deviceToken + "\"}";
            JSONObject jsonBody = null;
            try {
                jsonBody = new JSONObject(jsonObj);
            } catch (JSONException e) {
                VolleyLog.d("Volley EnviaDevice Erro1", e.getMessage());
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Volley EnviaDevice", response.toString());

                        }
                    }, new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Volley EnviaDevice Erro2", error.getMessage());
                }
            }) {

          /*  @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("deviceToken", deviceToken);
                return params;
            }*/

            };

// Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }


    //fabio
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (view.getId() == listaFeed.getId()) {
            if (listaFeed.getLastVisiblePosition() + 1 == listaDadosFeeds.size()) {


                if (numPage == totalPage) {
                    Log.i("ListaFeed", "não tem mais paginas");
                } else {
                    numPage = numPage + 1;
                    String url = String.format(pageString + String.valueOf(numPage), Constantes.URL_SALVAR_OBJETIVOS_PREFERENCIAS, String.valueOf(usuario.getIdUsuario()));

                    numRecord = listaDadosFeeds.size();
                    callJson2(url, true, false);


                }

            }

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
    public static void bloquearUser(String status){
        Log.d("BLOQUEIO 4","4 4");
        Log.d("BLOQUEIO",status);

        if(status.equalsIgnoreCase("SUBSCRIPTION_CANCELED")){
            Log.d("BLOQUEIO 5","5 5 BLOQUEANDO......");

        }else{

        }

    }

    public void abrirTelaBloqueio(){
        Intent intent = new Intent("plano_expirado");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void verificaBloqueio(String email){
        String url = Constantes.URL_API_REST+"/customer/getData?email="+email;
        Log.d("BLOQUEIO 1"," 1 1");

        Utf8JsonObjectRequest jsonObjReq = new Utf8JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject j) {
                        Log.d("BLOQUEIO 2","2 2");
                        JSONObject json = null;
                        try {
                            json = j.getJSONObject("objectData");
                            String res = json.getString("customerStatus");

                            Log.d("BLOQUEIO 3","3 3");
                            AtividadeFeed.bloquearUser(res);

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("EE", "Error: " + error.getMessage());

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, AtividadeFeed.class.getSimpleName());

        Log.d("BLOQUEIO 6", " 6 6DEPOS FORA");

    }




}

