package com.kamengoranchev.singmaster;

import java.util.ArrayList;
import java.util.Collections;

import bg.singmaster.backend.DetectedPitch;
import bg.singmaster.backend.NoteSynthesisThread;
import bg.singmaster.backend.Parameters;
import bg.singmaster.backend.ParametersExercise;
import bg.singmaster.gui.util.GUIParameters;
import android.app.DialogFragment;
import android.util.Log;
import android.view.*;

public class OnPlayListener  implements View.OnClickListener {

	
	
	MainActivity mMainActivity;
	float [] mPitchSeriesTimes;
	float [] mPitchSeriesMIDIs;
	
	
	public OnPlayListener(MainActivity mainActivity){
	this.mMainActivity = mainActivity;	
	
	}
	
	@Override
	public void onClick(View v) {
		
		
			
		ArrayList<DetectedPitch> pitchSeries = mMainActivity.mAudioProcessor.mPitchExtractor.mDetectedPitchArray;
//		ArrayList<DetectedPitch> pitchSeries = createTestPitchSeries();
		
		
		boolean isPitchSeriesInVisibleInterval; 
		isPitchSeriesInVisibleInterval = checkDetectedPitch(pitchSeries);
		if (!isPitchSeriesInVisibleInterval) {
			return;
		}
		// prepare pitch data for screen dimensions
		pitchSeries2graphData(pitchSeries);
		 
		// load pitch data in graph view
		float[][] data1 = {mPitchSeriesTimes, mPitchSeriesMIDIs };

	        // The first dataset must be input into the graph using setData to replace the placeholder data already there
		 mMainActivity. mVoiceGraph.setData(new float[][][]{data1},  GUIParameters.minTime, GUIParameters.maxTime, GUIParameters.minCutOffMIDInumber, GUIParameters.maxCutOffMIDInumber);

		
		mMainActivity. mVoiceGraph.setVisibility(View.VISIBLE);
		
		
		// play audio TODO

	}

	/**
	 * check if detected pitch is outside of range for exercise, if it is pitched at all.
	 * Show corresponding message dialogue
	 * */
	private boolean checkDetectedPitch(ArrayList<DetectedPitch> pitchSeries){
		
		boolean isPitchSeriesInVisibleInterval = true;
		// 3 cases
				if (pitchSeries.size() == 0){
					Log.e("TAG", "no pitch detected.");
					
					createDialog(Messages.NoPitchMessage);
					isPitchSeriesInVisibleInterval = false;
					}
				
				else if (Collections.max(pitchSeries).mMIDInumber < GUIParameters.minCutOffMIDInumber){
				
					createDialog(Messages.lowPitchMessage);
					isPitchSeriesInVisibleInterval = false;
				}
				else if (Collections.min(pitchSeries).mMIDInumber > GUIParameters.maxCutOffMIDInumber){
					
					isPitchSeriesInVisibleInterval = false;
					createDialog(Messages.highPichMessage);

				}
				
				return isPitchSeriesInVisibleInterval;
		
	}
	
	private void createDialog(String infoMsg){
		DialogFragment infoPitchDialog = new MessageDialog(infoMsg);
		infoPitchDialog.show(mMainActivity.getFragmentManager(), infoMsg);
	}
	
	/**
	 * turn pitches into x-array and y-array to be ready for visualization display
	 * */
	public void pitchSeries2graphData(ArrayList<DetectedPitch> pitchSeries){
		int sizePitchSeries = pitchSeries.size();
		
		this.mPitchSeriesTimes = new float[sizePitchSeries];
		this.mPitchSeriesMIDIs = new float[sizePitchSeries];
		
		for(int i = 0; i < sizePitchSeries; i++) {
			double currTs = pitchSeries.get(i).mTimeStamp;
			this.mPitchSeriesTimes[i] = (float)currTs;
			
			double currPitchMIDI = pitchSeries.get(i).mMIDInumber;
			this.mPitchSeriesMIDIs[i] = (float)currPitchMIDI;
			
		}
			
	}
	/**
	 * stub method: test pitch series to visualize from onClick. 
	 * number of pitches @param: totalNumDetectedPitch 
	 * is multiple of 3. generates 3 notes and repeats each totalNumDetectedPitch/3 times
	 * */
	public ArrayList<DetectedPitch>  createTestPitchSeries(){
		ArrayList<DetectedPitch> pistSeries = new ArrayList<DetectedPitch>();
		
		int totalNumDetectedPitch = 12;
		int i,j,k = 0;
		
		for (i=0;  i < totalNumDetectedPitch/3; i++){
			double currTs = (double) i /totalNumDetectedPitch * Parameters.RECORDING_DURATION;
			
		DetectedPitch detPitch= new DetectedPitch(8.0f, currTs);
		detPitch.setPitchScaleNumber(60.0);
		pistSeries.add(detPitch);	
		}
		
		for (j=0;  j < totalNumDetectedPitch/3 ; j++){
			double currTs = (double) (i + j) /totalNumDetectedPitch * Parameters.RECORDING_DURATION;

		DetectedPitch detPitch2= new DetectedPitch(68.0f, currTs);
		detPitch2.setPitchScaleNumber(64.0);
		pistSeries.add(detPitch2);
		}
		for (k=0;  k < totalNumDetectedPitch/3; k++){
			double currTs = (double) (i + j + k) /totalNumDetectedPitch * Parameters.RECORDING_DURATION;

			DetectedPitch detPitch3= new DetectedPitch(68.0f, currTs);
		detPitch3.setPitchScaleNumber(67.0);
		pistSeries.add(detPitch3);
		}
		
		DetectedPitch detPitch3= new DetectedPitch(68.0f, totalNumDetectedPitch * Parameters.RECORDING_DURATION);
	detPitch3.setPitchScaleNumber(67.0);
	pistSeries.add(detPitch3);
		
		return pistSeries;
		
	}

}
