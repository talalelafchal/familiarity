
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Right after super.onCreate we get the retained instance state and reinitiate any persisted fields.
    if (getLastCustomNonConfigurationInstance() instanceof Map) {    
      List<Field> annotatedFields = getAllFieldsWithAnnotation(new ArrayList<Field>(), Persistable.class, this.getClass(), BaseActivity.class);
      for (Field field : annotatedFields) {
        // for each field, make the field accessible (in case it's private) and then set it's value based on the value in the map
        field.setAccessible(true);
        try {
          field.set(this, ((Map<String, Object>) getLastCustomNonConfigurationInstance()).get(field.getName()));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }