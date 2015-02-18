package bg.singmaster.backend;


/**
 * Holds b
*/
public class DetectedPitch {

	public float mPitchHz;
	// pitch Scale
	public double mPitchScaleNumber;
	
	public double mTimeStamp; 
	
	public DetectedPitch(float pitchHz, double timeStamp){
		this.mPitchHz = pitchHz; 
		this.mTimeStamp = timeStamp;
	}
	
	public void setPitchScaleNumber(double pitchScaleNumber){
	this.mPitchScaleNumber  = pitchScaleNumber;
	}
}
