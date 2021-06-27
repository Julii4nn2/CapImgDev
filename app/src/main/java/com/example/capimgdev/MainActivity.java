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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectStreamField;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {
    static final int REQUES_IMAGE_CAPTURE = 1;
    Button BtnCamera;
    private CameraPreview mPreview;
    String currentPhotoPath;
    private Camera mCamera;
    private SurfaceHolder mHolder;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // solicitar permisos para utilizar la camara
        requestPermissions(new String[]{ Manifest.permission.CAMERA }, 1);
        requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        //genero un instanciado para la camara
        mCamera = getCameraInstance(2);


        //Creo una vista previa y le coloco el contenido de la actividad
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        BtnCamera = findViewById(R.id.button_capture);

        BtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mCamera.takePicture(null,null, mPicture);

            }
        });
    }

    // Se comienza el desarrollo pra el objeto camara y manejarlo de manera manual
// se consigue un instanciado de camra
    public Camera getCameraInstance(int cameraselection){
        Camera c = null;
        try {
            c = Camera.open(cameraselection); // attempt to get a Camera instance
        }
        catch (Exception e){
            Toast.makeText(this, "La camara no esta disponible",Toast.LENGTH_LONG).show();
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    // Genero una vista previa de lo que esta viendo la camara para poder hacer las capturas
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

    // Se desarrolla la captura de imagenes
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = null;
            try{
                pictureFile = crearImagen();
            }catch (IOException e){

            }
            if (pictureFile == null){
//                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
//                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
//                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private File crearImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-ms").format(new Date());
        String nombreImagen = "foto"+timeStamp+"_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen , ".jpg",directorio);

        currentPhotoPath = imagen.getAbsolutePath();
        return imagen;
    }
    // FIN DEL DESARROLLO DE LA CAMARA MANUAL


}