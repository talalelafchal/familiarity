/*
 * Copyright (c) 2016 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package th.or.nectec.maps;


import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class ThaichoteMapTile extends UrlTileProvider {

    private static final String TILE_URL =
            "http://giportal.gistda.or.th/arcgis/rest/services/raster/thaichote_update_2014_2016/MapServer/tile/%d/%d/%d";

    public ThaichoteMapTile() {
        super(256, 256);
    }

    @Override
    public URL getTileUrl(int x, int y, int zoom) {
        String tileUrl = String.format(TILE_URL, zoom, y, x);
        URL url;
        try {
            url = new URL(tileUrl);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        return url;
    }

    public static TileOverlayOptions createTileOverlayOption() {
        return new TileOverlayOptions()
                .tileProvider(new ThaichoteMapTile())
                .fadeIn(true);
    }
}
