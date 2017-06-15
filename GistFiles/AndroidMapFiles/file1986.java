/*
 *	基本的な実装は他を参照してくだち！＞＜
 *
 */
	GoogleMap _map;

	private void init_map(){
		_map = ((SupportMapFragment)fragment).getMap();
		_map.setInfoWindowAdapter(new CustomInfoAdapter());
	}