package your.app.package;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
class Business implements Parcelable
{
    private JSONObject obj;

    private String placeId = "";
    private String placeName = "";
    private String placeAddr = "";
    private String placePhone = "";
    private String placePriceLevel = "";
    private double placeRating = 0;
    private int placeReviewCount = 0;
    private String placeImageUrl = "";
    private String placeUrl = "";
    private boolean placeClosed = true;
    private double distance = 0;
    private double placeLatitude = 0;
    private double placeLongitude = 0;

    Business(JSONObject obj)
    {
        this.obj = obj;
        setProperties();
    }

    //----------------------------------------------------------------------------------------//
    // Setters
    //----------------------------------------------------------------------------------------//

    private void setProperties()
    {
        UUID uuid = UUID.randomUUID();
        placeId = uuid.toString();

        String str = extractString("name");

        if (str != null)
        {
            placeName = str;
        }

        str = extractAddress("location");

        if (str != null)
        {
            placeAddr = str;
        }

        str = extractString("display_phone");

        if (str != null)
        {
            placePhone = str;
        }

        str = extractString("price");

        if (str != null)
        {
            placePriceLevel = str;
        }

        placeRating = extractDouble("rating");
        placeReviewCount = extractInt("review_count");

        str = extractString("image_url");

        if (str != null)
        {
            placeImageUrl = str.replace("\\", "");
        }

        str = extractString("url");

        if (str != null)
        {
            placeUrl = str.replace("\\", "");
        }

        placeClosed = extractPlaceCloseStatus();
        placeLatitude = extractLatitude();
        placeLongitude = extractLongitude();
    }

    void setDistance(double value)
    {
        distance = value;
    }

    //----------------------------------------------------------------------------------------//
    // Getters
    //----------------------------------------------------------------------------------------//

    String getPlaceId()
    {
        return placeId;
    }

    String getPlaceName()
    {
        return placeName;
    }

    String getPlaceAddr()
    {
        return placeAddr;
    }

    String getPlacePhone()
    {
        return placePhone;
    }

    String getPlacePriceLevel()
    {
        return placePriceLevel;
    }

    double getPlaceRating()
    {
        return placeRating;
    }

    int getPlaceReviewCount()
    {
        return placeReviewCount;
    }

    String getPlaceImageUrl()
    {
        return placeImageUrl;
    }

    String getPlaceUrl()
    {
        return placeUrl;
    }

    boolean isPlaceClosed()
    {
        return placeClosed;
    }

    double getDistance()
    {
        return distance;
    }

    double getPlaceLatitude()
    {
        return placeLatitude;
    }

    double getPlaceLongitude()
    {
        return placeLongitude;
    }

    //----------------------------------------------------------------------------------------//
    // Extraction functions
    //----------------------------------------------------------------------------------------//

    @SuppressWarnings("UnnecessaryLocalVariable")
    private String extractString(String key)
    {
        try
        {
            String str = obj.getString(key);
            return str;
        }
        catch (JSONException e)
        {
            //e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private String extractAddress(String key)
    {
        try
        {
            JSONObject location = obj.getJSONObject(key);
            JSONArray displayAddr = location.getJSONArray("display_address");
            String addr = "";

            if (displayAddr.length() > 0)
            {
                for (int i = 0; i < displayAddr.length(); i++)
                {
                    addr += displayAddr.getString(i);

                    if (i < (displayAddr.length() - 1))
                    {
                        addr += ", ";
                    }
                }
            }

            return addr;
        }
        catch (JSONException e)
        {
            //e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private double extractDouble(String key)
    {
        try
        {
            double dbl = obj.getDouble(key);
            return dbl;
        }
        catch (JSONException e)
        {
            //e.printStackTrace();
            return 0;
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private int extractInt(String key)
    {
        try
        {
            int i = obj.getInt(key);
            return i;
        }
        catch (JSONException e)
        {
            //e.printStackTrace();
            return 0;
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private boolean extractPlaceCloseStatus()
    {
        try
        {
            boolean closed = obj.getBoolean("is_closed");
            return closed;
        }
        catch (JSONException e)
        {
            //e.printStackTrace();
            return true;
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private double extractLatitude()
    {
        try
        {
            JSONObject coordinates = obj.getJSONObject("coordinates");
            double lat = coordinates.getDouble("latitude");

            return lat;
        }
        catch (JSONException e)
        {
            //e.printStackTrace();
            return 0;
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private double extractLongitude()
    {
        try
        {
            JSONObject coordinates = obj.getJSONObject("coordinates");
            double lng = coordinates.getDouble("longitude");

            return lng;
        }
        catch (JSONException e)
        {
            //e.printStackTrace();
            return 0;
        }
    }

    //----------------------------------------------------------------------------------------//
    // Parcelable
    //----------------------------------------------------------------------------------------//

    // Font: https://newfivefour.com/android-parcelable.html

    public static final Parcelable.Creator<Business> CREATOR = new Parcelable.Creator<Business>()
    {
        public Business createFromParcel(Parcel in)
        {
            return new Business(in);
        }

        public Business[] newArray(int size)
        {
            return new Business[size];
        }
    };

    private Business(Parcel in)
    {
        placeId = in.readString();
        placeName = in.readString();
        placeAddr = in.readString();
        placePhone = in.readString();
        placePriceLevel = in.readString();
        placeRating = in.readDouble();
        placeReviewCount = in.readInt();
        placeImageUrl = in.readString();
        placeUrl = in.readString();

        boolean[] myBooleanArr = new boolean[1];
        in.readBooleanArray(myBooleanArr);
        placeClosed = myBooleanArr[0];

        distance = in.readDouble();
        placeLatitude = in.readDouble();
        placeLongitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(placeId);
        dest.writeString(placeName);
        dest.writeString(placeAddr);
        dest.writeString(placePhone);
        dest.writeString(placePriceLevel);
        dest.writeDouble(placeRating);
        dest.writeInt(placeReviewCount);
        dest.writeString(placeImageUrl);
        dest.writeString(placeUrl);
        dest.writeBooleanArray(new boolean[]{placeClosed});
        dest.writeDouble(distance);
        dest.writeDouble(placeLatitude);
        dest.writeDouble(placeLongitude);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}