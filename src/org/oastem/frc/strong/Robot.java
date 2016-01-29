package org.oastem.frc.strong;
import org.oastem.frc.control.DriveSystem;
import org.oastem.frc.sensor.ADXL345Accelerometer;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
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
    private ADXL345Accelerometer acc;
    private PowerDistributionPanel pdp;
    private static double xAverage = 0.0;
    private static double yAverage = 0.0;
    private static double zAverage = 0.0;
    private static double[] accAverage2 = new double[50];
    private SmartDashboard dash;

    
    public Robot() {
    	myRobot.initializeDrive(FRONT_LEFT_DRIVE, BACK_LEFT_DRIVE, FRONT_RIGHT_DRIVE, BACK_RIGHT_DRIVE); //WE ARE SMART
        stickLeft = new Joystick(0);
        stickRight = new Joystick(1);
        pdp = new PowerDistributionPanel();
        dash = new SmartDashboard();
    }
    
    public void robotInit() {
    	chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto modes", chooser);
        acc = new ADXL345Accelerometer(I2C.Port.kOnboard);
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
       	pdp.clearStickyFaults();
        while (isOperatorControl() && isEnabled()) {
            //myRobot.arcadeDrive(stickLeft.getY(), stickLeft.getX()); // drive with arcade style (use right stick)
            //myRobot.tankDrive(stickLeft.getY(), stickRight.getY());
        	doArcadeDrive();
        	
        	dash.putNumber("X", acc.getX());
        	dash.putNumber("Y", acc.getY());
        	dash.putNumber("Z", acc.getZ());
        	
        	dash.putNumber("X Average", averageXValue(acc.getX())); 
        	dash.putNumber("Y Average", averageXValue(acc.getY()));
        	dash.putNumber("Z Average", averageXValue(acc.getZ())); //Vertical Axis Rotation
        	
        }
    }
    
    
    
    /**
     * Runs during test mode
     */
    public void test() {
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
	
	private double averageXValue(double xValue){
		xAverage = (xAverage * 99 + xValue) / 100;
		return xAverage;
	}
	
	private double averageYValue(double yValue){
		yAverage = (yAverage * 99 + yValue) / 100;
		return yAverage;
	}
	
	private double averageZValue(double zValue){
		zAverage = (zAverage * 99 + zValue) / 100;
		return zAverage;
	}
}
