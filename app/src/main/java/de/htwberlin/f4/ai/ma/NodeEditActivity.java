package de.htwberlin.f4.ai.ma;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;


/**
 * Created by Johann Winter
 */

public class NodeEditActivity extends BaseActivity {

    private EditText idEditText;
    TextView wlanTextview;
    private EditText descriptionEditText;
    private EditText coordinatesEditText;
    private ImageView cameraImageView;
    Button saveButton;
    Button deleteButton;
    Button changePictureButton;
    Button changeFingerprintButton;
    private Context context = this;
    private String oldNodeId;
    private String picturePath;

    private Node node;
    private DatabaseHandler databaseHandler;

    private File tempFile;
    private final File sdCard = Environment.getExternalStorageDirectory();
    private final File pictureFolder = new File(sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures");
    private final File tempFolder = new File(sdCard.getAbsolutePath() + "/IndoorPositioning/.temp");

    private Timestamp timestamp;
    private static final int CAM_REQUEST = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_node_edit, contentFrameLayout);

        idEditText = (EditText) findViewById(R.id.edit_id_edittext);
        wlanTextview = (TextView) findViewById(R.id.wifi_name_textview);
        descriptionEditText = (EditText) findViewById(R.id.description_edittext);
        coordinatesEditText = (EditText) findViewById(R.id.coordinates_edittext);
        cameraImageView = (ImageView) findViewById(R.id.camera_imageview);
        saveButton = (Button) findViewById(R.id.save_button);
        deleteButton = (Button) findViewById(R.id.delete_button);
        changePictureButton = (Button) findViewById(R.id.change_picture_button);
        changeFingerprintButton = (Button) findViewById(R.id.change_fingerprint_button);

        final Intent intent = getIntent();
        final String nodeName = intent.getExtras().get("nodeName").toString();

        // Create picture folders
        if (!pictureFolder.exists()) {
            pictureFolder.mkdirs();
        }
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }

        databaseHandler = DatabaseHandlerFactory.getInstance(this);
        node = databaseHandler.getNode(nodeName);
        oldNodeId = node.getId();

        idEditText.setText(node.getId());
        wlanTextview.setText(node.getFingerprint().getWifiName());
        descriptionEditText.setText(node.getDescription());
        coordinatesEditText.setText(node.getCoordinates());


        picturePath = node.getPicturePath();
        if (picturePath != null) {
            Glide.with(this).load(node.getPicturePath()).into(cameraImageView);

            System.out.println("picturePath = " + picturePath);
        } else {
            Glide.with(this).load(R.drawable.unknown).into(cameraImageView);
        }


        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MaxPictureActivity.class);
                intent.putExtra("picturePath", node.getPicturePath());
                startActivity(intent);
            }
        });


        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempFile = null;

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tempFile = new File(tempFolder, node.getId() + ".jpg");

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(cameraIntent, CAM_REQUEST);
            }
        });


        changeFingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                node.setId(idEditText.getText().toString());
                node.setDescription(descriptionEditText.getText().toString());
                node.setCoordinates(coordinatesEditText.getText().toString());
                node.setPicturePath(picturePath);

                timestamp = new Timestamp(System.currentTimeMillis());
                long realTimestamp = timestamp.getTime();

                if (tempFile != null) {
                    try {
                        copyFile(tempFile, new File(pictureFolder, idEditText.getText().toString() + "_" + realTimestamp + ".jpg"));
                        node.setPicturePath(sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/" + idEditText.getText().toString() + "_" + realTimestamp +  ".jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                databaseHandler.updateNode(node, oldNodeId);
                finish();
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.delete_title_question))
                        .setMessage("Soll der Ort \"" + nodeName + "\" wirklich gelöscht werden?")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Log.d("NodeEditActivity", "Imagefilepath to delete: " + node.getPicturePath());
                                if (node.getPicturePath() != null) {
                                    File imageFile = new File(node.getPicturePath());
                                    imageFile.delete();
                                }
                                databaseHandler.deleteNode(node);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Glide.with(this).load(tempFile).into(cameraImageView);
        cameraImageView.setClickable(false);
    }

    // Copy temporary image file to image folder
    public static void copyFile(File source, File destination) throws IOException {
        FileChannel inChannel = new FileInputStream(source).getChannel();
        FileChannel outChannel = new FileOutputStream(destination).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            outChannel.close();

            source.delete();
        }
    }
}