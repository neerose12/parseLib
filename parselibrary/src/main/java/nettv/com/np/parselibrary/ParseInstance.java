package nettv.com.np.parselibrary;


import android.content.Context;
import android.util.Log;

import com.parse.ConfigCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import nettv.com.np.parselibrary.callbacks.ParseInstanceCallbacks;
import nettv.com.np.parselibrary.models.UserChannel;
import nettv.com.np.parselibrary.models.UserData;
import nettv.com.np.parselibrary.models.UserMessage;
import nettv.com.np.parselibrary.modules.LiveTv;

public class ParseInstance {

    private ListenerInfo mListenerInfo;

    private final String sessionToken;

    public  ParseConfig parseConfig;


    private LiveTv liveTv;


    public enum ParseModels {
        USER_DATA, USER_MESSAGES
    }


    public ParseInstance(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    private void initConfig() {
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {

                if (e == null) {
                    parseConfig = ParseConfig.getCurrentConfig();
                    Log.d("TAG", "Yay! Config was fetched from the server.");
                } else {
                    Log.e("TAG", "Failed to fetch. Using Cached Config.");
                }

//                getListenerInfo().loginListener.onLogin(true, ParseUser.getCurrentUser(), null);


            }
        });

    }

    public void logIn(ParseInstanceCallbacks.OnLoginListener listner) {
        this.setOnLoginListener(listner);
        logIn();
    }

    private void logIn() {
        ParseUser.becomeInBackground(sessionToken, new LogInCallback() {
            @Override
            public void done(final ParseUser user, ParseException e) {
                if (e == null) {
                    ParseConfig.getInBackground(new ConfigCallback() {
                        @Override
                        public void done(ParseConfig config, ParseException e) {
                            if (e == null) {
                                parseConfig = ParseConfig.getCurrentConfig();
                                liveTv = new LiveTv(ParseInstance.this);
                                getListenerInfo().loginListener.onLogin(true, user, null);
                            } else {
                                getListenerInfo().loginListener.onLogin(false, null, e);
                            }
                        }
                    });
                } else {
                    getListenerInfo().loginListener.onLogin(false, null, e);
                }
            }
        });
    }


    public boolean isLoggedIn() {
        return ParseUser.getCurrentUser() == null ? false : true;
    }

    public LiveTv getLiveTv() {
        return liveTv;
    }

    public ParseConfig getParseConfig() {
        return parseConfig;
    }

    public void subscribeForLiveQuery(ParseModels model, String serial, ParseInstanceCallbacks.OnLiveQueryResponseListener listener) {

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        setOnLiveQueryResponseListener(listener);

        switch (model) {
            case USER_DATA:
                ParseQuery<UserData> queryUserData = ParseQuery.getQuery(UserData.class);
                SubscriptionHandling<UserData> subscriptionHandlingUserData = parseLiveQueryClient.subscribe(queryUserData);
                subscriptionHandlingUserData.handleEvents(new SubscriptionHandling.HandleEventsCallback<UserData>() {
                    @Override
                    public void onEvents(ParseQuery<UserData> query, SubscriptionHandling.Event event, UserData parseMessage) {
                        getListenerInfo().liveQueryResponseListener.onLiveQueryResponse(UserData.class, query, event, parseMessage);
                    }
                });
                break;
            case USER_MESSAGES:

                ParseQuery<UserMessage> queryUserMessage = ParseQuery.getQuery(UserMessage.class);
                queryUserMessage.whereEqualTo(UserMessage.KEY_SERIAL, serial);
                SubscriptionHandling<UserMessage> subscriptionHandlingUserMessage = parseLiveQueryClient.subscribe(queryUserMessage);
                subscriptionHandlingUserMessage.handleEvents(new SubscriptionHandling.HandleEventsCallback<UserMessage>() {
                    @Override
                    public void onEvents(ParseQuery<UserMessage> query, SubscriptionHandling.Event event, UserMessage parseMessage) {
                        getListenerInfo().liveQueryResponseListener.onLiveQueryResponse(UserMessage.class, query, event, parseMessage);
                    }
                });
                break;
        }

    }


    public static void initParseInstance(Context context) {

        ParseObject.registerSubclass(UserMessage.class);
        ParseObject.registerSubclass(UserData.class);
        ParseObject.registerSubclass(UserChannel.class);


        Parse.enableLocalDatastore(context);

        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(context.getString(R.string.parse_app_id))
                .clientKey(context.getString(R.string.parse_client_key))
                .server(context.getString(R.string.parse_server))
                .enableLocalDataStore()
                .build()
        );

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }


    // call backs
    public void setOnLoginListener(ParseInstanceCallbacks.OnLoginListener loginListener) {
        getListenerInfo().loginListener = loginListener;
    }

    public void setOnLiveQueryResponseListener(ParseInstanceCallbacks.OnLiveQueryResponseListener liveQueryResponseListener) {
        getListenerInfo().liveQueryResponseListener = liveQueryResponseListener;
    }


    protected ListenerInfo getListenerInfo() {
        if (mListenerInfo != null) {
            return mListenerInfo;
        }
        mListenerInfo = new ListenerInfo();
        return mListenerInfo;
    }

    // register listern
    static class ListenerInfo {
        public ParseInstanceCallbacks.OnLoginListener loginListener;
        public ParseInstanceCallbacks.OnLiveQueryResponseListener liveQueryResponseListener;
    }


}
