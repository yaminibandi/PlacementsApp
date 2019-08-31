package com.develop.android.placements;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.util.JsonToken.NULL;
import static java.lang.Thread.sleep;


public class ShowCompanyDetails extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCompanyDatabaseReference;
    private FirebaseStorage storage;
    private ChildEventListener mChildEventListener;
    private StorageReference storageReference;
    TextView cname,jd,ec;
    Button showid,showiq;
    HashMap<String, String> jdhm = new HashMap<String, String>();
    HashMap<String, String> echm = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_company_details);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCompanyDatabaseReference = mFirebaseDatabase.getReference().child("Company");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        cname=(TextView)findViewById(R.id.CompanyName);

        final TextView jdhead=(TextView)findViewById(R.id.JobDescription);
        final TextView echead=(TextView)findViewById(R.id.EligibityCriteria);

        jd=(TextView)findViewById(R.id.JobDescriptionDetails);
        ec=(TextView)findViewById(R.id.EligibityCriteriaDetails);
        showid=(Button)findViewById(R.id.downloadinterviewdetails);
        showiq=(Button)findViewById(R.id.downloadinterviewquestions);
        Intent intent = getIntent();
        final String companyName = intent.getStringExtra("companyName");
        Query query=mCompanyDatabaseReference.child(companyName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CompanyDetails cd=dataSnapshot.getValue(CompanyDetails.class);
                cname.setText(cd.companyName);
                jdhm=cd.jd;
                echm=cd.ec;
                if((NULL).equals(jdhm)) {
                    for (Map.Entry mapElement : jdhm.entrySet()) {
                        String key = (String) mapElement.getKey();
                        String value = (String) mapElement.getValue();
                        jd.setText(jd.getText() + key + " : " + value + "\n");
                    }
                }
                else
                {
                    jdhead.setVisibility(View.GONE);
                }
                if((NULL).equals(echm)) {
                    for (Map.Entry mapElement : echm.entrySet()) {
                        String key = (String) mapElement.getKey();
                        String value = (String) mapElement.getValue();
                        ec.setText(ec.getText() + key + " : " + value + "\n");
                    }
                }
                else
                {
                    echead.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        showiq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference ref = storageReference.child(companyName).child("Files/"+ "Interview_questions");


                String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Attendance/";
                File file = new File(directory_path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String targetPdf = directory_path + companyName+" Interview_Questions"+".pdf";
                File filePath = new File(targetPdf);
                try {
                    if (hasWritePermissions()) {
                        document.writeTo(new FileOutputStream(filePath));
                        try {
                            sleep(1000);
                        } catch (Exception e) {

                        }
                        Toast.makeText(ShowCompanyDetails.this, "Pdf File Saved", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    Toast.makeText(ShowCompanyDetails.this, "You didn't permit storage access", Toast.LENGTH_SHORT).show();
                }


                File localFile = new File(Environment.getExternalStorageDirectory() + "/Attendance/"+);
                ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        Toast.makeText(ShowCompanyDetails.this,"Yay",Toast.LENGTH_LONG);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Toast.makeText(ShowCompanyDetails.this,exception.getMessage(),Toast.LENGTH_LONG);
                    }
                });
            }
        });
        showid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference ref = storageReference.child(companyName).child("Files/"+ "d");
                File localFile = new File(Environment.getExternalStorageDirectory() + "/Placements/"+companyName+" Interview_Details"+".pdf");
                ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        Toast.makeText(ShowCompanyDetails.this,"Yay",Toast.LENGTH_LONG);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Toast.makeText(ShowCompanyDetails.this,exception.getMessage(),Toast.LENGTH_LONG);

                    }
                });
            }
        });
    }
    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 112); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
}
