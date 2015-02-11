package org.usfirst.frc.team2374.robot;

import java.util.ArrayList;

public class CommandManager {
	ArrayList<Command2374> commandList; //this will hold all of the current commands for later use
	double targetDistance,targetHeading;
	
	
	public CommandManager(){
		commandList=new ArrayList<Command2374>(); 
	}
	
	public void moveElevator(double targetHeight){ //height from bottom in feet
		commandList.add(new Command2374(SYSTEM_ELEVATOR,TYPE_MOVE,targetHeight,0,0.5));
	}
	
	public void moveDistance(double dist, double speed){
		targetDistance+=dist;
		commandList.add(new Command2374(SYSTEM_DRIVE,TYPE_MOVE,targetDistance,targetHeading,speed));
	}
	
	public void turnToHeading(double heading, double speed){
		targetHeading=heading;
		commandList.add(new Command2374(SYSTEM_DRIVE,TYPE_MOVE,targetDistance,targetHeading,speed));
	}
	public void alignWithCrate(VisionReport vision){
		turnToHeading(30*Math.signum(vision.horizontalOffset),0.5);
		
		//move twice the horizontal offset (since sin(30)=0.5)
		this.moveDistance(Math.max(Math.abs(vision.horizontalOffset)*2,0.5),0.5);
		
		//turn back to the original heading
		this.turnToHeading(0, 0.5);
	}
	public void turnToCrate(VisionReport vision){
		double fov=20;
		turnToHeading((vision.getCenterX()-160)*fov/320,0.5);
	}
	
	
	
	//to set starting point (as a reference for bearings)
	//call this method whenever starting new sequence because its how the robot 'knows where it started'
	public void setReferenceFrame(double distance, double heading){
		targetDistance=distance;
		targetHeading=heading;
	}
	
	//gets first command on list
	public Command2374 getCommand(){
		if(hasCommand()){
			return commandList.get(0);
		}
		return null;
	}
	
	public boolean hasCommand(){
		return commandList.size()>0;
	}
	
	//removes first command in commandList
	public void removeCommand(){
		if(hasCommand())commandList.remove(0);
	}
	
	public void clearCommands(){
		commandList.clear();
	}
	//constants for subsystem specification
	public static final int SYSTEM_DRIVE=0;
	public static final int SYSTEM_ELEVATOR=1;
	public static final int SYSTEM_OTHER=2;
	//constants for specifying command type
	public static final int TYPE_WAIT=0;
	public static final int TYPE_MOVE=1;
	public static final int TYPE_TRACK=2;
	public static final int TYPE_HYBRID=3;
}
