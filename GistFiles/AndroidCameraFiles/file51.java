package com.candychat.net.activity.timeline;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.candychat.net.WOUApp;
import com.candychat.net.activity.ChatActivity;
import com.candychat.net.activity.main.event.GetRelationDataEvent;
import com.candychat.net.adapter.ExpandableListViewAdapter;
import com.candychat.net.adapter.MemberGroupAdapter;
import com.candychat.net.base.BaseFragment;
import com.candychat.net.event.AddUserEventSuccess;
import com.candychat.net.event.ContentInfoEvent;
import com.candychat.net.event.GetRoomChatEvent;
import com.candychat.net.handler.ApiBus;
import com.candychat.net.manager.PrefManager;
import com.candychat.net.view.RoundedTransformation;
import com.candychat.net.woumodel.Relations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.module.candychat.net.event.ChatInfo;
import com.module.candychat.net.event.GetChatInfoSuccess;
import com.module.candychat.net.model.RelationsGroup;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.wouchat.messenger.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupMemberFragment extends BaseFragment {
    boolean checkData = false;
    ListView listView4;
    private int userId;
    public PrefManager prefManager;
    //private HttpProxyCache proxyCache;
    public static Relations relations;
    private AQuery aq;
    String url;
    MemberGroupAdapter memberGroupAdapter;
    int position;

    public static GroupMemberFragment newInstance(int cid) {
        Bundle args = new Bundle();
        GroupMemberFragment fragment = new GroupMemberFragment();
        args.putInt("CID", cid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getInt("CID", 0) != 0) {
            ApiBus.getInstance().post(new ContentInfoEvent(getArguments().getInt("CID", 0), 1, 100));
        }
        prefManager = WOUApp.get(getActivity()).getPrefManager();
        userId = prefManager.userId().getOr(0);
        Log.e("userId", userId + "");

        if (userId == 0)
            WOUApp.logout(getActivity());
        //setHasOptionsMenu(true);


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_member_fragment, container, false);
        listView4 = (ListView) rootView.findViewById(R.id.listView4);
        aq = new AQuery(getActivity());
        if (!checkData) {
            getRelationsData();
            checkData = true;
        }
        position = getActivity().getIntent().getExtras().getInt("position");
        Log.e("position", position + "");
        fetchContact();
        Toast.makeText(getActivity(), "ssss", Toast.LENGTH_LONG).show();


        return rootView;
    }


    public void fetchContact() {
        url = WOUApp.API_ENDPOINT + "/user/" + userId + "/relations";
        Log.e("aaa", url);
        aq.ajax(url, JSONObject.class, this, "jsonCallback");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getRelationsData() {
        String url = WOUApp.API_ENDPOINT + "/user/" + userId + "/relations";

        Map<String, String> params = new HashMap<>();

        AQuery aq = new AQuery(getContext());
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Gson gson = new GsonBuilder().create();
                relations = gson.fromJson(json.toString(), Relations.class);// obj is your object
                Log.e("kkkkkk", relations.getGroup().size() + "");

            }
        });


    }

    Dialog dialog;

    public void jsonCallback(String url, JSONObject json, AjaxStatus status) {

        if (json != null) {
            Gson gson = new GsonBuilder().create();
            relations = gson.fromJson(json.toString(), Relations.class);// obj is your object
            final ArrayList<Relations.GroupEntity.ConversationMembersEntity> groupList = new ArrayList<>();
            for (int i = 0; i < relations.getGroup().size(); i++) {
                groupList.add(relations.getGroup().get(position).getConversationMembers().get(i));

                Log.e("ccc", relations.getGroup().get(position).getConversationMembers().get(i).getName() + "");

                memberGroupAdapter = new MemberGroupAdapter(getActivity(), groupList);
                listView4.setAdapter(memberGroupAdapter);
                listView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        String imageProfile = WOUApp.SOCIAL_ENDPOINT + "/" + groupList.get(position).getAvatar();
                        String name = groupList.get(position).getName();

                        final int partnerId = relations.getGroup().get(15).getConversationMembers().get(position).getUserId();
                        dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
                        dialog.setContentView(R.layout.dialog_group_member);

                        ImageView cover = (ImageView) dialog.findViewById(R.id.header_imageview);
                        ImageView profile = (ImageView) dialog.findViewById(R.id.profile);
                        TextView names = (TextView) dialog.findViewById(R.id.name);
                        TextView txt_chat = (TextView) dialog.findViewById(R.id.txt_chat);


                        Picasso.with(getActivity())
                                .load(imageProfile)
                                .centerCrop()
                                .resize(200, 200)
                                .transform(new RoundedTransformation(100, 4))
                                .into(profile);

                        names.setText(name);

                        txt_chat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), ChatActivity.class);
                                i.putExtra("USER_ID_1", WOUApp.mPref.userId().getOr(1));
                                i.putExtra("USER_ID_2", partnerId);
                                i.putExtra("CHAT_TYPE", 1);
                                i.putExtra("CONVERSATION_ID", relations.getGroup().get(position).getId());
                                startActivity(i);
                                dialog.hide();
                            }
                        });

                        dialog.show();


                        //ApiBus.getInstance().post(new GetRoomChatEvent(userId, partnerId));
                    }
                });
            }


//            Relations.GroupEntity.ConversationMembersEntity conversationMembersEntity : relations.getGroup().get(0).getConversationMembers();

        }
    }


    @Subscribe
    public void onAddUserEventSuccess(AddUserEventSuccess event) {
        Toast.makeText(getContext(), event.user.getMessage(), Toast.LENGTH_SHORT).show();
        getRelationsData();
    }


    @Subscribe
    public void onGetRelationsData(GetRelationDataEvent event) {
        getRelationsData();
    }

    @Override
    public void onResume() {
        super.onResume();
        getRelationsData();
//        if(toolbar != null) {
//            getActivity().setTitle(Spanny.spanText(getResources().getString(R.string.friends), new CustomTypefaceSpan(WOUApp.CustomFontTypeFace())));
//            toolbar.inflateMenu(R.menu.menu_main_friends);
//        }

    }


}