package com.matteolupatelli.dspproject.app;

import java.util.ArrayList;
import java.util.List;

import com.baasbox.android.BaasClientException;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasServerException;
import com.baasbox.android.BaasUser;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends ListActivity implements
        ActionMode.Callback {

    private static final int MENUITEM_REFRESH = 1;
    private static final int MENUITEM_ADD = 2;
    private static final int MENUITEM_DELETE = 3;
    private static final int MENUITEM_LOGOUT = 4;


    private ListTask listTask;
    private AddTask addTask;
    private ArrayAdapter<BaasDocument> adapter;
    private MenuItem refreshMenuItem;
    private MenuItem logoutMenuItem;

    private int selectedItem = -1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        this.adapter = new Adapter(this);
        this.setListAdapter(adapter);

        final ListView lv = this.getListView();

        this.getListView().setLongClickable(true);
        this.getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        this.getListView().setOnItemClickListener(
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View view, int position, long id) {

                        BaasDocument selectedDocument =(BaasDocument) (lv.getItemAtPosition(position));

                        selectedItem=position;

                        onRecipeClicked(selectedDocument);




            }
        });



        this.getListView().setOnItemLongClickListener(
                new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View view, int position, long id) {
                        if (selectedItem != -1) {
                            return false;
                        }

                        selectedItem = position;
                        startActionMode(DashboardActivity.this);
                        view.setSelected(true);
                        return true;
                    }
                });
    }


     public void onRecipeClicked(BaasDocument selectedDocument) {
         View layout = getLayoutInflater().inflate(R.layout.dialog_view, null);

         TextView viewnameRecipe = (TextView) layout.findViewById(R.id.viewNameRecipe);
         TextView viewingredientsRecipe = (TextView) layout.findViewById(R.id.viewIngredientsRecipe);
         TextView viewdirectionsRecipe = (TextView) layout.findViewById(R.id.viewDirectionsRecipe);

         String nameRecipe=selectedDocument.getString("name");

         String ingredientsRecipe=selectedDocument.getString("ingredients");

         String directionsRecipe=selectedDocument.getString("directions");

         viewnameRecipe.setText(nameRecipe);
         viewingredientsRecipe.setText(ingredientsRecipe);
         viewdirectionsRecipe.setText(directionsRecipe);



         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setView(layout);
         builder.setNegativeButton("Cancel", null);



         builder.create().show();

         Log.d("LOG", "SELECTED NAME RECIPE " + nameRecipe);
         Log.d("LOG", "SELECTED INGREDIENTS RECIPE " + ingredientsRecipe);
         Log.d("LOG", "SELECTED DIRECTIONS RECIPE " + directionsRecipe);

     }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        refreshMenuItem = menu.add(Menu.NONE, MENUITEM_REFRESH, Menu.NONE,
                "Refresh");
        refreshMenuItem.setIcon(R.drawable.ic_menu_refresh);
        refreshMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (listTask != null && listTask.getStatus() == Status.RUNNING)
            refreshMenuItem.setActionView(R.layout.view_menuitem_refresh);

        MenuItem add = menu.add(Menu.NONE, MENUITEM_ADD, Menu.NONE, "Add");
        add.setIcon(R.drawable.ic_menu_add);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        logoutMenuItem = menu.add(Menu.NONE, MENUITEM_LOGOUT, Menu.NONE,
                "Logout");
        logoutMenuItem.setIcon(R.drawable.ic_menu_logout);
        logoutMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case MENUITEM_ADD:
                onClickAddRecipe();
                break;
            case MENUITEM_REFRESH:
                refresh();
                break;
            case MENUITEM_LOGOUT:
                logoutUser();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
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



    private void onUserLogged() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }


    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case MENUITEM_DELETE:
                delete(selectedItem);
                mode.finish();
                break;
            default:
                return super.onContextItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        BaasDocument recipe = adapter.getItem(selectedItem);
        mode.setTitle(recipe.getString("name"));

        MenuItem delete = menu.add(ContextMenu.NONE, MENUITEM_DELETE,
                ContextMenu.NONE, "Delete");
        delete.setIcon(R.drawable.ic_menu_delete);
        delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectedItem = -1;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    protected void delete(int position) {
        BaasDocument recipe = adapter.getItem(position);
        adapter.remove(recipe);
        new DeleteTask().execute(recipe);
    }

    private void refresh() {
        listTask = new ListTask();
        listTask.execute();
    }

    private void onClickAddRecipe() {
        View layout = getLayoutInflater().inflate(R.layout.dialog_add, null);
        final EditText namerecipeText = (EditText) layout.findViewById(R.id.nameRecipe);
        final EditText ingredientsText = (EditText) layout.findViewById(R.id.ingredientsRecipe);
        final EditText directionsText = (EditText) layout.findViewById(R.id.directionsRecipe);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = namerecipeText.getText().toString().trim();
                String ingredients = ingredientsText.getText().toString().trim();
                String directions = directionsText.getText().toString().trim();
                Log.d("LOG", "NAME RECIPE: " + name);
                Log.d("LOG", "INGREDIENTS RECIPE: " + ingredients);
                Log.d("LOG", "DIRECTIONS RECIPE: " + directions);


                if (name.length() > 0 && ingredients.length() > 0 && directions.length() >0)
                    addRecipe(name, ingredients, directions);
            }
        });

        builder.create().show();
    }

    protected void addRecipe(String name, String ingredients, String directions) {
        addTask = new AddTask();
        addTask.execute(name, ingredients,directions);
    }

    public void onRecipeAdded(BaasResult<BaasDocument> result) {
        try {
            adapter.add(result.get());
            adapter.notifyDataSetChanged();
        } catch (BaasClientException e) {
            Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        } catch (BaasServerException e) {
            Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        } catch (BaasException e) {
            Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
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
            Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        } catch (BaasServerException e) {
            Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        } catch (BaasException e) {
            Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        }
    }

    protected void onRecipeDeleted(BaasResult<Void> result) {
        try {
            result.get();
        } catch (BaasClientException e) {
            Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        } catch (BaasServerException e) {
            Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        } catch (BaasException e) {
            Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
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
            return BaasDocument.fetchAllSync("recipes");
        }

        @Override
        protected void onPostExecute(BaasResult<List<BaasDocument>> result) {
            if (refreshMenuItem != null)
                refreshMenuItem.setActionView(null);
            onListReceived(result);
        }
    }

    public class AddTask extends AsyncTask<String, Void, BaasResult<BaasDocument>> {

        @Override
        protected BaasResult<BaasDocument> doInBackground(String... params) {
            BaasDocument recipes = new BaasDocument("recipes");

            recipes.putString("name", params[0]);
            recipes.putString("ingredients", params[1]);
            recipes.putString("directions", params[2]);


            return recipes.saveSync();
        }

        @Override
        protected void onPostExecute(BaasResult<BaasDocument> result) {
            onRecipeAdded(result);
        }
    }

    public class DeleteTask extends	AsyncTask<BaasDocument, Void, BaasResult<Void>> {

        @Override
        protected BaasResult<Void> doInBackground(BaasDocument... params) {
            return params[0].deleteSync();
        }

        @Override
        protected void onPostExecute(BaasResult<Void> result) {
            onRecipeDeleted(result);
        }
    }

    public class Adapter extends ArrayAdapter<BaasDocument> {

        public Adapter(Context context) {
            super(context, android.R.layout.simple_list_item_2,	new ArrayList<BaasDocument>());
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2,
                        null);

                Tag tag = new Tag();
                tag.text1 = (TextView) view.findViewById(android.R.id.text1);
                tag.text2 = (TextView) view.findViewById(android.R.id.text2);
                view.setTag(tag);
            }

            Tag tag = (Tag) view.getTag();
            BaasDocument entry = getItem(position);
            tag.text1.setText(entry.getString("name"));

            return view;
        }

    }

    protected static class Tag {

        public TextView text1;
        public TextView text2;
    }

}
