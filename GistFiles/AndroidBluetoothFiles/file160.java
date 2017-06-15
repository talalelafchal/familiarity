package andrej.jelic.attendance;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Korisnik on 14.7.2015..
 */
public class StudentCursor extends CursorAdapter {

    public StudentCursor(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.students, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView student = (TextView) view.findViewById(R.id.StudentsList);
        TextView studentAttendTime = (TextView) view.findViewById(R.id.StudentsAttendTime);
        TextView studentLeaveTime = (TextView) view.findViewById(R.id.StudentsLeaveTime);

        String studentString = cursor.getString(cursor.getColumnIndexOrThrow("student"));
        String studentAttendTimeString = cursor.getString(cursor.getColumnIndexOrThrow("attendTime"));
        String studentLeaveTimeString = cursor.getString(cursor.getColumnIndexOrThrow("leaveTime"));

        student.setText(studentString);
        studentAttendTime.setText(studentAttendTimeString);
        studentLeaveTime.setText(studentLeaveTimeString);

    }
}
