package org.usfirst.frc.team2374.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;


public class Elevator {
	
	//class variables
	//jaguar ports
	//int portr = 0;
	//int portl = 0;
	//encoder ports
	int portA = 2;
	int portB = 3;
	
	//motors
	Jaguar jag1;
	Jaguar jag2;
	
	//sensors
	Encoder encoder; //encoder
	//limit switch(ES?)
	//Hall effect? in the middle
	
	//constants
	public static final double ENCODER_COUNTS_TO_FEET=0.01; //NEEDS TESTING
	public static final double ADJUSTMENT_SCALE=0.1;
	//methods
	
	//constructor
	public Elevator(int port1, int port2){
		jag1 = new Jaguar(port1);
		jag2 = new Jaguar(port2);
		encoder = new Encoder(portA, portB);
	}
	
	//basic functions
	public void set(double speed){
		jag1.set(speed);
		jag2.set(speed);
	}
	
	public boolean followCommand(Command2374 command){
		double difference=command.distance-getElevatorPosition();
		
		if(Math.abs(difference)>0.3){
			double speed=difference*ADJUSTMENT_SCALE;//PID
			//adjust magnitude if the speed is too fast or slow
			if(Math.abs(speed)<0.1)speed=Math.signum(difference)*0.1;
			if(Math.abs(speed)>command.speed)speed=Math.signum(difference)*command.speed;
			set(speed);
		}
		else{
			set(0);
			return true;
		}
		
		return false;
	}
	
	public double getElevatorPosition(){
		return (double)encoder.get()*ENCODER_COUNTS_TO_FEET;
	}
	
    /*METHODS FOR AUTOMATION THAT WE SHOULD HAVE
     * Stops: bottom, one, two, top
     *public void goToTop() //top of tote at top of elevator
     *public void goToBottom() //for absolute lowest for elevator
     *public void goToPickupPosition() //lower limit for picking up crate
     *public void UpOneStop() //should default for stacking 
     *public void UpTwoStop() //we may or may not need this
     *public void DownOneStop() //from top to position two or equivalent distance
     *public void DownTwoStop() //from top to position one or equivalent distance
     *
     *
     * 
     */
	
	//   methods/capabilities we need
	/*
	 * go UP 
	 *  basic - check
	 * 	first stop 
	 * 	second stop
	 *  third stop (etc)
	 *  TOP
	 * go DOWN 
	 *  basic - check
	 * 	...third stop
	 *  second stop
	 *  first stop
	 *  BOTTOM
	 * STOP - check
	 * 
	 */
	

}
