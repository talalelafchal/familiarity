public class MediaEntity  {
    @SerializedName("media_id")
    public final long mediaId;
    @SerializedName("media_id_string")
    public final String mediaIdString;
    @SerializedName("size")
    public final int size;
    @SerializedName("image")
    public final MediaEntity.Image image;

    public MediaEntity(long mediaId, String mediaIdString, int size, MediaEntity.Image image) {
        this.mediaId = mediaId;
        this.mediaIdString = mediaIdString;
        this.size = size;
        this.image = image;
    }

    public static class Image {
        @SerializedName("w")
        public final int w;
        @SerializedName("h")
        public final int h;
        @SerializedName("image_type")
        public final String imageType;

        public Image(int w, int h, String imageType) {
            this.w = w;
            this.h = h;
            this.imageType = imageType;
        }
    }
