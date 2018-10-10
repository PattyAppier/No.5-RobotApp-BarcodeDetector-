package com.pattyappier.pattyluvapp.no5robot;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.Frame;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.widget.TextView;
import com.google.android.gms.vision.CameraSource;
import android.view.SurfaceHolder;
import java.io.IOException;
import com.google.android.gms.vision.Detector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap myQRCode = null;
        try {
            myQRCode = BitmapFactory.decodeStream(getAssets().open("myqrcode.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        Frame myFrame = new Frame.Builder()
                .setBitmap(myQRCode)
                .build();

        SparseArray<Barcode> barcodes = barcodeDetector.detect(myFrame);

        if (barcodes.size() != 0) {

            Log.d("My QR Code's Data",
                    barcodes.valueAt(0).displayValue
            );
        }

        SurfaceView cameraView = findViewById(R.id.camera_view);
        TextView barcodeInfo = findViewById(R.id.code_info);

        barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();


        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    cameraSource.start(cameraView.getHolder()); // cameraSource shall declared to be final

                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop(); // cameraSource shall declared to be final
            }

        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    // 因为receiveDetections在非UI线程中执行
                    barcodeInfo.post(new Runnable() { //barcodeInfo needs to be declared final
                        public void run() {
                            barcodeInfo.setText( //barcodeInfo needs to be declared final
                                    barcodes.valueAt(0).displayValue
                            );
                        }
                    });
                }

            }
        });






    }//on crate


}
