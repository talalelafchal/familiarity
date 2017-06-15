OpenWeatherApi apiInterface = RestServiceGenerator.createService(OpenWeatherApi.class);

    final Call<OpenWeatherApiResponse> weatherForecast =
        apiInterface.getWeatherForecast(BuildConfig.OPEN_WEATHER_MAP_API_KEY, "json", unit,
            "14", location);

    weatherForecast.enqueue(new Callback<OpenWeatherApiResponse>() {
      @Override
      public void onResponse(Call<OpenWeatherApiResponse> call,
                             Response<OpenWeatherApiResponse> response) {
        if (response.isSuccessful()) {
          dayForecasts = response.body().getResults();
          formateAndSave(dayForecasts , response.body().getResultCity());

        } else {
          try {
            ApiErrorClass error = new Gson().fromJson(response.errorBody().string(), ApiErrorClass.class);
            mPresenter.onWeatherResultLoadingError(error);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      @Override
      public void onFailure(Call<OpenWeatherApiResponse> call, Throwable t) {
        Log.d(getClass().getSimpleName(), "called onFailure() " + t.getMessage());
      }
    });