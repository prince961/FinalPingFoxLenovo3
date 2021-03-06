package com.example.mohit.finalpingfoxlenovo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private Button BLogin;
    private SignInButton gButton;
    private FirebaseAuth mAuth;
    GoogleApiClient googleApiClient ;
    private final static int RC_SIGN_IN = 2;
    GoogleSignInClient mGoogleSignInClient;private GoogleSignInAccount account;
    private DatabaseReference databaseReference;
    FirebaseDatabase database;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = database.getReference();
        sharedPreferences = getApplicationContext().getSharedPreferences("UserSP",0);
        editor = sharedPreferences.edit();

        SignInButton gButton;
        gButton = (SignInButton) findViewById(R.id.googleSignInBtn);
        gButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("ClickListner", "button pressed");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                Log.w("ClickListner", "Data sent");


            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            //Create a user account on firebase with firebaseAuthWithGoogle method
            firebaseAuthWithGoogle(account);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d("firebase auth", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("firebase auth", "signInWithCredential:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            FirebaseUserMetadata metadata = firebaseUser.getMetadata();
                            User localUser = new User(firebaseUser.getDisplayName(),firebaseUser.getEmail());
                            databaseReference.child("users").child(firebaseUser.getUid()).setValue(localUser);
                            assert metadata != null;
                            if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                                // The user is new, show them a fancy intro screen!
                                editor.putBoolean("UserLoggedIn",true);
                                editor.putBoolean("NewUser",true);
                                Log.d("LoggedInUser","new user");
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(intent);
                                //fragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).commit();



                            } else {
                                Log.d("LoggedInUser","old user");
                                editor.putBoolean("UserLoggedIn",true);
                                editor.putBoolean("NewUser",false);
                                //fragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).commit();
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(intent);

                                //Log.d("phoneNumbber", Objects.requireNonNull(user.getPhoneNumber()));

                                // This is an existing user, show them a welcome back screen.
                            }
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("firebase auth", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }








    public void RegisterText(View view) {
        Snackbar.make(view, "Please use Google sign In", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void LoginButton(View view) {
        Snackbar.make(view, "Please use Google sign In", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

   /* public void googleLogin(View view) {
        Log.w("ClickListner", "button pressed");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.w("ClickListner", "Data sent");qweeww
    }*/
}
