package com.example.jean.replicator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jean.replicator.utils.CustomAdapter;
import com.example.jean.replicator.utils.Preferences;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES;

public class MainActivity extends AppCompatActivity {

    private final static int CAMERA_REQUEST_PERMISSION = 1;
    private TextView txt_no_account;
    private CustomAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // We execute the cameraRequest function to see if we can launch the intent
                if (!requestCamera())
                    return;
                // we configure the qr code intent
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setPrompt("Scannez un QRCode OTP");
                integrator.setDesiredBarcodeFormats(QR_CODE_TYPES);
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });

        txt_no_account = (TextView) findViewById(R.id.txt_no_account);
        ListView lv = (ListView) findViewById(R.id.listview);

        // Display the 'hello' message if there is no account. Hide it if we do have one
        if (Preferences.get(this).getItems().size() > 0)
            txt_no_account.setVisibility(View.INVISIBLE);

        // setting up the listview
        adapter = new CustomAdapter(this, R.layout.list_row, Preferences.get(this).getItems(), txt_no_account);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            /*
            ** If result code is OK, it means a QR CODE has been read
            ** So we have to parse the content and to add an Account into our list
            */
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                System.out.println("contents = " + contents);
                try {
                    contents = URLDecoder.decode(contents, "UTF-8");
                    String secret = "";
                    String issuer = "";
                    List<String> items = Arrays.asList(Arrays.asList(contents.split("\\s*/\\s*")).get(3).split("\\s*\\?\\s*"));
                    List<String> tinfos = Arrays.asList(items.get(1).split("\\s*\\&\\s*"));
                    List<String> ainfos = Arrays.asList(items.get(0).split("\\s*:\\s*"));
                    for (String in : tinfos)
                    {
                        List<String> kv = Arrays.asList(in.split("\\s*=\\s*"));
                        if (kv.get(0).equals("secret"))
                            secret = kv.get(1);
                        else if (kv.get(0).equals("issuer"))
                            issuer = kv.get(1);
                    }
                    String username = ainfos.get(0);
                    if (ainfos.size() == 2)
                        username = ainfos.get(1);
                    if (username.length() == 0 || secret.length() == 0 || issuer.length() == 0) {
                        Snackbar.make(findViewById(R.id.content_main), "QR Code invalide", Snackbar.LENGTH_LONG).show();
                    }
                    if (!Preferences.get(this).addItem(new ReplicatorItem(secret, issuer, username)))
                        Snackbar.make(findViewById(R.id.content_main), "La clé du compte a été mise à jour", Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(findViewById(R.id.content_main), "Compte ajouté", Snackbar.LENGTH_LONG).show();
                    if (Preferences.get(this).getItems().size() > 0)
                        txt_no_account.setVisibility(View.INVISIBLE);
                    adapter.clear();
                    adapter.addAll(Preferences.get(this).getItems());
                    adapter.notifyDataSetChanged();
                } catch (UnsupportedEncodingException | IndexOutOfBoundsException e) {
                    Snackbar.make(findViewById(R.id.content_main), "QR Code invalide", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    /*
    ** requestPermission for the camera, we need it to scan a QR Code
    */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean requestCamera() {
        if (getBaseContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_PERMISSION);
            if (getBaseContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    /*
    ** onRequestPermissionResult function is here to check the camera permission request result and request it again if we don't have the rights to use it
    */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_PERMISSION: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // User don't accept the camera permission request, we don do anything but we inform him why we need it
                    Snackbar.make(findViewById(R.id.content_main), "Pour scanner un QR Code, nous avons besoin d'accéder à votre caméra", Snackbar.LENGTH_LONG).show();
                } else {
                    // User accept the camera permission request, we perform the click to launch the QR Code Reader Activity
                    findViewById(R.id.fab).performClick();
                }
            }
        }
    }

}