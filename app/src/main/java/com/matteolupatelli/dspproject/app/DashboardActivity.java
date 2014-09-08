package com.matteolupatelli.dspproject.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baasbox.android.BaasClientException;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasServerException;
import com.baasbox.android.BaasUser;

import java.util.List;

/**
 * Created by matteo on 04/09/14.
 */
public class DashboardActivity extends ListActivity implements View.OnClickListener {

    private AddTask addTask;
    private ListTask listTask;
    private MenuItem refreshMenuItem;
    private ArrayAdapter<BaasDocument> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (!( BaasUser.current().isAuthentcated())) {
            Log.d("LOG", "USER NOT LOGGED");
            startLoginActivity();
            return;
        }

    }

    public void onClick(View v){
        logoutUser();
    }

    public void logoutUser(){
        BaasUser.current().logout(new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> result) {
                if(result.isSuccess()) {
                    Log.d("LOG", "Logged out: " + (BaasUser.current() == null));
                    Toast logoutOkToast = Toast.makeText(getApplicationContext(), "user logout", Toast.LENGTH_SHORT);
                    logoutOkToast.show();
                } else{
                    Log.e("LOG","Show error",result.error());
                    Toast logoutKoToast = Toast.makeText(getApplicationContext(), result.error().getMessage(),Toast.LENGTH_LONG);
                    logoutKoToast.show();
                }
            };
        });
        startLoginActivity();
        return;
    }

    public void startLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                //Toast test = Toast.makeText(getApplicationContext(),"REFRESH",Toast.LENGTH_LONG);
                //test.show();
                refresh();
                return true;
            case R.id.action_add:
                //test = Toast.makeText(getApplicationContext(),"ADD",Toast.LENGTH_SHORT);
                //test.show();
                onClickAddRecipe();
                return true;
            case R.id.action_logout:
                logoutUser();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickAddRecipe(){
        View layout = getLayoutInflater().inflate(R.layout.dialog_add, null);

        final EditText nameText = (EditText) layout.findViewById(R.id.nameRecipe);
        final EditText ingredientsText = (EditText) layout.findViewById(R.id.ingredientsRecipe);
        final EditText directionsText = (EditText) layout.findViewById(R.id.directionsRecipe);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameText.getText().toString().trim();
                String ingredients = ingredientsText.getText().toString().trim();
                String directions = directionsText.getText().toString().trim();

                if (name.length() > 0 && ingredients.length() > 0 && directions.length() > 0)
                    addRecipe(name, ingredients,directions);
            }
        });
        builder.create().show();
    }

    public void addRecipe(String name, String ingredients, String directions) {
        addTask = new AddTask();
        addTask.execute(name, ingredients,directions);
    }

    public void refresh(){
        listTask = new ListTask();
        listTask.execute();
    }

    public class AddTask extends AsyncTask<String, Void, BaasResult<BaasDocument>> {

        @Override
        protected BaasResult<BaasDocument> doInBackground(String... params) {
            BaasDocument recipe = new BaasDocument("recipes");

            recipe.putString("name", params[0]);
            recipe.putString("ingredients", params[1]);
            recipe.putString("directions", params[2]);



            return recipe.saveSync();
        }
    }

    public class ListTask extends AsyncTask<Void, Void, BaasResult<List<BaasDocument>>> {

        @Override
        protected void onPreExecute() {
            if (refreshMenuItem != null)
                refreshMenuItem.setActionView(R.layout.view_menuitem_refresh);
        }

        @Override
        protected BaasResult<List<BaasDocument>> doInBackground(Void... params) {
            return BaasDocument.fetchAllSync("address-book");
        }

        @Override
        protected void onPostExecute(BaasResult<List<BaasDocument>> result) {
            if (refreshMenuItem != null)
                refreshMenuItem.setActionView(null);
            onListReceived(result);
        }
    }

    protected void onListReceived(BaasResult<List<BaasDocument>> result) {
        try {
            List<BaasDocument> array = result.get();
            adapter.clear();

            for (int i = 0; i < array.size(); i++)
                adapter.add(array.get(i));

            adapter.notifyDataSetChanged();
        } catch (BaasClientException e) {
            //AlertUtils.showErrorAlert(this, e);
        } catch (BaasServerException e) {
            //AlertUtils.showErrorAlert(this, e);
        } catch (BaasException e) {
            //AlertUtils.showErrorAlert(this, e);
        }
    }

}


