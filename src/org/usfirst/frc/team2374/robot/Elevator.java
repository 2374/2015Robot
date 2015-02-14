package org.usfirst.frc.team2374.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;


public class Elevator {
	
	//class variables
	//jaguar ports
	//int portr = 0;
	//int portl = 0;
	//encoder ports
	int portA = 0;
	int portB = 1;
	
	//limitPorts
	DigitalInput limitBottom, limitTop;
	int limitBottomPort = 5;
	int limitTopPort=4;
	//motors
	Jaguar jag1;
	Jaguar jag2;
	
	//sensors
	Encoder encoder; //encoder
	//limit switch(ES?)
	//Hall effect? in the middle
	
	public static final double FEET_PER_ENCODER_COUNT=4./6000.;
	
	public static final double ADJUSTMENT_SCALE=20;
	//methods
	
	//constructor
	public Elevator(int port1, int port2){
		jag1 = new Jaguar(port1);
		jag2 = new Jaguar(port2);
		encoder = new Encoder(portA, portB);
		limitBottom=new DigitalInput(limitBottomPort);
		limitTop=new DigitalInput(limitTopPort);
	}
	
	//basic functions
	public void set(double speed){
		if(speed>0 && limitBottom.get()){
			encoder.reset();
			set(0);
			return;
		}
		if(speed<0 && limitTop.get()){
			set(0);
			return;
		}
		jag1.set(speed);
		jag2.set(speed);
	}
	
	public boolean followCommand(Command2374 command){
		double difference=command.distance-getElevatorPosition();
		if(command.distance==0){
			set(command.speed);
			return limitBottom.get();
		}
		if(Math.abs(difference)>0.2){
			double speed=difference*ADJUSTMENT_SCALE;//PID
			//adjust magnitude if the speed is too fast or slow
			if(Math.abs(speed)<0.5)speed=Math.signum(difference)*0.5;
			if(Math.abs(speed)>command.speed)speed=Math.signum(difference)*command.speed;
			set(-speed);
		}
		else{
			set(0);
			return true;
		}
		
		return false;
	}
	
	public double getElevatorPosition(){
		return (double)encoder.get()*FEET_PER_ENCODER_COUNT;
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
