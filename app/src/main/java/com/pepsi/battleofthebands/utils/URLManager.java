package com.pepsi.battleofthebands.utils;

public class URLManager {

    // Live URL
    public static final String SERVER_URL = "https://pepsibattleofthebands.com/api/";

    // Theme URL
//    public static final String SERVER_URL = "http://beta.pepsibattleofthebands.com/api/";

    public static final String GET_SEASONS = SERVER_URL + "seasons";
    public static final String GET_SEASONS_DATA = SERVER_URL + "seasons/";
    public static final String BANDS_URL = SERVER_URL + "bands";
    public static final String GET_SETTINGS = SERVER_URL + "settings";
    public static final String BANDS_SONGS_URL = SERVER_URL + "songs";
    public static final String BANDS_JUDGES_URL = SERVER_URL + "judges";
    public static final String GET_FEEDBACK_URL = SERVER_URL + "reports";

    public static final String LOGIN_URL = SERVER_URL + "login";
    public static final String SIGNUP_URL = SERVER_URL + "register";
    public static final String LOGIN_WITH_FACEBOOK_URL = LOGIN_URL + "/facebook";
    public static final String LOGIN_WITH_GOOGLE_URL = LOGIN_URL + "/google";
    public static final String GET_FORGOT_PASSWORD_URL = SERVER_URL + "forgot";
    public static final String GET_UPDATE_URL = SERVER_URL + "user";
    public static final String GET_TOKEN_URL = SERVER_URL + "token";
    public static final String GET_VOTING_URL = SERVER_URL + "vote";
}
