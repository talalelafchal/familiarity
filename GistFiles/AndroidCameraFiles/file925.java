StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&mode=" + travelMode);
        urlString.append("&language=" + Constant.LANGUAGE);
        urlString.append("&key=" + Constant.GOOGLE_SERVER_KEY);
        Log.i(TAG, "makeURL: " + urlString.toString());
        return urlString.toString();