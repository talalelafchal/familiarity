package com.gluonhq.charm.down.plugins.ios;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.KeyboardService;
import com.gluonhq.charm.down.plugins.LifecycleEvent;
import com.gluonhq.charm.down.plugins.LifecycleService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;

public class IOSKeyboardService implements KeyboardService {

    static {
        System.loadLibrary("Keyboard");
        initKeyboard();
    }
    
    private static ReadOnlyFloatWrapper height = new ReadOnlyFloatWrapper();

    public IOSKeyboardService() {
        Services.get(LifecycleService.class).ifPresent(l -> {
            l.addListener(LifecycleEvent.PAUSE, IOSKeyboardService::stopObserver);
            l.addListener(LifecycleEvent.RESUME, IOSKeyboardService::startObserver);
        });
        startObserver();
    }
    
    @Override
    public ReadOnlyFloatProperty visibleHeightProperty() {
        return height.getReadOnlyProperty();
    }
    
    // native
    private static native void initKeyboard();
    private static native void startObserver();
    private static native void stopObserver();
    
    private void notifyKeyboard(float height) {
        Platform.runLater(() -> this.height.setValue(height));
    }
    
}
