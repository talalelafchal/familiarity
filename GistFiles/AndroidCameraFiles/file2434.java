package com.eventify.android.fragments;
 
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
 
import com.afollestad.materialdialogs.MaterialDialog;
import com.eventify.android.R;
import com.eventify.android.models.Attendee;
import com.eventify.android.models.Comment;
import com.eventify.android.models.Event;
import com.eventify.android.models.EventifyUser;
import com.eventify.android.utils.AsyncCallback;
import com.eventify.android.utils.MeasurementUtils;
import com.eventify.android.views.BaseParseArrayAdapter;
import com.eventify.android.views.ViewHolder;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.parse.ConfigCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
 
import butterknife.InjectView;
import butterknife.OnClick;
 
/**
 * Created by Greg on 5/28/15.
 */
public class EventDetailsFragment extends BaseFragment
{
    @InjectView(R.id.text_view_event_name_details) TextView mEventNameTextView;
    @InjectView(R.id.rating_bar_event_details) RatingBar mEventsRatingBar;
    @InjectView(R.id.grid_view_attendees) GridLayout mAttendeesGridLayout;
    @InjectView(R.id.list_view_comments) ListView mCommentsListView;
    @InjectView(R.id.edit_text_comment) EditText mCommmentEditText;
    @InjectView(R.id.button_sign_me_up) Button mSignMeUpButton;
    @InjectView(R.id.button_send_comment) Button mSendCommentButton;
    @InjectView(R.id.sliding_panel_header) View mEventsHeader;
 
    private CommentsArrayAdapter mCommentsAdapter;
 
    private final int MAX_PICS = 27;
    private int profilePicWidth, profilePicHeight;
    private Event mCurrentlySelectedEvent;
    private Attendee mCurrentAttendee;
 
    @Override
    public View getRootView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }
 
    @Override
    public void initUI()
    {
        profilePicWidth = (int) MeasurementUtils.convertDpToPixel(50, getActivity());
        profilePicHeight = (int) MeasurementUtils.convertDpToPixel(50, getActivity());
        setRatingBarColor(Color.WHITE);
    }
 
    @OnClick(R.id.button_send_comment)
    public void onSendCommentClicked()
    {
        String commentText = mCommmentEditText.getText().toString();
        if (commentText.length() > 0)
        {
            mSendCommentButton.setEnabled(false);
            mCommmentEditText.setText("");
 
            Comment comment = new Comment();
            comment.setAttendee(mCurrentAttendee);
            comment.setContents(commentText);
            comment.saveInBackground(new SaveCallback()
            {
                @Override
                public void done(ParseException e)
                {
                    if (mSendCommentButton != null)
                        mSendCommentButton.setEnabled(true);
 
                    if (e == null)
                    {
                        if (mCommentsAdapter != null)
                            mCommentsAdapter.loadObjects();
                    }
                    else
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
 
    @OnClick(R.id.button_sign_me_up)
    public void onSignMeUpClicked()
    {
        final MaterialDialog progressDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .content(R.string.signing_up)
                .title(R.string.please_wait)
                .build();
 
        Attendee attendee = new Attendee();
        attendee.setEvent(mCurrentlySelectedEvent);
        attendee.setUser(EventifyUser.getCurrentUser());
        attendee.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (progressDialog != null)
                    progressDialog.cancel();
 
                if (e == null)
                {
                    showAttendeeView();
                }
                else
                {
                    e.printStackTrace();
                    if (getActivity() != null)
                    {
                        Toast.makeText(getActivity(), R.string.error_signing_up, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
 
    @OnClick(R.id.button_invite_friends)
    public void onInviteFriendsButtonClicked()
    {
        ParseConfig.getInBackground(new ConfigCallback()
        {
            @Override
            public void done(ParseConfig parseConfig, ParseException e)
            {
                if (e == null)
                {
                    String shareUrl = parseConfig.getString("shareUrl");
                    String pictureUrl = parseConfig.getParseFile("shareLogo") != null ?
                            parseConfig.getParseFile("shareLogo").getUrl() : "";
                    sendFacebookInvite(shareUrl, pictureUrl);
                }
                else
                {
                    e.printStackTrace();
                }
            }
        });
    }
 
    public void updateUI(final Event eventData)
    {
        hideAttendeeView();
        mSignMeUpButton.setEnabled(false);
 
        int eventColor = eventData.getEventType().getColor();
        mEventsHeader.setBackgroundColor(eventColor);
 
        int overlayColor = MeasurementUtils.shouldUseBlackTextColor(eventColor) ? Color.BLACK : Color.WHITE;
        mEventNameTextView.setTextColor(overlayColor);
        setRatingBarColor(overlayColor);
 
        getAttendee(new AsyncCallback<Attendee>()
        {
            @Override
            public void onAsyncOperationCompleted(Attendee attendee)
            {
                if (getActivity() != null)
                {
                    mCurrentAttendee = attendee;
 
                    if (attendee != null)
                        showAttendeeView();
                    else
                        hideAttendeeView();
 
                    mEventNameTextView.setText(eventData.getName());
                    mEventsRatingBar.setRating((float) eventData.getRating());
                    updateAttendeesImages(eventData);
 
                    mCommentsAdapter = new CommentsArrayAdapter(getActivity(), eventData);
                    mCommentsListView.setAdapter(mCommentsAdapter);
                }
            }
        });
    }
 
    private void updateAttendeesImages(Event eventData)
    {
        mAttendeesGridLayout.removeAllViews();
        mCurrentlySelectedEvent = eventData;
 
        Attendee.getQuery()
                .whereEqualTo("event", eventData)
                .setLimit(MAX_PICS)
                .include("user")
                .findInBackground(new FindCallback<Attendee>()
                {
                    @Override
                    public void done(List<Attendee> list, ParseException e)
                    {
                        if (e == null)
                        {
                            if (getActivity() != null)
                            {
                                addAttendeesProfilePictures(list);
                            }
                        }
                        else
                        {
                            e.printStackTrace();
                        }
                    }
                });
    }
 
    public void showAttendeeView()
    {
        mCommmentEditText.setVisibility(View.VISIBLE);
        mSendCommentButton.setVisibility(View.VISIBLE);
        mSignMeUpButton.setVisibility(View.INVISIBLE);
    }
 
    public void hideAttendeeView()
    {
        mCommmentEditText.setVisibility(View.INVISIBLE);
        mSendCommentButton.setVisibility(View.INVISIBLE);
        mSignMeUpButton.setVisibility(View.VISIBLE);
        mSignMeUpButton.setEnabled(true);
    }
 
    private void getAttendee(final AsyncCallback<Attendee> isAttendeeCallback)
    {
        Attendee.getQuery()
                .whereEqualTo("user", EventifyUser.getCurrentUser())
                .getFirstInBackground(new GetCallback<Attendee>()
                {
                    @Override
                    public void done(Attendee attendee, ParseException e)
                    {
                        isAttendeeCallback.onAsyncOperationCompleted(attendee);
                    }
                });
    }
 
    private void addAttendeesProfilePictures(List<Attendee> attendees)
    {
        for (Attendee attendee : attendees)
            attendee.getUser().getProfilePictureUrl(profilePicWidth,
                    profilePicHeight, new AsyncCallback<String>()
                    {
                        @Override
                        public void onAsyncOperationCompleted(String result)
                        {
                            fetchAndAddProfilePicture(result);
                        }
                    });
 
        int nCols = mAttendeesGridLayout.getWidth() / profilePicWidth;
        mAttendeesGridLayout.setColumnCount(nCols);
    }
 
    private void fetchAndAddProfilePicture(String profileUrl)
    {
        final ImageView profilePicImageView = (ImageView) View.inflate(getActivity(),
                R.layout.row_layout_attendee, null);
 
        if (profilePicImageView != null)
        {
            Picasso.with(getActivity()).load(profileUrl).into(profilePicImageView, new Callback()
            {
                @Override
                public void onSuccess()
                {
                    if (mAttendeesGridLayout != null)
                        mAttendeesGridLayout.addView(profilePicImageView);
                }
 
                @Override
                public void onError()
                {
                }
            });
        }
    }
 
    private void sendFacebookInvite(String appShareUrl, String appSharePictureUrl)
    {
        if (AppInviteDialog.canShow())
        {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appShareUrl)
                    .setPreviewImageUrl(appSharePictureUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }
    }
 
    private void setRatingBarColor(int color)
    {
        LayerDrawable stars = (LayerDrawable) mEventsRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        mEventsRatingBar.setProgressDrawable(stars);
    }
 
    private static class CommentsArrayAdapter extends BaseParseArrayAdapter<Comment>
    {
        public CommentsArrayAdapter(Context context, final Event event)
        {
            super(context, new QueryFactory()
            {
                @Override
                public ParseQuery<Comment> create()
                {
                    ParseQuery<Attendee> attendeesQuery = Attendee.getQuery()
                            .whereEqualTo("event", event);
 
                    return Comment.getQuery()
                            .whereMatchesQuery("commenter", attendeesQuery);
                }
            }, R.layout.row_layout_comment);
        }
 
        @Override
        protected void initView(ViewHolder holder, Comment data)
        {
            TextView commentTextView = (TextView) holder.getView(R.id.text_view_comment);
            TextView commentTimeTextView = (TextView) holder.getView(R.id.text_view_comment_time);
 
            commentTextView.setText(data.getContents());
            commentTimeTextView.setText(getFormattedTime(data.getUpdatedAt()));
        }
 
        private SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        private SimpleDateFormat dayFormatter = new SimpleDateFormat("EEE");
        private SimpleDateFormat monthFormatter = new SimpleDateFormat("LLL d");
        private SimpleDateFormat yearFormatter = new SimpleDateFormat("MM/d/yyyy");
 
        private String getFormattedTime(Date date)
        {
            long dDays = (System.currentTimeMillis() - date.getTime()) / (1000 * 3600 * 24);
            long dWeeks = (System.currentTimeMillis() - date.getTime()) / (1000 * 3600 * 24 * 7);
            long dYears = (System.currentTimeMillis() - date.getTime()) / (1000 * 3600 * 24 * 356);
 
            if (dDays == 0)
                return timeFormatter.format(date);
            else if (dWeeks == 0)
                return dayFormatter.format(date);
            else if (dYears == 0)
                return monthFormatter.format(date);
            else
                return yearFormatter.format(date);
        }
    }
}