package bg.singmaster.gui.util;

import bg.singmaster.backend.Parameters;
import bg.singmaster.backend.ParametersExercise;

public class GUIParameters {

	public static final int minTime = 0;
	public static final float maxTime = (float) Parameters.RECORDING_DURATION;
	
	// -1 to see if below reference tone
	public static final int minPitch = ParametersExercise.lowestNoteMIDI - 1;
	// HARD CODED: for one octave than lowest
	// +1 to see if above reference tone
	public static final int maxPitch = ParametersExercise.lowestNoteMIDI + 12 + 1;
	
}