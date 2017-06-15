package com.mariniero.aga.ui.chats;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geo.dating.R;
import com.mariniero.aga.ui.adapters.MessagesAdapter;
import com.mariniero.aga.ui.comparators.ChatComparator;
import com.mariniero.aga.ui.fragments.MessagesListFragment;
import com.mariniero.aga.ui.photos.BaseUploadPhotoActivity;
import com.mariniero.aga.ui.settings.BlockedContactsActivity;
import com.mariniero.aga.ui.widget.WrapContentLinearLayoutManager;
import com.mariniero.aga.utils.Lists;
import com.mariniero.aga.utils.TimeUtils;
import com.mariniero.aga.utils.UIUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import geo.dating.engine.chat.Chat;
import geo.dating.engine.chat.ChatListener;
import geo.dating.engine.chat.ChatMessage;
import geo.dating.engine.enums.ChatMessageType;
import geo.dating.engine.general.Constants;
import geo.dating.engine.models.ChatUser;
import geo.dating.engine.models.GeoDatingEngine;
import geo.dating.engine.models.User;
import geo.dating.engine.results.ChatMessagesOperationResult;
import geo.dating.engine.results.ChatOperationResult;
import geo.dating.engine.results.OperationResult;
import geo.dating.engine.results.UserOperationResult;

public class ChatActivity extends BaseUploadPhotoActivity implements ChatListener,
        MessagesListFragment.Listener, MessagesAdapter.Listener, BaseUploadPhotoActivity.Callback {

    private static final String LOG_TAG = ChatActivity.class.getSimpleName();
    private static final int SCROLL_UP_OFFSET = -100;
    private static final int SCROLL_UP_OFFSET_PRELOAD = 15;
    private static final long CHAT_MESSAGE_TIMEOUT_DURATION = 3000;
    private EditText mMessageEditText;

    private long mUserId;

    private ChatMessagesOperationResult mOperationResult;

    private List<ChatMessage> mMessages;
    private List<ChatMessage> mMessagesInProgress;
    private List<ChatMessage> mMessagesFailed;

    private MessagesListFragment mMessagesListFragment;
    private RecyclerView mMessagesListFragmentRecyclerView;
    private MessagesAdapter mMessagesListAdapter;

    private WrapContentLinearLayoutManager mLayoutManager;
    private ImageButton mMessageSendButton;
    private Chat mChat;
    private boolean mIsGroupChat;
    private ImageButton mMessageSendAttachmentButton;
    private Resources mResources;
    private boolean mIsOnline;
    private String mTitle;

    private boolean mTimeoutDeletionEnabled;
    private MenuItem mTimeoutDeletionMenuItem;
    private Toolbar mHeaderBar;
    private String mDescription;
    private LinearLayout mAttachmentsLayout;
    private Point mDisplaySize;
    private Button mCameraButton;
    private Button mGalleryButton;
    private Button mVideoButton;
    private Button mVideoGalleryButton;

    private long mDuration;
    private boolean mCopy;

    private LinearLayout mMessageLayout;
    private boolean mIsCopy = false;
    private TextView mTitleTextView;
    private TextView mLastSeenTextview;
    private ImageView mOnlineImageView;

    private boolean mBlockedUsersAlertDialogShown;
    private boolean mStopScrollOnResume;
    private boolean mUserBlockedFrom;
    private ChatOperationResult mChatOperationResult;
    private User mUser;
    private boolean mNoHistoryToLoad = false;
    private boolean mResetToList = false;
    private long mOwnUserId;
    private ProgressBar mProgressBarHorizontal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSuppressKeyboardHiding();

        mUIHandler = new Handler();
        mContext = this;
        mResources = getResources();
        measureDisplay();

        Intent intent = getIntent();
        mAlbumId = -1;
        mBlockedUsersAlertDialogShown = false;
        mChatId = intent.getLongExtra("chatId", -1);
        mUserId = intent.getLongExtra("userId", -1);
        mTitle = intent.getStringExtra("name");
        mDescription = intent.getStringExtra("description");
        mIsGroupChat = intent.getBooleanExtra("is_group", false);
        mIsOnline = intent.getBooleanExtra("online", false);
        mOwnUserId = Long.valueOf(GeoDatingEngine.getInstance().getCurrentUserNumber());
        mDuration = intent.getLongExtra("duration", 0L);
        mCopy = intent.getBooleanExtra("copy", false);

        mTimeoutDeletionEnabled = intent.getBooleanExtra("is_private_enabled", false);

        mHeaderBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        actionBarColorByTimeoutDeletion();

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationIcon(R.drawable.navi_icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mOnlineImageView = (ImageView) toolbar.findViewById(R.id.toolbar_actionbar_online_imageview);
        mTitleTextView = (TextView) toolbar.findViewById(R.id.toolbar_actionbar_title);
        mLastSeenTextview = (TextView) toolbar.findViewById(R.id.toolbar_actionbar_online_title);
        mLastSeenTextview.setVisibility(mIsGroupChat ? View.INVISIBLE : View.VISIBLE);
        setToolbarValues();
        getSupportActionBar().setTitle("");

        mMessagesListAdapter = new MessagesAdapter(mMessages, mContext);
        mMessagesListAdapter.setHasStableIds(true);

        getFragmentManager().beginTransaction()
                .add(R.id.messages_list_container, new MessagesListFragment())
                .commit();

        mMessagesInProgress = Lists.newArrayList();
        mMessagesFailed = Lists.newArrayList();
        mMessagesListAdapter = new MessagesAdapter(mMessages, mContext);
        mMessagesListAdapter.setHasStableIds(true);
    }

    @Override
    protected String getPageTitleForAnalytics() {
        Log.i(LOG_TAG, "Analytics title is: " + getResources().getString(R.string.title_activity_chat_ga));
        return getResources().getString(R.string.title_activity_chat_ga);
    }

    private void setToolbarValues() {
        if (mTitleTextView != null) {
            if (mChat == null) {
//                mTitleTextView.setText(mTitle + (!getIntent().hasExtra("qty") ? "" : " " + getIntent().getStringExtra("qty")));
                mTitleTextView.setText(mTitle);
            } else {
                mTitleTextView.setText(mTitle + (mIsGroupChat ? " " + getResources().getString(R.string.title_activity_group_users).replace("qty", String.valueOf(mChat.getUserList().size())) : ""));
            }
        }

        if (mOnlineImageView == null) {
            return;
        }

        mOnlineImageView.setVisibility(mIsGroupChat ? View.GONE : View.VISIBLE);
        if (!mIsGroupChat) {
            mOnlineImageView.setImageResource(mIsOnline ? R.drawable.online : R.drawable.offline);
        }
    }

    @Override
    public void onBackPressed() {
        if (mMessagesListAdapter.isInEditMode()) {
            mMessagesListAdapter.toggleEditMode(false);
            invalidateOptionsMenu();
        } else {
            GeoDatingEngine.getInstance().endChat();
            if (mResetToList) {
                setResult(RESULT_OK);
                finish();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        GeoDatingEngine.getInstance().endChat();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initKeyboardListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(mMessagesListAdapter != null && mMessagesListAdapter.isInEditMode() ?
                R.menu.menu_chat_messages :
                (mIsGroupChat ? R.menu.menu_chat_view : R.menu.menu_chat_private_view), menu);

        mTimeoutDeletionMenuItem = menu.findItem(R.id.action_messages_private_toggle);
        if (mTimeoutDeletionMenuItem != null) {
            mTimeoutDeletionMenuItem.setIcon(mTimeoutDeletionEnabled ?
                    R.drawable.navi_icon_lock : R.drawable.navi_icon_unlock);
        }

        if (mMessageLayout != null) {
            mMessageLayout.setVisibility(mMessagesListAdapter != null && mMessagesListAdapter
                    .isInEditMode() ? View.GONE : View.VISIBLE);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                if (mIsGroupChat) {
                    openGroupChatSettings();
                } else {
                    openChatSettings();
                }

                return true;
            case R.id.action_messages_private_toggle:
                if (mIsGroupChat) {
                    return true;
                }

                toggleTimeoutDeletion();
                return true;
            case R.id.action_messages_pick_all:
                if (mMessagesListAdapter.isInEditMode()) {
                    mMessagesListAdapter.pickAllEditMode();
                }
                return true;
            case R.id.action_messages_delete:
                if (mMessagesListAdapter.isInEditMode()) {
                    mMessagesListAdapter.bulkRemovePicked();
                    mMessagesListAdapter.toggleEditMode(false);
                    invalidateOptionsMenu();
                }
                return true;
            default:
                return true;
        }
    }

    private void toggleTimeoutDeletion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final UserOperationResult userResult = GeoDatingEngine.getInstance().getUser(mUserId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (failCheckOperationResult(userResult) || userResult.getUser() == null) {
                            return;
                        }

                        User user = userResult.getUser();
                        mIsOnline = user.isOnline();
                        mTitle = user.getFirstName();
                        setToolbarValues();

                        if (!mIsOnline && mDuration <= 0) {
                            new AlertDialog.Builder(mContext)
                                    .setTitle(R.string.alert_dialog_timeout_deletion_mode_title)
                                    .setMessage(mContext.getResources()
                                            .getString(R.string.alert_dialog_timeout_deletion_mode_message))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    })
                                    .show();
                        } else if (!mTimeoutDeletionEnabled) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    GeoDatingEngine.getInstance().sendRequestPrivateEnable(mChatId);
                                }
                            }).start();
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    GeoDatingEngine.getInstance().sendRequestPrivateDisable(mChatId);
                                }
                            }).start();
                        }
                    }
                });
            }
        }).start();
    }

    private void actionBarColorByTimeoutDeletion() {
        if (mHeaderBar != null) {
            mHeaderBar.setBackgroundResource(mTimeoutDeletionEnabled ?
                    R.color.theme_accent_pink : R.color.theme_primary_dark);
            mHeaderBar.invalidate();
        }
    }

    @Override
    public void onLongClickTrigger(final MessagesAdapter.ViewHolder holder) {
        if (mTimeoutDeletionEnabled && mIsCopy) {
            toggleEditMode();
        } else {
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.dialog_chat_action_pick_action)
                    .setItems(new CharSequence[]{
                            mResources.getString(R.string.message_item_action_copy),
                            mResources.getString(R.string.message_item_action_edit),
                            mResources.getString(R.string.photo_item_action_close_dialog)
                    }, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    if (holder.getMessage().getType() == ChatMessageType.TEXT) {
                                        copyMessageText(holder.getMessage().getMessage());
                                    }
                                    break;
                                case 1:
                                    toggleEditMode();
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .show();
        }
    }

    private void copyMessageText(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("message text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, getResources().getString(R.string.message_item_action_copy_success), Toast.LENGTH_SHORT).show();
    }

    private void toggleEditMode() {
        mMessagesListAdapter.toggleEditMode(!mMessagesListAdapter.isInEditMode());
        invalidateOptionsMenu();
        hideSoftKeyboard();
    }

    @Override
    public void onClickTrigger() {

    }

    @Override
    public void onImageClickTrigger(ChatMessage message) {
        openAttachmentView(message);
    }

    private void openAttachmentView(ChatMessage message) {
        Bundle bundle = new Bundle();
        bundle.putString("photo", message.getFirstPhoto() != null ?
                message.getFirstPhoto().getUrl() : null);
        Intent intent = new Intent(mContext, ChatAttachmentViewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRemoveTrigger() {
        final List<ChatMessage> editList = mMessagesListAdapter.getEditDataset();
        if (editList.size() != 0 && mChatId > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<Long> list = new ArrayList<>();
                        for (ChatMessage message : editList) {
                            list.add(message.getId());
                        }

                        final OperationResult result = GeoDatingEngine.getInstance()
                                .deleteChatMessage(mChatId, list);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (failCheckOperationResult(result)) {
                                    return;
                                }

                                mMessagesListAdapter.resetEditDataset();
                            }
                        });
                    } catch (IllegalStateException ignored) {

                    }
                }
            }).start();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mStopScrollOnResume = true;
        requestDataRefresh();
        GeoDatingEngine.getInstance().endChat();
        if (mChatId <= 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ChatOperationResult operationResult = GeoDatingEngine.getInstance()
                            .createChat("", "", Constants.ChatGroupType.INDIVIDUAL, mUserId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (failCheckOperationResult(operationResult) || operationResult.getChat() == null) {
                                finish();
                                return;
                            }

                            mChat = operationResult.getChat();
                            mChatId = mChat.getId();
                            if (!mIsGroupChat) {
                                long currentUserId = Long.valueOf(GeoDatingEngine.getInstance().getCurrentUserNumber());
                                for (ChatUser user : mChat.getUserList()) {
                                    if (user.getId() != currentUserId) {
                                        mLastSeenTextview.setText(TimeUtils.formatHumanFriendlyDateForOnline(mContext, user.getLastTimePresence(), user.getSex()));
                                        mLastSeenTextview.setVisibility(user.isOnline() ? View.INVISIBLE : View.VISIBLE);
                                        break;
                                    }
                                }
                            } else {
                                setToolbarValues();
                            }
                            GeoDatingEngine.getInstance().startChat(mChatId, mUserId, (ChatListener) mContext);
                        }
                    });
                }
            }).start();
        } else {
            GeoDatingEngine.getInstance().startChat(mChatId, mUserId, this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ChatOperationResult operationResult = GeoDatingEngine.getInstance().getChat(mChatId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (failCheckOperationResult(operationResult) || operationResult.getChat() == null) {
                                finish();
                                return;
                            }

                            mChat = operationResult.getChat();
                            mChatId = mChat.getId();
                            if (!mIsGroupChat) {
                                long currentUserId = Long.valueOf(GeoDatingEngine.getInstance().getCurrentUserNumber());
                                for (ChatUser user : mChat.getUserList()) {
                                    if (user.getId() != currentUserId) {
                                        mLastSeenTextview.setText(TimeUtils.formatHumanFriendlyDateForOnline(mContext, user.getLastTimePresence(), user.getSex()));
                                        mLastSeenTextview.setVisibility(user.isOnline() ? View.INVISIBLE : View.VISIBLE);
                                        mIsOnline = user.isOnline();
                                        setToolbarValues();
                                        break;
                                    }
                                }
                            } else {
                                setToolbarValues();
                            }
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onAddTrigger() {
        mLayoutManager.scrollToPosition(mMessagesListAdapter.getItemCount() - 1);
    }

    private void openGroupChatSettings() {
        Intent intent = new Intent(mContext, ChatSettingsGroupActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Bundle userBundle = new Bundle();
        userBundle.putLong("id", mChatId);
        userBundle.putLong("userId", 0L);
        userBundle.putLong("chatId", mChatId);
        userBundle.putString("name", mTitle);
        userBundle.putString("description", mDescription);
        if (mChat != null) {
            userBundle.putBoolean("sound", mChat.isSound());
            userBundle.putBoolean("is_open", mChat.isOpen());
            userBundle.putBoolean("is_admin", mChat.isAdmin());
            userBundle.putString("photo_url", mChat.getThumbPhoto() != null ?
                    mChat.getThumbPhoto().getUrl() : null);
        }

        intent.putExtras(userBundle);
        startActivityForResult(intent, CHAT_SETTINGS_ADD_USER_RESULT);
    }

    private void openChatSettings() {
        Intent intent = new Intent(mContext, ChatSettingsPrivateActivity.class);
        Bundle userBundle = new Bundle();
        userBundle.putLong("chatId", mChatId);
        userBundle.putLong("userId", mUserId);
        userBundle.putInt("duration", (int) mDuration);
        userBundle.putBoolean("copy", mIsCopy);
        intent.putExtras(userBundle);
        startActivityForResult(intent, CHAT_SETTINGS_ADD_USER_RESULT);
    }

    @Override
    protected void uploadPhoto() {}

    protected void uploadVideo() {
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setCreated((System.currentTimeMillis() + TimeUtils.getCurrentTimestampOffset()) / 1000);
        chatMessage.setClientCreated(chatMessage.getCreated());
        chatMessage.setType(ChatMessageType.VIDEO);
        chatMessage.setOwn(true);
        chatMessage.setMessage(mSelectedImageFile.getAbsolutePath());
        chatMessage.setSenderUserId(mOwnUserId);
        chatMessage.setTimeoutRunnable(new Runnable() {
            @Override
            public void run() {
                mMessagesInProgress.remove(chatMessage);
                mMessagesFailed.add(chatMessage);
                if (mMessagesInProgress.size() == 0 && mProgressBarHorizontal != null) {
                    mProgressBarHorizontal.setVisibility(View.GONE);
                }
                chatMessage.setStatus(ChatMessage.Status.FAILED);
                mMessagesListAdapter.setItem(mMessagesListAdapter.getItemPosition(chatMessage), chatMessage);
            }
        });
        mHandler.postDelayed(chatMessage.getTimeoutRunnable(), CHAT_MESSAGE_TIMEOUT_DURATION);
        mMessagesInProgress.add(chatMessage);
        if (mMessagesInProgress.size() > 0 && mProgressBarHorizontal != null) {
            mProgressBarHorizontal.setVisibility(View.VISIBLE);
            mProgressBarHorizontal.bringToFront();
        }

        mMessagesListAdapter.addItem(chatMessage);
        upload();
    }

    protected void upload() {
        if (mSelectedImageFile == null) {
            return;
        }

        showProgress(true);
        mMessageSendAttachmentButton.setClickable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OperationResult chatFileUpload = GeoDatingEngine.getInstance()
                        .createChatFileMessage(mSelectedImageFile.getAbsolutePath(), mChatId, ChatMessageType.VIDEO);
                if (chatFileUpload != null && chatFileUpload.isSuccess()) {
                    GeoDatingEngine.getInstance().sendRequestFile(mChatId, ChatMessageType.VIDEO);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            onRefreshingStateChanged(false);
                            mMessageSendAttachmentButton.setClickable(true);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void requestDataRefresh() {
        super.requestDataRefresh();
        if (mChatId <= 0L) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                mChatOperationResult = GeoDatingEngine.getInstance().getChat(mChatId);
                mOperationResult = GeoDatingEngine.getInstance().getChatMessages(mChatId, mUserId, 0, 20000);
                final long currentId = Long.valueOf(GeoDatingEngine.getInstance().getCurrentUserNumber());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (failCheckOperationResult(mOperationResult) && failCheckOperationResult(mChatOperationResult)) {
                            return;
                        }

                        mChat = mChatOperationResult.getChat();
                        if (mChat == null) {
                            new AlertDialog.Builder(mContext)
                                    .setMessage(R.string.alert_dialog_group_non_existent)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                            return;
                        }

                        if (mOperationResult.isSuccess() && mMessagesListFragment != null) {
                            /** Individual chat blockade check **/
                            if (mChat.getUserList().size() == 2) {
                                for (ChatUser user : mChat.getUserList()) {
                                    if (user.getId() != currentId) {
                                        if (user.isBlockedTo()) {
                                            /** User have blocked you **/
                                            mMessageEditText.setText(R.string.chat_user_blocked_from);
                                            UIUtils.disableEditText(mMessageEditText);
                                            mMessageSendAttachmentButton.setVisibility(View.INVISIBLE);
                                            mMessageSendButton.setVisibility(View.INVISIBLE);
                                        }

                                        /** You've blocked the user **/
                                        mUserBlockedFrom = user.isBlockedFrom();
                                    }
                                }

                                if (!mUserBlockedFrom) {
                                    //TODO: WHO'VE BLOCKED THE CHAT ? // server api
                                    if (mChat.isBlocked()) {
                                        mMessageEditText.setText(R.string.chat_user_blocked_from);
                                        UIUtils.disableEditText(mMessageEditText);
                                        mMessageSendAttachmentButton.setVisibility(View.INVISIBLE);
                                        mMessageSendButton.setVisibility(View.INVISIBLE);
                                        mUserBlockedFrom = true;
                                    } else {
                                        if (mMessageEditText.getText()!= null && mMessageEditText.getText().toString() == getResources().getString(R.string.chat_user_blocked_from)) {
                                            mMessageEditText.setText("");
                                        }
                                        UIUtils.enableEditText(mMessageEditText);
                                        mMessageSendAttachmentButton.setVisibility(View.VISIBLE);
                                        mMessageSendButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                                for (ChatUser user : mChat.getUserList()) {
                                    if (user.getId() != currentId && user.isBlockedFrom() && !mBlockedUsersAlertDialogShown) {
                                        /** You've blocked the user **/
                                        new AlertDialog.Builder(mContext)
                                                .setMessage(R.string.chat_user_blocked_in_group_chat)
                                                .setPositiveButton(R.string.chat_user_blocked_in_group_chat_ok_button, null)
                                                .setNegativeButton(R.string.chat_user_blocked_in_group_chat_cancel_button, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        deleteGroupChat();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();

                                        mBlockedUsersAlertDialogShown = true;
                                    }
                                }
                            }

                            mIsCopy = mChat.isPrivateCopy();
                            mDuration = mChat.getPrivateDuration();
                            mTimeoutDeletionEnabled = mChat.getPrivateStatus();
                            actionBarColorByTimeoutDeletion();
                            invalidateOptionsMenu();

                            mMessages = mOperationResult.getChatMessageList();
                            if (mMessagesInProgress.size() != 0) {
                                mMessages.addAll(mMessagesInProgress);
                            }
                            if (mMessagesFailed.size() != 0) {
                                mMessages.addAll(mMessagesFailed);
                            }

                            if (mIsGroupChat) {
                                mDescription = mChat.getDescription();
                                mTitle = mChat.getName();
                            }
                            if (mMessages != null) {
                                Collections.sort(mMessages, ChatComparator.MESSAGE_DATE_COMPARATOR);
                            }
                            mMessagesListAdapter.setDataset(mMessages);
                            if (!mStopScrollOnResume) {
                                mLayoutManager.scrollToPosition(mMessagesListAdapter.getItemCount() - 1);
                            }
                            mStopScrollOnResume = false;
                            setRefreshing(false);
                            onRefreshingStateChanged(false);
                        }
                    }
                });
            }
        }).start();
    }

    private void deleteGroupChat() {
        setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OperationResult operationResult = GeoDatingEngine.getInstance().deleteChat(mChatId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (failCheckOperationResult(operationResult)) {
                            return;
                        }

                        if (operationResult.isSuccess()) {
                            onRefreshingStateChanged(false);
                            finish();
                        }

                        setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onMessagesListFragmentViewCreated(Fragment fragment) {
        mMessagesListFragment = (MessagesListFragment) fragment;
        mMessagesListFragmentRecyclerView = (RecyclerView) mMessagesListFragment.getView()
                .findViewById(R.id.recycler);

        mProgressBarHorizontal = (ProgressBar) mMessagesListFragment.getView().findViewById(R.id.progressBar);
        mMessagesListFragmentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < SCROLL_UP_OFFSET) {
                    hideSoftKeyboard();
                    if (mAttachmentsLayout.getAlpha() != .0f) {
                        hideAttachmentLayoutWithAnimation();
                    }
                }

                if (!mNoHistoryToLoad && mLayoutManager.findFirstVisibleItemPosition() <= SCROLL_UP_OFFSET_PRELOAD &&
                        dy < 0 && !isProgressShowing()) {
//                    showProgress(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final ChatMessagesOperationResult operationResult = GeoDatingEngine
                                    .getInstance().getChatMessages(mChatId, mUserId,
                                            mMessagesListAdapter.getItemCount(), 50);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (failCheckOperationResult(operationResult)) {
                                        return;
                                    }

                                    if (!operationResult.isSuccess() || operationResult.getChatMessageList() == null) {
                                        return;
                                    }

                                    if (operationResult.getChatMessageList().size() > 0) {
                                        mMessagesListAdapter.addList(operationResult.getChatMessageList());
                                    } else {
                                        mNoHistoryToLoad = true;
                                    }
//                                    showProgress(false);
                                }
                            });
                        }
                    }).start();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mLayoutManager = new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        mMessagesListFragmentRecyclerView.setLayoutManager(mLayoutManager);
        mMessagesListFragmentRecyclerView.setHasFixedSize(true);
        mMessagesListFragmentRecyclerView.setAdapter(mMessagesListAdapter);

        mMessageLayout = (LinearLayout) mMessagesListFragment.getView().findViewById(R.id.message_layout);
        mMessageEditText = (EditText) mMessagesListFragment.getView().findViewById(R.id.message);
        mMessageSendAttachmentButton = (ImageButton) mMessagesListFragment.getView()
                .findViewById(R.id.messages_send_attachment_button);

        mMessageSendAttachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAttachmentsLayout();
            }
        });

        mAttachmentsLayout = (LinearLayout) mMessagesListFragment.getView()
                .findViewById(R.id.messages_attachments_layout);
        mAttachmentsLayout.measure(mAttachmentsLayout.getWidth(), mAttachmentsLayout.getHeight());
        mAttachmentsLayout.setAlpha(.0f);
        mAttachmentsLayout.setY(mDisplaySize.y);
        mMessagesListFragment.getView().invalidate();

        mCameraButton = (Button) mMessagesListFragment.getView()
                .findViewById(R.id.messages_camera_button);
        mGalleryButton = (Button) mMessagesListFragment.getView()
                .findViewById(R.id.messages_gallery_button);
        mVideoButton = (Button) mMessagesListFragment.getView()
                .findViewById(R.id.messages_video_camera_button);
        mVideoGalleryButton = (Button) mMessagesListFragment.getView()
                .findViewById(R.id.messages_video_gallery_button);

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryIntent();
            }
        });
        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoCameraIntent();
            }
        });
        mVideoGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoGalleryIntent();
            }
        });

        mMessageSendButton = (ImageButton) mMessagesListFragment.getView()
                .findViewById(R.id.messages_send_button);
        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUserBlockedFrom) {
                    /** You've blocked the user **/
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.blocked_contact_unlock_button_title)
                            .setMessage(mContext.getResources()
                                    .getString(R.string.chat_user_blocked_to))
                            .setPositiveButton(android.R.string.no, null)
                            .setNegativeButton(R.string.blocked_contact_unlock_button_title, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    startActivity(new Intent(mContext, BlockedContactsActivity.class));
                                }
                            })
                            .show();
                    return;
                }
                String message = mMessageEditText.getText().toString().replaceAll("\\n+", "\n");
                if (message.isEmpty()) {
                    return;
                }

                mMessageSendButton.setClickable(false);

                final ChatMessage chatMessage = new ChatMessage();
                chatMessage.setCreated((System.currentTimeMillis() + TimeUtils.getCurrentTimestampOffset()) / 1000);
                chatMessage.setClientCreated(chatMessage.getCreated());
                chatMessage.setType(ChatMessageType.TEXT);
                chatMessage.setOwn(true);
                chatMessage.setMessage(message);
                chatMessage.setSenderUserId(mOwnUserId);
                chatMessage.setTimeoutRunnable(new Runnable() {
                    @Override
                    public void run() {
                        mMessagesInProgress.remove(chatMessage);
                        mMessagesFailed.add(chatMessage);
                        if (mMessagesInProgress.size() == 0 && mProgressBarHorizontal != null) {
                            mProgressBarHorizontal.setVisibility(View.GONE);
                        }
                        chatMessage.setStatus(ChatMessage.Status.FAILED);
                        mMessagesListAdapter.setItem(mMessagesListAdapter.getItemPosition(chatMessage), chatMessage);
                    }
                });
                mHandler.postDelayed(chatMessage.getTimeoutRunnable(), CHAT_MESSAGE_TIMEOUT_DURATION);

                mMessagesInProgress.add(chatMessage);
                if (mMessagesInProgress.size() > 0 && mProgressBarHorizontal != null) {
                    mProgressBarHorizontal.setVisibility(View.VISIBLE);
                    mProgressBarHorizontal.bringToFront();
                }

                mMessagesListAdapter.addItem(chatMessage);

                GeoDatingEngine.getInstance().sendChatMessage(message);
                mMessageEditText.setText("");
                mMessageSendButton.setClickable(true);
            }
        });
        requestDataRefresh();
    }

    @Override
    public void onRefreshTrigger(ChatMessage chatMessage, int position) {
        mMessagesInProgress.add(chatMessage);
        mMessagesFailed.remove(chatMessage);
        if (mMessagesInProgress.size() == 0 && mProgressBarHorizontal != null) {
            mProgressBarHorizontal.setVisibility(View.VISIBLE);
        } else if (mMessagesInProgress.size() > 0 && mProgressBarHorizontal != null) {
            mProgressBarHorizontal.setVisibility(View.VISIBLE);
            mProgressBarHorizontal.bringToFront();
        }

        chatMessage.setStatus(ChatMessage.Status.PENDING);
        mMessagesListAdapter.setItem(position, chatMessage);
        mHandler.postDelayed(chatMessage.getTimeoutRunnable(), CHAT_MESSAGE_TIMEOUT_DURATION);
        switch (chatMessage.getType()) {
            case ChatMessageType.TEXT: {
                GeoDatingEngine.getInstance().sendChatMessage(chatMessage.getMessage());
            }
            break;
            case ChatMessageType.IMAGE: {

            }
            break;
            case ChatMessageType.VIDEO: {
                mSelectedImageFile = new File(chatMessage.getMessage());
                upload();
            }
            break;
        }
    }

    @Override
    public void onChangeTrigger(ChatMessage chatMessage) {
        mHandler.removeCallbacks(chatMessage.getTimeoutRunnable());
        mMessagesInProgress.remove(chatMessage);
        if (mMessagesInProgress.size() == 0 && mProgressBarHorizontal != null) {
            mProgressBarHorizontal.setVisibility(View.GONE);
        }
    }

    protected void openVideoCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        mSelectedImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                String.format("aga_video_%d.mp4", System.currentTimeMillis()));
        if (!Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mSelectedImageFile));
        }

        startActivityForResult(intent, RESULT_PHOTO_CAMERA_VIDEO);
    }

    protected void openVideoGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Выберите видео"),
                RESULT_PHOTO_ALBUM_VIDEO);
    }

    protected void toggleAttachmentsLayout() {
        if (mAttachmentsLayout.getAlpha() == .0f) {
            mMessageSendAttachmentButton.setImageResource(R.drawable.icon_clouse_add_file_dialog);
            hideSoftKeyboard();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAttachmentsLayout.animate()
                            .alpha(1.0f)
                            .translationY(.0f)
                            .withStartAction(new Runnable() {
                                @Override
                                public void run() {
                                    mAttachmentsLayout.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }, 300);
        } else {
            hideAttachmentLayoutWithAnimation();
        }
    }

    private void hideAttachmentLayoutWithAnimation() {
        mMessageSendAttachmentButton.setImageResource(R.drawable.icon_add_file);
        mAttachmentsLayout.animate()
                .alpha(.0f)
                .translationY(mDisplaySize.y)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mAttachmentsLayout.setVisibility(View.GONE);
                    }
                });
    }

    private void measureDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        mDisplaySize = new Point();
        display.getSize(mDisplaySize);
    }

    @Override
    protected void onGlobalLayoutChanged(boolean keyboardActive) {
        if (mAttachmentsLayout.getAlpha() != .0f) {
            mAttachmentsLayout.setVisibility(View.GONE);
            mAttachmentsLayout.setAlpha(.0f);
            mAttachmentsLayout.setY(mDisplaySize.y);
            mMessageSendAttachmentButton.setImageResource(R.drawable.icon_add_file);
        }

        super.onGlobalLayoutChanged(keyboardActive);
        if (keyboardActive && mLayoutManager.findLastCompletelyVisibleItemPosition() !=
                mMessagesListAdapter.getItemCount() - 1) {
            mMessagesListAdapter.notifyDataSetChanged();
        }
    }

    protected String getImagePath(Uri uri) {
        String path = null;

        Cursor cursor = getContentResolver().query(uri, new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE}, null, null, null);
        if (cursor.moveToFirst()) {
            mPhotoId = cursor.getLong(0);
            path = cursor.getString(1);
        }
        cursor.close();

        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_PHOTO_CAMERA_VIDEO: {
                if (resultCode != RESULT_CANCELED) {
                    if (mSelectedImageFile == null && data != null) {
                        mSelectedImageFile = new File(getImagePath(data.getData()));
                    } else if (mSelectedImageFile == null && (data == null || data.getData() == null)) {
                        return;
                    }

                    uploadVideo();
                    Log.v(LOG_TAG, mSelectedImageFile != null ? mSelectedImageFile.getAbsolutePath() : "EMPTY");
                }
            }
            break;
            case RESULT_PHOTO_ALBUM_VIDEO: {
                if (resultCode != RESULT_CANCELED) {
                    if (data == null || data.getData() == null) {
                        return;
                    }

                    mSelectedImageFile = new File(getImagePath(data.getData()));
                    uploadVideo();
                    Log.v(LOG_TAG, mSelectedImageFile != null ? mSelectedImageFile.getAbsolutePath() : "EMPTY");
                }
            }
            break;
            case CHAT_SETTINGS_ADD_USER_RESULT: {
                if (resultCode == RESULT_FIRST_USER && data.hasExtra("duration") && data.hasExtra("copy")) {
                    mDuration = data.getIntExtra("duration", -1);
                    mCopy = data.getBooleanExtra("copy", true);
                    if (mTimeoutDeletionEnabled) {
                        changePrivateSetting(GeoDatingEngine.getInstance().getCurrentUserNumber(), mDuration, mCopy, 0);
                    }
                }

                if (resultCode != RESULT_OK) {
                    return;
                }

                mTitle = data.getStringExtra("name");
                mDescription = data.getStringExtra("description");
                mIsOnline = data.getBooleanExtra("online", false);
                mChatId = data.getLongExtra("chatId", 0L);
                mIsGroupChat = data.getBooleanExtra("group", true);
                mResetToList = data.getBooleanExtra("reset_to_private_list", false);
                setToolbarValues();
                invalidateOptionsMenu();
                requestDataRefresh();
            }
            break;
        }
    }

    @Override
    public void uploadCallback() {
    }

    @Override
    public void photoSettingCallback() {
    }

    @Override
    public void activityResultAction() {
        if (mSelectedImageFile != null) {
            Log.v(LOG_TAG, mSelectedImageFile.getAbsolutePath());
            uploadPhoto();
        }
    }

    @Override
    public void onMessagesListFragmentAttached(MessagesListFragment fragment) {

    }

    @Override
    public void onMessagesListFragmentDetached(MessagesListFragment fragment) {
        mMessagesListFragment = null;
    }

    @Override
    public void receiveMessage(ChatMessage chatMessage) {
        if (mChat != null && mChat.isNotification() && !chatMessage.isOwn()) {
            Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(mContext, defaultRingtoneUri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                mediaPlayer.start();
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (SecurityException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (IllegalStateException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
            mMessagesListAdapter.addItem(chatMessage);
//            mLayoutManager.scrollToPosition(mMessagesListAdapter.getItemCount() - 1);
        } else if (chatMessage.isOwn()) {
            mMessagesListAdapter.changeItem(chatMessage);
        }
    }

    @Override
    public void beginPrivateMode(final String userNumber, final long duration, final boolean copy, final long created) {
        mTimeoutDeletionEnabled = true;
        actionBarColorByTimeoutDeletion();
        invalidateOptionsMenu();
        mIsCopy = copy;
        mDuration = duration;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final UserOperationResult operationResult = GeoDatingEngine.getInstance()
                        .getUser(Long.valueOf(userNumber));
                if (operationResult == null || operationResult.getUser() == null) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (failCheckOperationResult(operationResult)) {
                            return;
                        }

                        final User user = operationResult.getUser();
                        if (mMessagesListAdapter == null) {
                            mUIHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mMessagesListAdapter.privateModeOn(user.getFirstName(), duration, copy, created);
                                }
                            }, 1500);
                        } else {
                            mMessagesListAdapter.privateModeOn(user.getFirstName(), duration, copy, created);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void endPrivateMode(long created) {
        mTimeoutDeletionEnabled = false;
        actionBarColorByTimeoutDeletion();
        invalidateOptionsMenu();
        mIsCopy = true;
        mDuration = 0;
        mMessagesListAdapter.privateModeOff(created);
    }

    @Override
    public void deleteMessagesPrivateMode() {
        mMessagesListAdapter.deletePeriodically();
    }

    @Override
    public void chatError(int errorCode) {
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }





    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    @Override
    public void changeType(int type, long created) {
        mChat.setIsOpen(type);
        mMessagesListAdapter.chatTypeChanged(type);
    }

    @Override
    public void changePrivateSetting(final String userNumber, final long duration, final boolean copy, final long created) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final UserOperationResult operationResult = GeoDatingEngine.getInstance()
                        .getUser(Long.valueOf(userNumber));
                final ChatOperationResult chatOperationResult = GeoDatingEngine.getInstance()
                        .getChat(mChatId);
                if (chatOperationResult != null) {
                    mChat = chatOperationResult.getChat();
                }

                mCopy = mChat.isPrivateCopy();
                mDuration = mChat.getPrivateDuration();
                mTimeoutDeletionEnabled = mChat.getPrivateStatus();
                if (operationResult == null || operationResult.getUser() == null) {
                    return;
                }
                mIsOnline = operationResult.getUser().isOnline();
                if (!mIsOnline || !mTimeoutDeletionEnabled) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (failCheckOperationResult(operationResult)) {
                            return;
                        }

                        final User user = operationResult.getUser();
                        if (mMessagesListAdapter == null) {
                            mUIHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mMessagesListAdapter.privateModeOn(user.getFirstName(), duration, copy, created);
                                }
                            }, 1500);
                        } else {
                            mMessagesListAdapter.privateModeOn(user.getFirstName(), duration, copy, created);
                        }
                    }
                });
            }
        }).start();
    }

}
