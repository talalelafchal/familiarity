package andrej.jelic.attendance;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {

    WebView webView;

    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_web_view, container, false);

        webView = (WebView) v.findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl("http://andrejkeno.net46.net/viewStudents.php");

        return v;
    }


}
