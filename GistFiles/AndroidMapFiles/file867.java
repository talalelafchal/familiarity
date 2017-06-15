
/**
 * implementation of PkgUsageStats associated with an
 * application package.
 *  @hide
 */
public class PkgUsageStats implements Parcelable {
    public String packageName;
    public int launchCount;
    public long usageTime;
    public Map<String, Long> componentResumeTimes;

    public PkgUsageStats(String pkgName, int count, long time, Map<String, Long> lastResumeTimes) {
        packageName = pkgName;
        launchCount = count;
        usageTime = time;
        componentResumeTimes = new HashMap<String, Long>(lastResumeTimes);
    }

    // Parcelableの実装があります。。
}

