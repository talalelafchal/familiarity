package com.gluonhq.charm.down.plugins;

import javafx.beans.property.ReadOnlyFloatProperty;

public interface KeyboardService {
    
    /**
     * Gets the visible height of the Keyboard, so scene or views can be moved up 
     * if some of their components might be covered by the keyboard.
     * 
     * @return A ReadOnlyFloatProperty with the height of the soft keyboard
     */
    public ReadOnlyFloatProperty visibleHeightProperty();
    
}
