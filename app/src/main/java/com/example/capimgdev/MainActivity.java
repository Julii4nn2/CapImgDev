blapackage com.example.capimgdev;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.capimgdev.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUES_IMAGE_CAPTURE = 1;
    Button BtnCamera;
    ImageView imageView;
    String currentPhotoPath;

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

    private boolean safeCameraOpen (int id){
        boolean qOpened = false;

        try {
            releaseCameraandPreview();
            camera = Camera.open(id);
            qOpened = (camera != null);
        } catch (Exception e){
            Log.e(getString(R.string.app_name), "Failed to open Camera");
            e.printStackTrace();
        }
        return qOpened;
    }

    private void releaseCameraandPreview(){
        preview.setCamera(null);
        if (camera != null){
            camera.release();
            camera = null;
        }
    }
}