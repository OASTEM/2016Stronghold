package org.oastem.frc.sensor;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.util.ArrayList;

/**
 * @author mandarker
 */

public class ImageProcessing {

	private NetworkTable table;

	// These enums allow much easier handling between the different
	// types of image processing.
	public enum ProcessingType {
		Lines, Contours, Blobs;
	}

	private String contours;
	private String lines;
	private String blobs;

	// This defaultValue is here in the case that there is nothing detected on
	// the Network Table, so that there would be no error.
	private double[] defaultValue;

	/**
	 * Initializes a Network Table that retrieves line values from the GRIP
	 * image processor. Make sure GRIP is deployed to the robot to reduce camera
	 * lag. Add empty Strings for the vision processing types (contours, lines,
	 * blobs) for those that are not published by GRIP onto the Network Table.
	 * 
	 * @param contours
	 *            The address for the Network Table of contours data published
	 *            by GRIP.
	 * @param lines
	 *            The address for the Network Table of lines data published by
	 *            GRIP.
	 * @param blobs
	 *            The address for the Network Table of blobs data published by
	 *            GRIP.
	 */
	public ImageProcessing(String contours, String lines, String blobs) {
		this.contours = contours;
		this.lines = lines;
		this.blobs = blobs;
		defaultValue = new double[0];
	}

	/**
	 * Switches between the three types of vision processing.
	 * 
	 * @param type
	 *            The enum of the type of vision processing intended to be used.
	 */
	public void switchType(ProcessingType type) {
		switch (type) {
		case Lines:
			table = NetworkTable.getTable(lines);
			break;
		case Contours:
			table = NetworkTable.getTable(contours);
			break;
		case Blobs:
			table = NetworkTable.getTable(blobs);
			break;
		}
	}

	/**
	 * Gets the angle of each line detected by the GRIP image processor.
	 * 
	 * @return An ArrayList of the angle of each line retrieved from the Network
	 *         Table published by the "Publish LinesReport" operation.
	 */
	public ArrayList<Double> getAngles() {
		switchType(ProcessingType.Lines);
		double[] current = table.getNumberArray("angle", defaultValue);

		return convert(current);
	}

	/**
	 * Gets the length of each line detected by the GRIP image processor.
	 * 
	 * @return An ArrayList of the length of each line retrieved from the
	 *         Network Table published by the "Publish LinesReport" operation.
	 */
	public ArrayList<Double> getLengths() {
		switchType(ProcessingType.Lines);
		double[] current = table.getNumberArray("length", defaultValue);

		return convert(current);
	}

	/**
	 * Gets the raw values of the two points creating each line, straight from
	 * the Network Table. SOMETIMES THE X VALUES DO NOT MATCH UP WITH THE Y
	 * VALUES WTF.
	 * 
	 * @return A 2d ArrayList of the points of each line retrieved straight from
	 *         the Network Table published by the "Publish LinesReport"
	 *         operation.
	 */
	public ArrayList<ArrayList<Double>> getRawPoints() {
		switchType(ProcessingType.Lines);
		double[][] pointValues = new double[4][0];
		pointValues[0] = table.getNumberArray("x1", defaultValue);
		pointValues[1] = table.getNumberArray("x2", defaultValue);
		pointValues[2] = table.getNumberArray("y1", defaultValue);
		pointValues[3] = table.getNumberArray("y2", defaultValue);

		double[][] actualPointValues = new double[4][Math.min(Math.min(pointValues[0].length, pointValues[1].length),
				Math.min(pointValues[2].length, pointValues[3].length))];

		for (int i = 0; i < actualPointValues.length; i++) {
			for (int j = 0; j < actualPointValues[i].length; j++) {
				actualPointValues[i][j] = pointValues[i][j];
			}
		}

		return convert(actualPointValues);
	}

	/**
	 * Gets the raw points and turns them into a more manageable 2d ArrayList,
	 * where the first ArrayList is the x value and the second ArrayList is the
	 * y value.
	 * 
	 * @return A 2d ArrayList of the points of each line retrieved from the
	 *         Network Table published by the "Publish LinesReport" operation.
	 */
	public ArrayList<ArrayList<Double>> getPoints() {
		switchType(ProcessingType.Lines);
		ArrayList<ArrayList<Double>> rawValues = getRawPoints();
		ArrayList<ArrayList<Double>> re = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();

		for (int i = 0; i < rawValues.get(0).size(); i++) {
			x.add(rawValues.get(0).get(i));
			x.add(rawValues.get(2).get(i));
			y.add(rawValues.get(1).get(i));
			y.add(rawValues.get(3).get(i));
		}

		re.add(x);
		re.add(y);

		return re;
	}

	/**
	 * Gets the largest polygon with intended sides created from a group of
	 * points.
	 * 
	 * @param points
	 *            A 2d ArrayList of a set of points, where the first ArrayList
	 *            is the x value and the second ArrayList is the y value.
	 * @param point
	 *            The amount of sides the intended polygon has.
	 * @return A 2d array of the set of points of the polygon with the largest
	 *         area, where the first column is the x value and the second column
	 *         is the y value.
	 */
	public double[][] getPolygon(ArrayList<ArrayList<Double>> points, int point) { //FIX THIS SHIT SPRING
		shape = new double[point][2];
		area = 0;
		this.point = point;
		this.points = points;
		
		largest = new double[point][2];
		
		for (int i = 0; i < points.get(0).size(); i++)
			permutation(i, point);

		for (int r = 0; r < largest.length; r++)
			System.out.println(largest[r][0] + "\t" + largest[r][1]);
		
		return largest;
	}

	// These are global variables used for the recursive function that finds the
	// polygon
	// with the largest area.
	private double[][] largest;
	private double[][] shape;
	private double area;
	private int point;
	private ArrayList<ArrayList<Double>> points;

	/**
	 * Creates all configurations of polygons recursively and finds the area of
	 * the largest polygon.
	 * 
	 * @param index
	 *            The index going to be used in the global shape array.
	 * @param number
	 *            The current amount of points inside the global shape array.
	 */
	private void permutation(int index, int number) {
		if (number == 0) {
			if (getArea(shape) > area){
				area = getArea(shape);				
				for (int r = 0; r < shape.length; r++)
				{
					System.out.println(shape[r][0] + "\t" + shape[r][1]);
					largest[r][0] = shape[r][0];
					largest[r][1] = shape[r][1];
				}
				System.out.println(area);
				//largest = new double[4][2];
			}
		} else {
			shape[point - number][0] = points.get(0).get(index);
			shape[point - number][1] = points.get(1).get(index);
			for (int i = 0; i < points.get(0).size(); i++)
				permutation(i, number - 1);
		}
	}

	/**
	 * Gets the area of a polygon, given the set of points.
	 * 
	 * @param points
	 *            The 2d array of the set of points the polygon has, where the
	 *            first column is the x-value and the second column is the
	 *            y-value.
	 * @return A double of the area from those points.
	 */
	public double getArea(double[][] points) {
		double area = 0;
		double[][] curr = points;
		double[] angles = new double[curr.length];

		double centerX = 0;
		double centerY = 0;

		for (int i = 0; i < points.length; i++) {
			centerX += points[i][0];
			centerY += points[i][1];
		}

		centerX /= curr.length;
		centerY /= curr.length;

		double currentAngle;
		for (int i = 0; i < angles.length; i++){
			currentAngle = Math.atan((points[i][1] - centerY) / (points[i][0] - centerX));
			
			if (points[i][0] < centerX){
				if (points[i][1] < centerY){
					currentAngle += Math.PI;
				}
				else if (points[i][1] > centerY){
					currentAngle += Math.PI;
				}
			}
			else if (points[i][0] > centerX){
				if (points[i][1] < centerY){
					currentAngle += 2 * Math.PI;
				}
			}
			
			angles[i] = currentAngle;
		}
		boolean sorted = true;

		int j = 0;
		while (sorted) {
			sorted = false;
			j++;
			for (int i = 0; i < angles.length - j; i++) {
				if (angles[i] > angles[i + 1]) {
					double temp = angles[i];
					angles[i] = angles[i + 1];
					angles[i + 1] = temp;
					
					temp = curr[i][0];
					curr[i][0] = curr[i + 1][0];
					curr[i + 1][0] = temp;
					
					temp = curr[i][1];
					curr[i][1] = curr[i + 1][1];
					curr[i + 1][1] = temp;
					sorted = true;
				}
			}
		}

		for (int i = 0; i < points.length - 1; i++) {
			area += points[i][0] * points[i + 1][1];
			area -= points[i + 1][0] * points[i][1];
		}

		area += points[points.length - 1][0] * points[0][1];
		area -= points[0][0] * points[points.length - 1][1];

		return Math.abs(area / 2);
	}

	/**
	 * Gets the center of all the points based on the average x and y values.
	 * 
	 * @return An array of the center of all the points detected in the Network
	 *         Table published by the "Publish LinesReport" operation, where the
	 *         first number is an x-value and the second number is the y-value.
	 */
	public double[] getAveCenter() {
		switchType(ProcessingType.Lines);
		ArrayList<ArrayList<Double>> points = getPoints();

		int sumX = 0;
		int sumY = 0;

		for (int i = 0; i < points.size(); i++) {
			sumX += points.get(i).get(0);
			sumY += points.get(i).get(1);
		}

		double[] center = new double[2];

		center[0] = sumX / points.size();
		center[1] = sumY / points.size();

		return center;
	}

	/**
	 * Gets the proportion of solidity from 0 to 1 from the contours detected by
	 * the GRIP image processor.
	 * 
	 * @return An ArrayList of proportion of solidity from the contours
	 *         retrieved from the Network Table published by the
	 *         "Publish ContoursReport" operation.
	 */
	public ArrayList<Double> solidities() {
		switchType(ProcessingType.Contours);
		double[] solidity = table.getNumberArray("solidity", defaultValue);

		return convert(solidity);
	}

	/**
	 * Gets the centers from the contours detected by the GRIP image processor.
	 * 
	 * @return An ArrayList of centers from the contours retrieved from the
	 *         Network Table published by the "Publish ContoursReport"
	 *         operation.
	 */
	public ArrayList<ArrayList<Double>> centers() {
		switchType(ProcessingType.Contours);
		double[] x = table.getNumberArray("centerX", defaultValue);
		double[] y = table.getNumberArray("centerY", defaultValue);
		double[][] center = new double[x.length][2];

		for (int i = 0; i < x.length; i++) {
			center[i][0] = x[i];
			center[i][1] = y[i];
		}

		return convert(center);
	}

	/**
	 * Gets the widths of the contours detected by the GRIP image processor.
	 * 
	 * @return An ArrayList of the widths of the contours retrieved from the
	 *         Network Table published by the "Publish ContoursReport"
	 *         operation.
	 */
	public ArrayList<Double> widths() {
		switchType(ProcessingType.Contours);
		double[] width = table.getNumberArray("width", defaultValue);

		return convert(width);
	}

	/**
	 * Gets the heights of the contours detected by the GRIP image processor.
	 * 
	 * @return An ArrayList of the heights of the contours retrieved from the
	 *         Network Table published by the "Publish ContoursReport"
	 *         operation.
	 */
	public ArrayList<Double> heights() {
		switchType(ProcessingType.Contours);
		double[] height = table.getNumberArray("height", defaultValue);

		return convert(height);
	}

	/**
	 * Gets the areas of the contours detected by the GRIP image processor.
	 * 
	 * @return An ArrayList of the areas of the contours retrieved from the
	 *         Network Table published by the "Publish ContoursReport"
	 *         operation.
	 */
	public ArrayList<Double> areas() {
		switchType(ProcessingType.Contours);
		double[] height = table.getNumberArray("height", defaultValue);

		return convert(height);
	}

	/**
	 * Gets the x-values of the blobs detected by the GRIP image processor.
	 * 
	 * @return An ArrayList of the x-values of the blobs retrieved from the
	 *         Network Table published by the "Publish ContoursReport"
	 *         operation.
	 */
	public ArrayList<Double> xValues() {
		switchType(ProcessingType.Blobs);
		double[] x = table.getNumberArray("x", defaultValue);

		return convert(x);
	}

	/**
	 * Gets the y-values of the blobs detected by the GRIP image processor.
	 * 
	 * @return An ArrayList of the y-values of the blobs retrieved from the
	 *         Network Table published by the "Publish ContoursReport"
	 *         operation.
	 */
	public ArrayList<Double> yValues() {
		switchType(ProcessingType.Blobs);
		double[] y = table.getNumberArray("y", defaultValue);

		return convert(y);
	}

	/**
	 * Gets the sizes of the blobs detected by the GRIP image processor.
	 * 
	 * @return An ArrayList of the sizes of the blobs retrieved from the Network
	 *         Table published by the "Publish ContoursReport" operation.
	 */
	public ArrayList<Double> sizes() {
		switchType(ProcessingType.Blobs);
		double[] size = table.getNumberArray("sizes", defaultValue);

		return convert(size);
	}

	/**
	 * Converts an array of doubles to an ArrayList of doubles.
	 * 
	 * @param values
	 *            An array of doubles.
	 * @return An ArrayList of doubles.
	 */
	public ArrayList<Double> convert(double[] values) {
		ArrayList<Double> re = new ArrayList<Double>();

		for (int i = 0; i < values.length; i++)
			re.add(values[i]);

		return re;
	}

	/**
	 * Converts a 2d array of doubles to a 2d ArrayList of doubles.
	 * 
	 * @param values
	 *            A 2d array of doubles.
	 * @return A 2d ArrayList of doubles.
	 */
	public ArrayList<ArrayList<Double>> convert(double[][] values) {
		ArrayList<ArrayList<Double>> re = new ArrayList<ArrayList<Double>>();

		for (int i = 0; i < values.length; i++) {
			ArrayList<Double> current = new ArrayList<Double>();

			for (int j = 0; j < values[0].length; j++)
				current.add(values[i][j]);

			re.add(current);
		}

		return re;
	}
}