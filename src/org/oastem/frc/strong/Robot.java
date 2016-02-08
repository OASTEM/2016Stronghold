package org.oastem.frc.strong;

import org.oastem.frc.sensor.ImageProcessing;
import org.oastem.frc.sensor.ImageProcessing.ProcessingType;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;

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
	Joystick stickLeft;
	Joystick stickRight;
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	SendableChooser chooser;
	SmartDashboard dash;

	ImageProcessing process;

	public void robotInit() {
		chooser = new SendableChooser();
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);

		dash = new SmartDashboard();
		dash.putData("Auto modes", chooser);

		stickLeft = new Joystick(0);
		stickLeft = new Joystick(1);
		process = new ImageProcessing("GRIP/myContoursReport", "GRIP/myLinesReport", "GRIP/myBlobsReport");

	}

	public void operatorControl() {
		int kek = 0;
		double[][] lol;
		ArrayList<ArrayList<Double>> points;
		while (isOperatorControl() && isEnabled()) {
			// double[][] points = process.getPoints();
			// dash.putNumber("length", points.length);

			points = process.getRawPoints();
			ArrayList<Double> swag = process.getAngles();

			for (int i = 0; i < swag.size(); i++) {
				dash.putString("Angle " + (i + 1), swag.get(i) + "");
				// dash.putString("Angle " + (i + 1), "lol");
				// dash.putString("Point " + (i + 1), points[i][0] + ", " +
				// points[i][1]);

			}

			if (swag.size() > kek)
				kek = swag.size();
			else {
				for (int i = swag.size(); i < kek; i++)
					dash.putString("Angle " + (i + 1), "BASE IS NOT FOUND");
			}
			
			print2d(points, "points");

			
			// 182.2 inches
			// 15 ft 2 in

		}
	}

	public void print(double[] set, String key) {
		for (int i = 0; i < set.length; i++)
			dash.putNumber(key + " " + (i + 1) + ":", set[i]);
	}

	public void print2d(double[][] set, String key) {
		String ret = "";

		for (int i = 0; i < set.length; i++) {
			for (int j = 0; j < set[0].length; j++) {
				if (j != set[0].length - 1)
					ret += set[i][j] + ", ";
				else
					ret += set[i][j];
			}
			dash.putString(key + " " + (i + 1) + ":", ret);
		}
	}

	public void print(ArrayList<Double> set, String key) {
		for (int i = 0; i < set.size(); i++)
			dash.putNumber(key + " " + (i + 1) + ":", set.get(i));
	}

	public void print2d(ArrayList<ArrayList<Double>> set, String key) {
		String ret = "";

		for (int i = 0; i < set.size(); i++) {
			for (int j = 0; j < set.get(0).size(); j++) {
				if (j != set.get(0).size() - 1)
					ret += set.get(i).get(j) + ", ";
				else
					ret += set.get(i).get(j);
			}
			dash.putString(key + " " + (i + 1) + ":", ret);
		}
	}

	/**
	 * Runs during test mode
	 */
	public void test() {
	}

}
