package org.usfirst.frc.team2374.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically it 
 * contains the code necessary to operate a robot with tank drive.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
    Joystick joystick;
    CommandManager commandManager;
    Drivetrain drivetrain;
    Elevator elevator;
    public Robot() {
        joystick=new Joystick(0);
        commandManager=new CommandManager();
        drivetrain=new Drivetrain();
        elevator=new Elevator(4,5);
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
    			drivetrain.preciseTank(-joystick.getRawAxis(1), -joystick.getRawAxis(5));
    			elevator.set(joystick.getRawAxis(3)-joystick.getRawAxis(2));//Ian's cool controller
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
