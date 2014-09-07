package bg.singmaster.backend;

import java.util.ArrayList;

import android.util.Log;
import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.pitch.PitchDetectionHandler;
import be.hogent.tarsos.dsp.pitch.PitchDetectionResult;
import be.hogent.tarsos.dsp.pitch.PitchProcessor;

/**
 * handles processing related to detection of pitch 
 * 
 * */
public class PitchExtractor implements PitchDetectionHandler {
	
	// external module for pitch extraction
	public PitchProcessor mPitchProcessor;
	
	// holder of curr pitch info
	DetectedPitch mCurrDetectedPitch;
	
	// holder of sequence of extracted pitch values
	public ArrayList<DetectedPitch> mDetectedPitchArray;
	
	
	/**
	 * constructor. which pitch detection algorithm
	 * */
	public PitchExtractor(int sampleRate, int bufferSizeInSamples){
		PitchProcessor.PitchEstimationAlgorithm alg = 
				PitchProcessor.PitchEstimationAlgorithm.AMDF;
		
//		PitchProcessor.PitchEstimationAlgorithm alg = 
//				PitchProcessor.PitchEstimationAlgorithm.FFT_YIN;
		
		
		
		mPitchProcessor = new PitchProcessor(alg, sampleRate, bufferSizeInSamples, this);
		
	}
	
	/***
	 * is called in be.hogent.tarsos.dsp.pitch.PitchProcessor.process()
	 * */
	public void handlePitch(final PitchDetectionResult pitchDetectionResult,
			AudioEvent audioEvent) {
		float currPitch = 0;
		double currTs = audioEvent.getTimeStamp();
		
		if (pitchDetectionResult.isPitched()) {
			currPitch = pitchDetectionResult.getPitch();
			
		}
	
		mCurrDetectedPitch = new DetectedPitch(currPitch, currTs );
		// array initialized on press of record button
		this.mDetectedPitchArray.add(mCurrDetectedPitch);
		
		Log.i("TAG", "pitch is " + currPitch);
		
		
	}
	
}
