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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private static final String TAG = "Register";
    EditText rUserName, rUserPass,rUserConfPass,rUserEmail;
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

        signUpBtn = findViewById(R.id.registerBtn);
        fAuth = FirebaseAuth.getInstance();
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uUsername = rUserName.getText().toString();
                String uUserEmail = rUserEmail.getText().toString();
                String uUserPass = rUserPass.getText().toString();
                String uUserConfPass = rUserConfPass.getText().toString();

                if (uUserEmail.isEmpty() || uUsername.isEmpty() || uUserPass.isEmpty() || uUserConfPass.isEmpty()) {
                    Toast.makeText(Register.this, "All Fields Are Required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!uUserPass.equals(uUserConfPass)) {
                    rUserConfPass.setError("Password Do not Match. Password should be at least 6 letters long");
                }

//
                fAuth.createUserWithEmailAndPassword(uUserEmail, uUserPass).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = fAuth.getCurrentUser();

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

                                                                        Intent signInIntent = new Intent(Register.this, dashboard.class);
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
        });

    }

    private void updateUIregister(FirebaseUser user,  String uUsername) {
    if (user!=null){
        String personName = uUsername;
        String personemail = user.getEmail();

        Toast.makeText(this, "Welcome " + personName, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,dashboard.class);
        intent.putExtra("personName",personName);
        intent.putExtra("personemail",personemail);
        startActivity(intent);
        finish();
    }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}