package com.pengyan.collection;

import java.util.EnumSet;

/**
 * Created by pengyan on 2017-02-28.
 */
public class EnumSetDemo {
    public enum Color {
        BLACK, WHITE, RED, BLUR, GREEN, YELLOW
    }

    public static void useEnumSet() {
        EnumSet<Color> set = EnumSet.noneOf(Color.class);
        set.add(Color.BLACK);
        set.add(Color.BLUR);
        System.out.println(set.toString());
    }

    public static void main(String[] args) {
        useEnumSet();
    }

}
