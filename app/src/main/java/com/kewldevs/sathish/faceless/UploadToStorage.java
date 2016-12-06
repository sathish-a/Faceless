package com.kewldevs.sathish.faceless;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sathish on 12/6/16.
 */

public class UploadToStorage extends AsyncTask<Void, Void, Void> {


    private static String TAG = "CARD";
    StorageReference storageReference;
    Context context;
    String name;
    DatabaseReference databaseReference;
    Uri uriFilePath;
    ProgressDialog progressDialog;
    ImageView updateView;


    public UploadToStorage(StorageReference storageReference, Context context, String name, DatabaseReference databaseReference, Uri uriFilePath, ImageView updateView) {

        this.storageReference = storageReference; //Storage directory
        this.context = context; // app context
        this.name = name; // file name
        this.databaseReference = databaseReference; // database reference to store the dwnld url
        this.uriFilePath = uriFilePath; // source image uri path
        this.updateView = updateView;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Please wait", "Uploading.");
    }

    @Override
    protected Void doInBackground(Void... voids) {

        //Image Compression..
        Log.d(TAG, "doInBackground: Compression Initiated!!");
        InputStream imageStream = null;
        byte[] byteArray = null;
        try {
            imageStream = context.getContentResolver().openInputStream(uriFilePath);
            Bitmap bmp = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            try {
                stream.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Image upload task
        if (byteArray != null) {
            Log.d(TAG, "doInBackground: Upload Initiated!!");
            UploadTask uploadTask = storageReference.child(name + ".png").putBytes(byteArray);
            uploadTask.addOnSuccessListener((Activity) context, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onComplete: Upload Finished!!");
                    String dwnldUrl = taskSnapshot.getDownloadUrl().toString();
                    ImageSetTask imageSetTask = new ImageSetTask(updateView, dwnldUrl);
                    imageSetTask.execute();
                    Log.d(TAG, "onComplete: Url=" + dwnldUrl);
                    databaseReference.setValue(dwnldUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Upload Successful!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Upload Failed, Reason:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener((Activity) context, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Upload Failed, Reason:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }

        return null;
    }


}
