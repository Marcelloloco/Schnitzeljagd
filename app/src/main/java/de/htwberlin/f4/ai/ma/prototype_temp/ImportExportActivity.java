package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.carol.bvg.R;

import java.io.IOException;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;

/**
 * Created by Johann Winter
 */

public class ImportExportActivity extends BaseActivity {

    Button importButton;
    Button exportButton;
    private DatabaseHandler databaseHandler;
    private Context context;

    private static final int PICKFILE_REQUEST_CODE = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_import_export, contentFrameLayout);

        context = getApplicationContext();

        importButton = (Button) findViewById(R.id.import_button);
        exportButton = (Button) findViewById(R.id.export_button);

        databaseHandler = DatabaseHandlerFactory.getInstance(this);

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(Intent.createChooser(intent, "Bitte .db Datei wählen"), PICKFILE_REQUEST_CODE);
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean exportSuccessful = databaseHandler.exportDatabase();
                if (exportSuccessful) {
                    Toast.makeText(context, "Datenbank exportiert!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            final Uri selectedFile = data.getData();

            // Check if selected file has extension .db
            if (!selectedFile.getPath().endsWith(".db")) {
                new AlertDialog.Builder(this)
                        .setTitle("Falsche Dateiendung")
                        .setMessage(getString(R.string.import_database_wrong_file_extension_error))
                        .setCancelable(true)
                        .setPositiveButton("Erneut versuchen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("file/*");
                                startActivityForResult(Intent.createChooser(intent, "Bitte .db Datei wählen"), PICKFILE_REQUEST_CODE);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Importieren?")
                        .setMessage(getString(R.string.import_database_warining))
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    boolean importSuccessful = DatabaseHandlerFactory.getInstance(context).importDatabase(selectedFile.getPath());
                                    if (importSuccessful) {
                                        Toast.makeText(context, "Datenbank importiert.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (IOException e) {e.printStackTrace();}
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }
}
