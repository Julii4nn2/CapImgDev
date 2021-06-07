package com.example.capimgdev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamField;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUES_IMAGE_CAPTURE = 1;
    Button BtnCamera;
    ImageView imageView;
    String currentPhotoPath;
    Camera mCamera = null;
    private SurfaceHolder mHolder;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // solicitar permisos para utilizar la camara
        requestPermissions(new String[]{ Manifest.permission.CAMERA }, 1);
        requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        //Instancio la camara
        try{
            mCamera = Camera.open();
        }catch (Exception e){
            // La camara no esta disponible
            Toast.makeText(this,"La camara no esta disponible", Toast.LENGTH_LONG).show();

        }


        BtnCamera = findViewById(R.id.button_capture);
        //mHolder = findViewById(R.id.camera_preview);

        BtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //dispatchTakePictureIntent();
            }
        });
    }

    // Se comienza el desarrollo pra el objeto camara y manejarlo de manera manual


    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{



        public CameraPreview(Context context, Camera camera){
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);

            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        }
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
              //  Toast.makeText(this,"Error al configurar la vista previa",Toast.LENGTH_LONG).show();
                //Log.d(TAG, "Error setting camera preview: " + e.getMessage()
            }

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                //Toast.makeText(this,"Error al iniciar la vista previa",Toast.LENGTH_LONG).show();
                //Log.d(Tag, "Error starting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            mCamera.release();
        }
    }
    // FIN DEL DESARROLLO DE LA CAMARA MANUAL


//    private void dispatchTakePictureIntent(){
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
//
//            File imagenArchivo = null;
//
//            try{
//                imagenArchivo = crearImagen();
//            } catch (IOException ex){
//                Log.e("Error",ex.toString());
//            }
//            if(imagenArchivo != null){
//                Uri fotoUri = FileProvider.getUriForFile(this, "com.example.capimgdev.fileprovider", imagenArchivo);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
//                startActivityForResult(takePictureIntent, REQUES_IMAGE_CAPTURE);
//            }
//        }
//    }
//
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == REQUES_IMAGE_CAPTURE && resultCode == RESULT_OK){
//            //Modifico el muestreo para que lo haga con el archivo guardado.
//            //Bundle extras = data.getExtras();
//            //Bitmap imageBitmap = (Bitmap) extras.get("data");
//            //imageView.setImageBitmap(imageBitmap);
//
//            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
//            imageView.setImageBitmap(imageBitmap);
//
//        }
//    }
//
//    private File crearImagen() throws IOException {
//        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-ms").format(new Date());
//        String nombreImagen = "foto"+timeStamp+"_";
//        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File imagen = File.createTempFile(nombreImagen , ".jpg",directorio);
//
//        currentPhotoPath = imagen.getAbsolutePath();
//        return imagen;
//    }
}