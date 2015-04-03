package com.kamengoranchev.singmaster;

import bg.singmaster.backend.Parameters;
import bg.singmaster.backend.ParametersExercise;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class OnTempoChangeListener implements SeekBar.OnSeekBarChangeListener{
MainActivity mMa; 

public OnTempoChangeListener(MainActivity ma){
	mMa = ma;
}
	
	public static int FACTOR_FAST_TEMPO = 1;
	public static int FACTOR_SLOW_TEMPO = 2;
	
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
		if (progress == 0){ // slow tempo
			Parameters.RECORDING_DURATION = ParametersExercise.noteMIDIs.length * FACTOR_SLOW_TEMPO;
			mMa.mTempoState.setText("slow");
		}
		// progress =1 . fast tempo
		else  {
			Parameters.RECORDING_DURATION = ParametersExercise.noteMIDIs.length * FACTOR_FAST_TEMPO;
			mMa.mTempoState.setText("fast");
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}


//
//new OnSeekBarChangeListener() {
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar,
//            int progress, boolean fromUser) {
//                    }
//}
