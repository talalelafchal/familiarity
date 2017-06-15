package edu.ualr.cpsc7398.updatechecker.controller.service.utils;

/**
 * Created by uddhav on 2/25/17.
 */

public class DataBean {
    public static String url;
    public static int seed;
    public static int totalUpdates;

    public static String uniqueLastModified;
    public static long modifiedInterval;
    public static long largestInterval;
    public static long leastInterval;

    public DataBean() {
        uniqueLastModified = "";
        modifiedInterval = 0;
        largestInterval = 0;
        leastInterval = 0;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        DataBean.url = url;
    }

    public static int getSeed() {
        return seed;
    }

    public static void setSeed(int seed) {
        DataBean.seed = seed;
    }

    public static int getTotalUpdates() {
        return totalUpdates;
    }

    public static void setTotalUpdates(int totalUpdates) {
        DataBean.totalUpdates = totalUpdates;
    }

    public static String getUniqueLastModified() {
        return uniqueLastModified;
    }

    public static void setUniqueLastModified(String uniqueLastModified) {
        DataBean.uniqueLastModified = uniqueLastModified;
    }

    public static long getModifiedInterval() {
        return modifiedInterval;
    }

    public static void setModifiedInterval(long modifiedInterval) {
        DataBean.modifiedInterval = modifiedInterval;
    }

    public static long getLargestInterval() {
        return largestInterval;
    }

    public static void setLargestInterval(long largestInterval) {
        DataBean.largestInterval = largestInterval;
    }

    public static long getLeastInterval() {
        return leastInterval;
    }

    public static void setLeastInterval(long leastInterval) {
        DataBean.leastInterval = leastInterval;
    }
}
