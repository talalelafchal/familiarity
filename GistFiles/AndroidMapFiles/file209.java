package annotations;

import android.content.res.Configuration;

public enum ScreenSize {

    SMALL("small", Configuration.SCREENLAYOUT_SIZE_SMALL),

    NORMAL("normal", Configuration.SCREENLAYOUT_SIZE_NORMAL),

    LARGE("large", Configuration.SCREENLAYOUT_SIZE_LARGE),

    XLARGE("xlarge", Configuration.SCREENLAYOUT_SIZE_XLARGE);

    private String path;
    private int configurationSize;

    private ScreenSize(String path, int configurationSize) {
        this.path = path;
        this.configurationSize = configurationSize;
    }

    public String getPath() {
        return path;
    }

    public int getConfigurationSize() {
        return configurationSize;
    }
}