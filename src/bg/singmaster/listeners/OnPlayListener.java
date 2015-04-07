package bg.singmaster.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import bg.singmaster.backend.DetectedPitch;
import bg.singmaster.backend.NoteSynthesisThread;
import bg.singmaster.backend.Parameters;
import bg.singmaster.backend.ParametersExercise;
import bg.singmaster.backend.VoicePlaybackThread;
import bg.singmaster.gui.MainActivity;
import bg.singmaster.gui.MessageDialog;
import bg.singmaster.gui.Messages;
import bg.singmaster.gui.util.GUIParameters;
import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.util.Log;
import android.view.*;

public class OnPlayListener  implements View.OnClickListener {

	
	
	MainActivity mMainActivity;
	float [] mPitchSeriesTimes;
	float [] mPitchSeriesMIDIs;
	int mMaxWidthGraphView = -1;
	
	/**
	 * workaround timer
	 * */
	Timer mPlayBackTimer;
	
	public OnPlayListener(MainActivity mainActivity){
	this.mMainActivity = mainActivity;
	this.mPlayBackTimer = new Timer();
	
	}
	
	public void setMaxWidthGraphView(int maxWidthGraphView){
		mMaxWidthGraphView = maxWidthGraphView;
	}
	
	@Override
	public void onClick(View v) {
		
			
		 boolean isPitchSeriesInVisibleInterval = visualizePitchContour();
		 
		 //if not visible pitch, dont play sound
		 if (!isPitchSeriesInVisibleInterval) {
				return;
			} 
		 
		 // This timer has no functionality as timer, but used to force the update of grid to show before playback thread.
		 mPlayBackTimer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                
	            	// play audio 
	        		VoicePlaybackThread vpt = new VoicePlaybackThread(mMainActivity.mAudioProcessor);
	        		vpt.run();
	            }
	        }, 0);
		 
		 
	
	}

	public boolean visualizePitchContour() {
				ArrayList<DetectedPitch> pitchSeries = mMainActivity.mAudioProcessor.mPitchExtractor.mDetectedPitchArray;
//				ArrayList<DetectedPitch> pitchSeries = createTestPitchSeries();
//				ArrayList<DetectedPitch> pitchSeries = createTestPitchSeriesOnePitch();
				
				
				boolean isPitchSeriesInVisibleInterval; 
				isPitchSeriesInVisibleInterval = checkDetectedPitch(pitchSeries);
				if (!isPitchSeriesInVisibleInterval)
					return isPitchSeriesInVisibleInterval;
				
				// prepare pitch data for screen dimensions
				pitchSeries2graphData(pitchSeries);
				 
				// load pitch data in graph view
				float[][] data1 = {mPitchSeriesTimes, mPitchSeriesMIDIs };
				
				if (mMaxWidthGraphView == -1){
					Log.e(OnPlayListener.class.getName(), "maxWidthGraphView not set. Make sure it is set on press of record");
					System.exit(1);
					
				}
			        // The first dataset must be input into the graph using setData to replace the placeholder data already there
				 mMainActivity. mVoiceGraph.setData(new float[][][]{data1},  0, mMaxWidthGraphView, GUIParameters.minCutOffMIDInumber, GUIParameters.maxCutOffMIDInumber);
				
					
				  this.mMainActivity.runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				        
							mMainActivity. mVoiceGraph.setVisibility(View.VISIBLE);		
				            
				        }
				    });
				  return isPitchSeriesInVisibleInterval;
	}

	/**
	 * check if detected pitch is outside of range for exercise, if it is pitched at all.
	 * Show corresponding message dialogue
	 * */
	private boolean checkDetectedPitch(ArrayList<DetectedPitch> pitchSeries){
		
		boolean isPitchSeriesInVisibleInterval = true;
		// 3 cases
		if (pitchSeries == null){
			createDialog(Messages.NoRecordedVoiceMessage);
			isPitchSeriesInVisibleInterval = false;
			}
		// if pitch size is 1, graphView breaks
		else if (pitchSeries.size() == 0 || pitchSeries.size() == 1 ){
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
	
	@TargetApi(11)
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
	
	
	public ArrayList<DetectedPitch>  createTestPitchSeriesOnePitch(){
		ArrayList<DetectedPitch> pistSeries = new ArrayList<DetectedPitch>();
		
		double currTs = 0.5;
		DetectedPitch detPitch= new DetectedPitch(8.0f, currTs);
		detPitch.setPitchScaleNumber(64.0);
		pistSeries.add(detPitch);	
		
		
		return pistSeries;

		
	}

}
