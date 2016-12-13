package wildlogic.fishlog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.BreakIterator;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import wildlogic.*;
import wildlogic.fishlog.R;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks
        ,GoogleApiClient.OnConnectionFailedListener{

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CREATE_RECORD = 1;

    public String mCurrentPhotoPath;

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private String locLat, locLong;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Uri mUri;
    private File tempImage;
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
        }
        System.out.println(mGoogleApiClient.toString());
    }
//    public void demoFuntion(View view) {
//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = (EditText) findViewById(R.id.pictureNameInput);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
//    }

//
//
////
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    public void createRecord(View view) {
        System.out.println("Taking picture");
        dispatchTakePictureIntent();
    }

    public void getLocation(View view) {
        System.out.println("Getting Location");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            locLat = String.valueOf(mLastLocation.getLatitude());
            locLong = String.valueOf(mLastLocation.getLongitude());
            System.out.println("Device latitude is : " + locLat);
            System.out.println("Device longitude is : " + locLong);
        }
    }




    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File pictureFile = new File(Environment.getExternalStorageDirectory() + "/imageFileName");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
      //  File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        System.out.println("Environment.DIRECTORY_PICTURES is " + Environment.getExternalStorageDirectory());
        System.out.println("getExternalFilesDir is " + getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
            if(storageDir.exists()){
                System.out.println("file exists");
            }
        System.out.println("Path is : " + image.getAbsolutePath());
        tempImage = image;
        return image;
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
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                mUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
//                        //"pic"+ String.valueOf(System.currentTimeMillis()) + ".jpg"));
//                        photoURI.getEncodedPath() + ".jpg"));
//
//                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mUri);
//
               // Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                else {
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
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File("file:" + mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1777)
        {
            //Get our saved file into a bitmap object:
            File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
            //Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
        }
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode ==  REQUEST_TAKE_PHOTO) && resultCode == RESULT_OK ) {
//            if(data != null) {
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                System.out.println("data is not null in onActivityForResult");

           // File root = Environment.getExternalStorageDirectory();
          //  Bitmap bMap = BitmapFactory.decodeFile(root+"/images/01.jpg");


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = null;
            //Bitmap bitmap = BitmapFactory.decodeFile("file:" + mCurrentPhotoPath, options);
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(tempImage), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            Intent i = new Intent(getApplicationContext(), CreateRecordActivity.class);
            //i.putExtra("pictureData", bitmap);
            i.putExtra("pictureData", tempImage);

            i.putExtra("recLat", locLat);
            i.putExtra("recLon", locLong);
            startActivityForResult(i , REQUEST_CREATE_RECORD);
//            mImageView.setImageBitmap(imageBitmap);

        }
//        if (requestCode == PICK_CONTACT_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                // A contact was picked.  Here we will just display it
//                // to the user.
//                startActivity(new Intent(Intent.ACTION_VIEW, data));
//            }
//        }
        //galleryAddPic();
    }


    protected void onStart() {
        System.out.println("in onStart");
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        System.out.println("in onStop");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void onPause(){
        System.out.println("in onPause");
        super.onPause();
    }

    protected void onResume(){
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
//            BreakIterator mLatitudeText = null;
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));

            locLat = String.valueOf(mLastLocation.getLatitude());
            locLong = String.valueOf(mLastLocation.getLongitude());

            System.out.println("Device latitude is : " + locLat);
            System.out.println("Device longitude is : " + locLong);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        System.out.println(mCurrentPhotoPath);
        //imageView = (ImageView) findViewById(R.id.imageView1);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println(mCurrentPhotoPath);
        //imageView = (ImageView) findViewById(R.id.imageView1);
    }
}