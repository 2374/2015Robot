package org.usfirst.frc.team2374.robot;

import java.util.ArrayList;

public class CommandManager {
	ArrayList<Command2374> commandList; // this will hold all of the current
										// commands for later use
	double targetDistance, targetHeading;

	public CommandManager() {
		commandList = new ArrayList<Command2374>();
	}

	public void moveElevator(double targetHeight) {
		commandList.add(new Command2374(SYSTEM_ELEVATOR, TYPE_MOVE,
				targetHeight, 0, 0.5));
	}

	public void moveDistance(double dist, double speed) {
		targetDistance += dist;
		commandList.add(new Command2374(SYSTEM_DRIVE, TYPE_MOVE,
				targetDistance, targetHeading, speed));
	}

	public void turnToHeading(double heading, double speed) {
		targetHeading = heading;
		commandList.add(new Command2374(SYSTEM_DRIVE, TYPE_MOVE,
				targetDistance, targetHeading, speed));
	}

	public void setReferenceFrame(double distance, double heading) {
		targetDistance = distance;
		targetHeading = heading;
	}

	public Command2374 getCommand() {
		if (hasCommand()) {
			return commandList.get(0);
		}
		return null;
	}

	public boolean hasCommand() {
		return commandList.size() > 0;
	}

	public void removeCommand() {
		if (hasCommand())
			commandList.remove(0);
	}

	public void clearCommands() {
		commandList.clear();
	}

	// constants for subsystem specification
	public static final int SYSTEM_DRIVE = 0;
	public static final int SYSTEM_ELEVATOR = 1;
	public static final int SYSTEM_OTHER = 2;
	// constants for specifying command type
	public static final int TYPE_WAIT = 0;
	public static final int TYPE_MOVE = 1;
	public static final int TYPE_TRACK = 2;
}