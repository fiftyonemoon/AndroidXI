package com.fom.storage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;

import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fom.storage.databinding.ActivityMainBinding;
import com.fom.storage.media.AndroidXI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private Uri uri;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btSave.setOnClickListener(this);
        binding.btDelete.setOnClickListener(this);
        binding.btRename.setOnClickListener(this);
        binding.btDuplicate.setOnClickListener(this);
        binding.btShow.setOnClickListener(this);
    }

    private final ActivityResultLauncher<IntentSenderRequest> launcher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == binding.btSave.getId()) {
            save();
        } else if (id == binding.btDelete.getId()) {
            delete();
        } else if (id == binding.btRename.getId()) {
            rename();
        } else if (id == binding.btDuplicate.getId()) {
            duplicate();
        } else if (id == binding.btShow.getId()) {
            show();
        }
    }

    private void save() {

        String path = Environment.DIRECTORY_DCIM + File.separator + getString(R.string.app_name);

        uri = AndroidXI.getInstance()
                .with(this)
                .create(path, "ex.jpg", "image/*");

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ((ImageView) findViewById(R.id.ivIcon)).getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void delete() {
        if (uri == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            AndroidXI.getInstance().with(this).delete(launcher, uri);
        }
    }

    private void rename() {
        if (uri == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            AndroidXI.getInstance().with(this).rename(uri, "rename");
        }
    }

    private void duplicate() {
        if (uri == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            uri = AndroidXI.getInstance().with(this).duplicate(uri);
        }
    }

    private void show() {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        String text = "null";

        if (cursor.moveToNext()) {
            text = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        }else uri = null;

        cursor.close();

        binding.text.setText(text);
    }
}