package org.oastem.frc.control;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class TalonDriveSystem extends DriveSystem {
	//TALON_SRX's
	private CANTalon frontRightDrive;
	private CANTalon frontLeftDrive;
	private CANTalon backRightDrive;
	private CANTalon backLeftDrive;
	private int encoderCodePerRev;
	private int wheelDiamter;
    
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
    
    public void initializeTalonDrive(int leftFront, int leftRear, int rightFront,
    								int rightRear, int pulsesPerRev, int wheelDiameter){
    	frontRightDrive = new CANTalon(rightFront);
    	frontLeftDrive = new CANTalon(leftFront);
    	backRightDrive = new CANTalon(rightRear);
    	backLeftDrive = new CANTalon(leftRear);
    	encoderCodePerRev = pulsesPerRev;
    	this.wheelDiamter = wheelDiameter;
    	initCan();
    	super.initializeDrive(leftFront, leftRear, rightFront, rightRear);
    }
    
    public void initializeTalonDrive(int left, int right, int pulsesPerRev, int wheelDiameter)
    {
    	frontRightDrive = null;
    	frontLeftDrive = null;
    	backRightDrive = new CANTalon(right);
    	backLeftDrive = new CANTalon(left);
    	encoderCodePerRev = pulsesPerRev;
    	this.wheelDiamter = wheelDiameter;
    	initCan();
    	super.initializeDrive(left, right);
    }
    
    private void initCan()
    {
    	TalonControlMode mode = TalonControlMode.Speed;
    	if(frontRightDrive != null)
    	{
    		frontRightDrive.changeControlMode(mode);
    		frontRightDrive.configEncoderCodesPerRev(encoderCodePerRev);
    		frontRightDrive.enable();
    	}
    	if (frontLeftDrive != null)
    	{    		
    		frontLeftDrive.changeControlMode(mode);
    		frontLeftDrive.configEncoderCodesPerRev(encoderCodePerRev);
   			frontLeftDrive.enable();
    	}
    	backRightDrive.changeControlMode(mode);
    	backRightDrive.configEncoderCodesPerRev(encoderCodePerRev);
    	backRightDrive.enable();
    	backLeftDrive.changeControlMode(mode);
    	backLeftDrive.configEncoderCodesPerRev(encoderCodePerRev);
    	backLeftDrive.enable();
    }
    
    public void speedTankDrive(double leftValuePerMin, double rightValuePerMin,
    							boolean isInInches)
    {
    	double leftRPM = leftValuePerMin;
    	double rightRPM = rightValuePerMin;
    	if (isInInches)
    	{
    		leftRPM /= wheelDiamter;
    		rightRPM /= wheelDiamter;
    	}
    	backLeftDrive.set(leftRPM);
    	if (frontLeftDrive != null)
    		frontLeftDrive.set(leftRPM);
    	backRightDrive.set(rightRPM);
    	if (frontRightDrive != null)
    		frontRightDrive.set(rightRPM);
    	
    }

    
    
}
