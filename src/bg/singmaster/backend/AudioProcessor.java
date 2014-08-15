package bg.singmaster.backend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;


import be.hogent.tarsos.dsp.AudioEvent;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/***
 * 
 * records sound from mic
 * calls pitchExtractor from TarsosDSP
*/
public class AudioProcessor {
	
	private int mBufferSize;
	private byte[] mBuffer;
	
	private AudioRecord mRecorder;
	private int mSampleRate;
	
	public boolean mIsRecording = false;
	
	private be.hogent.tarsos.dsp.AudioFormat mTarsosFormat;
	
	public PitchExtractor mPitchExtractor; 
	
	// in seconds
	public final static double mRecordingDuration = 10.0; 
	
	
	
	public AudioProcessor(int sampleRate) {
		mSampleRate = sampleRate;
		
		mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		
		mBuffer = new byte[mBufferSize];
		
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				mSampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				mBufferSize);
		
		mTarsosFormat = new be.hogent.tarsos.dsp.AudioFormat(
				(float)mSampleRate, 16, 1, true, false);
		
		mPitchExtractor = new PitchExtractor(mSampleRate, mBufferSize);
		
		
	}
	
	/** trigger recording
	 * 
	 */
	public void record() {
		// prepare output container
		this.mPitchExtractor.mDetectedPitchArray = new ArrayList<DetectedPitch>(); 
		
		
		 if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
		        
	    	  Log.e(AudioProcessor.class.getName(), "Unable to access the audio recording hardware - is your mic working?");

	        return;
	      }
		
		mRecorder.startRecording();
		mIsRecording  = true;
		processAudio();
		
		
			
		
		
	}
	

	
	/**
	 * record and call pitch extraction
	 * */
	private void processAudio() {
		Thread audioProcessingThread = new Thread(new AudioProcessingThread());
		audioProcessingThread.start();
	}
	
	/**
	 *  thread with not file recording
	 * **/
	public class AudioProcessingThread implements Runnable{

		@Override
		public void run() {
			 // We're important...
		    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		    
		    		Date start = new Date();
		    	    Date end = new Date();  
		        // let record for some seconds
		    	 while (end.getTime() - start.getTime() < AudioProcessor.mRecordingDuration * 1000) {
		    		 
		    	 
			
				int bufferReadResult = mRecorder.read(mBuffer, 0, mBufferSize);
				AudioEvent audioEvent = new AudioEvent(mTarsosFormat, bufferReadResult);
				audioEvent.setFloatBufferWithByteBuffer(mBuffer);
				
				Date timeBefore = new Date();
				mPitchExtractor.mPitchProcessor.process(audioEvent);
				Date timeAfter = new Date();
				Log.i(AudioProcessor.class.getName(), String.valueOf(end.getTime() - start.getTime() ) );
				
				// update time. Time for processing included in counting. not good
				end = new Date();
				
			}	
		    	 // trigger stop of recording process
		    	 mRecorder.stop();
		    	 mIsRecording = false;
			
		
		}// end run
	
	} // end AudioProcessigThread
	
	
	

}
