package org.oastem.frc.sensor;

import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

public class FRCGyroAccelerometer {
	private static final double DRIFT_PER_SECOND = .0161803398875;// 0.333/60; 
	private long lastUpdateTime = 0;
	private double gyroAverage = 0;

	private ADXRS450_Gyro gyro;
	private ADXL362 accel;

	public FRCGyroAccelerometer() {
		gyro = new ADXRS450_Gyro();
		accel = new ADXL362(Accelerometer.Range.k8G);
		gyro.calibrate();
		lastUpdateTime = System.currentTimeMillis();
	}

	public void resetGyro() {
		gyro.reset();
		gyroAverage = 0;
		lastUpdateTime = System.currentTimeMillis();
	}

	public double getGyroAngle() {
		long currentTime = System.currentTimeMillis();

		double value = gyro.getAngle()- DRIFT_PER_SECOND * (currentTime - lastUpdateTime) / 1000.0;
		
		return value;// averageGyroValue(value);
	}
	
	public double getAccelX()
	{
		return accel.getX();
	}
	
	public double getAccelY()
	{
		return accel.getY();
	}
	
	public double getAccelZ()
	{
		return accel.getZ();
	}
	
	public void freeAccel()
	{
		accel.free();
	}
	/*private double averageGyroValue (double gyroValue)
	{
		
	}*/

}