package com.example.cirugias;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.R.string;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UpdateLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ZoomButton;
import android.widget.ZoomControls;
public class MainActivity<PageIndicator> extends Activity {
	private ViewPager vp;
	private RelativeLayout p0;
	private RelativeLayout p1;
	private RelativeLayout p2;
	private RelativeLayout p3;
	private EditText n1, n2, n3, n4, n5, n6,et;
	private WebView tvs;
	private Calendar c1;
	private EditText et1, et3, et4,et2,et5,et6,etn;
	private adaptadorListado al;
	private adaptadorListadon al1;
	private ListView lv1,lv2;
	private ArrayList<cirugias> listacirugias;
	private ArrayList<pacientes> listapacientes;
	private Spinner sp1;
	private String sn="idCirugia";
	private int n=0,nn1=0;
	private String names="juan";
	private int log=0;
	private SearchView etn1;
	private float zoomb=100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences preferences =getSharedPreferences("cargas", Context.MODE_PRIVATE);
	String sp=preferences.getString("carga","");
	if(sp.length()>0)
	{
		log=Integer.parseInt(sp);
	}
	c1 = new GregorianCalendar();//metodo con el cual obtengo el calendario
		vp = (ViewPager) findViewById(R.id.view);
		vp.setAdapter(new MainPageAdapter());
		vp.setCurrentItem(0);

		
		

	}
//clase mediante la cual agrego las paginas a la ventana principal
	class MainPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		public Object instantiateItem(final ViewGroup collection, int position) {
			View page = null;
			switch (position) {
			case 0:
				if (p0 == null)
				{	p0 = (RelativeLayout) LayoutInflater
							.from(MainActivity.this).inflate(R.layout.welcome,
									null);
				Button b=(Button) p0.findViewById(R.id.button1);
				b.setBackgroundColor(Color.rgb(170, 170, 255));
				Button b1=(Button) p0.findViewById(R.id.button2);
				b1.setBackgroundColor(Color.rgb(170, 170, 255));
				Button b3=(Button) p0.findViewById(R.id.button3);
				b3.setBackgroundColor(Color.rgb(170, 170, 255));
				}
				page = p0;

				break;
			case 1:
				if (p1 == null) {
					p1 = (RelativeLayout) LayoutInflater
							.from(MainActivity.this).inflate(R.layout.paciente,
									null);
					n1 = (EditText) p1.findViewById(R.id.np4);//selecciono el number picker
					n2 = (EditText) p1.findViewById(R.id.np5);
					n3 = (EditText) p1.findViewById(R.id.np6);
					n4 = (EditText) p1.findViewById(R.id.np1);
					n5 = (EditText) p1.findViewById(R.id.np2);
					n6 = (EditText) p1.findViewById(R.id.np3);
				
			
					et1 = (EditText) p1.findViewById(R.id.editText1);//genero los editext
					et2=(EditText) p1.findViewById(R.id.editText2);
					et3 = (EditText) p1.findViewById(R.id.editText3);
					et4 = (EditText) p1.findViewById(R.id.editText5);
					et5=(EditText) p1.findViewById(R.id.editText4);
					et6=(EditText) p1.findViewById(R.id.editText6);
				n1.setText(c1.get(Calendar.DAY_OF_MONTH)+"");
					n2.setText(c1.get(Calendar.MONTH)+"");
					n3.setText(c1.get(Calendar.YEAR)+"");
					touc();
				}

				page = p1;

				break;
			case 2:
				if (p2 == null) {
					p2 = (RelativeLayout) LayoutInflater
							.from(MainActivity.this).inflate(R.layout.listado1,
									null);
					lv1 = (ListView) p2.findViewById(R.id.listView1);
					lv2=(ListView) p2.findViewById(R.id.listView2);
					CargarListado();
					lv2.setEnabled(false);
					lv2.setClickable(false);
					//metodo mediante el cual le hago funcionalidad al onclick de un list view
					lv1.setOnItemClickListener(new OnItemClickListener() {
					
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
					
				
				et1.setText(listapacientes.get(arg2).getNombre());
			names=listapacientes.get(arg2).getNombre();
			CargarListado1(names);
						lv1.setVisibility(255);
						lv2.setVisibility(0);
						lv1.setEnabled(false);
						lv1.setClickable(false);
						lv2.setEnabled(true);
						lv2.setClickable(true);
						}
					});
			
					
					
					lv2.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							et3.setText(listacirugias.get(arg2).getLugar());
							et4.setText("" + listacirugias.get(arg2).getCobro());
							n1.setText(listacirugias.get(arg2).getDia()+"");
							n2.setText(listacirugias.get(arg2).getMes()+"");
							n3.setText(listacirugias.get(arg2).getA�o()+"");
							n4.setText(listacirugias.get(arg2).getDia1()+"");
							n5.setText(listacirugias.get(arg2).getMes1()+"");
							n6.setText(listacirugias.get(arg2).getA�o1()+"");
						et5.setText(listacirugias.get(arg2).getNroC()+"");
						et2.setText(listacirugias.get(arg2).getNombreC());
						et6.setText(listacirugias.get(arg2).getObraSocial());
			lv1.setVisibility(0);
							lv2.setVisibility(255);
							lv1.setEnabled(true);
							lv1.setClickable(true);
							lv2.setEnabled(false);
							lv2.setClickable(false);
							vp.setCurrentItem(1);
						}
					});
				}
				
				page = p2;

				break;
			case 3:
				if(p3==null)
				{
					p3=(RelativeLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.listado2, null);
					sp1=(Spinner) p3.findViewById(R.id.spinner1);
					sp1.setSelection(1);
				sp1.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						cargarListado();
						
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						
					}
				});
					etn=(EditText) p3.findViewById(R.id.texts1);
					tvs=(WebView) p3.findViewById(R.id.textosw);
					cargarListado();
				
                }
				page=p3;
				break;
			}

			((ViewPager) collection).addView(page, 0);

			return page;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}

		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Bienvenido";
			case 1:
				return "Agregar Paciente";
			case 2:
				return "Listado pacientes";
			case 3:
				return "informe ";
			default:
				return "";
			}
		}

	}
//metodo en el cual grabo los datos en una base de datos
	public void grabar(View v) {
		
		bd db = new bd(this, "base1", null, 1);//llamo la base de datos
		
		SQLiteDatabase sql = db.getWritableDatabase();
		ContentValues registro = new ContentValues();
		Cursor cs = sql.rawQuery("select idCirugia from cirugia", null);//Selecciono por el nombre
		boolean b = false;//bandera para agregar o modificar los datos
		while (cs.moveToNext()) {
			if (cs.getInt(0)==Integer.parseInt(et5.getText().toString())) {
				b = true;//
			}
		}
		if (b == false) {//metodo mediante el cual agrego los datos
			if (et1.getText().length() > 0 && et3.getText().length() > 0
					&& et4.getText().length() > 0&&et2.getText().length()>0) {
				registro.put("nombre", et1.getText().toString());
				registro.put("nombre_C", et2.getText().toString());
				registro.put("obra_social", et6.getText().toString());
				registro.put("dia_C",Integer.parseInt( n1.getText().toString()));
				registro.put("mes_C", Integer.parseInt(n2.getText().toString()));
				registro.put("an_C",Integer.parseInt( n3.getText().toString()));
				registro.put("lugar", et3.getText().toString());
				registro.put("diaC",Integer.parseInt( n4.getText().toString()));
				registro.put("mesC", Integer.parseInt(n5.getText().toString()));
				registro.put("anC", Integer.parseInt(n6.getText().toString()));
				registro.put("monto",
						Float.parseFloat(et4.getText().toString()));
				sql.insert("cirugia", null,registro);//inserto los datos1
				Toast.makeText(this, "Datos Cargados", Toast.LENGTH_LONG)
						.show();
				
				
			} else {
				Toast.makeText(this, "ingrese los datos faltantes",
						Toast.LENGTH_LONG).show();//si falta algun dato muestro este texto
			}
			
		}

		else {
			if (et1.getText().length() > 0 && et3.getText().length() > 0
					&& et4.getText().length() > 0&&et2.getText().length()>0) {
				registro.put("nombre", et1.getText().toString());
				registro.put("nombre_C", et2.getText().toString());
				registro.put("obra_social", et6.getText().toString());
				registro.put("dia_C", n1.getText().toString());
				registro.put("mes_C", n2.getText().toString());
				registro.put("an_C", n3.getText().toString());
				registro.put("lugar", et3.getText().toString());
				registro.put("diaC", n4.getText().toString());
				registro.put("mesC", n5.getText().toString());
				registro.put("anC", n6.getText().toString());
				registro.put("monto",
						Float.parseFloat(et4.getText().toString()));
				int cant = sql.update("cirugia", registro, "idCirugia="
						+ et5.getText().toString() , null);//metodo mediante el cual actializo los datos
				if (cant == 1)
					Toast.makeText(this, "Datos modificados", Toast.LENGTH_LONG)
							.show();//si se modifico alun dato muestro esto

			} else {
				Toast.makeText(this, "ingrese los datos faltantes",
						Toast.LENGTH_LONG).show();
			}
		
			cs.close();
			sql.close();
		}
if(log!=0){
		CargarListado();//cargo el listado en el arrayList
		limpiarForm();
		cargarListado();
}else
{
	SharedPreferences preferences =getSharedPreferences("cargas", Context.MODE_PRIVATE);
	Editor editor=preferences.edit();
    editor.putString("carga", (log+1)+"");
    editor.commit();
	limpiarForm();
	
}
	}

	public void CargarListado() {
		
		listapacientes = new ArrayList<pacientes>();//listado cirgias
		
		bd db = new bd(this, "base1", null, 1);
		SQLiteDatabase sql = db.getWritableDatabase();//base de datos 

		Cursor registro = sql
				.rawQuery(
						//"select idCirugia,nombre,lugar,dia_C,mes_C,an_C,diaC,mesC,anC,monto,nombre_C,obra_social from cirugia order by idCirugia desc",
						"Select  distinct nombre from cirugia",null);
		while (registro.moveToNext()) {
			listapacientes.add(new pacientes(registro.getString(0)));
			}
		int cn=0;
		Cursor curso1=sql.rawQuery("select distinct idCirugia from cirugia", null);
		ContentValues registro1=new ContentValues();
		while(curso1.moveToNext())
		{cn++;
			registro1.put("idCirugia", cn);
			sql.update("cirugia",registro1 , "idCirugia="+curso1.getString(0),null);
		}
		
		sql.close();
		al=new adaptadorListado(this);//genero el arrayList de la clase
		lv1.setAdapter(al);//agrego el listado al listView1

	}
	public void CargarListado1(String nombre1)
	{
		bd db = new bd(this, "base1", null, 1);
		listacirugias=new ArrayList<cirugias>();
		SQLiteDatabase sql = db.getWritableDatabase();//base de datos 
		Cursor registro = sql.rawQuery("select idCirugia,nombre,lugar,dia_C,mes_C,an_C,diaC,mesC,anC,monto,nombre_C,obra_social from cirugia where nombre ='"+nombre1+"'  order by idCirugia",null);
		while (registro.moveToNext()) {
			listacirugias.add(new cirugias(registro.getString(1), registro
					.getString(2), Integer.parseInt(registro.getString(3)),
					Integer.parseInt(registro.getString(4)), Integer
							.parseInt(registro.getString(5)), Integer
							.parseInt(registro.getString(6)), Integer
							.parseInt(registro.getString(7)), Integer
							.parseInt(registro.getString(8)), Float
							.parseFloat(registro.getString(9)),registro.getString(10),Integer.parseInt(registro.getString(0)),registro.getString(11)));
			}
		sql.close();
		al1 = new adaptadorListadon(this);//genero el arrayList de la clase
		lv2.setAdapter(al1);//agrego el listado al listView1
		
	}

	public class adaptadorListado extends ArrayAdapter<pacientes> {

		private Context context;

		public adaptadorListado(Context contexto) {
			super(contexto, R.layout.consultas, listapacientes);

			context = contexto;
		}

		public View getView(int posicion, View convertView, ViewGroup parent) {
			LayoutInflater li = LayoutInflater.from(context);
			View v1 = li.inflate(R.layout.consultas, null);
			TextView tv1=(TextView) v1.findViewById(R.id.tvs1);
			tv1.setText(listapacientes.get(posicion).getNombre());
			return v1;
		}
	}
	public class adaptadorListadon extends ArrayAdapter<cirugias> {

		private Context context;

		public adaptadorListadon(Context contexto) {
			super(contexto, R.layout.consultas1,listacirugias);
			context = contexto;
		}

		public View getView(int posicion, View convertView, ViewGroup parent) {
			LayoutInflater li = LayoutInflater.from(context);
			View v1 = li.inflate(R.layout.consultas1, null);
			TextView tv1 = (TextView) v1.findViewById(R.id.ts1);
			TextView tv2 = (TextView) v1.findViewById(R.id.ts2);
			TextView tv3 = (TextView) v1.findViewById(R.id.ts3);
			TextView tv4 = (TextView) v1.findViewById(R.id.ts4);
			TextView tv5 = (TextView) v1.findViewById(R.id.ts5);
			TextView tv6=(TextView) v1.findViewById(R.id.ts10);
			tv1.setText((posicion+1)+"");
			tv2.setText("Monto :" + listacirugias.get(posicion).getCobro());
			tv3.setText("fecha de cirugia :"
					+ listacirugias.get(posicion).getDia() + "/"
					+ listacirugias.get(posicion).getMes() + "/"
					+ listacirugias.get(posicion).getA�o());
			tv4.setText("fecha de cobro :"
					+ listacirugias.get(posicion).getDia1() + "/"
					+ listacirugias.get(posicion).getMes1() + "/"
					+ listacirugias.get(posicion).getA�o1());
			tv5.setText("lugar:" + listacirugias.get(posicion).getLugar());
			tv6.setText("Obra Social :"+listacirugias.get(posicion).getObraSocial());
			
			return v1;
		}
	}
	public void borra(View v) {
		bd db = new bd(this, "base1", null, 1);
		SQLiteDatabase sql = db.getWritableDatabase();
		int cont = sql.delete("cirugia", "idCirugia=" + et5.getText().toString()
				, null);
		if (cont == 1) {
			Toast.makeText(this, "dato eliminada", Toast.LENGTH_SHORT).show();
			limpiarForm();
		} else {
			Toast.makeText(this, "no existe la cirugia ingresada",
					Toast.LENGTH_SHORT).show();
		}
		
			int cn=0;
			Cursor curso=sql.rawQuery("select distinct idCirugia from cirugia", null);
			ContentValues registro=new ContentValues();
			while(curso.moveToNext())
			{cn++;
				registro.put("idCirugia", cn);
				sql.update("cirugia",registro , "idCirugia="+curso.getString(0),null);
			}
		
		
		sql.close();
		CargarListado();
		limpiarForm();
		cargarListado();
	}
public void irListado(View v)
{
	//salto a la pagina 3
vp.setCurrentItem(2);	

}
public void irPacientes(View v)
{
	//salto a la pagina 2
vp.setCurrentItem(1);	

}
public void informe(View v)
{
	//salto a la pagina 4
vp.setCurrentItem(3);	
}
public void cargarListado()
{
	tvs.removeAllViews();
int cont=0;

TableRow tr;
TextView nombre,nombreCirugia,fechaCirugia,monto,FechaCobro,lugar,obraSocial;
int mes=0;
switch(sp1.getSelectedItemPosition())
{
case 0:sn="nombre";
	break;
case 1:sn="an_C,mes_C,dia_C";
	break;
case 2:
	sn="lugar";
	break;
case 3:
	sn="obra_social";
	break;
case 4:
	sn="monto";
	break;	
}
String pagina;
pagina="<!doctype html>" +"<head><style> body{overflow:scroll; background-color:green; } p{color:white;}#tabla{overflow:scroll; } </style></head>"+
		"<body><div id="+"\""+"tabla"+"\""+"><table>";


bd db = new bd(this, "base1", null, 1);
SQLiteDatabase sql = db.getWritableDatabase();//base de datos 
int x=nn1*29;
int x2=x+29;
int meses=1;
int year=0;
Cursor registro = sql
		.rawQuery("select nombre,dia_C,mes_C,an_C,diaC,mesC,anC,monto,nombre_C,lugar,obra_social,idCirugia from cirugia order by "+sn +" limit "+x+","+x2,null);//
while (registro.moveToNext()) {
	monto=new TextView(this);
	monto.setText(" "+registro.getString(7));
  meses=Integer.parseInt(registro.getString(2));
  int an=Integer.parseInt(registro.getString(3));
	if(meses!=mes||an!=year){
	mes=meses;
	year=an;
	String smes="";
	switch(mes)
	{
	case 1:smes="enero";
		    break;
	case 2:smes="febrero";break;
	case 3:smes="marzo";break;
	case 4:smes="abril";break;
	case 5:smes="mayo";break;
	case 6:smes="junio";break;
	case 7:smes="julio";break;
	case 8:smes="agosto";break;
	case 9:smes="septiembre";break;
	case 10:smes="octubre";break;
	case 11:smes="noviembre";break;
	case 12:smes="Diciembre";break;
	}
	pagina+="<tr bgcolor=\"gray\"><td colspan=4>"+smes+"</td><td colspan=3>"+registro.getString(3)+"<colspan></tr>"+
	"<tr bgcolor=\"gray\"><td>Paciente</td><td>Cirugia</td><td>fecha</td><td>Lugar</td><td>OS</td><td>Monto</td><td>Fecha de cobro</td></tr>";
	}
	if(Float.parseFloat(monto.getText().toString())==0)
	{
		
		pagina+="<tr bgColor=\"red \"><td nowrap><p>"+registro.getString(0)+"</p></td><td nowrap><p>"+registro.getString(8)+"</p></td><td nowrap><p>"+registro.getString(1)+"</p></td><td nowrap><p>"+registro.getString(9)+"</p></td><td nowrap><p>"+registro.getString(10)+"</p></td><td nowrap><p>"+"No se cobro nada"+"</p></td><td nowrap><p>"+registro.getString(4)+"/"+registro.getString(5)+"/"+registro.getString(6)+"</p></td></tr></div> ";
	}
	else
	{
		pagina+="<tr bgColor=\"blue\"><td nowrap><p>"+registro.getString(0)+"</p></td><td nowrap><p>"+registro.getString(8)+"</p></td><td nowrap><p>"+registro.getString(1)+"</p></td><td nowrap><p>"+registro.getString(9)+"</p></td><td nowrap><p>"+registro.getString(10)+"</p></td><td nowrap><p>"+registro.getString(7)+"</p></td><td nowrap><p>"+registro.getString(4)+"/"+registro.getString(5)+"/"+registro.getString(6)+"</p></td></tr></div> ";
	}
	n++;
}
pagina+="</table></div></body></html>";
tvs.loadData(pagina, "text/html","utf-8");
tvs.reload();		
registro.close();

sql.close();

}

public void limpiarForm()
{
	et1.setText("");
	et3.setText("");
	et4.setText("0");
	et5.setText("0");
	n1.setText(""+1);
	n2.setText(""+1);
	n3.setText(c1.get(Calendar.YEAR)+"");
	n4.setText(1+"");
	n5.setText(1+"");
	n6.setText(c1.get(Calendar.YEAR)+"");	
	et6.setText("");
	et2.setText("");
}
public void update(View v)
{
cargarListado();	
}
public void siguiente(View v)
{
	

	nn1++;
etn.setText(nn1+"");
cargarListado();
}
public void anterior(View v)
{if(nn1>0)
{	
	nn1--;
	etn.setText(nn1+"");
	cargarListado();
}
}
public void zoomin(View v)
{
tvs.zoomIn();
tvs.scrollTo(0, 0);


}
public void zoomout(View v)
{
	tvs.zoomOut();
	tvs.scrollTo(0, 0);
}
public void touc()//touch listener dentro de las fechas
{
	n1.setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
		
			n1.setText("");
			InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			   imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
			return false;
		}
	});
	n2.setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
n2.setText("");
InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
			return false;
		}
	});
	n3.setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			n3.setText("");
			InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			   imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
			return false;
		}
	});
	n4.setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
		n4.setText("");
			return false;
		}
	});
	n5.setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			n5.setText("");
			return false;
		}
	});
	n6.setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			n6.setText("");
			return false;
		}
	});
}
}
