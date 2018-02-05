package com.example.avkor.test;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by avkor on 25.11.2017.
 */

public class Profile extends AppCompatActivity {

    private ImageView imageview;
    private Button btnSelectImage;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private EditText nM;
    private EditText fM;
    private Button btnSave;
    DBHelper dbHelper;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        user = new User(getIntent().getStringExtra("login"));

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select l._id from Login l where l.name = '" + user.getLogin() + "'", new String[] {});
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        user.setId(id);

        imageview = (ImageView) findViewById(R.id.imageview);
        btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        nM = (EditText) findViewById(R.id.nM);
        fM = (EditText) findViewById(R.id.fM);

        btnSave = (Button) findViewById(R.id.btnSave);

        readName();
        readSecondname();
        readAvatar();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });


            //OnbtnSelectImage click event...
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });}

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }

        @Override
         public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
            switch (item.getItemId()) {
            case R.id.exit:
                exit();
                return true;
            case R.id.about:
                about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        private void about() {
            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
            builder.setTitle("Информация о создателе")
                    .setMessage("Анатолий Коробицын")
                    .setCancelable(false)
                    .setNegativeButton("Ок",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        private void readAvatar() {

            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("select fi.avatar from Fi fi, Login l where fi._id = l._id and l.name = '" + user.getLogin() + "'", new String[] {});
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String avatar = cursor.getString(0);
                setImage(avatar);
                user.setImageurl(avatar);
            }
            cursor.close();

        }

    private void setImage(String avatar) {
        if (avatar!=null) {
            File imagefile = new File(avatar);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(avatar, options);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            destination = imagefile;
            imageview.setImageBitmap(bitmap);
        }

    }

    private void readSecondname() {

            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("select fi.fm from Fi fi, Login l where fi._id = l._id and l.name = '" + user.getLogin() + "'", new String[] {});
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String secondname = cursor.getString(0);
                fM.setText(secondname);
                user.setSecondname(secondname);
            }
            cursor.close();

        }


        private void readName() {
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("select fi.nm from Fi fi, Login l where fi._id = l._id and l.name = '" + user.getLogin() + "'", new String[] {});
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String name = cursor.getString(0);
                nM.setText(name);
                user.setName(name);
            }
            cursor.close();
        }

        private  void exit() {
        Intent intent = new Intent(Profile.this,LoginActivity.class);
        startActivity(intent);
    }

        private void save() {
        nM.setError(null);
        fM.setError(null);

        String name = nM.getText().toString();
        String secondname = fM.getText().toString();


        boolean cancel = false;
        View focusView = null;

            if (TextUtils.isEmpty(name)) {
                nM.setError("Введите Имя");
                focusView = nM;
                cancel = true;
            }

            if (TextUtils.isEmpty(secondname)) {
                fM.setError("Введите Фамилию");
                focusView = fM;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                dbHelper.insertOnTableFI(name, secondname, user.getImageurl(), user.getId());
                user.setName(name);
                user.setSecondname(secondname);
            }
        }



    private void selectImage() {
        try {
            Permission.checkPermission(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Profile.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Permission.REQUEST_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String parentDirectory = Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name);
                File parentFile = new File(parentDirectory);
                if (!parentFile.exists()) {
                    parentFile.mkdir();
                }
                destination = new File(parentDirectory, "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgPath = destination.getAbsolutePath();
                imageview.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    Log.e("Activity", "Pick from Gallery::>>> ");

                    imgPath = getRealPathFromURI(selectedImage);
                    destination = new File(imgPath.toString());
                    imageview.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        dbHelper.insertOnTableFI(user.getName(), user.getSecondname(), imgPath, user.getId());
        user.setImageurl(imgPath);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
