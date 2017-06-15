public class MyBean implements Parcelable {

    public static final String BOLD = "bold";

    public static final String ITALIC = "italic";

    public static final String REGULAR = "regular";

    @Expose
    @SerializedName(BOLD)
    public String bold;

    @Expose
    @SerializedName(ITALIC)
    public String italic;

    @Expose
    @SerializedName(REGULAR)
    public String regular;

    public MyBean(String bold, String italic, String regular) {
        this.bold = bold;
        this.italic = italic;
        this.regular = regular;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bold);
        dest.writeString(this.italic);
        dest.writeString(this.regular);
    }

    private MyBean(Parcel in) {
        this.bold = in.readString();
        this.italic = in.readString();
        this.regular = in.readString();
    }

    public static final Parcelable.Creator<MyBean> CREATOR = new Parcelable.Creator<MyBean>() {
        public MyBean createFromParcel(Parcel source) {
            return new MyBean(source);
        }

        public MyBean[] newArray(int size) {
            return new MyBean[size];
        }
    };
}