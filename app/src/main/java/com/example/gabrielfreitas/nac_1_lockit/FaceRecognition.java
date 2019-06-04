package com.example.gabrielfreitas.nac_1_lockit;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.example.gabrielfreitas.nac_1_lockit.Helper.GraphicOverlay;
import com.example.gabrielfreitas.nac_1_lockit.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class FaceRecognition extends AppCompatActivity {

    CameraKitView cameraView;
    GraphicOverlay graphicOverlay;
    Button btnDetect;
    Button btnVoltar;

    AlertDialog waitingDialog;

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onPause() {
        cameraView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        //Init view
        cameraView = findViewById(R.id.camera_view);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        btnDetect = findViewById(R.id.btn_detectar);
        btnVoltar = findViewById(R.id.btn_voltar);
        waitingDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Por favor, aguarde...")
                .setCancelable(false)
                .build();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FaceRecognition.this, MapsActivity.class));
            }
        });

        //event
        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage(new CameraKitView.ImageCallback() {
                    File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                        try {
                            waitingDialog.show();

                            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                            outputStream.write(capturedImage);
                            outputStream.close();
                            Bitmap bitmap = BitmapFactory.decodeFile(savedPhoto.getAbsolutePath());
                            bitmap = Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                            runFaceDetector(bitmap);
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                graphicOverlay.clear();
            }
        });
        /*cameraView.setCameraListener(new CameraKitView.CameraListener() {
            @Override
            public void onOpened() {

            }

            @Override
            public void onClosed() {

            }
        });

        cameraView.setErrorListener(new CameraKitView.ErrorListener() {
            @Override
            public void onError(CameraKitView cameraKitView, CameraKitView.CameraException e) {

            }
        });*/

    }

    private void runFaceDetector(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        processFaceResult(firebaseVisionFaces);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FaceRecognition.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processFaceResult(List<FirebaseVisionFace> firebaseVisionFaces) {
        int count = 0;
        for(FirebaseVisionFace face: firebaseVisionFaces){
            Rect bounds = face.getBoundingBox();
            //Draw rectangle
            RectOverlay rect = new RectOverlay(graphicOverlay,bounds);
            graphicOverlay.add(rect);

            count++;
        }
        waitingDialog.dismiss();
        Toast.makeText(this, String.format(Locale.US,"%d Face(s) na imagem",count), Toast.LENGTH_SHORT).show();
    }
}
