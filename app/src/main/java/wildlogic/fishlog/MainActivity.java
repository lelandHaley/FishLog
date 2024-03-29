package wildlogic.fishlog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GET_RECORDS = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CREATE_RECORD = 1;
    static final int REQUEST_GET_WEATHER = 1;
    static final int REQUEST_MANAGE_FRIENDS = 1;
    static final int REQUEST_VIEW_MAP = 1;

    private String urlString = "http://138.49.101.89:80/FishLogServlet/DBConectionServlet";//virtual server


    public String mCurrentPhotoPath;


    private String locLat, locLong;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private File tempImage;
    private Dialog loginDialog;
    private Dialog registerDialog;
    String curUser, curLure = "";
    private Record[] recordsToSend;
    DBHandler dbHandler;

    private static final String PREFS = "prefFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the user interface layout for this Activity
        // The layout file is defined in the project res/layout/main_activity.xml file
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            boolean check = mGoogleApiClient.isConnected();
            System.out.println(check);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            locLat = String.valueOf(location.getLatitude());
            locLong = String.valueOf(location.getLongitude());
            System.out.println("Device latitude is : " + locLat);
            System.out.println("Device longitude is : " + locLong);
        }
        if (mLastLocation != null) {
            locLat = String.valueOf(mLastLocation.getLatitude());
            locLong = String.valueOf(mLastLocation.getLongitude());
            System.out.println("Device latitude is : " + locLat);
            System.out.println("Device longitude is : " + locLong);
        }


        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        curUser = settings.getString("CurrentUser", "");
        if (curUser.equals("")) {
            callLoginDialog();
        }

        System.out.println(mGoogleApiClient.toString());
        dbHandler = new DBHandler(this);
    }

    public void uploadRecords() {
        recordsToSend = dbHandler.getRecords();
        for (Record r : recordsToSend) {
            String[] parameters = new String[12];
            parameters[0] = "uploadRecord";
            parameters[1] = r.getName();
            parameters[2] = r.getLatitude();
            parameters[3] = r.getLongitude();
            parameters[4] = r.getLure();
            parameters[5] = r.getWeather();
            parameters[6] = r.getSpecies();
            parameters[7] = r.getTime();
            parameters[8] = r.getTemperature();
            parameters[9] = r.getPath();
            parameters[10] = r.getUser();
            parameters[11] = r.getHour();

            networkConnection c = new networkConnection(this);
            String response = String.valueOf(c.execute(parameters));
        }
    }

    public void viewMap(View view) {

        if (isThereReception()) {
            System.out.println("viewing map");
            uploadRecords();
            Intent i = new Intent(getApplicationContext(), MapActivity.class);
            i.putExtra("curUser", curUser);
            i.putExtra("curLat", locLat);
            i.putExtra("curLon", locLong);

            startActivityForResult(i, REQUEST_VIEW_MAP);
        } else {
            displayPopupMessage("No Data Connection, Cannot View Map");
        }
    }


    public void manageFreinds(View view) {
        System.out.println("Going to manage friends page");

        if (isThereReception()) {
            uploadRecords();
            Intent i = new Intent(getApplicationContext(), ManageFriendsActivity.class);
            i.putExtra("curUser", curUser);
            startActivityForResult(i, REQUEST_MANAGE_FRIENDS);
        } else {
            displayPopupMessage("No Data Connection, Cannot Manage Friends");
        }

    }


    public void createRecord(View view) {
        System.out.println("Taking picture");
        dispatchTakePictureIntent();
    }

    public void getWeather(View view) {
        if (isThereReception()) {
            uploadRecords();
            System.out.println("Getting Weather");
            Intent i = new Intent(getApplicationContext(), GetWeatherActivity.class);
            i.putExtra("recLat", locLat);
            i.putExtra("recLon", locLong);
            startActivityForResult(i, REQUEST_GET_WEATHER);
        } else {
            displayPopupMessage("No Data Connection, Cannot View Weather");
        }
    }

    public void getRecords(View view) {
        if (isThereReception()) {
            uploadRecords();

            System.out.println("Getting Records");
            Intent i = new Intent(getApplicationContext(), GetRecordsActivity.class);
            i.putExtra("user", curUser);
            i.putExtra("recLat", locLat);
            i.putExtra("recLon", locLong);
            startActivityForResult(i, REQUEST_GET_RECORDS);
        } else {
            displayPopupMessage("No Data Connection, Cannot View Saved Records");
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        System.out.println("Environment.DIRECTORY_PICTURES is " + Environment.getExternalStorageDirectory());
        System.out.println("getExternalFilesDir is " + getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        if (storageDir.exists()) {
            System.out.println("file exists");
        }
        System.out.println("Path is : " + image.getAbsolutePath());
        tempImage = image;
        return image;
    }

    private void callLoginDialog() {
        loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.form_login);
        loginDialog.setCancelable(false);
        TextView titleView = (TextView) loginDialog.findViewById(android.R.id.title);
        titleView.setGravity(Gravity.CENTER);
        loginDialog.setTitle(getString(R.string.login_banner));
        loginDialog.show();
    }

    private void processResult(String message, String username) {
        AlertDialog infoDialog = new AlertDialog.Builder(MainActivity.this).create();

        infoDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (message.equals("0")) {
            infoDialog.setMessage("Success, You Should Be Receiving A Confirmation Email Shortly");
            curUser = username;
            registerDialog.dismiss();
        } else if (message.equals("1")) {
            infoDialog.setMessage("The Provided Username Already Exists In The Database");
            infoDialog.setTitle(getString(R.string.error));
        } else if (message.equals("2") || message.equals("4")) {
            infoDialog.setMessage("The User Could Not Be Added");
        } else if (message.equals("3")) {
            infoDialog.setMessage("Something Went Wrong While Checking If Username Was Taken");
        } else if (message.equals("5")) {
            infoDialog.setMessage("Something Went Wrong While Checking If Email Was Taken");
        } else if (message.equals("6")) {
            infoDialog.setMessage("The Provided Email Already Exists In Our Records");
        }
        infoDialog.show();
    }

    private void processLoginResult(String message, String user) {
        AlertDialog infoDialog = new AlertDialog.Builder(MainActivity.this).create();

        infoDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (message.equals("0")) {
            infoDialog.setMessage("Login Successfull");
            curUser = user;
            SharedPreferences settings = getSharedPreferences(PREFS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("CurrentUser", curUser);
            // Commit the edits
            editor.commit();
            loginDialog.dismiss();
        } else if (message.equals("1")) {
            infoDialog.setMessage("Username Does Not Match Password");
            infoDialog.setTitle(getString(R.string.error));
        } else if (message.equals("2")) {
            infoDialog.setTitle(getString(R.string.error));
            infoDialog.setMessage("Something Went Wrong While Connecting To Server");
        } else if (message.equals("3")) {
            infoDialog.setTitle(getString(R.string.error));
            infoDialog.setMessage("Something Went Wrong While Checking If Username And Password Match");
        }
        infoDialog.show();
    }

    public void registerNewUser(View view) {

        System.out.println("from register new user");
        EditText usernameEditText = (EditText) registerDialog.findViewById(R.id.registerUsername);
        EditText passwordEditText = (EditText) registerDialog.findViewById(R.id.registerPassword);
        EditText confirmPasswordEditText = (EditText) registerDialog.findViewById(R.id.registerConfirmPassword);
        EditText emailEditText = (EditText) registerDialog.findViewById(R.id.registerEmail);

        String usernameInput = String.valueOf(usernameEditText.getText());
        String passwordInput = String.valueOf(passwordEditText.getText());
        String confirmPasswordInput = String.valueOf(confirmPasswordEditText.getText());
        String emailInput = String.valueOf(emailEditText.getText());
        boolean errorState = false;
        String errorMessage = "";
        if (usernameInput.length() == 0) {
            errorState = true;
            errorMessage = getString(R.string.username_length_error);
        }
        else if (!passwordInput.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$")) { // quotes or \" around edges may cause error
            errorState = true;
            errorMessage = getString(R.string.password_format_error);
        } else if (!confirmPasswordInput.equals(passwordInput)) {
            errorState = true;
            errorMessage = getString(R.string.password_match_error);
        } else if (!emailInput.matches("[\\da-zA-Z.!#$%&'*+\\-\\/=?^_`{|}~;]{1,}@[\\da-zA-Z]{1,}\\.[\\da-zA-Z\\-]{1,}")) {
            errorState = true;
            errorMessage = getString(R.string.email_format_error);
        }
        if (errorState) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.error));
            alertDialog.setMessage(errorMessage);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            String[] parameters = new String[4];
            parameters[0] = "createUser";
            parameters[1] = usernameInput;
            parameters[2] = passwordInput;
            parameters[3] = emailInput;
            networkConnection c = new networkConnection(this);
            c.execute(parameters);
        }


    }

    public void navigeteToNewUserView(View view) {

        System.out.println("from navigeteToNewUserView");
        System.out.println("from login");
        if (isThereReception()) {
            loginDialog.dismiss();
            registerDialog = new Dialog(this);
            registerDialog.setContentView(R.layout.form_register);
            registerDialog.setCancelable(false);
            registerDialog.setTitle(getString(R.string.register_banner));
            TextView titleView = (TextView) registerDialog.findViewById(android.R.id.title);
            titleView.setGravity(Gravity.CENTER);
            registerDialog.show();
        } else {
            displayPopupMessage("No Data Connection, Cannnot Create New User");
        }

    }

    public void logIn(View view) {
        if(isThereReception()){
            EditText usernameEditText = (EditText) loginDialog.findViewById(R.id.loginUsername);
            EditText passwordEditText = (EditText) loginDialog.findViewById(R.id.loginPassword);
            String usernameInput = String.valueOf(usernameEditText.getText());
            String passwordInput = String.valueOf(passwordEditText.getText());
            String[] parameters = new String[3];
            parameters[0] = "checkLogin";
            parameters[1] = usernameInput;
            parameters[2] = passwordInput;
            networkConnection c = new networkConnection(this);
            c.execute(parameters);
        } else {
            displayPopupMessage("No Data Connection, Cannnot Log In");
        }

    }

    public void logout(View view) {
        curUser = "";
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("CurrentUser", curUser);
        // Commit the edits
        editor.commit();
        callLoginDialog();
    }


    private void dispatchTakePictureIntent() {
        //  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                System.out.println("photoFile created");
            } catch (IOException ex) {
                System.out.print("IOException caught");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = null;
                photoURI = FileProvider.getUriForFile(this,
                        "wildlogic.fishlog.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }

            }
        }
    }

    private void displayPopupMessage(String message) {
        runOnUiThread(new PopupDisplay(message, this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1777) {
            //Get our saved file into a bitmap object:
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        }
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_TAKE_PHOTO) && resultCode == RESULT_OK) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;



           boolean hasReception = isThereReception();

            Intent i = new Intent(getApplicationContext(), CreateRecordActivity.class);
            i.putExtra("pictureData", tempImage);
            i.putExtra("recLat", locLat);
            i.putExtra("recLon", locLong);
            i.putExtra("curUser", curUser);
            i.putExtra("curLure", curLure);
            i.putExtra("hasReception", hasReception);
            startActivityForResult(i, REQUEST_CREATE_RECORD);

        }
    }

    public boolean isThereReception() {
        ConnectivityManager conn = (ConnectivityManager)
                this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (networkInfo != null) {
            return true;

        } else {
            return false;
        }
    }

    protected void onStart() {
        System.out.println("in onStart");
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        System.out.println("in onStop");
        mGoogleApiClient.disconnect();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
//        SharedPreferences settings = getSharedPreferences(PREFS, 0);
//        SharedPreferences.Editor editor = settings.edit();
//        //editor.putBoolean("silentMode", true);
//        editor.putString("CurrentUser", curUser);
//        // Commit the edits
//        editor.commit();

        super.onStop();
    }

    protected void onPause() {
        System.out.println("in onPause");
        super.onPause();
    }

    protected void onResume() {
        System.out.println("in onResume");
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        // Stop method tracing that the activity started during onCreate()
        // android.os.Debug.stopMethodTracing();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("in OnConnected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            System.out.println("Device latitude is : " + locLat);
            System.out.println("Device longitude is : " + locLong);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (mCurrentPhotoPath != null) {
            System.out.println(mCurrentPhotoPath);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
    }

    private class networkConnection extends AsyncTask<String, Void, String> {

        private static final String OPEN_WEATHER_MAP_URL =
                "https://api.darksky.net/forecast/5411c439cc32573f9f153a02d54a19a4/%s,%s";

        private static final String OPEN_WEATHER_MAP_API = "====== YOUR OPEN WEATHER MAP API ======";
        HttpURLConnection conn = null;
        HttpClient httpClient = new DefaultHttpClient();
        private MainActivity parent;

        public networkConnection(MainActivity m) {
            parent = m;
        }

        @Override
        protected String doInBackground(String[] params) {//doInBackground(String[] params) {
            int status = 0;
            if (params[0].equals("createUser")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "createUser");
                    params1.put("uname", params[1]);
                    params1.put("pword", params[2]);
                    params1.put("email", params[3]);

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
                    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    status = conn.getResponseCode();
                    String responseString = "";
                    for (int c; (c = in.read()) >= 0; ) {
                        System.out.print((char) c);
                        responseString += (char) c;
                    }
                    final String ops[] = responseString.split(".*:");
                    final String uname = params[1];
                    if (ops[1] != null) {
                        parent.runOnUiThread(new Runnable() {
                            public void run() {
                                processResult(ops[1],uname);
                            }
                        });
                    }
                    System.out.println("Response code is " + status);
                } catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
                return "" + status;
            } else if (params[0].equals("checkLogin")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>(); ////
                    params1.put("func", "checkLogin");                   // perhaps make this entire request sending area one function
                    params1.put("uname", params[1]);
                    params1.put("pword", params[2]);
                    final String uname = params[1];
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
                    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    status = conn.getResponseCode();
                    String responseString = "";
                    for (int c; (c = in.read()) >= 0; ) {
                        System.out.print((char) c);
                        responseString += (char) c;
                    }
                    final String ops[] = responseString.split(".*:");
                    if (ops[1] != null) {
                        parent.runOnUiThread(new Runnable() {
                            public void run() {
                                processLoginResult(ops[1], uname);
                            }
                        });
                    }
                    System.out.println("Response code is " + status);
                } catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.toString());
                }
                return "" + status;

            } else if (params[0].equals("uploadRecord")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>(); ////
                    params1.put("func", "uploadRecord");                   // perhaps make this entire request sending area one function
                    params1.put("name", params[1]);
                    params1.put("lat", params[2]);
                    params1.put("lon", params[3]);                   // perhaps make this entire request sending area one function
                    params1.put("lure", params[4]);
                    params1.put("weather", params[5]);
                    params1.put("species", params[6]);                   // perhaps make this entire request sending area one function
                    params1.put("time", params[7]);
                    params1.put("temp", params[8]);
                    params1.put("path", params[9]);                   // perhaps make this entire request sending area one function
                    params1.put("user", params[10]);
                    params1.put("hour", params[11]);


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
                    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    status = conn.getResponseCode();
                    String responseString = "";
                    for (int c; (c = in.read()) >= 0; ) {
                        System.out.print((char) c);
                        responseString += (char) c;
                    }
                    final String ops[] = responseString.split("Response:");
                    if (ops.length > 1) {
                        if (ops[1].equals("true\"")) {
                            Record r = new Record(params[1], params[2], params[3],
                                    params[4], params[5], params[6], params[7],
                                    params[8], params[10], params[9], params[11]);
                            dbHandler.changeRecordToUploaded(r);

                        }

                    }
                    System.out.println("Response code is " + status);
                } catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
                return "" + status;

            }
            return "end of function";
        }

        @Override
        protected void onPostExecute(String message) {
        }
    }


}