package bg.singmaster;

import java.util.Timer;
import java.util.TimerTask;

import android.view.View;
import android.widget.TextView;
import bg.singmaster.R;

public class OnRecordListener implements View.OnClickListener {

	MainActivity mMainActivity;
	int duration; 
	
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
        }, 1000);
        
        // record : TODO
        
        // visualize notes 
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enableNote(1);
            }
        }, 2000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enableNote(2);
            }
        }, 3000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enableNote(3);
            }
        }, 4000);
        
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
	            for (int i = 0; i < 3; i++) {
	                notes[i].setEnabled(noteNumber == i);
	            }
	        }
	    });
	}
	
	
	

}
