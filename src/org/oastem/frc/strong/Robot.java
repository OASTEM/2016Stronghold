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
	private final int LEFT = 1;
	private final int RIGHT = 2;
	private final int FRONT_LEFT_CAN_DRIVE = 0;
	private final int FRONT_RIGHT_CAN_DRIVE = 2;
	private final int BACK_LEFT_CAN_DRIVE = 1;
	private final int BACK_RIGHT_CAN_DRIVE = 3;
	private final int AUTO_PORT_1 = 8;
	private final int AUTO_PORT_2 = 9;

	private final int ARM_ENC_A = 0;
	private final int ARM_ENC_B = 1;

	private final int DRIVE_ENC_LEFT_A = 0;
	private final int DRIVE_ENC_LEFT_B = 1;

	private final int DRIVE_ENC_RIGHT_A = 3;
	private final int DRIVE_ENC_RIGHT_B = 4;

	// Values
	private final int DRIVE_ENC_CODE_PER_REV = 2048;
	private final int ARM_ENC_CODE_PER_REV = 497;
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
	private SendableChooser autoSelect;
	private final String defaultAuto = "Test";
	private final String customAuto1 = "Low Terrain";
	private final String customAuto2 = "Other Terrain";
	private final String customAuto3 = "Portcullis";
	private SendableChooser drive;
	private final String 
	
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
	private boolean armUp;
	private boolean armDown;
	private boolean manualButton;
	private boolean eStop1;
	private boolean eStop2;
	private boolean releaseWinch;

	public Robot() {
		talonDrive.initializeTalonDrive(FRONT_LEFT_CAN_DRIVE, BACK_LEFT_CAN_DRIVE, FRONT_RIGHT_CAN_DRIVE,
				BACK_RIGHT_CAN_DRIVE, DRIVE_ENC_CODE_PER_REV, DRIVE_WHEEL_DIAM, WHEEL_CIRCUMFERENCE);
	}

	public void robotInit() {
		autoSelect = new SendableChooser();
		autoSelect.addDefault("Test", defaultAuto);
		autoSelect.addObject("Low Terrain", customAuto1);
		autoSelect.addObject("Other Terrain", customAuto2);
		autoSelect.addObject("Portcullis", customAuto3);

		SmartDashboard.putData("Auto modes", autoSelect);
		
		dash = new SmartDashboard();
		gyro = new FRCGyroAccelerometer();
		talonDrive.calibrateGyro();
		accel = new BuiltInAccelerometer();
		accel = new BuiltInAccelerometer(Accelerometer.Range.k4G);
		armMotor = new Talon(0);
		// armPositionEncoder = new QuadratureEncoder(ARM_ENC_A, ARM_ENC_B,
		// ARM_ENC_CODE_PER_REV);
		pad = new LogitechGamingPad(0);
		drivePressed = false;
		speedToggle = false;
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
		talonDrive.resetGyro();
		int autoMode = 0;
		if (autoSelect.getSelected().equals(defaultAuto))
			autoMode = TEST;
		if (autoSelect.getSelected().equals(customAuto1))
			autoMode = LOW_BAR;
		if (autoSelect.getSelected().equals(customAuto2))
			autoMode = OTHER_TERRAIN;
		if (autoSelect.getSelected().equals(customAuto3))
			autoMode = PORTCULLIS;
		
		/*
		if (auto1.get()) {
			if (auto2.get()) {
				autoMode = LOW_BAR;
			} else
				autoMode = OTHER_TERRAIN;
		} else {
			if (auto2.get())
				autoMode = PORTCULLIS;
		}

		while (isAutonomous() && isEnabled()) {
			dash.putNumber("Autonomous State",autoMode);/*
								 * if (state.equals("Neutral") &&
								 * passDefense(autoMode)) state = "Passed"; if
								 * (state.equals("Passed") && reverse(autoMode))
								 * state = "Returned"; if
								 * (state.equals("Returned") && reset(autoMode))
								 * state = "Back";
								 

		}*/
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

		// gyro.resetGyro();
		int what = 0; // Spring insisted
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
			if (!stop) {/*
						 * if (pad.checkDPad(0)) {
						 * talonDrive.tankDrive(scaleTrigger(1.0),
						 * scaleTrigger(1.0)); } else if (pad.checkDPad(1)) {
						 * talonDrive.tankDrive(scaleTrigger(1.0),
						 * scaleTrigger(0)); } else if (pad.checkDPad(2)) {
						 * talonDrive.tankDrive(scaleTrigger(1.0),
						 * scaleTrigger(-1.0)); } else if (pad.checkDPad(3)) {
						 * talonDrive.tankDrive(scaleTrigger(0),
						 * scaleTrigger(-1.0)); } else if (pad.checkDPad(4)) {
						 * talonDrive.tankDrive(scaleTrigger(-1.0),
						 * scaleTrigger(-1.0)); } else if (pad.checkDPad(5)) {
						 * talonDrive.tankDrive(scaleTrigger(-1.0),
						 * scaleTrigger(0)); } else if (pad.checkDPad(6)) {
						 * talonDrive.tankDrive(scaleTrigger(-1.0),
						 * scaleTrigger(1.0)); } else if (pad.checkDPad(7)) {
						 * talonDrive.tankDrive(scaleTrigger(0),
						 * scaleTrigger(1.0)); } else motorDrive(); //doArm();
						 * if (pad.getRightBumper()){ armMotor.set(-1); } else
						 * if (pad.getLeftBumper()){ armMotor.set(0.65); } else
						 * armMotor.set(0);
						 */
				motorDrive();
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
	private boolean winchRelease = false;

	private void doArm() {
		// encoderValue = armPositionEncoder.get();

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

			if (encoderValue < goalValue && !winchRelease)
				armMotor.set(MOVE_POWER);

			if (encoderValue > RELEASE_ARM_VALUE) {
				if (releaseWinch) {
					winchRelease = true;
					winchMotor.set(winchTrigger);
					// winchMotor.set(-winchTrigger);
				}
			}

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

	private boolean speedToggle = false;
	private boolean drivePressed;

	private boolean speedUpPressed = true;
	private boolean speedDownPressed = true;
	private boolean pUpPressed = true;
	private boolean pDownPressed = true;
	private boolean iUpPressed = true;
	private boolean iDownPressed = true;
	private boolean dUpPressed = true;
	private boolean dDownPressed = true;
	private boolean fUpPressed = true;
	private boolean fDownPressed = true;
	private double rpm = 60;
	private double p = 0.6;
	private double i = 0;
	private double d = 0;
	private double f = 0.534;

	private boolean stret = true;
	private boolean stretPressed = true;

	private void motorDrive() {
		// max 2 yd per sec = 72 in per sec
		// C = 1 rotation = 25.1327412287 in
		// rps = 2.86478897565
		// rpm = 171.887338539

		/*
		 * if (pad.getAButton() && !drivePressed) { drivePressed = true;
		 * speedToggle = !speedToggle; } if (!pad.getAButton()) drivePressed =
		 * false;
		 * 
		 * if (speedToggle) { talonDrive.speedTankDrive(6, 6, false);/*
		 * talonDrive.speedTankDrive(pad.getLeftAnalogY() * -1 *
		 * scaleTrigger(pad.getLeftTriggerValue()), pad.getRightAnalogY() *
		 * scaleTrigger(pad.getLeftTriggerValue())); } else {
		 * talonDrive.accelTankDrive(pad.getLeftAnalogY() *
		 * scaleTrigger(pad.getLeftTriggerValue()), pad.getRightAnalogY() *
		 * scaleTrigger(pad.getLeftTriggerValue())); }
		 */
		if (pad.getLeftBumper() && !drivePressed) {
			drivePressed = true;
			speedToggle = !speedToggle;
		}
		if (!pad.getLeftBumper())
			drivePressed = false;

		if (pad.getLeftAnalogButton() && pad.getRightAnalogButton() && !stretPressed) {
			stretPressed = true;
			stret = !stret;
		}
		if (!pad.getLeftAnalogButton() && !pad.getRightAnalogButton())
			stretPressed = false;

		if (speedToggle)
			talonDrive.driveStraight(pad.getLeftAnalogY() * -30);
		} else
			talonDrive.tankDrive(pad.getLeftAnalogY() * scaleTrigger(pad.getLeftTriggerValue()) * -1,
					pad.getRightAnalogY() * scaleTrigger(pad.getLeftTriggerValue()));
		// dash.putBoolean("speedtoggle", speedToggle);
	}

	private double scaleTrigger(double trigger) {
		return Math.min(1.0, 1.0 - 0.9 * trigger);
	}

	/**
	 * Runs during test mode
	 */
	public void test() {
// RPM
		if (pad.getBackButton() && !speedUpPressed) {
			speedUpPressed = true;
			rpm += 1;
		}
		if (!pad.getBackButton())
			speedUpPressed = false;
		
		if (pad.getStartButton() && !speedDownPressed) {
			speedDownPressed = true;
			rpm -= 1;
		}
		if (!pad.getStartButton())
			speedDownPressed = false;
		
// P value
		if (pad.getDPad() == 0 && !pUpPressed) {
			pUpPressed = true;
			p += 0.1;
		}
		if (!(pad.getDPad() == 0))
			pUpPressed = false;
		
		if (pad.getDPad() == 4 && !pDownPressed) {
			pDownPressed = true;
			p -= 0.1;
		}
		if (!(pad.getDPad() == 4))
			pDownPressed = false;
		
// I value
		if (pad.getDPad() == 2 && !iUpPressed) {
			iUpPressed = true;
			i += 0.1;
		}
		if (!(pad.getDPad() == 2))
			iUpPressed = false;
		
		if (pad.getDPad() == 6 && !iDownPressed) {
			iDownPressed = true;
			i -= 0.1;
		}
		if (!(pad.getDPad() == 6))
			iDownPressed = false;
		
// D value
		if (pad.getYButton() && !dUpPressed) {
			dUpPressed = true;
			d += 0.1;
		}
		if (!pad.getYButton())
			dUpPressed = false;
		
		if (pad.getAButton() && !dDownPressed) {
			dDownPressed = true;
			d -= 0.1;
		}
		if (!pad.getAButton())
			dDownPressed = false;
		
// F value
		if (pad.getXButton() && !fUpPressed) {
			fUpPressed = true;
			f += 0.01;
		}
		if (!pad.getXButton())
			fUpPressed = false;
		
		if (pad.getBButton() && !fDownPressed) {
			fDownPressed = true;
			f -= 0.01;
		}
		if (!pad.getBButton())
			fDownPressed = false;
		
		talonDrive.speedTankDrive(pad.getLeftAnalogY() * rpm * -1, pad.getRightAnalogY() * rpm * -1, false);
		talonDrive.setPID(p, i, d, f);
		
		dash.putNumber("RPM", rpm);
		dash.putNumber("P", p);
		dash.putNumber("I", i);
		dash.putNumber("D", d);
		dash.putNumber("F", f);
	}
}
