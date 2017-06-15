private static final String LIST_QUERY = "SELECT * FROM "
      + TodoItem.TABLE
      + " WHERE "
      + TodoItem.LIST_ID
      + " = ? ORDER BY "
      + TodoItem.COMPLETE
      + " ASC";

@Inject BriteDatabase db;
