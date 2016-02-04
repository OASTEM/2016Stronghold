package org.oastem.frc.strong;
import org.oastem.frc.sensor.ImageProcessingLines;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
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
    Joystick stickLeft;
    Joystick stickRight;
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    SendableChooser chooser;
    SmartDashboard swag;
    
    ImageProcessingLines process;
    
    
    public Robot() {
    	stickLeft = new Joystick(0);
    	stickLeft = new Joystick(1);
        process = new ImageProcessingLines("GRIP/myLinesReport");
    }
    
    public void robotInit() {
    	chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        
        swag = new SmartDashboard();
        SmartDashboard.putData("Auto modes", chooser);
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
        while (isOperatorControl() && isEnabled()) {
        	//double[] angles = process.getAngles();
        	//double[] lengths = process.getLengths();
        	double[][] points = process.getPoints();
        	
            for (int i = 0; i < points.length; i++){
            	swag.putString("Point " + (i + 1), points[i][0] + ", " + points[i][1]);
            }
            
        }
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
    
}
