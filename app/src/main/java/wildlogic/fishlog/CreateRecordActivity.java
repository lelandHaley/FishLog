package wildlogic.fishlog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.app.Activity;
import android.os.Bundle;


import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class CreateRecordActivity extends AppCompatActivity {
    //String url="jdbc:mysql://localhost:3306/DBNAME";
   // private String url="jdbc:mysql://192.168.0.6:8889/Fishlog";
    private String url="jdbc:mysql://localhost:8889/Fishlog";
    private String driver="org.gjt.mm.mysql.Driver";
    private String username="uroot";//user must have read-write permission to Database
    private String password= "proot";//user password, possible security risk here
    String recLat, recLon;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    //private MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_record);

        //Intent intent = getIntent();
       // Bundle extras = getIntent().getExtras().getBundle("pictureData");
       // Bitmap imageBitmap = (Bitmap) extras.get("data");

        //Bitmap imageBitmap = (Bitmap) getIntent().getExtras().get("pictureData");
        File tempImage = (File)  getIntent().getExtras().get("pictureData");
        recLat = (String)  getIntent().getExtras().get("recLat");
        recLon = (String)  getIntent().getExtras().get("recLon");
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
             bitmap = BitmapFactory.decodeStream(new FileInputStream(tempImage), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Spinner speciesSpinner = (Spinner) findViewById(R.id.speciesSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fish_species_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speciesSpinner.setAdapter(adapter);
        ImageView img= (ImageView) findViewById(R.id.picture_preview_view);
        img.setImageBitmap(bitmap);
        EditText nameField = (EditText)findViewById(R.id.nameField);
        nameField.setText(tempImage.getName());

      //  EditText nameField = (EditText)findViewById(R.id.name);
//        mapView = (MapView) findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(MapboxMap mapboxMap) {
//
//                // Customize map with markers, polylines, etc.
//
//            }
//        });
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                System.out.println("data is not null in create record");

            }
        }

    }
    public void createRecord(View view) {

    System.out.println("in createRecord");

//        HttpClient httpclient = HttpClients.createDefault();
//        HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");
//
//// Request parameters and other properties.
//        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
//        params.add(new BasicNameValuePair("param-1", "12345"));
//        params.add(new BasicNameValuePair("param-2", "Hello!"));
//        try {
//            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//        }
//        catch(java.io.UnsupportedEncodingException e){
//            System.out.println("UnsupportedEncodingException: " + e.getLocalizedMessage());
//        }
////Execute and get the response.
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity entity = response.getEntity();
//
//        if (entity != null) {
//            InputStream instream = entity.getContent();
//            try {
//                // do something useful
//            } finally {
//                try {
//                    instream.close();
//                }catch(java.io.IOException e){
//                    System.out.println("IOException: " + e.getLocalizedMessage());
//                }
//            }
//        }

//        try{
//
//            Class.forName(driver).newInstance();  //loads driver
//            Connection con = DriverManager.getConnection(url,username,password);
//
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//        boolean flag = false;
//        PreparedStatement myStmt = null;
//        ResultSet myRs = null;
//        Connection con = null;
//        Spinner speciesSpinner = (Spinner)findViewById(R.id.speciesSpinner);
//        EditText nameField = (EditText)findViewById(R.id.nameField);
//        String recName = nameField.getText().toString();
//        String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
//        EditText lureField = (EditText)findViewById(R.id.lureField);
//        EditText weatherField = (EditText)findViewById(R.id.weatherField);
//        //prepare statement
//        String sql ="insert into Records(name, lat, lon, lure, weather, species) values (?,?,?,?,?,?)";
//
//        try {
//            //connect to database
////            Connection myConn = Connect.getConnection();
//
//            Class.forName(driver).newInstance();//loading driver
//            con = DriverManager.getConnection(url,username,password);
//            myStmt = con.prepareStatement(sql);
//
//            //set params
//            myStmt.setString(1, recName);
//            myStmt.setFloat(2, Float.valueOf(recLat));
//            myStmt.setFloat(3, Float.valueOf(recLon));
//            myStmt.setString(4, lureField.getText().toString());
//            myStmt.setString(5, weatherField.getText().toString());
//            myStmt.setString(6, speciesSpinner.getSelectedItem().toString());
//
//            //execute the statement
//            flag = (myStmt.executeUpdate()>=0);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            releaseConn(myRs, myStmt, con);
//        }
//
//        //if flag == true
//        System.out.println("Saving record");


    }

    public static void releaseConn(ResultSet rs, Statement stmt, Connection conn) {
   //     if (rs != null && stmt != null && conn != null) {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
 //       }
    }
}