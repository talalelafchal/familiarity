package svk.vk.market.base;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.Collection;

public class VisibleStates implements Parcelable {

    public static final String KEY_STATE = "State views visibility";
    public static final Creator<VisibleStates> CREATOR = new Parcelable.Creator<VisibleStates>() {
        public VisibleStates createFromParcel(Parcel source) {
            return new VisibleStates(source);
        }

        public VisibleStates[] newArray(int size) {
            return new VisibleStates[size];
        }
    };
    public int viewIds[];
    public int viewVisibilities[];

    public VisibleStates(Collection<View> views) {
        viewIds = new int[views.size()];
        viewVisibilities = new int[views.size()];
        int k = 0;
        for (View view : views) {
            if (view == null) throw new IllegalArgumentException();
            viewIds[k] = view.getId();
            viewVisibilities[k] = view.getVisibility();
            k++;
        }
    }

    protected VisibleStates(Parcel in) {
        this.viewIds = in.createIntArray();
        this.viewVisibilities = in.createIntArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.viewIds);
        dest.writeIntArray(this.viewVisibilities);
    }
}