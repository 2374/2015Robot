package org.usfirst.frc.team2374.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
    Joystick joystick;
    CommandManager commandManager;
    Drivetrain drivetrain;
    Elevator elevator;
    VisionProcessor vision;
    boolean buttonPressed;
    public Robot() {
        joystick=new Joystick(0);
        commandManager=new CommandManager();
        drivetrain=new Drivetrain();
        elevator=new Elevator(4,5);
        vision=new VisionProcessor();
     
    }
    
    public void autonomous(){
   
    	drivetrain.encoder.reset();
    	drivetrain.gyro.calibrate();
		commandManager.moveDistance(5, 0.5);//(distance, speed) with distance in feet
		
		followAllCommands();
    }
    
    	
    
    public void operatorControl() {
    	int count=0;
    	drivetrain.calibrateGyro();
    	
    	while(isOperatorControl() && isEnabled()){
    		if(commandManager.hasCommand()){
    			followNextCommand();
    			if(checkDriverInputs()){
    				elevator.set(0);
    				drivetrain.setMotors(0, 0);
    				commandManager.clearCommands();
    			}
    			
    		}
    		else{
    			commandManager.setReferenceFrame(drivetrain.getEncoderFeet(), drivetrain.gyro.getAngle());
    			if(joystick.getRawButton(6)){
    				double averageSpeed=(-joystick.getRawAxis(1)-joystick.getRawAxis(5))/2;
    				//drivetrain.preciseTank(averageSpeed, averageSpeed);
    				drivetrain.moveForwards(averageSpeed);
    				
    			}
    			else{
    				drivetrain.preciseTank(-joystick.getRawAxis(1), -joystick.getRawAxis(5));
    			}
    			
    			elevator.set(joystick.getRawAxis(3)-joystick.getRawAxis(2));//Ian's cool controller
    			
    			//turnToHeading((vision.getCenterX()-160)*fov/320);
    			
    			if(count%10==0){
            		//process the camera input into a vision report
            		VisionReport v=vision.processCamera();
            		if(v!=null){
            			SmartDashboard.putNumber("Horizontal Offset", v.horizontalOffset);
            			//if(joystick.getRawButton(5)){
            				//auto-align!
            			//	drivetrain.alignWithCrate(v);
            			//}
            			if(joystick.getRawButton(6) && !commandManager.hasCommand()
            					&& !checkDriverInputs()){
            				drivetrain.resetGyro();
            				commandManager.turnToCrate(v);
            			}
            		}
            	}
    			
    			//target cycling code
    			if(joystick.getRawButton(1)){
    				if(!buttonPressed){
    					buttonPressed=true;
    					vision.changeTargets(true);
    				}
    			}
    			else if(joystick.getRawButton(2)){
    				if(!buttonPressed){
    					buttonPressed=true;
    					vision.changeTargets(false);
    				}
    			}
    			else{
    				buttonPressed=false;
    			}
    		}
    		
    		SmartDashboard.putNumber("Gyro",drivetrain.gyro.getAngle());
        	SmartDashboard.putNumber("DriveEncoder", drivetrain.encoder.get());
        	SmartDashboard.putNumber("ElevatorEncoder", elevator.encoder.get());
        	SmartDashboard.putBoolean("LimitTop", elevator.limitTop.get());
        	SmartDashboard.putBoolean("LimitBottom", elevator.limitBottom.get());
        	count++;
    		Timer.delay(0.005);
    	}
    }
    
    public void followAllCommands(){
    	while(commandManager.hasCommand()){
			followNextCommand();
			Timer.delay(0.005);
		}
    }
    
    public void followNextCommand(){
    	Command2374 command=commandManager.getCommand();
		if(followCommand(command)){
			commandManager.removeCommand();
		}
    }
    
    public boolean followCommand(Command2374 command){
    	if(command.system==CommandManager.SYSTEM_DRIVE){
			return (drivetrain.followCommand(command));
		}
		if(command.system==CommandManager.SYSTEM_ELEVATOR){
			return (elevator.followCommand(command));
		}
		if(command.type==CommandManager.TYPE_HYBRID){
			return followCommand(command.c1) && followCommand(command.c2);
		}
		return true;
    }
    
    
    public boolean checkDriverInputs(){
    	//a simple routine that cycles through all the joystick's axes and sees if they're zeroed
    	for(int i=0; i<6; ++i){
    		if(Math.abs(joystick.getRawAxis(i))>0.1)return true;
    	}
    	return false;
    }

}
