import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
public class ExpiringLruCacheTest {

    @Test
    public void get_shouldReturnValueNonExpiredKeys() {
        ExpiringLruCache<String, String> cache = spy(new ExpiringLruCache<String, String>(2, 1000));

        // Puts key expiry at 1000 + 500 => 1500
        doReturn(500L).when(cache).elapsedRealtime();
        cache.put("a", "A");

        // Puts key expiry at 1000 + 600 => 1600
        doReturn(600L).when(cache).elapsedRealtime();
        cache.put("b", "B");

        // Increase the time to just under the expiry time
        doReturn(1499L).when(cache).elapsedRealtime();
        assertThat(cache.get("a")).isEqualTo("A");

        // Increase the time to just under the expiry time
        doReturn(1599L).when(cache).elapsedRealtime();
        assertThat(cache.get("b")).isEqualTo("B");
    }

    @Test
    public void get_shouldReturnNullForExpiredKeys() {
        ExpiringLruCache<String, String> cache = spy(new ExpiringLruCache<String, String>(2, 1000));

        // Puts key expiry at 1000 + 500 => 1500
        doReturn(500L).when(cache).elapsedRealtime();
        cache.put("a", "A");

        // Puts key expiry at 1000 + 600 => 1600
        doReturn(600L).when(cache).elapsedRealtime();
        cache.put("b", "B");

        // Increase the time to the expiry time
        doReturn(1500L).when(cache).elapsedRealtime();
        assertThat(cache.get("a")).isNull();

        // Increase the time to the expiry time
        doReturn(1600L).when(cache).elapsedRealtime();
        assertThat(cache.get("b")).isNull();
    }

    @Test
    public void removingCachedKey_shouldRemoveExpiryCacheEntryForKey() {
        ExpiringLruCache<String, String> cache = spy(new ExpiringLruCache<String, String>(1, 1000));

        doReturn(500L).when(cache).elapsedRealtime();
        cache.put("a", "A");

        assertThat(cache.getExpiryTime("a")).isEqualTo(1500L);

        cache.removeExpiryTime("a");
        assertThat(cache.getExpiryTime("a")).isZero();
    }

    @Test
    public void exceedingMaxSize_shouldEvictLeastRecentlyUsedEntry_andRemoveExpiryCacheEntryForKey() {
        ExpiringLruCache<String, String> cache = spy(new ExpiringLruCache<String, String>(3, 1000));

        // Puts key expiry at 1000 + 500 => 1500
        doReturn(500L).when(cache).elapsedRealtime();
        cache.put("a", "A");

        // Puts key expiry at 1000 + 600 => 1600
        doReturn(600L).when(cache).elapsedRealtime();
        cache.put("b", "B");

        // Puts key expiry at 1000 + 700 => 1700
        doReturn(700L).when(cache).elapsedRealtime();
        cache.put("c", "C");

        // We are at 3, which is our max. Let's "use" a few keys
        cache.get("c");
        cache.get("c");
        cache.get("b");
        cache.get("a");
        cache.get("c");

        // Now add another, which should evict 'b'
        cache.put("d", "D");

        assertThat(cache.get("b")).isNull();
        assertThat(cache.getExpiryTime("b")).isZero();
    }
}