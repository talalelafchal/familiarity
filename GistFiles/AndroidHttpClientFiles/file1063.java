package com.android.handsfree.data;

/**
 * @author Denis_Zinkovskiy.
 */
public interface RestClient {
    <T> T create(Class<T> clazz);
}
