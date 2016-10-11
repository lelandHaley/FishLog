package wildlogic.fishlog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class CreateRecordActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_record);

        //Intent intent = getIntent();
       // Bundle extras = getIntent().getExtras().getBundle("pictureData");
       // Bitmap imageBitmap = (Bitmap) extras.get("data");

        Bitmap imageBitmap = (Bitmap) getIntent().getExtras().get("pictureData");
        Spinner speciesSpinner = (Spinner) findViewById(R.id.species_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fish_species_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speciesSpinner.setAdapter(adapter);
        ImageView img= (ImageView) findViewById(R.id.picture_preview_view);
        img.setImageBitmap(imageBitmap);

      //  EditText nameField = (EditText)findViewById(R.id.name);
        //nameField.setHint();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                System.out.println("data is not null in create record");

            }
        }

    }
}