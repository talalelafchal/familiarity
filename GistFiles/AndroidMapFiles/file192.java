package com.your.package;

import java.util.ArrayList;
import java.util.List;

/**
 * MapperUtils Class
 *
 * Extracted from Facebook Android API by Jorge Garrido Oval <firezenk@gmail.com>
 * Use case:
 * 
 * simpleIntegerList = MapperUtils.map(complexObjectList, new MapperUtils.Mapper<ComplexObject, Integer>() {
 *        @Override
 *        public Integer apply(ComplexObject item) {
 *          return item.id;
 *        }
 * });
 *
 * @author firezenk
 * @date 29/10/2015
 *
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class MapperUtils {

    public interface Mapper<T, K> {
        K apply(T item);
    }

    public static <T, K> List<K> map(final List<T> target, final Mapper<T, K> mapper) {
        if (target == null) {
            return null;
        }
        final List<K> list = new ArrayList<K>();
        for (T item : target) {
            final K mappedItem = mapper.apply(item);
            if (mappedItem != null) {
                list.add(mappedItem);
            }
        }
        return (list.size() == 0 ? null : list);
    }

}