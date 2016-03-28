package org.oastem.frc.strong;

import org.oastem.frc.LogitechGamingPad;
import org.oastem.frc.control.DriveSystem;
import org.oastem.frc.sensor.ImageProcessing;
import org.oastem.frc.sensor.ImageProcessing.ProcessingType;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
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
	DriveSystem drive;
	LogitechGamingPad pad;
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	SendableChooser chooser;
	SmartDashboard dash;
	final int LEFT_FRONT = 0;
	final int LEFT_REAR = 1;
	final int RIGHT_FRONT = 3;
	final int RIGHT_REAR = 2;
	
	CameraServer camera;

	ImageProcessing process;

	public void robotInit() {
		drive = DriveSystem.getInstance();
		drive.initializeDrive(LEFT_FRONT, LEFT_REAR, RIGHT_FRONT, RIGHT_REAR);
		chooser = new SendableChooser();
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		pad = new LogitechGamingPad(0);

		dash = new SmartDashboard();
		dash.putData("Auto modes", chooser);
		
		camera = CameraServer.getInstance();
		camera.startAutomaticCapture("cam0");
		
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
		
		double [] center;
		double currSize;
		
		double cen = 0; //REPLACE
		double curr;
		
		boolean autotarget = false;
		
		
		while (isOperatorControl() && isEnabled()) {
			if (pad.getBackButton())
				autotarget = false;
			
			if (pad.getStartButton())
				autotarget = true;
			
			ArrayList <Double> size = process.getAreas();
			ArrayList<Double> targetX = process.getCenterX();
			curr = 0;
			currSize = 0;
			for(int x = 0; x < size.size() && x < targetX.size(); x++){
				if (size.get(x) > currSize){
					curr = targetX.get(x);
				}
			}
			dash.putNumber("size", currSize);
			dash.putNumber("centerx", curr);
			
			if (autotarget){
				if (curr == 0){
				}
				else if (curr < cen)
					drive.mecanumDrive(0, -0.5, 0, 0);
				else if (curr > cen)
					drive.mecanumDrive(0, 0.5, 0, 0);
			}
			else{
				drive.mecanumDrive(0, pad.getLeftAnalogX(), pad.getLeftAnalogY(), 0);
			}
			
			if (pad.getLeftBumper())
				drive.mecanumDrive(-1.0, 0, 0, 0);
			if (pad.getRightBumper())
				drive.mecanumDrive(1.0, 0, 0, 0);
			
			/* DOESN'T WORK
			double[][] finalPoints = process.getPolywhirl(process.getPoints(), 4);
			for(int x = 0; x < finalPoints.length; x++){
				dash.putNumber("GonPoint " + (x+1) + "X", finalPoints[x][0]);
				dash.putNumber("GonPoint " + (x+1) + "Y", finalPoints[x][1]);
				dash.putString("AYY", "LMAO");
			}*/
			
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
