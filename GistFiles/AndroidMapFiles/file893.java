  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Right after super.onCreate we get the retained instance state and reinitiate any persisted fields.
    if (getLastCustomNonConfigurationInstance() instanceof Map) {
      reinitializePersistedFields(((Map<String, Object>) getLastCustomNonConfigurationInstance()));
    }

  }

  /**
   * Reinitializes any fields that have the Persistable annotation after state change
   */
  private void reinitializePersistedFields(Map<String, Object> persistedObjects) {
    // get all fields with the Persistable annotation
    List<Field> annotatedFields = getAllFieldsWithAnnotation(new ArrayList<Field>(), Persistable.class, this.getClass(), BaseActivity.class);
    for (Field field : annotatedFields) {
      // for each field, make the field accessible (in case it's private) and then set it's value based on the value in the map
      field.setAccessible(true);
      try {
        field.set(this, persistedObjects.get(field.getName()));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }