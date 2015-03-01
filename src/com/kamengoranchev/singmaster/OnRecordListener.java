package com.kamengoranchev.singmaster;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import bg.singmaster.backend.AudioProcessor;
import bg.singmaster.backend.Parameters;

import com.kamengoranchev.singmaster.R;

public class OnRecordListener implements View.OnClickListener {

	MainActivity mMainActivity;
	int numNotes = 3;
	Timer mNoteTimer;
	
	
	public OnRecordListener(MainActivity mainActivity){
	this.mMainActivity = mainActivity;	
	this.mNoteTimer  = new Timer();
	}
	
	
	@Override
	public void onClick(View v) {
		
	   this.mMainActivity.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	            for (int i = 0; i < numNotes; i++) {
	            	mMainActivity.mVoiceGraph.setVisibility(View.INVISIBLE);
	                mMainActivity.mPlayButton.setEnabled(false);
	                mMainActivity.mRecButton.setEnabled(false);
	            }
	        }
	    });
	
        
        
        // get duration t from tempo control TODO
        
        // wait some time. allow time to singer to prepare
        this.mNoteTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                
            	enableNote(0);

            	// start recording
                mMainActivity.mAudioProcessor.record(mMainActivity);
            }
        }, Parameters.INITIAL_WAIT_TIME * 1000);
        

        
        // highlight the other notes from exercise
        for (int noteNum = 1;  noteNum <= this.numNotes; noteNum ++)
       
        	this.mNoteTimer.schedule( 
        			new NoteTimerTask(noteNum),
        		 Parameters.INITIAL_WAIT_TIME  * 1000 + noteNum * Parameters.RECORDING_DURATION * 1000/numNotes );

    
	}	
	
	
	/**
	 * visualize color for one note 
	 * */
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
	
	/**
	 * controls the timing to enable Note 
	 * */
	public class NoteTimerTask extends TimerTask{
	
	private int mWhichNote;
	
	public NoteTimerTask(int whichNote){
	this.mWhichNote = whichNote; 
	} 
		@Override
		public void run() {
            enableNote(mWhichNote);
			
		}
		
	}

	
	
	

}
