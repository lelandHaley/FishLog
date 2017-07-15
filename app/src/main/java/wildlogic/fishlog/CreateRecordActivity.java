package wildlogic.fishlog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.app.Activity;
import android.os.Bundle;


//import com.android.internal.http.multipart.MultipartEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.client.volley.WeatherClientDefault;


import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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


public class CreateRecordActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    //String url="jdbc:mysql://localhost:3306/DBNAME";
    //private String urlString="jdbc:mysql://192.168.0.6:8889/Fishlog";
    private String urlString = "http://192.168.0.6:8080/FishLogServlet/DBConectionServlet";
    //private String urlString = "jdbc:mysql://localhost:8889/Fishlog";
    private String driver = "org.gjt.mm.mysql.Driver";
    private String username = "uroot";//user must have read-write permission to Database
    private String password = "proot";//user password, possible security risk here
    String recLat, recLon;
    Bitmap recordImage = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    WeatherClient wClient = WeatherClientDefault.getInstance();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


//    wClient.init(this.Context);
//    WeatherConfig config = new WeatherConfig();
//    config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
//    config.lang = "en"; // If you want to use english
//    config.maxResult = 5; // Max number of cities retrieved
//    config.numDays = 6; // Max num of days in the forecast


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        recLat = Double.toString(location.getLatitude());
        recLon = Double.toString(location.getLongitude());

    }

    //private MapView mapView;
    private class networkConnection extends AsyncTask<String, Void, String> {

                //"http://api.openweathermap.org/data/2.5/weather?lat=%slon=%sunits=metric";
               // https://api.darksky.net/forecast/5411c439cc32573f9f153a02d54a19a4/37.8267,-122.4233
                private static final String OPEN_WEATHER_MAP_URL =
        "https://api.darksky.net/forecast/5411c439cc32573f9f153a02d54a19a4/%s,%s";

        private static final String OPEN_WEATHER_MAP_API = "====== YOUR OPEN WEATHER MAP API ======";

//        @Override
//        protected String doInBackground(String[] params) {
////            return "returnvalue";
////        }
////
////
////        protected String insertRecord(String[] params) {
//
//            System.out.println("in networkConnection.insertRecord");
//            //Connection con = null;
//            HttpURLConnection conn = null;
//            HttpClient httpClient = new DefaultHttpClient();
//            //HttpPost httpPost = new HttpPost(Utility.AddProductWS);
//            try {
//                URL url = new URL(urlString);
//                Map<String, Object> params1 = new LinkedHashMap<>();
//                params1.put("func", "insertRecord");
//                params1.put("recName", params[1]);
//                params1.put("recLat", params[2]);
//                params1.put("recLon", params[3]);
//                params1.put("recLure", params[4]);
//                params1.put("recWeather", params[5]);
//                params1.put("recSpecies", params[6]);
//                params1.put("recImage", recordImage);
//                params1.put("recImage", params[7]);
//
//
////                params1.put("function", "insertRecord");
////                params1.put("recName", recName);
////                params1.put("recLat", recLat);
////                params1.put("recLon", recLon);
////                params1.put("recLure", recLure);
////                params1.put("recWeather", recWeather);
////                params1.put("recSpecies", recSpecies);
//                // params.put("message", "Shark attacks in Botany Bay have gotten out of control. We need more defensive dolphins to protect the schools here, but Mayor Porpoise is too busy stuffing his snout with lobsters. He's so shellfish.");
//
//                StringBuilder postData = new StringBuilder();
//                for (Map.Entry<String, Object> param : params1.entrySet()) {
//                    if (postData.length() != 0) postData.append('&');
//                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
//                    postData.append('=');
//                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
//                }
//                byte[] postDataBytes = postData.toString().getBytes("UTF-8");
//
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//                conn.setDoOutput(true);
//                conn.getOutputStream().write(postDataBytes);
//                //Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//                int status = conn.getResponseCode();
////                for (int c; (c = in.read()) >= 0; )
////                    System.out.print((char) c);
//                System.out.println("Response code is " + status);
//            } catch (Exception e) {
//                System.out.println("exception in createRecord: " + e.getMessage());
//        }
//            return "some message";
//        }

        public JSONObject getWeatherJSON(String lat, String lon) {
            JSONObject data = null;
            try {
                URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon));
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                //connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);
                //connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
                //connection.setRequestProperty("Accept","*/*");

                connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
                connection.setRequestProperty("Accept","*/*");
                connection.setDoOutput(false);

                int status = connection.getResponseCode();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp = "";
                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();

                 data = new JSONObject(json.toString());

                // This value will be 404 if the request was not
                // successful
//                if (data.getInt("cod") != 200) {
                if (status != 200) {
                    return null;
                }

                return data;
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
            return data;
        }


        @Override
        protected String doInBackground(String[] params) {
//            return "returnvalue";
//        }
//
//
//        protected String insertRecord(String[] params) {

//            System.out.println("in networkConnection.insertRecord");
//            //Connection con = null;
//            HttpURLConnection conn = null;
//            HttpClient httpClient = new DefaultHttpClient();
//            //HttpPost httpPost = new HttpPost(Utility.AddProductWS);
//            HttpClient client = new DefaultHttpClient();
//            HttpPost postMethod = new HttpPost("http://localhost/Upload/index.php");
//            File file = new File(filePath);
//            MultipartEntity entity = new MultipartEntity();
//            FileBody contentFile = new FileBody(file);
//            entity.addPart("userfile",contentFile);
//            StringBody contentString = new StringBody("This is contentString");
//            entity.addPart("contentString",contentString);
//
//            postMethod.setEntity(entity);
//            client.execute(postMethod);

            HttpPost httppost = new HttpPost(urlString);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

/* example for setting a HttpMultipartMode */
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


/* example for adding an image part */
            FileBody fileBody = new FileBody(new File(recordImage.toString())); //image should be a String
            //builder.addPart("my_file", fileBody);

            try {
                StringBody contentString = new StringBody("This is contentString");
            } catch (Exception e) {
                System.out.println("Exception " + e.getMessage());
            }


            // builder.addTextBody("func", "insertRecord");
            // builder.addTextBody("recName", params[1]);
            // builder.addTextBody("recLat", params[2]);
            // builder.addTextBody("recLon", params[3]);
            // builder.addTextBody("recLure", params[4]);
            // builder.addTextBody("recWeather", params[5]);
            // builder.addTextBody("recSpecies", params[6]);
            builder.addPart("recImage", fileBody);
            //builder.addTextBody("recImage", params[7]);

            // HttpEntity entity = builder.build();
            // httppost.setEntity(entity);
            // CloseableHttpClient httpclient = HttpClients.createDefault();


            JSONObject jsonWeather = null;
            JSONObject currentCond = null;
            String currTemp = "";
            String summary = "";
            try {
                jsonWeather = getWeatherJSON(params[2], params[3]);
                currentCond = jsonWeather.getJSONObject("currently");
                currTemp = currentCond.getString("temperature");
                summary = currentCond.getString("summary");


            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }

            try {
                //     httpclient.execute(httppost);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }


//            HttpPost postMethod = new HttpPost(urlString);
//            HttpClient client = new DefaultHttpClient();
//            //File file = new File(filePath);
//           // MultipartEntity entity = new MultipartEntity();
//            //F//ileBody contentFile = new FileBody(file);
//           // entity.addPart("userfile",contentFile);
//            try {
//            StringBody contentString = new StringBody("This is contentString");
//                builder.addPart("contentString",contentString);
//
//                HttpEntity entity = builder.build();
//
//                client.execute(postMethod);
//            }catch(Exception e){
//                System.out.println();
//            }


            //  try {
//                URL url = new URL(urlString);
//                Map<String, Object> params1 = new LinkedHashMap<>();
//                params1.put("func", "insertRecord");
//                params1.put("recName", params[1]);
//                params1.put("recLat", params[2]);
//                params1.put("recLon", params[3]);
//                params1.put("recLure", params[4]);
//                params1.put("recWeather", params[5]);
//                params1.put("recSpecies", params[6]);
//                params1.put("recImage", recordImage);
//                params1.put("recImage", params[7]);


//                params1.put("function", "insertRecord");
//                params1.put("recName", recName);
//                params1.put("recLat", recLat);
//                params1.put("recLon", recLon);
//                params1.put("recLure", recLure);
//                params1.put("recWeather", recWeather);
//                params1.put("recSpecies", recSpecies);
            // params.put("message", "Shark attacks in Botany Bay have gotten out of control. We need more defensive dolphins to protect the schools here, but Mayor Porpoise is too busy stuffing his snout with lobsters. He's so shellfish.");
//
//                StringBuilder postData = new StringBuilder();
//                for (Map.Entry<String, Object> param : params1.entrySet()) {
//                    if (postData.length() != 0) postData.append('&');
//                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
//                    postData.append('=');
//                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
//                }
//                byte[] postDataBytes = postData.toString().getBytes("UTF-8");
//
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//                conn.setDoOutput(true);
//                conn.getOutputStream().write(postDataBytes);
//                //Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//                int status = conn.getResponseCode();
////                for (int c; (c = in.read()) >= 0; )
////                    System.out.print((char) c);
//                System.out.println("Response code is " + status);
//            } catch (Exception e) {
//                System.out.println("exception in createRecord: " + e.getMessage());
//            }
            return "some message";
        }


        public void executeMultipartPost() throws Exception {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                recordImage.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] data = bos.toByteArray();
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(
                        "http://10.0.2.2/cfc/iphoneWebservice.cfc?returnformat=json&amp;method=testUpload");
                ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");
                // File file= new File("/mnt/sdcard/forest.png");
                // FileBody bin = new FileBody(file);
                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("uploaded", bab);
                reqEntity.addPart("photoCaption", new StringBody("sfsdfsdf"));
                postRequest.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                System.out.println("Response: " + s);
            } catch (Exception e) {
                // handle exception here
                System.out.println("exception in createRecord: " + e.getMessage());
            }
        }




        @Override
        protected void onPostExecute(String message) {
            //process message
        }
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_record);
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
//        System.out.println(mGoogleApiClient.toString());
//        this.getLocation();
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
        recordImage = bitmap;
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
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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


       ImageView img = (ImageView) findViewById(R.id.picture_preview_view);
       Bitmap recordPic = ((BitmapDrawable)img.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

       // recordPic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);

        //try {
            //URL url = new URL(urlString);
            String[] parameters = new String[8];
        //String[] parameters = new String[7];
            Map<String, Object> params = new LinkedHashMap<>();
            parameters[0]= "insertRecord";
            parameters[1]=  recName;
            parameters[2]=  recLat;
            parameters[3]= recLon;
            parameters[4]= recLure;
            parameters[5]= recWeather;
            parameters[6]= recSpecies;
            parameters[7]= encodedBytes;
            networkConnection c = new networkConnection();
            c.execute(parameters);

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       // mGoogleApiClient.connect();
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
       // AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
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
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
       // mGoogleApiClient.disconnect();
    }
}