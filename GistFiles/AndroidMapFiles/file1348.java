//Essa view nós inserimos no layout.xml

    <com.google.android.gms.maps.MapView
        android:id="@+id/mapa"
        android:layout_width="match_parent"
        android:layout_height="match_parent"

        android:layout_alignParentRight="true" />
        
//Isso nós vamos inserir no Manifest, cmo primiero ítem dentro da tag Application:

        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="@string/api_key" />

        <uses-library
            android:name="com.google.android.maps"
            android:required="false" />
            
//Vamos acessar esse site e criar uma nova Key para autorizar o acesso ao Google Maps:

https://console.developers.google.com/project?authuser=0&pli=1

//Agora vamos inserir a Key gerada no arquivo strings.xml

    <string name="api_key">AIzaSyARS0Ornl8_t2ruVdTIQ5UMk7_DhrVQ9U0</string>
    
//No activity, seguir estes passos para inicializar o mapa

mapView = (com.google.android.gms.maps.MapView) rootview.findViewById(R.id.mapa);
mapView.onCreate(savedInstanceState);
mapView.onResume();

//Para adicionar um ScaleBar no mapa:

SacaleBar scaleBar = new ScaleBar(getActivity());
scaleBar.setGoogleMap(mapView.getMap());
scaleBar.setLayoutParams(params);
this.container.addView(scaleBar);


