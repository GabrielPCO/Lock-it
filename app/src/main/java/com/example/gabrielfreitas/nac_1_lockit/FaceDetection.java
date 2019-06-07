package com.example.gabrielfreitas.nac_1_lockit;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
//import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import com.example.gabrielfreitas.nac_1_lockit.Helper.GraphicOverlay;
import com.example.gabrielfreitas.nac_1_lockit.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

//import java.io.File;
//import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class FaceDetection extends AppCompatActivity {

    CameraView cameraView;
    GraphicOverlay graphicOverlay;
    Button btnDetect;
    Button btnVoltar;

    AlertDialog waitingDialog;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

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
                startActivity(new Intent(FaceDetection.this, MapsActivity.class));
            }
        });

        //event
        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();

            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialog.show();

                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();
                runFaceDetector(bitmap);
                //graphicOverlay.clear();
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

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
                        Toast.makeText(FaceDetection.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        btnDetect.setVisibility(View.VISIBLE);
        btnVoltar.setVisibility(View.VISIBLE);
        Toast toast = Toast.makeText(this, String.format(Locale.US,"%d Face(s) na imagem",count), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, -100);
        toast.show();
    }
}
