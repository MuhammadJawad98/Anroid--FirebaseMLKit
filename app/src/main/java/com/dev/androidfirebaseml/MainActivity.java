package com.dev.androidfirebaseml;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView barcodeImageView, contentImageView, textReaderImageVew;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barcodeImageView = findViewById(R.id.barCodeImageView);
        textReaderImageVew = findViewById(R.id.textReaderImageView);
        contentImageView = findViewById(R.id.contentImageView);

        barcodeImageView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), FirebaseMLActivity.class);
            intent.putExtra("type",0);
            startActivity(intent);
        });
        contentImageView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), FirebaseMLActivity.class);
            intent.putExtra("type",1);
            startActivity(intent);
        });
        textReaderImageVew.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), FirebaseMLActivity.class);
            intent.putExtra("type",2);
            startActivity(intent);
        });
    }
}