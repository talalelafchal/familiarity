                ContentResolver cr = getContentResolver();
                InputStream is = cr.openInputStream(uri);
                InputStreamEntity ise = new InputStreamEntity(is, -1);
                String scheme = "http";
                String host = "192.168.0.3";
                String path = "/photos";
                HttpClient client = AndroidHttpClient.newInstance("Android");

                // @TODO: Use a URL builder.
                HttpPut put = new HttpPut(scheme + "://" + host + path);
                put.setEntity(ise);
                HttpResponse response = client.execute(put);