
/*
 * uses MyGeoCoder if !android.location.Geocoder.isPresent()
 */
public class GeoCoderWrapper {

	Geocoder geo;
	
	public GeoCoderWrapper(Context context){
		if(Geocoder.isPresent()){
			geo = new Geocoder(context);
		}
	}
	
	public List<Address> getFromLocationName(String address) throws IOException {
		if(geo != null){
			try{
				return geo.getFromLocationName(address, 10);
			}catch(Exception e){
				return MyGeoCoder.getFromLocationName(address);
			}
		}else{
			return MyGeoCoder.getFromLocationName(address);
		}
	}
	
	public List<Address> getFromLocationName(String address, int limit) throws IOException {
		if(geo != null){
			try{
				return geo.getFromLocationName(address, limit);
			}catch(Exception e){
				List<Address> a = MyGeoCoder.getFromLocationName(address);
				while(a.size()>limit){
					a.remove(a.size()-1);
				}
				return a;
			}
		}else{
			List<Address> a = MyGeoCoder.getFromLocationName(address);
			while(a.size()>limit){
				a.remove(a.size()-1);
			}
			return a;
		}
	}
	
	public List<Address> getFromLocation(double lat, double lng) throws IOException {
		if(geo != null){
			try{
				return geo.getFromLocation(lat, lng, 10);
			}catch(Exception e){
				return MyGeoCoder.getFromLocation(lat, lng);
			}
		}else{
			return MyGeoCoder.getFromLocation(lat, lng);
		}
	}
	
	public List<Address> getFromLocation(double lat, double lng, int limit) throws IOException {
		if(geo != null){
			try{
				return geo.getFromLocation(lat, lng, limit);
			}catch(Exception e){
				List<Address> a = MyGeoCoder.getFromLocation(lat, lng);
				if(a!=null){
					while(a.size()>limit){
						a.remove(a.size()-1);
					}
				}
				return a;
			}
		}else{
			List<Address> a = MyGeoCoder.getFromLocation(lat, lng);
			if(a!=null){
				while(a.size()>limit){
					a.remove(a.size()-1);
				}
			}
			return a;
		}
	}
	
}
