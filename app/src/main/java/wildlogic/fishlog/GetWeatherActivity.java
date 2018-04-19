package wildlogic.fishlog;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by thatDude on 7/12/17.
 */
public class GetWeatherActivity extends AppCompatActivity {
    private static final String OPEN_WEATHER_MAP_URL =
            "https://api.darksky.net/forecast/5411c439cc32573f9f153a02d54a19a4/%s,%s";
    private String recLat;
    private String recLon;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private JSONObject weatherJson;
    static final int REQUEST_BACK_TO_MAIN = 1;

    private class WeatherAPIClient extends AsyncTask<String, Void, String> {
        private GetWeatherActivity parentActivity = null;


        @Override
        protected String doInBackground(String... params) {
            JSONObject wData = this.getWeatherJSON(params[0], params[1]);
            parentActivity.setWeatherData(wData);
            return "SUCCESS";
        }


        public WeatherAPIClient(GetWeatherActivity parentActivity) {
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
                while ((tmp = reader.readLine()) != null) {
                    json.append(tmp).append("\n");
                }
                reader.close();
                data = new JSONObject(json.toString());

                // This value will be 404 if the request was not
                // successful
//                if (data.getInt("cod") != 200) {
                if (status != 200) {
                    System.out.println("Status code is 200!");
                    return null;
                }

                return data;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return data;
        }

        private JSONObject getWeather() {
            JSONObject jsonWeather = null;
//            JSONObject currentCond = null;
//            String currTemp = "";
//            String summary = "";
            try {
                jsonWeather = getWeatherJSON(recLat, recLon);
//                currentCond = jsonWeather.getJSONObject("currently");
//                currTemp = currentCond.getString("temperature");
//                summary = currentCond.getString("summary");
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }
            return jsonWeather;
        }
        protected void onPostExecute(JSONObject result) {

        }

    }

    private void setWeatherData(JSONObject wData) {
        this.weatherJson = wData;
        TextView currCondField = (TextView) findViewById(R.id.cur_cond_field);

        TextView currTempField = (TextView) findViewById(R.id.current_temperature_field);
        TextView cloudCoverField = (TextView) findViewById(R.id.cloud_cover_field);
        TextView windSpeedField = (TextView) findViewById(R.id.wind_speed_field);
        TextView precipChanceField = (TextView) findViewById(R.id.precip_chance_field);

        //cityField.setText(this.weatherJson.getString());
        try {

            JSONObject currWeatherObj = this.weatherJson.getJSONObject("currently");
            currCondField.setText(currWeatherObj.getString("icon"));
            currTempField.setText(currWeatherObj.getString("temperature") + " °F");
            cloudCoverField.setText(Float.toString(Float.parseFloat(currWeatherObj.getString("cloudCover")) * 100) + " %");
            windSpeedField.setText(currWeatherObj.getString("windSpeed") + " Miles per Hour");
            precipChanceField.setText(Float.toString(Float.parseFloat(currWeatherObj.getString("precipProbability")) * 100) + " %");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_weather);
        recLat = (String) getIntent().getExtras().get("recLat");// consider moving lat and long retreval
        recLon = (String) getIntent().getExtras().get("recLon");// code to this location
        String[] parameters = new String[8];
        String weatherStatusIndicator = "";
        //String[] parameters = new String[7];
        Map<String, Object> params = new LinkedHashMap<>();
        parameters[0]= recLat;
        parameters[1]= recLon;
        WeatherAPIClient wAPI = new WeatherAPIClient(this);
        try {
            weatherStatusIndicator = wAPI.execute(parameters).get();
        }
        catch(Exception e){
            System.out.println("Exception: " + e.getMessage());
        }
        // set ui fields to weather info and BAM


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                System.out.println("data is not null in create record");

            }
        }

    }

    public void goBackToMain(View view){
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        //i.putExtra("pictureData", bitmap);
//        startActivityForResult(i, REQUEST_BACK_TO_MAIN);
        finish();

    }



    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "GetWeather Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://wildlogic.fishlog/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "GetWeather Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://wildlogic.fishlog/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if(client != null) {
            client.disconnect();
        }
    }
}
