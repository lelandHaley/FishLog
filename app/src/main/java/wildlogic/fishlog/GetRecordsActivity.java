package wildlogic.fishlog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.regex.Pattern;

/**
 * Created by thatDude on 8/9/17.
 */
public class GetRecordsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String urlString = "http://192.168.1.6:8080/FishLogServlet/DBConectionServlet";// home
   //private String urlString = "http://192.168.1.13:8080/FishLogServlet/DBConectionServlet"; //tylers
    // private String urlString = "http://192.168.3.61:8080/FishLogServlet/DBConectionServlet";// sipnsurf
   // private String urlString = "http://192.168.1.9:8080/FishLogServlet/DBConectionServlet";// javavino
   // private String urlString = "http://192.168.1.5:8080/FishLogServlet/DBConectionServlet";// javavino
    //private String urlString = "http://192.168.44.19:8080/FishLogServlet/DBConectionServlet";// rootnote
    //private String urlString = "http://138.49.3.45:8080/FishLogServlet/DBConectionServlet";
    //private String urlString = "http://138.49.101.89:80/FishLogServlet/DBConectionServlet";//virtual server
    private String user = "";
    private String latitude = "";
    private String longitude = "";
    String origName = "";
    String origLat = "";
    String origLon = "";
    String origLure = "";
    String origWeather = "";
    String origSpecies = "";
    String origTime = "";
    String origTemp = "";
    //String orig = "";
    Semaphore mutex =  new Semaphore(0);

    Record[] currentRecords = new Record[0];
    int currentDisplayedRecordIndex = -1;

    public void goBackToMain(View view) {
        finish();
    }

    public void editRecord(View view){
        Button editButton = (Button) findViewById(R.id.editButton);
        TextView editingText = (TextView) findViewById(R.id.editingTextField);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

            EditText nameField = (EditText) findViewById(R.id.displayNameField);
            EditText latField = (EditText) findViewById(R.id.displayLatField);
            EditText longField = (EditText) findViewById(R.id.displayLonField);
            EditText lureField = (EditText) findViewById(R.id.displayLureField);
            EditText weatherField = (EditText) findViewById(R.id.displayWeatherField);
            EditText speciesField = (EditText) findViewById(R.id.displaySpeciesField);
            EditText timeField = (EditText) findViewById(R.id.displayTimeField);
            EditText tempField = (EditText) findViewById(R.id.displayTempField);
            EditText userField = (EditText) findViewById(R.id.displayUserField);

        if(editButton.getText().equals("Edit")) {

            origName = nameField.getText().toString();
            origLat = latField.getText().toString();
            origLon = longField.getText().toString();
            origLure = lureField.getText().toString();
            origWeather = weatherField.getText().toString();
            origSpecies = speciesField.getText().toString();
            origTime = timeField.getText().toString();
            origTemp = tempField.getText().toString();

            editingText.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
            editButton.setText("Finished Edititng");
            editButton.setHeight(90);

            nameField.setEnabled(true);
            latField.setEnabled(true);
            longField.setEnabled(true);
            lureField.setEnabled(true);
            weatherField.setEnabled(true);
            speciesField.setEnabled(true);
            timeField.setEnabled(true);//may need validation for time
            final EditText tempTimeField = timeField;

            tempTimeField.addTextChangedListener(new TextWatcher() {
                String valid_time = null;
                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub

                    // TODO Auto-generated method stub
                    Is_Valid_Time(tempTimeField); // pass your EditText Obj here.
                }

                public void Is_Valid_Time(EditText edt) {
                    if (edt.getText().toString() == null) {
                        edt.setError("Invalid Time Format (YYYY:MM:DD:HH:MM)");
                        valid_time = null;
                    } else if (isTimeValid(edt.getText().toString()) == false) {
                        edt.setError("Invalid Time Format (YYYY:MM:DD:HH:MM)");
                        valid_time = null;
                    } else {
                        valid_time = edt.getText().toString();
                    }
                }

                public boolean isTimeValid(String word) {

                    return word.matches("[0-9]{4}:([0-9]{1,2}:){3}[0-9]{1,2}");
                }
            });
            tempField.setEnabled(true);

        }
        else if(editButton.getText().equals("Finished Edititng")){
            System.out.println("debug");

            String nameText = nameField.getText().toString();
            String latText = latField.getText().toString();
            String longText = longField.getText().toString();
            String lureText = lureField.getText().toString();
            String weatherText = weatherField.getText().toString();
            String speciesText = speciesField.getText().toString();
            String timeText = timeField.getText().toString();
            String tempText = tempField.getText().toString();
            String timeErrorString = null;
            try {
                timeErrorString = timeField.getError().toString();
            }catch(NullPointerException npe){
                timeErrorString = "";
            }

            if(!timeErrorString.equals("")){
                displayPopupMessage("Invalid Time Format!");
            }
            if(!nameText.equals(origName) || !latText.equals(origLat) || !longText.equals(origLon) || !lureText.equals(origLure) || !weatherText.equals(origWeather) || !speciesText.equals(origSpecies) || !timeText.equals(origTime) || !tempText.equals(origTemp)) {

                editButton.setText("Edit");
                editingText.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
                editButton.setHeight(50);

                networkConnection con = new networkConnection(this);
                String[] parameters = new String[19];
                parameters[0] = "editRecord";
                parameters[1] = user;
                parameters[2] = nameText;
                parameters[3] = latText;
                parameters[4] = longText;
                parameters[5] = lureText;
                parameters[6] = weatherText;
                parameters[7] = speciesText;
                parameters[8] = timeText;
                parameters[9] = tempText;
                parameters[10] = origName;
                parameters[11] = origLat;
                parameters[12] = origLon;
                parameters[13] = origLure;
                parameters[14] = origWeather;
                parameters[15] = origSpecies;
                parameters[16] = origTime;
                parameters[17] = origTemp;
                parameters[18] = currentRecords[currentDisplayedRecordIndex].getPath();


                con.execute(parameters);

                nameField.setEnabled(false);
                latField.setEnabled(false);
                longField.setEnabled(false);
                lureField.setEnabled(false);
                weatherField.setEnabled(false);
                speciesField.setEnabled(false);
                timeField.setEnabled(false);
                tempField.setEnabled(false);
            }
            else{
                displayPopupMessage("No Changes Made!");
            }
        }

    }

private void displayPopupMessage(String message) {
        runOnUiThread(new PopupDisplay(message, this));
}



    public void deleteRecordClicked(View view){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteRecord();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are You Sure You Want To Delete This Record?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }

    public void deleteRecord() {

        networkConnection con = new networkConnection(this);
        String[] parameters = new String[15];
        Record curRecord = currentRecords[currentDisplayedRecordIndex];
        parameters[0] = "deleteRecord";
        parameters[1] = user;
        parameters[2] = curRecord.getName();
        parameters[3] = curRecord.getLatitude();
        parameters[4] = curRecord.getLongitude();
        parameters[5] = curRecord.getLure();
        parameters[6] = curRecord.getWeather();
        parameters[7] = curRecord.getSpecies();
        parameters[8] = curRecord.getTime();
        parameters[9] = curRecord.getTemperature();

        //parameters[3] = filterSelection;
        con.execute(parameters);


    }

    public void showPrevRecord(View view){
        if(currentDisplayedRecordIndex == 0){
            currentDisplayedRecordIndex = currentRecords.length;
        }
        currentDisplayedRecordIndex--;
        displayRecord(currentRecords[currentDisplayedRecordIndex]);
    }

    public void showNextRecord(View view){
        if(currentDisplayedRecordIndex == currentRecords.length - 1){
            currentDisplayedRecordIndex = -1;
        }
        currentDisplayedRecordIndex++;






        displayRecord(currentRecords[currentDisplayedRecordIndex]);
    }

    public void getRecords(View view){
        Spinner typeSpinner = (Spinner) findViewById(R.id.filterTypeSpinner);
        String filterType = typeSpinner.getSelectedItem().toString();
        Spinner selectionSpinner = null;
        String filterSelection = null;
        String filterSelection2 = null;
        if(filterType.equalsIgnoreCase("species") || filterType.equalsIgnoreCase("weather conditions")){
            selectionSpinner = (Spinner) findViewById(R.id.filterSelectionSpinner);
            filterSelection = selectionSpinner.getSelectedItem().toString();

        }else if(filterType.equalsIgnoreCase("time of day recorded")){
            EditText start = (EditText) findViewById(R.id.leftInputField);
            filterSelection = start.getText().toString();

            EditText end = (EditText) findViewById(R.id.rightInputField);
            filterSelection2 = end.getText().toString();
        }

        TypedArray filterTypeArray =getResources().obtainTypedArray(R.array.filter_records_by_attribute_array);
        networkConnection con = new networkConnection(this);
        if(filterType.equals(filterTypeArray.getString(0))) {//no filter
            //String species =
            String[] parameters = new String[3];
            parameters[0] = "getRecords";
            parameters[1] = user;
            parameters[2] = filterType;
            //parameters[3] = filterSelection;
            con.execute(parameters);
        }
        if(filterType.equals(filterTypeArray.getString(0+1))) {//Species
            //String species =
            String[] parameters = new String[5];
            parameters[0] = "getRecords";
            parameters[1] = user;
            parameters[2] = filterType;
            parameters[3] = filterSelection;
            con.execute(parameters);
        }

        if(filterType.equals(filterTypeArray.getString(0+2))) {//weather
            //String species =
            String[] parameters = new String[5];
            parameters[0] = "getRecords";
            parameters[1] = user;
            parameters[2] = filterType;
            parameters[3] = filterSelection;
            con.execute(parameters);
        }

        if(filterType.equals(filterTypeArray.getString(0+3))) {//location

            filterSelection = ((EditText) findViewById(R.id.nearMeInputField)).getText().toString();
            String[] parameters = new String[5];
            parameters[0] = "getRecords";
            parameters[1] = user;
            parameters[2] = filterType;
            parameters[3] = filterSelection;
            con.execute(parameters);
        }

        if(filterType.equals(filterTypeArray.getString(0+4))) {//time of day
            //String species =
            String[] parameters = new String[5];
            parameters[0] = "getRecords";
            parameters[1] = user;
            parameters[2] = filterType;
            parameters[3] = filterSelection;
            parameters[4] = filterSelection2;
            con.execute(parameters);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        setContentView(R.layout.activity_get_records);
        Spinner filterTypeSpinner = (Spinner) findViewById(R.id.filterTypeSpinner);
        filterTypeSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_records_by_attribute_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        filterTypeSpinner.setAdapter(adapter);
        user = (String) getIntent().getExtras().get("user");
        latitude = (String) getIntent().getExtras().get("recLat");
        longitude = (String) getIntent().getExtras().get("recLon");


    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // Spinner filterTypeSpinner = (Spinner) findViewById(R.id.filterSelectionSpinner);


        TextView filterDescription = (TextView) findViewById(R.id.filterDescription);
        Spinner selectionSpinner = (Spinner) findViewById(R.id.filterSelectionSpinner);
        Spinner typeSpinner = (Spinner) findViewById(R.id.filterTypeSpinner);
        String filterType = typeSpinner.getSelectedItem().toString();
        TypedArray filterTypeArray = getResources().obtainTypedArray(R.array.filter_records_by_attribute_array);
        EditText nearMeInputField = (EditText) findViewById(R.id.nearMeInputField);
        EditText leftInputField = (EditText) findViewById(R.id.leftInputField);
        EditText rightInputField = (EditText) findViewById(R.id.rightInputField);
//        String recLure = filterDescription.getText().toString();
//        String recWeather = selectionSpinner.getSelectedItem().toString();
        if(filterType.equals(filterTypeArray.getString(0))) {//no filter
            //filterDescription.setText(R.string.filter_near_me);
            filterDescription.setVisibility(View.GONE);
            nearMeInputField.setVisibility(View.GONE);
            leftInputField.setVisibility(View.GONE);
            rightInputField.setVisibility(View.GONE);
            selectionSpinner.setVisibility(View.GONE);
        }

        if(filterType.equals(filterTypeArray.getString(0+1))){//Species

            //selectionSpinner.setOnItemSelectedListener(this);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.fish_species_array, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            selectionSpinner.setAdapter(adapter);
            selectionSpinner.setVisibility(View.VISIBLE); //View.GONE
            filterDescription.setText(R.string.filter_by_species);
            filterDescription.setVisibility(View.VISIBLE);
            leftInputField.setVisibility(View.GONE);
            rightInputField.setVisibility(View.GONE);
            nearMeInputField.setVisibility(View.GONE);
        }
        else if (filterType.equals(filterTypeArray.getString(0+2))) {//weather
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.weather_display_array, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            selectionSpinner.setAdapter(adapter);
            selectionSpinner.setVisibility(View.VISIBLE);
            nearMeInputField.setVisibility(View.GONE);
            filterDescription.setText(R.string.filter_by_conditions);
            filterDescription.setVisibility(View.VISIBLE);
            leftInputField.setVisibility(View.GONE);
            rightInputField.setVisibility(View.GONE);
            nearMeInputField.setVisibility(View.GONE);
        } else if(filterType.equals(filterTypeArray.getString(0+3))){//Records Near Me
            filterDescription.setText(R.string.filter_near_me);
            filterDescription.setVisibility(View.VISIBLE);
            nearMeInputField.setVisibility(View.VISIBLE);
            leftInputField.setVisibility(View.GONE);
            rightInputField.setVisibility(View.GONE);
            selectionSpinner.setVisibility(View.GONE);
        }
        else if(filterType.equals(filterTypeArray.getString(0+4))){//Time Of Day Recorded
            filterDescription.setText(R.string.filter_time_of_day);
            filterDescription.setVisibility(View.VISIBLE);
            leftInputField.setVisibility(View.VISIBLE);
            rightInputField.setVisibility(View.VISIBLE);
            nearMeInputField.setVisibility(View.GONE);
            selectionSpinner.setVisibility(View.GONE);
        }


    }

    public void onNothingSelected(AdapterView parent) {
        // Do nothing.
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GetRecords Page", // TODO: Define a title for the content shown.
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
                "GetRecords Page", // TODO: Define a title for the content shown.
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


    public void processResult(String result){
        if(!result.equals("no records")) {
            processRecordString(result);
        }
        System.out.println(result);
        if(currentDisplayedRecordIndex == -1){
            currentDisplayedRecordIndex++;
        }
        if(currentRecords.length == 0){
            displayRecord(null);
        }else{
            displayRecord(currentRecords[currentDisplayedRecordIndex]);
        }

    }



    public void displayRecord(Record record){

        ImageView img = (ImageView) findViewById(R.id.pictureView);
//        img.setImageBitmap(bitmap);
//        recordImage = bitmap;
        TextView noRecordsText = (TextView) findViewById(R.id.noRecordsText);
        TextView nameField = (TextView) findViewById(R.id.displayNameField);
        TextView latField = (TextView) findViewById(R.id.displayLatField);
        TextView longField = (TextView) findViewById(R.id.displayLonField);
        TextView lureField = (TextView) findViewById(R.id.displayLureField);
        TextView weatherField = (TextView) findViewById(R.id.displayWeatherField);
        TextView speciesField = (TextView) findViewById(R.id.displaySpeciesField);
        TextView timeField = (TextView) findViewById(R.id.displayTimeField);
        TextView tempField = (TextView) findViewById(R.id.displayTempField);
        TextView userField = (TextView) findViewById(R.id.displayUserField);
        ScrollView ScrollContainer = (ScrollView) findViewById(R.id.RecordDisplayScrollContainer);
        Button editButton = (Button) findViewById(R.id.editButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        Button prevButton = (Button) findViewById(R.id.prevButton);

        if(currentRecords.length == 0){
            ScrollContainer.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            noRecordsText.setVisibility(View.VISIBLE);
        }else {
           // filePath = mSaveBit.getPath()
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(currentRecords[currentDisplayedRecordIndex].getPath());
                Space s1 = (Space) findViewById(R.id.SpaceAboveImageView);
                Space s2 = (Space) findViewById(R.id.SpaceBelowImageView);
                TextView currDisplay = (TextView) findViewById(R.id.currentDisplayText);
//                if(bitmap.equals(null)){
                currDisplay.setText("Displaying Record : " + (currentDisplayedRecordIndex + 1) +" of " + currentRecords.length);
                if(bitmap == null){
                    img.setVisibility(View.GONE);
                    s1.setVisibility(View.GONE);
                    s2.setVisibility(View.GONE);
                }else{
                    img.setVisibility(View.VISIBLE);
                    s1.setVisibility(View.VISIBLE);
                    s2.setVisibility(View.VISIBLE);
                }
                img.setImageBitmap(bitmap);

            }catch(Exception e){
                System.out.println("Error with imageView");
            }
            noRecordsText.setVisibility(View.GONE);
            nameField.setText(record.getName());
            latField.setText(record.getLatitude());
            longField.setText(record.getLongitude());
            lureField.setText(record.getLure());
            weatherField.setText(record.getWeather());
            speciesField.setText(record.getSpecies());
            timeField.setText(record.getTime());
            tempField.setText(record.getTemperature());
            userField.setText(record.getUser());
            ScrollContainer.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            prevButton.setVisibility(View.VISIBLE);
        }
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
                if(attribute[0].equalsIgnoreCase("name")){
                    temp.setName(attribute[1]);
                }
                if(attribute[0].equalsIgnoreCase("lat")){
                    temp.setLatitude(attribute[1]);
                }
                if(attribute[0].equalsIgnoreCase("lon")){
                    temp.setLongitude(attribute[1]);
                }
                if(attribute[0].equalsIgnoreCase("lure")){
                    temp.setLure(attribute[1]);
                }
                if(attribute[0].equalsIgnoreCase("weather")){
                    temp.setWeather(attribute[1]);
                }
                if(attribute[0].equalsIgnoreCase("species")){
                    temp.setSpecies(attribute[1]);
                }
                if(attribute[0].equalsIgnoreCase("time")){
                    temp.setTime(attribute[1]);
                }
                if(attribute[0].equalsIgnoreCase("temperature")){
                    temp.setTemperature(attribute[1]);
                }
                if(attribute[0].equalsIgnoreCase("user")){
                    temp.setUser(attribute[1]);
                }
                if(attribute[0].equalsIgnoreCase("path")){
                    temp.setPath(attribute[1]);
                }
            }
            recordArray[i] = temp;
        }
        currentRecords = recordArray;
    }





    private class networkConnection extends AsyncTask<String, Void, String> {

        HttpURLConnection conn = null;
        private GetRecordsActivity parent;

        public networkConnection(GetRecordsActivity m){
            parent = m;
        }
        @Override
        protected String doInBackground(String[] params) {//doInBackground(String[] params) {
            int status = 0;

            if(params[0].equals("getRecords")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "getRecords");
                    params1.put("uname", params[1]);
                    params1.put("filter", params[2]);
                    if(!params[2].equalsIgnoreCase("no filter")) {
                        params1.put("filterSpecifics", params[3]);
                        params1.put("filterSpecifics2", params[4]);
                        if (params[2].equalsIgnoreCase("records near me")) {
                            params1.put("latitude", latitude);
                            params1.put("longitude", longitude);
                        }
                    }

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
            if(params[0].equals("editRecord")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "editRecord");
                    params1.put("uname", params[1]);
                    params1.put("newName", params[2]);
                    params1.put("newLat", params[3]);
                    params1.put("newLon", params[4]);
                    params1.put("newLure", params[5]);
                    params1.put("newWeather", params[6]);
                    params1.put("newSpecies", params[7]);
                    params1.put("newTime", params[8]);
                    params1.put("newTemp", params[9]);
                    params1.put("origName", params[10]);
                    params1.put("origLat", params[11]);
                    params1.put("origLon", params[12]);
                    params1.put("origLure", params[13]);
                    params1.put("origWeather", params[14]);
                    params1.put("origSpecies", params[15]);
                    params1.put("origTime", params[16]);
                    params1.put("origTemp", params[17]);
                    params1.put("origPath", params[18]);

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

                    // boolean b = conn.

                    conn.getOutputStream().write(postDataBytes);
                    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    status = conn.getResponseCode();
                    String responseString = "";
                    for (int c; (c = in.read()) >= 0; ) {
                        System.out.print((char) c);
                        responseString += (char) c;
                    }
                    String successIndicator[] = responseString.split("responsCode:");
                    if(successIndicator.length == 2){
                        if(successIndicator[1].equals("1")){
                            displayPopupMessage("Record Changes Saved!");
                            currentRecords[currentDisplayedRecordIndex] = new Record(params[2], params[3],params[4],params[5],params[6],params[7],params[8],params[9], user, params[18]);

                        }else{
                            displayPopupMessage("Server Error, Changes Not Saved");
                        }
                    }
                    System.out.println("Response code is " + status);


                }catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
            }if(params[0].equals("deleteRecord")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "deleteRecord");
                    params1.put("uname", params[1]);
                    params1.put("name", params[2]);
                    params1.put("lat", params[3]);
                    params1.put("lon", params[4]);
                    params1.put("lure", params[5]);
                    params1.put("weather", params[6]);
                    params1.put("species", params[7]);
                    params1.put("time", params[8]);
                    params1.put("temp", params[9]);

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
                    String successIndicator[] = responseString.split("responsCode:");
                    if (successIndicator.length == 2) {
                        if (successIndicator[1].equals("1")) {
                            removeDisplayRecord();

                            displayPopupMessage("Record Deleted!");

                            parent.runOnUiThread(new Runnable() {
                                public void run() {
                                    if(currentRecords.length > 0) {
                                        displayRecord(currentRecords[currentDisplayedRecordIndex]);
                                    }else{
                                        displayRecord(new Record());
                                    }
                                }
                            });

                        } else {
                            displayPopupMessage("Server Error, Changes Not Saved");
                        }
                    }
                    System.out.println("Response code is " + status);
                }catch(Exception e){
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
            }

            return "end of function";
        }


        @Override
        protected void onPostExecute(String message) {
            //processResult(message);
        }
    }
    public void removeDisplayRecord() {
        System.out.println("debug1");
        Record[] temp = new Record[currentRecords.length - 1];
        System.arraycopy(currentRecords, currentDisplayedRecordIndex + 1, currentRecords, currentDisplayedRecordIndex, currentRecords.length - 1 - currentDisplayedRecordIndex);
        System.arraycopy(currentRecords, 0, temp, 0, currentRecords.length - 1);
        currentRecords = temp;
        if(currentDisplayedRecordIndex == currentRecords.length && currentDisplayedRecordIndex > 0){
            currentDisplayedRecordIndex--;
        }

        System.out.println("debug2");
    }
}
