package ec.edu.dordonez.parkingcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dordonez on 2017/12/22.
 * Created by dordonez on 2018/01/02.
 */

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    private Camera camera;
    private SurfaceView sv;
    private SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        sv = (SurfaceView) findViewById(R.id.sv);
        holder = sv.getHolder();
        holder.addCallback(holderCallback);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        camera = Camera.open();
    }

    public void tomaFoto(View v) {
        Camera.PictureCallback camCB = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                try {
                    String fName = "parking" + System.currentTimeMillis() + ".jpeg";
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fName);
                    FileOutputStream out = new FileOutputStream(file);
                    picture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    picture.recycle();
                    camera.startPreview();
                } catch (Exception e) {

                }
            }
        };
        camera.takePicture(null, null, camCB);
    }

    private SurfaceHolder.Callback holderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(holder);//Indica dónde dibujar las imágenes
                //camera.setPreviewCallback(previewCallback);//Activa onPreviewFrame
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Preview", "No se puede inciar el preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            try {
                //Controla la rotación de la pantalla
                switch(getWindowManager().getDefaultDisplay().getRotation()) {
                    case Surface.ROTATION_0:
                        camera.setDisplayOrientation(90);
                        break;
                    case Surface.ROTATION_90:
                        //Nada que cambiar
                        break;
                    case Surface.ROTATION_180:
                        //camera.setDisplayOrientation(90);
                        break;
                    case Surface.ROTATION_270:
                        //camera.setDisplayOrientation(90);
                        break;
                }

            } catch (Exception e) {}
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                camera.stopPreview();
            } catch (Exception e) {}
        }

    };

    @Override
    protected void onDestroy() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onDestroy();
    }
}
