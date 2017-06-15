package utils.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.apache.commons.lang.StringUtils;
import utils.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * <p/>
 * Date: 14-2-3
 * Author: Administrator
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public static String DB_NAME = System.getProperty("clint.db");

    public MySQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    public MySQLiteOpenHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public String getTableDef(Class poClazz) {
        String tablename = getTableName(ObjectUtils.getCanonicalClazz(poClazz));
        StringBuffer sb = new StringBuffer();
        Field[] fields = poClazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Transient.class)) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(field.getName());
                Class type = field.getType();
                if (String.class.equals(type)) {
                    sb.append(" TEXT");
                } else if (Integer.class.equals(type)) {
                    sb.append(" INTEGER");
                } else if (Double.class.equals(type) || Float.class.equals(type)) {
                    sb.append(" REAL");
                } else if (byte[].class.equals(type)) {
                    sb.append(" BLOB");
                }
                boolean isKey = field.isAnnotationPresent(Key.class);
                if (isKey) {
                    sb.append(" primary key");
                    boolean isAutoGen = field.isAnnotationPresent(AutoGen.class);
                    if (isAutoGen) {
                        sb.append(" autoincrement");
                    }
                }
            }
        }
        return "create table " + tablename + "(" + sb.toString() + ")";
    }

    public void insert(Object po) throws IllegalAccessException {
        SQLiteDatabase db = this.getWritableDatabase();
        Class poClazz = po.getClass();
        String tablename = getTableName(ObjectUtils.getCanonicalClazz(poClazz));
        ContentValues cv = new ContentValues();
        Field[] fields = poClazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Transient.class)) {
                Object value = ObjectUtils.getFieldValue(po, field.getName());
                if (value != null) {
                    put2cv(cv, field.getName(), value);
                }
            }
        }
        db.insert(tablename, null, cv);
    }

    public void execSQL(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    public void update(Object updatePO, Object criteriaPO) throws IllegalAccessException {
        update(updatePO, criteriaPO, false);
    }

    public void update(Object updatePO, Object criteriaPO, boolean withNull) throws IllegalAccessException {
        SQLiteDatabase db = this.getWritableDatabase();
        Class poClazz = criteriaPO.getClass();
        String tablename = getTableName(ObjectUtils.getCanonicalClazz(poClazz));
        ContentValues cv = new ContentValues();
        StringBuffer where = new StringBuffer();
        List<String> whereArgs = new ArrayList<String>();
        Field[] fields = poClazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Transient.class)) {
                Object value = ObjectUtils.getFieldValue(criteriaPO, field.getName());
                if (value != null) {
                    if (where.length() > 0) {
                        where.append(" and ");
                    }
                    String strValue = value.toString();
                    String operator = " = ";
                    if (strValue.contains("%")) {
                        operator = " like ";
                    }
                    where.append(field.getName() + operator + "?");
                    whereArgs.add(strValue);
                }
                value = ObjectUtils.getFieldValue(updatePO, field.getName());
                if (value != null || withNull) {
                    put2cv(cv, field.getName(), value);
                }
            }
        }
        db.update(tablename, cv, where.toString(), whereArgs.toArray(new String[whereArgs.size()]));
    }

    public List<Object> query(Object po) throws IllegalAccessException, InstantiationException {
        Class poClazz = po.getClass();
        String tablename = getTableName(ObjectUtils.getCanonicalClazz(poClazz));
        StringBuffer where = new StringBuffer();
        List<String> whereArgs = new ArrayList<String>();
        Field[] fields = poClazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Transient.class)) {
                Object value = ObjectUtils.getFieldValue(po, field.getName());
                if (value != null) {
                    if (where.length() > 0) {
                        where.append(" and ");
                    }
                    String strValue = value.toString();
                    String operator = " = ";
                    if (strValue.contains("%")) {
                        operator = " like ";
                    }
                    where.append(field.getName() + operator + "?");
                    whereArgs.add(strValue);
                }
            }
        }
        return query("select * from " + tablename + " where " + where.toString(),
                whereArgs.toArray(new String[whereArgs.size()]), poClazz);
    }

    public List<Object> query(String sql, String[] args, Class poClazz)
            throws IllegalAccessException, InstantiationException {
        List<Object> resList = new ArrayList<Object>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        while (cursor.moveToNext()) {
            int columnCount = cursor.getColumnCount();
            Object po = poClazz.newInstance();
            for (int i = 0; i < columnCount; i++) {
                String columName = cursor.getColumnName(i);
                ObjectUtils.setFieldValue(po, columName, getColumnValue(cursor, i));
            }
            resList.add(po);
        }
        return resList;
    }

    public void delete(Object po) throws IllegalAccessException {
        Class poClazz = po.getClass();
        SQLiteDatabase db = this.getWritableDatabase();
        String tablename = getTableName(ObjectUtils.getCanonicalClazz(poClazz));
        Field[] fields = poClazz.getDeclaredFields();
        StringBuffer where = new StringBuffer();
        List<String> whereArgs = new ArrayList<String>();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Transient.class)) {
                Object value = ObjectUtils.getFieldValue(po, field.getName());
                if (value != null) {
                    if (where.length() > 0) {
                        where.append(" and ");
                    }
                    String strValue = value.toString();
                    String operator = " = ";
                    if (strValue.contains("%")) {
                        operator = " like ";
                    }
                    where.append(field.getName() + operator + "?");
                    whereArgs.add(strValue);
                }
            }
        }
        db.delete(tablename, where.toString(), whereArgs.toArray(new String[whereArgs.size()]));
    }

    public String getTableName(Class poClazz) {
        String tablename = null;
        if (poClazz.isAnnotationPresent(Table.class)) {
            Table table = (Table) poClazz.getAnnotation(Table.class);
            if (!StringUtils.isEmpty(table.name())) {
                tablename = table.name().toLowerCase();
            }
        }
        if (StringUtils.isEmpty(tablename)) {
            tablename = StringUtils.replace(poClazz.getSimpleName(), "PO", "").toLowerCase();
        }
        return tablename;
    }

    public void dropTable(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DROP TABLE IF EXISTS" + table;
        db.execSQL(sql);
    }

    private void put2cv(ContentValues cv, String field, Object value) {
        if (value instanceof String) {
            cv.put(field, (String) value);
        } else if (value instanceof Integer) {
            cv.put(field, (Integer) value);
        } else if (value instanceof Long) {
            cv.put(field, (Long) value);
        } else if (value instanceof Float) {
            cv.put(field, (Float) value);
        } else if (value instanceof Double) {
            cv.put(field, (Double) value);
        } else if (value instanceof Boolean) {
            cv.put(field, (Boolean) value);
        } else if (value instanceof byte[]) {
            cv.put(field, (byte[]) value);
        } else if (value instanceof Byte) {
            cv.put(field, (Byte) value);
        } else if (value instanceof Short) {
            cv.put(field, (Short) value);
        }
    }

    private Object getColumnValue(Cursor cursor, int columnIndex) {
        Object res = null;
        switch (cursor.getType(columnIndex)) {
            case Cursor.FIELD_TYPE_NULL:
                // don't put anything
                break;
            case Cursor.FIELD_TYPE_INTEGER:
                res = cursor.getInt(columnIndex);
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                res = cursor.getFloat(columnIndex);
                break;
            case Cursor.FIELD_TYPE_STRING:
                res = cursor.getString(columnIndex);
                break;
            case Cursor.FIELD_TYPE_BLOB:
                res = cursor.getBlob(columnIndex);
                break;
            default:
                throw new IllegalStateException("Invalid or unhandled data type");
        }
        return res;
    }
}
