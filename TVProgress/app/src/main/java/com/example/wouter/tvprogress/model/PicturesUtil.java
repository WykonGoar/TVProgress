package com.example.wouter.tvprogress.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Wouter on 22-11-2015.
 */
public class PicturesUtil {

    private File filePath;

    public PicturesUtil() {
        if (hasSDCard()) { // SD card
            filePath = new File(getSDCardPath() + "/TVProgress/");
            filePath.mkdir();
        } else {
            filePath = Environment.getDataDirectory();
        }
    }

    public String saveBitmapLowerQuality(String name ,Bitmap fullBitmap) {
        String completeFileName = filePath.getAbsolutePath() + "/" + name + ".png";

        int nh = (int) (fullBitmap.getHeight() * (512.0 / fullBitmap.getWidth()));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, 512, nh, true);

        try {
            FileOutputStream out = new FileOutputStream(completeFileName);
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch(Exception e) {
            String message = e.getMessage();
        }

        return completeFileName;
    }

    public boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }
    public String getSDCardPath() {
        File path = Environment.getExternalStorageDirectory();
        return path.getAbsolutePath();
    }
}
