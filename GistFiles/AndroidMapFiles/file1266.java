  /**
   * Any field marked with this annotation will be persisted on state changes.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  protected @interface Persistable{}
