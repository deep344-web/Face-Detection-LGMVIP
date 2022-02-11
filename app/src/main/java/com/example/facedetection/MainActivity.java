package com.example.facedetection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final private String TAG = "Main Activity";
    final private int  PHOTO_PICKER = 121;
    final private int PHOTO_CLICK = 122;

    ImageView faceDetectedImage;
    Button clickPicturefromCamera, selectPicture;

    private Alertfragment alertfragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickPicturefromCamera = findViewById(R.id.clickpicture);
        selectPicture = findViewById(R.id.selectpicture);

        faceDetectedImage = findViewById(R.id.resultImage);

        clickPicturefromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,PHOTO_CLICK);
            }
        });

        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PHOTO_PICKER);
            }
        });


    }


    void detectFace(Bitmap bitmap) throws IOException {

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);


        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);


        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {

                                        faceDetectedImage.setImageBitmap(mutableBitmap);

                                        for (Face face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            Log.i(TAG, bounds.toString());
                                            Paint paint = new Paint();
                                            paint.setColor(Color.BLUE);
                                            paint.setStrokeWidth(4);
                                            paint.setStyle(Paint.Style.STROKE);
                                            canvas.drawRect(bounds, paint);

                                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                            Paint paint1 = new Paint();
                                            paint1.setColor(Color.YELLOW);
                                            paint1.setStyle(Paint.Style.STROKE);
                                            paint1.setStrokeWidth(3);



                                        }

                                        alertfragment = new Alertfragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("faceDetected", true);
                                        alertfragment.setArguments(bundle);
                                        alertfragment.show((MainActivity.this).getSupportFragmentManager(),"Image Dialog");

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        alertfragment = new Alertfragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("faceDetected", false);
                                        alertfragment.setArguments(bundle);
                                        alertfragment.show((MainActivity.this).getSupportFragmentManager(),"Image Dialog");

                                    }
                                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_CLICK) {
            if (resultCode == RESULT_OK) {

                Uri imageUri = data.getData();
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    detectFace(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(MainActivity.this, "No Image clicked", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == PHOTO_PICKER){
            if(resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    detectFace(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else{
                Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}