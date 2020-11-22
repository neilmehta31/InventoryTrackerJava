package com.bitspilani.inventorytrackerjava.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitspilani.inventorytrackerjava.MainActivity;
import com.bitspilani.inventorytrackerjava.R;
import com.bitspilani.inventorytrackerjava.dashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.internal.InternalTokenProvider;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {
    private static final String TAG = "Register";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    EditText rUserName, rUserPass,rUserConfPass,rUserEmail,rUserPhone;
    Button signUpBtn,signInUsingEmail;
    TextView loginAct;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rUserName = findViewById(R.id.registerName);
        rUserPass = findViewById(R.id.registerPassword);
        rUserConfPass = findViewById(R.id.regiter_confirm_password);
        rUserEmail = findViewById(R.id.registerEmail);
        rUserPhone = findViewById(R.id.registerPhone);
        signUpBtn = findViewById(R.id.registerBtn);
        fAuth = FirebaseAuth.getInstance();
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uUsername = rUserName.getText().toString();
                String uUserEmail = rUserEmail.getText().toString();
                String uUserPass = rUserPass.getText().toString();
                String uUserConfPass = rUserConfPass.getText().toString();
                String phone = rUserPhone.getText().toString();
                String uUserPhone = phone;

                if (uUserEmail.isEmpty() || uUsername.isEmpty() || uUserPass.isEmpty() || uUserConfPass.isEmpty() || uUserPhone.isEmpty()) {
                    Toast.makeText(Register.this, "All Fields Are Required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rUserPhone.getText().toString().length()!=10){
                    rUserPhone.setError( "Enter a valid phone number");
                    return;
                }
                if (!uUserPass.equals(uUserConfPass)) {
                    rUserConfPass.setError("Password Do not Match. Password should be at least 6 letters long");
                    return;
                }else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Register.this);
                    alertDialogBuilder.setTitle("Choose your preferred option for verification");
                    alertDialogBuilder.setMessage("Choose if you want to verify yourself using email or phone number")
                            .setCancelable(false)
                            .setPositiveButton("Email verification", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    fAuth.createUserWithEmailAndPassword(uUserEmail, uUserPass).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "createUserWithEmail:success");
                                                FirebaseUser user = fAuth.getCurrentUser();

                                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(uUsername)
                                                        .build();
                                                user.updateProfile(request);

                                                try {
                                                    if (user != null)
                                                        user.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "Email sent.");

                                                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                                                    Register.this);

                                                                            // set title
                                                                            alertDialogBuilder.setTitle("Please Verify Your EmailID");

                                                                            // set dialog message
                                                                            alertDialogBuilder
                                                                                    .setMessage("A verification Email Is Sent To Your Registered EmailID, please click on the link and Sign in again!")
                                                                                    .setCancelable(false)
                                                                                    .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                                            Register.this.finish();
                                                                                        }
                                                                                    });
                                                                            // create alert dialog
                                                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                                                            // show it
                                                                            alertDialog.show();
                                                                        }
                                                                    }
                                                                });

                                                } catch (Exception e) {
                                                    Log.d("second last exception",e.getMessage());//2nd last exception
                                                }
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(Register.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();

                                                if (task.getException() != null) {
                                                    Log.e("last task exception","last task exception",task.getException());//last exception
                                                }

                                            }

                                        }
                                    });

                                }
                            }).setNegativeButton("Phone number Verification", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), PhoneLogin.class);

                            intent.putExtra("phoneNumber",uUserPhone);
                            startActivity(intent);
                        }
                    });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}