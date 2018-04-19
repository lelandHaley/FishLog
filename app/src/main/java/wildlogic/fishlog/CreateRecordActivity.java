package wildlogic.fishlog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SpinnerAdapter;


//import com.android.internal.http.multipart.MultipartEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
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
import com.google.gson.Gson;
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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
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
import java.util.concurrent.Semaphore;


public class CreateRecordActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    //String url="jdbc:mysql://localhost:3306/DBNAME";
    //private String urlString="jdbc:mysql://192.168.0.6:8889/Fishlog";
    private String urlString = "http://192.168.1.6:8080/FishLogServlet/DBConectionServlet"; //home
    //private String urlString = "http://192.168.1.13:8080/FishLogServlet/DBConectionServlet"; //tylers
    //private String urlString = "http://138.49.3.45:8080/FishLogServlet/DBConectionServlet";//not sier what this one is
    //private String urlString = "http://138.49.101.89:80/FishLogServlet/DBConectionServlet"; // virtual server
    private String driver = "org.gjt.mm.mysql.Driver";
    //private String username = "uroot";//user must have read-write permission to Database
   // private String password = "proot";//user password, possible security risk here
    private String username = "root";//virtual account
    private String password = "root";
    private final String BROADCAST = this.getPackageName() + ".android.action.broadcast";
    String recLat, recLon;
    String curUser = "", curLure = "", recordName = "";
    Bitmap recordImage = null;
    String filePath = "";
    boolean saved = false;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    WeatherClient wClient = WeatherClientDefault.getInstance();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    final Semaphore mutex = new Semaphore(0);
    private static final String RECORD_SETTINGS = "lureSettings";
    private Dialog workingDialog;
    private boolean hasReception;
    private DBHandler dbHandler;
  //  private myBroadcastReceiver myReceiver;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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

        HttpURLConnection conn = null;
        private CreateRecordActivity parentActivity = null;

        public networkConnection(CreateRecordActivity parentActivity) {
            this.parentActivity = parentActivity;
        }

        public JSONObject getWeatherJSON(String lat, String lon) {
            JSONObject data = null;
            try {
                URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon));
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                connection.setRequestProperty("Accept", "*/*");
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
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return data;
        }


        @Override
        protected String doInBackground(String[] params) {
            String function = params[0];
            if(function.equals("getWeather")){
               JSONObject jsonWeather = getWeatherJSON(params[1], params[2]);
                parentActivity.setWeatherSpinnerSelection(jsonWeather);
                return "SUCCESS";

            }else if(function.equals("insertRecord")) {
                String response = "";
                JSONObject jsonWeather = null;
                JSONObject currentCond = null;
                String currTemp = "";
                String summary = "";
                //filePath = Environment.getExternalStorageDirectory() + File.separator + recordName;
                try {
                    jsonWeather = getWeatherJSON(params[2], params[3]);
                    currentCond = jsonWeather.getJSONObject("currently");
                    currTemp = currentCond.getString("temperature");
                    summary = currentCond.getString("summary");


                } catch (Exception e) {
                    Log.d("Error", "Cannot process JSON results", e);
                }

                try {
                    //  httpclient.execute(httppost);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }


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
                    params1.put("curUser", params[7]);
                    params1.put("recTemp", currTemp);
                    params1.put("recPath", filePath);

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
                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {//
                        String line;
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line=br.readLine()) != null) {
                            response+=line;
                        }
                    }
                    else {
                        response="noConnection";
                    }
                } catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
                System.out.println(response);

                parentActivity.setcreateRecordResponse(response);
                return response;
            }
            else {
               return "unknown  function";
            }
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
        }
    }


    private void setcreateRecordResponse(String response) {
        //Looper.prepare();
// go back to main activity, display message, after addition of saving... dialog, remove dialog
       // workingDialog.dismiss();
        String success = "\"success\"";
        String noConn = "\"noConnection\"";
        if(response.contains(success)){

            //create a file to write bitmap data
            //File file = new File(recName);
            try {
                //create a file to write bitmap data
                File file = new File(filePath);
                file.createNewFile();

                //Convert bitmap to byte array
                //Bitmap bitmap = your bitmap;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                recordImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();


            }catch(java.io.IOException ioe){
                System.out.println(ioe.getMessage());
            }

            SharedPreferences settings = getSharedPreferences(RECORD_SETTINGS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("CurrentLure", curLure);
            editor.commit();
            saved = true;
            workingDialog.dismiss();
            runOnUiThread(new PopupDisplay("Fish Saved!", this, true));
           try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            finish();
        }else if(response.contains(noConn)){
            runOnUiThread(new PopupDisplay("Could Not Connect To Server", this));
            workingDialog.dismiss();
        }else {
            runOnUiThread(new PopupDisplay("Server Error", this));
            workingDialog.dismiss();
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


        workingDialog= new Dialog(this);
        workingDialog.getWindow().getCurrentFocus();
        workingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        workingDialog.setContentView(R.layout.form_dialog);
        workingDialog.setCancelable(false);
        workingDialog.setTitle("Working...");
        workingDialog.setOwnerActivity(this);


        File tempImage = (File) getIntent().getExtras().get("pictureData");
        recLat = (String) getIntent().getExtras().get("recLat");
        recLon = (String) getIntent().getExtras().get("recLon");
        curUser = (String) getIntent().getExtras().get("curUser");
        hasReception = (boolean) getIntent().getExtras().get("hasReception");


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

        Spinner weatherSpinner = (Spinner) findViewById(R.id.weatherSpinner);
        ArrayAdapter<CharSequence> weatherAdapter = ArrayAdapter.createFromResource(this,
                R.array.weather_display_array, android.R.layout.simple_spinner_item);
        weatherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weatherSpinner.setAdapter(weatherAdapter);

        if(hasReception) {
            networkConnection c = new networkConnection(this);
            String[] parameters = new String[3];
            parameters[0] = "getWeather";
            parameters[1] = recLat;
            parameters[2] = recLon;
            try {
                c.execute(parameters).get();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        ImageView img = (ImageView) findViewById(R.id.picture_preview_view);
        img.setImageBitmap(bitmap);
        recordImage = bitmap;
        EditText nameField = (EditText) findViewById(R.id.nameField);
        nameField.setText(tempImage.getName());
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        SharedPreferences recordPrefs = getSharedPreferences(RECORD_SETTINGS, 0);
        curLure = recordPrefs.getString("CurrentLure", "");
       if(!curLure.equals("")){
           EditText lureField = (EditText) findViewById(R.id.lureField);
           lureField.setText(curLure);
        }

        dbHandler = new DBHandler(this);
       // myReceiver = new myBroadcastReceiver(this);

    }

    private void setWeatherSpinnerSelection(JSONObject weatherObj) {
        Spinner weatherSpinner = (Spinner) findViewById(R.id.weatherSpinner);
        try {
            JSONObject currentCond = weatherObj.getJSONObject("currently");
            String curCond = currentCond.getString("icon");
            System.out.println("current Weather is : " + curCond);
            String setValue = "";
            if (curCond.equals("clear-day")) {
                setValue = "Clear";
            } else if (curCond.equals("clear-night")) {
                setValue = "Clear";
            } else if (curCond.equals("rain")) {
                setValue = "Rain";
            } else if (curCond.equals("snow")) {
                setValue = "Snow";
            } else if (curCond.equals("sleet")) {
                setValue = "Sleet";
            } else if (curCond.equals("wind")) {
                setValue = "Windy";
            } else if (curCond.equals("fog")) {
                setValue = "Fog";
            } else if (curCond.equals("cloudy")) {
                setValue = "Cloudy";
            } else if (curCond.equals("partly-cloudy-day")) {
                setValue = "Partly Cloudy";
            } else if (curCond.equals("partly-cloudy-night")) {
                setValue = "Partly Cloudy";
            }

            for (int i = 0; i < weatherSpinner.getCount(); i++) {
                String s = weatherSpinner.getItemAtPosition(i).toString();
                if (weatherSpinner.getItemAtPosition(i).equals(setValue)) {
                    weatherSpinner.setSelection(i);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("some error!!!!");
            if(null != e.getMessage()){
                System.out.println(e.getMessage());
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                System.out.println("data is not null in create record");
            }
        }
    }
    public void createRecord(View view) {

        System.out.println("in createRecord");

        workingDialog.show();

        Spinner speciesSpinner = (Spinner) findViewById(R.id.speciesSpinner);
        EditText nameField = (EditText) findViewById(R.id.nameField);
        String recName = nameField.getText().toString();
        EditText lureField = (EditText) findViewById(R.id.lureField);
        Spinner weatherSpinner = (Spinner) findViewById(R.id.weatherSpinner);
        String recLure = lureField.getText().toString();
        String recWeather = weatherSpinner.getSelectedItem().toString();
        String recSpecies = speciesSpinner.getSelectedItem().toString();
        curLure = recLure;
        filePath = Environment.getExternalStorageDirectory() + File.separator + recordName;


        Intent intent = new Intent(BROADCAST);
        Bundle extras = new Bundle();
        extras.putString("send_data", "test");
        intent.putExtras(extras);
        sendBroadcast(intent);

        if(hasReception) {

            String[] parameters = new String[9];
            //String[] parameters = new String[7];
            Map<String, Object> params = new LinkedHashMap<>();
            parameters[0] = "insertRecord";
            parameters[1] = recName;
            parameters[2] = recLat;
            parameters[3] = recLon;
            parameters[4] = recLure;
            parameters[5] = recWeather;
            parameters[6] = recSpecies;
            parameters[7] = curUser;
            //parameters[8] = encodedBytes;

            recordName = recName;
            networkConnection c = new networkConnection(this);
            c.execute(parameters);
        }else{
           // db.insert(dbhelper.getDatabaseName(),null,null);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
            String time = sdf.format(new Date());
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
            String hours = sdf2.format(new Date());
            Record r = new Record(recName, recLat, recLon, recLure, recWeather, recSpecies, time, "0.0", curUser, filePath, hours);
            long l = dbHandler.addUnsavedRecord(r);
            workingDialog.dismiss();
            if(l >= 0){
                displayPopupMessage("No Data Connection, Data Saved Locally Until Data Connection Is Regained");
                finish();
            }else{
                displayPopupMessage("No Data Connection, Error Saving Record Locally");
                finish();
            }

        }

    }

    private void displayPopupMessage(String message) {
        runOnUiThread(new PopupDisplay(message, this));
    }
    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction2 = Action.newAction(
                Action.TYPE_VIEW,
                "CreateRecord Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://wildlogic.fishlog/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction2);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction2 = Action.newAction(
                Action.TYPE_VIEW,
                "CreateRecord Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://wildlogic.fishlog/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction2);

        client.disconnect();
    }

    public class myBroadcastReceiver extends BroadcastReceiver {
//        CreateRecordActivity parent;
//
//        public myBroadcastReceiver(CreateRecordActivity a){
//            parent = a;
//        }
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn =  (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();
            if ( networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // Toast.makeText(context, "WIFI CONNECTED", Toast.LENGTH_LONG).show();
                hasReception = true;
            } else if (networkInfo != null) {
                //Toast.makeText(context, "MOBILE NETWORK CONNECTED", Toast.LENGTH_LONG).show();
                hasReception = true;
            } else {
                //Toast.makeText(context, "NO CONNECTION", Toast.LENGTH_LONG).show();
                hasReception = false;
            }
        }
    }
}