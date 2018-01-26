package com.example.rspoo.inclass11;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailField;
    private EditText mPasswordField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "LoginActivity";
    ProgressDialog mProgress;
    Button mLogin,signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogin = (Button)findViewById(R.id.buttonLogin);

        signup = (Button)findViewById(R.id.Signup_button);
        signup.setOnClickListener(this);
        mEmailField = (EditText)findViewById(R.id.editTextEmail);
        mPasswordField = (EditText)findViewById(R.id.editTextPassword);

        mLogin.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Intent Expense = new Intent(MainActivity.this,Message.class);
                    Expense.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(Expense);

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {

                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);

            }
        };


    }




    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "You are not authorized",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent Expense = new Intent(MainActivity.this,Message.class);
                            startActivity(Expense);
                        }
                        hideProgressDialog();
                    }
                });

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }


    private void hideProgressDialog() {
        mProgress.hide();
    }

    private void showProgressDialog() {
        mProgress = new ProgressDialog(MainActivity.this);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setCancelable(false);
        mProgress.setMessage("Please Wait...");
        mProgress.show();
    }



    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void updateUI(FirebaseUser user) {
        mEmailField.setText("");
        mPasswordField.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.buttonLogin:
                FirebaseAuth.getInstance().signOut();
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.Signup_button:
                Intent signUP = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(signUP);
                break;
        }
    }
}

