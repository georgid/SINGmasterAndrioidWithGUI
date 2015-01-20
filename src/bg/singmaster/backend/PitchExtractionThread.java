package bg.singmaster.backend;

import java.util.Date;

import com.kamengoranchev.singmaster.MainActivity;

import android.util.Log;
import be.hogent.tarsos.dsp.AudioEvent;


/**
 * pitch extraction for each time window in separate thread. 
 * */
public class PitchExtractionThread implements Runnable{

MainActivity mMainActivity;
AudioProcessor mAudioProcessor;

long mTotalNumBytesProcessed;

//  which is current time window 
int mCurrWindowNumber;


public PitchExtractionThread(MainActivity mainActivity){
	
	this.mAudioProcessor = mainActivity.mAudioProcessor;
	mMainActivity = mainActivity;

	this.mTotalNumBytesProcessed = 0;
	
	
}

	@Override
	public void run() {
		
    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

		// process sequentially from queue:
		while (!this.mAudioProcessor.mQueueAudio.isEmpty()){
			
			byte [] currAudioBuffer = this.mAudioProcessor.mQueueAudio.poll();
			
			// time. only for DEBUG purpose
			Date timeBefore = new Date();
			
			processSingleAudioBuffer(currAudioBuffer);

			// time. only for DEBUG purpose
			Date timeAfter = new Date();
			Log.i(PitchExtractionThread.class.getName(),  ": time to process pitch window: " + String.valueOf(timeAfter.getTime() - timeBefore.getTime() ) );
			
		}
		
		
		// last audio buffer is processed , set flag
		mMainActivity.runOnUiThread(new Runnable(){
			@Override
			public void run(){
			mMainActivity.mPlayButton.setEnabled(true);
		}
		
		});
		//		if ( this.mCurrWindowNumber == mLastWindowNum)
		
	}

	void processSingleAudioBuffer(byte [] audioBuffer) {
	
		// I assume that 2 bytes represent one sample. Is this right? 
		//int numSamples = this.mNumBytesRead / (mAudioProcessor.mTarsosFormat.getSampleSizeInBits() / 8);
		int numSamples = (audioBuffer.length / 2) / (mAudioProcessor.mTarsosFormat.getSampleSizeInBits() / 8);
		
		AudioEvent audioEvent = new AudioEvent(mAudioProcessor.mTarsosFormat, numSamples);
		
		audioEvent.setFloatBufferWithByteBuffer(audioBuffer);
		mTotalNumBytesProcessed += audioBuffer.length;
		audioEvent.setBytesProcessed(mTotalNumBytesProcessed);
		
		this.mAudioProcessor.mPitchExtractor.mPitchProcessor.process(audioEvent);

	
		
	}
	

}
