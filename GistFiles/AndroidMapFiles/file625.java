package my;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.example.client.R;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Description:
 * <p/>
 * Date: 14-1-31
 * Author: Administrator
 */
public class TableLayout extends ViewGroup implements Position {
    private String layout;
    private String p;
    private int gap;

    public TableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TableLayout);
        layout = typedArray.getString(R.styleable.TableLayout_layout);
        p = typedArray.getString(R.styleable.TableLayout_p);
        gap = typedArray.getInt(R.styleable.TableLayout_gap, 0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        int width = getWidth();
        int height = getHeight();
        String[] layouts = layout.split(";");
        List<Integer> widths = getScaleValues(width, layouts[0]);
        List<Integer> heights = getScaleValues(height, layouts[1]);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof Position) {
                String p = ((Position) child).getP();
                if (!StringUtils.isEmpty(p)) {
                    String[] values = p.split(":");
                    String startPos = values[0];
                    int colStart = startPos.charAt(0) - 'A' + 1;
                    int rowStart = Integer.parseInt(startPos.substring(1));
                    int colEnd, rowEnd;
                    if (values.length > 1) {
                        String endPos = values[1];
                        colEnd = endPos.charAt(0) - 'A' + 1;
                        rowEnd = Integer.parseInt(endPos.substring(1));
                    } else {
                        colEnd = colStart;
                        rowEnd = rowStart;
                    }
                    int x1 = getOriginal(widths, colStart);
                    int y1 = getOriginal(heights, rowStart);
                    int x2 = getOriginal(widths, colEnd + 1) - gap;
                    int y2 = getOriginal(heights, rowEnd + 1) - gap;
                    child.layout(x1, y1, x2, y2);
                }
            }
            if (child instanceof TableLayout) {
                ((TableLayout) child).onLayout(changed, left, top, right, bottom);
            }
        }
    }

    private int getOriginal(List<Integer> sizes, int index) {
        int res = 0;
        for (int i = 1; i < index; i++) {
            res += sizes.get(i - 1);
        }
        res += index * gap;
        return res;
    }

    private List<Integer> getScaleValues(int length, String layout) {
        int scaleLen = getScaleLen(layout);
        length = length - scaleLen * gap;
        List<Integer> sizes = new ArrayList<Integer>();
        StringTokenizer st = new StringTokenizer(layout, ",");
        while (st.hasMoreElements()) {
            String scale = st.nextToken();
            float value;
            int repeat = 1;
            if (scale.contains("*")) {
                String[] values = scale.split("\\*");
                value = Float.parseFloat(values[0]);
                repeat = Integer.parseInt(values[1]);
            } else {
                value = Integer.parseInt(scale);
            }
            for (int i = 0; i < repeat; i++) {
                if (value <= 1) {
                    sizes.add(Math.round(length * value));
                } else {
                    sizes.add(Math.round(value));
                }
            }
        }
        return sizes;
    }

    private int getScaleLen(String layout) {
        int len = 0;
        StringTokenizer st = new StringTokenizer(layout, ",");
        while (st.hasMoreElements()) {
            String scale = st.nextToken();
            if (scale.contains("*")) {
                String[] values = scale.split("\\*");
                len = +Integer.parseInt(values[1]);
            } else {
                len++;
            }
        }
        return len;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    @Override
    public String getP() {
        return p;
    }
}
