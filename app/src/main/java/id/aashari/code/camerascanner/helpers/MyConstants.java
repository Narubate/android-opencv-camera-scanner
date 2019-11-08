package id.aashari.code.camerascanner.helpers;

import android.graphics.Bitmap;
import android.os.Environment;

public class MyConstants {
    public final static int CAMERA_LOADED = 1001;
    public final static int GALLERY_IMAGE_LOADED = 1002;
    public static Bitmap selectedImageBitmap;
    public final static String IMAGE_PATH = Environment
            .getExternalStorageDirectory().getPath() + "/CamScannerSample";
}
