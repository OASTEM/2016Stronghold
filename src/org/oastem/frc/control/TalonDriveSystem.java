package org.oastem.frc.control;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TalonDriveSystem extends DriveSystem {
	// TALON_SRX's
	private CANTalon frontRightDrive;
	private CANTalon frontLeftDrive;
	private CANTalon backRightDrive;
	private CANTalon backLeftDrive;
	private Accelerator acc;
	private int encoderCodePerRev;
	private int wheelDiameter;

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

	public void initializeTalonDrive(int leftFront, int leftRear,
			int rightFront, int rightRear, int pulsesPerRev, int wheelDiameter) {
		frontRightDrive = new CANTalon(rightFront);
		frontLeftDrive = new CANTalon(leftFront);
		backRightDrive = new CANTalon(rightRear);
		backLeftDrive = new CANTalon(leftRear);
		encoderCodePerRev = pulsesPerRev;
		this.wheelDiameter = wheelDiameter;
		// initCan();
		super.initializeDrive(leftFront, leftRear, rightFront, rightRear);
	}

	public void initializeTalonDrive(int left, int right, int pulsesPerRev,
			int wheelDiameter) {
		frontRightDrive = null;
		frontLeftDrive = null;
		backRightDrive = new CANTalon(right);
		backLeftDrive = new CANTalon(left);
		encoderCodePerRev = pulsesPerRev;
		this.wheelDiameter = wheelDiameter;
		acc = new Accelerator();
		// initCan();
		super.initializeDrive(left, right);
		SmartDashboard.putString("Swag", "Dank Dreams");
	}

	private void initCan() {
		TalonControlMode mode = TalonControlMode.Speed;
		if (frontRightDrive != null) {
			frontRightDrive.changeControlMode(mode);
			frontRightDrive.configEncoderCodesPerRev(encoderCodePerRev);
			frontRightDrive.enable();
		}
		if (frontLeftDrive != null) {
			frontLeftDrive.changeControlMode(mode);
			frontLeftDrive.configEncoderCodesPerRev(encoderCodePerRev);
			frontLeftDrive.enable();
		}
		backRightDrive.changeControlMode(mode);
		backRightDrive.configEncoderCodesPerRev(encoderCodePerRev);
		backRightDrive.enable();
		backRightDrive.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		backRightDrive.setPID(0.1, 0, 1.0);
		backLeftDrive.changeControlMode(mode);
		backLeftDrive.configEncoderCodesPerRev(encoderCodePerRev);
		backLeftDrive.enable();
		backLeftDrive.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		backLeftDrive.setPID(1.0, 1.0, 1.0);
	}

	public void speedTankDrive(double leftValuePerMin, double rightValuePerMin,
			boolean isInInches) {
		double leftRPM = leftValuePerMin;
		double rightRPM = rightValuePerMin;
		if (isInInches) {
			leftRPM /= wheelDiameter;
			rightRPM /= wheelDiameter;
		}
		backLeftDrive.set(leftRPM);
		SmartDashboard.putNumber("kek", leftRPM);
		SmartDashboard.putNumber("Back Left Speed", backLeftDrive.get());
		if (frontLeftDrive != null)
			frontLeftDrive.set(leftRPM);
		backRightDrive.set(rightRPM);
		SmartDashboard.putNumber("Back Right Speed", backRightDrive.get());
		if (frontLeftDrive != null)
			frontRightDrive.set(rightRPM);

	}

	public void tankDrive(double x, double y) {
		backRightDrive.set(acc.decelerateValue(backRightDrive.get(), x));
		backLeftDrive.set(acc.decelerateValue(backLeftDrive.get(), y));
	}
	
	public void fakeTankDrive(double x, double y) {
		backRightDrive.set(x);
		backLeftDrive.set(y);
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

	public CANTalon getBackRightDrive() {
		return backRightDrive;
	}
	

}
