package bg.singmaster.listeners;

import bg.singmaster.backend.NoteSynthesisThread;
import android.view.View;

/***
 *  replay reference notes for synthesis
 */
public class OnPlaySynthListener implements View.OnClickListener{

	public int mMidiNote;
	
	/**
	 * init with given midi note to replay
	 * */
	public OnPlaySynthListener (int midiNote){
		mMidiNote = midiNote;
	}
	
	@Override
	public void onClick(View v) {
		// convert to freq
		double tmpPower = (double) (mMidiNote - 69) / 12;
		double freq = Math.pow(2, tmpPower) * 440;
		

		NoteSynthesisThread noteSynthThhread = new NoteSynthesisThread();
		noteSynthThhread.setFreq(freq);
		noteSynthThhread.run();

	}
	
}
