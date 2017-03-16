package com.example.ilya.gb_android2_les5_notewidget.models.tables;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;


public class NoteContentProvider extends ContentProvider {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.example.ilya.gb_android2_les5_notewidget";
    private static final String NOTES_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + NOTES_PATH);


    private static final String TAG = "TAGG";
    public static final String MIME_DIR = "vnd.android.cursor.dir/vnd.";
    public static final String MIME_ITEM = "vnd.android.cursor.item/vnd.";


    //// UriMatcher
    // общий Uri
    private static final int ALL_NOTES_URI_CODE = 100;
    // Uri с указанным ID
    private static final int SINGLE_NOTE_URI_CODE = 101;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, NOTES_PATH, ALL_NOTES_URI_CODE);
        MATCHER.addURI(AUTHORITY, NOTES_PATH + "/#", SINGLE_NOTE_URI_CODE);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "Provider:onCreate provider: true");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        android.database.Cursor cursor = null;
        Log.d(TAG, "Provider: query: " + uri.toString());
        Log.d(TAG, "Provider: MATCHER: " + MATCHER.match(uri));
        switch (MATCHER.match(uri)) {
            case ALL_NOTES_URI_CODE: {
                Log.d(TAG, "Provider URI_CODE: " + ALL_NOTES_URI_CODE);
                cursor = FlowManager.getDatabase("AppDataBase")
                        .getWritableDatabase()
                        .query("TblNotes", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case SINGLE_NOTE_URI_CODE: {
                String id = uri.getLastPathSegment();
                Log.d(TAG, "Provider: URI_CODE: " + SINGLE_NOTE_URI_CODE);
                Log.d(TAG, "Provider: id: " + id);
                cursor = FlowManager.getDatabase("AppDataBase")
                        .getWritableDatabase()
                        .query("TblNotes", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String type;

        final int match = MATCHER.match(uri);
        switch (match) {
            case ALL_NOTES_URI_CODE: {
                type = MIME_DIR + NOTES_PATH;
                break;
            }

            case SINGLE_NOTE_URI_CODE: {
                type = MIME_ITEM + NOTES_PATH;
                break;
            }

            default: {
                throw new IllegalArgumentException("Unknown URI" + uri);
            }
        }
        Log.d(TAG, "Provider:getType: math:" + match + "-type:" + type);
        return type;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = MATCHER.match(uri);
        Log.d(TAG, "Provider: insert: " + uri.toString());
        Log.d(TAG, "Provider: MATCHER: " + match);
        switch (match) {
            case ALL_NOTES_URI_CODE: {
                ModelAdapter adapter = FlowManager.getModelAdapter(FlowManager.getTableClassForName("AppDataBase", "TblNotes"));
                final long id = FlowManager.getDatabase("AppDataBase")
                        .getWritableDatabase()
                        .insertWithOnConflict("TblNotes", null, values, ConflictAction
                                .getSQLiteDatabaseAlgorithmInt(adapter.getInsertOnConflictAction()));
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            }
            default: {
                throw new IllegalStateException("Provider:Unknown Uri" + uri);
            }
        }
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = MATCHER.match(uri);
        Log.d(TAG, "Provider: delete: " + uri.toString());
        Log.d(TAG, "Provider: MATCHER: " + match);
        switch (match) {
            case ALL_NOTES_URI_CODE: {
                long count = FlowManager.getDatabase("AppDataBase")
                        .getWritableDatabase()
                        .delete("TblNotes", selection, selectionArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return (int) count;
            }

            case SINGLE_NOTE_URI_CODE: {
                String id = uri.getLastPathSegment();
                Log.d(TAG, "Provider: Delete single:, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = "listPosition = " + id;
                } else {
                    selection = selection + " AND listPosition = " + id;
                }
                Log.d(TAG, "Provider: Delete selection: " + selection);

                SQLite.delete(com.example.ilya.gb_android2_les5_notewidget.models.tables.TblNotes.class)
                        .where(TblNotes_Table._id.is(Integer.parseInt(id)))
                        .async()
                        .execute();
                getContext().getContentResolver().notifyChange(uri, null);
                long count = FlowManager.getDatabase("AppDataBase")
                        .getWritableDatabase()
                        .delete("TblNotes", selection, selectionArgs);
                if (count > 0) {

                }
                return (int) count;
            }

            default: {
                throw new IllegalArgumentException("Provider:Unknown URI" + uri);
            }
        }
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = MATCHER.match(uri);
        switch (match) {
            case ALL_NOTES_URI_CODE: {
                ModelAdapter adapter = FlowManager.getModelAdapter(FlowManager.getTableClassForName("AppDataBase", "TblNotes"));
                long count = FlowManager
                        .getDatabase("AppDataBase")
                        .getWritableDatabase()
                        .updateWithOnConflict("TblNotes", values, selection, selectionArgs,
                                ConflictAction.getSQLiteDatabaseAlgorithmInt(adapter.getUpdateOnConflictAction()));
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return (int) count;
            }

            case SINGLE_NOTE_URI_CODE: {
                String id = uri.getLastPathSegment();
                Log.d(TAG, "Provider: Update single:, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = "_id = " + id;
                } else {
                    selection = selection + " AND _id = " + id;
                }
                Log.d(TAG, "Provider: Update single selection: " + selection);

                ModelAdapter adapter = FlowManager.getModelAdapter(FlowManager.getTableClassForName("AppDataBase", "TblNotes"));
                long count = FlowManager
                        .getDatabase(AppDataBase.class)
                        .getWritableDatabase()
                        .updateWithOnConflict("TblNotes", values, selection, selectionArgs,
                                ConflictAction.getSQLiteDatabaseAlgorithmInt(adapter.getUpdateOnConflictAction()));
                Log.d(TAG, "Provider: Update single count:, " + count);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return (int) count;
            }


            default: {
                throw new IllegalStateException("Provider:Unknown Uri" + uri);
            }
        }
    }
    // @ContentUri(path = NOTES_PATH, type = ContentUri.ContentType.VND_MULTIPLE + NOTES_PATH)

}