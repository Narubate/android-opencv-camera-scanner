package id.aashari.code.camerascanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.aashari.code.camerascanner.helpers.MyConstants;

public class MainActivity extends AppCompatActivity {

    Button btnOpenCamera;
    Button btnOpenGallery;
    Button btnImageProcess;

    ImageView imageView;

    Uri selectedImage;
    Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeElement();
        initializeEvent();
    }

    private void initializeElement() {
        this.imageView = findViewById(R.id.imageView);
        this.btnOpenCamera = findViewById(R.id.btnOpenCamera);
        this.btnOpenGallery = findViewById(R.id.btnOpenGallery);
        this.btnImageProcess = findViewById(R.id.btnImageProcess);
    }

    private void initializeEvent() {
        this.btnOpenCamera.setOnClickListener(this.btnOpenCameraClick);
        this.btnOpenGallery.setOnClickListener(this.btnOpenGalleryClick);
        this.btnImageProcess.setOnClickListener(this.btnImageProcessClick);
    }

    private View.OnClickListener btnOpenGalleryClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, MyConstants.GALLERY_IMAGE_LOADED);
        }
    };

    private View.OnClickListener btnOpenCameraClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File file = createImageFile();
            boolean isDirectoryCreated = file.getParentFile().mkdirs();
            Log.d("", "openCamera: isDirectoryCreated: " + isDirectoryCreated);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri tempFileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", // As defined in Manifest
                        file);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
            } else {
                Uri tempFileUri = Uri.fromFile(file);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
            }
            startActivityForResult(cameraIntent, MyConstants.CAMERA_LOADED);
        }
    };

    private View.OnClickListener btnImageProcessClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //save selected bitmap to our constants
            //this method will save the image to our device memory
            //so set this variable to null after the image is no longer used
            MyConstants.selectedImageBitmap = selectedBitmap;

            //create new intent to start process image
            Intent intent = new Intent(getApplicationContext(), ImageCropActivity.class);
            startActivity(intent);

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyConstants.CAMERA_LOADED:
                    this.loadImage();
                    break;
                case MyConstants.GALLERY_IMAGE_LOADED:
                    selectedImage = data.getData();
                    this.loadImage();
                    break;
            }
        } else {
            Toast.makeText(this, "Result not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(this.selectedImage);
            selectedBitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(selectedBitmap);
            btnImageProcess.setEnabled(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() {
        clearTempImages();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
                Date());
        File file = new File(MyConstants.IMAGE_PATH, "IMG_" + timeStamp +
                ".jpg");
        selectedImage = Uri.fromFile(file);
        return file;
    }

    private void clearTempImages() {
        try {
            File tempFolder = new File(MyConstants.IMAGE_PATH);
            for (File f : tempFolder.listFiles())
                f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
