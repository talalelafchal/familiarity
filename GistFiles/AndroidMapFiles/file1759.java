public class ColumnIndexCache {
   private ArrayMap<String, Integer> mMap = new ArrayMap<>();
   public int getColumnIndex(Cursor cursor, String columnName) {
      if (!mMap.containsKey(columnName))
         mMap.put(columnName, cursor.getColumnIndex(columnName));
      return mMap.get(ColumnName);
   }
   public void clear() {
      mMap.clear();
   }
}