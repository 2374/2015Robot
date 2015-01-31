package org.usfirst.frc.team2374.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Talon;

public class Drivetrain {
	
	static final double ENCODER_COUNTS_TO_FEET=0.05;//for prototype robot
	
	static final double ANGULAR_ADJUSTMENT_SCALE=0.02;//motor speed per degree
	static final double ANGULAR_ADJUSTMENT_MAX=0.5;//maximum angle speed
	
	static final double AUTO_SPEED_SCALE=0.3;//motor speed per foot
	
	Talon l1, l2, r1, r2;//jaguars, pretty normal
	CalibratedGyro gyro;//sensors
	Encoder encoder;
	int state;
	
	double targetHeading, targetDistance; //used for automatic movement
	
	public Drivetrain(){
		l1=new Talon(0);//ports, to be changed later
		l2=new Talon(1);
		r1=new Talon(2);
		r2=new Talon(3);
		
		gyro=new CalibratedGyro(0);//more ports :P
		
		encoder=new Encoder(1,2);
		
		targetHeading=0;
	}
	
	public boolean followCommand(Command2374 command){
		
		//computes the difference between the target angle/position and the current angle/position
		double angleDifference=command.direction-gyro.getAngle();
		double posDifference=command.distance-getEncoderFeet();
		
		//these values will eventually be what the motors are set to
		double speed=0;
		double turnSpeed=0;
		//angular adjustment, basic PID algorithm (P only)
		if(Math.abs(angleDifference)>3){
			turnSpeed=angleDifference*ANGULAR_ADJUSTMENT_SCALE;
			
			//scales for maximum and minimum values
			if(Math.abs(turnSpeed)>ANGULAR_ADJUSTMENT_MAX)turnSpeed=ANGULAR_ADJUSTMENT_MAX*Math.signum(turnSpeed);
			//0.4 seems to be enough to overcome the carpet's friction
			if(Math.abs(turnSpeed)<0.4)turnSpeed=0.4*Math.signum(turnSpeed);
		}
		//same algorithm for position adjustment
		if(Math.abs(posDifference)>0.5){
			speed=posDifference*AUTO_SPEED_SCALE;
			
			if(Math.abs(speed)>command.speed)speed=command.speed*Math.signum(speed);
			//0.3 also seems sufficient
			if(Math.abs(speed)<0.3)speed=0.3*Math.signum(speed);
		}
		
		//are we in position?
		if(Math.abs(angleDifference)<=3 && Math.abs(posDifference)<=0.5){
			//stop
			setMotors(0,0);
			//we followed the command, return confirmation
			return true;
		}
		setMotors(speed+turnSpeed,speed-turnSpeed);
		
		return false;//we still have more commands to follow
	}
	
	public double getEncoderFeet(){
		//returns the distance traveled, in feet
		return (double)getEncoderAdjusted()*ENCODER_COUNTS_TO_FEET;
	}
	
	public int getEncoderAdjusted(){
		//adjusts the encoder's values by the angle of the gyroscope
		//turning the robot in place changes the encoder's value, making this necessary
		return encoder.get()+(int)(gyro.getAngle()/6);
	}
	
	public void preciseTank(double lspeed, double rspeed){
		//an experimental algorithm to make tank drive as good at going straight/turning in place as arcade drive
		
		//converts left/right values into forwards/turn values
		double forwards=(lspeed+rspeed)/2;
		double turn=(lspeed-rspeed)/2;
		
		//scales those values
		forwards=quadraticScale(forwards);
		turn=deadbandScale(turn);
		
		//turns them back into left/right, sets motors
		setMotors(forwards+turn,forwards-turn);
	}
	
	double quadraticScale(double value){
		//scales the value quadratically, good for driving
		return value*Math.abs(value);
	}
	double deadbandScale(double value){
		//scales the value according to a deadband
		//Ian thinks this is better for rotation
		double deadband=0.1;
		if(value>deadband)return (value-deadband)/(1-deadband);
		else if(value<-deadband)return (value+deadband)/(1-deadband);
		else return 0;
	}
	
	public void setMotors(double lspeed, double rspeed){
		double ls2=lspeed;
		double rs2=rspeed;
		//normalize speeds that are too high
		if(Math.abs(ls2)>1){
			rs2/=Math.abs(ls2);
			ls2/=Math.abs(ls2);
		}
		if(Math.abs(rs2)>1){
			ls2/=Math.abs(rs2);
			rs2/=Math.abs(rs2);
		}
		//set the motors as usual
		l1.set(-ls2);
		l2.set(-ls2);
		r1.set(rs2);
		r2.set(rs2);
	}
	public void resetGyro(){
		gyro.reset();
	}
}
