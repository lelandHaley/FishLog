package wildlogic.fishlog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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

    }

    private void setWeatherData(JSONObject wData) {
        this.weatherJson = wData;
        TextView currCondField = (TextView) findViewById(R.id.cur_cond_field);

        TextView currTempField = (TextView) findViewById(R.id.current_temperature_field);
        TextView cloudCoverField = (TextView) findViewById(R.id.cloud_cover_field);
        TextView windSpeedField = (TextView) findViewById(R.id.wind_speed_field);
        TextView precipChanceField = (TextView) findViewById(R.id.precip_chance_field);

        try {

            JSONObject currWeatherObj = this.weatherJson.getJSONObject("currently");
            String conditions = currWeatherObj.getString("icon");

            String[] condArr = conditions.split("-");
            String condToDisplay = "";

            for(int i = 0; i < condArr.length; i++){
                condToDisplay += condArr[i].substring(0, 1).toUpperCase() + condArr[i].substring(1);
                if( i != condArr.length - 1){
                    condToDisplay += " ";
                }
            }

            currCondField.setText(condToDisplay);
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


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                System.out.println("data is not null in create record");

            }
        }

    }

    public void goBackToMain(View view){
        finish();
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(client != null) {
            client.disconnect();
        }
    }
}
