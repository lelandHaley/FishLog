package wildlogic.fishlog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by thatDude on 3/26/18.
 */
public class ManageFriendsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String curUser = "";
    private String urlString = "http://138.49.101.89:80/FishLogServlet/DBConectionServlet";//virtual server
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);
        curUser = (String) getIntent().getExtras().get("curUser");

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("In Activity Result");

    }

    public void declinePendingRequest(View view){
        System.out.println("in declinePendingRequest");
        Spinner incomingSpinner = (Spinner) findViewById(R.id.acceptPendingRequestSpinner);
        String selectedRequest = incomingSpinner.getSelectedItem().toString();

        networkConnection con = new networkConnection(this);
        String[] parameters = new String[3];
        parameters[0] = "declineRequest";
        parameters[1] = curUser;
        parameters[2] = selectedRequest;
        con.execute(parameters);

    }

    public void beginViewIncomingRequest(View view) {
        System.out.println("in beginSendRequest");
        Button sendButton = (Button) findViewById(R.id.sendFriendRequestButton);
        Button viewButton = (Button) findViewById(R.id.viewFreindsButton);
        Button removeButton = (Button) findViewById(R.id.RemoveFriendRequestButton);
        RelativeLayout incomingLayout = (RelativeLayout) findViewById(R.id.acceptPendingRequestLayout);
        Button incomingButton = (Button) findViewById(R.id.ViewIncomingRequestButton);
        incomingLayout.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.GONE);
        viewButton.setVisibility(View.GONE);
        removeButton.setVisibility(View.GONE);
        incomingButton.setVisibility(View.GONE);
        networkConnection con = new networkConnection(this);
        String[] parameters = new String[2];
        parameters[0] = "viewIncoming";
        parameters[1] = curUser;
        con.execute(parameters);

    }

    public void completeAcceptPendingRequest(View view) {
        System.out.println("in completeAcceptPendingRequest");
        Spinner incomingSpinner = (Spinner) findViewById(R.id.acceptPendingRequestSpinner);
        String selectedRequest = incomingSpinner.getSelectedItem().toString();

        networkConnection con = new networkConnection(this);
        String[] parameters = new String[3];
        parameters[0] = "acceptRequest";
        parameters[1] = curUser;
        parameters[2] = selectedRequest;
        con.execute(parameters);

    }

    public void populateIncoming(String[] incomingRequests) {
        System.out.println("in populateIncoming");
        Spinner incomingSpinner = (Spinner) findViewById(R.id.acceptPendingRequestSpinner);
        Button acceptButton = (Button) findViewById(R.id.acceptPendingRequestButton);
        Button declineButton = (Button) findViewById(R.id.declinePendingRequestButton);
        incomingSpinner.setOnItemSelectedListener(this);
        ArrayList<String> incoming = new ArrayList<String>();
        if (incomingRequests.length > 0) {
            if(incomingRequests[0].equalsIgnoreCase("No Incoming Friendship Requests")){
                acceptButton.setEnabled(false);
                incoming.add("No Incoming Friendship Requests");
            }else {
                for (int i = 0; i < incomingRequests.length; i++) {
                    incoming.add(incomingRequests[i]);
                }
                declineButton.setEnabled(true);
                acceptButton.setEnabled(true);
            }
        } else {
            incoming.add("No Incoming Friendship Requests");
            declineButton.setEnabled(false);
            acceptButton.setEnabled(false);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, incoming);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        incomingSpinner.setAdapter(adapter);

    }

    public void beginSendRequest(View view) {
        System.out.println("in beginSendRequest");
        Button sendButton = (Button) findViewById(R.id.sendFriendRequestButton);
        Button viewButton = (Button) findViewById(R.id.viewFreindsButton);
        Button removeButton = (Button) findViewById(R.id.RemoveFriendRequestButton);
        RelativeLayout sendLayout = (RelativeLayout) findViewById(R.id.sendRequestLayout);
        Button incomingButton = (Button) findViewById(R.id.ViewIncomingRequestButton);
        sendLayout.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.GONE);
        viewButton.setVisibility(View.GONE);
        removeButton.setVisibility(View.GONE);
        incomingButton.setVisibility(View.GONE);
    }

    public void completeRequestFriend(View view) {
        System.out.println("completeRequestFriend");

        EditText sendRequestEditField = (EditText) findViewById(R.id.sendRequestEditField);
        String newFriendUserName = sendRequestEditField.getText().toString();

        networkConnection con = new networkConnection(this);
        String[] parameters = new String[3];
        parameters[0] = "addFriend";
        parameters[1] = curUser;
        parameters[2] = newFriendUserName;
        con.execute(parameters);

    }

    public void viewFriends(View view) {
        System.out.println("in viewFriends");
        Button sendButton = (Button) findViewById(R.id.sendFriendRequestButton);
        Button viewButton = (Button) findViewById(R.id.viewFreindsButton);
        Button removeButton = (Button) findViewById(R.id.RemoveFriendRequestButton);
        RelativeLayout viewLayout = (RelativeLayout) findViewById(R.id.viewFriendsLayout);
        Button incomingButton = (Button) findViewById(R.id.ViewIncomingRequestButton);
        viewLayout.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.GONE);
        viewButton.setVisibility(View.GONE);
        removeButton.setVisibility(View.GONE);
        incomingButton.setVisibility(View.GONE);

        networkConnection con = new networkConnection(this);
        String[] parameters = new String[2];
        parameters[0] = "viewFriends";
        parameters[1] = curUser;
        con.execute(parameters);

    }

    public void beginRemoveFriend(View view) {
        System.out.println("beginRemoveFriend");
        System.out.println("in viewFriends");
        Button sendButton = (Button) findViewById(R.id.sendFriendRequestButton);
        Button viewButton = (Button) findViewById(R.id.viewFreindsButton);
        Button removeButton = (Button) findViewById(R.id.RemoveFriendRequestButton);
        RelativeLayout RemoveFriendLayout = (RelativeLayout) findViewById(R.id.removeFreindLayout);
        Button incomingButton = (Button) findViewById(R.id.ViewIncomingRequestButton);
        RemoveFriendLayout.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.GONE);
        viewButton.setVisibility(View.GONE);
        removeButton.setVisibility(View.GONE);
        incomingButton.setVisibility(View.GONE);

        networkConnection con = new networkConnection(this);
        String[] parameters = new String[2];
        parameters[0] = "getFriends";
        parameters[1] = curUser;
        con.execute(parameters);
    }

    public void completeDeleteFriendClicked(View view) {
        System.out.println("completeRemoveFriend");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        completeDeleteFriend();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are You Sure You Want To Delete This Freind?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    public void completeDeleteFriend() {
        Spinner friendSpinner = (Spinner) findViewById(R.id.removeFriendSpinner);
        String selectedUserToDelete = friendSpinner.getSelectedItem().toString();

        networkConnection con = new networkConnection(this);
        String[] parameters = new String[3];
        parameters[0] = "deleteFriend";
        parameters[1] = curUser;
        parameters[2] = selectedUserToDelete;
        con.execute(parameters);

    }


    public void goBackToMain(View view) {
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void populateFriendsToDelete(String[] usersToDisplay) {
        Spinner friendSpinner = (Spinner) findViewById(R.id.removeFriendSpinner);
        Button deleteButton = (Button) findViewById(R.id.deleteFriendButton);
        friendSpinner.setOnItemSelectedListener(this);
        ArrayList<String> friends = new ArrayList<String>();
        if (usersToDisplay.length > 0) {
            for (int i = 0; i < usersToDisplay.length; i++) {
                friends.add(usersToDisplay[i]);
                if(usersToDisplay.length == 1 && usersToDisplay[i].equals("No Friends Currently")){
                    deleteButton.setEnabled(false);
                }
            }
        }
        else {
            friends.add("No Friends Currently");
            deleteButton.setEnabled(false);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, friends);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        friendSpinner.setAdapter(adapter);

    }

    public void updateFriendsDisplay(String[] usersToDisplay, String[] usersToDisplayNotAccepted) {
        TextView frindsView = (TextView) findViewById(R.id.viewFriendsList);
        TextView frindsPendingView = (TextView) findViewById(R.id.pendingRequestsList);
        for (int i = 0; i < usersToDisplay.length; i++) {
            frindsView.append(usersToDisplay[i]);
            if (i != usersToDisplay.length - 1) {
                frindsView.append("\n");
            }
        }

        for (int i = 0; i < usersToDisplayNotAccepted.length; i++) {
            frindsPendingView.append(usersToDisplayNotAccepted[i]);
            if (i != usersToDisplayNotAccepted.length - 1) {
                frindsPendingView.append("\n");
            }
        }
    }


    private void displayPopupMessage(String message) {
        runOnUiThread(new PopupDisplay(message, this));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class networkConnection extends AsyncTask<String, Void, String> {

        HttpURLConnection conn = null;
        private ManageFriendsActivity parent;

        public networkConnection(ManageFriendsActivity m) {
            parent = m;
        }

        @Override
        protected String doInBackground(String[] params) {//doInBackground(String[] params) {
            int status = 0;

            if (params[0].equals("addFriend")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "addFriend");
                    params1.put("sender", params[1]);
                    params1.put("receiver", params[2]);

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
                    final String ops[] = responseString.split("responsCode:");

                    if (ops.length == 2) {
                        if (ops[1].equals("0")) {
                            displayPopupMessage("Friendship Request Send!!");
                            parent.runOnUiThread(new Runnable() {
                                public void run() {
                                    Button sendButton = (Button) findViewById(R.id.sendFriendRequestButton);
                                    Button viewButton = (Button) findViewById(R.id.viewFreindsButton);
                                    Button removeButton = (Button) findViewById(R.id.RemoveFriendRequestButton);
                                    RelativeLayout sendLayout = (RelativeLayout) findViewById(R.id.sendRequestLayout);
                                    Button incomingButton = (Button) findViewById(R.id.ViewIncomingRequestButton);
                                    sendLayout.setVisibility(View.GONE);
                                    sendButton.setVisibility(View.VISIBLE);
                                    viewButton.setVisibility(View.VISIBLE);
                                    removeButton.setVisibility(View.VISIBLE);
                                    incomingButton.setVisibility(View.VISIBLE);
                                }
                            });
                        } else if (ops[1].equals("1")) {
                            displayPopupMessage("User Does Not Exist, Enter A User That Does!!");
                        } else if (ops[1].equals("2") || ops[1].equals("5")) {
                            displayPopupMessage("Server Error!!");
                        } else if (ops[1].equals("3")) {
                            displayPopupMessage("Friendship Already Exists Between You And This User!!");
                        } else if (ops[1].equals("4") || ops[1].equals("6")) {
                            displayPopupMessage("Server Error, Frendship Could Not Be Created!!");
                        } else if (ops[1].equals("7")) {
                            displayPopupMessage("Friendship Request has Already Been Sent To This User!!");
                        }

                    }

                    System.out.println("Response code is " + status);
                } catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
                return "" + status;
            } else if (params[0].equals("viewFriends") || params[0].equals("getFriends")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "viewFriends");
                    params1.put("user", params[1]);

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
                    String split1[] = responseString.split("\\$\\$\\$\\$");
                    String split2[] = split1[1].split("%%%%");
                    final String[] ops = split2[0].split("\\^\\^\\^");

                    if(ops.length == 1 && (ops[0].equals("") || ops[0].equals("%\""))){
                        ops[0] = "No Friends Currently  ";
                    }
                    String[] ops2 = new String[1];

                    if(split2.length > 1) {
                       // final String[]
                        ops2 = split2[1].split("\\^\\^\\^");
                        ops2[ops2.length - 1] = ops2[ops2.length - 1].substring(0, ops2[ops2.length - 1].length() - 1);
                    }else{
                        ops2 = new String[1];
                        ops2[0] = "No Requests Pending";
                        ops[ops.length - 1] = ops[ops.length - 1].substring(0, ops[ops.length - 1].length() - 2);
                    }




                    if (params[0].equals("viewFriends")) {
                        final String[] opsFinal = ops2;
                        parent.runOnUiThread(new Runnable() {
                            public void run() {
                                updateFriendsDisplay(ops, opsFinal); // users to display, users not accepted yet
                            }
                        });
                    } else if (params[0].equals("getFriends")) {
                        parent.runOnUiThread(new Runnable() {
                            public void run() {
                                populateFriendsToDelete(ops);
                            }
                        });
                    }

                    System.out.println("Response code is " + status);
                } catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
                return "" + status;
            } else if (params[0].equals("deleteFriend")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "deleteFriend");
                    params1.put("user", params[1]);
                    params1.put("userToDelete", params[2]);

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
                    responseString = responseString.substring(0, responseString.length() - 1);
                    final String ops[] = responseString.split("ResponseCode:");


                    if (ops.length == 2) {
                        if (ops[1].equals("1")) {
                            displayPopupMessage("Friendship Deleted!!");
                            parent.runOnUiThread(new Runnable() {
                                public void run() {
                                    Button sendButton = (Button) findViewById(R.id.sendFriendRequestButton);
                                    Button viewButton = (Button) findViewById(R.id.viewFreindsButton);
                                    Button removeButton = (Button) findViewById(R.id.RemoveFriendRequestButton);
                                    RelativeLayout RemoveFriendLayout = (RelativeLayout) findViewById(R.id.removeFreindLayout);
                                    Button incomingButton = (Button) findViewById(R.id.ViewIncomingRequestButton);

                                    RemoveFriendLayout.setVisibility(View.GONE);
                                    sendButton.setVisibility(View.VISIBLE);
                                    viewButton.setVisibility(View.VISIBLE);
                                    removeButton.setVisibility(View.VISIBLE);
                                    incomingButton.setVisibility(View.VISIBLE);
                                }
                            });
                        } else if (ops[1].equals("1")) {
                            displayPopupMessage("This User Is Not Your Friend!!");
                        } else if (ops[1].equals("2")) {
                            displayPopupMessage("Server Error!!");
                        }
                    }
                    System.out.println("Response code is " + status);
                } catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
                return "" + status;
            } else if (params[0].equals("viewIncoming")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "viewIncoming");
                    params1.put("user", params[1]);

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
                    String split1[] = responseString.split("\\$\\$\\$\\$");
                    //String split2[] = split1[1].split("%%%%");
                    final String[] ops = split1[1].split("\\^\\^\\^");
                    ops[ops.length - 1] = ops[ops.length - 1].substring(0, ops[ops.length - 1].length() - 1);

                    parent.runOnUiThread(new Runnable() {
                            public void run() {
                                populateIncoming(ops);
                            }
                        });

                    System.out.println("Response code is " + status);
                } catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
                return "" + status;
            } else if (params[0].equals("acceptRequest")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "acceptRequest");
                    params1.put("user", params[1]);
                    params1.put("requestToAccept", params[2]);

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
                    //responseString = responseString.substring(0, responseString.length() - 1);
                    final String ops[] = responseString.split("Results:");


                    if (ops.length == 2) {
                        if (ops[1].equals("1")) {
                            displayPopupMessage("Friendship Request Accepted!!");
                            parent.runOnUiThread(new Runnable() {
                                public void run() {
                                    Button sendButton = (Button) findViewById(R.id.sendFriendRequestButton);
                                    Button viewButton = (Button) findViewById(R.id.viewFreindsButton);
                                    Button removeButton = (Button) findViewById(R.id.RemoveFriendRequestButton);
                                    RelativeLayout incomingLayout = (RelativeLayout) findViewById(R.id.acceptPendingRequestLayout);
                                    Button incomingButton = (Button) findViewById(R.id.ViewIncomingRequestButton);

                                    incomingLayout.setVisibility(View.GONE);
                                    sendButton.setVisibility(View.VISIBLE);
                                    viewButton.setVisibility(View.VISIBLE);
                                    removeButton.setVisibility(View.VISIBLE);
                                    incomingButton.setVisibility(View.VISIBLE);
                                }
                            });
                        } else if (ops[1].equals("2")) {
                            displayPopupMessage("Server Error!!");
                        }else if (ops[1].equals("0")) {
                            displayPopupMessage("Freindship Not Accepted");
                        }
                    }
                    System.out.println("Response code is " + status);
                } catch (Exception e) {
                    System.out.println("exception in createRecord: " + e.getMessage());
                }
                return "" + status;
            }else if (params[0].equals("declineRequest")) {
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> params1 = new LinkedHashMap<>();
                    params1.put("func", "declineRequest");
                    params1.put("user", params[1]);
                    params1.put("requestToDecline", params[2]);

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
                    //responseString = responseString.substring(0, responseString.length() - 1);
                    final String ops[] = responseString.split("Results:");


                    if (ops.length == 2) {
                        if (ops[1].equals("1")) {
                            displayPopupMessage("Friendship Request Declined!");
                            parent.runOnUiThread(new Runnable() {
                                public void run() {
                                    Button sendButton = (Button) findViewById(R.id.sendFriendRequestButton);
                                    Button viewButton = (Button) findViewById(R.id.viewFreindsButton);
                                    Button removeButton = (Button) findViewById(R.id.RemoveFriendRequestButton);
                                    RelativeLayout incomingLayout = (RelativeLayout) findViewById(R.id.acceptPendingRequestLayout);
                                    Button incomingButton = (Button) findViewById(R.id.ViewIncomingRequestButton);

                                    incomingLayout.setVisibility(View.GONE);
                                    sendButton.setVisibility(View.VISIBLE);
                                    viewButton.setVisibility(View.VISIBLE);
                                    removeButton.setVisibility(View.VISIBLE);
                                    incomingButton.setVisibility(View.VISIBLE);
                                }
                            });
                        } else if (ops[1].equals("2")) {
                            displayPopupMessage("Server Error!!");
                        }else if (ops[1].equals("0")) {
                            displayPopupMessage("Freindship Not Declined");
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
