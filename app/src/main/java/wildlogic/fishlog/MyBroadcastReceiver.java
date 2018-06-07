package wildlogic.fishlog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by thatDude on 4/11/18.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    Activity activity;
    boolean hasReception;


    MyBroadcastReceiver(Activity act){
        activity = act;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager conn =  (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        if ( networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            hasReception = true;

            // If the setting is ANY network and there is a network connection
            // (which by process of elimination would be mobile), sets refreshDisplay to true.
        } else if (networkInfo != null) {
            hasReception = true;

        } else {
            hasReception = false;
        }

    }
}
