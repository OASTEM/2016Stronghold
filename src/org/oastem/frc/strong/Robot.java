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
	private final int DSOLENOID_PORT_FORWARD = 0;
	private final int DSOLENOID_PORT_REVERSE = 1;
	private final int SSOLENOID_PORT = 2;

	// Values
	private final int DRIVE_ENC_CODE_PER_REV = 2048;
	private final int DRIVE_WHEEL_DIAM = 6;
	private final int LEFT = 1;
	private final int RIGHT = 2;
	//private final int BACK_LEFT_CAN_DRIVE = 1;
	//private final int BACK_RIGHT_CAN_DRIVE = 3;
	private final double WHEEL_CIRCUMFERENCE = 8.0 * Math.PI;
	private final double MAX_SPEED = 72; // in inches
	private final double ROTATION_SCALE = (MAX_SPEED / WHEEL_CIRCUMFERENCE) * 60; // in
	
	//Arm States
	private final int TOP_STATE = 0; 
	private final int MIDDLE_TOP_STATE = 1;
	private final int MIDDLE_BOTTOM_STATE = 3;
	private final int BOTTOM_STATE = 4;
	private final int MANUAL_STATE = 5;
	private final int E_STOP_STATE = 6;
		private final int MAX_ARM_VALUE = 180; //for now
		private final int MID_TOP_ARM_VALUE = 135;
		private final int MID_BOTTOM_ARM_VALUE = 45;
		private final int MIN_ARM_VALUE = 0; //for now
	
	private static double joyScale = 1.0;
    DriveSystem myRobot = DriveSystem.getInstance();
    TalonDriveSystem talonDrive = TalonDriveSystem.getInstance();
    //Joystick stickLeft;
    //Joystick stickRight;
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
       
    public Robot() {
    	talonDrive.initializeTalonDrive(LEFT, RIGHT, DRIVE_ENC_CODE_PER_REV, DRIVE_WHEEL_DIAM);
    	//talonDrive.initializeTalonDrive(LEFT_CAN_DRIVE, RIGHT_CAN_DRIVE, DRIVE_ENC_CODE_PER_REV, DRIVE_WHEEL_DIAM);
        //stickLeft = new Joystick(0);
        //stickRight = new Joystick(1);
    	pad = new LogitechGamingPad(0);
    	armPositionEncoder = new QuadratureEncoder(0,1,2); // i dont know what i did FIX LATER i actually knew what i was doing
    	armMotor = new Talon(0);
        test = new CANTalon(0);
        test.changeControlMode(TalonControlMode.Speed);
        test.reverseSensor(true);
        test.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        test.configEncoderCodesPerRev(2048);
        test.enable();
        test.setP(0);
        test.setI(0);
        test.setD(0);
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
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	gyro.resetGyro();
    	int stateOfArm = BOTTOM_STATE; 
        boolean isManualState = false;
    	int goalValue = 0;
        while (isOperatorControl() && isEnabled()) {
            //myRobot.arcadeDrive(stickLeft.getY(), stickLeft.getX()); // drive with arcade style (use right stick)
            //myRobot.tankDrive(stickLeft.getY(), stickRight.getY());
        	//doArcadeDrive();
        	//talonDrive.speedTankDrive(stickLeft.getY(), stickRight.getY(), false);
        	test.set(60);
        	dash.putNumber("Gyro Value:", gyro.getGyroAngle());
        	dash.putNumber("Accelerometer X Value: ", gyro.getAccelX());
        	dash.putNumber("Accelerometer Y Value: ", gyro.getAccelY());
        	dash.putNumber("Accelerometer Z Value: ", gyro.getAccelZ());
        	dash.putNumber("Built-In Accelerometer X Value: ", accel.getX());
        	dash.putNumber("Built-In Accelerometer Y Value: ", accel.getY());
        	dash.putNumber("Built-In Accelerometer Z Value: ", accel.getZ()-1);
        	
        	int encoderValue = armPositionEncoder.get();
        	//toggle button is the b button
        	if ( pad.getBButton() )
        		isManualState = !isManualState;
        	if (isManualState)
        		stateOfArm = MANUAL_STATE;
        	switch (stateOfArm)
        	{        		
        		case TOP_STATE :
        			goalValue = MAX_ARM_VALUE;
        			if ( encoderValue > goalValue)
        				//go down
        				armMotor.set(-.5);
	        		if (pad.getAButton())
	        			stateOfArm = MIDDLE_TOP_STATE;
	        		else if (pad.getBButton())
	        		{
	        			isManualState = !isManualState;
	        			stateOfArm = MANUAL_STATE;
	        		}
        			dash.putString("State: ", "top state");
        			break;
        		case MIDDLE_TOP_STATE :
        			goalValue = MID_TOP_ARM_VALUE;
        			if ( encoderValue > goalValue)
        				//go down
        				armMotor.set(-.5);
        			else if (encoderValue < goalValue)
	        			//go up
        				armMotor.set(.5);
	        		if (pad.getYButton())
	        			stateOfArm = TOP_STATE;
	        		else if (pad.getAButton())
	        			stateOfArm = MIDDLE_BOTTOM_STATE;
	        		else if (pad.getBButton())
	        		{
	        			isManualState = !isManualState;
	        			stateOfArm = MANUAL_STATE;
	        		}
        			dash.putString("State: ", "mid-top state");
        			break;
        		case MIDDLE_BOTTOM_STATE :
        			goalValue = MID_BOTTOM_ARM_VALUE;
        			if ( encoderValue > goalValue)
        				armMotor.set(-.5);
        			else if (encoderValue < goalValue)
        				armMotor.set(.5);
	        		if (pad.getYButton())
	        			stateOfArm = MIDDLE_TOP_STATE;
	        		else if (pad.getAButton())
	        			stateOfArm = BOTTOM_STATE;
	        		else if (pad.getBButton())
	        		{
	        			isManualState = !isManualState;
	        			stateOfArm = MANUAL_STATE;
	        		}
        			dash.putString("State: ", "mid-bottom");
        			break;
        		case BOTTOM_STATE :
        			goalValue = MIN_ARM_VALUE;
        			if ( encoderValue > goalValue)
        				armMotor.set(-.5);
        			else if (encoderValue < goalValue)
        				armMotor.set(.5);
	        		if (pad.getYButton())
	        			stateOfArm = MIDDLE_BOTTOM_STATE;
	        		else if (pad.getBButton())
	        		{
	        			isManualState = !isManualState;
	        			stateOfArm = MANUAL_STATE;
	        		}
        			dash.putString("State: ", "bottom");
        			break;
        		case MANUAL_STATE :
        			if(pad.getYButton() && encoderValue < MAX_ARM_VALUE) 
        				armMotor.set(.75);
        			else if (pad.getAButton() && encoderValue > MIN_ARM_VALUE ) 
        				armMotor.set(-.75);
        			if (pad.getBButton())
	        		{
	        			isManualState = !isManualState;
	        			//what would stateOfArm be?
	        		}
        			break;
        		case E_STOP_STATE :
        			armMotor.set(0);
        			//stop everything....but what is everything??
        			break;
        	}
        }
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
}
