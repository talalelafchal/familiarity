package com.lucaspozzi.bookscan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.IntentIntegrator;
import android.IntentResult;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;





public class MainActivity extends Activity implements OnClickListener {

    private Button previewBtn = (ImageButton) findViewById(R.id.preview_btn);
    private ImageButton linkBtn;
    private TextView authorText = (TextView) findViewById(R.id.book_author), titleText = (TextView) findViewById(R.id.book_title), descriptionText = (TextView) findViewById(R.id.book_description), dateText, ratingCountText;
    private LinearLayout starLayout;
    private ImageView thumbView;
    private ImageView[] starViews;
    private Bitmap thumbImg;
    private RatingBar book_rating_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button scanBtn = (Button) findViewById(R.id.scan_button);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        scanBtn.setOnClickListener(this);
        previewBtn.setVisibility(View.GONE);
        previewBtn.setOnClickListener(this);
        linkBtn = (ImageButton) findViewById(R.id.link_btn);
        linkBtn.setVisibility(View.GONE);
        linkBtn.setOnClickListener(this);
        dateText = (TextView) findViewById(R.id.book_date);
        starLayout = (LinearLayout) findViewById(R.id.star_layout);
        ratingCountText = (TextView) findViewById(R.id.book_rating_count);
        thumbView = (ImageView) findViewById(R.id.thumb);
        starViews = new ImageView[5];
        for (int s = 0; s < starViews.length; s++) {
            starViews[s] = new ImageView(this);
        }
        if (savedInstanceState != null) {
            authorText.setText(savedInstanceState.getString("author"));
            titleText.setText(savedInstanceState.getString("title"));
            descriptionText.setText(savedInstanceState.getString("description"));
            dateText.setText(savedInstanceState.getString("date"));
            ratingCountText.setText(savedInstanceState.getString("ratings"));
            int numStars = savedInstanceState.getInt("stars");
            for (int s = 0; s < numStars; s++) {
                starViews[s].setImageResource(android.R.drawable.star_on);
                starLayout.addView(starViews[s]);
            }
            starLayout.setTag(numStars);
            thumbImg = savedInstanceState.getParcelable("thumbPic");
            thumbView.setImageBitmap(thumbImg);
            previewBtn.setTag(savedInstanceState.getString("isbn"));
            if (savedInstanceState.getBoolean("isEmbed")) previewBtn.setEnabled(true);
            else previewBtn.setEnabled(false);
            if (savedInstanceState.getInt("isLink") == View.VISIBLE)
                linkBtn.setVisibility(View.VISIBLE);
            else linkBtn.setVisibility(View.GONE);
            previewBtn.setVisibility(View.VISIBLE);

        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.activity_main.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
        else if (v.getId() == R.id.link_btn) {
            String tag = (String) v.getTag();
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse(tag));
            startActivity(webIntent);
        }
        else if (v.getId() == R.id.preview_btn) {
            String tag = (String) v.getTag();
            Intent intent = new Intent(this, EmbeddedBook.class);
            intent.putExtra("isbn", tag);
            startActivity(intent);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            Log.v("SCAN", "content: " + scanContent + " - format: " + scanFormat);
            if (scanContent != null && scanFormat != null && scanFormat.equalsIgnoreCase("EAN_13")) {
                previewBtn.setTag(scanContent);
                String bookSearchString = "https://wwww.googleapis.com/books/v1/volumes?" +
                        "q=isbn:" + scanContent + "&key=AIzaSyASh3kkk29FQV78AaN0GAgsDKsY_K45afM";
                new GetBookInfo().execute(bookSearchString);

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Just searching Google", Toast.LENGTH_SHORT);
                toast.show();
            }

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Google didn't send anything again", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class GetBookInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... bookURLs) {
            StringBuilder bookBuilder = new StringBuilder();
            for (String bookSearchURL : bookURLs) {
                HttpClient bookClient = new DefaultHttpClient();
                try {
                    HttpGet bookGet = new HttpGet(bookSearchURL);
                    HttpResponse bookResponse = bookClient.execute(bookGet);
                    StatusLine bookSearchStatus = bookResponse.getStatusLine();

                    if (bookSearchStatus.getStatusCode() == 200) {
                        //result
                        HttpEntity bookEntity = bookResponse.getEntity();
                        InputStream bookContent = bookEntity.getContent();
                        InputStreamReader bookInput = new InputStreamReader(bookContent);
                        BufferedReader bookReader = new BufferedReader(bookInput);
                        String lineIn;
                        while ((lineIn = bookReader.readLine()) != null) {
                            bookBuilder.append(lineIn);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bookBuilder.toString();
        }

        protected void onPostExecute(String result) {
            try {
                previewBtn.setVisibility(View.VISIBLE);
                JSONObject resultObject = new JSONObject(result);
                JSONArray bookArray = resultObject.getJSONArray("items");
                JSONObject bookObject = bookArray.getJSONObject(0);
                JSONObject volumeObject = bookObject.getJSONObject("volumeInfo");

                try {
                    titleText.setText("TITLE: " + volumeObject.getString("title"));
                } catch (JSONException jse) {
                    titleText.setText("");
                    jse.printStackTrace();
                }

                StringBuilder authorBuild = new StringBuilder("");
                try {
                    JSONArray authorArray = volumeObject.getJSONArray("authors");
                    for (int a = 0; a < authorArray.length(); a++) {
                        if (a > 0) authorBuild.append(", ");
                        authorBuild.append(authorArray.getString(a));
                    }
                    authorText.setText("AUTHOR(S): " + authorBuild.toString());
                } catch (JSONException jse) {
                    authorText.setText("");
                    jse.printStackTrace();
                }
                try {
                    dateText.setText("PUBLISHED: " + volumeObject.getString("publishedDate"));
                } catch (JSONException jse) {
                    dateText.setText("");
                    jse.printStackTrace();
                }

                try {
                    descriptionText.setText("DESCRIPTION: " + volumeObject.getString("description"));
                } catch (JSONException jse) {
                    descriptionText.setText("");
                    jse.printStackTrace();
                }
                try {
                    double decNumStars = Double.parseDouble(volumeObject.getString("averageRating"));
                    int numStars = (int) decNumStars;
                    starLayout.setTag(numStars);
                    starLayout.removeAllViews();
                    for (int s = 0; s < numStars; s++) {
                        starViews[s].setImageResource(android.R.drawable.star_on);
                        starLayout.addView(starViews[s]);
                    }
                } catch (JSONException jse) {
                    starLayout.removeAllViews();
                    jse.printStackTrace();
                }

                try {
                    ratingCountText.setText(" - " + volumeObject.getString("ratingsCount") + " ratings");
                } catch (JSONException jse) {
                    ratingCountText.setText("");
                    jse.printStackTrace();
                }

                try {
                    boolean isEmbeddable = Boolean.parseBoolean
                            (bookObject.getJSONObject("accessInfo").getString("embeddable"));
                    if (isEmbeddable) previewBtn.setEnabled(true);
                    else previewBtn.setEnabled(false);
                } catch (JSONException jse) {
                    previewBtn.setEnabled(false);
                    jse.printStackTrace();
                }

                try {
                    linkBtn.setTag(volumeObject.getString("infoLink"));
                    linkBtn.setVisibility(View.VISIBLE);
                } catch (JSONException jse) {
                    linkBtn.setVisibility(View.GONE);
                    jse.printStackTrace();
                }

                try {
                    JSONObject imageInfo = volumeObject.getJSONObject("imageLinks");
                    new GetBookThumb().execute(imageInfo.getString("smallThumbnail"));
                } catch (JSONException jse) {
                    thumbView.setImageBitmap(null);
                    jse.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                titleText.setText("TRY EBSCO INSTEAD");
                authorText.setText("");
                descriptionText.setText("");
                dateText.setText("");
                starLayout.removeAllViews();
                ratingCountText.setText("");
                thumbView.setImageBitmap(null);
                previewBtn.setVisibility(View.GONE);
            }
        }
    }

    private class GetBookThumb extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... thumbURLs) {
            try {
                URL thumbURL = new URL(thumbURLs[0]);
                URLConnection thumbConn = thumbURL.openConnection();
                thumbConn.connect();
                InputStream thumbIn = thumbConn.getInputStream();
                BufferedInputStream thumbBuff = new BufferedInputStream(thumbIn);
                thumbImg = BitmapFactory.decodeStream(thumbBuff);
                thumbBuff.close();
                thumbIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            thumbView.setImageBitmap(thumbImg);
        }
    }

    protected void onSaveInstanceState(Bundle savedBundle) {
        savedBundle.putString("title", "" + titleText.getText());
        savedBundle.putString("author", "" + authorText.getText());
        savedBundle.putString("description", "" + descriptionText.getText());
        savedBundle.putString("date", "" + dateText.getText());
        savedBundle.putString("ratings", "" + ratingCountText.getText());
        savedBundle.putParcelable("thumbPic", thumbImg);

        if (starLayout.getTag() != null)
            savedBundle.putInt("stars", Integer.parseInt(starLayout.getTag().toString()));
        savedBundle.putBoolean("isEmbed", previewBtn.isEnabled());
        savedBundle.putInt("isLink", linkBtn.getVisibility());
        if (previewBtn.getTag() != null)
            savedBundle.putString("isbn", previewBtn.getTag().toString());
    }
}

/*
    **
    **    @Override
    **  public boolean onCreateOptionsMenu(Menu menu) {
    Inflate the menu; this adds items to the action bar if it is present.
    **    getMenuInflater().inflate(R.menu.main, menu);
    **      return true;
    **   }
    **  @Override
    **  public boolean onOptionsItemSelected(MenuItem item) {
    Handle action bar item clicks here. The action bar will
    automatically handle clicks on the Home/Up button, so long
    as you specify a parent activity in AndroidManifest.xml.
    **       int id = item.getItemId();
    **       if (id == R.id.action_settings) {
    **          return true;
    **      }
    **       return super.onOptionsItemSelected(item);
    **    }
    **
    */


