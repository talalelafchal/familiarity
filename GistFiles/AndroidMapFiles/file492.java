void foo() {
  GsonBuilder gsonBuilder = new GsonBuilder();
  gsonBuilder.registerTypeAdapter(Object.class, new NaturalDeserializer());
  Gson gson = gsonBuilder.create();
  
  // ...
  
  Object natural = gson.fromJson(source, Object.class);
}
