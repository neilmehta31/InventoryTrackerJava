package com.bitspilani.inventorytrackerjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitspilani.inventorytrackerjava.auth.Register;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private GoogleSignInClient mGoogleSignInClient;
    EditText loginemail,loginpassword;
    private SignInButton signInButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String email,password;
    private Button registerBtnMA,signInUsingEmailBtn;
    private final static int RC_SIGN_IN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("MyNotifications",  "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        signInButton = (SignInButton) findViewById(R.id.googleBtn);

        mAuth = FirebaseAuth.getInstance();
        loginemail = findViewById(R.id.emailText);
        loginpassword = findViewById(R.id.PasswordText);
        email =  loginemail.getText().toString();
        password = loginpassword.getText().toString();
        signInButton = (SignInButton) findViewById(R.id.googleBtn);
        signInUsingEmailBtn  = findViewById(R.id.btnLoginEmail);
        registerBtnMA = findViewById(R.id.registerUsingEmail);
        registerBtnMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class ));
            }
        });
        signInUsingEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loginemail.getText().toString().contentEquals("")) {
                    Toast.makeText(MainActivity.this, "Email cant be empty", Toast.LENGTH_SHORT).show();
                } else if (loginpassword.getText().toString().contentEquals("")) {
                    Toast.makeText(MainActivity.this, "Password cant be empty", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(loginemail.getText().toString(), loginpassword.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");

                                        FirebaseUser user = mAuth.getCurrentUser();

                                        if (user != null) {
                                            if (user.isEmailVerified()) {


                                                System.out.println("Email Verified : " + user.isEmailVerified());
                                                updateUIemaillogin(user);


                                            } else {


                                                Toast.makeText(MainActivity.this, "Please Verify your EmailID and SignIn", Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        if (task.getException() != null) {
                                            Log.e("sdfas","I dont know what is this!!!!!!!!!!!!\t\t",task.getException());

                                        }

                                    }

                                }
                            });

                }


            }
        });

        // Configure Google Sign In

        currentUser = mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }

    private void updateUIemaillogin(FirebaseUser user) {
        if (user!=null){
            String personName = user.getDisplayName();
            String personemail = user.getEmail();

            Toast.makeText(this, "Welcome " + personName, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,dashboard.class);
            intent.putExtra("personName",personName);
            intent.putExtra("personemail",personemail);
            startActivity(intent);
            finish();
        }

    }

    //This is first pr
    private void signIn() {
        mGoogleSignInClient.signOut();          //This line of code deletes any cache of previous signed in user and asks for pop up
                                                //to user to choose an account to sign in into the app.
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, " Sign In Failed", Toast.LENGTH_SHORT).show();

                Log.w(TAG, "Google sign in failed", e);
                firebaseAuthWithGoogle(null);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser fUser) {
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account!=null){
            String personName = account.getDisplayName();
            String personemail = account.getEmail();
            String personid = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            Toast.makeText(this, "Welcome " + personName, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,dashboard.class);
            intent.putExtra("personName",personName);
            intent.putExtra("personemail",personemail);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null){
            updateUI(currentUser);
        }
    }

}
