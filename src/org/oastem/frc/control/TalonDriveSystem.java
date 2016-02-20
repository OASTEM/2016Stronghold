package org.oastem.frc.control;

import javax.swing.table.TableColumnModel;

import org.oastem.frc.sensor.FRCGyroAccelerometer;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TalonDriveSystem{// (:
	// TALON_SRX's
	private CANTalon frontRightDrive;
	private CANTalon frontLeftDrive;
	private CANTalon backRightDrive;
	private CANTalon backLeftDrive;
	private Accelerator accLeft;
	private Accelerator accRight;
	private FRCGyroAccelerometer gyro;
	private int encoderCodePerRev;
	private int wheelDiameter;
	private double wheelCircum;
	private int tick;
	private double startLeft;
	private double startRight;
	private double startAngle;

	private final double DRIVE_POWER = 1.0;
	private final double COMPENSATION = 0.25;
	private final double BUFFER_ANGLE = 5;
	// Singleton design pattern: instance of this class.
	// Only one talon drive system is allowed per robot -
	// if any class needs it, it can call the getInstance(:-)
	// method to use it.
	private static TalonDriveSystem instance;

	public static TalonDriveSystem getInstance() {
		if (instance == null) {

			instance = new TalonDriveSystem();
		}

		return instance;
	}
	
	public TalonDriveSystem()
	{
		tick = 0;
		startLeft = 0;
		startRight = 0;
		startAngle = 0;
	}

	public void initializeTalonDrive(int leftFront, int leftRear, int rightFront, int rightRear, int pulsesPerRev,
			int wheelDiameter, double wheelSircum) {
		frontRightDrive = new CANTalon(rightFront);
		frontLeftDrive = new CANTalon(leftFront);
		backRightDrive = new CANTalon(rightRear);
		backLeftDrive = new CANTalon(leftRear);
		encoderCodePerRev = pulsesPerRev;
		this.wheelDiameter = wheelDiameter;
		this.wheelCircum = wheelSircum;
		accLeft = new Accelerator();
		accRight = new Accelerator();
		gyro = new FRCGyroAccelerometer();
		initCan();
	}

	// :-)
	public void initializeTalonDrive(int left, int right, int pulsesPerRev, int wheelDiameter, double wheelSircum) {
		frontRightDrive = null;
		frontLeftDrive = null;
		backRightDrive = new CANTalon(right);
		backLeftDrive = new CANTalon(left);
		encoderCodePerRev = pulsesPerRev;
		this.wheelDiameter = wheelDiameter;
		this.wheelCircum = wheelSircum;
		accLeft = new Accelerator();
		accRight = new Accelerator();
		gyro = new FRCGyroAccelerometer();
		initCan();
		SmartDashboard.putString("Swag", "Dank Dreams");
	}

	private void initCan() {
		frontRightDrive.changeControlMode(TalonControlMode.Follower);
		frontLeftDrive.changeControlMode(TalonControlMode.Follower);
		TalonControlMode mode = TalonControlMode.Speed;
		FeedbackDevice encoder = FeedbackDevice.QuadEncoder;
		backRightDrive.changeControlMode(mode);
		backRightDrive.setFeedbackDevice(encoder);
		backRightDrive.configEncoderCodesPerRev(encoderCodePerRev);
		backRightDrive.enable();
		backRightDrive.setPID(1, 0, 0);
		backRightDrive.setF(2);
		backRightDrive.reverseOutput(true);
		backRightDrive.reverseSensor(true);
		
		backLeftDrive.changeControlMode(mode);
		backLeftDrive.setFeedbackDevice(encoder);
		backLeftDrive.configEncoderCodesPerRev(encoderCodePerRev);
		backLeftDrive.enable();
		backLeftDrive.setPID(1, 0, 0);
		backLeftDrive.setF(2);

		 // :D 
		/*
		if (frontRightDrive != null)
		{
			frontRightDrive.changeControlMode(mode);
			frontRightDrive.setFeedbackDevice(encoder);
			frontRightDrive.configEncoderCodesPerRev(encoderCodePerRev);
			frontRightDrive.enable();
			frontRightDrive.setPID(0, 0, 0);
			frontRightDrive.setF(1);
		}
		if (frontLeftDrive != null)
		{
			frontLeftDrive.changeControlMode(mode);
			frontLeftDrive.setFeedbackDevice(encoder);
			frontLeftDrive.configEncoderCodesPerRev(encoderCodePerRev);
			frontLeftDrive.enable();
			frontLeftDrive.setPID(0, 0, 0);
			frontLeftDrive.setF(1);
		}*/
	}
	
	
	
	/**** GYRO STUFF ****/
    public double getAngle()
    {
    	return gyro.getGyroAngle();
    }
    
    public void resetGyro()
    {
    	gyro.resetGyro();
    }

    
    
    
	private void changeTalonToSpeed()
	{
		TalonControlMode mode = TalonControlMode.Speed;
		backLeftDrive.changeControlMode(mode);
		backRightDrive.changeControlMode(mode);
	}
	
	private void changeTalonToPercent()
	{
		TalonControlMode mode = TalonControlMode.PercentVbus;
		backLeftDrive.changeControlMode(mode);
		backRightDrive.changeControlMode(mode);
	}
	
	public void speedTankDrive(double leftValuePerMin, double rightValuePerMin, boolean isInInches) {
		changeTalonToSpeed();
		double leftRPM = leftValuePerMin;
		double rightRPM = rightValuePerMin;
		if (isInInches) {
			leftRPM /= wheelDiameter;
			rightRPM /= wheelDiameter;
		}
		backLeftDrive.set(leftRPM);
		SmartDashboard.putNumber("Back Left Speed", backLeftDrive.get());
		backRightDrive.set(rightRPM);
		SmartDashboard.putNumber("Back Right Speed", backRightDrive.get());
		slave();
	}// c:

	public void fakeSpeedTankDrive(double leftValuePerMin, double rightValuePerMin, boolean isInInches, double scalePower) {
		changeTalonToSpeed();
		double leftRPM = leftValuePerMin;
		double rightRPM = rightValuePerMin;
		if (isInInches) {
			leftRPM /= wheelDiameter;
			rightRPM /= wheelDiameter;
		}

		double currLeft = (leftRPM - backLeftDrive.getSpeed()) / scalePower;
		double currRight = (rightRPM - backRightDrive.getSpeed()) / scalePower;

		backLeftDrive.set(currLeft);
		SmartDashboard.putNumber("Back Left Speed", backLeftDrive.get());
		backRightDrive.set(currRight);
		SmartDashboard.putNumber("Back Right Speed", backRightDrive.get());
		slave();
	}

	public void accelTankDrive(double left, double right) {
		changeTalonToPercent();
		backLeftDrive.set(accLeft.decelerateValue(accLeft.getSpeed(), left));
		SmartDashboard.putNumber("Acc Left Speed", accLeft.getSpeed());
		backRightDrive.set(accRight.decelerateValue(accRight.getSpeed(), right));
		SmartDashboard.putNumber("Acc Right Speed", accRight.getSpeed());
		slave();
	}

	public void tankDrive(double left, double right) {
		changeTalonToPercent();
		backLeftDrive.set(left);
		backRightDrive.set(right);

		SmartDashboard.putNumber("Back Left Speed", backLeftDrive.getPosition());
		SmartDashboard.putNumber("Back Right Speed", backRightDrive.getPosition());

		slave();
	}
	
	public boolean fakeDriveDistance(double distanceInInches, boolean isFoward){
		changeTalonToPercent();
		double leftDistance = backLeftDrive.getEncPosition() * wheelCircum;
		double rightDistance = backRightDrive.getEncPosition() * wheelCircum;
		double currAngle = gyro.getGyroAngle();
		if(tick++ == 0){
			startLeft = leftDistance;
			startRight = rightDistance;
			startAngle = currAngle;
		}
		
		if(isFoward){
			if(leftDistance < startLeft + distanceInInches){
				if (currAngle > startAngle + BUFFER_ANGLE)
					backLeftDrive.set(DRIVE_POWER - COMPENSATION);
				else
					backLeftDrive.set(DRIVE_POWER);
			}
			else{
				backLeftDrive.set(0);
			}
			
			if(rightDistance < startRight + distanceInInches){
				if (currAngle < startAngle - BUFFER_ANGLE)
		    		backRightDrive.set(DRIVE_POWER - COMPENSATION);
				else
				backRightDrive.set(DRIVE_POWER);
			}
			else{
				backRightDrive.set(0);
			}
			
			if ((leftDistance >= startLeft + distanceInInches) &&
					(rightDistance >= startRight + distanceInInches))
			{
				tick = 0;
				return true;
			}
		}
		else{
			if(leftDistance > startLeft - distanceInInches){
				if (currAngle < startAngle - BUFFER_ANGLE)
					backLeftDrive.set(-DRIVE_POWER + COMPENSATION);
				else
					backLeftDrive.set(-DRIVE_POWER);
			}
			else{
				backLeftDrive.set(0);
			}
			
			if(rightDistance > startRight - distanceInInches){
				if (currAngle > startAngle + BUFFER_ANGLE)
					backRightDrive.set(-DRIVE_POWER + COMPENSATION);
				else
					backRightDrive.set(-DRIVE_POWER);
			}
			else{
				backRightDrive.set(0);
			}
			
			if ((leftDistance <= startLeft - distanceInInches) &&
					(rightDistance <= startRight - distanceInInches))
			{
				tick = 0;
				return true;
			}
		}
		
		slave();
		return false;
		
	}
	
	private void slave() {
		if(frontLeftDrive != null){
			frontLeftDrive.set(backLeftDrive.getDeviceID());
		}
		if(frontRightDrive != null){
			frontRightDrive.set(backRightDrive.getDeviceID());
		}
	}
	
	public void setPID(double p, double i, double d, double f){
		backLeftDrive.setPID(p, i, d);
		backLeftDrive.setF(f);
		backRightDrive.setPID(p, i, d);
		backRightDrive.setF(f);
	}
	
	public CANTalon getFrontLeftDrive() {
		return frontLeftDrive;
	}

	public CANTalon getFrontRightDrive() {
		return frontRightDrive;
	}

	public CANTalon getBackLeftDrive() {
		return backLeftDrive;
	}

	// :)
	public CANTalon getBackRightDrive() {
		return backRightDrive;
	}
}