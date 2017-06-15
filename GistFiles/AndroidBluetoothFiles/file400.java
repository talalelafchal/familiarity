package andrej.jelic.attendance;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Korisnik on 8.7.2015..
 */
public class ActiveStudentCursor extends CursorAdapter {
    public ActiveStudentCursor(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.active_students, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView student = (TextView) view.findViewById(R.id.activeStudentsList);
        TextView studentTime = (TextView) view.findViewById(R.id.activeStudentsTime);

        String studentString = cursor.getString(cursor.getColumnIndexOrThrow("student"));
        String studentTimeString = cursor.getString(cursor.getColumnIndexOrThrow("attendTime"));

        student.setText(studentString);
        studentTime.setText(studentTimeString);

    }
}
