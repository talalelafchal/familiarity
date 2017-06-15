// phân tích json
try {
            final JSONObject json = new JSONObject(result); // lưu JSON mà server trả
            JSONArray routeArray = json.getJSONArray("routes");

            if (json.getString("status").equals("OK")) {

                JSONObject routes = routeArray.getJSONObject(0);

                JSONObject overviewPolylines = routes
                        .getJSONObject("overview_polyline"); // duong di cua google

                // retrieve step
                JSONArray legsArray = routes.getJSONArray("legs");
                JSONObject leg = legsArray.getJSONObject(0);
                JSONArray stepsArray = leg.getJSONArray("steps");
                stepsArrayObject = new ArrayList<>();
                for (int i = 0; i < stepsArray.length(); i++) {
                    stepsArrayObject.add(stepsArray.getJSONObject(i));
                }

                String encodedString = overviewPolylines.getString("points"); // lấy value với key là "point"
                List<GeoPoint> list = decodePoly(encodedString); // hàm này return 1 list Geopoint doc  đường đi


                for (int z = 0; z < list.size() - 1; z++) {
                    GeoPoint src = list.get(z);
                    waypoints.add(src);
                }
            } else {
                isReturnOK = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }