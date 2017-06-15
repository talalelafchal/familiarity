import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Pair;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android SQLite Database Helper.
 */
public class DbHelper extends SQLiteOpenHelper {

  private final List<DbContext> dbContexts = new ArrayList<>();

  public DbHelper(Context context, String dbName, int dbVersion) {
    super(context, dbName, null, dbVersion);
  }

  public static String s(Column column) {
    return column.toString();
  }

  public static String s(Table table) {
    return table.toString();
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    for (DbContext dbContext : dbContexts) {
      db.execSQL(dbContext.getCreateStatement());
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    for (DbContext dbContext : dbContexts) {
      if (dbContext.isUpgradeSupported(oldVersion, newVersion)) {
        db.execSQL(dbContext.getAlterStatement(oldVersion, newVersion));
      } else {
        throw new IllegalArgumentException("Database upgrade from version " + oldVersion + " to version " + newVersion + " is not supported!");
        // db.execSQL(dbContext.getDropStatement());
        // db.execSQL(dbContext.getCreateStatement());
      }
    }
  }

  public void registerContext(DbContext dbContext) {
    dbContexts.add(dbContext);
  }

  public static abstract class QueryExecutor<T extends Row> {

    protected SQLiteDatabase db;

    protected SQLiteStatement insertStatement;
    protected SQLiteStatement updateStatement;
    protected SQLiteStatement deleteStatement;

    protected QueryExecutor(SQLiteDatabase db) {
      this.db = db;
    }

    public List<T> findAll(int limit) {
      Cursor cursor = db.query(s(getTable()), null, null,
        null, null, null, null, limit == 0 ? null : String.valueOf(limit));

      try {
        if (cursor.moveToFirst()) {
          List<T> result = new ArrayList<>();
          do {
            result.add(map(cursor));
          } while (cursor.moveToNext());
          return result;
        } else {
          return Collections.emptyList();
        }
      } finally {
        cursor.close();
      }
    }

    public T findById(long id) {
      Cursor cursor = db.query(s(getTable()), null, DbContext.ID + " = ?",
        new String[]{String.valueOf(id)}, null, null, null, null);

      try {
        if (cursor.moveToFirst()) {
          return map(cursor);
        } else {
          return null;
        }
      } finally {
        cursor.close();
      }
    }

    public long save(T entity) {
      if (entity.isPersistent()) {
        if (update(entity)) {
          return entity.getId();
        }
      }
      return insert(entity);
    }

    public boolean delete(T entity) {
      return entity.isPersistent() &&
        db.delete(s(getTable()), DbContext.ID + " = ?",
          new String[]{String.valueOf(entity.getId())}) > 0;
    }

    public long countAll() {
      return DatabaseUtils.queryNumEntries(db, s(getTable()));
    }

    public int insertAll(List<T> entities) {
      if (entities.isEmpty()) {
        return 0;
      }

      try {
        db.beginTransaction();
        int inserted = 0;

        if (!isBindingSupported()) {
          for (T entity : entities) {
            insert(entity);
            inserted++;
          }
        } else {
          SQLiteStatement statement = getInsertStatement();
          assert statement != null;
          for (T entity : entities) {
            bindInsertStatement(statement, entity);
            Long id = statement.executeInsert();
            entity.setId(id);
            inserted++;
          }
        }
        db.setTransactionSuccessful();

        return inserted;
      } finally {
        db.endTransaction();
      }
    }

    public int updateAll(List<T> entities) {
      if (entities.isEmpty()) {
        return 0;
      }

      try {
        db.beginTransaction();
        int updated = 0;

        if (!isBindingSupported()) {
          for (T entity : entities) {
            if (update(entity)) {
              updated++;
            }
          }
        } else {
          SQLiteStatement statement = getUpdateStatement();
          assert statement != null;
          for (T entity : entities) {
            bindUpdateStatement(statement, entity);
            updated += statement.executeUpdateDelete();
          }
        }
        db.setTransactionSuccessful();
        return updated;
      } finally {
        db.endTransaction();
      }
    }

    public int deleteAll(List<T> entities) {
      if (entities.isEmpty()) {
        return 0;
      }

      try {
        db.beginTransaction();
        int deleted = 0;

        if (!isBindingSupported()) {
          for (T entity : entities) {
            if (delete(entity)) {
              deleted++;
            }
          }
        } else {
          SQLiteStatement statement = getDeleteStatement();
          assert statement != null;
          for (T entity : entities) {
            bindDeleteStatement(statement, entity);
            deleted += statement.executeUpdateDelete();
          }
        }
        db.setTransactionSuccessful();
        return deleted;
      } finally {
        db.endTransaction();
      }
    }

    private boolean update(T entity) {
      return db.update(s(getTable()), entity.toContentValues(), DbContext.ID + " = ?",
        new String[]{String.valueOf(entity.getId())}) > 0;
    }

    private long insert(T entity) {
      Long id = db.insert(s(getTable()), null, entity.toContentValues());
      entity.setId(id);
      return id;
    }

    private void bindInsertStatement(SQLiteStatement statement, T entity) {
      statement.clearBindings();
      bind(statement, entity);
    }

    private void bindUpdateStatement(SQLiteStatement statement, T entity) {
      statement.clearBindings();
      bind(statement, entity);
      statement.bindLong(getColumnsCount() + 1, entity.getId());
    }

    private void bindDeleteStatement(SQLiteStatement statement, T entity) {
      statement.clearBindings();
      statement.bindLong(1, entity.getId());
    }

    private SQLiteStatement getInsertStatement() {
      if (insertStatement == null) {
        String insertSQL = DML.buildInsertStatement(getTable(), getColumns());
        insertStatement = db.compileStatement(insertSQL);
      }
      return insertStatement;
    }

    private SQLiteStatement getUpdateStatement() {
      if (updateStatement == null) {
        String updateSQL = DML.buildUpdateStatement(getTable(), getColumns(), Arrays.asList(DbContext.ID));
        updateStatement = db.compileStatement(updateSQL);
      }
      return updateStatement;
    }

    private SQLiteStatement getDeleteStatement() {
      if (deleteStatement == null) {
        String deleteSQL = DML.buildDeleteStatement(getTable(), Arrays.asList(DbContext.ID));
        deleteStatement = db.compileStatement(deleteSQL);
      }
      return deleteStatement;
    }

    private int getColumnsCount() {
      return getColumns().size();
    }

    protected abstract Table getTable();

    protected abstract List<Column> getColumns();

    protected abstract T map(Cursor cursor);

    protected abstract boolean isBindingSupported();

    protected abstract void bind(SQLiteStatement statement, T entity);
  }

  public static class DbContext {

    public static final Column ID = Column.create(BaseColumns._ID, Long.class, 0).primaryKey().autoincrement();

    protected String createStatement;
    protected String dropStatement;

    protected Map<Pair<Integer, Integer>, String> alterStatements = new HashMap<>();

    protected DbContext(String createStatement, String dropStatement) {
      this.createStatement = createStatement;
      this.dropStatement = dropStatement;
    }

    protected void addAlterStatement(int oldVersion, int newVersion, String alterStatement) {
      alterStatements.put(new Pair<>(oldVersion, newVersion), alterStatement);
    }

    protected boolean isUpgradeSupported(int oldVersion, int newVersion) {
      return alterStatements.containsKey(new Pair<>(oldVersion, newVersion));
    }

    protected String getCreateStatement() {
      return createStatement;
    }

    protected String getDropStatement() {
      return dropStatement;
    }

    protected String getAlterStatement(int oldVersion, int newVersion) {
      return alterStatements.get(new Pair<>(oldVersion, newVersion));
    }
  }

  /**
   * Representation of SQL row metadata.
   */
  public abstract static class Row {
    protected Long id;

    protected Row(Long id) {
      this.id = id;
    }

    public boolean isPersistent() {
      return id != null;
    }

    public Long getId() {
      return id;
    }

    protected void setId(Long id) {
      this.id = id;
    }

    public abstract ContentValues toContentValues();
  }

  /**
   * Representation of SQL column metadata.
   */
  public static class Column {

    private String name;
    private Class type;
    private int index;

    private int length;
    private boolean isNullable;
    private boolean isUnique;
    private boolean isPrimaryKey;
    private boolean isAutoincrement;

    private Column(String name, Class type, int index) {
      this.name = name;
      this.type = type;
      this.index = index;
      this.length = 0;
      this.isNullable = false;
      this.isUnique = false;
      this.isPrimaryKey = false;
      this.isAutoincrement = false;
    }

    private Column(Column column) {
      this.name = column.name;
      this.type = column.type;
      this.index = column.index;
      this.length = column.length;
      this.isNullable = column.isNullable;
      this.isUnique = column.isUnique;
      this.isPrimaryKey = column.isPrimaryKey;
      this.isAutoincrement = column.isAutoincrement;
    }

    public static Column create(String name, Class type, int index) {
      return new Column(name, type, index);
    }

    private static Column copy(Column column) {
      return new Column(column);
    }

    public Column withLength(int length) {
      Column column = copy(this);
      column.length = length;
      return column;
    }

    public Column nullable() {
      Column column = copy(this);
      column.isNullable = true;
      return column;
    }

    public Column unique() {
      Column column = copy(this);
      column.isUnique = true;
      return column;
    }

    public Column primaryKey() {
      Column column = copy(this);
      column.isPrimaryKey = true;
      column.isNullable = true; // to prevent appending 'NOT NULL' clause in DDL
      return column;
    }

    public Column autoincrement() {
      Column column = copy(this);
      column.isAutoincrement = true;
      return column;
    }

    public Class getType() {
      return type;
    }

    public int getIndex() {
      return index;
    }

    public boolean hasLength() {
      return length != 0;
    }

    public int getLength() {
      return length;
    }

    public boolean isNullable() {
      return isNullable;
    }

    public boolean isUnique() {
      return isUnique;
    }

    public boolean isPrimaryKey() {
      return isPrimaryKey;
    }

    public boolean isAutoincrement() {
      return isAutoincrement;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  /**
   * Representation of SQL table metadata.
   */
  public static class Table {

    private String name;
    private List<Column> columns;

    public Table(String name, List<Column> columns) {
      this.name = name;
      this.columns = columns;
    }

    public List<Column> getColumns() {
      return Collections.unmodifiableList(columns);
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public static class DML {

    public static String buildInsertStatement(Table table, List<Column> insertArgs) {
      int insertArgsNumber = insertArgs.size();
      if (insertArgsNumber < 1)
        throw new IllegalArgumentException("INSERT arguments list must be not empty!");

      StringBuilder intoArgs = new StringBuilder("(" + insertArgs.get(0));
      for (int i = 1; i < insertArgsNumber; i++) {
        intoArgs.append(", ").append(insertArgs.get(i));
      }
      intoArgs.append(")");

      StringBuilder values = new StringBuilder("(?");
      for (int i = 1; i < insertArgsNumber; i++) {
        values.append(", ?");
      }
      values.append(")");

      return String.format("INSERT INTO %1$s %2$s VALUES %3$s", table, intoArgs.toString(), values.toString()).trim();
    }

    public static String buildSelectStatement(Table table, List<Column> selectArgs, List<Column> whereArgs) {
      int selectArgsNumber = selectArgs.size();
      if (selectArgsNumber < 1)
        throw new IllegalArgumentException("SELECT arguments list must be not empty!");

      StringBuilder selectClause = new StringBuilder(s(selectArgs.get(0)));
      for (int i = 1; i < selectArgsNumber; i++) {
        selectClause.append(", ").append(selectArgs.get(i));
      }

      int whereArgsNumber = whereArgs.size();
      StringBuilder whereClause = new StringBuilder();
      if (whereArgsNumber > 0)
        whereClause.append("WHERE ").append(whereArgs.get(0)).append(" = ?");
      for (int i = 1; i < whereArgsNumber; i++) {
        whereClause.append(" AND ").append(whereArgs.get(i)).append(" = ?");
      }

      return String.format("SELECT %2$s FROM %1$s %3$s", table, selectClause.toString(), whereClause.toString()).trim();
    }

    public static String buildUpdateStatement(Table table, List<Column> updateArgs, List<Column> whereArgs) {
      int updateArgsNumber = updateArgs.size();
      if (updateArgsNumber < 1)
        throw new IllegalArgumentException("UPDATE arguments list must be not empty!");

      StringBuilder setArgs = new StringBuilder(updateArgs.get(0) + " = ?");
      for (int i = 1; i < updateArgsNumber; i++) {
        setArgs.append(", ").append(updateArgs.get(i)).append(" = ?");
      }

      int whereArgsNumber = whereArgs.size();
      StringBuilder whereClause = new StringBuilder();
      if (whereArgsNumber > 0)
        whereClause.append("WHERE ").append(whereArgs.get(0)).append(" = ?");
      for (int i = 1; i < whereArgsNumber; i++) {
        whereClause.append(" AND ").append(whereArgs.get(i)).append(" = ?");
      }

      return String.format("UPDATE %1$s SET %2$s %3$s", table, setArgs.toString(), whereClause.toString()).trim();
    }

    public static String buildDeleteStatement(Table table, List<Column> whereArgs) {
      int whereArgsNumber = whereArgs.size();
      StringBuilder whereClause = new StringBuilder();
      if (whereArgsNumber > 0)
        whereClause.append("WHERE ").append(whereArgs.get(0)).append(" = ?");
      for (int i = 1; i < whereArgsNumber; i++) {
        whereClause.append(" AND ").append(whereArgs.get(i)).append(" = ?");
      }

      return String.format("DELETE FROM %1$s %2$s", table, whereClause.toString()).trim();
    }
  }

  public static class DDL {

    public static String buildCreateStatement(Table table) {
      String tableName = s(table);
      List<Column> columns = table.getColumns();
      int columnsNumber = columns.size();
      if (columnsNumber > 0) {
        StringBuilder columnsDescription = new StringBuilder(describeColumn(columns.get(0)));
        for (int i = 1; i < columnsNumber; i++) {
          columnsDescription.append(", ").append(describeColumn(columns.get(i)));
        }
        return String.format("CREATE TABLE IF NOT EXISTS %1$s (%2$s)", tableName, columnsDescription.toString());

      } else {
        throw new IllegalArgumentException("Table " + tableName + " does not contain any columns!");
      }
    }

    public static String buildDropStatement(Table table) {
      return "DROP TABLE IF EXISTS " + table;
    }

    private static String describeColumn(Column column) {
      StringBuilder columnDescription = new StringBuilder("`" + column + "`");
      columnDescription.append(" ").append(describeType(column));
      if (column.isUnique()) {
        columnDescription.append(" ").append("UNIQUE");
      }
      if (!column.isNullable()) {
        columnDescription.append(" ").append("NOT NULL");
      }
      if (column.isPrimaryKey()) {
        columnDescription.append(" ").append("PRIMARY KEY");
        if (column.isAutoincrement()) {
          columnDescription.append(" ").append("AUTOINCREMENT");
        }
      }
      return columnDescription.toString();
    }

    private static String describeType(Column column) {
      Class type = column.getType();
      if (type == Integer.class || type == Long.class) {
        return "INTEGER";
      } else if (type == String.class) {
        String length = column.hasLength() ? String.valueOf(column.getLength()) : "255";
        return String.format("VARCHAR(%s)", length);
      } else if (type == Timestamp.class || type == Date.class) {
        return "TIMESTAMP";
      } else {
        return "BLOB";
      }
    }
  }
}