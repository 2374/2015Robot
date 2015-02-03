package org.usfirst.frc.team2374.robot;

public class Command2374 { 
	int system, type;
	double distance, direction, speed;
	public Command2374(int system, int type, double distance, double direction, double speed){
		this.system=system;
		this.type=type;
		this.distance=distance;
		this.direction=direction;
		this.speed=speed;
	}
}
