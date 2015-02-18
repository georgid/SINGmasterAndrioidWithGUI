package bg.singmaster.backend;

import java.util.ArrayList;

import android.util.Log;
import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.pitch.PitchDetectionHandler;
import be.hogent.tarsos.dsp.pitch.PitchDetectionResult;
import be.hogent.tarsos.dsp.pitch.PitchProcessor;

/**
 * wrapper. calls pitch extraction logic which is in PitchProcessor field
 * handles processing related to detection of pitch 
 * 
 * */
public class PitchExtractor implements PitchDetectionHandler {
	
	// external module for pitch extraction
	public PitchProcessor mPitchProcessor;
	
	
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
		// pitch in Hz
		float currPitchInHz = 0;
		
		double currTs = audioEvent.getTimeStamp();
		
		if (!pitchDetectionResult.isPitched() )
			return;
		
		currPitchInHz = pitchDetectionResult.getPitch();
		if (currPitchInHz == 0)
			return;
		
		DetectedPitch currDetectedPitch = new DetectedPitch(currPitchInHz, currTs );
		
		//convert to pitch scale 
		double tmpVal = currPitchInHz / 440.0;
			// log 2 (x) = log e (x ) /log e (2)
		double currPitchScaleNumber = 69 + 12 * (Math.log(tmpVal) / Math
				.log(2));
		currDetectedPitch.setPitchScaleNumber(currPitchScaleNumber);
		
		
		// array initialized on press of record button
		this.mDetectedPitchArray.add(currDetectedPitch);
		
		Log.d("TAG", "pitch is " + currPitchInHz);
		
		
		
		
	}
	
	

/***************************************************************************
	 * converts mDetectedPitchArray from frequency to pitch scale. Formula : p = 69 + 12*
	 * log2 (freq/440)
	 * @depracated. done immediately in handlePitch
	 */
	public void convertToPitchScale() {
		
		if (this.mDetectedPitchArray.size() == 0) {
			Log.e("convertToPitchScale", "PitchExtractor.mDetectedPitchArray is empty.");
			System.exit(1);
				}
		
		// loop through frequency time series
		for (DetectedPitch detPitch : this.mDetectedPitchArray) {
				double tmpVal = detPitch.mPitchHz / 440.0;
			// log 2 (x) = log e (x ) /log e (2)
			double currPitchScaleNumber = 69 + 12 * (Math.log(tmpVal) / Math
					.log(2));
			detPitch.setPitchScaleNumber(currPitchScaleNumber);
		}

	}
	
}
