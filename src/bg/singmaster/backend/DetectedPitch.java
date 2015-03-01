package bg.singmaster.backend;


/**
 * Holds detected pitch value
*/
public class DetectedPitch implements Comparable<DetectedPitch>{

	public float mPitchHz;
	// pitch Scale
	public double mMIDInumber;
	
	public double mTimeStamp; 
	
	public DetectedPitch(float pitchHz, double timeStamp){
		this.mPitchHz = pitchHz; 
		this.mTimeStamp = timeStamp;
	}
	
	public void setPitchScaleNumber(double pitchScaleNumber){
	this.mMIDInumber  = pitchScaleNumber;
	}

	@Override
	public int compareTo(DetectedPitch anotherPitch) {
//		 	if (this.mPitchScaleNumber < p2.getY()) return -1;
//	        if (p1.getY() > p2.getY()) return 1;
//	        return 0;
		
		return Double.compare(this.mMIDInumber, anotherPitch.mMIDInumber);
	}
}
