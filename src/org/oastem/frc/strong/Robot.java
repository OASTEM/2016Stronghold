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
		double[][] gg;
		ArrayList<ArrayList<Double>> points;
		int mismatchCount = 0;
		int i = 0;
		double[] shit = new double[5];

		gg = new double[4][2];
		gg[0][0] = 2;
		gg[0][1] = 0;
		gg[1][0] = 0;
		gg[1][1] = 4;
		gg[2][0] = 4;
		gg[2][1] = 4;
		gg[3][0] = 4;
		gg[3][1] = 0;

		dash.putString("list", shit[0] + "" + shit[1] + "" + shit[2]);
		
		ArrayList<ArrayList<Double>> originPoints = new ArrayList<ArrayList<Double>>();
	
		ArrayList<Double> xCoor = new ArrayList<Double>();
		ArrayList<Double> yCoor = new ArrayList<Double>();
		xCoor.add(0.0);
		yCoor.add(0.0);	
		xCoor.add(0.0);
		yCoor.add(4.0);
		xCoor.add(4.0);
		yCoor.add(0.0);
		xCoor.add(4.0);
		yCoor.add(4.0);
		xCoor.add(2.0);
		yCoor.add(2.0);
		
		originPoints.add(xCoor);
		originPoints.add(yCoor);
		
		double[][] finalPoints = process.getPolywhirl(originPoints, 4);
		
		int yolo = 0;
		while (isOperatorControl() && isEnabled()) {
			
			//originPoints = process.getPoints();
			/*double[][] finalPoints = process.getPolywhirl(originPoints, 4);
			for(int x = 0; x < finalPoints.length; x++){
				dash.putNumber("Point " + (x+1) + "X", finalPoints[x][0]);
				dash.putNumber("Point " + (x+1) + "Y", finalPoints[x][1]);
			}*/
			
			dash.putNumber("what", yolo++);
			
		}
	}

	public void print(double[] set, String key) {
		for (int i = 0; i < set.length; i++)
			dash.putNumber(key + " " + (i + 1) + ":", set[i]);
	}

	public void print2d(double[][] set, String key) {
		for (int i = 0; i < set.length; i++) {
			for (int j = 0; j < set[i].length; j++) {
				dash.putNumber(i + ", " + j, set[i][j]);
			}
		}
	}

	public void print(ArrayList<Double> set, String key) {
		for (int i = 0; i < set.size(); i++)
			dash.putNumber(key + " " + (i + 1) + ":", set.get(i));
	}

	public void print2d(ArrayList<ArrayList<Double>> set, String key) {
		for (int i = 0; i < set.size(); i++) {
			for (int j = 0; j < set.get(i).size(); j++) {
				dash.putNumber(i + ", " + j, set.get(i).get(j));
			}
		}
	}

	/**
	 * Runs during test mode
	 */
	public void test() {
	}

}
