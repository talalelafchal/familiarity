package org.firstinspires.ftc.robotcontroller.internal;


import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Encoder Test", group = "Tests")

public class AutonomousRed extends LinearOpMode {
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    ColorSensor colorSensor;
    //ColorSensor colorSensorGround;
    CRServo pressButton;

    private ElapsedTime runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.dcMotor.get("leftMotor1");
        frontRight = hardwareMap.dcMotor.get("rightMotor1");
        backLeft = hardwareMap.dcMotor.get("leftMotor2");
        backRight = hardwareMap.dcMotor.get("rightMotor2");
        DcMotor shooter = hardwareMap.dcMotor.get("shooter");
        DcMotor lift = hardwareMap.dcMotor.get("lift");
        CRServo pressButton = hardwareMap.crservo.get("button");
        ColorSensor colorSensor = hardwareMap.colorSensor.get("color sensor");

        //ColorSensor colorSensorGround = hardwareMap.colorSensor.get("color sensor ground");

        setMotorModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        setMotorModes(DcMotor.RunMode.RUN_USING_ENCODER);


        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        setTargetPositionL(0);
        setTargetPositionR(0);
        pressButton.setPower(0);

        boolean LEDState = true;

        waitForStart();
        //colorSensorGround.enableLed(LEDState);
        colorSensor.enableLed(!LEDState);

        float hsvValues[] = {0,0,0};
        //encoderDrive(2200,2200);

        while (opModeIsActive()) {
            //first value adds to left encoder position
            //right value adds to right encoder position
             // S2: Turn Right 12 Inches with 4 Sec timeout
            //encoderDrive(DRIVE_SPEED, 10, 10, 4.0);
            //Color.RGBToHSV(colorSensorGround.red() * 8,colorSensorGround.red() * 8,colorSensorGround.red() * 8, hsvValues);
            Color.RGBToHSV(colorSensor.red() * 8,colorSensor.red() * 8,colorSensor.red() * 8, hsvValues);
            if (colorSensor.blue() > colorSensor.red() || colorSensor.blue() > colorSensor.green()){
                pressButton.setPower(1);
                sleep(500);
                pressButton.setPower(-1);
            }
            encoderDrive(DRIVE_SPEED,  14,  14, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            encoderDrive(TURN_SPEED,   -12, 4, 5.0);
            sleep(2000);
            encoderDrive(DRIVE_SPEED,  5,  5, 5.0);
            // S1: Forward 47 Inches with 5 Sec timeout
            pressButton.setPower(1);
            sleep(500);
            pressButton.setPower(-1);



        /*
        encoderDrive(1120,1120);//go forward to check color
        if (colorSensor.blue() >= 5){
            encoderDrive(0,-1120);//turn backward to push button
            encoderDrive(1120,1120);//push button
        }
        if(colorSencor.red>=5){
        //go forward a little to push other button
        //do same process of turning backward
        //push button motion
        }
        //go backward a bit
        //turn to check other beacon now

        //pretty much copy paste first part for second sensor
        */
        }
    }

    private void setMotorPower(double power) {
        frontLeft.setPower(power);
        backLeft.setPower(power);
        frontRight.setPower(power);
        backRight.setPower(power);
    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) throws InterruptedException {
        int newLeftTargetFront;
        int newRightTargetFront;
        int newLeftTargetBack;
        int newRightTargetBack;


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTargetFront = frontLeft.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTargetFront = frontRight.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            newLeftTargetBack = backLeft.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            newRightTargetBack = backRight.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);

            frontLeft.setTargetPosition(newLeftTargetFront);
            frontRight.setTargetPosition(newRightTargetFront);
            backLeft.setTargetPosition(newLeftTargetBack);
            backRight.setTargetPosition(newRightTargetBack);

            // Turn On RUN_TO_POSITION
            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            // reset the timeout time and start motion.

            runtime.reset();
            frontLeft.setPower(Math.abs(speed));
            frontRight.setPower(Math.abs(speed));
            backLeft.setPower(Math.abs(speed));
            backRight.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTargetFront, newRightTargetFront);
                telemetry.addData("Path2", "Running at %7d :%7d",
                        frontLeft.getCurrentPosition(),
                        frontLeft.getCurrentPosition());
                telemetry.update();

                // Allow time for other processes to run..,vt
                idle();
            }
            setMotorPower(0);
            setMotorModes(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    private void setMotorModes(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    private void setTargetPositionL(int d) {
        frontLeft.setTargetPosition(d);
        backLeft.setTargetPosition(d);
    }

    private void setTargetPositionR(int z) {
        frontRight.setTargetPosition(z);
        backRight.setTargetPosition(z);
    }
}



