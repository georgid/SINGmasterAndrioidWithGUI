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
	
	
	
	public OnRecordListener(MainActivity mainActivity){
	this.mMainActivity = mainActivity;	
		
	}
	
	
	@Override
	public void onClick(View v) {

        this.mMainActivity.mVoiceGraph.setVisibility(View.INVISIBLE);
        // get duration t from tempo control TODO
        
        // wait some time. allow time to singer to prepare
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                
            	enableNote(0);

            	// start recording
                mMainActivity.mAudioProcessor.record();
            }
        }, Parameters.INITIAL_WAIT_TIME * 1000);
        

        
        // highlight other notes from exercise
        for (int noteNum = 1;  noteNum <= this.numNotes; noteNum ++)
       
        	timer.schedule( 
        			new NoteTimerTask(noteNum),
        		 Parameters.INITIAL_WAIT_TIME  * 1000 + noteNum * Parameters.RECORDING_DURATION * 1000/numNotes );

      
      
        
        // wait for recording to finish
//		while (mMainActivity.mAudioProcessor.mIsRecording)
//		{}
//		Log.i("TAG", "recording finished");	
		
        
		// wait  while all pitch extracted
//		while (!this.mIsAllAudioProcessed)
//		{}
//		Log.i("TAG", "pitch extraction finished");	
        
        
		//  store audio TODO
        
        // enable play button TODO
    
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
