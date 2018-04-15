package com.example.cootzy.contentprovider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.cootzy.contentprovider.MyProvider.CONTENT_URI;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createFile();
        openFile();
    }

    private void openFile() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse(CONTENT_URI + "test.txt"), "text/plain");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(i);
    }

    private void createFile() {
        File newFile = new File(getFilesDir(), "test.txt");

        if (!newFile.exists()) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(newFile);
                fos.write("Launching internal storage file in third party app via content provider :)".getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
