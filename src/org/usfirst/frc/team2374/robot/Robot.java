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
    	drivetrain.calibrateGyro();
		//commandManager.moveDistance(5, 0.5);//(distance, speed) with distance in feet
    	commandManager.moveElevator(1);
    	commandManager.moveElevator(0);
    	commandManager.moveElevator(2);
    	commandManager.moveElevator(0);
    	
    	followAllCommands();
    }
    
    public void operatorControl() {
    	int count=0;
    	double speedMultiplier=1;
    	drivetrain.calibrateGyro();
    	
    	while(isOperatorControl() && isEnabled()){
    		if(commandManager.hasCommand()){
    			followNextCommand();
    			if(checkDriverInputs()){
    				elevator.set(0);
    				drivetrain.setMotorsQuadratic(0, 0);
    				commandManager.clearCommands();
    			}
    			
    		}
    		else{
    			commandManager.setReferenceFrame(drivetrain.getEncoderFeet(), drivetrain.gyro.getAngle());
    			if(joystick.getPOV()==0)speedMultiplier=1;
    			if(joystick.getPOV()==90)speedMultiplier=0.5;
    			if(joystick.getPOV()==180)speedMultiplier=0.25;
    			if(joystick.getRawButton(6)){
    				double averageSpeed=(-joystick.getRawAxis(1)-joystick.getRawAxis(5))/2;
    				//drivetrain.preciseTank(averageSpeed, averageSpeed);
    				drivetrain.moveForwards(averageSpeed*speedMultiplier);
    				
    			}
    			else{
    				drivetrain.setMotorsQuadratic(-joystick.getRawAxis(1)*speedMultiplier, -joystick.getRawAxis(5)*speedMultiplier);
    			}
    			
    			elevator.set(joystick.getRawAxis(2)-joystick.getRawAxis(3));//Ian's cool controller
    			
    			//turnToHeading((vision.getCenterX()-160)*fov/320);
    			
    			if((joystick.getRawButton(5) || joystick.getRawButton(6)) 
    					&& !checkDriverInputs() && count%3==0){
            		//process the camera input into a vision report
            		VisionReport v=vision.processCamera();
            		if(v!=null){
            			SmartDashboard.putNumber("Horizontal Offset", v.horizontalOffset);
            			if(joystick.getRawButton(5)){
            				//auto-align!
            				drivetrain.resetGyro();
            				commandManager.alignWithCrate(v);
            			}
            			else if(joystick.getRawButton(6)){
            				drivetrain.resetGyro();
            				commandManager.turnToCrate(v);
            			}
            		}
            	}
    			vision.debug=joystick.getRawButton(3);
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
        	SmartDashboard.putNumber("Elevator", elevator.getElevatorPosition());
        	SmartDashboard.putBoolean("LimitTop", elevator.limitTop.get());
        	SmartDashboard.putBoolean("LimitBottom", elevator.limitBottom.get());
        	SmartDashboard.putNumber("Commands", commandManager.commandList.size());
        	
        	SmartDashboard.putBoolean("YellowTotes", vision.targetCycler==0);
        	SmartDashboard.putBoolean("GrayTotes", vision.targetCycler==1);
        	SmartDashboard.putBoolean("GreenBins", vision.targetCycler==2);
        	count++;
    		Timer.delay(0.005);
    	}
    }
    public void pickUp(){
    	commandManager.moveElevator(0);
		commandManager.moveElevator(1);
    }
    public void pickUpAndMoveForwards(){
    	double distBetweenTotes=10;
    	commandManager.moveAndElevate(distBetweenTotes/2, 0.5, 0);
    	commandManager.moveAndElevate(distBetweenTotes/2, 0.8, 2);
    	
    }
    public void oneToteOneBinAutonomous(){
    	//this program picks up a bin, moves the robot, picks up a tote, then scores them
    	//
		pickUp();//pick the bin up
		
		//move to the tote's position
		commandManager.moveDistance(-4, 0.5);
		commandManager.turnToHeading(30, 0.5);
		commandManager.moveDistance(-4, 0.5);
		commandManager.turnToHeading(0,0.5);
		commandManager.moveDistance(8, 0.5);
		
		pickUp();//pick up the tote
		
		commandManager.turnToHeading(90, 0.5);//move to the scoring position
		commandManager.moveDistance(16, 0.8);
		
		commandManager.moveElevator(0);//score and retreat
		commandManager.moveDistance(-4,0.5);
		
		followAllCommands();
    }
    public void xTotesOneBinAutonomous(int totes){
    	//this program picks up a bin, moves the robot, picks up a tote, then scores them
    	//
		pickUp();//pick the bin up
		
		//move to the tote's position
		commandManager.moveDistance(-4, 0.5);
		commandManager.turnToHeading(30, 0.5);
		commandManager.moveDistance(-4, 0.5);
		commandManager.turnToHeading(0,0.5);
		commandManager.moveDistance(8, 1);
		
		
		for(int i=0; i<totes-1; ++i){
			pickUpAndMoveForwards();//pick up ALL the totes
		}
		pickUp();
		
		commandManager.turnToHeading(90, 0.5); //move to the scoring position and score
		commandManager.moveAndElevate(16, 0.8,0);
		
		commandManager.moveDistance(-4,0.5); //retreat
		
		followAllCommands();
    }
    
    public void followAllCommands(){
    	while(commandManager.hasCommand()&&!checkDriverInputs()){
    		SmartDashboard.putNumber("Commands", commandManager.commandList.size());
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
    		if(Math.abs(joystick.getRawAxis(i))>0.2)return true;
    	}
    	return false;
    }
}
