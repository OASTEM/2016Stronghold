package org.oastem.frc.strong;
import org.oastem.frc.control.DriveSystem;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * This is a demo program showing the use of the RobotDrive class.
 * The SampleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
	private final int FRONT_LEFT_DRIVE = 1;
	private final int FRONT_RIGHT_DRIVE = 3;
	private final int BACK_LEFT_DRIVE = 0;
	private final int BACK_RIGHT_DRIVE = 2;
	private static double joyScale = 1.0;
    DriveSystem myRobot = DriveSystem.getInstance();
    Joystick stickLeft;
    Joystick stickRight;
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    SendableChooser chooser;
    // I COPIED THIS FROM GANTRYBOT
	private static final int RIGHT_ENC_A = 5;
	private static final int RIGHT_ENC_B = 3;
	private static final int LEFT_ENC_A = 2;
	private static final int LEFT_ENC_B = 0;
	private static final int DRIVE_ENC_CPR = 2048;
    
    public Robot() {
    	myRobot.initializeDrive(FRONT_LEFT_DRIVE, BACK_LEFT_DRIVE, FRONT_RIGHT_DRIVE, BACK_RIGHT_DRIVE);
        stickLeft = new Joystick(0);
        stickRight = new Joystick(1);
    }
    
    public void robotInit() {
    	myRobot.initializeEncoders(RIGHT_ENC_A, RIGHT_ENC_B, true, LEFT_ENC_A, LEFT_ENC_B, false, DRIVE_ENC_CPR);
    	chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto modes", chooser);
    }

    /**
     * Runs the motors with arcade steering.
     */
    @SuppressWarnings("deprecation")
	public void operatorControl() {
    	myRobot.resetEncoders();
        while (isOperatorControl() && isEnabled()) {
            //myRobot.arcadeDrive(stickLeft.getY(), stickLeft.getX()); // drive with arcade style (use right stick)
            //myRobot.tankDrive(stickLeft.getY(), stickRight.getY());
        	doArcadeDrive();
        	SmartDashboard.putDouble("Right Encoder", myRobot.getRightEnc());
        	SmartDashboard.putDouble("Left Encoder", myRobot.getLeftEnc());
        	SmartDashboard.putDouble("Right Encoder Rate", myRobot.getRateRightEnc());
        	SmartDashboard.putDouble("Left Encoder Rate", myRobot.getRateLeftEnc());
        	
        }
    }
    
    private void doArcadeDrive() {
		double leftMove = 0.0;
		double rightMove = 0.0;
		double zone = 0.04;

		joyScale = scaleZ(stickLeft.getZ());

		double x = stickLeft.getX();
		double y = stickLeft.getY() * -1;

		if (Math.abs(y) > zone) 
		{
			leftMove = y;
			rightMove = y;
		}

		if (Math.abs(x) > zone) 
		{
			leftMove = correct(leftMove + x);
			rightMove = correct(rightMove - x);
		}

		leftMove *= joyScale * -1;
		rightMove *= joyScale * -1;

		myRobot.tankDrive(leftMove, rightMove);
	}

	private double scaleZ(double rawZ) {
		return Math.min(1.0, 0.5 - 0.5 * rawZ);
	}

	private double correct(double val) {
		if (val > 1.0) {
			return 1.0;
		}
		if (val < -1.0) {
			return -1.0;
		}
		return val;
	}
	
	 /**
     * Runs during test mode
     */
    public void test() {
    }
}
