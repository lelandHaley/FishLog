package wildlogic.fishlog;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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

    private class WeatherAPIClient extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... params) {
            JSONObject wData = this.getWeatherJSON(params[0], params[1]);
            parentActivity.setWeatherDate(wData);
            return "SUCCESS";
        }
        private GetWeatherActivity parentActivity = null;

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

    private void setWeatherDate(JSONObject wData) {
        this.weatherJson = wData;
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



    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GetWeather Page", // TODO: Define a title for the content shown.
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
                "GetWeather Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://wildlogic.fishlog/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }
}
