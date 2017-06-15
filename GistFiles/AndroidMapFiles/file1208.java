
import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
 * Created by ekuivi on 8/24/16.
 *
 * Singleton object that makes it easy to pass object references between activities
 */
public class ReferenceMap {

    public class ReferenceList {

        private HashMap<String, Object> mReferenceList;

        private ReferenceList(){
            mReferenceList = new HashMap<String, Object>();
        }

        public void put(String key, Object object){
            mReferenceList.put(key, object);
        }

        public Object get(String key){
            if(!mReferenceList.containsKey(key))
                return null;

            return mReferenceList.get(key);
        }
    }

    private HashMap<String, ReferenceList> mRefListMap;

    private static ReferenceMap mSingleInstance = null;

    ReferenceMap(){
        mRefListMap = new HashMap<String, ReferenceList>();
    }

    public static ReferenceMap getInstance(){
        if(mSingleInstance == null)
            mSingleInstance = new ReferenceMap();

        return mSingleInstance;
    }

    private String getActivityNameFrom(Context context){
        return context.getClass().getCanonicalName();
    }

    private String getFragmentNameFrom(Fragment fragment){
        return  fragment.getClass().getCanonicalName();
    }

    private String getActivityNameFrom(Class activity){
        return activity.getCanonicalName();
    }

    public ReferenceList getReferenceList(Context context){
        String activityName = getActivityNameFrom(context);

        if(!mRefListMap.containsKey(activityName))
            return null;

        return mRefListMap.get(activityName);
    }

    public ReferenceList getReferenceList(Fragment fragment){
        String fragmentName = getFragmentNameFrom(fragment);

        if(!mRefListMap.containsKey(fragmentName))
            return null;

        return mRefListMap.get(fragmentName);
    }

    private void createActivityRefList(String activityName){
        mRefListMap.put(activityName, new ReferenceList());
    }

    public void putObjectReference(Class activity, String objectKey, Object object){

        String activtyName = getActivityNameFrom(activity);
        if(!mRefListMap.containsKey(activtyName))
            createActivityRefList(activtyName);

        ReferenceList referenceList = mRefListMap.get(activtyName);
        referenceList.put(objectKey, object);
    }

    public void releaseRefList(Context context){
        String activityName = getActivityNameFrom(context);

        if(mRefListMap.containsKey(activityName))
            mRefListMap.remove(activityName);
    }

    public void releaseRefList(Fragment fragment){
        String fragmentName = getFragmentNameFrom(fragment);

        if(mRefListMap.containsKey(fragmentName))
            mRefListMap.remove(fragmentName);
    }
}