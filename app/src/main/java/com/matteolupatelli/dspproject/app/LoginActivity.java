package com.matteolupatelli.dspproject.app;

import android.app.Activity;
import android.app.AlertDialog;
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


/**
 * Created by matteo on 15/08/14.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


      usernameEditText=(EditText) findViewById(R.id.username);
      passwordEditText=(EditText) findViewById(R.id.password);

      loginButton= (Button) findViewById(R.id.loginButton);

      loginButton.setOnClickListener(this);

      findViewById(R.id.signupLink).setOnClickListener(this);


    }

    public void onClick(View v) {
        if(v.getId()==R.id.loginButton) {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            onClickLogin(username, password);
        }
        else if(v.getId()==R.id.signupLink) {
            onClickSignup();
        }
    }


    public void startDashboardActivity() {
        Intent intent = new Intent (this,DashboardActivity.class);
        startActivity(intent);
    }

    private void onUserLogged() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }

    protected void onClickSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    protected void onClickLogin(String username, String password) {
        BaasUser user = BaasUser.withUserName(username)
                .setPassword(password);
        user.login(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> result) {
                if (result.isSuccess()) {
                    Log.d("LOG", "The user is currently logged in: " + result.value());
                    Toast okLoginToast = Toast.makeText(getApplicationContext(), "The user is currently logged in", Toast.LENGTH_SHORT);
                    okLoginToast.show();
                    startDashboardActivity();
                } else {
                    Log.e("LOG", "Show error", result.error());
                    Toast koLoginToast = Toast.makeText(getApplicationContext(), result.error().getMessage(), Toast.LENGTH_SHORT);
                    koLoginToast.show();
                }
            }
        });
    }

}
