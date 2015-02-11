package org.usfirst.frc.team2374.robot;

public class Command2374 {
	int system, type;
	double distance, direction, speed;
	Command2374 c1, c2; //only used for hybrid commands; null otherwise 
	public Command2374(int system, int type, double distance, double direction, double speed){
		this.system=system;
		this.type=type;
		this.distance=distance; //really means position, so an absolute location; need not calculate
		this.direction=direction; //angle; only used by drivetrain
		this.speed=speed; //MUST be positive (we do not mean vectors) 
		c1=c2=null;
	}
	//this is used to create a hybrid command by combining 2 commands into 1; allows for simultaneous execution
	public Command2374(Command2374 command1, Command2374 command2){
		c1=command1;
		c2=command2;
		system=CommandManager.SYSTEM_OTHER;
		type=CommandManager.TYPE_HYBRID;
	}
}