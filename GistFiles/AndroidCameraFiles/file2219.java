package br.com.nutrichat.feed.cadastro;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.melnykov.fab.FloatingActionButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.nutrichat.R;
import br.com.nutrichat.classes.Alimentos;
import br.com.nutrichat.classes.Comentario;
import br.com.nutrichat.classes.Feed;
import br.com.nutrichat.classes.FeedEnvio;
import br.com.nutrichat.classes.Foto;
import br.com.nutrichat.classes.TipoComida;
import br.com.nutrichat.classes.TipoComidaFeed;
import br.com.nutrichat.classes.UsuarioCadastro;
import br.com.nutrichat.dao.repositorios.RepositorioUsuario;
import br.com.nutrichat.gui.IImportacaoExportacaoDados;
import br.com.nutrichat.net.Conexao;
import br.com.nutrichat.net.TarefaConexaoDados;
import br.com.nutrichat.perfil.nutricionistas.AdapterListView;
import br.com.nutrichat.plano.StatusSubscription;
import br.com.nutrichat.util.ActionSheet;
import br.com.nutrichat.util.Alertas;
import br.com.nutrichat.util.Constantes;
import br.com.nutrichat.util.Data;
import br.com.nutrichat.util.Fontes;
import br.com.nutrichat.util.ManipuladorImagens;
import br.com.nutrichat.util.MixpanelUtil;
import br.com.nutrichat.util.PermissionUtils;
import br.com.nutrichat.util.UtilObjetivoPreferecia;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.drakeet.materialdialog.MaterialDialog;
import mehdi.sakout.fancybuttons.FancyButton;

import static android.Manifest.permission.READ_CONTACTS;

/**
 *
 * Nutrichat - 2015
 *
 * Classe AtividadeNovoPost
 *
 * Esta classe define os métodos de manipulação dos elementos da activity de novo post.
 */
public class AtividadeNovoPost extends FragmentActivity implements ActionSheet.ActionSheetListener, IImportacaoExportacaoDados, AdapterView.OnItemClickListener {
    private Button campoHora;
    private Spinner spinner;
    private EditText campoTextoComentario;
    private TextView numeroCaracteres, modalTitulo;
    private int numeroCaracteresInserido = 0;
    private List<String> imagensAdicionadas;
    private TipoComida tipoComida;
    private List<TipoComida> listaTiposComida;
    private Calendar dataSelecionada;
    private SlideDateTimeListener listener;
    private boolean modoEdicaoFeed;
    private ProgressDialog caixaDialogoProcesso;
    private UsuarioCadastro usuario;
    private static final int SELECT_PICTURE = 1;
    private int column_index;
    private ImageView imagemPost1;
    private ImageView imagemPost2;
    private ImageView imagemPost3;
    private ImageView imagemRemoverPost1;
    private ImageView imagemRemoverPost2;
    private ImageView imagemRemoverPost3;
    private String selectedImagePath;
    private FloatingActionButton fab;
    //ADDED
    private String filemanagerstring;
    private Cursor cursor;
    private Feed post;
     public String[] item = new String[] {"Please search..."};

    private int contador = 0 ;
    private boolean foto1Livre = true,foto2Livre = true,foto3Livre = true;


    //
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String BITMAP_STORAGE_ANTIGA_KEY = "viewbitmapAntiga";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private Bitmap mImageBitmap;
    private Bitmap mImageBitmapAntiga;


    private String base64;
    private String base64Antiga;

    private ImageLoader mImageLoader;
    private SweetAlertDialog caixaDeMenssagem;
    private String url_foto;
    private EditText descricao;
    private TextView cancelarLabel;
    private int contFotos;
    private ListView listaAlimentos;
    private List<Alimentos> listaDadosAlimentos;
    private AdapterListViewAlimentos adapterListView;
    private TextView listaVazia;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 9;

    public static final int REQUEST_PERMISSIONS_CODE = 128;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private MaterialDialog mMaterialDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_novo_post);
        if(verificarPermissoes()) {
            // Define o título da barra de título personalizada
            TextView titulo = (TextView) findViewById(R.id.barra_nutrichat_novo_post_titulo);
            titulo.setText(R.string.titulo_atividade_novo_post);
            titulo.setTextSize(Constantes.TAMANHO_FONTE_TITULO_MENU);

            RelativeLayout rl = (RelativeLayout) findViewById(R.id.novo_post_id);

            if (savedInstanceState != null) {
                savedInstanceState.getString("base64key");
            }

            rl.requestFocus();

            //        fab = (FloatingActionButton) findViewById(R.id.fab);
            //        configuraFab();
            //configuraBuscaAlimentos();
            // configuraCheckbox();
            post = (Feed) getIntent().getSerializableExtra("post");
            url_foto = (String) getIntent().getSerializableExtra("url_foto");

            usuario = new RepositorioUsuario(this).listar().get(0);

            imagensAdicionadas = new ArrayList<String>();
            listaTiposComida = new ArrayList<TipoComida>();

            imagemPost1 = (ImageView) findViewById(R.id.atividade_novo_post_imagem_post_1);
            imagemPost2 = (ImageView) findViewById(R.id.atividade_novo_post_imagem_post_2);
            //imagemPost1.setScaleType(ImageView.ScaleType.FIT_XY);
            //imagemPost3 = (ImageView)findViewById(R.id.atividade_novo_post_imagem_post_3);

            imagemRemoverPost1 = (ImageView) findViewById(R.id.atividade_novo_post_icone_fechar_1);
            imagemRemoverPost2 = (ImageView) findViewById(R.id.atividade_novo_post_icone_fechar_2);
            // imagemRemoverPost3 = (ImageView)findViewById(R.id.atividade_novo_post_icone_fechar_3);

            //Botao cancelar
            cancelarLabel = (TextView) findViewById(R.id.cancelar_label);
            SpannableString content = new SpannableString("Cancelar");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            cancelarLabel.setText(content);
            cancelarLabel.setTextColor(Color.BLACK);

            campoHora = (Button) findViewById(R.id.atividade_novo_post_seletor_data_hora);
            Calendar dataHora = new GregorianCalendar();
            campoHora.setText(String.format("%02d/%02d/%04d %02d:%02d",
                    dataHora.get(Calendar.DAY_OF_MONTH), dataHora.get(Calendar.MONTH) + 1,
                    dataHora.get(Calendar.YEAR), dataHora.get(Calendar.HOUR_OF_DAY),
                    dataHora.get(Calendar.MINUTE)));
            campoHora.setTextSize(16);
            campoHora.setTextColor(Color.DKGRAY);


            dataSelecionada = dataHora;
            try {
                if (!post.getDataPostagem().isEmpty())
                    dataSelecionada = Data.StringToCalendar(post.getDataPostagem());
            } catch (NullPointerException e) {
                dataSelecionada = dataHora;
            }

            campoTextoComentario = (EditText) findViewById(
                    R.id.atividade_novo_post_campo_descricao);
            numeroCaracteres = (TextView) findViewById(
                    R.id.atividade_novo_post_texto_numero_caracteres);
            campoTextoComentario.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
            campoTextoComentario.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    numeroCaracteresInserido = start + after;
                    numeroCaracteres.setText(String.format("%d/200", numeroCaracteresInserido));
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            Fontes.alterarFonte(this, titulo);
            Fontes.alterarFonte(this, campoTextoComentario);
            Fontes.alterarFonte(this, numeroCaracteres);
            Fontes.alterarFonte(this, campoHora);
            Fontes.alterarFonte(this, cancelarLabel);
            Fontes.alterarFonte(this, (TextView) findViewById(R.id.label_oque_comi));
            Fontes.alterarFonte(this, (TextView) findViewById(R.id.label_fotos_refeicao));
            Fontes.alterarFonte(this, (TextView) findViewById(R.id.botao_salvar_refeicao));

            configuraImageLoader();
            configuralistaAlimentos();


            obterTiposComidas();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

       
            if(UtilObjetivoPreferecia.getAlimentos().getTitulo() != null)
            {
                Alimentos a = new Alimentos();
                a.setId(UtilObjetivoPreferecia.getAlimentos().getId());
                a.setPeso(UtilObjetivoPreferecia.getAlimentos().getPeso());
                String titulo = UtilObjetivoPreferecia.getAlimentos().getTitulo();
                if(titulo.length()>39)
                {
                    a.setTitulo(titulo.substring(0,39)+"...");
                }else{
                    a.setTitulo(titulo);
                }

                Log.d("ALIMENTOS HEI", "-->" + listaAlimentos.getHeight());
                ViewGroup.LayoutParams params = listaAlimentos.getLayoutParams();
                params.height = listaAlimentos.getHeight()+130;
                listaAlimentos.setLayoutParams(params);
                listaAlimentos.requestLayout();
                listaDadosAlimentos.add(a);
                adapterListView.notifyDataSetChanged();

                if(listaDadosAlimentos.isEmpty()){
                    configuraBorda();
                }else{
                    configuraRemoveBorda();
                }

                Log.d("ALIMENTOS NOVO", "Alimento: " + UtilObjetivoPreferecia.getAlimentos().getTitulo() + " Gramas: " + UtilObjetivoPreferecia.getAlimentos().getPeso());
                UtilObjetivoPreferecia.setaAlimento(new Alimentos());
            }
      
    }

    public void configuraBorda(){

        RectShape rect = new RectShape();

        ShapeDrawable rectShapeDrawable = new ShapeDrawable(rect);

        Paint paint = rectShapeDrawable.getPaint();

        paint.setColor(Color.parseColor("#eeeeee"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.view_lista_alimetos);
        rl.setBackgroundDrawable(rectShapeDrawable);
    }

    public void configuraRemoveBorda(){

        RectShape rect = new RectShape();

        ShapeDrawable rectShapeDrawable = new ShapeDrawable(rect);

        Paint paint = rectShapeDrawable.getPaint();

        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);


        RelativeLayout rl = (RelativeLayout) findViewById(R.id.view_lista_alimetos);
        rl.setBackgroundDrawable(rectShapeDrawable);
    }


    public void configuraImageLoader(){

        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.nophoto_post)
                .showImageOnFail(R.drawable.icon_camera)
                .showImageOnLoading(R.color.branco)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                        //.displayer(new FadeInBitmapDisplayer(1000))
                .build();

        ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(AtividadeNovoPost.this)
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(2 * 1024 * 1024)
                .threadPoolSize(5)
                .writeDebugLogs()
                .build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);
    }
    private void obterTiposComidas(){

        spinner = (Spinner) findViewById(R.id.atividade_novo_post_seletor_tipo_comida);

        List<String> lista = new ArrayList<String>();
        TipoComida tipoComida1 = new TipoComida();

        tipoComida1.setIdTipoComida(1);
        tipoComida1.setNome("Café da Manhã");
        tipoComida1.setDescricao("Café da Manhã");
        tipoComida1.setTag("BREAKFAST");
        lista.add(tipoComida1.getNome());
        listaTiposComida.add(tipoComida1);

        TipoComida tipoComida2 = new TipoComida();
        tipoComida2.setIdTipoComida(2);
        tipoComida2.setNome("Lanche da Manhã");
        tipoComida2.setDescricao("Lanche da Manhã");
        tipoComida2.setTag("MORNING_SNACK");
        lista.add(tipoComida2.getNome());
        listaTiposComida.add(tipoComida2);

        TipoComida tipoComida3 = new TipoComida();
        tipoComida3.setIdTipoComida(3);
        tipoComida3.setNome("Almoço");
        tipoComida3.setDescricao("Almoço");
        tipoComida3.setTag("LUNCH");
        lista.add(tipoComida3.getNome());
        listaTiposComida.add(tipoComida3);

        TipoComida tipoComida4 = new TipoComida();
        tipoComida4.setIdTipoComida(4);
        tipoComida4.setNome("Lanche da Tarde");
        tipoComida4.setDescricao("Lanche da Tarde");
        tipoComida4.setTag("AFTERNOON_SNACK");
        lista.add(tipoComida4.getNome());
        listaTiposComida.add(tipoComida4);

        TipoComida tipoComida5 = new TipoComida();
        tipoComida5.setIdTipoComida(5);
        tipoComida5.setNome("Janta");
        tipoComida5.setDescricao("Janta");
        tipoComida5.setTag("DINNER");
        lista.add(tipoComida5.getNome());
        listaTiposComida.add(tipoComida5);

        TipoComida tipoComida6 = new TipoComida();
        tipoComida6.setIdTipoComida(6);
        tipoComida6.setNome("Ceia");
        tipoComida6.setDescricao("Ceia");
        tipoComida6.setTag("NIGHT_SNACK");
        lista.add(tipoComida6.getNome());
        listaTiposComida.add(tipoComida6);

        TipoComida tipoComida7 = new TipoComida();
        tipoComida7.setIdTipoComida(7);
        tipoComida7.setNome("Pré-Treino");
        tipoComida7.setDescricao("Pré-Treino");
        tipoComida7.setTag("PRE_WORKOUT");
        lista.add(tipoComida7.getNome());
        listaTiposComida.add(tipoComida7);

        TipoComida tipoComida8 = new TipoComida();
        tipoComida8.setIdTipoComida(8);
        tipoComida8.setNome("Pós-Treino");
        tipoComida8.setDescricao("Pós-Treino");
        tipoComida8.setTag("POST_WORKOUT");
        lista.add(tipoComida8.getNome());
        listaTiposComida.add(tipoComida8);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.item_seletor_tipo_comida, lista){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "fonts/Raleway_500.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(16);
                ((TextView) v).setTextColor(Color.DKGRAY);

                return v;
            }


            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "fonts/Raleway_500.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(16);
                return v;
            }
        };
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoComida = listaTiposComida.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(post.getIdFeed() != 0){
            modoEdicaoFeed = true;
            int indice = 0;
            for(TipoComida tp: listaTiposComida){
                if(tp.getIdTipoComida() == post.getTiposComida().getIdTipoComida()){
                    tipoComida = tp;
                    spinner.setSelection(indice);
                    break;
                }
                indice++;
            }
            campoTextoComentario.setText(post.getDescricao());
            Calendar calendar = Data.StringToCalendar(post.getDataPostagem());
            campoHora.setText(String.format("%02d/%02d/%04d %02d:%02d",
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)));

            numeroCaracteresInserido = post.getDescricao().length();
            numeroCaracteres.setText(String.format("%d/200", numeroCaracteresInserido));

            // foto do post
            int cont = 0;
            for(Foto foto: post.getFotos()) {


                if(cont == 0) {
                    foto1Livre = false;
                    imagemPost1.setScaleType(ImageView.ScaleType.FIT_XY);
                    mImageLoader.displayImage(post.getFotos().get(0).getUrlFoto(),
                            imagemPost1,
                            null,
                            new ImageLoadingListener() {

                                @Override
                                public void onLoadingCancelled(String uri, View view) {
                                    Log.i("Script", "onLoadingCancelled()");
                                }

                                @Override
                                public void onLoadingComplete(String uri, View view, Bitmap bmp) {
                                    Log.i("Script", "onLoadingComplete()");
                                   // BitmapDrawable drawable = (BitmapDrawable) imagemPost1.getDrawable();
                                    //Bitmap bitmap = drawable.getBitmap();
                                    List<String> listaFotoBase64 = new ArrayList<String>();
                                    listaFotoBase64 = UtilObjetivoPreferecia.getFotoBase64();

                                    //String base64 = ManipuladorImagens.converterBitmapToBase64(bitmap);

                                    imagensAdicionadas.add(0, listaFotoBase64.get(0));
                                }

                                @Override
                                public void onLoadingFailed(String uri, View view, FailReason fail) {
                                    Log.i("Script", "onLoadingFailed(" + fail + ")");
                                }

                                @Override
                                public void onLoadingStarted(String uri, View view) {
                                    Log.i("Script", "onLoadingStarted()");
                                }

                            }, new ImageLoadingProgressListener() {
                                @Override
                                public void onProgressUpdate(String uri, View view, int current, int total) {
                                    Log.i("Script", "onProgressUpdate(" + uri + " : " + total + " : " + current + ")");
                                }
                            });
                    //imagemPost1.setImageBitmap(bitmapImage);
                    imagemPost1.setVisibility(View.VISIBLE);
                    imagemRemoverPost1.setVisibility(View.VISIBLE);

                }else if(cont == 1){
                    foto2Livre = false;
                    imagemPost2.setScaleType(ImageView.ScaleType.FIT_XY);
                    mImageLoader.displayImage(post.getFotos().get(1).getUrlFoto(),
                            imagemPost2,
                            null,
                            new ImageLoadingListener() {

                                @Override
                                public void onLoadingCancelled(String uri, View view) {
                                    Log.i("Script", "onLoadingCancelled()");
                                }

                                @Override
                                public void onLoadingComplete(String uri, View view, Bitmap bmp) {
                                    Log.i("Script", "onLoadingComplete()");
//                                    BitmapDrawable drawable = (BitmapDrawable) imagemPost2.getDrawable();
 //                                   Bitmap bitmap = drawable.getBitmap();
                                    //String base64 = ManipuladorImagens.converterBitmapToBase64(bitmap);
                                    List<String> listaFotoBase64 = new ArrayList<String>();
                                    listaFotoBase64 = UtilObjetivoPreferecia.getFotoBase64();

                                    if(imagensAdicionadas.size() == 0){
                                        imagensAdicionadas.add(0, listaFotoBase64.get(1));
                                        //imagensAdicionadas.add(0, base64);
                                    }else {
                                        imagensAdicionadas.add(1, listaFotoBase64.get(1));
                                        //imagensAdicionadas.add(1, base64);
                                    }
                                }

                                @Override
                                public void onLoadingFailed(String uri, View view, FailReason fail) {
                                    Log.i("Script", "onLoadingFailed(" + fail + ")");
                                }

                                @Override
                                public void onLoadingStarted(String uri, View view) {
                                    Log.i("Script", "onLoadingStarted()");
                                }

                            }, new ImageLoadingProgressListener() {
                                @Override
                                public void onProgressUpdate(String uri, View view, int current, int total) {
                                    Log.i("Script", "onProgressUpdate(" + uri + " : " + total + " : " + current + ")");
                                }
                            });
                   // imagemPost2.setImageBitmap(bitmapImage);
                    imagemPost2.setVisibility(View.VISIBLE);
                    imagemRemoverPost2.setVisibility(View.VISIBLE);
                } else if(cont == 2){
                    foto3Livre = false;
                    mImageLoader.displayImage(post.getFotos().get(2).getUrlFoto(),
                            imagemPost3,
                            null,
                            new ImageLoadingListener() {

                                @Override
                                public void onLoadingCancelled(String uri, View view) {
                                    Log.i("Script", "onLoadingCancelled()");
                                }

                                @Override
                                public void onLoadingComplete(String uri, View view, Bitmap bmp) {
                                    Log.i("Script", "onLoadingComplete()");
                                    BitmapDrawable drawable = (BitmapDrawable) imagemPost3.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    String base64 = ManipuladorImagens.converterBitmapToBase64(bitmap);
                                    if(imagensAdicionadas.size() == 0){
                                        imagensAdicionadas.add(0, base64);
                                    }else if(imagensAdicionadas.size() == 1) {
                                        imagensAdicionadas.add(1, base64);
                                    }else {
                                        imagensAdicionadas.add(2, base64);
                                    }
                                }

                                @Override
                                public void onLoadingFailed(String uri, View view, FailReason fail) {
                                    Log.i("Script", "onLoadingFailed(" + fail + ")");
                                }

                                @Override
                                public void onLoadingStarted(String uri, View view) {
                                    Log.i("Script", "onLoadingStarted()");
                                }

                            }, new ImageLoadingProgressListener() {
                                @Override
                                public void onProgressUpdate(String uri, View view, int current, int total) {
                                    Log.i("Script", "onProgressUpdate(" + uri + " : " + total + " : " + current + ")");
                                }
                            });
                    imagemPost3.setVisibility(View.VISIBLE);
                    imagemRemoverPost3.setVisibility(View.VISIBLE);

                }

                cont++;
            }



        }else{
            spinner.setSelection(0);
            tipoComida = listaTiposComida.get(0);
            modoEdicaoFeed = false;
            // para add uma foto assim que abre para fazer um novo post
            setTheme(R.style.ActionSheetStyleIOS7);
            showActionSheet();
        }

        ImageView imagemUsuario = (ImageView)findViewById(
                R.id.atividade_novo_post_foto_usuario);
        // foto do usuário

        Log.d("TAG BUG",usuario.toString());

        if(url_foto != null && !url_foto.isEmpty()){
            ImageView imagemUsuarioSemFoto = (ImageView)findViewById(
                    R.id.atividade_novo_post_foto_usuario_sem_foto);
            imagemUsuarioSemFoto.setVisibility(View.INVISIBLE);
            colocarFoto(url_foto, imagemUsuario);

        }
    }

   /* @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        TextView tv = (TextView) v;

        // Buscar alguma view dentro deste item
        TextView tv2 = (TextView) v.findViewById(R.id.item_lista_busca_alimento_descricao);

    } */

    public void selecionarHora(View v) {
        listener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date){
                // Do something with the date. This Date object contains
                // the date and time that the user has selected.
                Calendar calendar = Data.DateToCalendar(date);
                //time = calendar.getTimeInMillis();
                campoHora.setText(String.format("%02d/%02d/%04d %02d:%02d",
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)));
                dataSelecionada = calendar;
            }

            @Override
            public void onDateTimeCancel() {
                // Overriding onDateTimeCancel() is optional.
            }
        };
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
            .setListener(listener)
            .setIs24HourTime(true)
            .setTheme(SlideDateTimePicker.HOLO_LIGHT)
            .setInitialDate(new Date())
            .build()
            .show();
    }

    public void selecionarFoto(View componente) {
        if(imagensAdicionadas.size() < 2) {

            setTheme(R.style.ActionSheetStyleIOS7);
            showActionSheet();
        }
    }

    public void showActionSheet() {
        FragmentManager transaction = getSupportFragmentManager();
        ActionSheet.createBuilder(this, transaction)
                .setCancelButtonTitle("Cancelar")
                .setOtherButtonTitles("Usar foto existente", "Tirar uma foto")
                .setCancelableOnTouchOutside(true).setListener(this).show();
    }
    private String pictureImagePath = "";
    public void abrirCamera() {
        contFotos++;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").format(new Date());
        String imageFileName = timeStamp + contFotos + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        0);
            }
        }

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, 0);
        if(foto1Livre){
            imagemPost1.setScaleType(ImageView.ScaleType.FIT_XY);
        } else if(foto2Livre){
            imagemPost2.setScaleType(ImageView.ScaleType.FIT_XY);
        }


    }

    public void abrirFotos() {
        if( ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ){

            if( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.READ_EXTERNAL_STORAGE ) ){
                callDialog( "É preciso a permission READ_EXTERNAL_STORAGE para apresentação do content.", new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} );
            }
            else{
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE );
            }
        }
        else{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);
            if(foto1Livre){
                imagemPost1.setScaleType(ImageView.ScaleType.FIT_XY);
            } else if(foto2Livre){
                imagemPost2.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }

    }

    private boolean verificarPermissoes() {
        int permissionCamera = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
        int permissionReadSd = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteSd = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (permissionReadSd != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissionWriteSd != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    public void clickFoto(View v){
        if (usuario.getCustomerStatus().equalsIgnoreCase(StatusSubscription.FREE_TRIAL_EXPIRED.name()) ||
                usuario.getCustomerStatus().equalsIgnoreCase(StatusSubscription.SUBSCRIPTION_CANCELED.name())) {
            Intent intent = new Intent("plano_expirado");
            startActivity(intent);
        } else {
            selecionarFoto(v);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Uri selectedImageUri = null;
            String base64 = null;
            Bitmap camera = null;
            if (mImageBitmap != null && mImageBitmap.isRecycled()) {
                mImageBitmap = null;
            }

            if (requestCode == 0) {
                File imgFile = new File(pictureImagePath);
                if (imgFile.exists()) {
                    camera = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    base64 = ManipuladorImagens.converterBitmapToBase64(
                            ManipuladorImagens.redimensionarImagem(camera));
                    this.base64 = base64;
                }
            } else {
                selectedImageUri = data.getData();
                filemanagerstring = selectedImageUri.getPath();
                selectedImagePath = getPath(selectedImageUri);

                base64 = ManipuladorImagens.converterBitmapToBase64(
                        ManipuladorImagens.redimensionarImagem(BitmapFactory.decodeFile(selectedImagePath)));
                this.base64 = base64;


            }


            //if (imagensAdicionadas.size() == 0) {
            if (foto1Livre) {
                foto1Livre= false;
                if (selectedImageUri != null)
                    imagemPost1.setImageBitmap(ManipuladorImagens.redimensionarImagem(BitmapFactory.decodeFile(selectedImagePath)));
                else
                    imagemPost1.setImageBitmap(ManipuladorImagens.redimensionarImagem(camera));

                imagemPost1.setVisibility(View.VISIBLE);
                imagemRemoverPost1.setVisibility(View.VISIBLE);
                imagensAdicionadas.add(0, base64);

           // } else if (imagensAdicionadas.size() == 1) {
            } else if (foto2Livre) {
                foto2Livre= false;
                if (selectedImageUri != null)
                    imagemPost2.setImageBitmap(ManipuladorImagens.redimensionarImagem(BitmapFactory.decodeFile(selectedImagePath)));
                else
                    imagemPost2.setImageBitmap(ManipuladorImagens.redimensionarImagem(camera));

//                imagemPost2.setScaleType(ImageView.ScaleType.FIT_XY);
                imagemPost2.setVisibility(View.VISIBLE);
                imagemRemoverPost2.setVisibility(View.VISIBLE);
                imagensAdicionadas.add(1, base64);

           // } else if (imagensAdicionadas.size() == 2) {
            } else if (foto3Livre) {
                foto3Livre= false;
                if (selectedImageUri != null)
                    imagemPost3.setImageBitmap(ManipuladorImagens.redimensionarImagem(BitmapFactory.decodeFile(selectedImagePath)));
                else
                    imagemPost3.setImageBitmap(ManipuladorImagens.redimensionarImagem(camera));

                imagemPost3.setVisibility(View.VISIBLE);
                imagemRemoverPost3.setVisibility(View.VISIBLE);
                imagensAdicionadas.add(2, base64);
               // //fab.setVisibility(View.INVISIBLE);
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(Uri uri) {
        final Context context = this.getApplicationContext();
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public void removerImagem(View componente) {
        int indice = Integer.parseInt(componente.getTag().toString());
       // imagensAdicionadas.remove(indice);
        contador--;
        if(indice == 0){
            foto1Livre = true;
            imagensAdicionadas.remove(0);
            imagemPost1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imagemPost1.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo, getApplicationContext().getTheme()));
            } else {
                imagemPost1.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo));
            }

            imagemRemoverPost1.setVisibility(View.INVISIBLE);
            //fab.setVisibility(View.VISIBLE);
        }else if(indice == 1){
            foto2Livre = true;
            if(imagensAdicionadas.size() >= 2){
               imagensAdicionadas.remove(1);
           }else if(imagensAdicionadas.size() == 1){
               imagensAdicionadas.remove(0);
           }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imagemPost2.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo, getApplicationContext().getTheme()));
            } else {
                imagemPost2.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo));
            }
            imagemPost2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
           // imagemPost2.setBackground(getResources().getDrawable(R.drawable.ic_add_a_photo));
            imagemRemoverPost2.setVisibility(View.INVISIBLE);
            //fab.setVisibility(View.VISIBLE);

        }else if(indice == 2){
            foto3Livre = true;
            if(imagensAdicionadas.size() >= 3){
                imagensAdicionadas.remove(2);
            }else if(imagensAdicionadas.size() == 2){
                imagensAdicionadas.remove(1);
            }

            imagemPost3.setVisibility(View.INVISIBLE);
            imagemRemoverPost3.setVisibility(View.INVISIBLE);
            //fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void comecouExecucao() {

    }

    @Override
    public void mostrarMensagem(String mensagem) {
        //caixaDialogoProcesso.setMessage(mensagem);
    }

    @Override
    public void terminouExecucao(ArrayList<Object> listaDados, String mensagemRetorno) {

       // caixaDeMenssagem.dismiss();
        Alertas.salvoComSucessoNovaTela("Post salvo com sucesso","feed", this, caixaDeMenssagem, usuario.getEmail());
        if(mensagemRetorno.contains("200")) {
       //  Alertas.salvoComSucessoNovaTela("Post salvo com sucesso","feed", this, caixaDeMenssagem);
        }else if(mensagemRetorno.equalsIgnoreCase("OK")){


        }
    }

    public void salvarDados(View componente){
        boolean camposValidos = true;
        descricao = (EditText)findViewById(R.id.atividade_novo_post_campo_descricao);


        if(imagensAdicionadas.isEmpty() && listaDadosAlimentos.size() < 1){
            camposValidos = false;
            caixaDeMenssagem = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            caixaDeMenssagem.setTitleText("Mais informações");
            caixaDeMenssagem.setContentText("Inclua fotos e alimentos da sua refeição para ajudar a avaliaçao de sua nutricionista");
            caixaDeMenssagem.setConfirmText("+ Alimento");
            caixaDeMenssagem.setCancelText("+ Foto");
            caixaDeMenssagem.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    //  caixaDeMenssagem.dismiss();
                    setTheme(R.style.ActionSheetStyleIOS7);
                    showActionSheet();
                    caixaDeMenssagem.dismiss();
                }
            });
            caixaDeMenssagem.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    caixaDeMenssagem.dismiss();
                    Intent intent = new Intent("busca_alimentos");
                    startActivity(intent);
                }
            });
            caixaDeMenssagem.show();



        }else if(descricao.getText().toString().length() == 0  ) {
            Alertas.menssagemDeErro("Forneça uma descrição válida!", this, usuario.getEmail());
            camposValidos = false;

        }

        if(camposValidos){

            if(imagensAdicionadas.isEmpty()){
                verificaSeTemFoto();
            }else{
                enviaPost();
            }

        }
    }

    public void cancelarDados(View componente){
        finish();
    }

    public void voltar(View componente){
        finish();
    }

    public void verificaSeTemFoto(){

        caixaDeMenssagem = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        caixaDeMenssagem.setTitleText("Incluir uma foto?");
        caixaDeMenssagem.setContentText("Inclua uma foto da sua refeição, isso vai ajudar a avaliação da sua nutricionista");
        caixaDeMenssagem.setConfirmText("Enviar sem foto");
        caixaDeMenssagem.setCancelText("Incluir");
        caixaDeMenssagem.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                //  caixaDeMenssagem.dismiss();
                setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet();
                caixaDeMenssagem.dismiss();
            }
        });
        caixaDeMenssagem.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                caixaDeMenssagem.dismiss();
                enviaPost();
            }
        });
        caixaDeMenssagem.show();

    }

    public void enviaPost(){


        FeedEnvio feed = new FeedEnvio();
        feed.setTitulo(descricao.getText().toString());
        feed.setDescricao(descricao.getText().toString());
        TipoComidaFeed tpComida = new TipoComidaFeed();
        tpComida.setIdTipoComida(tipoComida.getIdTipoComida());
        feed.setTipoComida(tpComida);
        feed.setDataPostagem(String.format("%02d-%02d-%04d %02d:%02d:%02d",
                dataSelecionada.get(Calendar.DAY_OF_MONTH), dataSelecionada.get(Calendar.MONTH) + 1,
                dataSelecionada.get(Calendar.YEAR), dataSelecionada.get(Calendar.HOUR_OF_DAY),
                dataSelecionada.get(Calendar.MINUTE), dataSelecionada.get(Calendar.SECOND)));
        List<Foto> fotos = new ArrayList<Foto>();
        for(String str: imagensAdicionadas){
            Foto f = new Foto();
            f.setUrlFoto(str);
            f.setTitulo(descricao.getText().toString());
            f.setDataCriacaoFoto(new Timestamp(new GregorianCalendar().getTimeInMillis()));
            fotos.add(f);
        }

        List<Alimentos> foods = new ArrayList<Alimentos>();
        for (Alimentos a :listaDadosAlimentos) {
            Alimentos ali = new Alimentos();
            ali.setPeso(a.getPeso());
            ali.setId(a.getId());
            foods.add(ali);
        }

        feed.setAlimentos(foods);
        feed.setFotos(fotos);
        if(Conexao.verificaConexao(this)){
            if(modoEdicaoFeed){
                caixaDeMenssagem = Alertas.barraProgresso("Salvando post", this, caixaDeMenssagem);


                Log.d("SALVANDO----->",feed.getDataPostagem());

                TarefaConexaoDados tarefa = new TarefaConexaoDados(this,TarefaConexaoDados.EDITAR_POST, feed);
                tarefa.execute(String.valueOf(usuario.getIdUsuario()),
                        String.valueOf(post.getIdFeed()));
            }else{
                MixpanelUtil.trackPost(usuario.getEmail(),this);
                caixaDeMenssagem = Alertas.barraProgresso("Salvando post", this, caixaDeMenssagem);
                TarefaConexaoDados tarefa = new TarefaConexaoDados(this,
                        TarefaConexaoDados.NOVO_POST, feed);
                tarefa.execute(String.valueOf(usuario.getIdUsuario()));
            }
        }else{
            Alertas.menssagemDeErroVoltar("Não há conexão com a internet", this, this,usuario.getEmail());
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (index == 0){
            abrirFotos();
        }else if (index == 1){
            abrirCamera();
        }
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putParcelable(BITMAP_STORAGE_ANTIGA_KEY, mImageBitmapAntiga);
        outState.putStringArrayList("imagensAdicionadasKey", (ArrayList<String>) imagensAdicionadas);
        outState.putString("base64key", base64);
        outState.putString("base64Antigakey", base64Antiga);
        outState.putInt("contadorKey", contador);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageBitmapAntiga = savedInstanceState.getParcelable(BITMAP_STORAGE_ANTIGA_KEY);
        base64 = savedInstanceState.getString("base64key");
        base64Antiga = savedInstanceState.getString("base64Antigakey");
        contador = savedInstanceState.getInt("contadorKey");
        imagensAdicionadas = savedInstanceState.getStringArrayList("imagensAdicionadasKey");
      //  imagemPost1 = (ImageView) savedInstanceState.getParcelable("image1");

        if(base64 != null ){

            if(contador == 1){
                if(base64 != base64Antiga){
                    base64Antiga = base64;
                    mImageBitmapAntiga = mImageBitmap;
                    imagensAdicionadas.add(0, base64);
                    imagemPost1.setImageBitmap(mImageBitmap);
                    imagemRemoverPost1.setVisibility(ImageView.VISIBLE);
                    imagemPost1.setVisibility(
                            savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                                    ImageView.VISIBLE : ImageView.INVISIBLE
                    );
                }

            }else if(contador == 2){
                if(base64 != base64Antiga){
                    base64Antiga = base64;
                    imagensAdicionadas.add(1, base64);
                    imagemPost1.setImageBitmap(mImageBitmapAntiga);
                    imagemRemoverPost1.setVisibility(ImageView.VISIBLE);
                    imagemRemoverPost2.setVisibility(ImageView.VISIBLE);
                    imagemPost2.setImageBitmap(mImageBitmap);
                    imagemPost2.setVisibility(
                        savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                                ImageView.VISIBLE : ImageView.INVISIBLE
                    );
                }
            }else if(contador == 3){
                if(base64 != base64Antiga){
                    base64Antiga = base64;
                    imagensAdicionadas.add(2,base64);
                    imagemPost2.setImageBitmap(mImageBitmapAntiga);
                    imagemRemoverPost2.setVisibility(ImageView.VISIBLE);
                    imagemRemoverPost3.setVisibility(ImageView.VISIBLE);
                    imagemPost3.setImageBitmap(mImageBitmap);
                    imagemPost3.setVisibility(
                            savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                                    ImageView.VISIBLE : ImageView.INVISIBLE
                    );
                }
            }
        }



    }

    public void colocarFoto(String caminho, ImageView imagem){


        mImageLoader.displayImage(caminho,
                imagem,
                null,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingCancelled(String uri, View view) {
                        Log.i("Script", "onLoadingCancelled()");
                    }

                    @Override
                    public void onLoadingComplete(String uri, View view, Bitmap bmp) {
                        Log.i("Script", "onLoadingComplete()");
                    }

                    @Override
                    public void onLoadingFailed(String uri, View view, FailReason fail) {
                        Log.i("Script", "onLoadingFailed(" + fail + ")");
                    }

                    @Override
                    public void onLoadingStarted(String uri, View view) {
                        Log.i("Script", "onLoadingStarted()");
                    }

                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String uri, View view, int current, int total) {
                        Log.i("Script", "onProgressUpdate(" + uri + " : " + total + " : " + current + ")");
                    }
                });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
		/*mImageLoader.clearMemoryCache();
		mImageLoader.clearDiskCache();*/

        ImageLoader.getInstance().destroy();
        //mImageLoader.stop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    static class ViewHolder {

        ImageView imageView;
        CircularProgressBar progressBar;


    }

    public void configuralistaAlimentos(){

        listaAlimentos = (ListView)findViewById(R.id.atividade_novo_post_lista_refeicao);
        listaAlimentos.setOnItemClickListener(this);
        listaVazia =  (TextView) findViewById(R.id.listavazia);
        listaVazia.setTextColor(Color.DKGRAY);
        Fontes.alterarFonte(this, listaVazia);
        listaAlimentos.setEmptyView((TextView) findViewById(R.id.listavazia));

        listaDadosAlimentos = new ArrayList<Alimentos>();
       // listaDadosAlimentos.add(null);

        if(post.getIdFeed() != 0) {
            for (Alimentos a : this.post.getAlimentos()) {
                listaDadosAlimentos.add(a);

            }

            int valor = 130;
            valor = valor * post.getAlimentos().size();
            ViewGroup.LayoutParams params = listaAlimentos.getLayoutParams();
            params.height = listaAlimentos.getHeight()+valor;
            listaAlimentos.setLayoutParams(params);
        }

        if(listaDadosAlimentos.isEmpty()){
            configuraBorda();
        }else{
            configuraRemoveBorda();
        }
        //Cria o adapter

        if (adapterListView == null) {

            adapterListView = new AdapterListViewAlimentos(this, listaDadosAlimentos);
            listaAlimentos.setAdapter(adapterListView);
        } else {
            adapterListView.notifyDataSetChanged();

        }

        //Define o Adapter

        //Cor quando a lista é selecionada para rolagem.
        listaAlimentos.setCacheColorHint(Color.TRANSPARENT);
    }

    public void addAlimentoNaLista(View v){

        Intent intent = new Intent("busca_alimentos");
        startActivity(intent);

    }



    public void removeAlimentoLista(final View v){

        caixaDeMenssagem = new SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEGATIVE);
        caixaDeMenssagem.setTitleText("Excluir o alimento ?");
        //caixaDeMenssagem.setContentText("Inclua uma foto da sua refeição, isso vai ajudar a avaliação da sua nutricionista");
        caixaDeMenssagem.setConfirmText("Sim");
        caixaDeMenssagem.setCancelText("Não");
        caixaDeMenssagem.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                //  caixaDeMenssagem.dismiss();
                setTheme(R.style.ActionSheetStyleIOS7);
                caixaDeMenssagem.dismiss();
            }
        });
        caixaDeMenssagem.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                // remove item da lista
                Object toRemove = adapterListView.getItem((Integer) v.getTag());
                listaDadosAlimentos.remove(toRemove);
                adapterListView.notifyDataSetChanged();

                ViewGroup.LayoutParams params = listaAlimentos.getLayoutParams();
                params.height = listaAlimentos.getHeight() - 100;
                listaAlimentos.setLayoutParams(params);
                listaAlimentos.requestLayout();

                if(listaDadosAlimentos.isEmpty()){
                    configuraBorda();
                }else{
                    configuraRemoveBorda();
                }

                caixaDeMenssagem.dismiss();
            }
        });
        caixaDeMenssagem.show();


    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void callDialog( String message, final String[] permissions ){
        mMaterialDialog = new MaterialDialog(this)
                .setTitle("Permission")
                .setMessage( message )
                .setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ActivityCompat.requestPermissions(AtividadeNovoPost.this, permissions, REQUEST_PERMISSIONS_CODE);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch( requestCode ){
            case REQUEST_PERMISSIONS_CODE:
                for( int i = 0; i < permissions.length; i++ ){

                     if( permissions[i].equalsIgnoreCase( Manifest.permission.WRITE_EXTERNAL_STORAGE )
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED ){

                         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                         intent.setType("image/*");
                         intent.addCategory(Intent.CATEGORY_OPENABLE);
                         startActivityForResult(intent, 1);
                         if(foto1Livre){
                             imagemPost1.setScaleType(ImageView.ScaleType.FIT_XY);
                         } else if(foto2Livre){
                             imagemPost2.setScaleType(ImageView.ScaleType.FIT_XY);
                         }

                        //createDeleteFolder();
                    }
                    else if( permissions[i].equalsIgnoreCase( Manifest.permission.READ_EXTERNAL_STORAGE )
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED ){

                         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                         intent.setType("image/*");
                         intent.addCategory(Intent.CATEGORY_OPENABLE);
                         startActivityForResult(intent, 1);
                         if(foto1Livre){
                             imagemPost1.setScaleType(ImageView.ScaleType.FIT_XY);
                         } else if(foto2Livre){
                             imagemPost2.setScaleType(ImageView.ScaleType.FIT_XY);
                         }
                       // readFile(Environment.getExternalStorageDirectory().toString() + "/myFolder");
                    }
                }
                break;
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("PERMISSION MA", "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("PERMISSION MA", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    verificarPermissoes();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
