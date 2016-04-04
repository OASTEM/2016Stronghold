package org.oastem.frc.strong;

import org.oastem.frc.*;
import org.oastem.frc.control.*;
import org.oastem.frc.sensor.*;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANJaguar.LimitMode;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;

import org.oastem.frc.sensor.FRCGyroAccelerometer;

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
	private final int ASSIST_LIMIT_PORT = 0;
	private final int ARM_LIMIT_PORT = 1;
	private final int ARM_CAN_PORT = 3;
	private final int ARM_PWM_PORT = 2;
	private final int WINCH_PORT = 1;
	private final int ARM_ASSIST_PORT = 0;
	private final int ARM_ENC_A = 2;
	private final int ARM_ENC_B = 3;

	// Values
	private final int DRIVE_ENC_CODE_PER_REV = 2048;
	private final int ARM_ENC_CODE_PER_REV = 497 * 3; // ACCOUNTED FOR GEAR
														// RATIO
	private final int DRIVE_WHEEL_DIAM = 8;
	private final double WHEEL_CIRCUMFERENCE = DRIVE_WHEEL_DIAM * Math.PI;
	private final double MAX_SPEED = 72; // in inches
	private final double ROTATION_SCALE = (MAX_SPEED / WHEEL_CIRCUMFERENCE) * 60;

	// Objects
	private TalonDriveSystem talonDrive = TalonDriveSystem.getInstance();
	private PowerDistributionPanel pdp;
	private LogitechGamingPad pad;
	private LogitechGamingPad padSupport;

	private SmartDashboard dash;
	private SendableChooser autoSelect;
	private final String defaultAuto = "Test";
	private final String customAuto1 = "Low Bar";
	private final String customAuto2 = "Moat";
	private final String customAuto3 = "Portcullis";

	private FRCGyroAccelerometer gyro;
	// private BuiltInAccelerometer accel;
	private CANJaguar armMotor;
	private Talon armPWM;
	private Talon winchMotor;
	private Talon armAssistMotor;
	private DigitalInput auto1;
	private DigitalInput auto2;
	private DigitalInput armAssistLimit;
	private DigitalInput armLimit;
	private CameraServer camera;
	private QuadratureEncoder armEnc;
	private Timer time;

	// Joystick commands

	private double slowTrigger;
	private double winchTrigger;
	private boolean armUpButtonPressed;
	private boolean armDownButtonPressed;
	private boolean manualButtonPressed;
	private boolean releaseWinchButtonPressed;
	private boolean activateAssistButtonPressed;
	private boolean speedButtonPressed;
	private boolean straightPressed1;
	private boolean straightPressed2;
	private boolean eStop1Pressed;
	private boolean eStop2Pressed;
	private boolean armPressed = false;

	public Robot() {
		talonDrive.initializeTalonDrive(FRONT_LEFT_CAN_DRIVE, BACK_LEFT_CAN_DRIVE, FRONT_RIGHT_CAN_DRIVE,
				BACK_RIGHT_CAN_DRIVE, DRIVE_ENC_CODE_PER_REV, DRIVE_WHEEL_DIAM, WHEEL_CIRCUMFERENCE);
	}

	public void robotInit() {
		dash = new SmartDashboard();
		gyro = new FRCGyroAccelerometer();
		talonDrive.calibrateGyro();
		// armMotor = new CANJaguar(ARM_CAN_PORT);
		// initArm();
		armPWM = new Talon(ARM_PWM_PORT);
		armPWM.setInverted(true);
		winchMotor = new Talon(WINCH_PORT);
		winchMotor.setInverted(true);
		armAssistMotor = new Talon(ARM_ASSIST_PORT);
		armAssistMotor.setInverted(true);
		pad = new LogitechGamingPad(0);
		padSupport = new LogitechGamingPad(1);

		armEnc = new QuadratureEncoder(ARM_ENC_A, ARM_ENC_B, true, ARM_ENC_CODE_PER_REV);
		armEnc.setDistancePerPulse(360);

		armAssistLimit = new DigitalInput(ASSIST_LIMIT_PORT);
		armLimit = new DigitalInput(ARM_LIMIT_PORT);

		autoSelect = new SendableChooser();
		autoSelect.addDefault(DO_EVERYTHING, DO_EVERYTHING);
		autoSelect.addObject(DRIVE_ONLY, DRIVE_ONLY);
		autoSelect.addObject(DO_NOTHING, DO_NOTHING);
		SmartDashboard.putData("Auto modes", autoSelect);

		camera = CameraServer.getInstance();
		camera.startAutomaticCapture();

		time = new Timer();

		// pdp = new PowerDistributionPanel();
		// pdp.clearStickyFaults();

		drive = false;
		speedToggle = false;
	}

	private void initArm() {
		armMotor.free();
		armMotor = new CANJaguar(ARM_CAN_PORT);
		armMotor.setPositionMode(CANJaguar.kQuadEncoder, ARM_ENC_CODE_PER_REV, 1500, .002, 1000);
		armMotor.enableControl(0);
		armMotor.free();
		armMotor = new CANJaguar(ARM_CAN_PORT);
		armMotor.setInverted(true);
		armMotor.setPercentMode(CANJaguar.kQuadEncoder, ARM_ENC_CODE_PER_REV);
		// armMotor.configEncoderCodesPerRev(ARM_ENC_CODE_PER_REV * 3);
		// armMotor.configForwardLimit(MAX_ARM_VALUE/360);
		// armMotor.configLimitMode(LimitMode.SoftPositionLimits);
		armMotor.enableControl(0);
	}

	// AUTONOMOUS MODES

	private static final String DO_EVERYTHING = "Drive and arm";
	private static final String DRIVE_ONLY = "Only Drive";
	private static final String DO_NOTHING = "Do Nothing";
	private static final int TEST = 3;

	private static final double DEF_THRESH = 0.1;

	private boolean startCross = false;
	private boolean onTerrain = false;
	private boolean crossed = false;

	public void autonomous() {
		crossed = false;
		time.start();
		dash.putNumber("Timer", time.get());
		gyro.resetGyro();
		
		String autoSelected = (String) autoSelect.getSelected();
		dash.putString("Autonomous Mode", autoSelected);
		
		
		switch (autoSelected)
		{
		case DO_EVERYTHING:
			while (!calibrateArm()) {
				dash.putBoolean("Calibrating Arm", true);
				dash.putNumber("Timer", time.get());
			}
			dash.putBoolean("Calibrating Arm", false);
			
			boolean encCheck = true;
			double encCheckTime = time.get();
			
			while (time.get() < 15 && encCheck) {
				dash.putNumber("Gyro Value:", gyro.getGyroAngle());
				setArm(30);
				if (getAngle() < 20 && time.get() - encCheckTime > 2){
					encCheck = false;
					armMotor.set(0);
			 		talonDrive.faketankDrive(0, 0);
				}
				System.out.println(crossed);
				dash.putNumber("Timer", time.get());
				if (!crossed)
					talonDrive.driveStraight(100);
				else
					talonDrive.faketankDrive(0, 0);
				if (time.get() > 10)
				{
					/***** ACCELEROMETER CHECK *******/
					crossed = true;
				}
			}
			armPWM.set(0);
			talonDrive.faketankDrive(0, 0);
			break;
		case DRIVE_ONLY:
			while (time.get() < 4)
			{
				talonDrive.driveStraight(120);
			}
			talonDrive.faketankDrive(0, 0);
			break;
		case DO_NOTHING:
			System.out.println("Looking pretty");
			break;
		}
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		stateOfArm = CALIBRATE_STATE;
		startTeleopTime = System.currentTimeMillis();
		boolean stop = false;
		boolean armStop = false;
		released = false;

		gyro.resetGyro();
		int what = 0; // Spring insisted
		while (isOperatorControl() && isEnabled()) {
			dash.putNumber("Ticks", what++);
			dash.putBoolean("Speed Toggle", speedToggle);
			dash.putNumber("Gyro Value:", gyro.getGyroAngle());
			dash.putNumber("Accelerometer X Value: ", gyro.getAccelX());
			dash.putNumber("Accelerometer Y Value: ", gyro.getAccelY());
			dash.putNumber("Accelerometer Z Value: ", gyro.getAccelZ());

			slowTrigger = pad.getLeftTriggerValue();
			winchTrigger = pad.getRightTriggerValue();

			/*
			 * if (!isManualState && !isPortcullisState) { if (!armPressed) {
			 * armUpButtonPressed = pad.getRightBumper(); armDownButtonPressed =
			 * pad.getLeftBumper(); if (armUpButtonPressed ||
			 * armDownButtonPressed) armPressed = true; } else {
			 * armUpButtonPressed = false; armDownButtonPressed = false; } if
			 * (!pad.getRightBumper() && !pad.getLeftBumper()) { armPressed =
			 * false; } } else {
			 */
			armUpButtonPressed = pad.getRightBumper() || padSupport.getRightBumper();
			armDownButtonPressed = pad.getLeftBumper() || padSupport.getLeftBumper();
			
			
			// }

			releaseWinchButtonPressed = pad.getYButton() || padSupport.getYButton();
			activateAssistButtonPressed = pad.getXButton();
			speedButtonPressed = pad.getAButton();
			// manualButtonPressed = pad.getBButton(); //always in manual state
			eStop1Pressed = pad.getBackButton() || padSupport.getBackButton();
			eStop2Pressed = pad.getStartButton() || padSupport.getStartButton();

			if (eStop1Pressed && eStop2Pressed)
				stop = true;
			if (eStop1Pressed)
				armStop = true;

			// "Arcade" Drive

			if (!stop) {
				if (pad.checkDPad(0)) {
					talonDrive.driveStraight(70 * scaleTrigger(slowTrigger));
				} else if (pad.checkDPad(1)) {
					talonDrive.faketankDrive(scaleTrigger(1.0), scaleTrigger(0));
				} else if (pad.checkDPad(2)) {
					talonDrive.faketankDrive(scaleTrigger(1.0), -scaleTrigger(1.0));
				} else if (pad.checkDPad(3)) {
					talonDrive.faketankDrive(scaleTrigger(0), -scaleTrigger(1.0));
				} else if (pad.checkDPad(4)) {
					talonDrive.driveStraight(-60 * scaleTrigger(slowTrigger));
				} else if (pad.checkDPad(5)) {
					talonDrive.faketankDrive(-scaleTrigger(1.0), scaleTrigger(0));
				} else if (pad.checkDPad(6)) {
					talonDrive.faketankDrive(-scaleTrigger(1.0), scaleTrigger(1.0));
				} else if (pad.checkDPad(7)) {
					talonDrive.faketankDrive(scaleTrigger(0), scaleTrigger(1.0));
				} else
					motorDrive();

				if (!pad.checkDPad(0) || !pad.checkDPad(4))
					talonDrive.resetTick();

				/*
				 * if (armUpPressed){ armMotor.set(1); } else if
				 * (armDownPressed){ armMotor.set(-0.65); } else
				 * armMotor.set(0);
				 */
			}
			if (!armStop)
				doArm();
		}
	}

	// Arm States
	private final int TOP_STATE = 0;
	private final int MIDDLE_STATE = 1;
	private final int BOTTOM_STATE = 2;
	private final int PORTCULLIS_STATE = 3;
	private final int RELEASE_STATE = 5;
	private final int CALIBRATE_STATE = 6;
	private final int MANUAL_STATE = 8;

	private final int RELEASE_ARM_VALUE = 150; // for now
	private final int MAX_ARM_VALUE = 100; // for now
	private final int MID_ARM_VALUE = 90; // for now
	private final int MIN_ARM_VALUE = 0; // for now

	private final double MOVE_POWER = 0.5;
	private final double REST_BOT_POWER = 0.3;
	private final double REST_MID_POWER = 0.4;
	private final double REST_TOP_POWER = 0.3;
	private final double ARM_MAN_POWER = 0.5;
	private final double MAX_ARM_POWER = 1.0;
	private final double ASSIST_DOWN_POWER = 0.4;
	private final double ARM_RESET_POW = 0.25;

	private int THRESHOLD_VALUE = 5;
	private double CONSTANT_POWER = .0275; // for now

	private int stateOfArm = CALIBRATE_STATE;
	private boolean isManualState = false; // was originally this but
	// private boolean isManualState = true; // use this for testing
	private boolean isPortcullisState = false;
	private int prevState;
	private boolean manToggle = false;
	private boolean assistToggle = false;
	private boolean armAssistReset = false;
	private boolean armDown = false;
	private boolean released = false;
	private boolean releaseToggle = false;
	private boolean abortRelease = false;
	private boolean calibrateStarting = true;

	private long startTeleopTime = 0L;
	private long currTime = 0L;
	private long checkTime = 0L;
	private double checkAngle = 0;
	private double currAngle = 0;
	private int goalValue;

	private double getAngle() {
		// return armMotor.getPosition() * 360;
		return armEnc.getDistance();
	}

	private boolean calibrateArm() {

		// armMotor.set(-ARM_RESET_POW);
		armPWM.set(-ARM_RESET_POW);
		/*
		 * if (!armMotor.getReverseLimitOK()) { armMotor.set(0); return true;
		 * }//
		 */

		// if (currTime - checkTime >= 250) // check every .25 seconds
		// {
		if (armLimit.get()) {// !armMotor.getForwardLimitOK()) {
			// armMotor.set(0);
			armPWM.set(0);
			// initArm();
			armEnc.reset();
			calibrateStarting = true;
			return true;
		}
		checkTime = currTime;
		checkAngle = currAngle;
		// }

		return false;// */
	}

	private boolean setArm(double goalAngle) {
		dash.putNumber("Arm Angle:", getAngle());
		currAngle = getAngle();
		if (currAngle < (goalAngle - THRESHOLD_VALUE) && currAngle < MAX_ARM_VALUE)
			armPWM.set(ARM_MAN_POWER);
		else if (currAngle > (goalAngle + THRESHOLD_VALUE) && currAngle > MIN_ARM_VALUE)
			armPWM.set(-ARM_MAN_POWER);
		else {
			armPWM.set(0);
			return true;
		}
		return false;
	}

	private void doArm() {
		currTime = System.currentTimeMillis();
		currAngle = getAngle(); // accounted for gear ratio of arm
		dash.putNumber("Arm Angle:", currAngle);
		// dash.putBoolean("Arm Forward Limit", armMotor.getForwardLimitOK());
		// dash.putBoolean("Arm Reverse Limit", armMotor.getReverseLimitOK());
		dash.putBoolean("Arm limit", armLimit.get());
		dash.putBoolean("Arm released", released);

		/**** toggle EMANUAL state ****/
		/*
		 * if (manualButtonPressed && !manToggle) { manToggle = true;
		 * isManualState = !isManualState; if (isManualState) {
		 * changeArmToPercent(); stateOfArm = MANUAL_STATE; } else {
		 * changeArmToPosition(); stateOfArm = prevState; } } if
		 * (!manualButtonPressed) manToggle = false;
		 */

		/**** toggle PORTCULLIS state ****/
		if (activateAssistButtonPressed && !assistToggle) {
			assistToggle = true;
			isPortcullisState = !isPortcullisState;
			if (isPortcullisState) {
				armAssistReset = false;
				// changeArmToPercent();
				stateOfArm = PORTCULLIS_STATE;
			} else {
				armDown = false;
				armAssistReset = true;
				// changeArmToPosition();
				stateOfArm = prevState;
			}
		}
		if (!activateAssistButtonPressed)
			assistToggle = false;

		dash.putBoolean("Assit Arm Limit", armAssistLimit.get());
		/**** Resetting assist arm ****/
		if (armAssistReset && !armAssistLimit.get())
			armAssistMotor.set(-ARM_MAN_POWER);
		else if (!armDown) {
			armAssistReset = false;
			armAssistMotor.set(0);
		}

		/*** WINCH RELEASE ****/
		if (releaseWinchButtonPressed && !releaseToggle) {// && (currTime -
															// startTeleopTime
															// >= 115000)) {
			releaseToggle = true;
			stateOfArm = RELEASE_STATE;
			if (abortRelease && currAngle < RELEASE_ARM_VALUE) {
				stateOfArm = prevState;
				abortRelease = !abortRelease;
			}
		}
		if (!releaseWinchButtonPressed)
			releaseToggle = false;

		switch (stateOfArm) {
		case CALIBRATE_STATE:
			prevState = CALIBRATE_STATE;
			if (calibrateStarting) {
				checkTime = currTime;
				checkAngle = currAngle;
				// changeArmToPercent();
				calibrateStarting = false;
			} else if (calibrateArm())
				stateOfArm = MANUAL_STATE;
			dash.putString("State: ", "calibrate state");
			break;
		case RELEASE_STATE:
			goalValue = RELEASE_ARM_VALUE;
			dash.putBoolean("Arm released", released);

			if (!released)
				// armMotor.set(goalValue);
				// armMotor.set(ARM_MAN_POWER);
				armPWM.set(ARM_MAN_POWER);
			// */
			if (armUpButtonPressed && armDownButtonPressed)
				abortRelease = true;
			/**** TEST CODE BELOW ****/
			/*
			 * if (currTime - startTeleopTime >= 60000) currAngle = 200;//
			 */

			if (currAngle >= RELEASE_ARM_VALUE) {
				released = true;
				// changeArmToPercent();
			}
			if (released) {
				if (armUpButtonPressed)
					// armMotor.set(ARM_RESET_POW);
					armPWM.set(ARM_RESET_POW);
				else if (armDownButtonPressed)
					// armMotor.set(-ARM_RESET_POW);
					armPWM.set(-ARM_RESET_POW);
				else
					armPWM.set(0);
				winchMotor.set(winchTrigger - padSupport.getRightTriggerValue());

			}
			dash.putString("State: ", "release state");
			break;
		/*
		 * case TOP_STATE: prevState = TOP_STATE; goalValue = MAX_ARM_VALUE;
		 * 
		 * armMotor.set(goalValue/360);
		 * 
		 * if (armDownButtonPressed) stateOfArm = MIDDLE_STATE; dash.putString(
		 * "State: ", "top state"); break; case MIDDLE_STATE: prevState =
		 * MIDDLE_STATE; goalValue = MID_ARM_VALUE;
		 * 
		 * armMotor.set(goalValue/360);
		 * 
		 * if (armUpButtonPressed) stateOfArm = TOP_STATE; else if
		 * (armDownButtonPressed) stateOfArm = BOTTOM_STATE; dash.putString(
		 * "State: ", "middle state"); break; case BOTTOM_STATE: prevState =
		 * BOTTOM_STATE; goalValue = MIN_ARM_VALUE;
		 * 
		 * armMotor.set(goalValue/360);
		 * 
		 * if (armUpButtonPressed) stateOfArm = MIDDLE_STATE; dash.putString(
		 * "State: ", "bottom"); break;
		 */
		case PORTCULLIS_STATE:
			if (armDown) {
				if (armUpButtonPressed && getAngle() < MAX_ARM_VALUE) {
					// armMotor.set(MAX_ARM_POWER);
					armPWM.set(MAX_ARM_POWER);
					armAssistMotor.set(ARM_MAN_POWER);
				} else if (armDownButtonPressed) {
					// armMotor.set(-MAX_ARM_POWER);
					armPWM.set(-MAX_ARM_POWER);
					if (!armAssistLimit.get())
						armAssistMotor.set(-ASSIST_DOWN_POWER);
				} else {
					// armMotor.set(0);
					armPWM.set(0);
					armAssistMotor.set(0);
				}
			} else
				armDown();

			dash.putString("State: ", "Portcullis");
			break;
		case MANUAL_STATE:
			prevState = MANUAL_STATE;
			if (armUpButtonPressed && getAngle() < MAX_ARM_VALUE)
				// armMotor.set(ARM_MAN_POWER);
				armPWM.set(ARM_MAN_POWER);
			else if (armDownButtonPressed)// && getAngle() > MIN_ARM_VALUE)
				// armMotor.set(-ARM_MAN_POWER);
				armPWM.set(-ARM_MAN_POWER);
			else
				// armMotor.set(0);
				armPWM.set(0);
			// */
			dash.putString("State: ", "EMANUEL");
			break;
		}
	}

	private void armDown() {

		if (getAngle() < 3) {
			armDown = true;
			// armMotor.set(0);
			armPWM.set(0);
		} else
			// armMotor.set(-ARM_RESET_POW);
			armPWM.set(-ARM_RESET_POW);
		// */
	}

	/*
	 * private void changeArmToPercent() {
	 * 
	 * armMotor.setPercentMode(CANJaguar.kQuadEncoder, ARM_ENC_CODE_PER_REV);
	 * armMotor.enableControl(armMotor.getPosition()); dash.putString("Arm Mode"
	 * , "PERCENT"); } //
	 */

	/*
	 * private void changeArmToPosition() {
	 * 
	 * armMotor.setPositionMode(CANJaguar.kQuadEncoder, ARM_ENC_CODE_PER_REV,
	 * 1500, .002, 1000); armMotor.enableControl(armMotor.getPosition());
	 * dash.putString("Arm Mode", "POSITION"); } //
	 */

	private void changeArmToPercent() {
		double armAngle = armMotor.getPosition();
		armMotor.free();
		armMotor = new CANJaguar(ARM_CAN_PORT);
		armMotor.setInverted(true);
		armMotor.setPercentMode(CANJaguar.kQuadEncoder, ARM_ENC_CODE_PER_REV);
		// armMotor.configEncoderCodesPerRev(ARM_ENC_CODE_PER_REV * 3);
		armMotor.configReverseLimit(MAX_ARM_VALUE / 360);
		armMotor.configLimitMode(LimitMode.SoftPositionLimits);
		armMotor.enableControl(armAngle);
		dash.putString("Arm Mode", "PERCENT");
	}

	private void changeArmToPosition() {
		double armAngle = armMotor.getPosition();
		armMotor.free();
		armMotor = new CANJaguar(ARM_CAN_PORT);
		armMotor.setInverted(false);
		armMotor.setPositionMode(CANJaguar.kQuadEncoder, ARM_ENC_CODE_PER_REV, 1500, .002, 1000);
		// armMotor.configEncoderCodesPerRev(ARM_ENC_CODE_PER_REV * 3);
		armMotor.configReverseLimit(MAX_ARM_VALUE / 360);
		armMotor.configLimitMode(LimitMode.SoftPositionLimits);
		armMotor.enableControl(armAngle);
		dash.putString("Arm Mode", "POSITION");
	}

	private boolean speedToggle = false;
	private boolean drive = false;
	private boolean straightToggle = false;
	private boolean straight = false;

	private void motorDrive() {
		// max 2 yd per sec = 72 in per sec
		// C = 1 rotation = 25.1327412287 in
		// rps = 2.86478897565
		// rpm = 171.887338539

		if (speedButtonPressed && !drive && !straightToggle) {
			drive = true;
			speedToggle = !speedToggle;
		}
		if (!speedButtonPressed)
			drive = false;

		if (pad.getBButton() && !straight && !speedToggle) {
			straight = true;
			straightToggle = !straightToggle;
		}
		if (!pad.getBButton())
			straight = false;

		if (speedToggle)
			talonDrive.speedTankDrive(pad.getLeftAnalogY() * -30 * scaleTrigger(slowTrigger),
					pad.getRightAnalogY() * -30 * scaleTrigger(slowTrigger), false);
		if (straightToggle)
			talonDrive.driveStraight(20);
		else
			talonDrive.faketankDrive(pad.getLeftAnalogY() * -scaleTrigger(slowTrigger),
					pad.getRightAnalogY() * -scaleTrigger(slowTrigger));
		// dash.putBoolean("speedtoggle", speedToggle);
	}

	private double scaleTrigger(double trigger) {
		return Math.min(1.0, 1.0 - 0.9 * trigger);
	}

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


	
	/**
	 * Runs during test mode
	 */
	public void test() {
		while (isTest() && isEnabled())
		{
		if (pad.getRightBumper() || padSupport.getRightBumper())
			// armMotor.set(ARM_RESET_POW);
			armPWM.set(ARM_RESET_POW);
		else if (pad.getLeftBumper() || padSupport.getLeftBumper())
			// armMotor.set(-ARM_RESET_POW);
			armPWM.set(-ARM_RESET_POW);
		else
			armPWM.set(0);
		winchMotor.set(pad.getRightTriggerValue() - padSupport.getRightTriggerValue());
		}
		
		/********* PID FOR DRIVE ***********/
		/*
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
		//*/
	}
}
