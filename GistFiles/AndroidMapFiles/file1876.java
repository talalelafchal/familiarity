/*
 * Copyright (C) 2016 Glowworm Software
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
 
package mobi.glowworm.demo.timeline;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;


/**
 * Abstracted interface to represent basic information that can be displayed by items on a timeline.
 * <p/>
 * Items to be shown in the {@link TimelineAdapter} implement this interface.
 */
public interface TimelineItem {

    /**
     * Default state that can be returned by {@link #getState()}.
     */
    int STATE_DEFAULT = 0;

    /**
     * Timelines often show items in different colours or icons based on some intrinsic state.
     *
     * @return integer value to determine what state this item is in
     */
    int getState();

    /**
     * Timelines often display a date or time value on the left.
     *
     * @return text to display in the date/time/distance field; or null to hide the field
     */
    @Nullable
    CharSequence getTime();

    /**
     * Timelines often display 2 lines of text. This method returns the main "header" or
     * "title" text to display.
     *
     * @return text to display as the header for the row; or null to hide the field
     */
    @Nullable
    CharSequence getPrimaryText();

    /**
     * Timelines often display 2 lines of text. This method returns the secondary "description"
     * to display.
     *
     * @return text to display as the description for the row; or null to hide the field
     */
    @Nullable
    CharSequence getSecondaryText();

    /**
     * Timelines often display an icon for each row.
     *
     * @return resource ID of a drawable to display as the marker for this row; or zero to
     * hide the field
     */
    @DrawableRes
    int getMarkerResId();

    /**
     * Timelines often display a vertical line linking the items. Sometimes this is a single
     * line, all the same colour, and sometimes it can be broken in places, or take on different
     * colours depending on the value of the {@link #getState()} method.
     *
     * @return resource ID of a drawable to display as the section of the timeline contained
     * in this row; or zero to hide the timeline for this row
     */
    @DrawableRes
    int getTimelineResId();
}
