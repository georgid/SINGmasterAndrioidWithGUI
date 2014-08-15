package com.kamengoranchev.singmaster;

import java.util.Timer;
import java.util.TimerTask;

import android.view.View;
import android.widget.TextView;
import com.kamengoranchev.singmaster.R;

public class OnRecordListener implements View.OnClickListener {

	MainActivity mMainActivity;
	int duration = 3000; 
	int waitTime = 1000;
	int numNotes = 3; 
	
	public OnRecordListener(MainActivity mainActivity){
	this.mMainActivity = mainActivity;	
		
	}
	@Override
	public void onClick(View v) {

        this.mMainActivity.mVoiceGraph.setVisibility(View.INVISIBLE);
        // get duration t from tempo control TODO
        
        // wait 1 sec. give time to singer to prepare
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enableNote(0);
            }
        }, waitTime);
        
        // record : TODO
        
        // visualize notes 
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enableNote(1);
            }
        }, waitTime + 1*duration/numNotes );

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enableNote(2);
            }
        }, waitTime + 2*duration/numNotes);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enableNote(3);
            }
        }, waitTime + 3*duration/numNotes);
        
        // stop recording after duration t TODO
        
        //  store audio TODO
        
        // enable play button
    
	}	

	private void enableNote(final int noteNumber) {
	    final TextView[] notes = new TextView[3];
	    notes[0] = (TextView) this.mMainActivity.findViewById(R.id.note_1);
	    notes[1] = (TextView) this.mMainActivity.findViewById(R.id.note_2);
	    notes[2] = (TextView) this.mMainActivity.findViewById(R.id.note_3);
	
	    this.mMainActivity.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	            for (int i = 0; i < numNotes; i++) {
	                notes[i].setEnabled(noteNumber == i);
	            }
	        }
	    });
	}
	
	
	

}
