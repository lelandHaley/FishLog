package wildlogic.fishlog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.app.Activity;
import android.os.Bundle;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class CreateRecordActivity extends AppCompatActivity {
    //String url="jdbc:mysql://localhost:3306/DBNAME";
    //private String urlString="jdbc:mysql://192.168.0.6:8889/Fishlog";
    private String urlString="http://192.168.0.6:8080/FishLogServlet/DBConectionServlet";
    //private String urlString = "jdbc:mysql://localhost:8889/Fishlog";
    private String driver = "org.gjt.mm.mysql.Driver";
    private String username = "uroot";//user must have read-write permission to Database
    private String password = "proot";//user password, possible security risk here
    String recLat, recLon;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    //private MapView mapView;
    private class networkConnection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
//            return "returnvalue";
//        }
//
//
//        protected String insertRecord(String[] params) {

            System.out.println("in networkConnection.insertRecord");
            //Connection con = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urlString);
                Map<String, Object> params1 = new LinkedHashMap<>();
                params1.put("func", "insertRecord");
                params1.put("recName", params[1]);
                params1.put("recLat", params[2]);
                params1.put("recLon", params[3]);
                params1.put("recLure", params[4]);
                params1.put("recWeather", params[5]);
                params1.put("recSpecies", params[6]);
//                params1.put("function", "insertRecord");
//                params1.put("recName", recName);
//                params1.put("recLat", recLat);
//                params1.put("recLon", recLon);
//                params1.put("recLure", recLure);
//                params1.put("recWeather", recWeather);
//                params1.put("recSpecies", recSpecies);
                // params.put("message", "Shark attacks in Botany Bay have gotten out of control. We need more defensive dolphins to protect the schools here, but Mayor Porpoise is too busy stuffing his snout with lobsters. He's so shellfish.");

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params1.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                //Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                int status = conn.getResponseCode();
//                for (int c; (c = in.read()) >= 0; )
//                    System.out.print((char) c);
                System.out.println("Response code is " + status);
            } catch (Exception e) {
                System.out.println("exception in createRecord: " + e.getMessage());
            }
            return "some message";
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_record);

        //Intent intent = getIntent();
        // Bundle extras = getIntent().getExtras().getBundle("pictureData");
        // Bitmap imageBitmap = (Bitmap) extras.get("data");

        //Bitmap imageBitmap = (Bitmap) getIntent().getExtras().get("pictureData");
        File tempImage = (File) getIntent().getExtras().get("pictureData");
        recLat = (String) getIntent().getExtras().get("recLat");// consider moving lat and long retreval
        recLon = (String) getIntent().getExtras().get("recLon");// code to this location

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
        ImageView img = (ImageView) findViewById(R.id.picture_preview_view);
        img.setImageBitmap(bitmap);
        EditText nameField = (EditText) findViewById(R.id.nameField);
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        //Connection myConn = Connect.getConnection();
//
//            Class.forName(driver).newInstance();//loading driver
//            con = DriverManager.getConnection(url,username,password);
        System.out.println("in createRecord");
        boolean flag = false;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;
        Connection con = null;
        HttpURLConnection conn = null;
        Spinner speciesSpinner = (Spinner) findViewById(R.id.speciesSpinner);
        EditText nameField = (EditText) findViewById(R.id.nameField);
        String recName = nameField.getText().toString();
        String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        EditText lureField = (EditText) findViewById(R.id.lureField);
        EditText weatherField = (EditText) findViewById(R.id.weatherField);
        String recLure = lureField.getText().toString();
        String recWeather = weatherField.getText().toString();
        String recSpecies = speciesSpinner.getSelectedItem().toString();
//        try{
//
//            Class.forName(driver).newInstance();  //loads driver
//           con = DriverManager.getConnection(urlString,username,password);
//
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }

        //try {
            //URL url = new URL(urlString);
            String[] parameters = new String[7];
            Map<String, Object> params = new LinkedHashMap<>();
            parameters[0]= "insertRecord";
            parameters[1]=  recName;
            parameters[2]=  recLat;
            parameters[3]= recLon;
            parameters[4]= recLure;
            parameters[5]= recWeather;
            parameters[6]= recSpecies;
            networkConnection c = new networkConnection();
            c.execute(parameters);
            // params.put("message", "Shark attacks in Botany Bay have gotten out of control. We need more defensive dolphins to protect the schools here, but Mayor Porpoise is too busy stuffing his snout with lobsters. He's so shellfish.");

//            StringBuilder postData = new StringBuilder();
//            for (Map.Entry<String, Object> param : params.entrySet()) {
//                if (postData.length() != 0) postData.append('&');
//                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
//                postData.append('=');
//                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
//            }
//            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
//
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//            conn.setDoOutput(true);
//            conn.getOutputStream().write(postDataBytes);
//
//            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//
//            for (int c; (c = in.read()) >= 0; )
//                System.out.print((char) c);
//        } catch (Exception e) {
//            System.out.println("exception in createRecord: " + e.getMessage());
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CreateRecord Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://wildlogic.fishlog/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CreateRecord Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://wildlogic.fishlog/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}