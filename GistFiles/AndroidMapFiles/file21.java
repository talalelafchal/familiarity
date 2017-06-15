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
package com.uphyca.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @see InstanceStateAnnotations
 */
@RunWith(AndroidJUnit4.class)
public class InstanceStateAnnotationsTest {

    static class TestTarget {
        @InstanceState
        String stringField;
        @InstanceState
        int intField;
        @InstanceState
        Long longWrapperField;
        @InstanceState
        Uri uriField;
        @InstanceState
        Intent intentField;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestTarget target = (TestTarget) o;

            if (intField != target.intField) return false;
            if (stringField != null ? !stringField.equals(target.stringField) : target.stringField != null)
                return false;
            if (longWrapperField != null ? !longWrapperField.equals(target.longWrapperField) : target.longWrapperField != null)
                return false;
            if (uriField != null ? !uriField.equals(target.uriField) : target.uriField != null)
                return false;
            return !(intentField != null ? !intentField.equals(target.intentField) : target.intentField != null);

        }

        @Override
        public int hashCode() {
            int result = stringField != null ? stringField.hashCode() : 0;
            result = 31 * result + intField;
            result = 31 * result + (longWrapperField != null ? longWrapperField.hashCode() : 0);
            result = 31 * result + (uriField != null ? uriField.hashCode() : 0);
            result = 31 * result + (intentField != null ? intentField.hashCode() : 0);
            return result;
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void saveAndRestoreInstanceState() throws Exception {
        TestTarget target = new TestTarget();

        target.stringField = "string";
        target.intField = Integer.MAX_VALUE;
        target.longWrapperField = Long.MAX_VALUE;
        target.uriField = Uri.parse("http://example.com");
        target.intentField = new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com"));

        Bundle outState = new Bundle();
        InstanceStateAnnotations.saveInstanceState(target, outState);

        final TestTarget restoreTarget = new TestTarget();
        InstanceStateAnnotations.restoreInstanceState(restoreTarget, outState);

        assertThat(restoreTarget).isEqualTo(target);
    }
}