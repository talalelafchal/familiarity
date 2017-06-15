public class MapDisplay extends android.support.v4.app.FragmentActivity {

//...

  private void setUpMap() {
	    TileProvider tileProvider = TileProviderFactory.getTileProvider();
	    mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));    
	}
}