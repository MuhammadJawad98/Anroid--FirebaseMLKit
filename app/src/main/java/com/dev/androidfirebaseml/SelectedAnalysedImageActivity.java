package com.dev.androidfirebaseml;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SelectedAnalysedImageActivity extends AppCompatActivity {
    private TextView tvTitle;
    private TextView editText;
    private ImageView imageView;
    private Button btnEdit, btnDelete, btnCancel;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("analysed_images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_analysed_image);
        getSupportActionBar().setTitle("View Selected Analysed Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        tvTitle = findViewById(R.id.textTitle);
        editText = findViewById(R.id.textResult);
        imageView = findViewById(R.id.imageView);
        btnEdit = findViewById(R.id.editBtn);
        btnDelete = findViewById(R.id.deleteBtn);
        btnCancel = findViewById(R.id.cancelBtn);

        String title = intent.getStringExtra("reader");
        String result = intent.getStringExtra("result");
        String id = intent.getStringExtra("id");
        String fileUri = intent.getStringExtra("fileUri");
        String filename = intent.getStringExtra("filename");

        editText.setText(result);
        tvTitle.setText(title);
        System.out.println("fileUri >>>>>>"+fileUri);
        Picasso.get().load(fileUri).into(imageView);
//        imageView.setImageURI(fileUri);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditContentActivity.class);
                intent.putExtra("title", tvTitle.getText().toString());
                intent.putExtra("filename", filename);
                intent.putExtra("imageUrl", fileUri);
                intent.putExtra("resultText", editText.getText().toString());
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef.getRef().child(id).removeValue();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_start_view:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_list_view:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}