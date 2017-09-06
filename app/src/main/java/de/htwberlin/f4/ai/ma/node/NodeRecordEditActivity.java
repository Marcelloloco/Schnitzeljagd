package de.htwberlin.f4.ai.ma.node;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import de.htwberlin.f4.ai.ma.wifiscanner.WifiScanner;
import de.htwberlin.f4.ai.ma.wifiscanner.WifiScannerFactory;
import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.fingerprint.AsyncResponse;
import de.htwberlin.f4.ai.ma.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.fingerprint.FingerprintTask;
import de.htwberlin.f4.ai.ma.nodelist.NodeListActivity;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.persistence.FileUtilities;
import de.htwberlin.f4.ai.ma.persistence.JSON.JSONWriter;


public class NodeRecordEditActivity extends BaseActivity implements AsyncResponse {

    private String picturePath;
    private String oldNodeId = null;
    String[] permissions;
    private int recordTime;
    private int progressStatus = 0;
    private ProgressBar progressBar;
    private JSONWriter JSONWriter;
    private TextView progressTextview;
    TextView initialWifiTextview;
    TextView initialWifiLabelTextview;
    TextView coordinatesLabelTextview;
    private ImageButton recordButton;
    ImageButton captureButton;
    ImageButton saveNodeButton;
    private ImageView cameraImageview;
    ImageView refreshImageview;
    private EditText nodeIdEdittext;
    private EditText recordTimeText;
    private EditText descriptionEdittext;
    private EditText coordinatesEdittext;
    private Spinner wifiNamesDropdown;
    private DatabaseHandler databaseHandler;
    private boolean pictureTaken;
    private boolean takingPictureAtTheMoment;
    private boolean showingBigPictureAtTheMoment;
    private boolean updateMode = false;
    private Node nodeToUpdate;
    private File sdCard = Environment.getExternalStorageDirectory();
    private Context context = this;
    private WifiManager wifiManager;
    private Timestamp timestamp;
    private Fingerprint fingerprint = null;
    private FingerprintTask fingerprintTask;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 3;
    private static final int CAM_REQUEST = 1;
    RelativeLayout buttonsLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_node_record_edit, contentFrameLayout);

        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA};

        // Check permissions
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(NodeRecordEditActivity.this, permissions, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }

        databaseHandler = DatabaseHandlerFactory.getInstance(this);
        JSONWriter = new JSONWriter();


        recordButton = (ImageButton) findViewById(R.id.record_button);
        captureButton  = (ImageButton) findViewById(R.id.capture_button);
        saveNodeButton = (ImageButton) findViewById(R.id.save_node_button);
        cameraImageview = (ImageView) findViewById(R.id.camera_imageview);
        refreshImageview = (ImageView) findViewById(R.id.refresh_imageview_recordactivity);
        descriptionEdittext = (EditText) findViewById(R.id.description_edittext);
        nodeIdEdittext = (EditText) findViewById(R.id.record_id_edittext);
        recordTimeText = (EditText) findViewById(R.id.measure_time_edittext);
        coordinatesEdittext = (EditText) findViewById(R.id.coordinates_edittext);
        wifiNamesDropdown = (Spinner) findViewById(R.id.wifi_names_dropdown);
        progressTextview = (TextView) findViewById(R.id.progress_textview);
        initialWifiTextview = (TextView) findViewById(R.id.initial_wifi_textview);
        initialWifiLabelTextview = (TextView) findViewById(R.id.initial_wifi_label_textview);
        coordinatesLabelTextview = (TextView) findViewById(R.id.coordinates_label_textview_editmode);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        buttonsLayout = (RelativeLayout) findViewById(R.id.buttons_layout_rec_and_edit);

        recordTimeText.setText("3");
        picturePath = null;

        pictureTaken = false;
        takingPictureAtTheMoment = false;
        showingBigPictureAtTheMoment = false;


        if (hasPermissions(this, permissions)) {
            refreshWifiDropdown();
        }

        recordButton.setImageResource(R.drawable.fingerprint);
        captureButton.setImageResource(R.drawable.camera);

        progressBar.setVisibility(View.INVISIBLE);
        progressTextview.setVisibility(View.INVISIBLE);
        initialWifiLabelTextview.setVisibility(View.INVISIBLE);
        initialWifiTextview.setVisibility(View.INVISIBLE);
        coordinatesLabelTextview.setVisibility(View.INVISIBLE);
        coordinatesEdittext.setVisibility(View.INVISIBLE);



        // Check if Update-Mode is enabled
        Intent intent = getIntent();
        if (intent.hasExtra("nodeId")) {
            updateMode = true;
            initialWifiTextview.setVisibility(View.VISIBLE);
            oldNodeId = (String) intent.getExtras().get("nodeId");
            nodeToUpdate = databaseHandler.getNode(oldNodeId);
            nodeIdEdittext.setText(nodeToUpdate.getId());
            descriptionEdittext.setText(nodeToUpdate.getDescription());
            picturePath = nodeToUpdate.getPicturePath();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.END_OF, R.id.save_node_button);

            ImageButton deleteNodeButton = new ImageButton(this);
            deleteNodeButton.setLayoutParams(params);
            deleteNodeButton.setImageResource(R.drawable.trash_node);
            buttonsLayout.addView(deleteNodeButton);


            if (nodeToUpdate.getFingerprint() != null) {
                recordButton.setImageResource(R.drawable.fingerprint_done);
                initialWifiLabelTextview.setVisibility(View.VISIBLE);
                initialWifiTextview.setText(nodeToUpdate.getFingerprint().getWifiName());
            }

            if (nodeToUpdate.getCoordinates().length() > 0) {
                coordinatesEdittext.setVisibility(View.VISIBLE);
                coordinatesLabelTextview.setVisibility(View.VISIBLE);
                coordinatesEdittext.setText(nodeToUpdate.getCoordinates());
            }

            if (picturePath == null) {
                Glide.with(this).load(R.drawable.unknown).into(cameraImageview);
            } else {
                Glide.with(this).load(picturePath).into(cameraImageview);
            }

            deleteNodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(getString(R.string.nodelist_delete_entry_title_question))
                            .setMessage("Soll der Node \"" + oldNodeId + "\" wirklich gelöscht werden?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if (nodeToUpdate.getPicturePath() != null) {
                                        File imageFile = new File(nodeToUpdate.getPicturePath());
                                        imageFile.delete();
                                    }
                                    databaseHandler.deleteNode(nodeToUpdate);

                                    finish();
                                    Intent intent = new Intent(context, NodeListActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }


        saveNodeButton.setImageResource(R.drawable.save);
        refreshImageview.setImageResource(R.drawable.refresh);
        refreshImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshWifiDropdown();
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (nodeIdEdittext.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), getString(R.string.please_enter_node_name), Toast.LENGTH_SHORT).show();
                    } else if (databaseHandler.checkIfNodeExists(nodeIdEdittext.getText().toString()) && !updateMode) {
                        Toast.makeText(getApplicationContext(), getString(R.string.node_already_exists_toast), Toast.LENGTH_SHORT).show();
                    } else {

                        recordButton.setEnabled(false);
                        recordTimeText.setEnabled(false);
                        wifiNamesDropdown.setEnabled(false);
                        refreshImageview.setEnabled(false);

                        progressBar.setVisibility(View.VISIBLE);
                        progressTextview.setVisibility(View.VISIBLE);
                        recordButton.setImageResource(R.drawable.fingerprint_low_contrast);

                        recordTime = Integer.parseInt(recordTimeText.getText().toString());

                        fingerprintTask = new FingerprintTask(wifiNamesDropdown.getSelectedItem().toString(), 60 * recordTime, wifiManager, false, progressBar, progressTextview);
                        fingerprintTask.delegate = NodeRecordEditActivity.this;
                        fingerprintTask.execute();
                    }
                }
        });


        captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if (nodeIdEdittext.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.please_enter_node_name), Toast.LENGTH_SHORT).show();
                } else {
                    takingPictureAtTheMoment = true;
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    timestamp = new Timestamp(System.currentTimeMillis());
                    File file = FileUtilities.getFile(nodeIdEdittext.getText().toString(), timestamp);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(cameraIntent, CAM_REQUEST);
                }
                }
        });


        cameraImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showingBigPictureAtTheMoment = true;
                if (pictureTaken) {
                    Intent intent = new Intent(getApplicationContext(), MaxPictureActivity.class);
                    intent.putExtra("picturePath", picturePath);
                    startActivity(intent);
                } else if (nodeToUpdate != null && nodeToUpdate.getPicturePath() != null) {

                    Intent intent = new Intent(getApplicationContext(), MaxPictureActivity.class);
                    intent.putExtra("picturePath", nodeToUpdate.getPicturePath());
                    startActivity(intent);
                }
            }
        });

        saveNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateMode) {
                    checkUpdatedNodeID();
                } else {
                    saveNewNode();
                }
            }
        });
    }


    /**
     * If the fingerprinting background task finished
     * @param fp the Fingerprint from the AsyncTask
     */
    @Override
    public void processFinish(Fingerprint fp, int seconds) {
        fingerprint = fp;

        if (fp == null) {
            recordButton.setImageResource(R.drawable.fingerprint);
        } else {
            recordButton.setImageResource(R.drawable.fingerprint_done);
        }
        progressBar.setVisibility(View.INVISIBLE);
        progressTextview.setVisibility(View.INVISIBLE);
        refreshImageview.setEnabled(true);
        //fingerprintTaken = true;
    }


    /**
     * Scan for WiFi names (SSIDs) and add them to the dropdown
     */
    private void refreshWifiDropdown() {

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiScanner wifiScanner = WifiScannerFactory.createInstance();
        List<String> wifiNamesList = wifiScanner.getAvailableNetworks(wifiManager, true);

        final ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, wifiNamesList);
        wifiNamesDropdown.setAdapter(dropdownAdapter);
        Toast.makeText(getApplicationContext(), getString(R.string.refreshed_toast), Toast.LENGTH_SHORT).show();
    }


    /**
     * When returning from Camera Activity after taking a picture
     * if a picture was taken (and not just returned),
     * save the path and show the picture in the imageview.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            pictureTaken = true;
            takingPictureAtTheMoment = false;
            long realTimestamp = timestamp.getTime();
            picturePath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/" + nodeIdEdittext.getText() + "_" + realTimestamp + ".jpg";
            Glide.with(this).load(picturePath).into(cameraImageview);
        }
    }


    /**
     * Create and persist the new Node.
     */
    private void saveNewNode() {
        if (nodeIdEdittext.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.please_enter_node_name), Toast.LENGTH_SHORT).show();
        } else {

                final String picPathToSave;
                if (pictureTaken) {
                    long realTimestamp = timestamp.getTime();
                    picPathToSave = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/" + nodeIdEdittext.getText() + "_" + realTimestamp + ".jpg";
                } else {
                    picPathToSave = null;
                }

                final String nodeID = nodeIdEdittext.getText().toString();
                final String nodeDescription = descriptionEdittext.getText().toString();

                if (databaseHandler.checkIfNodeExists(nodeIdEdittext.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.node_already_exists_toast), Toast.LENGTH_LONG).show();
                } else {

                    // If no fingerprint has been captured...
                    if (fingerprint == null) {
                        new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.no_fingerprint_title_text))
                                .setMessage("Soll der Ort \"" + nodeIdEdittext.getText().toString() + "\" wirklich ohne Fingerprint erstellt werden?")
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Node node = NodeFactory.createInstance(nodeID, nodeDescription, null, "", picPathToSave, "");
                                        JSONWriter.writeJSON(node);
                                        databaseHandler.insertNode(node);
                                        Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();
                                        resetUiElements();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    // If a fingerprint has been captured...
                    } else {

                        final Node node = NodeFactory.createInstance(nodeID, nodeDescription, fingerprint, "", picPathToSave, "");
                        JSONWriter.writeJSON(node);
                        databaseHandler.insertNode(node);
                        progressStatus = 0;
                        progressTextview.setText(String.valueOf(progressStatus));
                        progressBar.setProgress(progressStatus);
                        Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();
                        askForNewNode();
                }
            }
        }
    }


    /**
     * Check if the given nodeID is valid
     */
    private void checkUpdatedNodeID() {
        if (nodeIdEdittext.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.please_enter_node_name), Toast.LENGTH_SHORT).show();
        } else {
            if (oldNodeId.equals(nodeIdEdittext.getText().toString())) {
                // old id == new id -> update.
               saveUpdatedNode();
            } else {
                if (databaseHandler.checkIfNodeExists(nodeIdEdittext.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.node_already_exists_toast), Toast.LENGTH_LONG).show();
                } else {
                    saveUpdatedNode();
                }
            }
        }
    }


    /**
     * Save an updated Node to database
     */
    private void saveUpdatedNode() {
        final String picPathToSave;
        final String nodeID = nodeIdEdittext.getText().toString();
        final String nodeDescription = descriptionEdittext.getText().toString();
        final String coordinates = coordinatesEdittext.getText().toString();

        if (pictureTaken) {
            long realTimestamp = timestamp.getTime();
            picPathToSave = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/" + nodeIdEdittext.getText() + "_" + realTimestamp + ".jpg";
        } else {
            if (picturePath == null) {
                picPathToSave = null;
            } else {
                picPathToSave = picturePath;
            }
        }

        // If no new fingerprint was taken
        if (fingerprint == null) {
            // If an old fingerprint exists
            if (nodeToUpdate.getFingerprint() != null) {
                final Node node = NodeFactory.createInstance(nodeID, nodeDescription, nodeToUpdate.getFingerprint(), coordinates, picPathToSave, nodeToUpdate.getAdditionalInfo());
                JSONWriter.writeJSON(node);
                databaseHandler.updateNode(node, oldNodeId);
                Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();

                finish();
                Intent intent = new Intent(context, NodeListActivity.class);
                startActivity(intent);

            // If no new fingerprint was taken and no old exists
            } else {

            //if (nodeToUpdate.getFingerprint() == null) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.no_fingerprint_title_text))
                        .setMessage("Soll der Ort \"" + nodeIdEdittext.getText() + "\" wirklich ohne Fingerprint gespeichert werden?")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final Node node = NodeFactory.createInstance(nodeID, nodeDescription, null, coordinates, picPathToSave, "");
                                JSONWriter.writeJSON(node);
                                databaseHandler.updateNode(node, oldNodeId);
                                Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();
                                resetUiElements();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        // If a new fingerprint was taken
        } else {

            final Node node = NodeFactory.createInstance(nodeID, nodeDescription, fingerprint, coordinates, picPathToSave, nodeToUpdate.getAdditionalInfo());
            JSONWriter.writeJSON(node);
            databaseHandler.updateNode(node, oldNodeId);
            Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();

            finish();
            Intent intent = new Intent(context, NodeListActivity.class);
            startActivity(intent);
        }
    }


    /**
     * Reset buttons. textfields, and progress.
     * Cancel the fingerprinting backgroundtask.
     */
    private void resetUiElements() {
        if (fingerprintTask != null) {
            fingerprintTask.cancel(false);
        }
        progressStatus = 0;
        progressTextview.setText(String.valueOf(progressStatus));
        progressBar.setProgress(progressStatus);
        recordButton.setEnabled(true);
        recordTimeText.setEnabled(true);
        nodeIdEdittext.setEnabled(true);
        wifiNamesDropdown.setEnabled(true);
        descriptionEdittext.setEnabled(true);
    }


    /**
     * Ask, if new node should be created. If not, finish() and go to NodeListActivity to show all nodes
     */
    private void askForNewNode() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.record_another_node_title_text))
                .setMessage(getString(R.string.record_another_node_question))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(context, NodeListActivity.class);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /**
     * Check for permissions
     * @param context the context
     * @return boolean, if all permissions are given
     */
    private boolean hasPermissions(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("----- Permission not granted: " + permission);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check again for permissions if they were not granted
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(NodeRecordEditActivity.this, permissions, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Stop recording thread if the Activity is stopped,
     * except the user left the activity for taking a picture.
     */
    @Override
    protected void onStop() {
        super.onStop();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();

        if (isScreenOn) {
            if (!takingPictureAtTheMoment && !showingBigPictureAtTheMoment) {
                if (fingerprintTask != null) {
                    fingerprintTask.cancel(false);
                }
            }
        }
    }
}