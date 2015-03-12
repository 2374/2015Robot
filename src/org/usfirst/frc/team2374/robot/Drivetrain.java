package org.usfirst.frc.team2374.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;

public class Drivetrain {
	
	static final double FEET_PER_ENCODER_COUNT=1./340.;//for real robot
	
	static final double ANGULAR_ADJUSTMENT_SCALE=0.025;//motor speed per degree
	static final double ANGULAR_ADJUSTMENT_MAX=0.7;//maximum angle speed
	static final double ANGULAR_ADJUSTMENT_DERIVATIVE=-0.3;//to slow down rotation to a reasonable amount
	//the above variable MUST be negative!!!
	static final double AUTO_SPEED_SCALE=0.3;//motor speed per foot
	
	double prevAngle;
	
	Talon l1, l2, r1, r2;//talons, pretty normal
	CalibratedGyro gyro;//sensors
	Encoder encoder;
	int state;
	int counter;
	
	double targetHeading, targetDistance; //used for automatic movement
	
	public Drivetrain(){
		l1=new Talon(0);//ports, to be changed later
		l2=new Talon(1);
		r1=new Talon(2);
		r2=new Talon(3);
		
		gyro=new CalibratedGyro(0);//more ports :P
		
		encoder=new Encoder(2,3);
		
		targetHeading=0;
	}
	
	public void moveForwards(double speed){
		
		//computes the difference between the target angle/position and the current angle/position
		double angleDifference=-gyro.getAngle();
		
		//these values will eventually be what the motors are set to
		double turnSpeed=0;
		//angular adjustment, basic PID algorithm (P only)
		if(Math.abs(angleDifference)>2 || true){
			turnSpeed=angleDifference*ANGULAR_ADJUSTMENT_SCALE;
			
			//scales for maximum and minimum values
			if(Math.abs(turnSpeed)>ANGULAR_ADJUSTMENT_MAX)turnSpeed=ANGULAR_ADJUSTMENT_MAX*Math.signum(turnSpeed);
			//don't want a minimum turn value :(
			//if(Math.abs(turnSpeed)<0.4)turnSpeed=0.4*Math.signum(turnSpeed);
		}
		//same algorithm for position adjustment
		
		//are we in position?
		setMotorsQuadratic(speed+turnSpeed,speed-turnSpeed);
	}
	
	public boolean followCommand(Command2374 command){
		
		//computes the difference between the target angle/position and the current angle/position
		double angleDifference=command.direction-gyro.getAngle();
		double deltaAngle=gyro.getAngle()-prevAngle;
		prevAngle=gyro.getAngle();
		double posDifference=command.distance-getEncoderFeet();
		
		//these values will eventually be what the motors are set to
		double speed=0;
		double turnSpeed=0;
		//angular adjustment, basic PID algorithm (P only)
		if(Math.abs(angleDifference)>3){
			turnSpeed=angleDifference*ANGULAR_ADJUSTMENT_SCALE;
			
			//0.2 seems to be enough to overcome the carpet's friction
			if(Math.abs(turnSpeed)<0.2)turnSpeed=0.2*Math.signum(turnSpeed);
			
			turnSpeed+=ANGULAR_ADJUSTMENT_DERIVATIVE*deltaAngle;
			
			//scales for maximum values
			if(Math.abs(turnSpeed)>ANGULAR_ADJUSTMENT_MAX)turnSpeed=ANGULAR_ADJUSTMENT_MAX*Math.signum(turnSpeed);
		}
		//same algorithm for position adjustment
		if(Math.abs(posDifference)>0.2){
			speed=posDifference*AUTO_SPEED_SCALE;
			
			if(Math.abs(speed)>command.speed)speed=command.speed*Math.signum(speed);
			//0.3 also seems sufficient
			if(Math.abs(speed)<0.2)speed=0.2*Math.signum(speed);
		}
		
		//are we in position?
		if(Math.abs(angleDifference)<=3 && Math.abs(posDifference)<=0.2){
			
			//stop
			setMotors(0,0);
			counter++;//the counter's to make sure the robot comes to a full stop
			return counter>1;
		}
		else{
			counter=0;
		}
		setMotors(speed+turnSpeed,speed-turnSpeed);
		
		return false;//we still have more commands to follow
	}
	
	public double getEncoderFeet(){
		//returns the distance traveled, in feet
		return (double)getEncoderAdjusted()*FEET_PER_ENCODER_COUNT;
	}
	
	public int getEncoderAdjusted(){
		//adjusts the encoder's values by the angle of the gyroscope
		//turning the robot in place changes the encoder's value, making this necessary
		return -encoder.get()-(int)(gyro.getAngle()*5);
	}
	
	public void preciseTank(double lspeed, double rspeed){
		//an experimental algorithm to make tank drive as good at going straight/turning in place as arcade drive
		double deadband=0.05;
		//converts left/right values into forwards/turn values
		double forwards=(lspeed+rspeed)/2;
		double turn=(lspeed-rspeed)/2;
		
		//scales those values
		forwards=deadbandScale(forwards,deadband);
		turn=deadbandScale(turn,deadband);
		
		//turns them back into left/right, sets motors
		setMotorsQuadratic(forwards+turn,forwards-turn);
		
	}
	
	double quadraticScale(double value){
		//scales the value quadratically, good for driving
		return value*Math.abs(value);
	}
	double deadbandScale(double value, double deadband){
		//scales the value according to a deadband
		//Ian thinks this is better for rotation
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
		l1.set(ls2);
		l2.set(ls2);
		r1.set(-rs2);
		r2.set(-rs2);
	}
	public void setMotorsQuadratic(double lspeed, double rspeed){
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
		l1.set(quadraticScale(ls2));
		l2.set(quadraticScale(ls2));
		r1.set(-quadraticScale(rs2));
		r2.set(-quadraticScale(rs2));
	}
	public void resetGyro(){
		gyro.reset();
		prevAngle=0;
	}
	public void calibrateGyro(){
		gyro.calibrate();
	}
}
