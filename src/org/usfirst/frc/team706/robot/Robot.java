package org.usfirst.frc.team706.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;

@SuppressWarnings("deprecation")
public class Robot extends SampleRobot {
	
	XboxController xbox;
	TalonSRX leftMotorOne, leftMotorTwo;
	TalonSRX rightMotorOne, rightMotorTwo;
	
	public Robot() {
		xbox = new XboxController(Constants.XBOX);
		leftMotorOne = new TalonSRX(Constants.LEFT_MOTOR_ONE);
		leftMotorTwo = new TalonSRX(Constants.LEFT_MOTOR_TWO);
		rightMotorOne = new TalonSRX(Constants.RIGHT_MOTOR_ONE);
		rightMotorTwo = new TalonSRX(Constants.RIGHT_MOTOR_TWO);
	}

	@Override
	public void robotInit() {
	}
	
	@Override
	public void autonomous() {}
	
	@Override
	public void operatorControl() {
		while (isOperatorControl() && isEnabled()) {
			double leftJoyY = -(xbox.getRawAxis(3)+xbox.getRawAxis(0));
			double rightJoyY = -(xbox.getRawAxis(3)-xbox.getRawAxis(0));
			if (xbox.getRawAxis(2) > Constants.MIN_SPEED) {
				leftJoyY = (xbox.getRawAxis(2)+xbox.getRawAxis(0));
				rightJoyY = (xbox.getRawAxis(2)-xbox.getRawAxis(0));
			}
			leftJoyY *= Math.abs(leftJoyY) > Constants.MIN_SPEED ? Constants.MAX_SPEED : 0;
			rightJoyY *= Math.abs(rightJoyY) > Constants.MIN_SPEED ? -Constants.MAX_SPEED : 0;
			leftMotorOne.set(ControlMode.PercentOutput, leftJoyY);
			leftMotorTwo.set(ControlMode.PercentOutput, leftJoyY);
			rightMotorOne.set(ControlMode.PercentOutput, rightJoyY);
			rightMotorTwo.set(ControlMode.PercentOutput, rightJoyY);
			Timer.delay(0.005);
		}
	}
	
	@Override
	public void test() {}
}
