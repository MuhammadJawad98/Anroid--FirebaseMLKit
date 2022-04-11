package com.dev.androidfirebaseml;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class FirebaseRealtimeDbStorage {
    String reference = "analysed_images";
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(reference);
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + reference);

    public void uploadDataToRealtimeDatabase(Item item) {
        String key = dbRef.push().getKey();
        assert key != null;
        dbRef.child(key).child("filename").setValue(item.getFilename());
        dbRef.child(key).child("reader").setValue(item.getReader());
        dbRef.child(key).child("result").setValue(item.getResult());
    }

    public void downloadDataFromRealtimeDB(ArrayAdapter<Item> adapter) {
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                if (!snapshot.hasChildren() || snapshot.child("imageResource").getValue() == null)
                    return;
                Item item = new Item(
                        (String) snapshot.child("filename").getValue(),
                        (String) snapshot.child("reader").getValue(),
                        (String) snapshot.child("result").getValue()
                );
//                adapter.add(cbrEvent);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // UploadImage method
    private void uploadImage(Context context, Uri filePath) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            String date = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            Date dateTime = new Date();
            long timeMilli = dateTime.getTime();
            StorageReference ref
                    = storageRef
                    .child(date + timeMilli);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(context,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(context,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }
}
