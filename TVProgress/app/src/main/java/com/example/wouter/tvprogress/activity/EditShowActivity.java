package com.example.wouter.tvprogress.activity;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import com.example.wouter.tvprogress.R;
import com.example.wouter.tvprogress.model.DatabaseConnection;
import com.example.wouter.tvprogress.model.Show;

import java.util.LinkedList;

public class EditShowActivity extends AppCompatActivity {

    private Show mShow;
    private EditText etTitle;
    private ImageView ivImage;
    private ImageView ivBanner;
    private static int PickImageCode = 1;
    private static int PickBannerCode = 2;

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

        reloadImages();
    }

    private void reloadImages()
    {
        ivImage.setImageBitmap(mShow.getImageAsImage());
        ivBanner.setImageBitmap(mShow.getBannerAsImage());
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
        mShow.setTitle(etTitle.getText().toString());

        if(mShow.getTitle() == "")
        {
            Toast.makeText(getBaseContext(), "Title must be filled in.", Toast.LENGTH_LONG);
            return;
        }

       if(mShow.getId() != -1)
            mDatabaseConnection.executeNonReturn("UPDATE shows SET title = '" + mShow.getTitle() + "', image = '" + mShow.getImage() + "', banner = '" + mShow.getBanner() + "' WHERE _id = " + mShow.getId());
        else
           mDatabaseConnection.executeNonReturn("INSERT INTO shows (title, image ,banner) VALUES ('" + mShow.getTitle() + "', '" + mShow.getImage() + "', '" + mShow.getBanner() + "')");

        if(mShow.getId() != -1) {
            Intent mIntent = new Intent(getApplicationContext(), ShowListActivity.class);
            startActivity(mIntent);
        }
        else{
            Intent mIntent = new Intent(getApplicationContext(), ShowActivity.class);
            mIntent.putExtra("id", mShow.getId());

            startActivity(mIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String realPath = getRealPathFromURI(uri);

            if (realPath ==""){
                Toast.makeText(getBaseContext(), "Selecting picture failed", Toast.LENGTH_LONG);
                return;
            }

            if (requestCode == PickImageCode)
                mShow.setImage(realPath);

            if (requestCode == PickBannerCode)
                mShow.setBanner(realPath);

            reloadImages();
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


        return super.onOptionsItemSelected(item);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(
                this,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
