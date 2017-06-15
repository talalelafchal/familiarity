// Will contain the raw JSON response as a string.
// hàm parse là phân tich dạng // thành dạng uri
// theo dạng sau 
http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
1 http://api.example.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7

2
final String FORECAST_BASE_URL = 
    "http://api.example.org/data/2.5/forecast/daily?";
final String QUERY_PARAM = "q";
final String FORMAT_PARAM = "mode";
final String UNITS_PARAM = "units";
final String DAYS_PARAM = "cnt";

3
Uri builtUri = Uri.parse(FORECAST_BASE_URL)
    .buildUpon()
    .appendQueryParameter(QUERY_PARAM, params[0])
    .appendQueryParameter(FORMAT_PARAM, "json")
    .appendQueryParameter(UNITS_PARAM, "metric")
    .appendQueryParameter(DAYS_PARAM, Integer.toString(7))
    .build();
4
URL url = new URL(builtUri.toString());


            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, OPEN_WEATHER_MAP_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.i(TAG, MainActivity.LIFE_CYCLES + builtUri.toString());