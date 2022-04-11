package com.dev.androidfirebaseml;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FirebaseMLActivity extends AppCompatActivity {
    private Button openCameraBtn, loadingImageBtn, editResult;
    private ImageView imageView;
    private TextView tvTitle, tvResult;
    private static final int REQUEST_PERMISSION = 3000;
    private Uri imageFileUri;
    private int type;
    private FirebaseRealtimeDbStorage db = new FirebaseRealtimeDbStorage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ml);
        getSupportActionBar().setTitle("Barcode Reader");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = findViewById(R.id.imageView);
        tvResult = findViewById(R.id.textResult);
        tvTitle = findViewById(R.id.textTitle);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);

        openCameraBtn = findViewById(R.id.editBtn);
        editResult = findViewById(R.id.editResultBtn);
        loadingImageBtn = findViewById(R.id.deleteBtn);

        if (type == 0) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.barcode));
        } else if (type == 1) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.content));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.text));
        }

        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        loadingImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
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
                finish();
                return true;
            case R.id.action_list_view:
                Intent intent = new Intent(getApplicationContext(), ListViewActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void openCamera() {
        if (!checkPermissions())
            return;
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileUri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        activityResultLauncher.launch(takePhotoIntent);
    }

    public void loadImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(galleryIntent);
    }

    private boolean checkPermissions() {
        String permissions[] = {android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean grantCamera =
                ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED;
        boolean grantExternal =
                ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED;

        if (!grantCamera && !grantExternal) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
        } else if (!grantCamera) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[0]}, REQUEST_PERMISSION);
        } else if (!grantExternal) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[1]}, REQUEST_PERMISSION);
        }
        return grantCamera && grantExternal;
    }

    // Create launcher variable inside onAttach or onCreate or global
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // Add code
                        if (result.getData() != null && result.getData().getData() != null)
                            imageFileUri = result.getData().getData();
                        imageView.setImageURI(imageFileUri);
                        // Prepare to show ML kit results
                        tvResult.setText("");
                        // Create an InputImage object for ML kit
                        InputImage image = null;
                        try {
                            image = InputImage.fromFilePath(getBaseContext(), imageFileUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (image != null) {
                            if (type == 0) {
                                tvTitle.setText("Barcode content");
                                runBarcodeReader(image);
                            } else if (type == 1) {
                                tvTitle.setText("Image content");
                                runContentReader(imageFileUri);
                            } else {
                                tvTitle.setText("Detected text");
                                runTextReader(image);
                            }
                            editResult.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

    public void runBarcodeReader(InputImage image) {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    // Task completed successfully
                    tvResult.append("Detected barcode: \n");
                    String result1 = null;
                    for (Barcode barcode : barcodes) {
                        result1 = barcode.getRawValue();
                        tvResult.append("  " + result1 + "\n");
                    }
                    uploadData();

                    if (result1 == null) {
                        tvResult.append("  No barcode found\n");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvResult.setText("Failed");
                    }
                });
    }

    void uploadData() {
        String filename = new File(imageFileUri.getPath()).getName();
        Item item = new Item(filename, tvTitle.getText().toString(), tvResult.getText().toString());
        db.uploadDataToRealtimeDatabase(item);
    }

    public void runContentReader(Uri uri) {
        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(getApplicationContext(), uri);
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getOnDeviceImageLabeler();
            labeler.processImage(image).addOnSuccessListener(labels -> {
                for (FirebaseVisionImageLabel label : labels) {
                    String text = label.getText();
                    String entityId = label.getEntityId();
                    float confidence = label.getConfidence();
                    tvResult.append(text + "  " + confidence + "\n");
                }
                uploadData();

            }).addOnFailureListener(e -> tvResult.setText("No data found\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void runTextReader(InputImage image) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                String result = visionText.getText();
                                if (result.length() > 0) {
                                    tvResult.append("Detected text:\n  " + result + "\n");
                                    uploadData();
                                } else {
                                    tvResult.append("Detected text:\n  No text found.\n");
                                }
                            }

                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        tvResult.append("Failed\n");
                                    }
                                });

    }


    public void runImageContentReader(InputImage image) {
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        labeler.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {
                                for (ImageLabel label : labels) {

                                    String result = label.getText();

                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvResult.setText("Failed");
                    }
                });
    }
}