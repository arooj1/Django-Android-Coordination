package com.example.ahmed.syncserver;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MyContentProvider extends ContentProvider {
    // two types of queries.
    public static final int USERS = 0;
    public static final int USER_ID = 1;

    // declaring all possible queries.
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_USERS, USERS);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_USERS, USER_ID);
    }

    private SQLiteDatabase db;


    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rows;
        switch(uriMatcher.match(uri)){
            case USERS:
                rows = db.delete(DbContract.Users.COL_ID,selection,selectionArgs);
                break;
            default: throw new IllegalArgumentException("Invalid URI!");
        }
        // Notify any observers to update the UI
        if (rows != 0) {
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }


    @Nullable
    @Override
    public String getType(@Nullable Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        // Find the MIME type of the results... multiple results or a single result
        switch (uriMatcher.match(uri)) {
            case USERS:
                return DbContract.Users.CONTENT_TYPE;
// When you would like to sync a specific user information.
            case USER_ID:
                return DbContract.Users.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Invalid URI");
        }

    }

    @Override
    public Uri insert(@Nullable Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        long _id;
        switch (uriMatcher.match(uri)) {
            case USERS:
                _id = db.insert(DbContract.Users.COL_ID, null, values);
                returnUri = ContentUris.withAppendedId(DbContract.Users.CONTENT_URI, _id);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI!");
        }


        // Notify any observers to update the UI
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
            return returnUri;
    }
    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        this.db = DbHelper.getInstance(getContext()).getDb();
        return true;
    }

    @Override
    public Cursor query(@Nullable Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Cursor c;
        switch (uriMatcher.match(uri)) {
            case USERS:
                c = db.query(DbContract.Users.COL_ID,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case USER_ID:
                long _name = ContentUris.parseId(uri);
                c = db.query(DbContract.Users.COL_ID,
                        projection, DbContract.Users.COL_NAME + "=?", new String[]{String.valueOf(_name)},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI!");
        }
        // Tell the cursor to register a content observer to observe changes to the
        // URI or its descendants.
        assert getContext() != null;
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }


    @Override
    public int update(@Nullable Uri uri,@Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int rows;
        switch(uriMatcher.match(uri)){
            case USERS:
                rows = db.update(DbContract.Users.COL_ID,values,selection,selectionArgs);
                break;
            default: throw new IllegalArgumentException("Invalid URI!");
        }
        // Notify any observers to update the UI
        if (rows != 0) {
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }
}
