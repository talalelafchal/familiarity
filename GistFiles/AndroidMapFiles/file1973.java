import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListViewWithSectionsAdapter extends ArrayAdapter<Object> {

    private final Param param;

    public ListViewWithSectionsAdapter(Context context, DataProvider dataProvider, ViewProvider sectionViewProvider, ViewProvider entryViewProvider){
        this(createParam(context, dataProvider, sectionViewProvider, entryViewProvider));
    }

    private ListViewWithSectionsAdapter(Param param) {
        super(param.getContext(), 0, param.getAllObjects());
        this.param = param;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewProvider viewProvider = param.getViewProviderByPosition(position);
        Object item = param.getObjectByPosition(position);
        return viewProvider.getView(position, convertView, parent, item, getContext());
    }

    @Override
    public int getItemViewType(int position) {
        return param.getViewTypeForPosition(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public interface Param {
        Context getContext();
        ViewProvider getViewProviderByPosition(int position);
        Object getObjectByPosition(int position);
        List<Object> getAllObjects();

        int getViewTypeForPosition(int position);
    }

    public interface ViewProvider<T> {
        public View getView(int position, View convertView, ViewGroup parent, T item, Context context);
    }

    public interface DataProvider {
        List<Object> getSections();
        List<Object> getEntriesForSection(Object section);
    }

    private static Param createParam(Context context, DataProvider dataProvider, ViewProvider sectionViewProvider, ViewProvider entryViewProvider){
        ArrayList<Object> allObj = new ArrayList<Object>();
        HashMap<Integer, Object> positionToObjectMap = new HashMap<Integer, Object>();
        HashMap<Integer, ViewProvider>positionToViewProviderMap = new HashMap<Integer, ViewProvider>();
        int position = 0;
        for(Object section : dataProvider.getSections()){
            positionToViewProviderMap.put(position, sectionViewProvider);
            positionToObjectMap.put(position, section);
            allObj.add(section);

            position++;
            for(Object entry : dataProvider.getEntriesForSection(section)){
                positionToViewProviderMap.put(position, entryViewProvider);
                positionToObjectMap.put(position, entry);
                allObj.add(entry);
                position++;
            }
        }
        return new ParamImpl(context, allObj, positionToObjectMap, positionToViewProviderMap, sectionViewProvider, entryViewProvider);
    }

    private static class ParamImpl implements Param {

        private final Context context;
        private final ArrayList<Object> allObjects;
        private final HashMap<Integer, Object> positionToObjectMap;
        private final HashMap<Integer, ViewProvider> positionToViewProviderMap;
        private final ViewProvider sectionsViewProvider;

        public ParamImpl(Context context, ArrayList<Object> allObjects, HashMap<Integer, Object> positionToObjectMap, HashMap<Integer, ViewProvider> positionToViewProviderMap, ViewProvider sectionViewProvider, ViewProvider entryViewProvider) {
            this.context = context;
            this.allObjects = allObjects;
            this.positionToObjectMap = positionToObjectMap;
            this.positionToViewProviderMap = positionToViewProviderMap;
            this.sectionsViewProvider = sectionViewProvider;
        }

        @Override
        public Context getContext() {
            return this.context;
        }

        @Override
        public ViewProvider getViewProviderByPosition(int position) {
            return positionToViewProviderMap.get(position);
        }

        @Override
        public Object getObjectByPosition(int position) {
            return positionToObjectMap.get(position);
        }

        @Override
        public List<Object> getAllObjects() {
            return allObjects;
        }

        @Override
        public int getViewTypeForPosition(int position) {
            return getViewProviderByPosition(position) == sectionsViewProvider ? 0 : 1;
        }
    }

}
