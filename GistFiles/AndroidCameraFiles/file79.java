package com.example.zhangxingyu.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxingyu on 14-11-27.
 */
public class SegmentedTabGroup extends LinearLayout implements View.OnClickListener {
    public SegmentedTabGroup(Context context) {
        super(context);
    }

    private Context mContext;
    private int mTextSize = 12;

    public SegmentedTabGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = getContext();
        setOrientation(LinearLayout.HORIZONTAL);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SegmentedTabGroup);
        dividerColor = a.getColor(R.styleable.SegmentedTabGroup_stgDividerColor, dividerColor);
        dividerWidth = a.getDimensionPixelSize(R.styleable.SegmentedTabGroup_stgDividerWidth, dividerWidth);
        selectTabTextColor = a.getColor(R.styleable.SegmentedTabGroup_stgSelectedTabTextColor, selectTabTextColor);
        radius = a.getDimensionPixelSize(R.styleable.SegmentedTabGroup_stgRadius, (int) radius);
        mTextSize=a.getDimensionPixelSize(R.styleable.SegmentedTabGroup_stgTextSize,mTextSize);
        a.recycle();

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radius);
        drawable.setStroke(dividerWidth, dividerColor);
        drawable.setColor(selectTabTextColor);
        setBackgroundDrawable(drawable);

        mBgFirstTabSelect = new GradientDrawable();
        float[] r = {radius, radius, 0f, 0f, 0f, 0f, radius, radius};
        mBgFirstTabSelect.setCornerRadii(r);
        mBgFirstTabSelect.setStroke(dividerWidth, dividerColor);
        mBgFirstTabSelect.setColor(dividerColor);

        float[] d = {0f, 0f, radius, radius, radius, radius, 0f, 0f};
        mBgLastTabSelect = new GradientDrawable();
        mBgLastTabSelect.setCornerRadii(d);
        mBgLastTabSelect.setStroke(dividerWidth, dividerColor);
        mBgLastTabSelect.setColor(dividerColor);
    }

    private GradientDrawable mBgFirstTabSelect, mBgLastTabSelect;
    private float radius = 20f;
    private int dividerWidth = 2;
    private int dividerColor = Color.parseColor("#0000ff");
    private int selectTabTextColor = Color.parseColor("#ffffff");

    public void addTabs(final String... tabs) {
        mTabs = null;
        mTabs = new ArrayList<TextView>();
        mTabs.clear();
        removeAllViews();
        for (int index = 0; index < tabs.length; index++) {
            addTab(tabs[index], index);
        }
    }

    private List<TextView> mTabs;

    private void addTab(final String text, int index) {
        if (index != 0) {
            addView(genDivider());
        }
        TextView tv = new TextView(mContext);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
        tv.setLayoutParams(params);
        tv.setTag(index);
        tv.setTextSize(mTextSize);
        if (index == 0) {
            tv.setTextColor(selectTabTextColor);
            tv.setBackgroundDrawable(mBgFirstTabSelect);
            mCurrentTab = tv;
        } else {
            tv.setTextColor(dividerColor);
            tv.setBackgroundDrawable(null);
        }
        tv.setOnClickListener(this);
        mTabs.add(tv);
        addView(tv);
    }

    private TextView mCurrentTab;

    public View genDivider() {
        View divider = new View(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dividerWidth, LayoutParams.MATCH_PARENT);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(dividerColor);
        return divider;
    }

    @Override
    public void onClick(View v) {
        TextView tab = (TextView) v;
        int position = (Integer) v.getTag();
        if (tab.equals(mCurrentTab))
            return;
        tab.setTextColor(selectTabTextColor);
        if (position == 0) {
            tab.setBackgroundDrawable(mBgFirstTabSelect);
        } else if (position == (mTabs.size()-1)) {
            tab.setBackgroundDrawable(mBgLastTabSelect);
        } else {
            tab.setBackgroundColor(dividerColor);
        }
        mCurrentTab.setTextColor(dividerColor);
        mCurrentTab.setBackgroundDrawable(null);
        mCurrentTab = tab;
        Log.i("SegmentedTabGroup", ((Integer) v.getTag()) + "");
    }
}
