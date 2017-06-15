
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by TalErez on 07/08/2016.
 */
public class MyCustomeCursorAdapter extends CursorAdapter {


    public MyCustomeCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View myItem= LayoutInflater.from(context).inflate(R.layout.singlelist,parent,false) ;
        return myItem;
}

    @Override
    public void bindView(View view, Context context, Cursor cursor) {



       // Bitmap resized = Bitmap.createScaledBitmap(bitmapOrg, 35, 35, true);

        TextView tx = (TextView) view.findViewById(R.id.movienameF);
        ImageView imv2 = (ImageView) view.findViewById(R.id.imageViewF);
        ImageView txurl = (ImageView) view.findViewById(R.id.imageView5);

        String addItem = cursor.getString(cursor.getColumnIndex(DBcontains.SBUJECT));
        tx.setText(addItem);


   int seenMV = cursor.getInt(cursor.getColumnIndex(DBcontains.Seen));


        if (seenMV == 1) {
            Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.vvv);
            txurl.setImageBitmap(bitmapOrg);
        }

        else if (seenMV == 0) {


            txurl.setImageBitmap(null);
        }

        if (!(addItem==null)) {
            try {
                ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
                File directory = cw.getDir("moviesImages", Context.MODE_PRIVATE);
                File f = new File(directory, "img_" + addItem + ".jpg");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                ImageView img = (ImageView) view.findViewById(R.id.imageViewF);
                img.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }



}
