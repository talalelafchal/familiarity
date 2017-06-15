package com.baidu.demo.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-10-29
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
public class DemoStreetscapeStreetData {
    public String mStreetAddress;
    public String mDayNightMode;
    public boolean mIsNightAvailable;
    public String mSwitchId;
    public String mIndoorId;
    public ArrayList<HashMap<String, String>> mFloorArray = new ArrayList<HashMap<String, String>>();
    public int mDefaultFloor;

    public String mType; // 街景类型("street", "inter", "park")/(街景，室内景，公园景)

    public void parseStreetInfoJson(String json) {
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);

                mStreetAddress = jsonObject.optString("rname");
                mDayNightMode = jsonObject.optString("mode");
                mIsNightAvailable = jsonObject.optBoolean("switch");
                mSwitchId = jsonObject.optString("switchid");
                mType = jsonObject.optString("type");
                mIndoorId = jsonObject.optString("iid");
                mDefaultFloor = jsonObject.optInt("defaultfloor");
                JSONArray jsonFloor = jsonObject.optJSONArray("indoors");
                if (jsonFloor != null && jsonFloor.length() > 0) {
                    String defaultFloorName = null;
                    LinkedList<HashMap<String, String>> list = new LinkedList<HashMap<String, String>>();
                    for (int i = 0; i < jsonFloor.length(); i++) {
                        JSONObject oneFloorObj = jsonFloor.optJSONObject(i);
                        HashMap<String, String> item = new HashMap<String, String>();
                        item.put("name", oneFloorObj.optString("name"));
                        item.put("pid", oneFloorObj.optString("pid"));
                        if (mDefaultFloor == i) {
                            defaultFloorName = item.get("name");
                        }

                        int j = 0;
                        for (; j < list.size(); j++) {
                            int n1 = Integer.valueOf(item.get("name"));
                            int n2 = Integer.valueOf(list.get(j).get("name"));
                            if (n1 <= n2) {
                                list.add(j, item);
                                break;
                            }
                        }
                        if (j == list.size()) {
                            list.add(item);
                        }

                    }
                    mFloorArray.clear();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, String> item = list.get(i);
                        if (defaultFloorName != null && defaultFloorName.equals(item.get("name"))) {
                            mDefaultFloor = i;
                        }

                        int n1 = Integer.valueOf(item.get("name"));
                        if (n1 < 0) {
                            n1 = -n1;
                            item.put("name", String.format("B%d", n1));
                        } else {
                            item.put("name", String.format("F%d", n1));
                        }
                        mFloorArray.add(item);
                    }
                }
            } catch (JSONException e) {
            }
        }
    }

}
