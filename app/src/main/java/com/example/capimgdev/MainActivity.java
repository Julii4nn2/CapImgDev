package com.example.capimgdev;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    static final int REQUES_IMAGE_CAPTURE = 1;
    Button BtnCamera;
    ImageView imageView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // solicitar permisos para utilizar la camara
        requestPermissions(new String[]{ Manifest.permission.CAMERA }, 1);
        requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        BtnCamera = findViewById(R.id.BtnCamara);
        imageView = findViewById(R.id.imageView);

        BtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUES_IMAGE_CAPTURE);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUES_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

        }
    }
}