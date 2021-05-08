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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.capimgdev.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int REQUES_IMAGE_CAPTURE = 1;
    Button BtnCamera;
    ImageView imageView;
    String currentPhotoPath;
    Camera mCamera;

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

            File imagenArchivo = null;

            try{
                imagenArchivo = crearImagen();
            } catch (IOException ex){
                Log.e("Error",ex.toString());
            }
            if(imagenArchivo != null){
                Uri fotoUri = FileProvider.getUriForFile(this, "com.example.capimgdev.fileprovider", imagenArchivo);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(takePictureIntent, REQUES_IMAGE_CAPTURE);
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUES_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //Modifico el muestreo para que lo haga con el archivo guardado.
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);

            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(imageBitmap);

        }
    }

    private File crearImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-ms").format(new Date());
        String nombreImagen = "foto"+timeStamp+"_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen , ".jpg",directorio);

        currentPhotoPath = imagen.getAbsolutePath();
        return imagen;
    }

//    Apertura del objeto camara

    private boolean safeCameraOpen (int id){
        boolean qOpened = false;

        try {
            releaseCameraandPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e){
            Log.e(getString(R.string.app_name), "Failed to open Camera");
            e.printStackTrace();
        }
        return qOpened;
    }

    private void releaseCameraandPreview(){
        preview.setCamera(null);
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

//    Creacion de la vista previa de la camara
class Preview extends ViewGroup implements SurfaceHolder.Callback {

    SurfaceView surfaceView;
    SurfaceHolder holder;

    Preview(Context context) {
        super(context);

        surfaceView = new SurfaceView(context);
        addView(surfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}

public void setCamera (Camera camera){
        if (mCamera == camera) { return;}

        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null){
            List<Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            supportedPreviewSizes = localSizes;
            requestLayout();

            try {
                mCamera.setPreviewDisplay(holder);
                } catch (IOException e){
                    e.printStackTrace();
                }

//                Important: Call startPreview() to start updating the preview
//                surface. Preview must be started before you can take a picture.
                mCamera.startPreview();
        }
}

//Modificar la configuracion de camara

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int w, int h){
//        Now that the size is known, set up the camera parameters and begin the preview
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        requestLayout();
        mCamera.setParameters(parameters);

//        Important: Call startPreview() to start updating the preview surface
//        Preview must be started before you can take a picture.
        mCamera.startPreview();
    }

    @Override
    public void onClick(View v) {
        switch(previewState) {
            case K_STATE_FROZEN:
                camera.startPreview();
                previewState = K_STATE_PREVIEW;
                break;

            default:
                camera.takePicture( null, rawCallback, null);
                previewState = K_STATE_BUSY;
        } // switch
        shutterBtnConfig();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
        }
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }

}