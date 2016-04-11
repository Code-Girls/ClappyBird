package com.zoneigh.clappybird;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.zoneigh.clappybird.model.AndroidClapListener;
import com.zoneigh.clappybird.model.AndroidGamer;

public class MainActivity extends AndroidApplication {
		
	private AndroidClapListener androidClapListener;
    
    public MainActivity() {
    	super();
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	if (null != androidClapListener) {
    		androidClapListener.stopListening();
    	}
    };
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (null != androidClapListener) {
    		androidClapListener.stopListening();
    	}
    }
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Window Configuration
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        RelativeLayout layout = new RelativeLayout(this);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
        // Gamer
        AndroidGamer androidGamer = new AndroidGamer(this);
        
        // Clap Listener
    	this.androidClapListener = new AndroidClapListener();

        View gameView = initializeForView(new ClappyBird(androidGamer, this.androidClapListener), cfg);
        layout.addView(gameView);
        
        // TODO place real banner
        Button sampleButton = new Button(this);
        sampleButton.setOnClickListener(new Button.OnClickListener() {
            // TODO replace this with clap detection
            public void onClick(View v) {
            	androidClapListener.sendClap();
            	return;
            }
        });
        
        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layout.addView(sampleButton, adParams);
        // END
        
        setContentView(layout);
    }
}