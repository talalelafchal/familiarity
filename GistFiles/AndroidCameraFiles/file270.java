package cardexc.com.practicework;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class AddFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 115;

    private OnAddFinish mActivity;

    private ImageView camera_image;
    private EditText editText_place;
    private static EditText editText_date;
    private static EditText editText_time;
    private Button button_set_date;
    private Button button_set_time;

    private Bitmap image;

    public interface OnAddFinish{
        void OnAddFinish(String text);
        void OnAddFinish(Intent intent);
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = (OnAddFinish) activity;
        super.onAttach(activity);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_layout, container, false);

        setHasOptionsMenu(true);

        camera_image = (ImageView) view.findViewById(R.id.camera_image);
        editText_place = (EditText) view.findViewById(R.id.addLayout_editText_place);
        editText_date = (EditText) view.findViewById(R.id.addLayout_editText_date);
        editText_time = (EditText) view.findViewById(R.id.addLayout_editText_time);

        button_set_date = (Button) view.findViewById(R.id.addLayout_button_set_date);
        button_set_time = (Button) view.findViewById(R.id.addLayout_button_set_time);

        camera_image.setOnClickListener(onImageClick());
        button_set_date.setOnClickListener(onSetDateClick());
        button_set_time.setOnClickListener(onSetTimeClick());

        return view;

    }

    @NonNull
    private View.OnClickListener onSetTimeClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
            }
        };
    }

    @NonNull
    private View.OnClickListener onSetDateClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "datePicker");
            }
        };
    }

    @NonNull
    private View.OnClickListener onImageClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.action_done): {

                Intent intent = new Intent();

                if (image != null)
                    intent.putExtra("image", image);

                if (!"".equals(editText_time.getText().toString()))
                    intent.putExtra("time", editText_time.getText().toString());

                if (!"".equals(editText_date.getText().toString()))
                    intent.putExtra("date", editText_date.getText().toString());

                if (!"".equals(editText_place.getText().toString()))
                    intent.putExtra("place", editText_place.getText().toString());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("image", byteArray);

                mActivity.OnAddFinish(intent);

                return true;
            }
        }
        return true;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_menu, menu);
    }

    public void takePicture() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            image = data.getParcelableExtra("data");
            camera_image.setImageBitmap(image);
        }


    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            editText_date.setText(year + "/" + month + "/" + day);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            editText_time.setText(hourOfDay + ":" + minute);
        }
    }

}
