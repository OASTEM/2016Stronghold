package org.oastem.frc.strong;
import org.oastem.frc.control.DriveSystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
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
    
    private NetworkTable table;
    
    public Robot() {
    	stickLeft = new Joystick(0);
    	stickLeft = new Joystick(1);
        table = NetworkTable.getTable("GRIP/myLinesReport");
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
        double[] defaultValue = new double[0];
        while (isOperatorControl() && isEnabled()) {
        	
            double[] angle = table.getNumberArray("angle", defaultValue);
            double[] length = table.getNumberArray("length", defaultValue);
            double[][] points = new double[4][0];
            
            points[0] = table.getNumberArray("x1", defaultValue);
            points[1] = table.getNumberArray("x2", defaultValue);
            points[2] = table.getNumberArray("y1", defaultValue);
            points[3] = table.getNumberArray("y2", defaultValue);
            
            for (int i = 0; i < points[0].length; i++){
            	swag.putNumber("Angle " + (i + 1) + ":", angle[i]);
            	swag.putString("Point " + (i + 1) + ":", points[0][i] + ", " + points[1][i] + ", " + points[2][i] + ", " + points[3][i] + ", ");
            	swag.putNumber("Length " + (i + 1) + ":", length[i]);
            }
            
            
            //swag.
        }
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
    
}
