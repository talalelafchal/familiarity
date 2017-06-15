import android.support.annotation.Nullable;

import com.fernandocejas.arrow.strings.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for a static google maps URL. Must be initialized with a valid API key with the google maps service enabled.
 *
 * Example:
 * https://maps.googleapis.com/maps/api/staticmap?maptype=hybrid&center=45.002340,-93.210022&zoom=17&size=640x640&markers=45.002340,-93.210022&key={$key}&style=feature:poi%7Cvisibility:off&style=feature:transit%7Cvisibility:off
 */
public class StaticMapBuilder {
    private static final String MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?";
    private static final String KEY_PARAM = "key=";
    private static final String TYPE_PARAM = "maptype=";
    private static final String ZOOM_PARAM = "zoom=";
    private static final String CENTER_PARAM = "center=";
    private static final String STYLE_PARAM = "style=";
    private static final String MARKER_PARAM = "markers=";
    private static final String SIZE_PARAM = "size=";
    private static final String SCALE_PARAM = "scale=";
    private static final String FORMAT_PARAM = "format=";

    // UTF-8 characters needed in URL
    private static final String URL_AMP = "&";
    private static final String URL_PIPE = "|";

    private final String googleApiKey;

    private Size size;
    private MapScale scale;
    private ImageFormat imageFormat;

    private MapType mapType;

    private String zoom;
    private Point center;
    private List<Marker> markers;
    private List<MapStyle> styles;

    private List<String> manualParams;

    public StaticMapBuilder(String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    /**
     * Sets the size of the map image to be returned. Valid values depend on if the maps api key is from a paid account or not.
     */
    public StaticMapBuilder setSize(int height, int width) {
        this.size = new Size(height, width);
        return this;
    }

    /**
     * Sets the scale of the image being returned. For example {@link MapScale#DOUBLE} downsamples an image that's twice the size of the value specified.
     *
     * Note that {@link MapScale#QUAD} is only valid for a paid maps account
     */
    public StaticMapBuilder setScale(MapScale scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Sets the image format to one of the predefined {@link ImageFormat}
     */
    public StaticMapBuilder setFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    /**
     * Sets the map type to one of the types defined in {@link MapType}
     */
    public StaticMapBuilder setMapType(MapType mapType) {
        this.mapType = mapType;
        return this;
    }

    /**
     * Sets the zoom to a value between 0 and 20
     */
    public StaticMapBuilder setZoom(int zoom) {
        if (zoom < 0 || zoom > 20) {
            throw new IllegalArgumentException("Zoom cannot be above 20");
        }

        // saving this as a string to let if be null by default without using an autoboxed class
        this.zoom = Integer.toString(zoom);
        return this;
    }

    /**
     * Sets the center point of the map. This can be excluded if two or more markers are present
     */
    public StaticMapBuilder setCenterPoint(double lat, double lon) {
        this.center = new Point(lat, lon);
        return this;
    }

    /**
     * Adds a default marker at the specified location
     */
    public StaticMapBuilder addMarker(double lat, double lon) {
        if (markers == null) {
            markers = new ArrayList<>();
        }

        markers.add(new Marker(new Point(lat, lon), null, null, null, null, null));

        return this;
    }

    /**
     * size: (optional) specifies the size of marker from the set {tiny, mid, small}. If no size parameter is set, the marker will appear in its default (normal) size.
     *
     * color: (optional) specifies a 24-bit color (example: color=0xFFFFCC) or a predefined color from the set {black, brown, green, purple, yellow, blue, gray, orange, red, white}.
     *
     * label: (optional) specifies a single uppercase alphanumeric character from the set {A-Z, 0-9}. (The requirement for uppercase characters is new to this version of the API.) Note that default and mid sized markers are the only markers capable of displaying an alphanumeric-character parameter. tiny and small markers are not capable of displaying an alphanumeric-character.
     */
    public StaticMapBuilder addMarker(double lat, double lon, @Nullable MarkerSize markerSize, @Nullable String color, @Nullable String label) {
        if (markers == null) {
            markers = new ArrayList<>();
        }

        markers.add(new Marker(new Point(lat, lon), markerSize, label, color, null, null));

        return this;
    }

    /**
     * size: (optional) specifies the size of marker from the set {tiny, mid, small}. If no size parameter is set, the marker will appear in its default (normal) size.
     *
     * color: (optional) specifies a 24-bit color (example: color=0xFFFFCC) or a predefined color from the set {black, brown, green, purple, yellow, blue, gray, orange, red, white}.
     *
     * label: (optional) specifies a single uppercase alphanumeric character from the set {A-Z, 0-9}. (The requirement for uppercase characters is new to this version of the API.) Note that default and mid sized markers are the only markers capable of displaying an alphanumeric-character parameter. tiny and small markers are not capable of displaying an alphanumeric-character.
     */
    public StaticMapBuilder addMarker(double lat, double lon, @Nullable MarkerSize markerSize, MarkerColors color, @Nullable String label) {
        if (markers == null) {
            markers = new ArrayList<>();
        }

        markers.add(new Marker(new Point(lat, lon), markerSize, label, color.value, null, null));

        return this;
    }

    /**
     * icon:URLofIcon
     *
     * Set the anchor as an x,y point of the icon (such as 10,5), or as a predefined alignment using one of the following values: top, bottom, left, right, center, topleft, topright, bottomleft, or bottomright.
     *
     * For example:
     * markers=anchor:bottomright|icon:URLofIcon|markerLocation1|markerLocation2
     */
    public StaticMapBuilder addMarker(double lat, double lon, String url, @Nullable CustomMarkerAnchor anchor) {
        if (markers == null) {
            markers = new ArrayList<>();
        }

        markers.add(new Marker(new Point(lat, lon), null, null, null, url, anchor));

        return this;
    }

    /**
     Each style declaration may contain the following arguments, separated by pipe characters ("|"):

     feature (optional) indicates the features to select for this style modification. Features include things on the map, like roads, parks, or other points of interest. If no feature argument is present, the specified style applies to all features.
     element (optional) indicates the element(s) of the specified feature to select for this style modification. Elements are characteristics of a feature, such as geometry or labels. If no element argument is present, the style applies to all elements of the specified feature.
     A set of style rules (mandatory) to apply to the specified feature(s) and element(s). The API applies the rules in the order in which they appear in the style declaration. You can include any number of rules, within the normal URL-length constraints of the Google Static Maps API.

     The only options currently supported from this builder are color and visibility, but the full list of style options are included bellow.

     The following style options are supported:
     hue (an RGB hex string of format 0xRRGGBB) indicates the basic color.
     Note: This option sets the hue while keeping the saturation and lightness specified in the default Google style (or in other style options you define on the map). The resulting color is relative to the style of the base map. If Google makes any changes to the base map style, the changes affect your map's features styled with hue. It's better to use the absolute color styler if you can.

     lightness (a floating point value between -100 and 100) indicates the percentage change in brightness of the element. Negative values increase darkness (where -100 specifies black) while positive values increase brightness (where +100 specifies white).
     Note: This option sets the lightness while keeping the saturation and hue specified in the default Google style (or in other style options you define on the map). The resulting color is relative to the style of the base map. If Google makes any changes to the base map style, the changes affect your map's features styled with lightness. It's better to use the absolute color styler if you can.

     saturation (a floating point value between -100 and 100) indicates the percentage change in intensity of the basic color to apply to the element.
     Note: This option sets the saturation while keeping the hue and lightness specified in the default Google style (or in other style options you define on the map). The resulting color is relative to the style of the base map. If Google makes any changes to the base map style, the changes affect your map's features styled with saturation. It's better to use the absolute color styler if you can.

     gamma (a floating point value between 0.01 and 10.0, where 1.0 applies no correction) indicates the amount of gamma correction to apply to the element. Gamma corrections modify the lightness of colors in a non-linear fashion, while not affecting white or black values. Gamma correction is typically used to modify the contrast of multiple elements. For example, you can modify the gamma to increase or decrease the contrast between the edges and interiors of elements.
     Note: This option adjusts the lightness relative to the default Google style, using a gamma curve. If Google makes any changes to the base map style, the changes affect your map's features styled with gamma. It's better to use the absolute color styler if you can.

     invert_lightness (if true) inverts the existing lightness. This is useful, for example, for quickly switching to a darker map with white text.
     Note: This option simply inverts the default Google style. If Google makes any changes to the base map style, the changes affect your map's features styled with invert_lightness. It's better to use the absolute color styler if you can.

     visibility (on, off, or simplified) indicates whether and how the element appears on the map. A simplified visibility removes some style features from the affected features; roads, for example, are simplified into thinner lines without outlines, while parks lose their label text but retain the label icon.

     color (an RGB hex string of format 0xRRGGBB) sets the color of the feature.

     weight (an integer value, greater than or equal to zero) sets the weight of the feature, in pixels. Setting the weight to a high value may result in clipping near tile borders.

     -----------------------------------------------------------------------------------------------
     Style rules are applied in the order that you specify. Do not combine multiple operations into a single style operation. Instead, define each operation as a separate entry in the style array.
     Note: Order is important, as some operations are not commutative. Features and/or elements that are modified through style operations (usually) already have existing styles. The operations act on those existing styles, if present.
     */
    public StaticMapBuilder addStyle(MapStyleFeature feature, MapStyleElement element, MapStyleVisibility visibility, String color) {
        if (feature == null) {
            throw new IllegalArgumentException("All styles must declare a feature");
        }

        if (styles == null) {
            styles = new ArrayList<>();
        }

        styles.add(new MapStyle(feature, element, visibility, color));

        return this;
    }

    /**
     * Here for developers to add unsupported params. Use at your own risk.
     *
     * The & will be inserted between each of these, so leave it out of your param string.
     */
    public StaticMapBuilder addManualParam(String manualParam) {
        if (manualParams == null) {
            manualParams = new ArrayList<>();
        }

        manualParams.add(manualParam);

        return this;
    }

    /**
     * Nulls out all properties of this class so a new URL can be created without retaining the options of the last one built.
     *
     * Mostly here because we need the API key across instances, so this class makes more sense as an injectable singleton.
     */
    public void clear() {
        size = null;
        scale = null;
        imageFormat = null;

        mapType = null;

        zoom = null;
        center = null;
        markers = null;
        styles = null;

        manualParams = null;
    }

    /**
     * Builds a (hopefully) valid maps URL from the information passed to the builder.
     *
     * Several checks for validity are present, but that doesn't guarantee that this URL will return an image
     *
     * A placeholder image should be present in the case that there is no image found at this URL
     */
    public String build() {
        if (Strings.isNullOrEmpty(googleApiKey)) {
            throw new IllegalStateException("Cannot build a Maps URL without an API key");
        }

        if (size == null) {
            throw new IllegalStateException("Cannot build a Maps URL without a size");
        }

        if (center == null && (markers == null || markers.size() == 0)) {
            throw new IllegalStateException("Map cannot be created without a center point or two or more markers");
        }

        StringBuilder sb = new StringBuilder(MAP_URL);

        sb.append(KEY_PARAM).append(googleApiKey);

        sb.append(URL_AMP).append(SIZE_PARAM).append(size.toString());

        if (scale != null) {
            sb.append(URL_AMP).append(scale.value());
        }

        if (mapType != null) {
            sb.append(URL_AMP).append(mapType.value());
        }

        if (zoom != null) {
            sb.append(URL_AMP).append(ZOOM_PARAM).append(zoom);
        }

        if (imageFormat != null) {
            sb.append(URL_AMP).append(imageFormat.value());
        }

        if (center != null) {
            sb.append(URL_AMP).append(CENTER_PARAM).append(center.toString());
        }

        if (markers != null) {
            for (Marker marker : markers) {
                sb.append(URL_AMP).append(marker.toString());
            }
        }

        if (styles != null) {
            for (MapStyle mapStyle : styles) {
                sb.append(URL_AMP).append(mapStyle.toString());
            }
        }

        if (manualParams != null) {
            for (String param : manualParams) {
                sb.append(URL_AMP).append(param);
            }
        }

        return sb.toString();
    }

    /**
     The Google Static Maps API creates maps in several formats, listed below:

     roadmap (default) specifies a standard roadmap image, as is normally shown on the Google Maps website. If no maptype value is specified, the Google Static Maps API serves roadmap tiles by default.
     satellite specifies a satellite image.
     terrain specifies a physical relief map image, showing terrain and vegetation.
     hybrid specifies a hybrid of the satellite and roadmap image, showing a transparent layer of major streets and place names on the satellite image.
     */
    public enum MapType {
        ROADMAP("roadmap"),
        SATELLITE("satellite"),
        TERRAIN("terrain"),
        HYBRID("hybrid");

        private String value;

        MapType(String value) {
            this.value = value;
        }

        public String value() {
            return TYPE_PARAM + value;
        }
    }

    /**
     * Sizes of markers available. Default is mid.
     */
    public enum MarkerSize {
        TINY("tiny"),
        MID("mid"),
        SMALL("small");

        private String value;

        MarkerSize(String value) {
            this.value = value;
        }

        public String value() {
            return "size:" + value;
        }
    }

    /**
     * Predefined colors to use for markers or styles
     */
    public enum MarkerColors {
        BLACK("black"),
        BROWN("brown"),
        GREEN("green"),
        PURPLE("purple"),
        YELLOW("yellow"),
        BLUE("blue"),
        GRAY("gray"),
        ORANGE("orange"),
        RED("red") ,
        WHITE("white");

        private String value;

        MarkerColors(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    /**
     * Custom markers can specify an anchor gravity in relation to the position of the pin on the map
     */
    public enum CustomMarkerAnchor {
        TOP("top"),
        BOTTOM("bottom"),
        LEFT("left"),
        RIGHT("right"),
        CENTER("center"),
        TOP_LEFT("topleft"),
        TOP_RIGHT("topright"),
        BOTTOM_LEFT("bottomleft"),
        BOTTOM_RIGHT("bottomright");

        private String value;

        CustomMarkerAnchor(String value) {
            this.value = value;
        }

        public String value() {
            return "anchor:" + value;
        }
    }

    /**
     all (default) selects all features.

     administrative selects all administrative areas. Styling affects only the labels of administrative areas, not the geographical borders or fill.
     administrative.country selects countries.
     administrative.land_parcel selects land parcels.
     administrative.locality selects localities.
     administrative.neighborhood selects neighborhoods.
     administrative.province selects provinces.

     landscape selects all landscapes.
     landscape.man_made selects structures built by humans.
     landscape.natural selects natural features.
     landscape.natural.landcover selects landcover features.
     landscape.natural.terrain selects terrain features.

     poi selects all points of interest.
     poi.attraction selects tourist attractions.
     poi.business selects businesses.
     poi.government selects government buildings.
     poi.medical selects emergency services, including hospitals, pharmacies, police, doctors, and others.
     poi.park selects parks.
     poi.place_of_worship selects places of worship, including churches, temples, mosques, and others.
     poi.school selects schools.
     poi.sports_complex selects sports complexes.

     road selects all roads.
     road.arterial selects arterial roads.
     road.highway selects highways.
     road.highway.controlled_access selects highways with controlled access.
     road.local selects local roads.

     transit selects all transit stations and lines.
     transit.line selects transit lines.
     transit.station selects all transit stations.
     transit.station.airport selects airports.
     transit.station.bus selects bus stops.
     transit.station.rail selects rail stations.

     water selects bodies of water.
     */
    public enum MapStyleFeature {
        ALL("all"),
        ADMINISTRATIVE("administrative"),
        ADMINISTRATIVE_COUNTRY("administrative.country"),
        ADMINISTRATIVE_LAND_PARCEL("administrative.land_parcel"),
        ADMINISTRATIVE_LOCALITY("administrative.locality"),
        ADMINISTRATIVE_NEIGHBORHOOD("administrative.neighborhood"),
        ADMINISTRATIVE_PROVINCE("administrative.province"),
        LANDSCAPE("landscape"),
        LANDSCAPE_MAN_MADE("landscape.man_made"),
        LANDSCAPE_NATURAL("landscape.natural"),
        LANDSCAPE_NATURAL_LANDCOVER("landscape.natural.landcover"),
        LANDSCAPE_NATURAL_TERRAIN("landscape.natural.terrain"),
        POI("poi"),
        POI_ATTRACTION("poi.attraction"),
        POI_BUSINESS("poi.business"),
        POI_GOVERNMENT("poi.government"),
        POI_MEDICAL("poi.medical"),
        POI_PARK("poi.park"),
        POI_PLACE_OF_WORSHIP("poi.place_of_worship"),
        POI_SCHOOL("poi.school"),
        POI_SPORTS_COMPLEX("poi.sports_complex"),
        ROAD("road"),
        ROAD_ARTERIAL("road.arterial"),
        ROAD_HIGHWAY("road.highway"),
        ROAD_HIGHWAY_CONTROLLED_ACCESS("road.highway.controlled_access"),
        ROAD_LOCAL("road.local"),
        TRANSIT("transit"),
        TRANSIT_LINE("transit.line"),
        TRANSIT_STATION("transit.station"),
        TRANSIT_STATION_AIRPORT("transit.station.airport"),
        TRANSIT_STATION_BUS("transit.station.bus"),
        TRANSIT_STATION_RAIL("transit.station.rail"),
        WATER("water");

        private String value;

        MapStyleFeature(String value) {
            this.value = value;
        }

        public String value() {
            return "feature:" + value;
        }
    }

    /**
     all (default) selects all elements of the specified feature.

     geometry selects all geometric elements of the specified feature.
     geometry.fill selects only the fill of the feature's geometry.
     geometry.stroke selects only the stroke of the feature's geometry.

     labels selects the textual labels associated with the specified feature.
     labels.icon selects only the icon displayed within the feature's label.
     labels.text selects only the text of the label.
     labels.text.fill selects only the fill of the label. The fill of a label is typically rendered as a colored outline that surrounds the label text.
     labels.text.stroke selects only the stroke of the label's text.
     */
    public enum MapStyleElement {
        ALL("all"),
        GEOMETRY("geometry"),
        GEOMETRY_FILL("geometry.fill"),
        GEOMETRY_STROKE("geometry.stroke"),
        LABELS("labels"),
        LABELS_ICON("labels.icon"),
        LABELS_TEXT("labels.text"),
        LABELS_TEXT_FILL("labels.text.fill"),
        LABELS_TEXT_STROKE("labels.text.stroke");

        private String value;

        MapStyleElement(String value) {
            this.value = value;
        }

        public String value() {
            return "element:" + value;
        }
    }

    /**
     visibility (on, off, or simplified) indicates whether and how the element appears on the map.

     A simplified visibility removes some style features from the affected features; roads, for example, are simplified into thinner lines without outlines, while parks lose their label text but retain the label icon.
     */
    public enum MapStyleVisibility {
        ON("on"),
        OFF("off"),
        SIMPLIFIED("simplified");

        private String value;

        MapStyleVisibility(String value) {
            this.value = value;
        }

        public String value() {
            return "visibility:" + value;
        }
    }

    /**
     1, 2 or 4

     4 is only valid for a paid maps account
     */
    public enum MapScale {
        DEFAULT("1"),
        DOUBLE("2"),
        QUAD("4");

        private String value;

        MapScale(String scale) {
            this.value = scale;
        }

        public String value() {
            return SCALE_PARAM + value;
        }
    }

    /**
     png8 or png (default) specifies the 8-bit PNG format.
     png32 specifies the 32-bit PNG format.
     gif specifies the GIF format.
     jpg specifies the JPEG compression format.
     jpg-baseline specifies a non-progressive JPEG compression format.
     */
    public enum ImageFormat {
        PNG("png"),
        PNG32("png32"),
        GIF("gif"),
        JPG("jpg"),
        JPG_BASELINE("jpg-baseline");

        private String value;

        ImageFormat(String scale) {
            this.value = scale;
        }

        public String value() {
            return FORMAT_PARAM + value;
        }
    }

    private class Point {
        double latitude;
        double longitude;

        Point(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return latitude + "," + longitude;
        }
    }

    private class Marker {
        StaticMapBuilder.Point position;
        StaticMapBuilder.MarkerSize size;

        String label;
        String color;

        String iconUrl;
        StaticMapBuilder.CustomMarkerAnchor anchor;

        Marker(Point position, MarkerSize size, String label, String color, String iconUrl, CustomMarkerAnchor anchor) {
            this.position = position;
            this.size = size;
            this.label = label;
            this.color = color;
            this.iconUrl = iconUrl;
            this.anchor = anchor;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(MARKER_PARAM);

            sb.append(position.latitude).append(",").append(position.longitude);

            if (!Strings.isNullOrEmpty(iconUrl)) {
                sb.append(URL_PIPE).append("icon:").append(iconUrl);

                if (anchor != null) {
                    sb.append(URL_PIPE).append(anchor.value());
                }

                return sb.toString();
            }

            if (!Strings.isNullOrEmpty(label)) {
                sb.append(URL_PIPE).append("label:").append(label);
            }

            if (!Strings.isNullOrEmpty(color)) {
                sb.append(URL_PIPE).append("color:").append(color);
            }

            if (size != null) {
                sb.append(URL_PIPE).append(size.value());
            }

            return sb.toString();
        }
    }

    private class MapStyle {
        MapStyleFeature feature;
        MapStyleElement element;
        MapStyleVisibility visibility;
        String color;

        public MapStyle(MapStyleFeature feature, MapStyleElement element, MapStyleVisibility visibility, String color) {
            this.feature = feature;
            this.element = element;
            this.visibility = visibility;
            this.color = color;
        }

        @Override
        public String toString() {
            // we have to have a feature, elements are just a subset of a feature
            if (feature == null) {
                return Strings.EMPTY;
            }

            StringBuilder sb = new StringBuilder(STYLE_PARAM);

            sb.append(feature.value());

            if (element != null) {
                sb.append(URL_PIPE).append(element.value());
            }

            if (visibility != null) {
                sb.append(URL_PIPE).append(visibility.value());
            }

            if (!Strings.isNullOrEmpty(color)) {
                sb.append(URL_PIPE).append("color:").append(color);
            }

            return sb.toString();
        }
    }

    private class Size {
        private int width;
        private int height;

        Size(int height, int width) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return height + "x" + width;
        }
    }
}
