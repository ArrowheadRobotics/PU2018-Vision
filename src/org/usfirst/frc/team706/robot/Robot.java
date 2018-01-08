package org.usfirst.frc.team706.robot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SerialPort;

@SuppressWarnings("deprecation")
public class Robot extends SampleRobot {
	
	DriverStation driverStation;
	Joystick leftJoy, rightJoy;
	TalonSRX leftMotorOne, leftMotorTwo, leftMotorThree;
	TalonSRX rightMotorOne, rightMotorTwo, rightMotorThree;
	AHRS navx;
	CameraServer camera;
	UsbCamera usbCam;
	
	char[] fieldData;
	double bearingOffset;
	Thread socketThread;
	byte[] buffer;
	byte[] lastResponse;
	DatagramPacket packet;
	DatagramSocket socket;
	
	public Robot() {
		driverStation = DriverStation.getInstance();
		leftJoy = new Joystick(Constants.LEFT_JOY);
		rightJoy = new Joystick(Constants.RIGHT_JOY);
		leftMotorOne = new TalonSRX(Constants.LEFT_MOTOR_ONE);
		leftMotorTwo = new TalonSRX(Constants.LEFT_MOTOR_TWO);
		leftMotorThree = new TalonSRX(Constants.LEFT_MOTOR_THREE);
		rightMotorOne = new TalonSRX(Constants.RIGHT_MOTOR_ONE);
		rightMotorOne = new TalonSRX(Constants.RIGHT_MOTOR_TWO);
		rightMotorOne = new TalonSRX(Constants.RIGHT_MOTOR_THREE);
		navx = new AHRS(SerialPort.Port.kMXP);
		camera = CameraServer.getInstance();
		usbCam = camera.startAutomaticCapture();
		usbCam.setFPS(Constants.FPS);
		usbCam.setResolution(Constants.WIDTH, Constants.HEIGHT);
		try {
			socket = new DatagramSocket(Constants.SOCKET_PORT);
			buffer = new byte[Constants.BUFFER_LENGTH];
			packet = new DatagramPacket(buffer, buffer.length);
		}
		catch (SocketException e) {
			System.out.println("Socket Exception: " + e.toString());
			socket = null;
		}
	}

	@Override
	public void robotInit() {
		fieldData = driverStation.getGameSpecificMessage().toCharArray();
		bearingOffset = navx.getAngle();
		socketThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (socket != null) {
						try {
							socket.receive(packet);
							lastResponse = packet.getData();
							System.out.println(lastResponse.toString());
						}
						catch (IOException e) {
							System.out.println("IOException: " + e.toString());
						}
					}
				}
			}
		});
	}
	
	@Override
	public void autonomous() {}
	
	@Override
	public void operatorControl() {
		socketThread.start();
		while (isOperatorControl() && isEnabled()) {
			double leftJoyY = leftJoy.getY();
			double rightJoyY = rightJoy.getY();
			leftJoyY *= Math.abs(leftJoyY) > Constants.MIN_SPEED ? Constants.MAX_SPEED : 0;
			rightJoyY *= Math.abs(rightJoyY) > Constants.MIN_SPEED ? Constants.MAX_SPEED : 0;
			leftMotorOne.set(ControlMode.Current, leftJoyY);
			leftMotorTwo.set(ControlMode.Current, leftJoyY);
			leftMotorThree.set(ControlMode.Current, leftJoyY);
			rightMotorOne.set(ControlMode.Current, rightJoyY);
			rightMotorTwo.set(ControlMode.Current, rightJoyY);
			rightMotorThree.set(ControlMode.Current, rightJoyY);
		}
	}
	
	@Override
	public void test() {}
}
