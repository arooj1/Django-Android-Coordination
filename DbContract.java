package com.example.ahmed.syncserver;

import android.net.Uri;

/**
 * Created by Ahmed on 7/10/2017.
 */

public class DbContract {
    public static final int SYNC_STATUS_OK =0;
    public static final int SYNC_STATUS_FAILED =1;

    // ContentProvider information
    public static final String CONTENT_AUTHORITY = "com.example.ahmed.syncserver";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_USERS = "User";

    // Declare Server Variable as well.
   // public static final String SERVER_URL = "http://10.0.0.2/syncdemo/syncinfo.php";
    public static final String SERVER_URL = "http://130.113.70.48:8000/users/?format=api";
    // to update Recycler view after broadcast.
    public static final String UI_UPDATE_BROADCAST = "com.example.ahmed.syncapp.uiupdatebroadcast";

    // declare some variables to define database sqlite.
    public static final int DATABASE_VERSION =1;
    public static final String DATABASE_NAME = "userdb";
    public static final String TABLE_NAME = "userinfo";

    // name of the table columns declared below.
    public static class Users {
    // not sure if I should create user_id here. But i guess i need UUID to maintain user identity.
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_LINK = "link";
        public static final String COL_EMAIL = "email";
        public static final String COL_PASSWORD = "password";

        // ContentProvider information for articles

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();
        //for when you expect the Cursor to contain 0 through infinity items
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_USERS;
        //for when you expect the Cursor to contain 1 item
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_USERS;

    }
}
