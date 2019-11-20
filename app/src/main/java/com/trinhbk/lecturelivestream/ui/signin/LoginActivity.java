package com.trinhbk.lecturelivestream.ui.signin;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.youtube.YouTubeScopes;
import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.ui.base.BaseActivity;
import com.trinhbk.lecturelivestream.utils.AppPreferences;
import com.trinhbk.lecturelivestream.utils.Constants;

import java.util.Arrays;

/**
 * Created by TrinhBK on 8/29/2018.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = LoginActivity.class.getSimpleName();

    public static final String SCOPE_YOUTUBE = YouTubeScopes.YOUTUBE;
    public static final String SCOPE_PROFILE = Scopes.PROFILE;


    private AppCompatButton ivFacebook;
    private Button btnGoogle;

    private CallbackManager callbackManager;

    private GoogleSignInClient mGoogleSignInClient;
    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initGoogle();
        initFacebook();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            mAccount = account.getAccount();
//        }
//    }

    private void initView() {
        btnGoogle = findViewById(R.id.btnGoogle);
        ivFacebook = findViewById(R.id.btnFacebook);
        btnGoogle.setOnClickListener(this);
        ivFacebook.setOnClickListener(this);
    }

    private void initGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(SCOPE_YOUTUBE), new Scope(SCOPE_PROFILE))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code\
                        Log.d(TAG, "onSuccess: " + loginResult.getAccessToken().getToken());
                        AppPreferences.INSTANCE.setKeyBoolean(Constants.KeyPreference.IS_LOGINED, true);
                        AppPreferences.INSTANCE.setKeyBoolean(Constants.KeyPreference.LOGIN_FROM_FACEBOOK, true);
                        signInSuccessful(true);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d(TAG,"");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d(TAG,"");
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGoogle:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, Constants.RequestCode.RC_SIGN_IN);
                break;
            case R.id.btnFacebook:
                LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_video"));
                break;
        }
    }

    private void signInSuccessful(boolean isLoginFromGoogle) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.RC_SIGN_IN) {
//            Auth.GoogleSignInApi.signOut(mGoogleSignInClient).setResultCallback(status -> {});
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {});
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Get the account from the sign in result
                GoogleSignInAccount account = task.getResult(ApiException.class);


                // Store the account from the result
                mAccount = account.getAccount();
                AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.ACCOUNT_NAME, mAccount.name);
                AppPreferences.INSTANCE.setKeyBoolean(Constants.KeyPreference.IS_LOGINED, true);
                AppPreferences.INSTANCE.setKeyBoolean(Constants.KeyPreference.LOGIN_FROM_GOOGLE, true);
                // Asynchronously access the People API for the account
                signInSuccessful(true);
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                // Signed out, show unauthenticated UI.
                //updateUI(false);
            }
        }

//        // Handling a user-recoverable auth exception
//        if (requestCode == RC_RECOVERABLE) {
//            if (resultCode == RESULT_OK) {
//            } else {
//                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed:" + connectionResult);
    }
}
