package com.kewldevs.sathish.faceless;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sathish on 10/18/16.
 */

public class ImageSetTask extends AsyncTask<Void,Void,Bitmap> {

    ImageView imageView;
    String URl;
    Bitmap bmp = null;
    String TAG = "CARD";


    public ImageSetTask(ImageView imageView, String URl) {
        this.imageView = imageView;
        this.URl = URl;

    }

    public ImageSetTask(String URl) {
        this.URl = URl;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            URL urlConnection = new URL(URl);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        setBmp(bitmap);
        if(imageView!=null)
        imageView.setImageBitmap(bitmap);
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }
}
