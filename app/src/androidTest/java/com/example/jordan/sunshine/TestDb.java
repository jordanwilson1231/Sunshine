package com.example.jordan.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.jordan.sunshine.data.WeatherContract.LocationEntry;
import com.example.jordan.sunshine.data.WeatherContract.WeatherEntry;
import com.example.jordan.sunshine.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by Jordan on 8/7/2014.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        // If there's and error in those massive SQL table creation Strings,
        // errors will ne thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper =
                new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues locationValues = createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);

        //Verify we got a row back
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);


        // A cursor is your primary interface to the query results.
        Cursor locationCursor = db.query(
                LocationEntry.TABLE_NAME, // Table to Query
                null, // Columns to return
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // Columns to group by
                null, // Columns to filter by row groups
                null // sort order
        );

        validateCursor(locationCursor, locationValues);

        ContentValues weatherValues = createWeatherValues(locationRowId);

        // Insert a row into the weather table
        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
        // Verify the row was added
        assertTrue(weatherRowId != -1);

        Cursor weatherCursor = db.query(
                WeatherEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        validateCursor(weatherCursor, weatherValues);

        dbHelper.close();
    }

    static ContentValues createNorthPoleLocationValues() {
        //Test data we're going to insert into the DB
        String testName = "North Pole";
        String testLocationSetting = "99705";
        double testLatitude = 64.772;
        double testLongitude = -147.355;
        // Create a new map of values, where column names are the keys
        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationEntry.COLUMN_CITY_NAME, testName);
        locationValues.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        locationValues.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        locationValues.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);

        return locationValues;
    }

    static ContentValues createWeatherValues(long locationRowId) {
        // A map of values where the weather table columns are the keys
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
