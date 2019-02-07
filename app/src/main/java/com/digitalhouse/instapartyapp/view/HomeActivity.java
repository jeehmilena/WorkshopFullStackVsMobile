package com.digitalhouse.instapartyapp.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.digitalhouse.instapartyapp.R;
import com.digitalhouse.instapartyapp.adapter.RecyclerViewPhotosAdapter;
import com.digitalhouse.instapartyapp.model.Photos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_CAPTURE_IMAGE = 150;
    private FloatingActionButton btnAdicionar;
    private RecyclerView recyclerView;
    private RecyclerViewPhotosAdapter adapter;
    private FirebaseStorage firebaseStorage;
    private ArrayList<Photos> photosList = new ArrayList<>();
    private ProgressBar progressBar;
    private String photoFilePath;
    private String photoFileName;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Vamos desenvolver juntas


    }

    private void initViews() {
        btnAdicionar = findViewById(R.id.floatingActionButtonAdd);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerViewPhotosAdapter(photosList);
        firebaseStorage = FirebaseStorage.getInstance();
        progressBar = findViewById(R.id.progressbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        photoFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(photoFileName, ".jpg", storageDir);

        photoFileName = photoFileName + ".jpg";
        photoFilePath = image.getAbsolutePath();
        return image;
    }

    private void openCameraIntent() {

        progressBar.setVisibility(View.VISIBLE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERMISSION);
            return;
        }

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("LOG", "Erro ao criar caminho para a imagem");
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.digitalhouse.instapartyapp.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            Log.i("LOG", "imageFilePath: " + photoFilePath);
            saveImageOnFirebaseStorage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CAMERA_PERMISSION) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("LOG", "Camera permission granted - initialize the camera source");
            openCameraIntent();
        }
    }

    private void saveImageOnFirebaseStorage() {

        try {
            StorageReference imagesRef = firebaseStorage.getReference().child("images");
            StorageReference childRef = imagesRef.child(photoFileName);
            databaseReference = FirebaseDatabase.getInstance().getReference("images");

            InputStream stream;
            stream = new FileInputStream(new File(photoFilePath));
            UploadTask uploadTask = childRef.putStream(stream);


            uploadTask.addOnFailureListener(exception ->
                    Log.i("LOG", "Não foi possivel efetuar o upload: " + exception.getMessage())
            ).addOnSuccessListener(taskSnapshot -> {


                Toast.makeText(HomeActivity.this, "Upload efetuado com sucesso!", Toast.LENGTH_SHORT).show();

                childRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String name = taskSnapshot.getMetadata().getName();
                            photosList.add(new Photos(uri.toString(), name));

                           // List<Photos> photosList = new ArrayList<>();
                            Photos photos = new Photos(uri.toString(), name);
                            DatabaseReference imgReference = databaseReference.push();
                            imgReference.setValue(photos);
                            adapter.update(photosList);
                            progressBar.setVisibility(View.GONE);
                        }
                );
            });

        } catch (FileNotFoundException e) {
            Log.e("LOG", "saveImageOnFirebaseStorage: ", e);
        }
    }

    private void getAllPhotos() {
        databaseReference = FirebaseDatabase.getInstance().getReference("images");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //String name = postSnapshot.getKey();
                    //String url = postSnapshot.getValue(String.class);
                    Map<String, String> mapPhotos = (Map<String, String>) postSnapshot.getValue();
                    Photos photo = new Photos(mapPhotos.get("urlPhoto"), mapPhotos.get("tituloPhoto"));
                    photosList.add(photo);
                }

                adapter = new RecyclerViewPhotosAdapter(photosList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
