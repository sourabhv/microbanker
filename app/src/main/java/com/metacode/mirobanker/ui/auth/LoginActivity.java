package com.metacode.mirobanker.ui.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.metacode.mirobanker.BuildConfig;
import com.metacode.mirobanker.R;
import com.metacode.mirobanker.databinding.ActivityLoginBinding;
import com.metacode.mirobanker.ui.list.ListActivity;
import com.metacode.mirobanker.util.SimpleTextWatcher;

public class LoginActivity extends AppCompatActivity implements OnCompleteListener<AuthResult> {

    private FirebaseAuth mFirebaseAuth;
    private ActivityLoginBinding mBindings;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBindings = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mBindings.loginButton.setOnClickListener(v -> {
            mBindings.error.setVisibility(View.GONE);
            String email = mBindings.emailEditText.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mBindings.emailLayout.setError(getString(R.string.login_error_email_invalid));
                return;
            } else {
                mBindings.emailLayout.setErrorEnabled(false);
            }
            String password = mBindings.passwordEditText.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mBindings.passwordLayout.setError(getString(R.string.login_error_password_empty));
                return;
            } else {
                mBindings.passwordLayout.setErrorEnabled(false);
            }

            mProgressDialog = ProgressDialog.show(LoginActivity.this, null, getString(R.string.login_dialog_message), true, false);
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this);
        });
        mBindings.loginButton.setOnLongClickListener(v -> {
            // Only for testing
            if (BuildConfig.DEBUG) {
                mBindings.emailEditText.setText("test@lifcare.in");
                mBindings.passwordEditText.setText("testtest");
                return true;
            }
            return false;
        });
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (!task.isSuccessful()) {
            mBindings.error.setText(R.string.login_error_auth_failed);
            mBindings.error.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(this, ListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
