package wildlogic.fishlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by thatDude on 4/12/18.
 */
public class DBHandler {

    //SQLiteOpenHelper dbHelper;
    RecordsOpenHelper dbHelper;
    SQLiteDatabase database;

    private static final String TABLE_RECORDS = "records";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LON = "lon";
    private static final String COLUMN_LURE = "lure";
    private static final String COLUMN_SPECIES = "species";
    private static final String COLUMN_WEATHER = "weather";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_TEMP = "temp";
    private static final String COLUMN_PATH = "path";
    private static final String COLUMN_USER = "user";
    private static final String COLUMN_HOUR = "hour";
    private static final String COLUMN_UPLOADED = "uploaded";


    public DBHandler(Context context) {
        dbHelper = new RecordsOpenHelper(context);

    }

    public long addUnsavedRecord(Record record) {

        // open the database connection
        open();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, record.getName());
        values.put(COLUMN_LAT, record.getLatitude());
        values.put(COLUMN_LON, record.getLongitude());
        values.put(COLUMN_LURE, record.getLure());
        values.put(COLUMN_SPECIES, record.getSpecies());
        values.put(COLUMN_WEATHER, record.getWeather());
        values.put(COLUMN_TIME, record.getTime());
        values.put(COLUMN_TEMP, record.getTemperature());
        values.put(COLUMN_USER, record.getUser());
        values.put(COLUMN_PATH, record.getPath());
        values.put(COLUMN_HOUR, record.getHour());
        values.put(COLUMN_UPLOADED, "false");
        long l = database.insert(TABLE_RECORDS, null, values);

        close();
        return l;
    }

    public Record[] getRecords() {
        Record[] resultsRecords = null;


        String[] whereArgs = null;

        String count = "";
        int countInt = -1;
        String countQuery =
                "SELECT count(*) from records where uploaded = 'false'";
        Cursor c1 = database.rawQuery(countQuery, whereArgs);


        c1.moveToFirst();
        if (!c1.isAfterLast()) {
            do {
                count = c1.getString(c1.getColumnIndex("count(*)"));
            } while (c1.moveToNext());
        } else {
            System.out.println("There are no records in the data set");
        }

        try {
            countInt = Integer.parseInt(count);

        } catch (Exception e) {
            System.out.println("count not in number format");
        }

        if (countInt > 0) {

            String queryString =
                    "SELECT * from records where uploaded = 'false'";
            Cursor c = database.rawQuery(queryString, whereArgs);
            resultsRecords = new Record[countInt];
            int index = 0;
            c.moveToFirst();
            //this checks to make sure you don't have an empty set
            if (!c.isAfterLast()) {
                do {

                    String name = c.getString(c.getColumnIndex("name"));
                    String lat = c.getString(c.getColumnIndex("lat"));
                    String lon = c.getString(c.getColumnIndex("lon"));
                    String lure = c.getString(c.getColumnIndex("lure"));
                    String species = c.getString(c.getColumnIndex("species"));
                    String weather = c.getString(c.getColumnIndex("weather"));
                    String time = c.getString(c.getColumnIndex("time"));
                    String temp = c.getString(c.getColumnIndex("temp"));
                    String user = c.getString(c.getColumnIndex("user"));
                    String path = c.getString(c.getColumnIndex("path"));
                    String hour = c.getString(c.getColumnIndex("hour"));
                    String uploaded = c.getString(c.getColumnIndex("uploaded"));

                    Record r = new Record(name, lat, lon, lure, weather, species, time, temp, user, path, hour, uploaded);
                    resultsRecords[index] = r;
                    index++;
                    System.out.println(name);
                } while (c.moveToNext());
            } else {
                System.out.println("There are no records in the data set");
            }

            close();
        }
        if(resultsRecords == null){
            resultsRecords = new Record[0];
        }
        return resultsRecords;
    }


    public void changeRecordToUploaded(Record r) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_UPLOADED, "true"); // (column name, new row value)
        open();

        int numRowsEffected = database.update(TABLE_RECORDS,
                contentValues,
                COLUMN_NAME + " = ? AND " + COLUMN_LAT + " = ? AND " +
                        COLUMN_LON + " = ? AND " + COLUMN_LURE + " = ? AND " +
                        COLUMN_WEATHER + " = ? AND " + COLUMN_SPECIES + " = ? AND " +
                        COLUMN_TIME + " = ? AND " + COLUMN_TEMP + " = ? AND " +
                        COLUMN_USER + " = ? AND " + COLUMN_PATH + " = ? AND " +
                        COLUMN_HOUR + " = ?",
                new String[]{r.getName(), r.getLatitude(), r.getLongitude(), r.getLure(),
                        r.getWeather(), r.getSpecies(), r.getTime(), r.getTemperature(), r.getUser(),
                        r.getPath(), r.getHour()});

        System.out.println("HERE");

        close();

        System.out.println("num rows updated = " + numRowsEffected);

    }



    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

}


