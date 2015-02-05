package org.usfirst.frc.team2374.robot;

public class Command2374 {
	int system, type;
	double distance, direction, speed;
	Command2374 hybrid1, hybrid2;
	public Command2374(int system, int type, double distance, double direction, double speed){
		this.system=system;
		this.type=type;
		this.distance=distance;
		this.direction=direction;
		this.speed=speed;
		hybrid1=hybrid2=null;
	}
	public Command2374(Command2374 c1, Command2374 c2){
		hybrid1=c1;
		hybrid2=c2;
		system=CommandManager.SYSTEM_OTHER;
		type=CommandManager.TYPE_HYBRID;
	}
}
