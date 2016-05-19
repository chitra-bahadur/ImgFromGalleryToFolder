package com.example.chitra.imagefromgallery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            System.out.println("picturePath" + picturePath);
            String extension = picturePath.substring(picturePath.indexOf("."));
            int indexOfDot = picturePath.indexOf(".");
            int firstIndex = 0;
            for(int i = indexOfDot; i >0 ; i--){
                if(picturePath.charAt(i) == '/'){
                    firstIndex = i + 1;
                    break;
                }
            }
            String imageName = picturePath.substring(firstIndex);
            System.out.println("imagename:" + imageName);
            cursor.close();
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            Bitmap thumbnail;
            thumbnail = (BitmapFactory.decodeFile(picturePath));
            saveImage(thumbnail, imageName);

        }
    }

    public void saveImage(Bitmap bitmapImg, String name){
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        System.out.println("root:" + root);
        File myFile = new File(root + "/folder_name");
        if(!myFile.exists()){
            myFile.mkdirs();
        }


        String iname = name;
        File file = new File(myFile, iname);
        if(file.exists()){
            file.delete();
        }
        try{
            FileOutputStream out = new FileOutputStream(file);
            bitmapImg.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch(Exception ae){
            System.out.println(ae);
        }

        //tell the media scanner about the new image so it will be available immediatly
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {

                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);

                    }
                });

    }
}

