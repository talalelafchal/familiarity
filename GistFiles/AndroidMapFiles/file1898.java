  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    Map<String, Object> objectsToPersist = new HashMap<>();
    // get all fields with the Persistable annotation
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(Persistable.class)) {
        // for each field, make the field accessible (in case it's private) and then write its value into the map
        field.setAccessible(true);
        try {
          objectsToPersist.put(field.getName(), field.get(this));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return objectsToPersist;
  }