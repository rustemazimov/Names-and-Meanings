package com.apps.romeo.adlarinmenasi;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private HttpHandler sh;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final TextView meaningTextView = (TextView) findViewById(R.id.meaning_text_view);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meaningTextView.setText("");
                if (sh == null)
                {
                    sh = new HttpHandler();
                }

                if (progressDialog == null)
                {
                    initializeProgressDiaglog();
                }
                final boolean[] isCanceled = {false};
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        isCanceled[0] = true;
                    }
                });

                progressDialog.show();

                if (isCanceled[0])
                {
                    return;
                }
                String name = ((EditText) findViewById(R.id.name_edit_text)).getText().toString().trim();
                if (name.isEmpty())
                {
                    meaningTextView.setText("Ad daxil edilməyib");
                    return;
                }
                String jsonStr = sh.makeServiceCall("https://opendata.e-gov.az/api/v1/json/home/MeaningOfName/"
                        + Uri.encode(name)
                        + "?pretty");
                progressDialog.setMessage("Preparing result");
                if (jsonStr != null)
                {
                    String meaning;
                    try {
                        meaning = new JSONObject(jsonStr).getJSONObject("Response").getString("Meaning");
                    } catch (JSONException e) {
                        progressDialog.hide();
                        meaning = "Belə ad təyin olunmayıb";
                        if (name.length() > 1 && name.charAt(name.length() - 1) == 'ə')
                        {
                            meaning += "\nKişi adlarına 'ə' əlavə olunmaqla düzələn qadın adları sistemdə təyin olunmayıb" +
                                    "\nBele qadın adlarının mənasını öyrənmək üçün adın kişi cinsi versiyasını sınayın: \n" +
                                    name.substring(0, name.length() - 1git);
                        }
                    }
                    meaningTextView.setText(meaning);

                }
                else
                {
                    progressDialog.hide();
                    showMessage("Internet əlaqəsini yoxlayın");
                }
                progressDialog.hide();
            }
        });
    }

    private void initializeProgressDiaglog() {
        this.progressDialog = new ProgressDialog(MainActivity.this);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setMessage("Downloading data");
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(true);
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}


