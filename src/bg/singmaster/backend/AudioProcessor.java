package bg.singmaster.backend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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
import android.view.View;

import java.util.Queue;
import java.util.LinkedList;

import com.kamengoranchev.singmaster.MainActivity;

/***
 * 
 * records sound from mic
 * field pitchExtractor from TarsosDSP
*/
public class AudioProcessor {

	// 	record form mic Logic 
	public AudioRecord mRecorder;

	public be.hogent.tarsos.dsp.AudioFormat mTarsosFormat;
	public PitchExtractor mPitchExtractor; 

	
	
	// in bytes
	public int mBufferSize;
	
	
	private byte[] mCurrentBuffer;
	

	
	private int mNumSamplesPerBuffer;
	public int numBytesPerSample;
	
	public int mSampleRate;
	
	public boolean mIsAllAudioProcessed = false;
	
	public WavRecorder mWavRecorder; 
	
	/*** @deprecated in seconds */
	public int mLastWindowNum = -1;
	
	
	ByteArrayOutputStream mAudioOutStream = null;
	Thread mPitchExtractionThread;
	
	/**
	 * constructor
	 * */
	public AudioProcessor(int sampleRate) {
		mSampleRate = sampleRate;
		
//		 these are same
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		numBytesPerSample = 2;
		
		// around 100 ms
		mNumSamplesPerBuffer = (int)Math.round(sampleRate * 0.1);
		
		// in bytes 
		int minBufferSize =  AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO, audioFormat);
		
		mBufferSize = mNumSamplesPerBuffer * numBytesPerSample;
		if (minBufferSize > mBufferSize){
			mBufferSize = minBufferSize;
			mNumSamplesPerBuffer = minBufferSize / numBytesPerSample;
		}	
		
	
		
		
		mCurrentBuffer = new byte[mBufferSize];
		
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				mSampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				mBufferSize);
		
		mTarsosFormat = new be.hogent.tarsos.dsp.AudioFormat(
				(float)mSampleRate, 16, 1, true, false);
		
		mPitchExtractor = new PitchExtractor(mSampleRate);
		
		
	}
	
	/** trigger recording
	 * 
	 */
	public void record(MainActivity ma) {
		// reinitialize output container
		this.mPitchExtractor.mDetectedPitchArray = new ArrayList<DetectedPitch>(); 
		this.mAudioOutStream = new ByteArrayOutputStream();
		
		 if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
		        
	    	  Log.e(AudioProcessor.class.getName(), "Unable to access the audio recording hardware - is your mic working?");

	        return;
	      }
		
		
		 mRecorder.startRecording();
		
//		PipedInputStream audioReadStream = new PipedInputStream();
//		try {
//			mAudioWriter.connect(audioReadStream);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		// recording
		Thread audioRecordingThread = new Thread(new AudioRecordingThread(ma));
		audioRecordingThread.start();
		
		
	}
	

	
	/**
	 * record and call pitch extraction
	 * */
	public class AudioRecordingThread implements Runnable{
		
		MainActivity mMainActi;
		public AudioRecordingThread(	MainActivity ma){
			mMainActi = ma;
		}
		@Override
		public void run() {
			 // We're important...
		    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		    	
		    	
		    	int currWindowNUmber = 0;	// for DEBUG
		    	int totalNumBytesRead = 0;
		     	

		    	//record
				int currNumBytesRead = mRecorder.read(mCurrentBuffer, 0,  mBufferSize);
				totalNumBytesRead += currNumBytesRead;

		        // let record all samples
				
				// DEBUG: date: 
				Date timeBefore = new Date();
		    	 while ( totalNumBytesRead < Parameters.RECORDING_DURATION * mSampleRate * numBytesPerSample) {
		    		
		    		mAudioOutStream.write(mCurrentBuffer,0, currNumBytesRead); 
		  
		    		//record next buffer
					currNumBytesRead = mRecorder.read(mCurrentBuffer, 0,  mBufferSize);
					totalNumBytesRead += currNumBytesRead;

					
					// DEBUG
					if (currNumBytesRead != mCurrentBuffer.length) {
							System.out.println(currNumBytesRead);
							System.out.println(" but ");
							System.out.println(mCurrentBuffer.length);
					}
					
					
					Log.i(AudioRecordingThread.class.getName(),  " recorded wind " + currWindowNUmber + "with " + currNumBytesRead + "bytes"   );
				
					// update . not needed
					currWindowNUmber++;
					
				}	
		    	 
		    	 Date timeAfter = new Date();
					Log.i(AudioProcessor.class.getName(),  " recordering time: " + String.valueOf(timeAfter.getTime() - timeBefore.getTime() ) );

		    	 
		    	 // trigger stop of recording process
		    	 mRecorder.stop();
		    	 mLastWindowNum = currWindowNUmber -1;
		    	
		    	 // inform user that pitch processing starts
		    	 this.mMainActi.runOnUiThread(new Runnable() {
		 	        @Override
		 	        public void run() {
		 	        		mMainActi.mProcTextView.setVisibility(View.VISIBLE);
		 	        }
		 	    });
		
		    	 // once recorded audio, start pitch extraction
		    	 mPitchExtractionThread = new Thread(new PitchExtractionThread(mMainActi, mAudioOutStream));
		    	 mPitchExtractionThread.start();
		    	 
		
		}// end run
	
	} // end AudioProcessigThread
	

}
