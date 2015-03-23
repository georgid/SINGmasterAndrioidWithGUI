package bg.singmaster.backend;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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

public PipedInputStream mAudioReadStream;

public PitchExtractionThread(MainActivity mainActivity, PipedInputStream audioReadStream){
	
	this.mAudioProcessor = mainActivity.mAudioProcessor;
	mMainActivity = mainActivity;

	this.mTotalNumBytesProcessed = 0;
	mAudioReadStream = audioReadStream;
	
	
}
	/**
	 * extract pitch for each audioFrame  (buffer) in audio buffer. 
	 * convert to pitchScale 
	 * */
	@Override
	public void run() {
		
    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    	
//    	Log.i("TAG: ", "in PITCH EXTRACTION THREAD!");
		
    	
	while (this.mAudioProcessor.mRecorder.getRecordingState() != this.mAudioProcessor.mRecorder.RECORDSTATE_STOPPED)
		
		try {
			while (mAudioReadStream.available() != 0){
	
//				Log.i("TAG: ", "PROCESSING QUEUE!");
				
				byte [] currAudioBuffer = new byte[1024];
				int numBytesRead = mAudioReadStream.read(currAudioBuffer, 0, 1024);
				
				// time. only for DEBUG purpose
				Date timeBefore = new Date();
				
				processSingleAudioBuffer(currAudioBuffer, numBytesRead);
	
				// time. only for DEBUG purpose
				Date timeAfter = new Date();
				Log.i(PitchExtractionThread.class.getName(),  ": time to process pitch window: " + String.valueOf(timeAfter.getTime() - timeBefore.getTime() ) );
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
//		while (this.mAudioProcessor.mRecorder.getRecordingState() != this.mAudioProcessor.mRecorder.RECORDSTATE_STOPPED)
//			
//			
//			// if empty, but still recording, outer loop will get it here
//			// process sequentially from queue:
//			while (!this.mAudioProcessor.mQueueAudio.isEmpty()){
//	
////				Log.i("TAG: ", "PROCESSING QUEUE!");
//				
//				byte [] currAudioBuffer = this.mAudioProcessor.mQueueAudio.poll();
//				
//				// time. only for DEBUG purpose
//				Date timeBefore = new Date();
//				
//				processSingleAudioBuffer(currAudioBuffer);
//	
//				// time. only for DEBUG purpose
//				Date timeAfter = new Date();
//				Log.i(PitchExtractionThread.class.getName(),  ": time to process pitch window: " + String.valueOf(timeAfter.getTime() - timeBefore.getTime() ) );
//				
//			}
		
		
		// last audio buffer is processed , set flag
		mMainActivity.runOnUiThread(new Runnable(){
			@Override
			public void run(){
			mMainActivity.mPlayButton.setEnabled(true);
			mMainActivity.mRecButton.setEnabled(true);
		}
		
		});
		//		if ( this.mCurrWindowNumber == mLastWindowNum)
		
	}

	void processSingleAudioBuffer(byte [] audioBuffer, int numBytes) {
	
		// I assume that 2 bytes represent one sample. Is this right? 
		//int numSamples = this.mNumBytesRead / (mAudioProcessor.mTarsosFormat.getSampleSizeInBits() / 8);
//		int numSamples = (audioBuffer.length / 2) / (mAudioProcessor.mTarsosFormat.getSampleSizeInBits() / 8);
		
		if (numBytes != audioBuffer.length)
			Log.i("TAG", "buffer has audio less than total given buffer length" );
		byte [] audioNonEmpty = new byte[numBytes];
		System.arraycopy(audioBuffer, 0, audioNonEmpty, 0, numBytes);
		
		AudioEvent audioEvent = new AudioEvent(mAudioProcessor.mTarsosFormat, numBytes);
		
		audioEvent.setFloatBufferWithByteBuffer(audioNonEmpty);
		mTotalNumBytesProcessed += numBytes;
		audioEvent.setBytesProcessed(mTotalNumBytesProcessed);
		
		// this calls internally mPitchExtractor.handlePitch
		this.mAudioProcessor.mPitchExtractor.mPitchProcessor.process(audioEvent);

	
		
	}
	

}
