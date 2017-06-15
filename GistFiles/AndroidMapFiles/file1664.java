/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.toastkid.search_widget.R;
import okio.Okio;

class LicenseViewer {

    public static final String DIRECTORY_OF_LICENSES = "licenses";

    private Context mContext;

    LicenseViewer(@NonNull final Context context) {
        mContext = context;
    }

    void invoke() {
        try {
            final AssetManager assets = mContext.getAssets();
            final String[] licenseFiles = assets.list(DIRECTORY_OF_LICENSES);
            final Map<String, String> licenseMap = new LinkedHashMap<>(licenseFiles.length);
            for (final String fileName : licenseFiles) {
                final InputStream stream = assets.open(DIRECTORY_OF_LICENSES + "/" + fileName);
                licenseMap.put(fileName.substring(0, fileName.lastIndexOf(".")), readUtf8(stream));
                stream.close();
            }
            final String[] items = licenseMap.keySet().toArray(new String[]{});
            new AlertDialog.Builder(mContext).setTitle(R.string.title_licenses)
                    .setItems(items, (d, index) ->
                            new AlertDialog.Builder(mContext)
                                    .setTitle(items[index])
                                    .setMessage(licenseMap.get(items[index]))
                                    .setCancelable(true)
                                    .setPositiveButton(R.string.close, (dialog, i) -> dialog.dismiss())
                                    .show()
                    )
                    .setCancelable(true)
                    .setPositiveButton(R.string.close, (d, i) -> d.dismiss())
                    .show();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private String readUtf8(final InputStream i) throws IOException {
        return Okio.buffer(Okio.source(i)).readUtf8();
    }
}