//this class is used by gson for serializing JSON object 
//I have separated them as per response 
//http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&units=metric&cnt=14&APPID=a25f941d28a7f74da4a1c65c039086a4&lat=23.721012&lon=90.394353
//see the response then this classes will more clear

public class OpenWeatherApiResponse {

  @SerializedName("city") City resultCity;

  @SerializedName("cod") String statusCode;

  @SerializedName("cnt") int dayCount;

  @SerializedName("list") List<DayForecast> results;

  public City getResultCity() {
    return resultCity;
  }

  public void setResultCity(City resultCity) {
    this.resultCity = resultCity;
  }

  public String getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }

  public int getDayCount() {
    return dayCount;
  }

  public void setDayCount(int dayCount) {
    this.dayCount = dayCount;
  }

  public List<DayForecast> getResults() {
    return results;
  }

  public void setResults(List<DayForecast> results) {
    this.results = results;
  }
}

public class DayForecast {

  @SerializedName("dt") long forecastTime;

  @SerializedName("temp") Temperature dayTemp;

  @SerializedName("pressure") double pressure;

  @SerializedName("humidity") int humidity;

  @SerializedName("speed") double windSpeed;

  @SerializedName("deg") double windDirection;

  @SerializedName("clouds") int cloudPercent;

  @SerializedName("weather") List<Weather> weather;

  String uxFormatedWeatherForecast;

  public String getUxFormatedWeatherForecast() {
    return uxFormatedWeatherForecast;
  }

  public void setUxFormatedWeatherForecast(String uxFormatedWeatherForecast) {
    this.uxFormatedWeatherForecast = uxFormatedWeatherForecast;
  }

  public void setPrettyDate(long prettyDate) {
    this.forecastTime = prettyDate;
  }

  public List<Weather> getWeather() {
    return weather;
  }

  public void setWeather(List<Weather> weather) {
    this.weather = weather;
  }

  public long getForecastTime() {
    return forecastTime;
  }

  public void setForecastTime(long forecastTime) {
    this.forecastTime = forecastTime;
  }

  public Temperature getDayTemp() {
    return dayTemp;
  }

  public void setDayTemp(Temperature dayTemp) {
    this.dayTemp = dayTemp;
  }

  public double getPressure() {
    return pressure;
  }

  public void setPressure(double pressure) {
    this.pressure = pressure;
  }

  public int getHumidity() {
    return humidity;
  }

  public void setHumidity(int humidity) {
    this.humidity = humidity;
  }

  public double getWindSpeed() {
    return windSpeed;
  }

  public void setWindSpeed(double windSpeed) {
    this.windSpeed = windSpeed;
  }

  public double getWindDirection() {
    return windDirection;
  }

  public void setWindDirection(double windDirection) {
    this.windDirection = windDirection;
  }

  public int getCloudPercent() {
    return cloudPercent;
  }

  public void setCloudPercent(int cloudPercent) {
    this.cloudPercent = cloudPercent;
  }

  public class Temperature {

    @SerializedName("day") double dayTemp;
    @SerializedName("max") double tempMax;

    @SerializedName("min") double tempMin;

    @SerializedName("night") double tempNight;

    @SerializedName("eve") double tempEvening;

    @SerializedName("morn") double tempMorning;

    public double getDayTemp() {
      return dayTemp;
    }

    public void setDayTemp(double dayTemp) {
      this.dayTemp = dayTemp;
    }

    public double getTempMax() {
      return tempMax;
    }

    public void setTempMax(double tempMax) {
      this.tempMax = tempMax;
    }

    public double getTempMin() {
      return tempMin;
    }

    public void setTempMin(double tempMin) {
      this.tempMin = tempMin;
    }

    public double getTempNight() {
      return tempNight;
    }

    public void setTempNight(double tempNight) {
      this.tempNight = tempNight;
    }

    public double getTempEvening() {
      return tempEvening;
    }

    public void setTempEvening(double tempEvening) {
      this.tempEvening = tempEvening;
    }

    public double getTempMorning() {
      return tempMorning;
    }

    public void setTempMorning(double tempMorning) {
      this.tempMorning = tempMorning;
    }
  }

  public class Weather{

    @SerializedName("id") int id;

    @SerializedName("main") String main;

    @SerializedName("description") String description;

    @SerializedName("icon") String icon;

    public String getMain() {
      return main;
    }

    public void setMain(String main) {
      this.main = main;
    }

    public String getDescriprion() {
      return description;
    }

    public void setDescriprion(String description) {
      this.description = description;
    }

    public String getIcon() {
      return icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }
  }
}


public class City {

  @SerializedName("id")
  int id;

  @SerializedName("name")
  String cityName;

  @SerializedName("country")
  String CountrCode;


  @SerializedName("coord")
  CityLocation location;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public String getCountrCode() {
    return CountrCode;
  }

  public void setCountrCode(String countrCode) {
    CountrCode = countrCode;
  }

  public CityLocation getLocation() {
    return location;
  }

  public void setLocation(CityLocation location) {
    this.location = location;
  }

  public class CityLocation{

    @SerializedName("lat")
    double lat;

    @SerializedName("lon")
    double lon;

    public double getLat() {
      return lat;
    }

    public void setLat(double lat) {
      this.lat = lat;
    }

    public double getLon() {
      return lon;
    }

    public void setLon(double lon) {
      this.lon = lon;
    }
  }
}
