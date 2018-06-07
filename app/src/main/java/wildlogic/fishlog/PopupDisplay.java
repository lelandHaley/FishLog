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
    boolean finished = false;

    PopupDisplay(String s, Activity act) {
        str = s;
        activity = act;
    }
    PopupDisplay(String s, Activity act, boolean finish) {
        str = s;
        activity = act;
        finished = finish;
    }

    public void run() {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setMessage(str);
        if(!finished) {
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }else{
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                            dialog.dismiss();
                        }
                    });
        }
        alertDialog.show();
    }

    public void runAndFinish(){
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setMessage(str);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                    }
                });

        alertDialog.show();
    }
}

