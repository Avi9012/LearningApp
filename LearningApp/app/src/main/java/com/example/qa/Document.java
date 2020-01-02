package com.example.qa;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Document extends AppCompatActivity {

    NfcAdapter nfcAdapter;

    boolean androidBeamAvailable;

    private List<Uri> fileUris;

    private FileUriCallback fileUriCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitydocument);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {

            Toast.makeText(this, "Your device does not support NFC, sorry can't share document...", Toast.LENGTH_SHORT).show();
            finish();

        } else if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.JELLY_BEAN_MR1) {

            androidBeamAvailable = false;

        } else {
            androidBeamAvailable = true;
        }

        if(androidBeamAvailable) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);

            fileUriCallback = new FileUriCallback();

            nfcAdapter.setBeamPushUrisCallback(fileUriCallback, this);

            fileUris = new ArrayList<>();

            String transferFile = "transferimage.jpg";
            File extDir = getExternalFilesDir(null);
            File requestFile = new File(extDir, transferFile);
            requestFile.setReadable(true, false);
            // Get a URI for the File and add it to the list of URIs
            Uri fileUri = Uri.fromFile(requestFile);
            if (fileUri != null) {
                fileUris.add(fileUri);
            } else {
                Log.e("My Activity", "No File URI available for file.");
            }
        }
    }

    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {

        public FileUriCallback() {
            
        }

        @Override
        public Uri[] createBeamUris(NfcEvent event) {

            Uri [] uri = new Uri[fileUris.size()];
            for(int i = 0; i < fileUris.size(); i++)
            {
                uri[i] = fileUris.get(i);
            }
            return uri;
        }
    }

}
