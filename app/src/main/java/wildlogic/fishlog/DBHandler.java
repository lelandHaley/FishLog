package wildlogic.fishlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        // I will suggest to keep the connection to the database open when your app is
        // running, the recommended time to close the connection is when your app is
        //going to pause/stop.
        close();
        return l;
    }

    public Record[] getRecords() {
        Record[] resultsRecords = null;

        String[] tableColumns = new String[]{"*"};
        String whereClause = "uploaded = 'false'";
        //String[] whereArgs = new String[] {"false"};
        String[] whereArgs = null;
        //String orderBy = "";
        open();

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
                    // String name = c.getString(c.getColumnIndex("name"));
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

//
//
//
//            if (c.moveToFirst()){
//                while(!c.isAfterLast()){
//                    String data = c.getString(c.getColumnIndex("data"));
//                    // do what ever you want here
//
//
////                    County county = new County();
////                    county.setId(cursor.getString(cursor
////                            .getColumnIndex(ColumnID.ID)));
////                    county.setAgent_Name(cursor.getString(cursor
////                            .getColumnIndex(ColumnID.AGENT_NAME)));
////                    county.setAddress_Line_1(cursor.getString(cursor
////                            .getColumnIndex(ColumnID.ADDRESS_LINE_1)));
////
////
////                    countyList.add(county);
//
//
//
//                    c.moveToNext();
//                }
//            }
//            c.close();

//            String s = c.getString(0);
  //          String s1 = c.getString(1);
  //          String s2 = c.toString();
//        String queryString =
//                "SELECT column1, (SELECT max(column1) FROM table1) AS max FROM table1 " +
//                        "WHERE column1 = ? OR column1 = ? ORDER BY column1";
//        database.rawQuery(queryString, whereArgs);
//            close();
//        }
//        return null;


    }


    public void changeRecordToUploaded(Record r) {

       // long date = System.currentTimeMillis();

        //database.open();
       // SQLiteDatabase db = helper.getWritableDatabase(); // helper is MyDatabaseHelper, a subclass database control class in which this updateTime method is resides
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_UPLOADED, "true"); // (column name, new row value)
       // String selection = MyDatabaseHelper.ID + " LIKE ?"; // where ID column = rowId (that is, selectionArgs)



        String selection = COLUMN_NAME + " = '"+r.getName() +"' and " +
                COLUMN_LAT + " = '"+r.getLatitude() +"' and " +
                COLUMN_LON + " = '"+r.getLongitude() +"' and " +
                COLUMN_LURE + " = '"+r.getLure() +"' and " +
                COLUMN_WEATHER + " = '"+r.getWeather() +"' and " +
                COLUMN_SPECIES + " = '"+r.getSpecies() +"' and " +
                COLUMN_TIME + " = '"+r.getTime() +"' and " +
                COLUMN_TEMP + " = '"+r.getTemperature() +"' and " +
                COLUMN_PATH + " = '"+r.getPath() +"' and " +
                COLUMN_USER + " = '"+r.getUser() +"' and " +
                COLUMN_HOUR + " = '"+r.getHour() +"' and " +
                COLUMN_UPLOADED + " = 'false'";


        //String[] selectionArgs = { String.valueOf(rowId) };
       // String[] selectionArgs = null;
//        open();
       // int numRowsEffected = database.update(TABLE_RECORDS, contentValues, selection, selectionArgs);

//        close();





        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_UPLOADED, "true"); // (column name, new row value)
        open();
//        int numRowsEffected = database.update(TABLE_RECORDS,
//                contentValues,
//                COLUMN_NAME + " = ? AND " + COLUMN_LAT + " = ? AND " +
//                        COLUMN_LON + " = ? AND " + COLUMN_LURE + " = ? AND " +
//                        COLUMN_WEATHER + " = ? AND " + COLUMN_SPECIES + " = ? AND " +
//                        COLUMN_TIME + " = ? AND " + COLUMN_TEMP + " = ? AND " +
//                        COLUMN_USER + " = ? AND " + COLUMN_PATH + " = ? AND " +
//                        COLUMN_HOUR + " = ? AND " + COLUMN_UPLOADED + " = ?",
//                new String[]{r.getName(), r.getLatitude(), r.getLongitude(), r.getLure(),
//                r.getWeather(), r.getSpecies(), r.getTime(), r.getTemperature(), r.getUser(),
//                r.getPath(), r.getHour(), "false"});

       // Record[] rec = getRecords();





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



//        String strSQL = "UPDATE myTable SET Column1 = someValue WHERE columnId = "+ someValue;
////
//       // int numRowsEffected =
//        database.execSQL(strSQL);




        System.out.println("num rows updated = " + numRowsEffected);

        //database.close();
        //return id;

    }



    public void open() {
        database = dbHelper.getWritableDatabase();
//        Log.i(LOG_TAG, "Database Opened");
    } // end method open

    public void close() {
//        Log.i(LOG_TAG, "Database Closed");
        dbHelper.close();
    } // end method close

}


