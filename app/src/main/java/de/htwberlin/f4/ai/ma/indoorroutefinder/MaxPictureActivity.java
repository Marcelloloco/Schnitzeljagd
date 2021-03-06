package de.htwberlin.f4.ai.ma.indoorroutefinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import de.htwberlin.f4.ai.ma.indoorroutefinder.android.BaseActivity;


/**
 * Created by Johann Winter
 *
 * Acitvity for showing a node's picture in fullscreen mode
 */

public class MaxPictureActivity extends BaseActivity {

    ImageView maxImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_maximize_picture, contentFrameLayout);

        maxImageView = (ImageView) findViewById(R.id.maxImageView);

        final Intent intent = getIntent();
        String picturePath = (String) intent.getExtras().get("picturePath");
        setTitle((String) intent.getExtras().get("nodeID"));

        maxImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(intent.getBooleanExtra("isHint", false)){
                    finish();
                }
            }
        });

        if (picturePath != null) {
            Glide.with(this).load(picturePath).into(maxImageView);
        } else {
            Glide.with(this).load(R.drawable.unknown).into(maxImageView);
        }
    }
}
