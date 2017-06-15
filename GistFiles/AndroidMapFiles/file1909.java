package mobi.glowworm.demo.timeline.dummy;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.glowworm.demo.timeline.TimelineItem;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<TimelineItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        return "Details about Item: " + position;
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem implements TimelineItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }

        @Override
        public int getState() {
            return TimelineItem.STATE_DEFAULT;
        }

        @Nullable
        @Override
        public CharSequence getTime() {
            return String.valueOf(id);
        }

        @Nullable
        @Override
        public CharSequence getPrimaryText() {
            return content;
        }

        @Nullable
        @Override
        public CharSequence getSecondaryText() {
            return details;
        }

        @Override
        public int getMarkerResId() {
            return android.R.drawable.ic_menu_compass;
        }

        @Override
        public int getTimelineResId() {
            return android.R.drawable.ic_menu_compass;
        }
    }
}
