package org.oastem.frc.strong;
import org.oastem.frc.Dashboard;
import org.oastem.frc.control.DriveSystem;
<<<<<<< HEAD

=======
import org.oastem.frc.sensor.QuadratureEncoder;

import edu.wpi.first.wpilibj.DoubleSolenoid;
>>>>>>> 573434c0dbc95530834570f6dc33de4f36f4d24e
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
<<<<<<< HEAD
=======
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
>>>>>>> 573434c0dbc95530834570f6dc33de4f36f4d24e
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
	
	//MOTOR PORTS
	private final int FRONT_LEFT_DRIVE = 1;
	private final int FRONT_RIGHT_DRIVE = 3;
	private final int BACK_LEFT_DRIVE = 0;
	private final int BACK_RIGHT_DRIVE = 2;
	
	//Encoders
	public static final int RIGHT_ENC_A = 1; //UPDATE THIS
	public static final int RIGHT_ENC_B = 2; //UPDATE THIS
	public static final int LEFT_ENC_A = 3;  //UPDATE THIS
	public static final int LEFT_ENC_B = 4;  //UPDATE THIS
	
	//DECLARING OBJECTS
	private DoubleSolenoid ds;
	private Solenoid solenoid;
	private Dashboard dash;
	private DriveSystem myRobot;
	private Joystick stickLeft;
    private Joystick stickRight;
    private SendableChooser chooser;
    private QuadratureEncoder pEncoder;
	
	//JOYSTICK
	private static double joyScale = 1.0;
    
    
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
<<<<<<< HEAD
    SendableChooser chooser;
    // I COPIED THIS FROM GANTRYBOT
	private static final int RIGHT_ENC_A = 5;
	private static final int RIGHT_ENC_B = 3;
	private static final int LEFT_ENC_A = 2;
	private static final int LEFT_ENC_B = 0;
	private static final int DRIVE_ENC_CPR = 2048;
    
    public Robot() {
    	myRobot.initializeDrive(FRONT_LEFT_DRIVE, BACK_LEFT_DRIVE, FRONT_RIGHT_DRIVE, BACK_RIGHT_DRIVE);
=======
    private static final int DRIVE_ENC_CPR = 2048;
    

    
    public Robot() {
    	myRobot = DriveSystem.getInstance();
    	myRobot.initializeDrive(FRONT_LEFT_DRIVE, BACK_LEFT_DRIVE, FRONT_RIGHT_DRIVE, BACK_RIGHT_DRIVE);
    	//myRobot = new RobotDrive(FRONT_LEFT_DRIVE, BACK_LEFT_DRIVE, FRONT_RIGHT_DRIVE, BACK_RIGHT_DRIVE); //WE ARE SMART
    	//myRobot.initializeEncoders(RIGHT_ENC_A, RIGHT_ENC_B, false, LEFT_ENC_A, LEFT_ENC_B, false, DRIVE_ENC_CPR);
>>>>>>> 573434c0dbc95530834570f6dc33de4f36f4d24e
        stickLeft = new Joystick(0);
        stickRight = new Joystick(1);
        ds = new DoubleSolenoid(0,1); //CHANGE THIS LATER
        solenoid = new Solenoid(2);
        dash = new Dashboard();
    }
    
    public void robotInit() {
<<<<<<< HEAD
    	myRobot.initializeEncoders(RIGHT_ENC_A, RIGHT_ENC_B, true, LEFT_ENC_A, LEFT_ENC_B, false, DRIVE_ENC_CPR);
=======
    	myRobot = DriveSystem.getInstance();
>>>>>>> 573434c0dbc95530834570f6dc33de4f36f4d24e
    	chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto modes", chooser);
    }

    /**
     * Runs the motors with arcade steering.
     */
<<<<<<< HEAD
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
        	
=======
    public void operatorControl() {
    	String solenoidState = "Off";
    	while (isOperatorControl() && isEnabled()) {
            //myRobot.arcadeDrive(stickLeft.getY(), stickLeft.getX()); // drive with arcade style (use right stick)
            //myRobot.tankDrive(stickLeft.getY(), stickRight.getY());
        	doArcadeDrive();
        	
        	//Testing Pneumatics
        	/*if(stickLeft.getRawButton(3)){
        		ds.set(DoubleSolenoid.Value.kForward);
        	}
        	else if (stickLeft.getRawButton(4)){
        		ds.set(DoubleSolenoid.Value.kReverse);
        	}
        	else{
        		ds.set(DoubleSolenoid.Value.kOff);
        	}
        	
        	if (stickLeft.getRawButton(2))
        		solenoid.set(true);
        	else
        		solenoid.set(false);
        }*/
        
        	// Solenoids require pressure to work
        if (stickLeft.getRawButton(3)){
			ds.set(DoubleSolenoid.Value.kForward);
        	solenoidState = "On";
>>>>>>> 573434c0dbc95530834570f6dc33de4f36f4d24e
        }
		else if (ds.get().equals(DoubleSolenoid.Value.kReverse) || ds.get().equals(DoubleSolenoid.Value.kOff)){	
        	ds.set(DoubleSolenoid.Value.kOff);//Reverse
        	solenoidState = "Off";
		}
		else {
			ds.set(DoubleSolenoid.Value.kReverse);
			solenoidState = "Reverse";
		}
        
		if (stickLeft.getRawButton(4))
			solenoid.set(true);
		else
			solenoid.set(false);
      
    	
    	dash.putString("Pneumatics State", solenoidState);
    	}
    }
<<<<<<< HEAD
=======
    /**
     * Runs during test mode
     */
    public void test() {
    }
>>>>>>> 573434c0dbc95530834570f6dc33de4f36f4d24e
    
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
