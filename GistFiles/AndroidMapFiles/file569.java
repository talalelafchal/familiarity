package com.example.clojureequivalent2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.*;

public class MyActivity2 extends FragmentActivity {

    private TextView vName;
    private TextView vLocation;
    private TextView vDate;
    private Button vPickDateButton;
    private Button vAddEventButton;
    private TextView vListing;

    private void showPicker(DialogFragment dialog) {
        dialog.show(getSupportFragmentManager(), "datePicker");
    }

    private SortedMap<String, ArrayList<Event>> listing = new TreeMap<String, ArrayList<Event>>();

    class Event {
        String name;
        String location;

        public Event(String name, String location) {
            this.name = name;
            this.location = location;
        }
    }

    private String formatEvents(ArrayList<Event> events) {
        String ret = "";
        for (int i = 0; i < events.size(); i++) {
            if (i > 0) {
                ret += "                      ";
            }
            Event event = events.get(i);
            ret += String.format("%s - %s\n", event.location, event.name);
        }
        return ret;
    }

    private String formatListing(SortedMap<String, ArrayList<Event>> list) {
        String ret = "";
        for (Map.Entry<String, ArrayList<Event>> entry : list.entrySet()) {
            ret += String.format("%s - %s", entry.getKey(), formatEvents(entry.getValue()));
        }
        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        vName = (TextView) findViewById(R.id.name);
        vLocation = (TextView) findViewById(R.id.location);
        vDate = (TextView) findViewById(R.id.date);
        vPickDateButton = (Button) findViewById(R.id.pickDateButton);
        vAddEventButton = (Button) findViewById(R.id.addEventButton);
        vListing = (TextView) findViewById(R.id.listing);

        vPickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(datePicker());
            }
        });
        vAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent();
            }
        });
    }

    private void updateUi() {
        vListing.setText(formatListing(listing));
        vLocation.setText("");
        vName.setText("");
    }

    private void addEvent() {
        String dateKey = vDate.getText().toString();
        try {
            Integer.parseInt(dateKey);
        } catch (Exception e) {
            return;
        }
        ArrayList<Event> value = listing.get(dateKey);
        if (value == null) {
            value = new ArrayList<Event>();
        }
        value.add(new Event(vName.getText().toString(), vLocation.getText().toString()));
        listing.put(dateKey, value);
        updateUi();
    }

    private DialogFragment datePicker() {

        DialogFragment dialog = DatePickerDialogFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                vDate.setText(String.format("%d%02d%02d", year, monthOfYear + 1, dayOfMonth));
            }
        });
        return dialog;
    }

    static public class DatePickerDialogFragment extends DialogFragment {

        static public DatePickerDialogFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
            DatePickerDialogFragment f = new DatePickerDialogFragment();
            f.listener = listener;
            return f;
        }

        private DatePickerDialog.OnDateSetListener listener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                    listener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            return dialog;
        }
    }
}
