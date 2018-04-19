package wildlogic.fishlog;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, CompoundButton.OnCheckedChangeListener {

    private GoogleApiClient client;
    private String urlString = "http://192.168.1.6:8080/FishLogServlet/DBConectionServlet";// home
    //private String urlString = "http://192.168.1.13:8080/FishLogServlet/DBConectionServlet"; //tylers
    // private String urlString = "http://192.168.3.61:8080/FishLogServlet/DBConectionServlet";// sipnsurf
    // private String urlString = "http://192.168.1.9:8080/FishLogServlet/DBConectionServlet";// javavino
    // private String urlString = "http://192.168.1.5:8080/FishLogServlet/DBConectionServlet";// javavino
    //private String urlString = "http://192.168.44.19:8080/FishLogServlet/DBConectionServlet";// rootnote
    //private String urlString = "http://138.49.3.45:8080/FishLogServlet/DBConectionServlet";
    //private String urlString = "http://138.49.101.89:80/FishLogServlet/DBConectionServlet";//virtual server
    private GoogleMap mMap;
    private String latitude = "0.0";
    private String longitude = "0.0";
    private Double lat = 0.0;
    private Double lon = 0.0;
    private Double viewMinLat = 0.0;
    private Double viewMinLon = 0.0;
    private Double viewMaxLat = 0.0;
    private Double viewMaxLon = 0.0;
    private String user = "";
    private Dialog filterDialog;
    private String filterSpecies= "All Species";
    private String filterWeather= "All Conditions";
    private String filterStartime= "0";
    private String filterEndtime= "24";
    private boolean filterShowFreindRecords = false;
    private boolean tempFilterShowFreindRecords = false;
    Record[] currentRecords = new Record[0];



    public void goBackToMain(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        user = (String) getIntent().getExtras().get("curUser");
        lat = Double.parseDouble((String) getIntent().getExtras().get("curLat"));
        lon = Double.parseDouble((String) getIntent().getExtras().get("curLon"));
    }

    public void recenterMap(View view){
        System.out.println("Recenter Clicked");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 13.0f));
    }
    public void refreshMapButtonClicked(View view){
        System.out.println("refresh clicked");
        refreshMap();
    }

    public void refreshMap(){
        System.out.println("refreshMap");
        VisibleRegion vRegion = mMap.getProjection().getVisibleRegion();
        LatLng southwest = vRegion.latLngBounds.southwest;
        LatLng northeast = vRegion.latLngBounds.northeast;

        viewMinLat = southwest.latitude;
        viewMaxLat = northeast.latitude;
        viewMinLon = northeast.longitude;
        viewMaxLon = southwest.longitude;

        if(viewMinLat > viewMaxLat){
            Double temp = viewMinLat;
            viewMinLat = viewMaxLat;
            viewMaxLat = temp;
        }
        if(viewMinLon > viewMaxLon){
            Double temp = viewMinLon;
            viewMinLon = viewMaxLon;
            viewMaxLon = temp;
        }

        networkConnection con = new networkConnection(this);
        String[] parameters = new String[11];
       // Record curRecord = currentRecords[currentDisplayedRecordIndex];
        parameters[0] = "getRecordsForMap";
        parameters[1] = user;
        parameters[2] = viewMinLat.toString();
        parameters[3] = viewMaxLat.toString();
        parameters[4] = viewMinLon.toString();
        parameters[5] = viewMaxLon.toString();
        parameters[6] = filterSpecies;
        parameters[7] = filterWeather;
        parameters[8] = filterStartime;
        parameters[9] = filterEndtime;
        parameters[10] = String.valueOf(filterShowFreindRecords); // view all user records

        //parameters[3] = filterSelection;
        con.execute(parameters);


    }

    public void cancelFilter(){
        System.out.println("cancelFilter clicked");
        filterDialog.dismiss();
    }

    public void saveFilter(){
        System.out.println("saveFilter clicked");
        Spinner speciesSpinner = (Spinner) filterDialog.findViewById(R.id.speciesSelectionSpinner);
        Spinner weatherSpinner = (Spinner) filterDialog.findViewById(R.id.weatherSelectionSpinner);
        EditText starttime = (EditText) filterDialog.findViewById(R.id.leftInputField);
        EditText endtime = (EditText) filterDialog.findViewById(R.id.rightInputField);
       // Switch viewFreinds = (Switch) filterDialog.findViewById(R.id.viewAllFreindsSwitch);
//        String tempSpecies = speciesSpinner.getSelectedItem().toString();
//        String tempWeather = weatherSpinner.getSelectedItem().toString();
        String tempStart = starttime.getText().toString();
        String tempEnd = endtime.getText().toString();
        if(tempStart.equals("")){
            tempStart = "0";
        }
        int start = Integer.parseInt(tempStart);
        int end = Integer.parseInt(tempEnd);

        if(start < 0 || start > 24 || end  < 0 || end > 24){
            displayPopupMessage("Start And End Time Must Be Between 0 And 24");
        }else if(start > end){
            displayPopupMessage("Start Time Must Be Before End Time");
        }else{
            filterShowFreindRecords = tempFilterShowFreindRecords;
            filterSpecies = speciesSpinner.getSelectedItem().toString();
            filterWeather = weatherSpinner.getSelectedItem().toString();
            filterStartime = starttime.getText().toString();
            filterEndtime = endtime.getText().toString();
            filterDialog.dismiss();
            refreshMap();
        }


    }

    public void changeFilterButtonClicked(View view) {
        System.out.println("changefilter clicked");
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 5.0f));
        callFilterDialog();
    }
    private void callFilterDialog() {

        filterDialog = new Dialog(this);
        filterDialog.setContentView(R.layout.map_filter_dialog);
        filterDialog.setCancelable(false);
        filterDialog.setTitle(getString(R.string.filter_map_title));
        filterDialog.show();

        Spinner speciesSpinner = (Spinner) filterDialog.findViewById(R.id.speciesSelectionSpinner);
        Spinner weatherSpinner = (Spinner) filterDialog.findViewById(R.id.weatherSelectionSpinner);
        EditText starttime = (EditText) filterDialog.findViewById(R.id.leftInputField);
        EditText endtime = (EditText) filterDialog.findViewById(R.id.rightInputField);

        starttime.setText(filterStartime);
        endtime.setText(filterEndtime);

        Resources res = getResources();
        String[] species = new String[10];
        int currentSpeciesIndex = 0;
        String[] species2 =  res.getStringArray(R.array.fish_species_array);
        for(int i = 9; i > 0; i --){
            species[i] = species2[i - 1];
            if(species[i].equalsIgnoreCase(filterSpecies)){
                currentSpeciesIndex = i;
            }
        }
        species[0] = "All Species";
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList(Arrays.asList(species)));

        speciesSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        speciesSpinner.setSelection(currentSpeciesIndex);

        String[] weather = new String[9];
        String[] weather2 =  res.getStringArray(R.array.weather_display_array);
        int currentWeatherIndex = 0;
        for(int i = 8; i > 0; i --){
            weather[i] = weather2[i - 1];
            if(weather[i].equalsIgnoreCase(filterWeather)){
                currentWeatherIndex = i;
            }
        }
        weather[0] = "All Conditions";
        ArrayAdapter<CharSequence> weatherAdapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList(Arrays.asList(weather)));

        weatherSpinner.setAdapter(weatherAdapter);
        weatherAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        weatherSpinner.setSelection(currentWeatherIndex);

        Button cancelbtn = (Button) filterDialog.findViewById(R.id.cancelFilterButton);
        cancelbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("from cancel onClickListener"); //keeping in case i need to use this method to keep track of dialog, maybe view in method
                cancelFilter();
            }
        });

        Button savebtn = (Button) filterDialog.findViewById(R.id.saveFilterButton);
        savebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("from save onClickListener"); //keeping in case i need to use this method to keep track of dialog, maybe view in method
                saveFilter();
            }
        });

        Switch freindsSwitch = (Switch) filterDialog.findViewById(R.id.viewAllFreindsSwitch);
        if (freindsSwitch != null) {
            freindsSwitch.setOnCheckedChangeListener(this);
        }
        if(filterShowFreindRecords){
            freindsSwitch.setChecked(true);
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker)   {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                String title = marker.getTitle();
                String snippet = marker.getSnippet();
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_layout, null);
                TextView tTitle = (TextView) v.findViewById(R.id.title);
                TextView tText = (TextView) v.findViewById(R.id.info);
                tTitle.setText(title);
                tText.setText(snippet);
                //tText.setTextColor(Color.GREEN);

                return v;
            }

        });

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng curPlace = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(curPlace).title("Current Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15.0f));
        VisibleRegion vRegion = mMap.getProjection().getVisibleRegion();
        LatLng southwest = vRegion.latLngBounds.southwest;
        LatLng northeast = vRegion.latLngBounds.northeast;
        viewMinLat = southwest.latitude;
        viewMaxLat = northeast.latitude;
        viewMinLon = northeast.longitude;
        viewMaxLon = southwest.longitude;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        tempFilterShowFreindRecords = isChecked;
    }

    private void displayPopupMessage(String message) {
        runOnUiThread(new PopupDisplay(message, this));
    }
    private void displayRecords(){
        int markerIndex = 0;
        mMap.clear();
        LatLng curPlace = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(curPlace).title("Current Location"));

        for(Record r: currentRecords) {
            String recordInfo = "Name: "+r.getName() +"\nLure: " + r.getLure() + "\nSpecies: " + r.getSpecies() + "\nWeather: " + r.getWeather() + "\nTime Caught: " + r.getTime() + "\nTemperature " + r.getTemperature();
            if(filterShowFreindRecords){
                recordInfo += "\nUser: "+r.getUser();
            }
            LatLng ltln = new LatLng(Double.parseDouble(r.getLatitude()), Double.parseDouble(r.getLongitude()));
            MarkerOptions m = new MarkerOptions().position(ltln).title(r.getName()).snippet(recordInfo).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            //currentMarkers[markerIndex] = m;
            markerIndex++;
            mMap.addMarker(m);
            //mMap.addMarker(new MarkerOptions().position(mLatLng).title("My Title").snippet("My Snippet"+"\n"+"1st Line Text"+"\n"+"2nd Line Text"+"\n"+"3rd Line Text").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

          //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15.0f));
        }
    }

    public void processResult(String result){
        if(!result.equals("no records")) {
            processRecordString(result);
        }
        System.out.println(result);

       displayRecords();


    }

    public void processRecordString(String records){
        final String ops[] = records.split("\\^\\^\\^\\^\\^");
        Record[] recordArray = null;//new Record[5];
        for(int i = 0; i < ops.length - 1; i++){
            if(i == 0){
                recordArray = new Record[ops.length - 1];
            }
            String recordInfo[] = ops[i].split("%%");
            Record temp = new Record();
            for(int j = 0; j < recordInfo.length; j++){
                String attribute[] = recordInfo[j].split("@@");
                if(attribute.length > 1) {
                    if (attribute[0].equalsIgnoreCase("name")) {
                        temp.setName(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("lat")) {
                        temp.setLatitude(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("lon")) {
                        temp.setLongitude(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("lure")) {
                        temp.setLure(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("weather")) {
                        temp.setWeather(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("species")) {
                        temp.setSpecies(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("time")) {
                        temp.setTime(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("temperature")) {
                        temp.setTemperature(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("user")) {
                        temp.setUser(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("path")) {
                        temp.setPath(attribute[1]);
                    }
                    if (attribute[0].equalsIgnoreCase("hour")) {
                        temp.setHour(attribute[1]);
                    }
                }
            }
            recordArray[i] = temp;
            System.out.println(temp.toString());
        }
        currentRecords = recordArray;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println("DEBUGGGER");

             //   mMap.addMarker(new MarkerOptions().position(mLatLng).title("My Title").snippet("My Snippet"+"\n"+"1st Line Text"+"\n"+"2nd Line Text"+"\n"+"3rd Line Text").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

    }


    private class networkConnection extends AsyncTask<String, Void, String> {

        HttpURLConnection conn = null;
        private MapActivity parent;

        public networkConnection(MapActivity m){
            parent = m;
        }
        @Override
        protected String doInBackground(String[] params) {//doInBackground(String[] params) {
            int status = 0;

            if(params[0].equals("getRecordsForMap")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "getRecordsForMap");
                    params1.put("uname", params[1]);
                    params1.put("minlat", params[2]);
                    params1.put("minlon", params[4]);
                    params1.put("maxlat", params[3]);
                    params1.put("maxlon", params[5]);
                    params1.put("species", params[6]);
                    params1.put("weather", params[7]);
                    params1.put("startTime", params[8]);
                    params1.put("endTime", params[9]);
                    params1.put("showFriends", params[10]);


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
                    final String split[] = responseString.split("responsCode:", 2);
                    if(split.length > 1){
                        if(split[1].equals("[\"No Records Matching Search\"]")){
                            parent.runOnUiThread(new Runnable() {
                                public void run() {
                                    processResult("no records");
                                }
                            });
                        }else {
                            final String ops[] = responseString.split("\\$\\$\\$\\$", 2);
                            if (ops[1] != null) {
                                parent.runOnUiThread(new Runnable() {
                                    public void run() {
                                        processResult(ops[1]);
                                    }
                                });
                            }
                        }
                    }

                    System.out.println("Response code is " + status);
                }  catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
                return "" + status;
            }

            return "end of function";
        }




        @Override
        protected void onPostExecute(String message) {
            //processResult(message);
        }
    }
}
