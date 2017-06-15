package andrej.jelic.attendance;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Korisnik on 20.7.2015..
 */
public class TableAdapter extends CursorAdapter {

    public TableAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.tables, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView table = (TextView) view.findViewById(R.id.tablesList);

        String tableString = cursor.getString(cursor.getColumnIndexOrThrow("tableName"));

        table.setText(tableString);

    }
}
