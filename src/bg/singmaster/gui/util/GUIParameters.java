package bg.singmaster.gui.util;

import bg.singmaster.backend.Parameters;
import bg.singmaster.backend.ParametersExercise;

public class GUIParameters {

	public static final int minTime = 0;
	public static final float maxTime = (float) Parameters.RECORDING_DURATION;
	
	// -1 semitone to see if below reference tone
	public static final int minCutOffMIDInumber = ParametersExercise.noteMIDIs[0] - 1;
	// HARD CODED: for one octave than lowest
	// +1 semitone to see if above reference tone
	public static final int maxCutOffMIDInumber = ParametersExercise.noteMIDIs[0] + 12 + 1;
	
}