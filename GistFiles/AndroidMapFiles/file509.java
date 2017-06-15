// this file is in library
package moe.xing.databindingnogetterinlibrary.databinding;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
public abstract class ActivityMainBinding extends ViewDataBinding {
    protected ActivityMainBinding(android.databinding.DataBindingComponent bindingComponent, android.view.View root_, int localFieldCount
    ) {
        super(bindingComponent, root_, localFieldCount);
    }
    public abstract void setText(java.lang.String text);
    public static ActivityMainBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ActivityMainBinding inflate(android.view.LayoutInflater inflater) {
        return inflate(inflater, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ActivityMainBinding bind(android.view.View view) {
        return null;
    }
    public static ActivityMainBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot, android.databinding.DataBindingComponent bindingComponent) {
        return null;
    }
    public static ActivityMainBinding inflate(android.view.LayoutInflater inflater, android.databinding.DataBindingComponent bindingComponent) {
        return null;
    }
    public static ActivityMainBinding bind(android.view.View view, android.databinding.DataBindingComponent bindingComponent) {
        return null;
    }
}
