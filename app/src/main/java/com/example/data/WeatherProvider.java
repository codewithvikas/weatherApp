package com.example.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.utils.WeatherDateUtils;

public class WeatherProvider extends ContentProvider {

    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    WeatherDbHelper mWeatherDbHelper;

    static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.WEATHER_PATH,CODE_WEATHER);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.WEATHER_PATH+"/#",CODE_WEATHER_WITH_DATE);
        return uriMatcher;
    }
    @Override
    public boolean onCreate() {
        mWeatherDbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor ;
        switch (sUriMatcher.match(uri)){
                case CODE_WEATHER:
                    cursor = mWeatherDbHelper.getReadableDatabase().query(
                            WeatherContract.WeatherEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                    break;
            case CODE_WEATHER_WITH_DATE:
                cursor =mWeatherDbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherContract.WeatherEntry.COLUMN_DATE+" = ?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri :"+uri);
            }
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        SQLiteDatabase db = mWeatherDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case CODE_WEATHER:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values){
                        long weatherDate = value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
                        if (!WeatherDateUtils.isNormalizedDate(weatherDate)){
                            throw new IllegalArgumentException("Date must be normalized");
                        }
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,value);
                        if (_id > -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
            if (rowsInserted > 0){
                getContext().getContentResolver().notifyChange(uri,null);
            }
            return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int num_rows_deleted ;

        switch (sUriMatcher.match(uri)){
            case CODE_WEATHER:
                if (selection==null){
                    selection = "1";
                }
                num_rows_deleted = mWeatherDbHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                if (num_rows_deleted > 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
        return num_rows_deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
