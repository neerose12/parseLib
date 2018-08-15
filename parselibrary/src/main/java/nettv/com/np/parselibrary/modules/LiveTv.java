package nettv.com.np.parselibrary.modules;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.util.List;

import nettv.com.np.parselibrary.ParseInstance;
import nettv.com.np.parselibrary.callbacks.LiveTvCallbacks;
import nettv.com.np.parselibrary.models.UserChannel;
import nettv.com.np.parselibrary.models.UserData;

public class LiveTv {

    private ListenerInfo mListenerInfo;

    @SuppressWarnings("FieldCanBeLocal")
    private final ParseInstance parseInstance;

    private int defaultFavChannelLimit = 20;
    private int defaultRecChannelLimit = 20;
    private int defaultMarkRecentTimeout = 20;

    private static final String LIVETV_INIT_FAVORITE_CHANNELS_LIMIT = "LIVETV_INIT_FAVORITE_CHANNELS_LIMIT";
    private static final String LIVETV_INIT_RECENT_CHANNELS_LIMIT = "LIVETV_INIT_RECENT_CHANNELS_LIMIT";
    private static final String LIVETV_INIT_MARK_RECENT_TIMEOUT = "LIVETV_INIT_MARK_RECENT_TIMEOUT";


    public LiveTv(ParseInstance parseInstance) {

        this.parseInstance = parseInstance;

        defaultFavChannelLimit = parseInstance.getParseConfig().getInt(LIVETV_INIT_FAVORITE_CHANNELS_LIMIT, defaultFavChannelLimit);
        defaultRecChannelLimit = parseInstance.getParseConfig().getInt(LIVETV_INIT_RECENT_CHANNELS_LIMIT, defaultRecChannelLimit);
        defaultMarkRecentTimeout = parseInstance.getParseConfig().getInt(LIVETV_INIT_MARK_RECENT_TIMEOUT, defaultRecChannelLimit);

    }

    public int getMarkRecentTimeout() {
        return 300;
    }

    // get
    public void getFavoriteChannels(String serial) {
        getChannels(UserChannel.Type.FAVORITE, serial);
    }

    public void getFavoriteChannels(String serial, LiveTvCallbacks.OnFavoriteGetListener listener) {
        setOnFavoriteGetListener(listener);
        getChannels(UserChannel.Type.FAVORITE, serial);
    }

    public void getRecentChannels(String serial) {
        getChannels(UserChannel.Type.RECENT, serial);
    }

    public void getRecentChannels(String serial, LiveTvCallbacks.OnRecentGetListener listener) {
        setOnRecentGetListener(listener);
        getChannels(UserChannel.Type.RECENT, serial);
    }


    // set
    public void setFavorite(final String serial, UserChannel userChannel, LiveTvCallbacks.OnFavoriteSetListener listener) {
        userChannel.setType(UserChannel.Type.FAVORITE);
        setOnFavoirteSetListener(listener);
        setRecFavChannel(UserChannel.Type.FAVORITE, serial, userChannel);
    }

    public void setFavorite(final String serial, UserChannel userChannel) {
        userChannel.setType(UserChannel.Type.FAVORITE);
        setRecFavChannel(UserChannel.Type.FAVORITE, serial, userChannel);
    }

    public void setRecent(final String serial, UserChannel userChannel, LiveTvCallbacks.OnRecentSetListener listener) {
        userChannel.setType(UserChannel.Type.RECENT);
        setOnRecentSetListener(listener);
        setRecFavChannel(UserChannel.Type.RECENT, serial, userChannel);
    }

    public void setRecent(final String serial, UserChannel userChannel) {
        userChannel.setType(UserChannel.Type.RECENT);
        setRecFavChannel(UserChannel.Type.RECENT, serial, userChannel);
    }


    // delete
    public void deleteFavorite(final String serial, int channelId, LiveTvCallbacks.OnFavoriteDeleteListener listener) {
        setOnFavoriteDeleteListener(listener);
        deleteChannel(UserChannel.Type.FAVORITE, serial, channelId);
    }

    public void deleteFavorite(final String serial, int channelId) {
        deleteChannel(UserChannel.Type.FAVORITE, serial, channelId);
    }

    public void deleteRecent(final String serial, int channelId, LiveTvCallbacks.OnRecentDeleteListener listener) {
        setOnRecentDeleteListener(listener);
        deleteChannel(UserChannel.Type.RECENT, serial, channelId);
    }

    private void deleteRecent(final String serial, int channelId) {
        deleteChannel(UserChannel.Type.RECENT, serial, channelId);
    }


    /**
     * Request for channels, list will be returned via either onRecentGet or onFavoriteGet callbacks
     *
     * @param type
     * @param serial
     * @return
     */
    private void getChannels(final UserChannel.Type type, String serial) {

        ParseQuery<UserData> userQuery = new ParseQuery<>(UserData.class);
        userQuery.whereEqualTo(UserData.KEY_SERIAL, serial);
        userQuery.findInBackground(new FindCallback<UserData>() {
            @Override
            public void done(List<UserData> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        UserData userData = objects.get(0);

                        ParseRelation<UserChannel> relation = userData.getRelation(UserData.KEY_CHANNELS);
                        ParseQuery<UserChannel> query = relation.getQuery();

                        int userLimit;
                        // user parseConfig overrides default limit parseConfig
                        if (type == UserChannel.Type.RECENT) {
                            userLimit = userData.getRecentChannelsLimit();
                        } else {
                            userLimit = userData.getFavoriteChannelsLimit();
                        }

                        if (userLimit == 0) {
                            if (type == UserChannel.Type.RECENT) {
                                userLimit = defaultRecChannelLimit;
                            } else {
                                userLimit = defaultFavChannelLimit;
                            }
                        }


                        if (type == UserChannel.Type.FAVORITE) {
                            query.whereEqualTo(UserChannel.KEY_TYPE, 0);
                        } else {
                            query.whereEqualTo(UserChannel.KEY_TYPE, 1);
                        }
                        query.orderByDescending("updatedAt");
                        query.setLimit(userLimit);

                        query.findInBackground(new FindCallback<UserChannel>() {
                            @Override
                            public void done(List<UserChannel> userChannels, ParseException e) {
                                if (e == null) {
                                    if (type == UserChannel.Type.FAVORITE) {
                                        getListenerInfo().favoriteGetListener.onFavoriteGet(userChannels, null);
                                    } else {
                                        getListenerInfo().recentGetListener.onRecentGet(userChannels, null);
                                    }
                                } else {
                                    if (type == UserChannel.Type.FAVORITE) {
                                        getListenerInfo().favoriteGetListener.onFavoriteGet(null, e);
                                    } else {
                                        getListenerInfo().recentGetListener.onRecentGet(null, e);
                                    }

                                }
                            }
                        });

                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Set favorite or recent channel
     *
     * @param type
     * @param serial
     * @param userChannel
     */
    private void setRecFavChannel(final UserChannel.Type type, final String serial, final UserChannel userChannel) {

        final DeleteCallback deleteCallback = new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseQuery<UserData> userQuery = new ParseQuery<>(UserData.class);
                    userQuery.whereEqualTo(UserData.KEY_SERIAL, serial);
                    userQuery.findInBackground(new FindCallback<UserData>() {
                        @Override
                        public void done(List<UserData> objects, ParseException e) {
                            if (e == null) {
                                // no objects for serial
                                if (objects.size() == 0) {
                                    final UserData userData = new UserData();
                                    userData.setSerial(serial);

                                    ParseRelation<UserChannel> channelsParseRelation = userData.getRelation(UserData.KEY_CHANNELS);
                                    channelsParseRelation.add(userChannel);
                                    userChannel.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                userData.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e != null) {
                                                            userChannel.deleteEventually();
                                                            if (type == UserChannel.Type.FAVORITE) {
                                                                getListenerInfo().favoirteSetListener.onFavoriteSet(false, e);
                                                            } else {
                                                                getListenerInfo().recentSetListener.onRecentSet(false, e);
                                                            }
                                                        } else {
                                                            if (type == UserChannel.Type.FAVORITE) {
                                                                getListenerInfo().favoirteSetListener.onFavoriteSet(true, null);
                                                            } else {
                                                                getListenerInfo().recentSetListener.onRecentSet(true, null);
                                                            }
                                                        }
                                                    }
                                                });
                                            } else {
                                                if (type == UserChannel.Type.FAVORITE) {
                                                    getListenerInfo().favoirteSetListener.onFavoriteSet(false, e);
                                                } else {
                                                    getListenerInfo().recentSetListener.onRecentSet(false, e);
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    final UserData userData = objects.get(0);
                                    ParseRelation<UserChannel> relation = userData.getRelation(UserData.KEY_CHANNELS);
                                    relation.add(userChannel);
                                    userChannel.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            userData.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e != null) {
                                                        userChannel.deleteEventually();
                                                        if (type == UserChannel.Type.FAVORITE) {
                                                            getListenerInfo().favoirteSetListener.onFavoriteSet(false, e);
                                                        } else {
                                                            getListenerInfo().recentSetListener.onRecentSet(true, e);
                                                        }
                                                    } else {
                                                        if (type == UserChannel.Type.FAVORITE) {
                                                            getListenerInfo().favoirteSetListener.onFavoriteSet(true, null);
                                                        } else {
                                                            getListenerInfo().recentSetListener.onRecentSet(true, null);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            } else {
                                if (type == UserChannel.Type.FAVORITE) {
                                    getListenerInfo().favoirteSetListener.onFavoriteSet(false, e);
                                } else {
                                    getListenerInfo().recentSetListener.onRecentSet(true, e);
                                }
                            }

                        }
                    });
                }
            }
        };


        ParseQuery<UserChannel> userQuery = new ParseQuery<>(UserChannel.class);
        userQuery.whereEqualTo(UserChannel.KEY_SERIAL, serial);

        if (type == UserChannel.Type.FAVORITE) {
            userQuery.whereEqualTo(UserChannel.KEY_TYPE, 0);
        } else {
            userQuery.whereEqualTo(UserChannel.KEY_TYPE, 1);
        }

        if (userChannel.getId() >= 0) {
            userQuery.whereEqualTo(UserChannel.KEY_ID, userChannel.getId());
        }

        userQuery.findInBackground(new FindCallback<UserChannel>() {
            @Override
            public void done(List<UserChannel> objects, ParseException e) {
                if (e == null) {
                    UserChannel.deleteAllInBackground(objects, deleteCallback);
                } else {
                    if (type == UserChannel.Type.FAVORITE) {
                        getListenerInfo().favoirteSetListener.onFavoriteSet(false, e);
                    } else {
                        getListenerInfo().recentSetListener.onRecentSet(false, e);
                    }
                }
            }
        });


    }

    /**
     * Delete channel from recent or favorite
     *
     * @param type
     * @param serial
     * @param channelId
     */
    private void deleteChannel(final UserChannel.Type type, final String serial, int channelId) {

        ParseQuery<UserChannel> userQuery = new ParseQuery<>(UserChannel.class);
        userQuery.whereEqualTo(UserChannel.KEY_SERIAL, serial);

        if (type == UserChannel.Type.FAVORITE) {
            userQuery.whereEqualTo(UserChannel.KEY_TYPE, 0);
        } else {
            userQuery.whereEqualTo(UserChannel.KEY_TYPE, 1);
        }

        if (channelId >= 0) {
            userQuery.whereEqualTo(UserChannel.KEY_ID, channelId);
        }

        userQuery.findInBackground(new FindCallback<UserChannel>() {
            @Override
            public void done(List<UserChannel> objects, ParseException e) {
                if (e == null) {
                    UserChannel.deleteAllInBackground(objects, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (type == UserChannel.Type.FAVORITE) {
                                    getListenerInfo().favoriteDeleteListener.onFavoriteDelete(true, null);
                                } else {
                                    getListenerInfo().recentDeleteListener.onRecentDelete(true, null);
                                }
                            } else {
                                if (type == UserChannel.Type.FAVORITE) {
                                    getListenerInfo().favoriteDeleteListener.onFavoriteDelete(false, e);
                                } else {
                                    getListenerInfo().recentDeleteListener.onRecentDelete(false, e);
                                }
                            }
                        }
                    });
                } else {
                    if (type == UserChannel.Type.FAVORITE) {
                        getListenerInfo().favoriteDeleteListener.onFavoriteDelete(false, e);
                    } else {
                        getListenerInfo().recentDeleteListener.onRecentDelete(false, e);
                    }
                }
            }
        });

    }


    // callbacks

    public void setOnFavoriteGetListener(LiveTvCallbacks.OnFavoriteGetListener favoriteGetListener) {
        getListenerInfo().favoriteGetListener = favoriteGetListener;
    }

    public void setOnFavoirteSetListener(LiveTvCallbacks.OnFavoriteSetListener favoriteListener) {
        getListenerInfo().favoirteSetListener = favoriteListener;
    }

    public void setOnFavoriteDeleteListener(LiveTvCallbacks.OnFavoriteDeleteListener favoriteDeleteListener) {
        getListenerInfo().favoriteDeleteListener = favoriteDeleteListener;
    }

    public void setOnRecentGetListener(LiveTvCallbacks.OnRecentGetListener recentGetListener) {
        getListenerInfo().recentGetListener = recentGetListener;
    }

    public void setOnRecentSetListener(LiveTvCallbacks.OnRecentSetListener recentListener) {
        getListenerInfo().recentSetListener = recentListener;
    }

    public void setOnRecentDeleteListener(LiveTvCallbacks.OnRecentDeleteListener recentDeleteListener) {
        getListenerInfo().recentDeleteListener = recentDeleteListener;
    }


    public ListenerInfo getListenerInfo() {
        if (mListenerInfo != null) {
            return mListenerInfo;
        }
        mListenerInfo = new ListenerInfo();
        return mListenerInfo;
    }

    // register listern
    private static class ListenerInfo {

        public LiveTvCallbacks.OnFavoriteGetListener favoriteGetListener;
        public LiveTvCallbacks.OnRecentGetListener recentGetListener;

        public LiveTvCallbacks.OnFavoriteSetListener favoirteSetListener;
        public LiveTvCallbacks.OnRecentSetListener recentSetListener;

        public LiveTvCallbacks.OnFavoriteDeleteListener favoriteDeleteListener;
        public LiveTvCallbacks.OnRecentDeleteListener recentDeleteListener;

    }

}
