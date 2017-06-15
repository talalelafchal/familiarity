return new GsonBuilder()
                .registerTypeAdapter(SimpleArrayMapJsonSerializer.TYPE, new SimpleArrayMapJsonSerializer())
                .create();