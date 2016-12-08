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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait!");
        progressDialog.setMessage("Uploading..");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

    }

    @Override
    protected Void doInBackground(Void... voids) {

        //Image Compression..
        Log.d(TAG, "doInBackground: Compression Initiated!!");
        InputStream imageStream = null;
        byte[] byteArray = null;
        /*try {
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
        }*/

        byteArray = getByteArrayForURI(uriFilePath, context);
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

                    Picasso.with(context).load(dwnldUrl).into(updateView);

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
            }).addOnProgressListener((Activity) context, new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setProgress((int) progress);
                }
            });

        }

        return null;
    }


    public byte[] getByteArrayForURI(Uri uriFilePath, Context context) {
        try {
            Log.d(TAG, "doInBackground: Compression Initiated!!");
            byte[] byteArray = null;
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image
            InputStream imageStream = context.getContentResolver().openInputStream(uriFilePath);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(imageStream, null, o);
            imageStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            imageStream = context.getContentResolver().openInputStream(uriFilePath);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream, null, o2);
            imageStream.close();

            // here i override the original image file
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byteArray = stream.toByteArray();


            return byteArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
