package org.usfirst.frc.team2374.robot;

public class Command2374 {
	int system, type;
	double distance, direction, speed;
	Command2374 c1, c2;
	public Command2374(int system, int type, double distance, double direction, double speed){
		this.system=system;
		this.type=type;
		this.distance=distance; //This is in feet
		this.direction=direction; 
		this.speed=speed;
		c1=c2=null;
	}
	public Command2374(Command2374 command1, Command2374 command2){
		c1=command1;
		c2=command2;
		system=CommandManager.SYSTEM_OTHER;
		type=CommandManager.TYPE_HYBRID;
	}
}