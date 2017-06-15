
  @Override
  public final Object onRetainCustomNonConfigurationInstance() {
    Map<String, Object> objectsToPersist = new HashMap<>();
    // get all fields with the Persistable annotation
    List<Field> annotatedFields = getAllFieldsWithAnnotation(new ArrayList<Field>(), Persistable.class, this.getClass(), BaseActivity.class);
    for (Field field : annotatedFields) {
      // for each field, make the field accessible (in case it's private) and then write its value into the map
      field.setAccessible(true);
      try {
        objectsToPersist.put(field.getName(), field.get(this));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return objectsToPersist;
  }

  /**
   * Finds all the fields with the given annotation (public and private0 in the given class. Recursively repeats
   * this for super classes until reaching the given endClass.
   * @param annotatedFields an empty list that will be populated with any fields that match the annotationClazz
   * @param annotationClass the annotation to look for
   * @param startClazz the starting subclass to look for fields in
   * @param endClazz the super class at which to stop looking for fields
   * @return all fields with the given annotation
   */
  private List<Field> getAllFieldsWithAnnotation(List<Field> annotatedFields, Class<Persistable> annotationClass, Class startClazz, Class<BaseActivity> endClazz) {
    Field[] fields = startClazz.getDeclaredFields();
    for (Field field : fields) {
      if(field.isAnnotationPresent(annotationClass)) {
        annotatedFields.add(field);
      }
    }
    if (endClazz.equals(startClazz)) {
      return annotatedFields;
    }
    return getAllFieldsWithAnnotation(annotatedFields, annotationClass, startClazz.getSuperclass(), endClazz);
  }