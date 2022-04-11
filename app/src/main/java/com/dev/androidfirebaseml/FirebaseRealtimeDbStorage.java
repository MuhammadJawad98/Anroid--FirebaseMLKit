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
import com.google.android.gms.tasks.Task;
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
        String key;
        if(item.getId()!=null){
             key = item.getId();
        }else{
             key = dbRef.push().getKey();
        }
//        String key = dbRef.push().getKey();
        dbRef.child(key).child("imageUrl").setValue(item.getImageUrl());
        dbRef.child(key).child("filename").setValue(item.getFilename());
        dbRef.child(key).child("reader").setValue(item.getReader());
        dbRef.child(key).child("result").setValue(item.getResult());
    }

    public void downloadDataFromRealtimeDB(ArrayAdapter<Item> adapter) {
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                Item item = new Item(
                        (String) snapshot.child("filename").getValue(),
                        (String) snapshot.child("reader").getValue(),
                        (String) snapshot.child("result").getValue(),
                        snapshot.getKey(),
                        (String) snapshot.child("imageUrl").getValue()
                );
                adapter.add(item);
                System.out.println(">>>>>> " + item);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                System.out.println(">>>>id::: " + dataSnapshot.getKey());
                System.out.println(">>>>filename::: " + dataSnapshot.child("filename").getValue());
                Item item = new Item(
                        (String) dataSnapshot.child("filename").getValue(),
                        (String) dataSnapshot.child("reader").getValue(),
                        (String) dataSnapshot.child("result").getValue(),
                        dataSnapshot.getKey(),
                        (String) dataSnapshot.child("imageUrl").getValue()
                );
                System.out.println("position:::: "+adapter.getPosition(item));
                adapter.remove(item);
                adapter.notifyDataSetChanged();
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
    public void uploadImage(Context context, Uri filePath, String filename, Item item) {
        if (filePath != null) {

            StorageReference ref = storageRef.child(filename);

            ref.putFile(filePath).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            item.setImageUrl(imageUrl);
                                            uploadDataToRealtimeDatabase(item);
                                            Toast.makeText(context, "Data Save successfully ", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }


                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });
        }
    }
}
