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

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import jp.co.oneteam.phonebook.ui.InstanceState;
import jp.co.oneteam.phonebook.ui.InstanceStateAnnotations;

public class ExampleActivity extends Activity{

    @InstanceState
    Uri exmpleUri;
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        InstanceStateAnnotations.saveInstanceState(this, outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        InstanceStateAnnotations.restoreInstanceState(this, savedInstanceState);
    }
}
