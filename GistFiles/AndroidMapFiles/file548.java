package com.mariniero.aga.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geo.dating.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.mariniero.aga.Config;
import com.mariniero.aga.ui.comparators.ChatComparator;
import com.mariniero.aga.ui.widget.RoundedTransformation;
import com.mariniero.aga.utils.TimeUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tengchong.android.CircleLoadingView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import geo.dating.engine.chat.ChatMessage;
import geo.dating.engine.enums.ChatMessageType;
import geo.dating.engine.enums.ChatType;
import geo.dating.engine.models.GeoDatingEngine;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private static final String LOG_TAG = MessagesAdapter.class.getSimpleName();
    private static List<ChatMessage> mEditDataset;
    private final Context mContext;
    private final boolean mSaveImages;
    private final int mSaveVideos;
    private final long mOwnId;
    private final int VIEW_TYPE_OWN = 0;
    private final int VIEW_TYPE_NOT_OWN = 1;
    private List<ChatMessage> mDataset;
    private boolean mEditMode = false;

    private HashMap<Long, PrivateMode> mPrivateModeOn;
    private HashMap<Long, PrivateMode> mPrivateModeOff;
    private HashMap<Long, Integer> mTypeChange;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessagesAdapter(List<ChatMessage> myDataset, Context context) {
        mDataset = myDataset;
        if (mDataset != null) {
            Collections.sort(mDataset, ChatComparator.MESSAGE_DATE_COMPARATOR);
        }

        mContext = context;
        mEditDataset = new ArrayList<>();
        mPrivateModeOn = new HashMap<>();
        mPrivateModeOff = new HashMap<>();
        mTypeChange = new HashMap<>();
        mOwnId = Long.valueOf(GeoDatingEngine.getInstance().getCurrentUserNumber());

        File downloadDirectory = new File(Config.SAVE_PATH);
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdir();
        }

        mSaveImages = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(Config.Preferences.SAVE_IMAGES, true);
        mSaveVideos = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getInt(Config.Preferences.SAVE_VIDEOS, Config.Autoload.WIFI);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        // set the view's size, margins, paddings and layout parameters
        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_NOT_OWN:
                layoutId = R.layout.list_item_chat_message;
                break;
            case VIEW_TYPE_OWN:
            default:
                layoutId = R.layout.list_item_chat_message_own;
                break;
        }

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataset != null) {
            return mDataset.get(position).getSenderUserId() == mOwnId ?
                    VIEW_TYPE_OWN : VIEW_TYPE_NOT_OWN;
        }

        return VIEW_TYPE_NOT_OWN;
    }

    public void addList(List<ChatMessage> dataset) {
        if (dataset == null) {
            return;
        }

        mDataset.addAll(dataset);
        Collections.sort(mDataset, ChatComparator.MESSAGE_DATE_COMPARATOR);
        notifyItemRangeInserted(0, dataset.size());
    }

    public void toggleEditMode(boolean toggle) {
        if (toggle) {
            mEditDataset = new ArrayList<>();
        }
        mEditMode = toggle;
        notifyDataSetChanged();
    }

    public void pickAllEditMode() {
        mEditDataset = mDataset;
        notifyDataSetChanged();
    }


    public boolean isInEditMode() {
        return mEditMode;
    }

    public List<ChatMessage> getEditDataset() {
        return mEditDataset;
    }

    public void resetEditDataset() {
        mEditDataset = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void bulkRemovePicked() {
        if (mContext instanceof Listener) {
            ((Listener) mContext).onRemoveTrigger();
        }
        mDataset.removeAll(mEditDataset);
        notifyDataSetChanged();
    }

    public void addItem(ChatMessage message) {
        if (mDataset == null) {
            mDataset = new ArrayList<>();
        }

        mDataset.add(message);
        notifyItemInserted(mDataset.size() - 1);
        if (mContext instanceof Listener) {
            ((Listener) mContext).onAddTrigger();
        }
    }

    public void setItem(int position, ChatMessage message) {
        if (mDataset == null) {
            mDataset = new ArrayList<>();
            mDataset.add(message);
        } else if (position > 0 && position < mDataset.size()) {
            mDataset.set(position, message);
            notifyItemChanged(position);
        }
    }

    public void changeItem(ChatMessage message) {
        if (mDataset == null) {
            mDataset = new ArrayList<>();
            mDataset.add(message);
            notifyItemInserted(mDataset.size() - 1);
        } else {
            for (ChatMessage chatMessage : mDataset) {
                if (chatMessage.getStatus() == ChatMessage.Status.PENDING) {
                    //TODO: fix this up for TIMESTAMP comprassion
                    if ((chatMessage.getTempId() == message.getTempId() && chatMessage.getType() == ChatMessageType.TEXT) || chatMessage.getType() == ChatMessageType.VIDEO) {
                        int position = mDataset.indexOf(chatMessage);
                        if (mContext instanceof Listener) {
                            ((Listener) mContext).onChangeTrigger(mDataset.get(position));
                        }
                        message.setStatus(ChatMessage.Status.DELIVERED);
                        mDataset.set(position, message);
                        notifyItemChanged(position);
                        break;
                    }
                }
            }
        }
    }

    public void privateModeOn(String name, long duration, boolean copy, long created) {
        long lastPosition = mDataset != null && mDataset.size() > 0 ?
                mDataset.get(mDataset.size() - 1).getId() :
                0L;
        if (mPrivateModeOff.containsKey(lastPosition)) {
            mPrivateModeOff.remove(lastPosition);
        }

        mPrivateModeOn.put(lastPosition, new PrivateMode(name, duration, copy, created));
        notifyItemChanged(mDataset != null ? mDataset.size() - 1 : 0);
    }

    public void privateModeOff(long created) {
        long lastPosition = mDataset != null && mDataset.size() > 0 ?
                mDataset.get(mDataset.size() - 1).getId() :
                0L;
        if (mPrivateModeOn.containsKey(lastPosition)) {
            mPrivateModeOn.remove(lastPosition);
        }

        mPrivateModeOff.put(lastPosition, new PrivateMode(null, 0L, false, created));
        notifyItemChanged(mDataset != null ? mDataset.size() - 1 : 0);
    }

    public void chatTypeChanged(int type) {
        long lastPosition = mDataset != null && mDataset.size() > 0 ?
                mDataset.get(mDataset.size() - 1).getId() :
                0L;
        if (mTypeChange.containsKey(lastPosition)) {
            mTypeChange.remove(lastPosition);
        }

        mTypeChange.put(lastPosition, type);
        notifyItemChanged(mDataset != null ? mDataset.size() - 1 : 0);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setMessage(mDataset.get(position));
        holder.mEditModeLinearLayout.setVisibility(mEditMode ? View.VISIBLE : View.GONE);
        if (holder.mTimeLinearLayout.getChildCount() > 0) {
            holder.mTimeLinearLayout.removeAllViews();
        }

        holder.mEditModeLinearLayout.setVisibility(mEditMode ? View.VISIBLE : View.GONE);
        holder.mEditModeButton.setImageResource(mEditDataset
                .contains(mDataset.get(position)) ?
                R.drawable.select_dialog_delete_active : R.drawable.select_dialog_delete_normal);
        holder.mEditModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButtonClickEvent(holder);
            }
        });

        holder.mPrivacyLinearLayout.removeAllViews();
        if (mPrivateModeOn.containsKey(mDataset.get(position).getId())) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.fragment_dialog_alert_private_on, null, false);

            PrivateMode mode = mPrivateModeOn.get(mDataset.get(position).getId());


            int timeResource;
            switch ((int) mode.getDuration()) {
                case 120:
                    timeResource = R.string.chat_private_session_time_2;
                    break;
                case 300:
                    timeResource = R.string.chat_private_session_time_3;
                    break;
                case 600:
                    timeResource = R.string.chat_private_session_time_4;
                    break;
                case 1800:
                    timeResource = R.string.chat_private_session_time_5;
                    break;
                case 3600:
                    timeResource = R.string.chat_private_session_time_6;
                    break;
                case 86400:
                    timeResource = R.string.chat_private_session_time_7;
                    break;
                case 604800:
                    timeResource = R.string.chat_private_session_time_8;
                    break;
                default:
                    timeResource = R.string.chat_private_session_time_1;
                    break;
            }

            ((TextView) view.findViewById(R.id.chat_item_private_dialog_settings_lifetime_textview))
                    .setText(timeResource);
            ((TextView) view.findViewById(R.id.chat_item_private_dialog_settings_copy_textview))
                    .setText(mContext.getResources().getString(mode.isCopy() ? R.string.yes : R.string.no));

            holder.mPrivacyLinearLayout.addView(view);
        }

        if (mPrivateModeOff.containsKey(mDataset.get(position).getId())) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.fragment_dialog_alert_private_off, null, false);
            holder.mPrivacyLinearLayout.addView(view);
        }

        if (mTypeChange.containsKey(mDataset.get(position).getId())) {
            switch (mTypeChange.get(mDataset.get(position).getId())) {
                case ChatType.CLOSED: {
                    holder.mPrivacyLinearLayout.addView(LayoutInflater.from(mContext)
                            .inflate(R.layout.fragment_dialog_alert_group_opened_on, null, false));
                }
                break;
                case ChatType.OPENED: {
                    holder.mPrivacyLinearLayout.addView(LayoutInflater.from(mContext)
                            .inflate(R.layout.fragment_dialog_alert_group_opened_off, null, false));
                }
                break;
            }
        }

        if (mEditMode) {
            holder.mAvatarImageView.setVisibility(View.GONE);
            holder.mAvatarLinearLayout.setVisibility(View.GONE);
        } else if (mDataset.get(position).getUserFile() != null && (position == 0 || (mDataset.get((position - 1)).getSenderUserId() != mDataset.get(position).getSenderUserId()))) {
            holder.mAvatarImageView.setVisibility(View.VISIBLE);
            holder.mAvatarLinearLayout.setVisibility(View.VISIBLE);
            Picasso.with(mContext).cancelRequest(holder.mAvatarImageView);
            Picasso.with(mContext).load(mDataset.get(position).getUserFile()).into(holder.mAvatarImageView);
        } else {
//            holder.mAvatarImageView.setVisibility(View.GONE);
            holder.mAvatarLinearLayout.setVisibility(View.VISIBLE);

            String prevTime = TimeUtils.getDateFromTimestampForChatMessage(mDataset.get(position - 1).getCreated());
            String nowTime = TimeUtils.getDateFromTimestampForChatMessage(mDataset.get(position).getCreated());
            holder.mTimeTextView.setVisibility((prevTime.toCharArray()[prevTime.length() - 1] == nowTime.toCharArray()[prevTime.length() - 1]) ? View.GONE : View.VISIBLE);
            holder.mAvatarImageView.setVisibility((mDataset.get(position).getCreated() - mDataset.get(position-1).getCreated()) >= 180000 ? View.VISIBLE : View.GONE);
            if ((mDataset.get(position).getCreated() - mDataset.get(position-1).getCreated()) >= 180000) {
                Picasso.with(mContext).cancelRequest(holder.mAvatarImageView);
                Picasso.with(mContext).load(mDataset.get(position).getUserFile()).into(holder.mAvatarImageView);
            }
        }

        if (position == 0 || TimeUtils.diffOneDay(mDataset.get(position - 1).getCreated(), mDataset.get(position).getCreated())) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_chat_message_time, null, false);
            ((TextView) view.findViewById(R.id.list_item_chat_date_layout))
                    .setText(TimeUtils.formatHumanFriendlyDate(mContext, mDataset.get(position).getCreated()));
            holder.mTimeLinearLayout.addView(view);

            holder.mAvatarImageView.setVisibility(View.VISIBLE);
            holder.mAvatarLinearLayout.setVisibility(View.VISIBLE);
            Picasso.with(mContext).cancelRequest(holder.mAvatarImageView);
            Picasso.with(mContext).load(mDataset.get(position).getUserFile()).into(holder.mAvatarImageView);
        }

        if (mDataset.get(position).getCreated() != 0) {
            holder.mTimeTextView.setText(TimeUtils.getDateFromTimestampForChatMessage(
                    mDataset.get(position).getCreated()));
        } else {
            holder.mTimeTextView.setText("");
        }

        switch (mDataset.get(position).getType()) {
            case ChatMessageType.IMAGE:
                holder.mProgress.setVisibility(View.VISIBLE);
                holder.mFileImageView.setVisibility(View.VISIBLE);

                Picasso.with(mContext).cancelRequest(holder.mFileImageView);
                if (mSaveImages) {
                    downloadAndSaveImage(holder, position);
                } else {
                    setImage(holder, position);
                }

                holder.mFileImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mContext instanceof Listener) {
                            ((Listener) mContext).onImageClickTrigger(mDataset.get(position));
                        }
                    }
                });
                holder.mFileImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        toggleEditModeWithCallback(holder);
                        return true;
                    }
                });
                break;
            case ChatMessageType.TEXT:
                holder.mProgress.setVisibility(View.GONE);
                holder.mFileImageView.setVisibility(View.GONE);
                holder.mLoading.setVisibility(View.GONE);
                holder.mTextView.setText(mDataset.get(position).getMessage());
                holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        toggleEditModeWithCallback(holder);
                        return true;
                    }
                });
                break;
            case ChatMessageType.VIDEO:
                holder.mFileImageView.setVisibility(View.GONE);
                holder.mProgress.setVisibility(View.INVISIBLE);
                holder.mLoading.setVisibility(View.VISIBLE);
                switch (mSaveVideos) {
                    case Config.Autoload.NEVER:
                        break;
                    case Config.Autoload.WIFI:
                        NetworkInfo wifi = ((ConnectivityManager) mContext
                                .getSystemService(Context.CONNECTIVITY_SERVICE))
                                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        if (wifi.isConnected()) {
                            downloadOnly(mDataset.get(position).getMessage(), holder);
                        }
                        break;
                    case Config.Autoload.WIFI_CELLULAR:
                        downloadOnly(mDataset.get(position).getMessage(), holder);
                        break;
                }

                String fileName = mDataset.get(position).getMessage().split("/")[mDataset.get(position).getMessage().split("/").length - 1];
                final File file = new File(mDataset.get(holder.getAdapterPosition()).getStatus() == ChatMessage.Status.PENDING ?
                        mDataset.get(holder.getAdapterPosition()).getMessage() : Config.SAVE_PATH + fileName);
                if (file.exists()) {
                    holder.mLoading.setPercent(100);
                    holder.mVideoFileLabelImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFileDialog(file);
                        }
                    });
                } else {
                    holder.mVideoFileLabelImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadVideoFile(mDataset.get(position).getMessage(), holder);
                        }
                    });
                }

                if (holder.mTarget != null) {
                    Picasso.with(mContext).cancelRequest(holder.mTarget);
                }

                holder.mTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        holder.mLoading.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                holder.mLoading.setVisibility(View.VISIBLE);
                holder.mVideoFileLabelImageView.bringToFront();
                if (mDataset.get(holder.getAdapterPosition()).getStatus() != ChatMessage.Status.DELIVERED) {
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mDataset.get(holder.getAdapterPosition()).getMessage(), MediaStore.Images.Thumbnails.MINI_KIND);
                    holder.mLoading.setImageBitmap(thumb);
                } else {
                    Picasso.with(mContext).load(mDataset.get(position).getUserFile())
                            .transform(new RoundedTransformation(15))
                            .into(holder.mTarget);
                }
                holder.mLoading.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mContext instanceof Listener) {
                            ((Listener) mContext).onClickTrigger();
                        }
                    }
                });
                holder.mLoading.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        toggleEditModeWithCallback(holder);
                        return true;
                    }
                });
                break;
            default:
                break;
        }

        if (mDataset.get(holder.getAdapterPosition()).isOwn()) {
            switch (mDataset.get(holder.getAdapterPosition()).getStatus()) {
                case DELIVERED: {
                    holder.mFilesRelativeLayout.setBackgroundResource(R.drawable.message_own_layout);
                    holder.mTextView.setBackgroundResource(R.drawable.message_own_layout);
                }
                break;
                case PENDING: {
                    holder.mFilesRelativeLayout.setBackgroundResource(R.drawable.message_own_layout);
                    holder.mTextView.setBackgroundResource(R.drawable.message_own_layout);
                }
                break;
                case FAILED: {
                    holder.mFilesRelativeLayout.setBackgroundResource(R.drawable.message_own_not_delivered_layout);
                    holder.mTextView.setBackgroundResource(R.drawable.message_own_not_delivered_layout);
                }
                break;
            }

            switch (mDataset.get(holder.getAdapterPosition()).getType()) {
                case ChatMessageType.TEXT: {
                    holder.mRetryButton.setImageResource(R.drawable.mess_text_not_send);
                }
                break;
                case ChatMessageType.IMAGE: {
                    holder.mRetryButton.setImageResource(R.drawable.file_not_send);
                }
                break;
                case ChatMessageType.VIDEO: {
                    holder.mRetryButton.setImageResource(R.drawable.file_not_send);
                }
                break;
            }

            holder.mRetryButton.setClickable(true);
            holder.mRetryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof Listener) {
                        holder.mRetryButton.setClickable(false);
                        ((Listener) mContext).onRefreshTrigger(mDataset.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                    }
                }
            });
            holder.mRetryButton.setVisibility(mDataset.get(holder.getAdapterPosition()).getStatus() == ChatMessage.Status.FAILED ?
                    View.VISIBLE : View.GONE);
        }

        holder.mTimeTextView.getRootView().invalidate();
        holder.mFilesRelativeLayout.setVisibility(mDataset.get(position).getType() != ChatMessageType.TEXT ?
                View.VISIBLE : View.GONE);
        holder.mTextView.setVisibility(mDataset.get(position).getType() == ChatMessageType.TEXT ?
                View.VISIBLE : View.GONE);
        holder.mVideoFileLabelImageView.setVisibility(mDataset.get(position).getType() ==
                ChatMessageType.VIDEO ? View.VISIBLE : View.GONE);
    }

    private void setImage(final ViewHolder holder, int position) {
//        Glide.clear(holder.mFileImageView);
//        Glide.with(mContext)
//                .load(mDataset.get(position).getAttachment())
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        if (holder.mProgress != null) {
//                            holder.mProgress.setVisibility(View.GONE);
//                        }
//                        return false;
//                    }
//                })
//                .into(holder.mFileImageView);

        holder.mProgress.setIndeterminate(false);
        Picasso.with(mContext).cancelRequest(holder.mFileImageView);
        Picasso.with(mContext).load(mDataset.get(position).getAttachment())
                .into(holder.mFileImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (holder.mProgress != null) {
                            holder.mProgress.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void downloadAndSaveImage(final ViewHolder holder, int position) {
        holder.mProgress.setVisibility(View.GONE);
        String fileName = mDataset.get(position).getAttachment().split("/")
                [mDataset.get(position).getAttachment().split("/").length - 1];

        File fileSaved = new File(Config.SAVE_PATH + fileName);
        if (fileSaved.exists()) {
            Picasso.with(mContext).load(fileSaved)
                    .placeholder(R.drawable.cap_album)
                    .into(holder.mFileImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.mProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
            return;
        }

        holder.mLoading.setVisibility(View.GONE);
        holder.mProgress.setVisibility(View.VISIBLE);
        holder.mProgress.setIndeterminate(true);
        Ion.with(mContext)
                .load(mDataset.get(position).getAttachment())
                .progressBar(null)
                .progressDialog(null)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        holder.mProgress.setProgress((int) (((double) downloaded / total) * 100));
                    }
                })
                .write(fileSaved)
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (file == null || !file.exists()) {
                            return;
                        }

                        Picasso.with(mContext)
                                .load(file)
                                .placeholder(R.drawable.cap_album)
                                .into(holder.mFileImageView);
                        holder.mProgress.setIndeterminate(false);
                        holder.mProgress.setVisibility(View.GONE);
                    }
                });
    }

    private void downloadOnly(String url, final ViewHolder holder) {
        holder.mVideoFileLabelImageView.setVisibility(View.GONE);
        String fileName = url.split("/")[url.split("/").length - 1];
        File file = new File(Config.SAVE_PATH + fileName);
        if (file.exists()) {
            holder.mVideoFileLabelImageView.setVisibility(View.VISIBLE);
            holder.mLoading.setPercent(100);
            return;
        }

        holder.mLoading.bringToFront();
        Ion.with(mContext)
                .load(url)
                .progressBar(null)
                .progressDialog(null)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        holder.mLoading.setPercent((int) (((double) downloaded / total) * 100));
                    }
                })

                .write(file)
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        holder.mVideoFileLabelImageView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void downloadVideoFile(String url, final ViewHolder holder) {
        String fileName = url.split("/")[url.split("/").length - 1];
        File file = new File(Config.SAVE_PATH + fileName);
        if (file.exists()) {
            holder.mLoading.setPercent(100);
            openFileDialog(file);
            return;
        }

        holder.mLoading.bringToFront();
        Ion.with(mContext)
                .load(url)
                .progressBar(null)
                .progressDialog(null)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        holder.mLoading.setPercent((int) (((double) downloaded / total) * 100));
                    }
                })
                .write(file)
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (file != null && file.exists()) {
                            holder.mVideoFileLabelImageView.bringToFront();
                        }
                    }
                });
    }

    private void openFileDialog(File file) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "video/mpeg");
        mContext.startActivity(intent);
    }

    private void toggleEditModeWithCallback(ViewHolder holder) {
        if (mContext instanceof Listener) {
            ((Listener) mContext).onLongClickTrigger(holder);
        }
    }

    private void editButtonClickEvent(ViewHolder holder) {
        try {
            if (mEditDataset.contains(mDataset.get(holder.getAdapterPosition()))) {
                for (int i = 0; i < mEditDataset.size(); i++) {
                    if (mDataset.get(holder.getAdapterPosition()).getCreated() == mEditDataset.get(i).getCreated()) {
                        mEditDataset.remove(i);
                        break;
                    }
                }
                holder.mEditModeButton.setImageResource(R.drawable.select_dialog_delete_normal);
            } else {
                mEditDataset.add(mDataset.get(holder.getAdapterPosition()));
                holder.mEditModeButton.setImageResource(R.drawable.select_dialog_delete_active);
            }
        } catch (IndexOutOfBoundsException ignored) {

        }
//        notifyItemChanged(holder.getAdapterPosition());
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).getSenderUserId() + mDataset.get(position).getCreated();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public void deletePeriodically() {

        long firstPosition = 0;
        long lastPosition = 0;

        long timeZoneOffset = TimeZone.getDefault().getRawOffset();
        if (TimeZone.getDefault().inDaylightTime(new Date())) {
            timeZoneOffset = timeZoneOffset + TimeZone.getDefault().getDSTSavings();
        }
        long now = System.currentTimeMillis();

        if (mDataset == null) {
            return;
        }
        for (Iterator<ChatMessage> iterator = mDataset.iterator(); iterator.hasNext(); ) {
            ChatMessage item = iterator.next();
            if (item.getDeleted() > 0 && item.getDeleted() < (now + timeZoneOffset)) {
                if (firstPosition == 0) {
                    firstPosition = item.getId();
                }
                lastPosition = item.getId();
                iterator.remove();
            }
        }

        if (firstPosition != 0) {
            notifyItemRangeRemoved((int) firstPosition, (int) lastPosition);
        }
    }

    public void setDataset(List<ChatMessage> dataset) {
        this.mDataset = dataset;
        notifyDataSetChanged();
    }

    public int getItemPosition(ChatMessage message) {
        return mDataset.indexOf(message);
    }

    public interface Listener {
        void onLongClickTrigger(ViewHolder holder);

        void onClickTrigger();

        void onImageClickTrigger(ChatMessage message);

        void onRemoveTrigger();

        void onAddTrigger();

        void onChangeTrigger(ChatMessage chatMessage);

        void onRefreshTrigger(ChatMessage chatMessage, int position);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mAvatarImageView;
        //        private final LinearLayout mAvatarLinearLayout;
        private final TextView mTimeTextView;
        private final ImageView mFileImageView;
        //        private final ImageView mVideoFileImageView;
        private final ImageView mVideoFileLabelImageView;

        private final LinearLayout mEditModeLinearLayout;
        private final ImageButton mEditModeButton;
        private final ImageButton mRetryButton;
        private final LinearLayout mTimeLinearLayout;
        private final LinearLayout mPrivacyLinearLayout;
        private final LinearLayout mAvatarLinearLayout;
        private final RelativeLayout mFilesRelativeLayout;

        // each data item is just a string in this case
        public TextView mTextView;
        public ProgressBar mProgress;
        public CircleLoadingView mLoading;
        public Target mTarget;
        private ChatMessage mMessage;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.chat_item_name);
            mTimeTextView = (TextView) v.findViewById(R.id.list_item_chat_message_time);

            mEditModeLinearLayout = (LinearLayout) v.findViewById(R.id.list_item_chat_edit_layout);
            mEditModeButton = (ImageButton) v.findViewById(R.id.list_item_chat_edit_button);
            mRetryButton = (ImageButton) v.findViewById(R.id.list_item_chat_retry_button);

            mTimeLinearLayout = (LinearLayout) v.findViewById(R.id.list_item_chat_time_layout);
            mPrivacyLinearLayout = (LinearLayout) v.findViewById(R.id.list_item_chat_privacy_layout);

            mProgress = (ProgressBar) v.findViewById(R.id.progress);
            mLoading = (CircleLoadingView) v.findViewById(R.id.loading);
            mLoading.setVisibility(View.GONE);
            mLoading.bringToFront();

            mFileImageView = (ImageView) v.findViewById(R.id.list_item_chat_message_file);
//            mVideoFileImageView = (ImageView) v.findViewById(R.id.list_item_chat_message_video_file);
            mVideoFileLabelImageView = (ImageView) v.findViewById(R.id.list_item_chat_message_video_file_label_imageview);
            mAvatarImageView = (ImageView) v.findViewById(R.id.list_item_chat_avatar_imageview);
            mAvatarLinearLayout = (LinearLayout) v.findViewById(R.id.list_item_chat_avatar_layout);
            mFilesRelativeLayout = (RelativeLayout) v.findViewById(R.id.list_item_chat_message_file_layout);
        }

        public ChatMessage getMessage() {
            return mMessage;
        }

        public void setMessage(ChatMessage message) {
            mMessage = message;
        }
    }

    private class PrivateMode {
        String mName;
        long mDuration;
        boolean mCopy;
//        long mCreated;

        public PrivateMode(String name, long duration, boolean copy, long created) {
            mName = name;
            mDuration = duration;
            mCopy = copy;
//            mCreated = created;
        }

        public String getName() {
            return mName;
        }

        public long getDuration() {
            return mDuration;
        }

        public boolean isCopy() {
            return mCopy;
        }

//        public long getCreated() {
//            return mCreated;
//        }
    }
}

