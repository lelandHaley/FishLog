package wildlogic.fishlog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by thatDude on 3/30/18.
 */
public class PopupDisplay implements Runnable {
    String str;
    Activity activity;

    PopupDisplay(String s, Activity act) {
        str = s;
        activity = act;
    }

    public void run() {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setMessage(str);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}

