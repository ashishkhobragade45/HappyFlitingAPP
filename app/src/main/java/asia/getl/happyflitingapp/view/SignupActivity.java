package asia.getl.happyflitingapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import asia.getl.happyflitingapp.R;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,checkPassword,address,pincode,mobile_number,name;
    private Button btnSignIn, btnSignUp, btnResetPassword,profile_pic_button;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        findViewById();



        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profile_pic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String checkpassword = checkPassword.getText().toString().trim();
                final String Address = address.getText().toString().trim();
                final String Pincode = pincode.getText().toString().trim();
                final String Mobile_number = mobile_number.getText().toString().trim();
                final String Name = name.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(checkpassword)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkpassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkpassword.equals(password))
                {
                    Toast.makeText(getApplicationContext(), "Password and Check Password not match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Address)) {
                    Toast.makeText(getApplicationContext(), "Enter Address address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Pincode)) {
                    Toast.makeText(getApplicationContext(), "Enter Pin-code!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Pincode.length()!=6) {
                    Toast.makeText(getApplicationContext(), "Enter correct Pin-code!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Mobile_number)) {
                    Toast.makeText(getApplicationContext(), "Enter Contact Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(Name)) {
                    Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
                    return;
                }



                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "Completed", Toast.LENGTH_SHORT).show();




                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else {

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("userinfo").child(mobile_number.getText().toString().trim());
                                    myRef.child("name").setValue(Name);
                                    myRef.child("address").setValue(Address);
                                    myRef.child("pincode").setValue(Pincode);
                                    myRef.child("email").setValue(email);
                                    myRef.child("mobile").setValue(Mobile_number);

                                    Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    private void uplodeImage() {

        //Firebase
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(mobile_number.getText().toString().trim());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(SignupActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignupActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void findViewById() {

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        checkPassword = (EditText) findViewById(R.id.checkPassword);
        address = (EditText) findViewById(R.id.address);
        pincode = (EditText) findViewById(R.id.pincode);
        mobile_number = (EditText) findViewById(R.id.mobile_number);
        name = (EditText)findViewById(R.id.name);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        profile_pic_button = (Button)findViewById(R.id.profile_pic_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();

            uplodeImage();
           /* try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }*/
        }
    }

}