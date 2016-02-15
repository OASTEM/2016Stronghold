package org.oastem.frc.strong;

import org.oastem.frc.LogitechGamingPad;
import org.oastem.frc.control.DriveSystem;
import org.oastem.frc.control.TalonDriveSystem;

import edu.wpi.first.wpilibj.CANTalon;
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
	//private final int BACK_LEFT_CAN_DRIVE = 1;
	//private final int BACK_RIGHT_CAN_DRIVE = 3;
	
	// Values
	private final int DRIVE_ENC_CODE_PER_REV = 2048;
	private final int DRIVE_WHEEL_DIAM = 8;
	private final double WHEEL_CIRCUMFERENCE = 8.0 * Math.PI;
	private final double MAX_SPEED = 72; // in inches
	private final double ROTATION_SCALE = (MAX_SPEED / WHEEL_CIRCUMFERENCE) * 60; // in
																					// rpm
	private boolean speedToggle;
	private boolean isPressed;

	// Objects
	private TalonDriveSystem talonDrive = TalonDriveSystem.getInstance();
	private SendableChooser chooser;
	//private FRCGyroAccelerometer gyro;
	private SmartDashboard dash;
	private BuiltInAccelerometer accel;
	private LogitechGamingPad pad;
	private PowerDistributionPanel pdp;
	private CANTalon left;
	private CANTalon right;

	// Strings
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";

	public Robot() {
		talonDrive.initializeTalonDrive(LEFT, RIGHT, DRIVE_ENC_CODE_PER_REV, DRIVE_WHEEL_DIAM);
	}

	public void robotInit() {
		chooser = new SendableChooser();
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto modes", chooser);
		dash = new SmartDashboard();
		//gyro = new FRCGyroAccelerometer();
		accel = new BuiltInAccelerometer();
		accel = new BuiltInAccelerometer(Accelerometer.Range.k4G);
		pad = new LogitechGamingPad(0);
		isPressed = false;
		speedToggle = false;
		pdp = new PowerDistributionPanel();
		

		pdp.clearStickyFaults();
	}

	/**
	 * Runs the motors.
	 */
	public void operatorControl() {

		int what = 0; // Spring insisted
		
		left = talonDrive.getBackLeftDrive();
		right = talonDrive.getBackRightDrive();
		while (isOperatorControl() && isEnabled()) {
			dash.putNumber("Ticks", what++);

			
			motorDrive();
			dash.putNumber("Left Y", pad.getLeftAnalogY());
			dash.putNumber("Right Y", pad.getRightAnalogY());
			dash.putBoolean("Speed Toggle", speedToggle);
			
		}
	}

	/**
	 * Runs during test mode1
	 */
	public void test() {
	}

	private double scaleTrigger(double trigger) {
		return Math.min(1.0, 0.8 - 0.5 * trigger);
	}

	private void motorDrive() {
		// max 2 yd per sec = 72 in per sec
		// C = 1 rotation = 25.1327412287 in
		// rps = 2.86478897565
		// rpm = 171.887338539
		
		
		if (speedToggle){
			talonDrive.speedTankDrive(pad.getLeftAnalogY() * -1, pad.getRightAnalogY() * -1,
					false);
		}
		else{
			talonDrive.fakeTankDrive(pad.getLeftAnalogY() * -1 * scaleTrigger(pad.getLeftTriggerValue()),
					pad.getRightAnalogY() * scaleTrigger(pad.getLeftTriggerValue()));
		}

		if (pad.getLeftBumper() && !isPressed) {
			isPressed = true;
			speedToggle = !speedToggle;
		}
		if (!pad.getLeftBumper())
			isPressed = false;
		
		if (pad.checkDPad(2))
			talonDrive.fakeTankDrive(0.5, 0.5);
		else if (pad.checkDPad(6)){
			talonDrive.fakeTankDrive(-0.5, -0.5);
		}

		/*dash.putNumber(talonDrive.getFrontLeftDrive().getSmartDashboardType(),
				talonDrive.getFrontLeftDrive().getOutputVoltage());
		dash.putNumber(talonDrive.getFrontRightDrive().getSmartDashboardType(),
				
				talonDrive.getFrontRightDrive().getOutputVoltage());
		dash.putNumber(talonDrive.getBackLeftDrive().getSmartDashboardType(),
				talonDrive.getBackLeftDrive().getOutputVoltage());
		dash.putNumber(talonDrive.getBackRightDrive().getSmartDashboardType(),
				talonDrive.getBackRightDrive().getOutputVoltage());
		
		if (talonDrive.getFrontLeftDrive().getOutputVoltage() > 12.0) {
			talonDrive.getFrontLeftDrive().setVoltageRampRate(0); // ???
		}

		if (talonDrive.getFrontRightDrive().getOutputVoltage() > 12.0) {
			talonDrive.getFrontRightDrive().setVoltageRampRate(0); // ???
		}

		if (talonDrive.getBackLeftDrive().getOutputVoltage() > 12.0) {
			talonDrive.getBackLeftDrive().setVoltageRampRate(0); // ???
		}

		if (talonDrive.getBackRightDrive().getOutputVoltage() > 12.0) {
			talonDrive.getBackRightDrive().setVoltageRampRate(0); // ???
		}
		*/
	}
	
	public void accelerate(){
		
	}
}
