package org.usfirst.frc.team2374.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;


public class Elevator {
	
	//CLASS VARIABLES
	
	//jaguar ports
	//motors
	Jaguar jag1;
	Jaguar jag2;
	int portA = 0;
	int portB = 1;
	
	//limit switches
	DigitalInput limitBottom, limitTop;
	int limitBottomPort = 5;
	int limitTopPort=4;
	
	//encoder
	Encoder encoder; 
	
	
	public static final double FEET_PER_ENCODER_COUNT=2.5/6000;
	
	public static final double ADJUSTMENT_SCALE=0.1; //for PID
	
	//Preset Positions (in feet from bottom of elevator) PLEASE VERIFY THROUGH TESTING!!!!!!!!!
	public static final double BOTTOM = 0; //lowest elevator can go
	public static final double PICKUP_POSITION = 0.5; //position elevator should be to in order to pick up crate by going up
	public static final double INTAKE_POSITION = 1; //position to which to lift totes in order to drive into another w/out toppling already existing stack
	public static final double TOP = 3; //highest elevator can go
	
	//METHODS
	
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
