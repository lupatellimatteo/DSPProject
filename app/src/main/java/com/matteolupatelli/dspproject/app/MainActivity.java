package com.matteolupatelli.dspproject.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.baasbox.android.BaasBox;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.*;
import com.baasbox.android.BaasUser.*;




public class MainActivity extends ActionBarActivity {

    private BaasBox client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("Lancia subActivity");
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startSubActivity();
            }
        });


        /*
        BaasBox.Builder b =
                new BaasBox.Builder(this);
        client = b.setApiDomain("dspproject.mlupatelli.baasbox.io")
                .setAppCode("dspProject1405705407")
                .init();


        BaasUser user = BaasUser.withUserName("andrea")
                                .setPassword("password");
        JsonObject extras = user.getScope(Scope.PRIVATE);
                          extras.putLong("age_info",27);
        user.signup(new BaasHandler<BaasUser>(){
            @Override
            public void handle(BaasResult<BaasUser> result){
                if(result.isSuccess()) {
                    Log.d("LOG","Current user is: "+result.value());
                } else {
                    Log.e("LOG","Show error",result.error());
                }
            }
        });

        */


        setContentView(button);
    }

     private void startSubActivity() {
         Intent intent = new Intent (this,SubActivity.class);
         startActivity(intent);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
