
// 期間が1日なのに、intervalTypeを年単位にしてしまった
int intervalType = UsageStatsManager.INTERVAL_YEARLY;
long start = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
long end = System.currentTimeMillis();

// ついてませんが、@Nullableです
List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(intervalType, start, end);

:
usageStatsList.get(0).getLastTimeStamp();
// 1430907218507
// firstTimeStampは大丈夫
usageStatsList.get(0).getFirstTimeStamp();
// 2295981492
// 変に丸められてる？
usageStatsList.get(1).getLastTimeStamp();
// 1430907218507
usageStatsList.get(1).getFirstTimeStamp();
// 999981492
// 変に丸められてる？
usageStatsList.get(2).getLastTimeStamp();
// 1430907218507
usageStatsList.get(2).getFirstTimeStamp();
// 1433218181823
// 大丈夫なときもある
:

