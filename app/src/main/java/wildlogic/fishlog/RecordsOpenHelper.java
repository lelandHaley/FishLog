package wildlogic.fishlog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by thatDude on 4/12/18.
 */
public class RecordsOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fishlog";
    private static final int DATABASE_VERSION = 1;

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

    private static final String TABLE_RECORDS_CREATE =
            "CREATE TABLE " + TABLE_RECORDS + " (" +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_LAT + " TEXT," +
                    COLUMN_LON + " TEXT," +
                    COLUMN_LURE + " TEXT," +
                    COLUMN_SPECIES + " TEXT," +
                    COLUMN_WEATHER + " TEXT," +
                    COLUMN_TIME + " TEXT," +
                    COLUMN_TEMP + " TEXT," +
                    COLUMN_PATH + " TEXT," +
                    COLUMN_USER + " TEXT," +
                    COLUMN_HOUR + " TEXT," +
                    COLUMN_UPLOADED + " TEXT)";


    public RecordsOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    public RecordsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_RECORDS_CREATE);
        System.out.println("DATABASE CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }
}
