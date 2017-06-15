import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class MyRequest extends SpringAndroidSpiceRequest<MyResponse> {

	public MyRequest(){
		super(MyResponse.class);
	}

	@Override
	public MyResponse loadDataFromNetwork() throws Exception {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("foo", "bar");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(parameters, headers);

		return getRestTemplate().postForObject("http://www.yoursite.com", request, MyResponse.class);
	}
}