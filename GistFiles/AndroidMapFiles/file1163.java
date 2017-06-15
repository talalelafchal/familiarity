package my;

/**
 * Description:
 * <p/>
 * Date: 14-2-3
 * Author: Administrator
 */
public interface ModelBinder {
    String getField();

    void setValue(Object value);

    Object getValue();

}
