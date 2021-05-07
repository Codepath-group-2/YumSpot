package com.codepath.yjoh.yumspot.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.yjoh.yumspot.ChatAdapter;
import com.codepath.yjoh.yumspot.Message;
import com.codepath.yjoh.yumspot.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ChatActivity extends Fragment {

    static final String TAG = ChatActivity.class.getSimpleName();
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    EditText etMessage;
    Button btSend;

    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;


    public ChatActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_chat, container, false);
    }

    // Create a handler which can run code periodically
//    static final int POLL_INTERVAL = 1000; // milliseconds
//    Handler myHandler = new Handler();  // android.os.Handler
//    Runnable mRefreshMessagesRunnable = new Runnable() {
//        @Override
//        public void run() {
//            refreshMessages();
//            myHandler.postDelayed(this, POLL_INTERVAL);
//        }
//    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        btSend = (Button) view.findViewById(R.id.btSend);
        rvChat = (RecyclerView) view.findViewById(R.id.rvChat);



        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }

        mMessages = new ArrayList<>();
//        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(getActivity(), userId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);

        Message message = new Message();
        message.setUserId(ParseUser.getCurrentUser().getObjectId());


        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        parseQuery.orderByDescending("createdAt");
        parseQuery.findInBackground(new FindCallback<Message>() {
                                        public void done(List<Message> messages, ParseException e) {
                                            if (e == null) {
                                                mMessages.clear();
                                                mMessages.addAll(messages);
                                                mAdapter.notifyDataSetChanged(); // update adapter
                                                rvChat.scrollToPosition(0);
                                            }
                                        }
                                    });



        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new

                SubscriptionHandling.HandleEventCallback<Message>() {

                    @Override

                    public void onEvent(ParseQuery<Message> query, Message object) {

                        mMessages.add(0, object);

                        // RecyclerView updates need to be run on the UI thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                rvChat.scrollToPosition(0);
                            }
                        });
                    }
                });
        // User login
    }

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }


    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    // Setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // Find the text field and button
//        etMessage = (EditText) view.findViewById(R.id.etMessage);
//        btSend = (Button) view.findViewById(R.id.btSend);
//        rvChat = (RecyclerView) view.findViewById(R.id.rvChat);
//        mMessages = new ArrayList<>();
////        mFirstLoad = true;
//        final String userId = ParseUser.getCurrentUser().getObjectId();
//        mAdapter = new ChatAdapter(getActivity(), userId, mMessages);
//        rvChat.setAdapter(mAdapter);
//
//        // associate the LayoutManager with the RecylcerView
//        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        rvChat.setLayoutManager(linearLayoutManager);

        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
//                ParseObject message = ParseObject.create("Message");
//                message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
//                message.put(BODY_KEY, data);
                // Using new `Message` Parse-backed model now
                Message message = new Message();
                message.setBody(data);
                message.setUserId(ParseUser.getCurrentUser().getObjectId());
                message.setUserNumberKey(ParseUser.getCurrentUser().getInt("number"));
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "Successfully created message on Parse",
                                    Toast.LENGTH_SHORT).show();
                            refreshMessages();
                        }
                    }
                });
                etMessage.setText(null);
            }
        });
    }


//     Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    rvChat.scrollToPosition(0);
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }
}
