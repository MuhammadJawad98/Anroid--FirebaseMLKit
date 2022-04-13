package com.dev.androidfirebaseml;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditContentActivity extends AppCompatActivity {
    private FirebaseRealtimeDbStorage db = new FirebaseRealtimeDbStorage();
    private EditText tvTitle;
    private EditText editText;
    private ImageView imageView;
    private Button btnSave;
    private Uri fileUri;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);
        getSupportActionBar().setTitle("Edit Image Content");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String uri = intent.getStringExtra("fileUri");
        String imageUrl = intent.getStringExtra("imageUrl");
        id = intent.getStringExtra("id");

        String result = intent.getStringExtra("resultText");
        String filename = intent.getStringExtra("filename");
        tvTitle = findViewById(R.id.titleTv);
        editText = findViewById(R.id.resultEditText);
        imageView = findViewById(R.id.imageview);
        btnSave = findViewById(R.id.btnSave);

        tvTitle.setText(title);
        editText.setText(result);
        if (uri != null) {
            fileUri = Uri.parse(uri);
            imageView.setImageURI(fileUri);
        }else{
            Picasso.get().load(imageUrl).into(imageView);
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id != null) {
                    Item item = new Item(filename, tvTitle.getText().toString(), editText.getText().toString(), id, uri);
                    db.uploadDataToRealtimeDatabase(item);
                    Toast.makeText(getApplicationContext(), "Data Update successfully", Toast.LENGTH_SHORT).show();
                } else {
                    uploadData(fileUri, title, result);
                }
            }
        });
    }

    void uploadData(Uri imageFileUri, String title, String result) {
        String date = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        Date dateTime = new Date();
        long timeMilli = dateTime.getTime();
        Item item = new Item(date + timeMilli, title, result);
        db.uploadImage(getApplicationContext(), imageFileUri, date + timeMilli, item);
    }
}