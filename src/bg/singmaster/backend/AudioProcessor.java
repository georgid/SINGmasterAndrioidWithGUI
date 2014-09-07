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
	
	// in bytes
	private int mBufferSize;
	private byte[] mCurrentBuffer;
	
	private int mNumSamplesPerBuffer;
	
	private AudioRecord mRecorder;
	public int mSampleRate;
	
	public boolean mIsRecording = false;
	public boolean mIsAllAudioProcessed = false;
	
	private be.hogent.tarsos.dsp.AudioFormat mTarsosFormat;
	
	public PitchExtractor mPitchExtractor; 
	
	// in seconds
	public int mLastWindowNum = -1;
	
	
	
	/**
	 * constructor
	 * */
	public AudioProcessor(int sampleRate) {
		mSampleRate = sampleRate;
		
		// in bytes 
		mBufferSize =  AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		
		mCurrentBuffer = new byte[mBufferSize];
		
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				mSampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				mBufferSize);
		
		if (mRecorder.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT)
		mNumSamplesPerBuffer  = mBufferSize / 2;
				
		mTarsosFormat = new be.hogent.tarsos.dsp.AudioFormat(
				(float)mSampleRate, 16, 1, true, false);
		
		mPitchExtractor = new PitchExtractor(mSampleRate, mNumSamplesPerBuffer);
		
		
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
		
		Thread audioProcessingThread = new Thread(new AudioProcessingThread());
		audioProcessingThread.start();
		
		
	
		
	
		
	}
	

	
	/**
	 * record and call pitch extraction
	 * */
	public class AudioProcessingThread implements Runnable{

		@Override
		public void run() {
			 // We're important...
		    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		    		
		    	int currWindowNUmber = 1;	
		     	
		    	    
		        // let record all samples before all gone
		    	//FIXME: this while  might not be true if num bytes read is differnt from mBufferSize
		    	 while ( (currWindowNUmber - 1) * mNumSamplesPerBuffer < Parameters.RECORDING_DURATION * mSampleRate) {
		    		 
			    	//record
					int currNumBytesRead = mRecorder.read(mCurrentBuffer, 0,  mBufferSize);
					
					Log.i(AudioProcessingThread.class.getName(),  " recorded wind " + currWindowNUmber + "with " + currNumBytesRead + "bytes"   );

					
					// extract pitch
					Thread pitchDetectThread = new Thread(new PitchDetectionThread(currNumBytesRead / 2, currWindowNUmber));
					pitchDetectThread.start();
					
					// update 
					currWindowNUmber++;
					
				}	
		    	 // trigger stop of recording process
		    	 mRecorder.stop();
		    	 mIsRecording = false;
		    	 mLastWindowNum = currWindowNUmber -1;
			
		
		}// end run
	
	} // end AudioProcessigThread
	
	/**
	 * pitch extraction for each time window in separate thread. 
	 * */
	public class PitchDetectionThread implements Runnable{
	
	//num bytes read
	int mNumBytesRead;
	//  which is current time window 
	int mCurrWindowNumber;
	
	public PitchDetectionThread(int numBytesRead, int currWindowNUmber){
		this.mNumBytesRead = numBytesRead;
		this.mCurrWindowNumber = currWindowNUmber;
	}
	
		@Override
		public void run() {
			
	    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

			// time only for DEBUG purpose
			Date timeBefore = new Date();
			
			AudioEvent audioEvent = new AudioEvent(mTarsosFormat, this.mNumBytesRead / (mTarsosFormat.getSampleSizeInBits() / 8) );
			audioEvent.setFloatBufferWithByteBuffer(mCurrentBuffer);

			
			mPitchExtractor.mPitchProcessor.process(audioEvent);
			
			Date timeAfter = new Date();
			Log.i(PitchDetectionThread.class.getName(),  " currwindow " + this.mCurrWindowNumber +  ": time to process pitch window: " + String.valueOf(timeAfter.getTime() - timeBefore.getTime() ) );
			
			// if last window is processed , set flag
			if ( this.mCurrWindowNumber == mLastWindowNum)
				mIsAllAudioProcessed = true;
			
		}}
	

}
