package org.oastem.frc.sensor;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.util.ArrayList;

/**
 * @author mandarker
 */

public class ImageProcessingLines{
	
	private NetworkTable table;
	
	//This defaultValue is here in the case that there is nothing detected, so that there would be no error.
	private double[] defaultValue;
	
	/**
	 * Initializes a Network Table that retrieves line values from the GRIP image processor. Make sure
	 * GRIP is connected to the robot and "Publish LinesReport" is a connected operation in the
	 * operation flowchart.
	 * @param address The address for the Network Table that can be found in the Outline Viewer.
	 */
	public ImageProcessingLines(String address){
		table = NetworkTable.getTable(address);
        defaultValue = new double[0];
	}
	
	/**
	 * Gets the angle of each line detected by the GRIP image processor.
	 * @return The angle of each line retrieved from the Network Table published by the "Publish LinesReport" operation.
	 */
	public double[] getAngles(){
		return table.getNumberArray("angle", defaultValue);
	}
	
	/**
	 * Gets the length of each line detected by the GRIP image processor.
	 * @return The length of each line retrieved from the Network Table published by the "Publish LinesReport" operation.
	 */
	public double[] getLengths(){
		return table.getNumberArray("length", defaultValue);
	}
	
	/**
	 * Gets the raw values of the two points creating each line, straight from the Network Table.
	 * @return The raw points of each line retrieved from the Network Table published by the "Publish LinesReport" operation.
	 */
	public double[][] getRawPoints(){
		double[][] pointValues = new double[4][0];
        pointValues[0] = table.getNumberArray("x1", defaultValue);
        pointValues[1] = table.getNumberArray("x2", defaultValue);
        pointValues[2] = table.getNumberArray("y1", defaultValue);
        pointValues[3] = table.getNumberArray("y2", defaultValue);
		return pointValues;
	}
	
	/**
	 * Gets the raw points and turns into a more manageable table, where the left column is the x value 
	 * and the right column is the y value.
	 * @return The points of each line retrieved from the Network Table published by the "Publish LinesReport" operation.
	 */
	public double[][] getPoints(){
		double[][] rawValues = getRawPoints();
		double[][] points = new double [rawValues[0].length * 2][2];
        
        for (int i = 0 ; i < rawValues[0].length; i++){
        	points[i][0] = rawValues[0][i];
        	points[i][1] = rawValues[2][i];
        	points[rawValues[0].length + i][0] = rawValues[1][i];
        	points[rawValues[0].length + i][1] = rawValues[3][i];
        }
        
		return points;
	}
	
	/**
	 * Gets the center of all the points based on the average x and y values.
	 * @return The center of all the points detected in the Network Table published by the "Publish LinesReport" operation.
	 */
	public double[] getAveCenter(){
		double [][] points = getPoints();
		
		int sumX = 0;
		int sumY = 0;
		
		for (int i = 0; i < points.length; i++){
			sumX += points[i][0];
			sumY += points[i][1];
		}
		
		double[] center = new double[2];
		
		center[0] = sumX / points.length;
		center[1] = sumY / points.length;
		
		return center;
	}
}