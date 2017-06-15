package tools.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * <p/>
 * Date: 14-2-2
 * Author: Administrator
 */
public class MethodMeta {
    private String rtnType;
    private String name;
    private List<ParamMeta> params = new ArrayList<ParamMeta>();

    public String getRtnType() {
        return rtnType;
    }

    public void setRtnType(String rtnType) {
        this.rtnType = rtnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParamMeta> getParams() {
        return params;
    }

    public void setParams(List<ParamMeta> params) {
        this.params = params;
    }
}
