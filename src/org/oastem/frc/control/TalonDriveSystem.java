package org.oastem.frc.control;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TalonDriveSystem extends DriveSystem {// (:
	// TALON_SRX's
	private CANTalon frontRightDrive;
	private CANTalon frontLeftDrive;
	private CANTalon backRightDrive;
	private CANTalon backLeftDrive;
	private Accelerator accLeft;
	private Accelerator accRight;
	private int encoderCodePerRev;
	private int wheelDiameter;

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

	public void initializeTalonDrive(int leftFront, int leftRear, int rightFront, int rightRear, int pulsesPerRev,
			int wheelDiameter) {
		frontRightDrive = new CANTalon(rightFront);
		frontLeftDrive = new CANTalon(leftFront);
		backRightDrive = new CANTalon(rightRear);
		backLeftDrive = new CANTalon(leftRear);
		frontRightDrive.changeControlMode(TalonControlMode.Follower);
		frontLeftDrive.changeControlMode(TalonControlMode.Follower);
		encoderCodePerRev = pulsesPerRev;
		this.wheelDiameter = wheelDiameter;
		accLeft = new Accelerator();
		accRight = new Accelerator();
		initCan();
		// super.initializeDrive(leftFront, leftRear, rightFront, rightRear);
	}

	// :-)
	public void initializeTalonDrive(int left, int right, int pulsesPerRev, int wheelDiameter) {
		frontRightDrive = null;
		frontLeftDrive = null;
		backRightDrive = new CANTalon(right);
		backLeftDrive = new CANTalon(left);
		encoderCodePerRev = pulsesPerRev;
		this.wheelDiameter = wheelDiameter;
		accLeft = new Accelerator();
		accRight = new Accelerator();
		initCan();
		super.initializeDrive(left, right);
		SmartDashboard.putString("Swag", "Dank Dreams");
	}

	private void initCan() {
		TalonControlMode mode = TalonControlMode.Speed;
		FeedbackDevice encoder = FeedbackDevice.QuadEncoder;
		backRightDrive.changeControlMode(mode);
		backRightDrive.setFeedbackDevice(encoder);
		backRightDrive.configEncoderCodesPerRev(encoderCodePerRev);
		backRightDrive.enable();
		backLeftDrive.changeControlMode(mode);
		backLeftDrive.setFeedbackDevice(encoder);
		backLeftDrive.configEncoderCodesPerRev(encoderCodePerRev);
		backLeftDrive.enable();
		 // :D
		if (frontRightDrive != null)
		{
			frontRightDrive.changeControlMode(mode);
			frontRightDrive.setFeedbackDevice(encoder);
			frontRightDrive.configEncoderCodesPerRev(encoderCodePerRev);
			frontRightDrive.enable();
			frontRightDrive.setPID(1, 0, 0);
		}
		if (frontLeftDrive != null)
		{
			frontLeftDrive.changeControlMode(mode);
			frontLeftDrive.setFeedbackDevice(encoder);
			frontLeftDrive.configEncoderCodesPerRev(encoderCodePerRev);
			frontLeftDrive.enable();
			frontLeftDrive.setPID(1, 0, 0);
		}
	}

	public void speedTankDrive(double leftValuePerMin, double rightValuePerMin, boolean isInInches) {
		double leftRPM = leftValuePerMin;
		double rightRPM = rightValuePerMin;
		if (isInInches) {
			leftRPM /= wheelDiameter;
			rightRPM /= wheelDiameter;
		}
		backLeftDrive.set(leftRPM);
		SmartDashboard.putNumber("Back Left Speed", backLeftDrive.get());
		if (frontLeftDrive != null)
			frontLeftDrive.set(backLeftDrive.getDeviceID());
		backRightDrive.set(rightRPM);
		SmartDashboard.putNumber("Back Right Speed", backRightDrive.get());
		if (frontLeftDrive != null)
			frontRightDrive.set(frontRightDrive.getDeviceID());

	}// c:

	public void fakeSpeedTankDrive(double leftValuePerMin, double rightValuePerMin, boolean isInInches, double scalePower) {
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
		if (frontLeftDrive != null)
			frontLeftDrive.set(backLeftDrive.getDeviceID());
		backRightDrive.set(currRight);
		SmartDashboard.putNumber("Back Right Speed", backRightDrive.get());
		if (frontLeftDrive != null)
			frontRightDrive.set(frontRightDrive.getDeviceID());
	}

	public void tankDrive(double left, double right) {
		backLeftDrive.set(accLeft.decelerateValue(accLeft.getSpeed(), left));
		SmartDashboard.putNumber("Acc Left Speed", accLeft.getSpeed());
		backRightDrive.set(accRight.decelerateValue(accRight.getSpeed(), right));
		SmartDashboard.putNumber("Acc Right Speed", accRight.getSpeed());
		if (frontLeftDrive != null)
			frontLeftDrive.set(frontLeftDrive.getDeviceID());
		if (frontRightDrive != null)
			frontRightDrive.set(frontRightDrive.getDeviceID());
	}

	public void fakeTankDrive(double left, double right) {
		backLeftDrive.set(left);
		backRightDrive.set(right);
		if (frontLeftDrive != null)
			frontLeftDrive.set(backLeftDrive.getDeviceID());
		if (frontRightDrive != null)
			frontRightDrive.set(backRightDrive.getDeviceID());
	}

	public CANTalon getFrontLeftDrive() {
		return frontLeftDrive;
	}

	// :)
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