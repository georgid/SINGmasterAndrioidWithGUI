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
	private int mBufferSize;
	
	
	private byte[] mCurrentBuffer;
	Queue<byte[]> mQueueAudio;
	
	private int mNumSamplesPerBuffer;
	public int numBytesPerSample;
	
	public int mSampleRate;
	
	public boolean mIsAllAudioProcessed = false;
	
	
	/*** @deprecated in seconds */
	public int mLastWindowNum = -1;
	
	
	
	
	/**
	 * constructor
	 * */
	public AudioProcessor(int sampleRate) {
		mSampleRate = sampleRate;
		
		// these are same
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		numBytesPerSample = 2;
		
		mNumSamplesPerBuffer  = 512;
		
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
		
		mPitchExtractor = new PitchExtractor(mSampleRate, mNumSamplesPerBuffer);
		
		
	}
	
	/** trigger recording
	 * 
	 */
	public void record(MainActivity ma) {
		// reinitialize output container
		this.mPitchExtractor.mDetectedPitchArray = new ArrayList<DetectedPitch>(); 
		
		
		 if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
		        
	    	  Log.e(AudioProcessor.class.getName(), "Unable to access the audio recording hardware - is your mic working?");

	        return;
	      }
		
		mRecorder.startRecording();
		// make sure audio queue is empty
		this.mQueueAudio = new LinkedList<byte[]>();
		
		// recording
		Thread audioRecordingThread = new Thread(new AudioRecordingThread());
		audioRecordingThread.start();
		
		// start pitch extraction
		Thread pitchExtractionThread = new Thread(new PitchExtractionThread(ma));
		pitchExtractionThread.start();
	}
	

	
	/**
	 * record and call pitch extraction
	 * */
	public class AudioRecordingThread implements Runnable{

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
		    	 while ( totalNumBytesRead < Parameters.RECORDING_DURATION * mSampleRate * numBytesPerSample) {
		    		 
		    		// put mCurrentBuffer in Queue
					mQueueAudio.add(mCurrentBuffer);
						
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

					
					// extract pitch
//					Thread pitchDetectThread = new Thread(new PitchExtractionThread(mPitchExtractor, currNumBytesRead / 2, totalNumBytesProcessed, currWindowNUmber));
//					pitchDetectThread.start();
//					
					// update 
					currWindowNUmber++;
					
				}	
		    	 // trigger stop of recording process
		    	 mRecorder.stop();
		    	 mLastWindowNum = currWindowNUmber -1;
		    	 
		
		}// end run
	
	} // end AudioProcessigThread
	

}
