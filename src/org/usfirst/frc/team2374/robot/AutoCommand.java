package org.usfirst.frc.team2374.robot;

public class AutoCommand {
	int mode;
	int startPosition;
	int endPosition;
	public AutoCommand(int mode){
		this.mode=mode;
		startPosition=-1;
		endPosition=-1;
	}
	public AutoCommand(int mode, int start, int end){
		this.mode=mode;
		startPosition=start;
		endPosition=end;
	}
	public int getMode(){
		return mode;
	}
	
	public static final int MODE_NO_AUTONOMOUS=-1;
	public static final int MODE_FORWARDS_AUTONOMOUS=0;
	public static final int MODE_ONE_TOTE_AUTONOMOUS=1;
	public static final int MODE_TWO_TOTE_AUTONOMOUS=2;
	public static final int MODE_THREE_TOTE_AUTONOMOUS=3;
	public static final int MODE_ONE_TOTE_ONE_BIN_AUTONOMOUS=4;
	
}
