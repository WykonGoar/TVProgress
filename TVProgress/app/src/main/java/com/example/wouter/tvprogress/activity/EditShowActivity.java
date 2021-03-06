package com.example.wouter.tvprogress.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wouter.tvprogress.R;
import com.example.wouter.tvprogress.model.API.CallAPIAllShows;
import com.example.wouter.tvprogress.model.API.ShowResource;
import com.example.wouter.tvprogress.model.API.WiFiConnection;
import com.example.wouter.tvprogress.model.DatabaseConnection;
import com.example.wouter.tvprogress.model.PicturesUtil;
import com.example.wouter.tvprogress.model.Show;
import com.example.wouter.tvprogress.model.API.iOnTaskCompleted;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class EditShowActivity extends AppCompatActivity implements iOnTaskCompleted{

    private Show mShow;
    private EditText etTitle;
    private ImageView ivImage;
    private ImageView ivBanner;
    private TextView tvResource;

    private String selectedImage;
    private String selectedBanner;
    private String selectedResource;

    private static int PickImageCode = 1;
    private static int PickBannerCode = 2;
    private static int PickResourceCode = 3;

    private boolean imageChanged = false;
    private boolean bannerChanged = false;

    private DatabaseConnection mDatabaseConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_show);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImage();
            }
        });

        SQLiteDatabase mDatabase = openOrCreateDatabase("TVProgressDB", MODE_PRIVATE, null);
        mDatabaseConnection = new DatabaseConnection(mDatabase, this);

        mShow = new Show();

        Intent mIntent = getIntent();
        int showId = mIntent.getIntExtra("id", -1);
        if (showId != -1)
            mShow = getShow(showId);

        selectedImage = mShow.getImage();
        selectedBanner = mShow.getBanner();
        selectedResource = mShow.getURL();

        etTitle = (EditText) findViewById(R.id.etTitle);
        etTitle.setText(mShow.getTitle());

        Button bEditImage = (Button) findViewById(R.id.bEditImage);
        bEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImage();
            }
        });

        ivBanner = (ImageView) findViewById(R.id.ivBanner);
        ivBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBanner();
            }
        });

        Button bEditBanner = (Button) findViewById(R.id.bEditBanner);
        bEditBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBanner();
            }
        });

        Button bSave = (Button) findViewById(R.id.bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShow();
            }
        });

        Button bSelectResource = (Button) findViewById(R.id.bSelectResource);
        bSelectResource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectResource();
            }
        });

        tvResource = (TextView) findViewById(R.id.tvResource);
        tvResource.setText(mShow.getURL());

        Button bRemoveResource = (Button) findViewById(R.id.bRemoveResource);
        bRemoveResource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedResource = "";
                tvResource.setText("");
            }
        });

        reloadImages();
    }

    private void reloadImages()
    {
        try{
        if(!selectedImage.isEmpty()){
            Bitmap fullImage = BitmapFactory.decodeFile(selectedImage);
            int nhImage = (int) (fullImage.getHeight() * (512.0 / fullImage.getWidth()));
            Bitmap scaledImage = Bitmap.createScaledBitmap(fullImage, 512, nhImage, true);
            ivImage.setImageBitmap(scaledImage);
        }

        if(!selectedBanner.isEmpty()){
            Bitmap fullBanner = BitmapFactory.decodeFile(selectedBanner);
            int nhBanner = (int) (fullBanner.getHeight() * (512.0 / fullBanner.getWidth()));
            Bitmap scaledBanner = Bitmap.createScaledBitmap(fullBanner, 512, nhBanner, true);
            ivBanner.setImageBitmap(scaledBanner);
        }}
        catch (Exception e){}
    }

    private Show getShow(int showId){
        LinkedList<Show> shows = mDatabaseConnection.getShows("SELECT * FROM shows WHERE _id = " + showId);
        Show selectedShow = null;

        if (shows.size() == 0)
            throw new IndexOutOfBoundsException("Show with id '" + showId + "' not found.");
        else
            selectedShow = shows.get(0);

        return selectedShow;
    }

    private void editImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, "Select picture"), PickImageCode);
    }

    private void editBanner(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, "Select picture"), PickBannerCode);
    }

    private void saveShow(){
        Intent mIntent = new Intent(getApplicationContext(), ShowActivity.class);

        mShow.setTitle(etTitle.getText().toString());

        //Save pictures
        PicturesUtil picturesUtil = new PicturesUtil();
        if(!selectedImage.isEmpty() && imageChanged){
            if(!mShow.getImage().isEmpty())
                picturesUtil.removePicture(mShow.getImage());

            String nameImage = getNameFromPath(selectedImage);
            Bitmap bitmapImage = BitmapFactory.decodeFile(selectedImage);
            String newImagePath = picturesUtil.saveBitmapLowerQuality(nameImage, bitmapImage);

            mShow.setImage(newImagePath);
        }

        if(!selectedBanner.isEmpty() && bannerChanged){
            if(!mShow.getBanner().isEmpty())
                picturesUtil.removePicture(mShow.getBanner());

            String nameBanner = getNameFromPath(selectedBanner);
            Bitmap bitmapBanner = BitmapFactory.decodeFile(selectedBanner);
            String newBannerPath = picturesUtil.saveBitmapLowerQuality(nameBanner, bitmapBanner);

            mShow.setBanner(newBannerPath);
        }

        if(mShow.getTitle().isEmpty())
        {
            Toast.makeText(getBaseContext(), "Title must be filled in.", Toast.LENGTH_LONG).show();
            return;
        }

        if(!selectedResource.isEmpty() && (selectedResource != mShow.getURL())) {
            mIntent.putExtra("newResource", true);
        }
        else if(selectedResource.isEmpty() && mShow.getId() != -1){
            String query = "DELETE FROM episodes WHERE showId = ?";
            SQLiteStatement statement = mDatabaseConnection.getNewStatement(query);
            statement.bindLong(1, mShow.getId());
            mDatabaseConnection.executeNonReturn(statement);
        }

        mShow.setURL(tvResource.getText().toString());

        SQLiteStatement statement = null;
       if(mShow.getId() != -1) {

           String query = "UPDATE shows SET title = ?, image = ?, banner = ?, url = ? WHERE _id = ?;";
            statement = mDatabaseConnection.getNewStatement(query);

           statement.bindLong(5, mShow.getId());
       }
        else{
           String query = "INSERT INTO shows (title, image ,banner, url) VALUES (?, ?, ?, ?)";
           statement = mDatabaseConnection.getNewStatement(query);
       }

        statement.bindString(1, mShow.getTitle());
        statement.bindString(2, mShow.getImage());
        statement.bindString(3, mShow.getBanner());
        statement.bindString(4, mShow.getURL());

        if(mShow.getId() != -1) {
            mDatabaseConnection.executeNonReturn(statement);
        }
        else{
            int newId = mDatabaseConnection.executeInsertQuery(statement);
            if(newId == -1) {
                Toast.makeText(this, "Can't add this show.\nTry changing the name.", Toast.LENGTH_LONG).show();
                return;
            }
            mShow.setId(newId);
        }

        mIntent.putExtra("id", mShow.getId());
        startActivity(mIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if(requestCode == PickImageCode || requestCode == PickBannerCode) {
                Uri uri = data.getData();
                String realPath = getRealPathFromURI(uri);

                if (requestCode == PickImageCode) {
                    selectedImage = realPath;
                    imageChanged = true;
                }

                if (requestCode == PickBannerCode) {
                    selectedBanner = realPath;
                    bannerChanged = true;
                }

                reloadImages();
            }

            if(requestCode == PickResourceCode){
                String resource = data.getStringExtra("resource");
                selectedResource = resource;
                tvResource.setText(resource);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Remove " + mShow.getTitle() + " ?");
            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String query = "DELETE FROM shows WHERE _id = ?";
                    SQLiteStatement statement = mDatabaseConnection.getNewStatement(query);
                    statement.bindLong(1, mShow.getId());
                    mDatabaseConnection.executeNonReturn(statement);

                    Toast.makeText(getApplicationContext(), mShow.getTitle() + " removed", Toast.LENGTH_LONG).show();

                    if (!mShow.getImage().isEmpty()) {
                        File imageFile = new File(mShow.getImage());
                        imageFile.delete();
                    }

                    if (!mShow.getBanner().isEmpty()) {
                        File bannerFile = new File(mShow.getBanner());
                        bannerFile.delete();
                    }

                    Intent mIntent = new Intent(getApplicationContext(), ShowListActivity.class);

                    startActivity(mIntent);

                    finish();
                }
        });
            builder.setNegativeButton("Cancel",null);
            // Create the AlertDialog object and return it
            Dialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        CursorLoader cursorLoader = new CursorLoader( this, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private String getNameFromPath(String path){
        int indexExtention = path.lastIndexOf('.');
        int indexName = path.lastIndexOf('/') +1;

        return path.substring(indexName, indexExtention);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void selectResource(){
        WiFiConnection wiFiConnection = new WiFiConnection(this);

        if(wiFiConnection.isConnectedToWiFi()) {
            startCallAPIAllShows();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Not connected to WiFi!\nDo you want to use mobile internet?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startCallAPIAllShows();
                }
            });
            builder.setNegativeButton("Cancel",null);
            // Create the AlertDialog object and return it
            builder.create().show();
        }
    }

    private void startCallAPIAllShows(){
        new CallAPIAllShows(this, this).execute();
    }

    @Override
    public void onTaskCompleted(Object values) {
        if (values != null){
            if(values.getClass() == ArrayList.class){
                ArrayList<ShowResource> resourceList = (ArrayList<ShowResource>) values;
                Intent intent = new Intent(this, SelectShowResourceActivity.class);
                intent.putParcelableArrayListExtra("resources", resourceList);
                startActivityForResult(intent, PickResourceCode);
            }
        }
    }
}
