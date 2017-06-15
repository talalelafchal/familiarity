package com.ztt.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.Date;
import java.util.UUID;

import model.Crime;
import model.CrimeLab;
import model.Photo;
import util.PictureUtils;

/**
 * Created by 123 on 14-11-9.
 */
public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID="com.example.criminal_intent.crime_id";
    private static final String DIALOG_IMAGE="image";
    private static final String TAG="CrimeFragment";
    private static final String DIALOG_DATE="date";
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_PHOTO=1;
    private static final int REQUEST_CANTACT=2;

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mSuspectButton;
    private CheckBox mSolvedCheckBox;
    public static CrimeFragment newInstance(UUID crimeId)
    {
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_CRIME_ID,crimeId);

        CrimeFragment fragment=new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_crime,container,false);

        if(NavUtils.getParentActivityName(getActivity())!=null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleField=(EditText)v.findViewById(R.id.crime_title);

        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mCrime.setTitle(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mDateButton=(Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm=getActivity().getSupportFragmentManager();
                DatePickerFragment dialog=DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(fm,DIALOG_DATE);
            }
        });

        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });

        mPhotoButton=(ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
                //getImageFromCamera();

            }
        });
        mPhotoView=(ImageView)v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Photo p=mCrime.getPhoto();
                if(p==null)
                    return;
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                String path=getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fragmentManager,DIALOG_IMAGE);
            }
        });
        Button reportButton=(Button)v.findViewById(R.id.crime_reportButton);
        Log.i(TAG,"reportButton:"+reportButton.getText());
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                String crimeReportText=getCrimeReport();
                i.putExtra(Intent.EXTRA_TEXT,crimeReportText);
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                i=Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);
            }
        });
        mSuspectButton=(Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i,REQUEST_CANTACT);
            }
        });
        if(mCrime.getSuspect()!=null)
        {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeID=(UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        Log.v(EXTRA_CRIME_ID,"crimeID"+crimeID.toString());
        mCrime= CrimeLab.get(getActivity()).getCrime(crimeID);
        Log.v(EXTRA_CRIME_ID,"crimeIsNull"+new Boolean(mCrime==null).toString());
        setHasOptionsMenu(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity())!=null)
                    NavUtils.navigateUpFromSameTask(getActivity());
                return  true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK) return;

        if(requestCode== REQUEST_DATE)
        {
            Date date=(Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            Log.v(EXTRA_CRIME_ID,"date.toString():"+date.toString());
            mCrime.setDate(date);
            updateDate();
        }
        else if(requestCode==REQUEST_PHOTO)
        {

          String filename=data.getStringExtra(CrimeCameraFragment.EXTRA_FILENAME);
            if(filename!=null)
            {
                Log.i(TAG,"filename: "+filename);
                Photo p=new Photo(filename);
                mCrime.setPhoto(p);
                Log.i(TAG,"Crime "+mCrime+"has a photo");
                showPhoto();

            }


        }else  if(requestCode==REQUEST_CANTACT){
            Uri contactUri=data.getData();
            String[] queryFields=new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c=getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            if(c.getCount()==0)
            {
                c.close();
                return;
            }
            c.moveToFirst();
            String suspect=c.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
            c.close();

        }
    }
    private void getImageFromCamera()
    {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, REQUEST_PHOTO);
        }
        else {
            Toast.makeText(getActivity(), "NO SD CARD", Toast.LENGTH_LONG).show();
        }
    }
    private void updateDate()
    {
        mDateButton.setText(mCrime.getDate().toString());
    }
    private void showPhoto()
    {
        Photo p=mCrime.getPhoto();
        BitmapDrawable bitmapDrawable=null;

        if(p!=null)
        {
            String path=getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            bitmapDrawable= PictureUtils.getScaledDrawable(getActivity(),path);
        }
        mPhotoView.setImageDrawable(bitmapDrawable);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    private String getCrimeReport()
    {
        String solvedString =null;
        if(mCrime.isSolved())
        {
            solvedString=getString(R.string.crime_report_solved);
        }
        else
        {
            solvedString=getString(R.string.crime_report_unsolved);
        }
        String dateFormat="EEE , MMM dd";
        String dateString= DateFormat.format(dateFormat,mCrime.getDate()).toString();
        String suspect=mCrime.getSuspect();
        if(suspect==null)
        {
            suspect=getString(R.string.crime_report_no_suspect);
        }
        else
        {
            suspect=getString(R.string.crime_report_suspect,suspect);
        }
        String report=getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }
}
