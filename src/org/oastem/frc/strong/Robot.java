package org.oastem.frc.strong;

import org.oastem.frc.*;
import org.oastem.frc.control.*;
import org.oastem.frc.sensor.*;


import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANJaguar.JaguarControlMode;
import edu.wpi.first.wpilibj.CANJaguar.LimitMode;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;

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
	private final int ARM_CAN_PORT = 0;

	private final int ARM_ENC_A = 0;
	private final int ARM_ENC_B = 1;

	// Values
	private final int DRIVE_ENC_CODE_PER_REV = 2048;
	private final int ARM_ENC_CODE_PER_REV = 497 * 3; // ACCOUNTED FOR GEAR RATIO
	private final int DRIVE_WHEEL_DIAM = 8;
	private final double WHEEL_CIRCUMFERENCE = DRIVE_WHEEL_DIAM * Math.PI;
	private final double MAX_SPEED = 72; // in inches
	private final double ROTATION_SCALE = (MAX_SPEED / WHEEL_CIRCUMFERENCE) * 60;

	// Objects
	private TalonDriveSystem talonDrive = TalonDriveSystem.getInstance();
	private PowerDistributionPanel pdp;
	private LogitechGamingPad pad;

	private SmartDashboard dash;
	private SendableChooser autoSelect;
	private final String defaultAuto = "Test";
	private final String customAuto1 = "Low Terrain";
	private final String customAuto2 = "Other Terrain";
	private final String customAuto3 = "Portcullis";
	
	private FRCGyroAccelerometer gyro;
	private BuiltInAccelerometer accel;
	private CANJaguar armMotor;
	private CANTalon winchMotor;
	private DigitalInput auto1;
	private DigitalInput auto2;

	// Joystick commands

	private double slowTrigger;
	private double winchTrigger;
	private boolean armUpPressed;
	private boolean armDownPressed;
	private boolean manualButtonPressed;
	private boolean releaseWinchPressed;
	private boolean noScopePressed;
	private boolean speedPressed;
	private boolean eStop1Pressed;
	private boolean eStop2Pressed;
	

	public Robot() {
		talonDrive.initializeTalonDrive(FRONT_LEFT_CAN_DRIVE, BACK_LEFT_CAN_DRIVE, FRONT_RIGHT_CAN_DRIVE,
				BACK_RIGHT_CAN_DRIVE, DRIVE_ENC_CODE_PER_REV, DRIVE_WHEEL_DIAM, WHEEL_CIRCUMFERENCE);
	}

	public void robotInit() {
		dash = new SmartDashboard();
		gyro = new FRCGyroAccelerometer();
		talonDrive.calibrateGyro();
		armMotor = new CANJaguar(ARM_CAN_PORT);
		initArm();
		pad = new LogitechGamingPad(0);
		
		auto1 = new DigitalInput(AUTO_PORT_1);
		auto2 = new DigitalInput(AUTO_PORT_2);

		autoSelect = new SendableChooser();
		autoSelect.addDefault("Test", defaultAuto);
		autoSelect.addObject("Low Terrain", customAuto1);
		autoSelect.addObject("Other Terrain", customAuto2);
		autoSelect.addObject("Portcullis", customAuto3);
		
		SmartDashboard.putData("Auto modes", autoSelect);

		pdp = new PowerDistributionPanel();
		pdp.clearStickyFaults();
		
		drive = false;
		speedToggle = false;
	}
	
	private void initArm()
	{
		armMotor.free();
		armMotor = new CANJaguar(ARM_CAN_PORT);
		armMotor.setInverted(true);
		armMotor.setPositionMode(CANJaguar.kQuadEncoder, ARM_ENC_CODE_PER_REV, 1500, .002, 1000);
		//armMotor.configEncoderCodesPerRev(ARM_ENC_CODE_PER_REV * 3);
		armMotor.configForwardLimit(MAX_ARM_VALUE);
		armMotor.configLimitMode(LimitMode.SoftPositionLimits);
		armMotor.enableControl(0);
	}
	
	
	
	private double getAngle()
	{
		if (armMotor.getControlMode() == JaguarControlMode.Position)
		{
			return armMotor.getPosition() * 360;
		}
		return 0;
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
		}*/

		while (isAutonomous() && isEnabled()) {
			dash.putNumber("Autonomous Type:", autoMode);
			dash.putString("Autonomous State:", state);
			
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
			releaseWinchPressed = pad.getYButton();
			noScopePressed = pad.getXButton();
			speedPressed = pad.getAButton();
			manualButtonPressed = pad.getBButton();
			eStop1Pressed = pad.getBackButton();
			eStop2Pressed = pad.getStartButton();

			if (eStop1Pressed && eStop2Pressed)
				stop = true;

			// "Arcade" Drive

			if (!stop) {
				if (pad.checkDPad(0)) {
					talonDrive.faketankDrive(scaleTrigger(1.0), scaleTrigger(1.0));
				} else if (pad.checkDPad(1)) {
					talonDrive.faketankDrive(scaleTrigger(1.0), scaleTrigger(0));
				} else if (pad.checkDPad(2)) {
					talonDrive.faketankDrive(scaleTrigger(1.0), scaleTrigger(-1.0));
				} else if (pad.checkDPad(3)) {
					talonDrive.faketankDrive(scaleTrigger(0), scaleTrigger(-1.0));
				} else if (pad.checkDPad(4)) {
					talonDrive.faketankDrive(scaleTrigger(-1.0), scaleTrigger(-1.0));
				} else if (pad.checkDPad(5)) {
					talonDrive.faketankDrive(scaleTrigger(-1.0), scaleTrigger(0));
				} else if (pad.checkDPad(6)) {
					talonDrive.faketankDrive(scaleTrigger(-1.0), scaleTrigger(1.0));
				} else if (pad.checkDPad(7)) {
					talonDrive.faketankDrive(scaleTrigger(0), scaleTrigger(1.0));
				} else
					motorDrive();
				doArm();
				
				/*
				if (armUpPressed){
					armMotor.set(1);
				}
				else if (armDownPressed){
					armMotor.set(-0.65);
				}
				else
					armMotor.set(0);
					*/
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
	private long checkTime = 0L;
	private double checkAngle = 0;
	private double currAngle = 0;
	private int goalValue;


	private boolean calibrateArm()
	{
		armMotor.set(-.25);
		if (currTime - checkTime >= 250) // check every .25 seconds
		{
			if (checkAngle - currAngle <= 2)
			{
				armMotor.set(0);
				armMotor.free();
				armMotor = new CANJaguar(ARM_CAN_PORT);
				initArm();
				calibrateStarting = true;
				return true;
			}
			checkTime = currTime;
			checkAngle = currAngle;
		}
		return false;
	}
	
	private void doArm() {
		currTime = System.currentTimeMillis();
		currAngle = getAngle(); // accounted for gear ratio of arm

		if (manualButtonPressed && !manPressed) {
			manPressed = true;
			isManualState = !isManualState;
			if (isManualState)
			{
				armMotor.setPercentMode(CANJaguar.kQuadEncoder, ARM_ENC_CODE_PER_REV);
				armMotor.enableControl(armMotor.getPosition());
				stateOfArm = MANUAL_STATE;
			}
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
		if (!releaseWinchPressed)
			releasePressed = false;

		switch (stateOfArm) {
		case CALIBRATE_STATE:
			if (calibrateStarting)
			{
				checkTime = currTime;
				checkAngle = currAngle;
				armMotor.setPercentMode(CANJaguar.kQuadEncoder, ARM_ENC_CODE_PER_REV);
				armMotor.enableControl(armMotor.getPosition());
				calibrateStarting = false;
			}
			if(calibrateArm())
				stateOfArm = BOTTOM_STATE;
			break;
		case RELEASE_STATE:
			prevState = RELEASE_STATE;
			goalValue = RELEASE_ARM_VALUE;

			if (currAngle < goalValue && !winchRelease)
				armMotor.set(MOVE_POWER);

			if (currAngle > RELEASE_ARM_VALUE) {
				if (releaseWinchPressed) {
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

			armMotor.set(goalValue);
			
			if (pad.getLeftBumper())
				stateOfArm = MIDDLE_STATE;
			dash.putString("State: ", "top state");
			break;
		case MIDDLE_STATE:
			prevState = MIDDLE_STATE;
			goalValue = MID_ARM_VALUE;

			armMotor.set(goalValue);
			
			if (pad.getRightBumper())
				stateOfArm = TOP_STATE;
			else if (pad.getLeftBumper())
				stateOfArm = MIDDLE_STATE;
			dash.putString("State: ", "middle state");
			break;
		case BOTTOM_STATE:
			prevState = BOTTOM_STATE;
			goalValue = MIN_ARM_VALUE;
		
			armMotor.set(goalValue);
			
			if (pad.getRightBumper())
				stateOfArm = MIDDLE_STATE;
			dash.putString("State: ", "bottom");
			break;
		case MANUAL_STATE:
			if (pad.getRightBumper())// && encoderValue < MAX_ARM_VALUE)
				armMotor.set(ARM_MAN_POWER);
			else if (pad.getLeftBumper())// && encoderValue > MIN_ARM_VALUE)
				armMotor.set(-ARM_MAN_POWER);
			else
				armMotor.set(0);
			dash.putString("State: ", "EMANUEL");
			break;
		}
	}

	private boolean speedToggle = false;
	private boolean drive;



	private void motorDrive() {
		// max 2 yd per sec = 72 in per sec
		// C = 1 rotation = 25.1327412287 in 
		// rps = 2.86478897565
		// rpm = 171.887338539
		
		if (speedPressed && !drive) {
			drive = true;
			speedToggle = !speedToggle;
		}
		if (!speedPressed)
			drive = false;

		if (speedToggle){
			talonDrive.driveStraight(pad.getLeftAnalogY() * -30);
		} else
			talonDrive.faketankDrive(pad.getLeftAnalogY() * scaleTrigger(pad.getLeftTriggerValue()),
					pad.getRightAnalogY() * scaleTrigger(pad.getLeftTriggerValue()));
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
