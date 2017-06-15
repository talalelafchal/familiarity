package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import data.Publication;
import ru.ebook.store.R;

/**
 * Created by artyomvursalov on 04.06.13.
 */
public class GridAdapterPublications extends BaseAdapter {
    private Activity activity;
    private Vector<Publication> data;
    private static LayoutInflater inflater=null;
    private Bitmap blankImage;
    private Bitmap blankImage2;

    public GridAdapterPublications(Activity a, Vector<Publication> d) {
        activity = a;
        if(d!=null)
            data=d;
        else
            data=new Vector<Publication>();
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        blankImage= BitmapFactory.decodeResource(a.getBaseContext().getResources(), R.raw.ffffff);
        blankImage2= BitmapFactory.decodeResource(a.getBaseContext().getResources(), R.raw.i115155);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        if(position<data.size())
            return data.get(position);
        else
            return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        vi = inflater.inflate(R.layout.gridview_item_publication, null);


        ImageView imageView=(ImageView)vi.findViewById(R.id.poster);
        Bitmap bitmap;
        /*if(data.get(position).bitmap!=null){
            bitmap=data.get(position).bitmap;
            try{
                ShadowImageView sImageView=(ShadowImageView)vi.findViewById(R.id.poster);
                sImageView.setImageBitmap(bitmap);
            }
            catch(ClassCastException e){
                ShadowImageView sImageView = new ShadowImageView(API.CONTEXT);
                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                sImageView.setLayoutParams(lp);

                sImageView.setScaleType(ImageView.ScaleType.CENTER);
                sImageView.setId(R.id.poster);

                ViewGroup parentImageView=(ViewGroup)imageView.getParent();
                int index = parentImageView.indexOfChild(imageView);
                parentImageView.removeView(imageView);
                parentImageView.addView(sImageView, index);
                sImageView.setImageBitmap(bitmap);
            }
        }
        else{*/
        //    imageView.setImageBitmap(blankImage);
        //}

        if(data.get(position).id%2==0){
            imageView.setImageBitmap(blankImage);
        }
        else{
            imageView.setImageBitmap(blankImage2);
        }


        TextView name=(TextView)vi.findViewById(R.id.name);
        name.setText(data.get(position).name);

        return vi;
    }

    public void setBitmapAtIndex(int i,Bitmap image){
        //data.get(i).bitmap=image;
    }
}