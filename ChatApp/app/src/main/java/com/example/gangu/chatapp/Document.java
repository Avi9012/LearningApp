package com.example.gangu.chatapp;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class Document extends AppCompatActivity {

    NfcAdapter nfcAdapter;

    boolean androidBeamAvailable  = false;

    private Uri[] fileUris = new Uri[10];

    private FileUriCallback fileUriCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {

        } else if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.JELLY_BEAN_MR1) {

            androidBeamAvailable = false;

        } else {
            androidBeamAvailable = true;
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        fileUriCallback = new Document.FileUriCallback();

        nfcAdapter.setBeamPushUrisCallback(fileUriCallback,this);

        String transferFile = "transferimage.jpg";
        File extDir = getExternalFilesDir(null);
        File requestFile = new File(extDir, transferFile);
        requestFile.setReadable(true, false);
        // Get a URI for the File and add it to the list of URIs
        Uri fileUri = Uri.fromFile(requestFile);
        if (fileUri != null) {
            fileUris[0] = fileUri;
        } else {
            Log.e("My Activity", "No File URI available for file.");
        }
    }

    private class FileUriCallback implements
            NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {
        }

        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return fileUris;
        }
    }

}
