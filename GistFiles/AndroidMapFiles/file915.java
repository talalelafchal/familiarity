import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestTypeSafeSharedPreferences {

    private SharedPreferences mSharedPref;

    @Before
    public void setup() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        mSharedPref = new TypeSafeSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context));
    }

    @Test
    public void testLong() {
        clear();

        mSharedPref.edit().putLong("long", 42).apply();
        test("long", 42);
        test("long", 42L);
        test("long", 42f);
    }

    @Test
    public void testInt() {
        clear();

        mSharedPref.edit().putInt("int", 42).apply();
        test("int", 42);
        test("int", 42L);
        test("int", 42f);
    }

    @Test
    public void testFloat() {
        clear();

        mSharedPref.edit().putFloat("float", 42).apply();
        test("float", 42);
        test("float", 42L);
        test("float", 42f);
    }

    @Test
    public void testString() {
        clear();

        mSharedPref.edit().putString("string", "42").apply();
        test("string", 42);
        test("string", 42L);
        test("string", 42f);
    }

    @SuppressWarnings("ConstantConditions")
    private void test(String key, int value) {
        assertEquals(value, mSharedPref.getInt(key, 0));
        assertEquals((long)value, mSharedPref.getLong(key, 0));
        assertEquals((float)value, mSharedPref.getFloat(key, 0));
        assertEquals(value, (int) (double) Double.parseDouble(mSharedPref.getString(key, null)));
        assertEquals(value != 0, mSharedPref.getBoolean(key, value == 0));
    }

    @SuppressWarnings("ConstantConditions")
    private void test(String key, long value) {
        assertEquals((int) value, mSharedPref.getInt(key, 0));
        assertEquals(value, mSharedPref.getLong(key, 0));
        assertEquals((float)value, mSharedPref.getFloat(key, 0));
        assertEquals(value, (long) (double) Double.parseDouble(mSharedPref.getString(key, null)));
        assertEquals(value != 0, mSharedPref.getBoolean(key, value == 0));
    }

    @SuppressWarnings("ConstantConditions")
    private void test(String key, float value) {
        assertEquals((int)value, mSharedPref.getInt(key, 0));
        assertEquals((long)value, mSharedPref.getLong(key, 0));
        assertEquals(value, mSharedPref.getFloat(key, 0));
        assertEquals(value, (float) (double) Double.parseDouble(mSharedPref.getString(key, null)));
        assertEquals(value != 0, mSharedPref.getBoolean(key, value == 0));
    }


    private void clear() {
        mSharedPref.edit().clear().apply();
    }
}