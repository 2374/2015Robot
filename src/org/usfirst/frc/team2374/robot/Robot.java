package org.usfirst.frc.team2374.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

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
    	
    }
    
    public void operatorControl() {
    	
    	while(isOperatorControl() && isEnabled()){
    		if(commandManager.hasCommand()){
    			Command2374 command=commandManager.getCommand();
    			if(command.system==CommandManager.SYSTEM_DRIVE){
    				if(drivetrain.followCommand(command)){
    					commandManager.removeCommand();
    				}
    			}
    			if(command.system==CommandManager.SYSTEM_ELEVATOR){
    				if(elevator.followCommand(command)){
    					commandManager.removeCommand();
    				}
    			}
    			if(checkDriverInputs()){
    				elevator.set(0);
    				drivetrain.setMotors(0, 0);
    				commandManager.clearCommands();
    			}
    			
    		}
    		else{
    			//reset command manager
    			commandManager.setReferenceFrame(drivetrain.getEncoderFeet(), drivetrain.gyro.getAngle());
    			
    			//drivetrain
    			drivetrain.preciseTank(-joystick.getRawAxis(1), -joystick.getRawAxis(5));
    			//elevator
    			elevator.set(joystick.getRawAxis(3)-joystick.getRawAxis(2));//Ian's cool controller; //manual
    			//preset positions NON-command based
    			
    			//preset positions COMMAND-based
    			
    			//target cycling code
    			if(joystick.getRawButton(5)){
    				if(!buttonPressed){
    					buttonPressed=true;
    					vision.changeTargets(true);
    				}
    			}
    			else if(joystick.getRawButton(6)){
    				if(!buttonPressed){
    					buttonPressed=true;
    					vision.changeTargets(false);
    				}
    			}
    			else{
    				buttonPressed=false;
    			}
    			//end target cycling code
    		}
    		Timer.delay(0.005);
    	}
    }
    
    public boolean checkDriverInputs(){
    	//a simple routine that cycles through all the joystick's axes and sees if they're zeroed
    	for(int i=0; i<6; ++i){
    		if(Math.abs(joystick.getRawAxis(i))>0.2)return true;
    	}
    	return false;
    }

}
