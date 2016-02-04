package org.oastem.frc.control;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.TalonSRX;

public class TalonDriveSystem extends DriveSystem {
	//TALON_SRX's
	TalonSRX frontRightDrive;
    TalonSRX frontLeftDrive;
    TalonSRX backRightDrive;
    TalonSRX backLeftDrive;
    
	// Singleton design pattern: instance of this class.
    // Only one talon drive system is allowed per robot - 
    // if any class needs it, it can call the getInstance()
    // method to use it.
    private static TalonDriveSystem instance;
    
    public static TalonDriveSystem getInstance() {
        if (instance == null) {
        	
            instance = new TalonDriveSystem();
        }
        
        return instance;
    }
    
    public void initializeTalonDrive(int leftFront, int leftRear, int rightFront, int rightRear){
    	frontRightDrive = new TalonSRX(rightFront);
    	frontLeftDrive = new TalonSRX(leftFront);
    	backRightDrive = new TalonSRX(rightRear);
    	backLeftDrive = new TalonSRX(leftRear);
    	super.initializeDrive(leftFront, leftRear, rightFront, rightRear);
    }
    
    
}
