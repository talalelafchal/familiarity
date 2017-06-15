import java.util.List;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class ItemLoader extends AsyncTaskLoader<List<Item>> {

	private Context mContext;
	private int mId;

	public OperationLoader(Context context,int id) {
		super(context);
		mId =id;
	}

	@Override
	public List<Item> loadInBackground() {		
		return  OperationHandler.getInstance(mContext).getItemsFromID(mId);
	}

}