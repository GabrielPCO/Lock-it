package com.example.gabrielfreitas.nac_1_lockit;

//import android.Manifest;
//import android.app.Activity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
//import android.content.pm.PackageManager;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Rect;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//import android.os.Environment;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
//import android.view.Gravity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

//import com.camerakit.CameraKitView;
//import com.journeyapps.barcodescanner.Util;
//import com.example.gabrielfreitas.nac_1_lockit.Helper.GraphicOverlay;
//import com.example.gabrielfreitas.nac_1_lockit.Helper.RectOverlay;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.ml.vision.FirebaseVision;
//import com.google.firebase.ml.vision.common.FirebaseVisionImage;
//import com.google.firebase.ml.vision.face.FirebaseVisionFace;
//import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
//import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

//import org.jetbrains.annotations.NotNull;

//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
//import java.util.List;
//import java.util.Locale;

import dmax.dialog.SpotsDialog;
import edmt.dev.edmtdevcognitiveface.Contract.Face;
import edmt.dev.edmtdevcognitiveface.Contract.IdentifyResult;
import edmt.dev.edmtdevcognitiveface.Contract.Person;
import edmt.dev.edmtdevcognitiveface.Contract.TrainingStatus;
import edmt.dev.edmtdevcognitiveface.FaceServiceClient;
import edmt.dev.edmtdevcognitiveface.FaceServiceRestClient;
import edmt.dev.edmtdevcognitiveface.Rest.ClientException;
import edmt.dev.edmtdevcognitiveface.Rest.Utils;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class FaceRecognition extends AppCompatActivity {

    private final String API_KEY="1a5d1ac1d39746b0942251158fcfb5bb";
    private final String API_LINK="https://brazilsouth.api.cognitive.microsoft.com/face/v1.0";

    private FaceServiceClient faceServiceClient = new FaceServiceRestClient(API_LINK,API_KEY);
    private final String personGroupID = "celebritiesactor";

    //CameraKitView cameraView;
    //GraphicOverlay graphicOverlay;
    ImageView img_view;
    Bitmap bitmap;
    Button btnDetect;
    Button btnVoltar;
    TextView txtFaceDetect;
    String pathToFile;

    private static final int REQUEST_CAPTURE_IMAGE = 1;

    //AlertDialog waitingDialog;
    Face[] faceDetected;

    class detectTask extends AsyncTask<InputStream,String,Face[]>{

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(FaceRecognition.this)
                .setCancelable(false)
                .build();

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            alertDialog.setMessage(values[0]);
        }

        @Override
        protected Face[] doInBackground(InputStream... inputStreams) {

            try{
                publishProgress("Detectando...");
                Log.d("FaceRecognition: !!!!!!","Fez detectTask");
                Face[] result = faceServiceClient.detect(inputStreams[0], true, false, null);
                if(result == null)
                {
                    return null;
                }
                else
                    {
                      return result;
                    }
            }catch (ClientException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            if(faces == null){
                Toast.makeText(FaceRecognition.this,"Nenhuma face detectada",Toast.LENGTH_SHORT).show();
            }
            else{
                img_view.setImageBitmap(Utils.drawFaceRectangleOnBitmap(bitmap,faces,Color.YELLOW));

                if(faceDetected.length>0){
                        final UUID[] faceIds = new UUID[faceDetected.length];
                        for (int i = 0; i < faceDetected.length; i++)
                            faceIds[i] = faceDetected[i].faceId;

                        new IdentificationTask().execute(faceIds);
                }else {
                    Toast.makeText(FaceRecognition.this,"Nenhuma face para detectadar", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class IdentificationTask extends AsyncTask<UUID,String, IdentifyResult[]>{

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(FaceRecognition.this)
                .setCancelable(false)
                .build();

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            alertDialog.setMessage(values[0]);
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... uuids) {
            try{
                publishProgress("Coletando status de grupo da pessoa...");
                TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(personGroupID);

                if(trainingStatus.status != TrainingStatus.Status.Succeeded){
                    Log.d("ERROR","Person Group Training status is"+ trainingStatus.status);
                    return null;
                }

                publishProgress("Identificando");
                IdentifyResult[] result = faceServiceClient.identity(personGroupID, uuids, 1); //1 is max number of candidates returned

                return result;

                }catch(ClientException e){
                    e.printStackTrace();
                }catch(IOException e){
                    e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
            alertDialog.dismiss();
            if(identifyResults!=null){
                for(IdentifyResult identifyResult:identifyResults)
                    new PersonDetectionTask().execute(identifyResult.candidates.get(0).personId);
            }
        }
    }

    class PersonDetectionTask extends AsyncTask<UUID, String, Person>{

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(FaceRecognition.this)
                .setCancelable(false)
                .build();

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            alertDialog.setMessage(values[0]);
        }

        @Override
        protected Person doInBackground(UUID... uuids) {
            try {
                return faceServiceClient.getPerson(personGroupID, uuids[0]);
            }catch (ClientException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Person person) {
            alertDialog.dismiss();
            txtFaceDetect.setVisibility(View.VISIBLE);
            btnDetect.setVisibility(View.VISIBLE);
            btnVoltar.setVisibility(View.VISIBLE);
            img_view.setImageBitmap(Utils.drawFaceRectangleWithTextOnBitmap(bitmap,faceDetected,person.name,Color.YELLOW,100));
        }
    }

    /*@Override
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
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        Log.d("FaceRecognition: !!!!!!","onCreate");

        //Init view
        //cameraView = findViewById(R.id.camera_view);
        //graphicOverlay = findViewById(R.id.graphic_overlay);
        btnDetect = findViewById(R.id.btn_detectar);
        btnVoltar = findViewById(R.id.btn_voltar);
        txtFaceDetect = findViewById(R.id.textView);
        img_view = findViewById(R.id.imageView);

        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }


        /*bitmap = BitmapFactory.decodeResource(getResources(),)
        img_view = findViewById(R.id.imageView);
        img_view.setImageBitmap(bitmap);*/
        /*waitingDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Por favor, aguarde...")
                .setCancelable(false)
                .build();*/

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FaceRecognition.this, MapsActivity.class));
            }
        });

        btnDetect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                dispatchPictureTakerAction();

                /*Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureName = getPictureName();
                File imageFile = new File(pictureDirectory,pictureName);
                Uri pictureUri = FileProvider.getUriForFile(FaceRecognition.this,  "com.example.gabrielfreitas.nac_1_lockit.fileprovider", imageFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);
                pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Log.d("FaceRecognition: !!!!!!","Clicou Botao Detect");
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
                /*if(pictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pictureIntent,
                            REQUEST_CAPTURE_IMAGE);
                }*/

            }
        });

        //event
        /*btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cameraView.captureImage(new CameraKitView.ImageCallback() {
                    File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                        Log.d("FaceRecognition: !!!!!!","Iniciou OnImage");

                        try {
                            btnDetect.setVisibility(View.GONE);
                            btnVoltar.setVisibility(View.GONE);
                            txtFaceDetect.setVisibility(View.GONE);
                            Log.d("FaceRecognition: !!!!!!","Iniciou OnImage");
                            //waitingDialog.show();

                            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                            outputStream.write(capturedImage);
                            outputStream.close();
                            Bitmap bitmap = BitmapFactory.decodeFile(savedPhoto.getAbsolutePath());
                            bitmap = Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                            img_view.setImageBitmap(bitmap);
                            ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,outputStream2);
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream2.toByteArray());
                            new detectTask().execute(inputStream);
                            Log.d("FaceRecognition: !!!!!!","Fez OnImage");

                            /*Bitmap bitmap = BitmapFactory.decodeFile(savedPhoto.getAbsolutePath());
                            bitmap = Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);*/


                            //runFaceDetector(bitmap);
                            //graphicOverlay.clear();
                        /*} catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RESULT_CANCELED) {
                if (resultCode == REQUEST_CAPTURE_IMAGE) {
                    Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                    img_view.setImageBitmap(bitmap);
                    Log.d("FaceRecognition: !!!!!!", "Bitmap para ImageView");
                }
            } else {
                Log.d("FaceRecognition: !!!!!!", "Bitmap para ImageView ERROR");
            }
    }

    private void dispatchPictureTakerAction() {

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager())!=null){

                pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureName = getPictureName();
                File imageFile = new File(pictureDirectory,pictureName);
                Uri pictureUri = FileProvider.getUriForFile(FaceRecognition.this,  "com.example.gabrielfreitas.nac_1_lockit.fileprovider", imageFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);
                pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Log.d("FaceRecognition: !!!!!!","Clicou Botao Detect");
                //setResult(RESULT_OK,pictureIntent);
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);


        }
    }

    /*private File createPhotoFile(){
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try{
            image = File.createTempFile(name,".jpg", storageDir);
        }catch (IOException e){
            Log.d("mylog","Excep : " + e.toString());
        }
        return image;
    }*/

    private String getPictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        String timestamp = sdf.format(new Date());
        return "Lock.it" +timestamp+".jpg";
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == REQUEST_CAPTURE_IMAGE &&
                resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {

                btnDetect.setVisibility(View.GONE);
                btnVoltar.setVisibility(View.GONE);
                txtFaceDetect.setVisibility(View.GONE);
                Log.d("FaceRecognition: !!!!!!","Iniciou OnImage");
                //waitingDialog.show();

                //bitmap = (Bitmap) data.getExtras().get("data");
                //img_view.setImageBitmap(bitmap);

                //FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                //outputStream.write(capturedImage);
                //outputStream.close();
                //Bitmap bitmap = BitmapFactory.decodeFile(savedPhoto.getAbsolutePath());
                //bitmap = Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                //img_view.setImageBitmap(bitmap);

                /*ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,outputStream2);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream2.toByteArray());
                new detectTask().execute(inputStream);
                Log.d("FaceRecognition: !!!!!!","Fez OnImage");*/
           // }
        //}
    //}

    /*private void runFaceDetector(Bitmap bitmap) {
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
    }*/

    /*private void processFaceResult(List<FirebaseVisionFace> firebaseVisionFaces) {
        int count = 0;
        for(FirebaseVisionFace face: firebaseVisionFaces){
            Rect bounds = face.getBoundingBox();
            //Draw rectangle
            RectOverlay rect = new RectOverlay(graphicOverlay,bounds);
            graphicOverlay.add(rect);

            count++;
        }
        waitingDialog.dismiss();
        txtFaceDetect.setVisibility(View.VISIBLE);
        btnDetect.setVisibility(View.VISIBLE);
        btnVoltar.setVisibility(View.VISIBLE);
        Toast toast = Toast.makeText(this, String.format(Locale.US,"%d Face(s) na imagem",count), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, -100);
        toast.show();
    }*/
}
