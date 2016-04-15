package com.fhb.powergoldv1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by FHB:Taufiq on 4/14/2016.
 */
public class FileUtils extends DatabaseController {
    private String dbFilePath;
    private String dstFilePath;
    private FileInputStream in;
    private FileOutputStream out;

    public FileUtils(Context context) {
        super(context);
    }

    public String checkExternalStatus () {
        String statExt = "Source or Target is not accessible!"; //
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            statExt = "" ; // OK state
            //mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            statExt = "External is readonly, not writable!!" ; // OK state
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            statExt = "External storage error!!" ; // OK state
        }
        return statExt;
    }

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     */
    public boolean importDatabase(String dbPath) throws IOException {
        //db.close();
        this.getWritableDatabase().close();
        this.setDbFilePath();

        File bckDbFilePath = new File(dbPath);
        File existingDBFilePath = new File(getDbFilePath());
        if (bckDbFilePath.exists()) {
            FileUtils.copyFile(new FileInputStream(bckDbFilePath), new FileOutputStream(existingDBFilePath));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            this.getWritableDatabase().close();
            return true;
        }
        return false;
    }


    /**
     * Creates the specified toFile as a byte for byte copy of the
     * fromFile. If to File already exists, then it
     * will be replaced with a copy of fromFile. The name and path
     * of toFile will be that of toFile.
     * <p/>
     * Note: from File and to File will be closed by
     * this function.
     *
     * @param fromFile - FileInputStream for the file to copy from.
     * @param toFile   - FileInputStream for the file to copy to.
     */
    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (toChannel != null) {
                    try {
                        toChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setExternalFilePath() {
        // to set the destination file path on the SD card. Not really on sdcard.
        // Its in /storage/emulator/0/ which I is actually a symlink..
        String dstDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File newFile=new File(dstDirPath, "PowerGold");

        // TODO check the existence of the directory before creation for next backup operation
        // after the first creation or it may return error.
        if (!newFile.exists()) {
            Boolean isCreate = newFile.mkdirs();
        }
        dstFilePath = newFile.toString() + File.separator + "PG.db";
    }

    public String getExternalFilePath() {
        // to set the destination file path on the SD card.
        return dstFilePath;
    }

    public void setDbFilePath() {
        // to set the current db file path.
        // DB_FILEPATH = "/data/data/com.fhb.powergoldv1/databases/PG.db";
        SQLiteDatabase db = this.getWritableDatabase();
        dbFilePath = db.getPath();
        db.close();
    }

    public String getDbFilePath() {
        // to set the current db file path.
        return dbFilePath;
    }

    public Boolean setFileInputStream(String inPath){
        in = null;
        Boolean isCreated;
        try {
            in = new FileInputStream(inPath);
            isCreated =  true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isCreated =  null;
        }
        return isCreated;
    }

    public FileInputStream getFileInputStream() {
        return in;
    }

    public void closeFileInputStream() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Boolean setFileOutputStream(String outPath){
        // eXTERNAL - FileOutputStream fos = new FileOutputStream(new File(getExternalFilesDir(null), "tours"));
        // INTERNAL - FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "tours"));
        out = null;
        Boolean isCreated;
        try {
            out = new FileOutputStream(outPath);
            isCreated =  true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isCreated =  null;
        }
        return isCreated;
    }

    public FileOutputStream getFileOutputStream() {
        return out;
    }

    public void closeFileOutputStream() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createExternalDir(String dirPath) {
        //File path = Environment.getExternalStorageDirectory();
        //String save_path = dirPath + File.separator;
        File newFile=new File(dirPath, "PowerGold");
        Boolean isCreate = newFile.mkdirs();
        dstFilePath = newFile.toString();

        return isCreate;
    }
}