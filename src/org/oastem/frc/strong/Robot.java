package org.oastem.frc.strong;

import org.oastem.frc.LogitechGamingPad;
import org.oastem.frc.control.DriveSystem;
import org.oastem.frc.control.TalonDriveSystem;
import org.oastem.frc.sensor.FRCGyroAccelerometer;
import org.oastem.frc.sensor.QuadratureEncoder;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CANTalon;

import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Talon;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import org.oastem.frc.sensor.FRCGyroAccelerometer;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {
	// Ports
	private final int FRONT_LEFT_CAN_DRIVE = 0;
	private final int FRONT_RIGHT_CAN_DRIVE = 2;
	private final int BACK_LEFT_CAN_DRIVE = 1;
	private final int BACK_RIGHT_CAN_DRIVE = 3;

	// Values
	private final int DRIVE_ENC_CODE_PER_REV = 2048;
	private final int DRIVE_WHEEL_DIAM = 8;
	private final double WHEEL_CIRCUMFERENCE = 8.0 * Math.PI;
	private final double MAX_SPEED = 72; // in inches
	private final double ROTATION_SCALE = (MAX_SPEED / WHEEL_CIRCUMFERENCE) * 60; // in

	// Arm States
	private final int TOP_STATE = 0;
	private final int MIDDLE_TOP_STATE = 1;
	private final int MIDDLE_BOTTOM_STATE = 3;
	private final int BOTTOM_STATE = 4;
	private final int MANUAL_STATE = 5;
	private final int E_STOP_STATE = 6;
	private final int MAX_ARM_VALUE = 180; // for now
	private final int MID_TOP_ARM_VALUE = 135;
	private final int MID_BOTTOM_ARM_VALUE = 45;
	private final int MIN_ARM_VALUE = 0; // for now
	private boolean speedToggle;
	private boolean isPressed;
	private boolean isRotating;
	private static double joyScale = 1.0;
	DriveSystem myRobot = DriveSystem.getInstance();
	TalonDriveSystem talonDrive = TalonDriveSystem.getInstance();
	// Joystick stickLeft;
	// Joystick stickRight;
	Talon armMotor;
	LogitechGamingPad pad;
	QuadratureEncoder armPositionEncoder;
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	SendableChooser chooser;
	CANTalon test;
	FRCGyroAccelerometer gyro;
	SmartDashboard dash;
	BuiltInAccelerometer accel;
	// Objects
	private PowerDistributionPanel pdp;
	private CANTalon left;
	private CANTalon right;

	public Robot() {
		talonDrive.initializeTalonDrive(FRONT_LEFT_CAN_DRIVE, BACK_LEFT_CAN_DRIVE, FRONT_RIGHT_CAN_DRIVE, BACK_RIGHT_CAN_DRIVE,
										DRIVE_ENC_CODE_PER_REV, DRIVE_WHEEL_DIAM);
		test = new CANTalon(0);
		test.changeControlMode(TalonControlMode.Speed);
		test.reverseSensor(true);
		test.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		test.configEncoderCodesPerRev(2048);
		test.enable();
	}

	public void robotInit() {
		chooser = new SendableChooser();
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto modes", chooser);
		dash = new SmartDashboard();
		gyro = new FRCGyroAccelerometer();
		accel = new BuiltInAccelerometer();
		accel = new BuiltInAccelerometer(Accelerometer.Range.k4G);
		armPositionEncoder = new QuadratureEncoder(0, 1, 2); // i dont know what
																// i did FIX
																// LATER i
																// actually knew
																// what i was
																// doing
		armMotor = new Talon(0);
		pad = new LogitechGamingPad(0);
		isPressed = false;
		speedToggle = false;
		isRotating = false;
		pdp = new PowerDistributionPanel();

		pdp.clearStickyFaults();
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		gyro.resetGyro();
		int stateOfArm = BOTTOM_STATE;
		boolean isManualState = false;
		int goalValue = 0;
		int what = 0; // Spring insisted
		left = talonDrive.getBackLeftDrive();
		right = talonDrive.getBackRightDrive();
		while (isOperatorControl() && isEnabled()) {
			dash.putNumber("Ticks", what++);
			motorDrive();
			dash.putNumber("Left Y", pad.getLeftAnalogY());
			dash.putNumber("Right Y", pad.getRightAnalogY());
			dash.putBoolean("Speed Toggle", speedToggle);
			test.set(60);
			dash.putNumber("Gyro Value:", gyro.getGyroAngle());
			dash.putNumber("Accelerometer X Value: ", gyro.getAccelX());
			dash.putNumber("Accelerometer Y Value: ", gyro.getAccelY());
			dash.putNumber("Accelerometer Z Value: ", gyro.getAccelZ());
			dash.putNumber("Built-In Accelerometer X Value: ", accel.getX());
			dash.putNumber("Built-In Accelerometer Y Value: ", accel.getY());
			dash.putNumber("Built-In Accelerometer Z Value: ", accel.getZ() - 1);

			int encoderValue = armPositionEncoder.get();
			// toggle button is the b button
			if (pad.getBButton())
				isManualState = !isManualState;
			if (isManualState)
				stateOfArm = MANUAL_STATE;
			switch (stateOfArm) {
			case TOP_STATE:
				goalValue = MAX_ARM_VALUE;
				if (encoderValue > goalValue)
					// go down
					armMotor.set(-.5);
				if (pad.getAButton())
					stateOfArm = MIDDLE_TOP_STATE;
				else if (pad.getBButton()) {
					isManualState = !isManualState;
					stateOfArm = MANUAL_STATE;
				}
				dash.putString("State: ", "top state");
				break;
			case MIDDLE_TOP_STATE:
				goalValue = MID_TOP_ARM_VALUE;
				if (encoderValue > goalValue)
					// go down
					armMotor.set(-.5);
				else if (encoderValue < goalValue)
					// go up
					armMotor.set(.5);
				if (pad.getYButton())
					stateOfArm = TOP_STATE;
				else if (pad.getAButton())
					stateOfArm = MIDDLE_BOTTOM_STATE;
				else if (pad.getBButton()) {
					isManualState = !isManualState;
					stateOfArm = MANUAL_STATE;
				}
				dash.putString("State: ", "mid-top state");
				break;
			case MIDDLE_BOTTOM_STATE:
				goalValue = MID_BOTTOM_ARM_VALUE;
				if (encoderValue > goalValue)
					armMotor.set(-.5);
				else if (encoderValue < goalValue)
					armMotor.set(.5);
				if (pad.getYButton())
					stateOfArm = MIDDLE_TOP_STATE;
				else if (pad.getAButton())
					stateOfArm = BOTTOM_STATE;
				else if (pad.getBButton()) {
					isManualState = !isManualState;
					stateOfArm = MANUAL_STATE;
				}
				dash.putString("State: ", "mid-bottom");
				break;
			case BOTTOM_STATE:
				goalValue = MIN_ARM_VALUE;
				if (encoderValue > goalValue)
					armMotor.set(-.5);
				else if (encoderValue < goalValue)
					armMotor.set(.5);
				if (pad.getYButton())
					stateOfArm = MIDDLE_BOTTOM_STATE;
				else if (pad.getBButton()) {
					isManualState = !isManualState;
					stateOfArm = MANUAL_STATE;
				}
				dash.putString("State: ", "bottom");
				break;
			case MANUAL_STATE:
				if (pad.getYButton() && encoderValue < MAX_ARM_VALUE)
					armMotor.set(.75);
				else if (pad.getAButton() && encoderValue > MIN_ARM_VALUE)
					armMotor.set(-.75);
				if (pad.getBButton()) {
					isManualState = !isManualState;
					// what would stateOfArm be?
				}
				break;
			case E_STOP_STATE:
				armMotor.set(0);
				// stop everything....but what is everything??
				break;
			}
		}
	}

	/**
	 * Runs during test mode
	 */
	public void test() {
	}
	// rp

	private double scaleTrigger(double trigger) {
		return Math.min(1.0, 0.8 - 0.5 * trigger);
	}

	private void motorDrive() {
		// max 2 yd per sec = 72 in per sec
		// C = 1 rotation = 25.1327412287 in
		// rps = 2.86478897565
		// rpm = 171.887338539

		if (speedToggle && !isRotating) {
			talonDrive.speedTankDrive(pad.getLeftAnalogY() * -1, pad.getRightAnalogY(), false);
		} else if (!isRotating) {
			talonDrive.tankDrive(pad.getLeftAnalogY() * scaleTrigger(pad.getLeftTriggerValue()),
					pad.getRightAnalogY() * scaleTrigger(pad.getLeftTriggerValue()));
		}

		if (pad.getLeftBumper() && !isPressed) {
			isPressed = true;
			speedToggle = !speedToggle;
		}
		if (!pad.getLeftBumper())
			isPressed = false;

		if (pad.checkDPad(2)) {
			isRotating = true;
			talonDrive.fakeTankDrive(0.1, 0.1);
		} else if (pad.checkDPad(6)) {
			isRotating = true;
			talonDrive.fakeTankDrive(-0.1, -0.1);
		} else
			isRotating = false;

		/*
		 * dash.putNumber(talonDrive.getFrontLeftDrive().getSmartDashboardType()
		 * , talonDrive.getFrontLeftDrive().getOutputVoltage());
		 * dash.putNumber(talonDrive.getFrontRightDrive().getSmartDashboardType(
		 * ),
		 * 
		 * talonDrive.getFrontRightDrive().getOutputVoltage());
		 * dash.putNumber(talonDrive.getBackLeftDrive().getSmartDashboardType(),
		 * talonDrive.getBackLeftDrive().getOutputVoltage());
		 * dash.putNumber(talonDrive.getBackRightDrive().getSmartDashboardType()
		 * , talonDrive.getBackRightDrive().getOutputVoltage());
		 * 
		 * if (talonDrive.getFrontLeftDrive().getOutputVoltage() > 12.0) {
		 * talonDrive.getFrontLeftDrive().setVoltageRampRate(0); // ??? }
		 * 
		 * if (talonDrive.getFrontRightDrive().getOutputVoltage() > 12.0) {
		 * talonDrive.getFrontRightDrive().setVoltageRampRate(0); // ??? }
		 * 
		 * if (talonDrive.getBackLeftDrive().getOutputVoltage() > 12.0) {
		 * talonDrive.getBackLeftDrive().setVoltageRampRate(0); // ??? }
		 * 
		 * if (talonDrive.getBackRightDrive().getOutputVoltage() > 12.0) {
		 * talonDrive.getBackRightDrive().setVoltageRampRate(0); // ??? }
		 */
	}

	public void accelerate() {

	}
}
