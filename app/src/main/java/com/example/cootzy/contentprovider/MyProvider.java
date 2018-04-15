package com.example.cootzy.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLConnection;

public class MyProvider extends ContentProvider {
    private final static String[] OPENABLE_PROJECTION= {
            OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE };

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + "com.example.cootzy.contentprovider" + ".files/");

    @Override
    public boolean onCreate() {

        File f = new File(getContext().getFilesDir(), "test.txt");
        Log.e("ON CREATE", Boolean.toString(f.exists()));

        if (!f.exists()) {
            Log.e("FileProvider", "Exception copying from assets");
            return false;
        }

        return (true);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File root = getContext().getFilesDir();
        File f = new File(root, uri.getPath()).getAbsoluteFile();

        if (!f.getPath().startsWith(root.getPath())) {
            throw new
                    SecurityException("Resolved path jumped beyond root");
        }

        if (f.exists()) {
            return (ParcelFileDescriptor.open(f, parseMode(mode)));
        }

        throw new FileNotFoundException(uri.getPath());
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (projection == null) {
            projection=OPENABLE_PROJECTION;
        }

        final MatrixCursor cursor=new MatrixCursor(projection, 1);

        MatrixCursor.RowBuilder b=cursor.newRow();

        for (String col : projection) {
            if (OpenableColumns.DISPLAY_NAME.equals(col)) {
                b.add(getFileName(uri));
            }
            else if (OpenableColumns.SIZE.equals(col)) {
                b.add(getDataLength(uri));
            }
            else { // unknown, so just add null
                b.add(null);
            }
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return(URLConnection.guessContentTypeFromName(uri.toString()));
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("Operation not supported");
    }

    private static int parseMode(String mode) {
        final int modeBits;
        if ("r".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_ONLY;
        } else if ("w".equals(mode) || "wt".equals(mode)) {
            modeBits =
                    ParcelFileDescriptor.MODE_WRITE_ONLY
                            | ParcelFileDescriptor.MODE_CREATE
                            | ParcelFileDescriptor.MODE_TRUNCATE;
        } else if ("wa".equals(mode)) {
            modeBits =
                    ParcelFileDescriptor.MODE_WRITE_ONLY
                            | ParcelFileDescriptor.MODE_CREATE
                            | ParcelFileDescriptor.MODE_APPEND;
        } else if ("rw".equals(mode)) {
            modeBits =
                    ParcelFileDescriptor.MODE_READ_WRITE
                            | ParcelFileDescriptor.MODE_CREATE;
        } else if ("rwt".equals(mode)) {
            modeBits =
                    ParcelFileDescriptor.MODE_READ_WRITE
                            | ParcelFileDescriptor.MODE_CREATE
                            | ParcelFileDescriptor.MODE_TRUNCATE;
        } else {
            throw new IllegalArgumentException("Bad mode '" + mode + "'");
        }
        return modeBits;
    }


    protected String getFileName(Uri uri) {
        return(uri.getLastPathSegment());
    }

    protected long getDataLength(Uri uri) {
        return(AssetFileDescriptor.UNKNOWN_LENGTH);
    }
}
