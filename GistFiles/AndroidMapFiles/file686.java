package com.wantedly.android.profile.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cattaka on 2016/08/03.
 */
public class ClassOrderComparator implements Comparator<Object> {
    public static ClassOrderComparator newInstance(Class<?>... classes) {
        Map<Class<?>, Integer> rankMap = new HashMap<>();
        int rank = 0;
        for (Class<?> clazz : classes) {
            if (clazz == null) {
                continue;
            }
            rankMap.put(clazz, rank);
            rank++;
        }
        return new ClassOrderComparator(rankMap);
    }

    private Map<Class<?>, Integer> mRankMap;

    private ClassOrderComparator(Map<Class<?>, Integer> rankMap) {
        mRankMap = rankMap;
    }

    @Override
    public int compare(Object o1, Object o2) {
        Class<?> c1 = (o1 != null) ? o1.getClass() : null;
        Class<?> c2 = (o2 != null) ? o2.getClass() : null;

        Integer r1 = (c1 != null) ? mRankMap.get(c1) : null;
        Integer r2 = (c2 != null) ? mRankMap.get(c2) : null;
        if (r1 == null) {
            r1 = mRankMap.size();
        }
        if (r2 == null) {
            r2 = mRankMap.size();
        }

        return r1 - r2;
    }
}
