package org.oastem.frc.strong;

import org.oastem.frc.*;
import org.oastem.frc.control.*;
import org.oastem.frc.sensor.*;
import org.oastem.frc.strong.*;


import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CANTalon;

import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
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
 * controlled cnby the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions . If you change the name of this class or the package after
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
	private final int AUTO_PORT_1 = 8;
	private final int AUTO_PORT_2 = 9;

	private final int ARM_ENC_A = 0;
	private final int ARM_ENC_B = 1;


	// Values
	private final int DRIVE_ENC_CODE_PER_REV = 2048;
	private final double ARM_ENC_CODE_PER_REV = 497;
	private final int DRIVE_WHEEL_DIAM = 8;
	private final double WHEEL_CIRCUMFERENCE = DRIVE_WHEEL_DIAM * Math.PI;
	private final double MAX_SPEED = 72; // in inches
	private final double ROTATION_SCALE = (MAX_SPEED / WHEEL_CIRCUMFERENCE) * 60;

	// Objects
	private TalonDriveSystem talonDrive = TalonDriveSystem.getInstance();
	private PowerDistributionPanel pdp;
	private LogitechGamingPad pad;

	private SmartDashboard dash;
	private SendableChooser chooser;
	private final String defaultAuto = "Default";
	private final String customAuto = "My Auto";

	private FRCGyroAccelerometer gyro;
	private BuiltInAccelerometer accel;
	private Talon armMotor;
	private QuadratureEncoder armPositionEncoder;
	private CANTalon winchMotor;
	private DigitalInput auto1;
	private DigitalInput auto2;

	// Joystick commands

	private double slowTrigger;
	private double winchTrigger;
	private boolean armUpPressed;
	private boolean armDownPressed;
	private boolean manualButtonPressed;
	private boolean eStop1Pressed;
	private boolean eStop2Pressed;
	private boolean releaseWinchPressed;
	

	public Robot() {
		talonDrive.initializeTalonDrive(FRONT_LEFT_CAN_DRIVE, BACK_LEFT_CAN_DRIVE, FRONT_RIGHT_CAN_DRIVE,
				BACK_RIGHT_CAN_DRIVE, DRIVE_ENC_CODE_PER_REV, DRIVE_WHEEL_DIAM, WHEEL_CIRCUMFERENCE);
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
		armMotor = new Talon(0);
		armMotor.setInverted(true);
		armPositionEncoder = new QuadratureEncoder(ARM_ENC_A, ARM_ENC_B, ARM_ENC_CODE_PER_REV);
		armPositionEncoder.setDistancePerPulse(360 * 3);
		pad = new LogitechGamingPad(0);
		//drivePressed = false;
		//speedToggle = false;
		pdp = new PowerDistributionPanel();
		auto1 = new DigitalInput(AUTO_PORT_1);
		auto2 = new DigitalInput(AUTO_PORT_2);

		pdp.clearStickyFaults();
	}

	// AUTONOMOUS MODES

	private static final int LOW_BAR = 0;
	private static final int OTHER_TERRAIN = 1;
	private static final int PORTCULLIS = 2;
	private static final int TEST = 3;

	public void autonomous() {
		String state = "Neutral";

		while (isAutonomous() && isEnabled()) {
			int autoMode = TEST;
			if (auto1.get()) {
				if (auto2.get())
					talonDrive.tankDrive(0.25, -0.25);
					//talonDrive.fakeDriveDistance(WHEEL_CIRCUMFERENCE, true);
					//autoMode = LOW_BAR;
				else
					autoMode = OTHER_TERRAIN;
			} else {
				if (auto2.get())
					autoMode = PORTCULLIS;
			}
			dash.putNumber("Autonomous State", autoMode);/*
<<<<<<< HEAD

=======
>>>>>>> autonomous
			if (state.equals("Neutral") && passDefense(autoMode))
				state = "Passed";
			if (state.equals("Passed") && reverse(autoMode))
				state = "Returned";
			if (state.equals("Returned") && reset(autoMode))
				state = "Back";*/
			
		}
	}

	private boolean passDefense(int mode) {
		if (mode == LOW_BAR) {
			
			return true;
		}
		if (mode == OTHER_TERRAIN) {

			return true;
		}
		if (mode == PORTCULLIS) {

			return true;
		}
		if (mode == TEST) {

			return true;
		}
		return false;
	}

	private boolean reverse(int mode) {
		if (mode == LOW_BAR) {

			return true;
		}
		if (mode == OTHER_TERRAIN) {

			return true;
		}
		if (mode == PORTCULLIS) {

			return true;
		}
		if (mode == TEST) {

			return true;
		}
		return false;
	}

	private boolean reset(int mode) {
		if (mode == LOW_BAR) {

			return true;
		}
		if (mode == OTHER_TERRAIN) {

			return true;
		}
		if (mode == PORTCULLIS) {

			return true;
		}
		if (mode == TEST) {

			return true;
		}
		return false;
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		boolean stop = false;

		//gyro.resetGyro();
		int what = 0; // Spring insisted
		while (isOperatorControl() && isEnabled()) {
			dash.putNumber("Ticks", what++);
			dash.putNumber("Left Y", pad.getLeftAnalogY());
			dash.putNumber("Right Y", pad.getRightAnalogY());
			//dash.putBoolean("Speed Toggle", speedToggle);
			//dash.putNumber("Gyro Value:", gyro.getGyroAngle());
			dash.putNumber("Accelerometer X Value: ", gyro.getAccelX());
			dash.putNumber("Accelerometer Y Value: ", gyro.getAccelY());
			dash.putNumber("Accelerometer Z Value: ", gyro.getAccelZ());
			dash.putNumber("Built-In Accelerometer X Value: ", accel.getX());
			dash.putNumber("Built-In Accelerometer Y Value: ", accel.getY());
			dash.putNumber("Built-In Accelerometer Z Value: ", accel.getZ() - 1);

			slowTrigger = pad.getLeftTriggerValue();
			winchTrigger = pad.getRightTriggerValue();
			armUpPressed = pad.getRightBumper();
			armDownPressed = pad.getLeftBumper();
			manualButtonPressed = pad.getBButton();
			eStop1Pressed = pad.getBackButton();
			eStop2Pressed = pad.getStartButton();
			releaseWinchPressed = pad.getYButton();

			if (eStop1Pressed && eStop2Pressed)
				stop = true;

			// "Arcade" Drive
			if (!stop) {
				if (pad.checkDPad(0)) {
					talonDrive.tankDrive(scaleTrigger(1.0), scaleTrigger(1.0));
				} else if (pad.checkDPad(1)) {
					talonDrive.tankDrive(scaleTrigger(1.0), scaleTrigger(0));
				} else if (pad.checkDPad(2)) {
					talonDrive.tankDrive(scaleTrigger(1.0), scaleTrigger(-1.0));
				} else if (pad.checkDPad(3)) {
					talonDrive.tankDrive(scaleTrigger(0), scaleTrigger(-1.0));
				} else if (pad.checkDPad(4)) {
					talonDrive.tankDrive(scaleTrigger(-1.0), scaleTrigger(-1.0));
				} else if (pad.checkDPad(5)) {
					talonDrive.tankDrive(scaleTrigger(-1.0), scaleTrigger(0));
				} else if (pad.checkDPad(6)) {
					talonDrive.tankDrive(scaleTrigger(-1.0), scaleTrigger(1.0));
				} else if (pad.checkDPad(7)) {
					talonDrive.tankDrive(scaleTrigger(0), scaleTrigger(1.0));
				} else
					System.out.println("lol");
					//motorDrive();
				//doArm();
				if (pad.getRightBumper()){
					armMotor.set(1);
				}
				else if (pad.getLeftBumper()){
					armMotor.set(-0.65);
				}
				else
					armMotor.set(0);

			}
		}
	}

	// Arm States
	private final int TOP_STATE = 0;
	private final int MIDDLE_STATE = 1;
	private final int BOTTOM_STATE = 2;
	private final int RELEASE_STATE = 5;
	private final int CALIBRATE_STATE = 6;
	private final int MANUAL_STATE = 8;

	private final int RELEASE_ARM_VALUE = 180; // for now
	private final int MAX_ARM_VALUE = 120; // for now
	private final int MID_ARM_VALUE = 90; // for now
	private final int MIN_ARM_VALUE = 0; // for now
	private int goalValue;
	private int encoderValue;

	private final double MOVE_POWER = 0.5;
	private final double REST_BOT_POWER = 0.3;
	private final double REST_MID_POWER = 0.4;
	private final double REST_TOP_POWER = 0.3;
	private final double ARM_MAN_POWER = 0.65;
	
	private int THRESHOLD_VALUE = 10;
	private double CONSTANT_POWER = .0275; //for now


	private int stateOfArm = BOTTOM_STATE;
	//private boolean isManualState = false; was originally this but
	private boolean isManualState = true; // use this for testing
	private int prevState;
	private boolean manPressed;
	private boolean released = false;
	private boolean releasePressed;
	private boolean winchRelease = false;
	private boolean calibrateStarting = false;
	
	private long currTime = 0L;
	private long caliStart = 0L;


	private void calibrateArm()
	{
		armMotor.set(-.025);
	}
	
	private void doArm() {
		currTime = System.currentTimeMillis();
		/*
		encoderValue = armPositionEncoder.get(); // accounted for gear ratio of arm
		if (manualButtonPressed && !manPressed) {
			manPressed = true;
			isManualState = !isManualState;
			if (isManualState)
				stateOfArm = MANUAL_STATE;
			else
				stateOfArm = prevState;
		}
		if (!manualButtonPressed)
			manPressed = false;
			
		if (releaseWinchPressed && !releasePressed) {
			releasePressed = true;
			released = !released;
			if (released)
				stateOfArm = RELEASE_STATE;
		}
<<<<<<< HEAD
		if (!releaseWinchPressed)
=======

		if (!pad.getBButton())
>>>>>>> autonomous
			releasePressed = false;

		switch (stateOfArm) {
		case CALIBRATE_STATE:
			if (calibrateStarting)
			{
				caliStart = currTime;
				calibrateStarting = false;
			}
			calibrateArm();
			break;
		case RELEASE_STATE:
			prevState = RELEASE_STATE;
			goalValue = RELEASE_ARM_VALUE;

			if (encoderValue < goalValue && !winchRelease)
				armMotor.set(MOVE_POWER);

			if (encoderValue > RELEASE_ARM_VALUE) {
				if (releaseWinchPressed) {
					winchRelease = true;
					winchMotor.set(winchTrigger);
					// winchMotor.set(-winchTrigger);
				}
			}
<<<<<<< HEAD

=======
>>>>>>> autonomous
			dash.putString("State: ", "release state");
			break;
		case TOP_STATE:
			prevState = TOP_STATE;
			goalValue = MAX_ARM_VALUE;

			/*if (goalValue - encoderValue >= -THRESHOLD_VALUE && goalValue - encoderValue <= THRESHOLD_VALUE)
				// set to a constant power
				armMotor.set(CONSTANT_POWER);
			else *//*if (encoderValue > goalValue)
				armMotor.set(-MOVE_POWER);
			else if (encoderValue < goalValue)
				armMotor.set(MOVE_POWER);
<<<<<<< HEAD
			
			if (pad.getLeftBumper())
=======
			if (pad.getAButton())
>>>>>>> autonomous
				stateOfArm = MIDDLE_STATE;
			dash.putString("State: ", "top state");
			break;
		case MIDDLE_STATE:
			prevState = MIDDLE_STATE;
			goalValue = MID_ARM_VALUE;

			if (goalValue - encoderValue >= -THRESHOLD_VALUE && goalValue - encoderValue <= THRESHOLD_VALUE)
				// set to a constant power
				armMotor.set(CONSTANT_POWER);
			else if (encoderValue > goalValue)
				armMotor.set(-MOVE_POWER);
			else if (encoderValue < goalValue)
				armMotor.set(MOVE_POWER);
			if (pad.getRightBumper())
				stateOfArm = TOP_STATE;
			else if (pad.getLeftBumper())
				stateOfArm = MIDDLE_STATE;
			dash.putString("State: ", "middle state");
			break;
		case BOTTOM_STATE:
			prevState = BOTTOM_STATE;
			goalValue = MIN_ARM_VALUE;
		
			if (goalValue - encoderValue >= -THRESHOLD_VALUE && goalValue - encoderValue <= THRESHOLD_VALUE)
				// set to a constant power
				armMotor.set(CONSTANT_POWER);
			else if (encoderValue > goalValue)
				armMotor.set(-MOVE_POWER);
			
			if (pad.getRightBumper())
				stateOfArm = MIDDLE_STATE;
			dash.putString("State: ", "bottom");
			break;
		case MANUAL_STATE:*/
			if (pad.getRightBumper())// && encoderValue < MAX_ARM_VALUE)
				armMotor.set(ARM_MAN_POWER);
			else if (pad.getLeftBumper())// && encoderValue > MIN_ARM_VALUE)
				armMotor.set(-ARM_MAN_POWER);
			else
				armMotor.set(0);
			dash.putString("State: ", "EMANUEL");
			/*break;
		}*/
	}

	private boolean speedToggle;
	private boolean drivePressed;

	private void motorDrive() {
		// max 2 yd per sec = 72 in per sec
		// C = 1 rotation = 25.1327412287 in
		// rps = 2.86478897565
		// rpm = 171.887338539

		if (pad.getAButton() && !drivePressed) {
			drivePressed = true;
			speedToggle = !speedToggle;
		}
		if (!pad.getAButton())
			drivePressed = false;

		if (speedToggle) {
			talonDrive.speedTankDrive(30, 30, false);/*
			talonDrive.speedTankDrive(pad.getLeftAnalogY() * -1 * scaleTrigger(pad.getLeftTriggerValue()),
					pad.getRightAnalogY() * scaleTrigger(pad.getLeftTriggerValue()));*/
		} else {
			talonDrive.accelTankDrive(pad.getLeftAnalogY() * scaleTrigger(pad.getLeftTriggerValue()),
					pad.getRightAnalogY() * scaleTrigger(pad.getLeftTriggerValue()));
		}

		dash.putBoolean("speedtoggle", speedToggle);
	}

	private double scaleTrigger(double trigger) {
		return Math.min(1.0, 1.0 - 0.9 * trigger);
	}

	/**
	 * Runs during test mode
	 */
	public void test() {
	}
}
