package org.oastem.frc;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

/**
 * @author mduong15
 *
 * Handle input from Logitech Gaming Pad devices connected to the Driver Station.
 * This class handles standard input that comes from the Driver Station. Each time
 * a value is requested, the most recent value is returned. There is a single class
 * instance for each Logitech Gaming Pad and the mapping of ports to hardware
 * buttons is already configured in the class by using the correct methods.
 * 
 * The Logitech Gaming Pad must be set to XInput (the back of the controller
 * should be switched to the 'X'/left side). DirectInput (or the right side)
 * requires additional software that is unnecessary.
 */

public class LogitechGamingPad extends GenericHID{
	private Joystick gamepad;
	
	/**
	 * Construct an instance of a Logitch Gaming Pad. The Logitech Gaming Pad index
	 * is the usb port on the driver station.
	 *
	 * @param port The port on the driver station that the gamepad is plugged
	 *        into.
	 */
	public LogitechGamingPad(int port)
	{
		gamepad = new Joystick(port);
	}

	
	// The following methods are get methods specific to the Logitech Gaming Pad.
	// Use these methods to get button values.
	
	public double getLeftAnalogX()
	{
		return getX(Hand.kLeft);
	}
	
	public double getLeftAnalogY()
	{
		return getY(Hand.kLeft);
	}
	
	public boolean getLeftAnalogButton()
	{
		return gamepad.getRawButton(9);
	}
	
	public boolean getLeftBumper()
	{
		return getBumper(Hand.kLeft);
	}
	
	public boolean getLeftTrigger()
	{
		return getTrigger(Hand.kLeft);
	}
	
	public double getLeftTriggerValue()
	{
		return gamepad.getRawAxis(2);
	}
	
	
	
	public double getRightAnalogX()
	{
		return getX(Hand.kRight);
	}
	
	public double getRightAnalogY()
	{
		return getY(Hand.kRight);
	}
	
	public boolean getRightAnalogButton()
	{
		return gamepad.getRawButton(10);
	}
	
	public boolean getRightBumper()
	{
		return getBumper(Hand.kRight);
	}
	
	public boolean getRightTrigger()
	{
		return getTrigger(Hand.kRight);
	}
	
	public double getRightTriggerValue()
	{
		return gamepad.getRawAxis(3);
	}
	
	
	
	public boolean getAButton()
	{
		return gamepad.getRawButton(1);
	}
	
	public boolean getBButton()
	{
		return gamepad.getRawButton(2);
	}
	
	public boolean getXButton()
	{
		return gamepad.getRawButton(3);
	}
	
	public boolean getYButton()
	{
		return gamepad.getRawButton(4);
	}
	
	public boolean getBackButton()
	{
		return gamepad.getRawButton(7);
	}
	
	public boolean getStartButton()
	{
		return gamepad.getRawButton(8);
	}
	
	public boolean checkDPad(int index)
	{
		if (0 <= index && index <= 7)
			return (index * 45) == gamepad.getPOV(0);
		else
			return false;
	}
	
	public boolean checkDPad(double angle, boolean inDegrees)
	{
		if (!inDegrees)
			angle = Math.toDegrees(angle);
		return angle == gamepad.getPOV(0); 
	}
	
	public int getDPad()
	{
		int pov = gamepad.getPOV(0);
		if (pov == -1)
			return pov;
		else
			return pov/45;
	}
	
	public double getDPad(boolean inDegrees)
	{
		if (inDegrees)
			return gamepad.getPOV(0);
		else
			return Math.toRadians(gamepad.getPOV(0));
	}
	
	public boolean dPadIsPressed()
	{
		return gamepad.getPOV(0) != -1;
	}
	
	
	// The following methods are generic methods required by the abstract class.
	// The above methods should be used to get the gamepad inputs. 
	/**
	 * Get the value of the x-axis of a specific analog.
	 * 
	 * @param hand The left or right analog.
	 * @return The value of the axis.
	 */
	public double getX(Hand hand) {
		if (hand == Hand.kLeft)
			return gamepad.getRawAxis(0);
		else
			return gamepad.getRawAxis(4);
	}

	/**
	 * Get the value of the y-axis of a specific analog.
	 * 
	 * @param hand The left or right analog.
	 * @return The value of the axis.
	 */
	public double getY(Hand hand) {
		if (hand == Hand.kLeft)
			return gamepad.getRawAxis(1);
		else
			return gamepad.getRawAxis(5);
	}

	/**
	 * Get the value of the axis.
	 *
	 * @param axis The axis to read, starting at 0.
	 * @return The value of the axis.
	 */
	public double getRawAxis(int which) {
		return gamepad.getRawAxis(which);
	}

	
	/**
	 * Read the state of the trigger of a specific side.
	 * 
	 * Because the trigger is read as an axis and returns a value
	 * [0, 1] instead of [-1, 1], this method will return true
	 * even if the trigger is only slightly pressed.
	 * 
	 * @param hand The left or right trigger(side).
	 * @return The state of the trigger.
	 */
	public boolean getTrigger(Hand hand) {
		if (hand == Hand.kLeft)
			return gamepad.getRawAxis(2) > 0;
		else
			return gamepad.getRawAxis(3) > 0;
	}

	/**
	 * Read the state of the bumper of a specific side.
	 * 
	 * @param hand The left or right bumper(side).
	 * @return The state of the bumper.
	 */
	public boolean getBumper(Hand hand) {
		if (hand == Hand.kLeft)
			return gamepad.getRawButton(5);
		else
			return gamepad.getRawButton(6);
	}

	/**
	 * Get the button value (starting at button 1).
	 *
	 * The appropriate button is returned as a boolean value.
	 *
	 * @param button The button number to be read (starting at 1).
	 * @return The state of the button.
	 */
	public boolean getRawButton(int button) {
		return gamepad.getRawButton(button);
	}

	/**
	 * Get the state of a POV on the gamepad.
	 *
	 * @param pov The index of the POV to read (starting at 0).
	 * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
	 */
	public int getPOV(int pov) {
		return gamepad.getPOV(pov);
	}
	
	/**
	 * This is not supported for the Logitech Gaming Pad. This method is only here to
	 * complete the GenericHID interface.
	 *
	 * @param hand This parameter is ignored and is only
	 *        here to complete the GenericHID interface.
	 * @return The value of the axis (always 0).   
	 */
	public double getZ(Hand hand) {
		return 0;
	}
	
	/**
	 * This is not supported for the Logitech Gaming Pad. This method is only here to
	 * complete the GenericHID interface.
	 * 
	 * @return The twist value of the gamepad (always 0).
	 */
	public double getTwist() {
		return 0;
	}
	
	/**
	 * This is not supported for the Logitech Gaming Pad. This method is only here to
	 * complete the GenericHID interface.
	 * 
	 * @return The throttle value of the gamepad (always 0).
	 */
	public double getThrottle() {
		return 0;
	}
	
	/**
	 * This is not supported for the Logitech Gaming Pad. This method is only here to
	 * complete the GenericHID interface.
	 *
	 * @param hand This parameter is ignored and is only
	 *        here to complete the GenericHID interface.
	 * @return The state of the top (always false).
	 */
	public boolean getTop(Hand hand) {
		return false;
	}
}
