package org.oastem.frc.sensor;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.util.ArrayList;

/**
 * @author mandarker
 */

public class ImageProcessing {

	private NetworkTable table;

	// This defaultValue is here in the case that there is nothing detected on
	// the Network Table,
	// so that there would be no error.

	public enum ProcessingType {
		Lines, Contours, Blobs;
	}

	private ProcessingType process;

	private String contours;
	private String lines;
	private String blobs;
	private double[] defaultValue;

	/**
	 * Initializes a Network Table that retrieves line values from the GRIP
	 * image processor. Make sure GRIP is connected to the robot and
	 * "Publish LinesReport" is a connected operation in the operation
	 * flowchart.
	 * 
	 * @param address
	 *            The address for the Network Table that can be found in the
	 *            Outline Viewer.
	 */
	public ImageProcessing(String contours, String lines, String blobs, ProcessingType type) {
		this.contours = contours;
		this.lines = lines;
		this.blobs = blobs;
		switchType(type);
		defaultValue = new double[0];
	}

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
		process = type;
	}

	/**
	 * Gets the angle of each line detected by the GRIP image processor.
	 * 
	 * @return The angle of each line retrieved from the Network Table published
	 *         by the "Publish LinesReport" operation.
	 */
	public ArrayList<Double> getAngles() {
		switchType(ProcessingType.Lines);
		double[] current = table.getNumberArray("angle", defaultValue);

		return convert(current);
	}

	/**
	 * Gets the length of each line detected by the GRIP image processor.
	 * 
	 * @return The length of each line retrieved from the Network Table
	 *         published by the "Publish LinesReport" operation.
	 */
	public ArrayList<Double> getLengths() {
		switchType(ProcessingType.Lines);
		double[] current = table.getNumberArray("length", defaultValue);

		return convert(current);
	}

	/**
	 * Gets the raw values of the two points creating each line, straight from
	 * the Network Table.
	 * 
	 * @return The raw points of each line retrieved from the Network Table
	 *         published by the "Publish LinesReport" operation.
	 */
	public ArrayList<ArrayList<Double>> getRawPoints() {
		switchType(ProcessingType.Lines);
		double[][] pointValues = new double[4][0];
		pointValues[0] = table.getNumberArray("x1", defaultValue);
		pointValues[1] = table.getNumberArray("x2", defaultValue);
		pointValues[2] = table.getNumberArray("y1", defaultValue);
		pointValues[3] = table.getNumberArray("y2", defaultValue);

		return convert(pointValues);

	}

	/**
	 * Gets the raw points and turns into a more manageable table, where the
	 * left column is the x value and the right column is the y value.
	 * 
	 * @return The points of each line retrieved from the Network Table
	 *         published by the "Publish LinesReport" operation.
	 */
	public ArrayList<ArrayList<Double>> getPoints() {
		switchType(ProcessingType.Lines);
		ArrayList<ArrayList<Double>> rawValues = getRawPoints();
		ArrayList<ArrayList<Double>> re = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();

		for (int i = 0; i < rawValues.get(0).size(); i++) {
			x.add(rawValues.get(0).get(i));
			y.add(rawValues.get(2).get(i));
			x.add(rawValues.get(1).get(i));
			y.add(rawValues.get(3).get(i));
		}

		return re;
	}

	public double[][] getRectangle() {
		return null;
	}

	/**
	 * Gets the center of all the points based on the average x and y values.
	 * 
	 * @return The center of all the points detected in the Network Table
	 *         published by the "Publish LinesReport" operation.
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

	public ArrayList<Double> solidities() {
		switchType(ProcessingType.Contours);
		double[] solidity = table.getNumberArray("solidity", defaultValue);

		return convert(solidity);
	}

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

	public ArrayList<Double> widths() {
		switchType(ProcessingType.Contours);
		double[] width = table.getNumberArray("width", defaultValue);

		return convert(width);
	}

	public ArrayList<Double> heights() {
		switchType(ProcessingType.Contours);
		double[] height = table.getNumberArray("height", defaultValue);

		return convert(height);
	}

	public ArrayList<Double> areas() {
		switchType(ProcessingType.Contours);
		double[] height = table.getNumberArray("height", defaultValue);

		return convert(height);
	}

	public ArrayList<Double> xValues() {
		switchType(ProcessingType.Blobs);
		double[] x = table.getNumberArray("x", defaultValue);
		
		return convert(x);
	}

	public ArrayList<Double> yValues() {
		switchType(ProcessingType.Blobs);
		double[] y = table.getNumberArray("y", defaultValue);
		
		return convert(y);
	}

	public ArrayList<Double> sizes() {
		switchType(ProcessingType.Blobs);
		double[] size = table.getNumberArray("sizes", defaultValue);
		
		return convert(size);
	}

	public ArrayList<Double> convert(double[] values) {
		ArrayList<Double> re = new ArrayList<Double>();

		for (int i = 0; i < values.length; i++) {
			re.add(values[i]);
		}

		return re;
	}

	public ArrayList<ArrayList<Double>> convert(double[][] values) {
		ArrayList<ArrayList<Double>> re = new ArrayList<ArrayList<Double>>();

		for (int i = 0; i < values.length; i++) {
			re.add(new ArrayList<Double>());
			ArrayList<Double> current = re.get(i);

			for (int j = 0; j < values[0].length; j++) {
				current.add(values[i][j]);
			}
		}

		return re;
	}
}