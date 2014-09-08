package com.matteolupatelli.dspproject.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;

/**
 * Created by matteo on 15/08/14.
 */
public class SignupActivity extends Activity implements View.OnClickListener {
    EditText usernameField;
    EditText passwordField;
    EditText retypepasswordusernameField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameField = (EditText) findViewById(R.id.registerUsername);

        passwordField = (EditText) findViewById(R.id.passwordRegisterUsername);

        retypepasswordusernameField = (EditText) findViewById(R.id.retypepasswordRegisterUsername);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);


    }

    public void onClick(View view) {



        if (!(passwordField.getText().toString().equals(retypepasswordusernameField.getText().toString()))) {
            Toast errorToast = Toast.makeText(getApplicationContext(), "ERROR:Different password", Toast.LENGTH_SHORT);
            errorToast.show();
            usernameField.setText("");
            passwordField.setText("");
            retypepasswordusernameField.setText("");
        } else {
            BaasUser user = BaasUser.withUserName(usernameField.getText().toString())
                    .setPassword(passwordField.getText().toString());
            user.signup(new BaasHandler<BaasUser>() {
                @Override
                public void handle(BaasResult<BaasUser> result) {
                    if (result.isSuccess()) {
                        Log.d("LOG", "Current user is: " + result.value());
                        Toast oksignupToast = Toast.makeText(getApplicationContext(), "USER CREATED", Toast.LENGTH_SHORT);
                        oksignupToast.show();
                        startLoginActivity();

                    } else {
                        Log.e("LOG", "Show error", result.error());
                        Toast kosignupToast = Toast.makeText(getApplicationContext(), result.error().getMessage(), Toast.LENGTH_SHORT);
                        kosignupToast.show();
                    }
                }
            });
        }

    }



    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}