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
	int portA = 2;
	int portB = 3;
	
	//motors
	Jaguar jag1;
	Jaguar jag2;
	
	//sensors
	Encoder encoder; //encoder
	
	//limit switch
	int switchport = 1; //PLEASE CHANGE TO ACTUAL PORT #
	DigitalInput limitSwitch = new DigitalInput(switchport);
	
	//Hall effect? in the middle
	
	//constants
	public static final double ENCODER_COUNTS_TO_FEET=0.01; //NEEDS TESTING
	public static final double ADJUSTMENT_SCALE=0.1;
	//for encoder
	public static final double TOP = 5; //NEEDS ACTUAL VALUE //for highest elevator can go
	public static final double BOTTOM = 0; //PLEASE VERIFY //for lowest elevator can go
	public static final double PICKUP_POSITION = 2.5; //NEEDS ACTUAL VALUE //for picking up a tote
	public static final double INTAKE_POSITION = 3; //NEEDS ACTUAL VALUE //for lifting stack above next tote to be added to stack
	
	//methods
	
	//constructor
	public Elevator(int port1, int port2){
		jag1 = new Jaguar(port1);
		jag2 = new Jaguar(port2);
		encoder = new Encoder(portA, portB);
	}
	
	//basic functions
	
	//positive = up?
	//negative = down?
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
     */
	
	//go highest possible
	//assumes you are not higher than upper limit
	public void goToTop(){
		this.set(0.5);
		while(!limitSwitch.get() || encoder.get()<TOP){
			
		}
		this.set(0);
	}
	
	//go lowest possible
	//ADD LIMIT SWITCH
	public void goToBottom(){
		if(encoder.get()>BOTTOM){
			this.set(-0.5);
			while(encoder.get()>BOTTOM){
				
			}
		}
		this.set(0);
		
	}
	
	//go to position from which to immediately pick up a tote
	public void goToPickupPosition(){
		//if above position go down
		if(encoder.get()>PICKUP_POSITION){
			this.set(-0.5);
			//go down until at position
			while(encoder.get()>PICKUP_POSITION){
				
			}
			this.set(0);
		}
		//if below position go up
		else{
			this.set(0.25);
			//go up until at position
			while(encoder.get()<PICKUP_POSITION){
				
			}
			this.set(0);
		}
	}
	
	//lift stack high enough so that you can drive into another tote
	public void goToIntakePosition(){
		//if above position go down
		if(encoder.get()>INTAKE_POSITION){
			this.set(-0.5);
			//go down until position
			while(encoder.get()>INTAKE_POSITION){
				
			}
		}
		//if below position go up
		else{
			this.set(0.5);
			//go up until position
			while(encoder.get()<INTAKE_POSITION){
				
			}
		}
		this.set(0);
	}
	
	
	
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
