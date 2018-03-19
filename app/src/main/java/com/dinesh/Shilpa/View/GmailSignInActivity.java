package com.dinesh.Shilpa.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dinesh.Shilpa.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GmailSignInActivity extends AppCompatActivity {

    private static final String TAG = "ItemsSelectionFragmentOne";
    private static  final int RC_SIGN_IN = 1001;

    private SignInButton mSingInBtn;
    private Button mSignOutBtn;

    //private OnFragmentInteractionListener mListener;



    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_sign_in);



        mSingInBtn = findViewById(R.id.sign_in_button);
        mSignOutBtn = findViewById(R.id.sign_out_button);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestEmail()
                .build();
        // [START build_client]
        // Build a GoogleSignInClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        // [END build_client]
        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes.
        mSingInBtn.setSize(SignInButton.SIZE_STANDARD);
        // [END customize_button]

        mSingInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();
            }
        });

        mSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if the user is already signed in and all required scopes are granted
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && GoogleSignIn.hasPermissions(account, new Scope(Scopes.DRIVE_APPFOLDER))) {
            updateUI(account);
        } else {
            updateUI(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(@Nullable Task<GoogleSignInAccount> completeTask){
        Log.d(TAG, "handleSignInResult:" + completeTask.isSuccessful());

        try {
            // Signed in successfully, show authenticated U
            GoogleSignInAccount account = completeTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            // Signed out, show unauthenticated UI.
            Log.w(TAG, "handleSignInResult:error", e);
            updateUI(null);
        }
    }


    private void updateUI(@Nullable GoogleSignInAccount account){
        if(account != null){
            mSingInBtn.setVisibility(View.GONE);
        }else {
            mSingInBtn.setVisibility(View.VISIBLE);
        }
    }


    private void singIn() {
        Intent signInClient = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInClient, RC_SIGN_IN);
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // START_EXCLUDE
                updateUI(null);
                // END_EXCLUDE
            }
        });
    }

    private void removeAccess(){
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }
}
