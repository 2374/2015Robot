package org.usfirst.frc.team2374.robot;

import edu.wpi.first.wpilibj.Talon;

public class SafeTalon extends Thread{
	Talon talon;
	long msUpdateRate;
	public SafeTalon(int port){
		super();
		talon=new Talon(port);
		this.start();
	}
	public void run(){
		
	}
}

