package your.app.package;

import java.util.ArrayList;

interface ServerRequestInterface
{
    void yelpAccessTokenRequestResult(String result, String accessToken);
    void searchPlacesResult(String result, ArrayList<Business> businesses);
}