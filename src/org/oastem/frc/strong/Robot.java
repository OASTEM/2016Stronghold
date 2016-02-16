package org.oastem.frc.strong;

import org.oastem.frc.LogitechGamingPad;
import org.oastem.frc.control.DriveSystem;
import org.oastem.frc.control.TalonDriveSystem;
import org.oastem.frc.sensor.FRCGyroAccelerometer;
import org.oastem.frc.sensor.QuadratureEncoder;

import com.sun.org.glassfish.gmbal.ManagedAttribute;

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
	private final int LEFT = 1;
	private final int RIGHT = 2;
	// private final int BACK_LEFT_CAN_DRIVE = 1;
	// private final int BACK_RIGHT_CAN_DRIVE = 3;
	private final int AUTO_PORT_1 = 1;
	private final int AUTO_PORT_2 = 2;

	private final int ARM_ENC_A = 0;
	private final int ARM_ENC_B = 1;
	private final int ARM_ENC_I = 2;

	private final int DRIVE_ENC_LEFT_A = 0;
	private final int DRIVE_ENC_LEFT_B = 1;

	private final int DRIVE_ENC_RIGHT_A = 3;
	private final int DRIVE_ENC_RIGHT_B = 4;

	// Values
	private final int DRIVE_ENC_CODE_PER_REV = 2048;
	private final int DRIVE_WHEEL_DIAM = 8;
	private final double WHEEL_CIRCUMFERENCE = DRIVE_WHEEL_DIAM * Math.PI;
	private final double MAX_SPEED = 72; // in inches
	private final double ROTATION_SCALE = (MAX_SPEED / WHEEL_CIRCUMFERENCE) * 60;

	// Objects
	private DriveSystem myRobot = DriveSystem.getInstance();
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
	private CANTalon left;
	private CANTalon right;
	private QuadratureEncoder leftDrive;
	private QuadratureEncoder rightDrive;
	private DigitalInput auto1;
	private DigitalInput auto2;

	// Joystick commands

	private double slowTrigger;
	private double winchTrigger;
	private boolean armUp;
	private boolean armDown;
	private boolean manualButton;
	private boolean eStop1;
	private boolean eStop2;
	private boolean releaseWinch;

	public Robot() {
		talonDrive.initializeTalonDrive(LEFT, RIGHT, DRIVE_ENC_CODE_PER_REV, DRIVE_WHEEL_DIAM);
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
		armPositionEncoder = new QuadratureEncoder(ARM_ENC_A, ARM_ENC_B, ARM_ENC_I); 
		armMotor = new Talon(0);
		pad = new LogitechGamingPad(0);
		drivePressed = false;
		speedToggle = false;
		pdp = new PowerDistributionPanel();
		auto1 = new DigitalInput(AUTO_PORT_1);
		auto2 = new DigitalInput(AUTO_PORT_2);
		leftDrive = new QuadratureEncoder(DRIVE_ENC_LEFT_A, DRIVE_ENC_LEFT_B, DRIVE_ENC_CODE_PER_REV);
		rightDrive = new QuadratureEncoder(DRIVE_ENC_RIGHT_A, DRIVE_ENC_RIGHT_B, DRIVE_ENC_CODE_PER_REV);

		pdp.clearStickyFaults();
	}

	// AUTONOMOUS MODES

	private static final int LOW_BAR = 0;
	private static final int OTHER_TERRAIN = 1;
	private static final int PORTCULLIS = 2;
	private static final int TEST = 3;

	public void autonomous() {
		int autoMode = TEST;
		if (auto1.get()) {
			if (auto2.get())
				autoMode = LOW_BAR;
			else
				autoMode = OTHER_TERRAIN;
		} else {
			if (auto2.get())
				autoMode = PORTCULLIS;
		}

		leftDrive.reset();
		rightDrive.reset();

		String state = "Neutral";

		while (isAutonomous() && isEnabled()) {
			if (state.equals("Neutral") && passDefense(autoMode))
				state = "Passed";
			if (state.equals("Passed") && reverse(autoMode))
				state = "Returned";
			if (state.equals("Returned") && reset(autoMode))
				state = "Back";

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

		gyro.resetGyro();
		int what = 0; // Spring insisted
		left = talonDrive.getBackLeftDrive();
		right = talonDrive.getBackRightDrive();
		while (isOperatorControl() && isEnabled()) {
			dash.putNumber("Ticks", what++);
			dash.putNumber("Left Y", pad.getLeftAnalogY());
			dash.putNumber("Right Y", pad.getRightAnalogY());
			dash.putBoolean("Speed Toggle", speedToggle);
			dash.putNumber("Gyro Value:", gyro.getGyroAngle());
			dash.putNumber("Accelerometer X Value: ", gyro.getAccelX());
			dash.putNumber("Accelerometer Y Value: ", gyro.getAccelY());
			dash.putNumber("Accelerometer Z Value: ", gyro.getAccelZ());
			dash.putNumber("Built-In Accelerometer X Value: ", accel.getX());
			dash.putNumber("Built-In Accelerometer Y Value: ", accel.getY());
			dash.putNumber("Built-In Accelerometer Z Value: ", accel.getZ() - 1);

			slowTrigger = pad.getLeftTriggerValue();
			winchTrigger = pad.getRightTriggerValue();
			armUp = pad.getRightBumper();
			armDown = pad.getLeftBumper();
			manualButton = pad.getBButton();
			eStop1 = pad.getBackButton();
			eStop2 = pad.getStartButton();
			releaseWinch = pad.getYButton();

			if (eStop1 && eStop2)
				stop = true;

			// "Arcade" Drive
			if (!stop) {
				if (pad.checkDPad(0)) {
					talonDrive.fakeTankDrive(scaleTrigger(1.0), scaleTrigger(1.0));
				} else if (pad.checkDPad(1)) {
					talonDrive.fakeTankDrive(scaleTrigger(1.0), scaleTrigger(0));
				} else if (pad.checkDPad(2)) {
					talonDrive.fakeTankDrive(scaleTrigger(1.0), scaleTrigger(-1.0));
				} else if (pad.checkDPad(3)) {
					talonDrive.fakeTankDrive(scaleTrigger(-1.0), scaleTrigger(0));
				} else if (pad.checkDPad(4)) {
					talonDrive.fakeTankDrive(scaleTrigger(-1.0), scaleTrigger(-1.0));
				} else if (pad.checkDPad(5)) {
					talonDrive.fakeTankDrive(scaleTrigger(0), scaleTrigger(-1.0));
				} else if (pad.checkDPad(6)) {
					talonDrive.fakeTankDrive(scaleTrigger(-1.0), scaleTrigger(1.0));
				} else if (pad.checkDPad(7)) {
					talonDrive.fakeTankDrive(scaleTrigger(0), scaleTrigger(1.0));
				} else
					motorDrive();
				doArm();
			}
		}
	}

	// Arm States
	private final int TOP_STATE = 0;
	private final int MIDDLE_STATE = 1;
	private final int BOTTOM_STATE = 2;
	private final int RELEASE_STATE = 5;
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
	private final double MAN_POWER = 1.0;
	
	private int stateOfArm = BOTTOM_STATE;
	private int prevState;
	private boolean isManualState = false;
	private boolean manPressed;
	private boolean released = false;
	private boolean releasePressed;

	private void doArm() {
		encoderValue = armPositionEncoder.get();

		if (manualButton && !manPressed) {
			manPressed = true;
			isManualState = !isManualState;
			if (isManualState)
				stateOfArm = MANUAL_STATE;
			else
				stateOfArm = prevState;

		}
		if (!manualButton)
			manPressed = false;

		if (releaseWinch && !releasePressed) {
			releasePressed = true;
			released = !released;
			if (released)
				stateOfArm = RELEASE_STATE;
		}
		if (!pad.getBButton())
			releasePressed = false;

		switch (stateOfArm) {
		case RELEASE_STATE:
			prevState = RELEASE_STATE;
			goalValue = RELEASE_ARM_VALUE;

			if (encoderValue < goalValue)
				armMotor.set(MOVE_POWER);

			dash.putString("State: ", "release state");
			break;

		case TOP_STATE:
			prevState = TOP_STATE;
			goalValue = MAX_ARM_VALUE;

			if (encoderValue < goalValue)
				// go down
				armMotor.set(MOVE_POWER);

			if (pad.getAButton())
				stateOfArm = MIDDLE_STATE;

			dash.putString("State: ", "top state");
			break;

		case MIDDLE_STATE:
			prevState = MIDDLE_STATE;
			goalValue = MID_ARM_VALUE;

			if (encoderValue > goalValue)
				// go down
				armMotor.set(-MOVE_POWER);
			else if (encoderValue < goalValue)
				// go up
				armMotor.set(MOVE_POWER);

			if (pad.getYButton())
				stateOfArm = TOP_STATE;
			else if (pad.getAButton())
				stateOfArm = MIDDLE_STATE;

			dash.putString("State: ", "mid-top state");
			break;

		case BOTTOM_STATE:
			prevState = BOTTOM_STATE;
			goalValue = MIN_ARM_VALUE;

			if (encoderValue > goalValue)
				armMotor.set(-MOVE_POWER);
			else if (encoderValue < goalValue)
				armMotor.set(MOVE_POWER);

			if (pad.getYButton())
				stateOfArm = BOTTOM_STATE;

			dash.putString("State: ", "bottom");
			break;

		case MANUAL_STATE:
			if (pad.getYButton() && encoderValue < MAX_ARM_VALUE)
				armMotor.set(scaleTrigger(MAN_POWER));
			else if (pad.getAButton() && encoderValue > MIN_ARM_VALUE)
				armMotor.set(-scaleTrigger(MAN_POWER));

			dash.putString("State: ", "EMANUEL");
			break;
		}
	}

	private boolean speedToggle;
	private boolean drivePressed;

	private void motorDrive() {
		// max 2 yd per sec = 72 in per sec
		// C = 1 rotation = 25.1327412287 in
		// rps = 2.86478897565
		// rpm = 171.887338539

		if (pad.getLeftBumper() && !drivePressed) {
			drivePressed = true;
			speedToggle = !speedToggle;
		}
		if (!pad.getLeftBumper())
			drivePressed = false;

		if (speedToggle) {
			talonDrive.speedTankDrive(pad.getLeftAnalogY() * -1, pad.getRightAnalogY() * -1, false);
		} else {
			talonDrive.fakeTankDrive(pad.getLeftAnalogY() * -1 * scaleTrigger(pad.getLeftTriggerValue()),
					pad.getRightAnalogY() * scaleTrigger(pad.getLeftTriggerValue()));
		}

		if (pad.checkDPad(2))
			talonDrive.fakeTankDrive(0.5, 0.5);
		else if (pad.checkDPad(6)) {
			talonDrive.fakeTankDrive(-0.5, -0.5);
		}
	}

	private double scaleTrigger(double trigger) {
		return Math.min(1.0, 0.4 + 0.5 * trigger);
	}

	/**
	 * Runs during test mode
	 */
	public void test() {
	}
}
