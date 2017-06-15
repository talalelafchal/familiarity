/*
 * Copyright (C) 2015 uPhyca Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.oneteam.phonebook.ui;

import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InstanceStateの保存・復元を行うユーティリティクラス
 *
 * @see InstanceState
 * @see android.app.Activity#onSaveInstanceState(Bundle)
 * @see android.app.Activity#onRestoreInstanceState(Bundle)
 */
public abstract class InstanceStateAnnotations {

    private static class MetaInfo {
        final Field field;
        final Method putMethod;
        final Method getMethod;

        public MetaInfo(Field field, Method putMethod, Method getMethod) {
            this.field = field;
            this.putMethod = putMethod;
            this.getMethod = getMethod;
        }
    }

    private static final Map<Class<?>, List<MetaInfo>> metaInfoListMap = new HashMap<>();

    private InstanceStateAnnotations() {
        throw new UnsupportedOperationException("No instances");
    }

    public static void saveInstanceState(Object target, Bundle outState) {
        Bundle myState = new Bundle();
        final Class<?> clazz = target.getClass();
        for (MetaInfo each : getMetaInfoList(clazz)) {
            try {
                each.putMethod.invoke(myState, each.field.getName(), each.field.get(target));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
        outState.putBundle(clazz.getCanonicalName(), myState);
    }

    public static void restoreInstanceState(Object target, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        final Class<?> clazz = target.getClass();
        Bundle myState = savedInstanceState.getBundle(clazz.getCanonicalName());
        if (myState == null) {
            return;
        }
        for (MetaInfo each : getMetaInfoList(clazz)) {
            try {
                each.field.set(target, each.getMethod.invoke(myState, each.field.getName()));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static List<MetaInfo> getMetaInfoList(Class<?> clazz) {
        List<MetaInfo> metaInfoList = metaInfoListMap.get(clazz);
        if (metaInfoList != null) {
            return metaInfoList;
        }
        metaInfoList = buildMetaInfoList(clazz);
        metaInfoListMap.put(clazz, metaInfoList);
        return metaInfoList;
    }

    private static List<MetaInfo> buildMetaInfoList(Class<?> clazz) {
        List<MetaInfo> metaInfoList = new ArrayList<>();
        for (Field each : clazz.getDeclaredFields()) {
            if (!each.isAnnotationPresent(InstanceState.class)) {
                continue;
            }
            final Method putMethod = findPutMethod(each.getType());
            if (putMethod == null) {
                throw new IllegalStateException("No put method found for " + each.getType().getName());
            }
            final Method getMethod = findGetMethod(each.getType());
            if (getMethod == null) {
                throw new IllegalStateException("No get method found for " + each.getType().getName());
            }
            metaInfoList.add(new MetaInfo(each, putMethod, getMethod));
        }
        return metaInfoList;
    }

    private static Method findPutMethod(Class<?> type) {
        for (Method each : Bundle.class.getMethods()) {
            if (!each.getName().startsWith("put")) {
                continue;
            }
            final Class<?>[] parameterTypes = each.getParameterTypes();
            if (parameterTypes.length != 2) {
                continue;
            }
            if (!parameterTypes[0].equals(String.class)) {
                continue;
            }
            if (!parameterTypes[1].isAssignableFrom(type)) {
                continue;
            }
            return each;
        }
        return null;
    }

    private static Method findGetMethod(Class<?> type) {
        for (Method each : Bundle.class.getMethods()) {
            if (!each.getName().startsWith("get")) {
                continue;
            }
            final Class<?>[] parameterTypes = each.getParameterTypes();
            if (parameterTypes.length != 1) {
                continue;
            }
            if (!parameterTypes[0].equals(String.class)) {
                continue;
            }
            final Class<?> returnType = each.getReturnType();
            if (!returnType.isAssignableFrom(type)) {
                continue;
            }
            return each;
        }
        return null;
    }
}
