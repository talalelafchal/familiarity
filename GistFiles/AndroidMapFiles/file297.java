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

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * A simple {@link RecyclerView.Adapter} implementation for displaying {@link TimelineItem} rows.
 */
public class TimelineAdapter
        extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    /**
     * Implement this to provide a layout resource that can be displayed on the timeline.
     * <p/>
     * This layout should contain fields with the following predefined resource identifiers:
     * <ul>
     * <li>{@link R.id#tvTLTime}</li>
     * <li>{@link R.id#tvTLPrimary}</li>
     * <li>{@link R.id#tvTLSecondary}</li>
     * <li>{@link R.id#ivTLMarker}</li>
     * <li>{@link R.id#ivTLTimeline}</li>
     * </ul>
     *
     * @param viewType The view type of the new View.
     * @return a layout resource that holds a View of the given view type.
     */
    @LayoutRes
    protected int getViewHolderResId(int viewType) {
        return R.layout.item_list_timeline_default;
    }


    @NonNull
    private final List<TimelineItem> items;

    public TimelineAdapter(@NonNull List<TimelineItem> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(getViewHolderResId(viewType), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = items.get(position);
        if (holder.tvTime != null) {
            holder.tvTime.setText(items.get(position).getTime());
        }
        if (holder.tvPrimary != null) {
            holder.tvPrimary.setText(items.get(position).getPrimaryText());
        }
        if (holder.tvSecondary != null) {
            holder.tvSecondary.setText(items.get(position).getSecondaryText());
        }
        if (holder.ivMarker != null) {
            holder.ivMarker.setImageResource(items.get(position).getMarkerResId());
        }
        if (holder.ivTimeline != null) {
            holder.ivTimeline.setImageResource(items.get(position).getTimelineResId());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View vRoot;

        public final TextView tvTime;
        public final TextView tvPrimary;
        public final TextView tvSecondary;
        public final ImageView ivMarker;
        public final ImageView ivTimeline;

        public TimelineItem item;

        public ViewHolder(View view) {
            super(view);
            vRoot = view;
            tvTime = (TextView) view.findViewById(R.id.tvTLTime);
            tvPrimary = (TextView) view.findViewById(R.id.tvTLPrimary);
            tvSecondary = (TextView) view.findViewById(R.id.tvTLSecondary);
            ivMarker = (ImageView) view.findViewById(R.id.ivTLMarker);
            ivTimeline = (ImageView) view.findViewById(R.id.ivTLTimeline);
        }

        @Override
        public String toString() {
            if ((item == null) || (item.getSecondaryText() == null)) {
                return null;
            }
            return item.getSecondaryText().toString();
        }
    }
}
