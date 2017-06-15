package org.firstinspires.ftc.robotcontroller.internal;


import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Encoder Test", group = "Tests")

public class AutonomousBlue extends LinearOpMode {
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;
    private DcMotor whisker;
    private DcMotor shooter;
    int shooterTargetPosition = 0;

    ColorSensor colorSensor;
    CRServo pressButton;
    OpticalDistanceSensor odsSensor;


    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        shooter = hardwareMap.dcMotor.get("shooter");
        whisker = hardwareMap.dcMotor.get("whisker");
        pressButton = hardwareMap.crservo.get("button");
        colorSensor = hardwareMap.colorSensor.get("color sensor");
        odsSensor = hardwareMap.opticalDistanceSensor.get("ods");

        setMotorModes(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        idle();


        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        boolean LEDState = true;

        waitForStart();
        shooter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        colorSensor.enableLed(!LEDState);
        /*
        shooterTargetPosition += 1120; // 1440 for Tetrix motor
        shooter.setTargetPosition( shooterTargetPosition );
        shooter.setPower(1);
        sleep(500);

        whisker.setPower(1);
        sleep(3000);
        whisker.setPower(0);

        sleep(1000);

        shooterTargetPosition += 1120; // 1440 for Tetrix motor
        shooter.setTargetPosition( shooterTargetPosition );
        shooter.setPower(1);
        sleep(2000);
        */
        //encoderDrive(-760,-.5);
        //encoderTurnR(-1250, -1);
        //encoderDrive(-3000,-1);
        //encoderTurnL(-1200,-.5);
        while(odsSensor.getRawLightDetected()<2.0){
            encoderDriveWhileLooking(-100,-0.25);
        }

        if (colorSensor.blue()>colorSensor.red()){
            pressButton.setPower(1);
            sleep(1000);
            pressButton.setPower(-1);
            sleep(1000);
        }
        else{
            encoderDrive(-300,-.5);
            pressButton.setPower(1);
            sleep(1000);
            pressButton.setPower(-1);
            sleep(1000);
        }
    }


    private void setMotorPower(double power) {
        frontLeft.setPower(power);
        backLeft.setPower(power);
        frontRight.setPower(power);
        backRight.setPower(power);
    }
    private void setMotorPowerL(double power) {
        frontRight.setPower(power);
        backRight.setPower(power);
    }
    private void setMotorPowerR(double power) {
        frontLeft.setPower(power);
        backLeft.setPower(power);
    }

    public void encoderTurnR(int position, double power) throws InterruptedException {

        int startingPosition, currentPosition;

        startingPosition = frontLeft.getCurrentPosition();
        currentPosition = startingPosition;
        setMotorPowerR(power);
        setMotorPowerL(-power);

        if (position>0) {
            while (opModeIsActive() && (currentPosition < startingPosition + position) ){
                currentPosition = frontLeft.getCurrentPosition();
                telemetry.addData("frontleft", currentPosition);
                sleep(10);
            }
        } else {
            while (opModeIsActive() && (currentPosition > startingPosition + position) ){
                currentPosition = frontLeft.getCurrentPosition();
                telemetry.addData("frontLeft", currentPosition);
                sleep(10);
            }
        }
        setMotorPower(0);
        sleep(500);
    }
    public void encoderTurnL(int position, double power) throws InterruptedException {

        int startingPosition, currentPosition;

        startingPosition = frontRight.getCurrentPosition();
        currentPosition = startingPosition;
        setMotorPowerL(power);
        setMotorPowerR(-power);

        if (position>0) {
            while (opModeIsActive() && (currentPosition < startingPosition + position) ){
                currentPosition = frontRight.getCurrentPosition();
                telemetry.addData("frontRight", currentPosition);
                sleep(10);
            }
        } else {
            while (opModeIsActive() && (currentPosition > startingPosition + position) ){
                currentPosition = frontRight.getCurrentPosition();
                telemetry.addData("frontRight", currentPosition);
                sleep(10);
            }
        }
        setMotorPower(0);
        sleep(500);
    }
    public void encoderDrive(int position, double power) throws InterruptedException {

        int startingPosition, currentPosition;

        startingPosition = frontLeft.getCurrentPosition();
        currentPosition = startingPosition;
        setMotorPower(power);

        if (position>0) {
            while (opModeIsActive() && (currentPosition < startingPosition + position) ){
                currentPosition = frontLeft.getCurrentPosition();
                telemetry.addData("frontLeft", currentPosition);
                sleep(10);
            }
        } else {
            while (opModeIsActive() && (currentPosition > startingPosition + position) ){
                currentPosition = frontLeft.getCurrentPosition();
                telemetry.addData("frontLeft", currentPosition);
                sleep(10);
            }
        }
        setMotorPower(0);
        sleep(500);
    }

    public void encoderDriveWhileLooking(int position, double power) throws InterruptedException {

        int startingPosition, currentPosition;

        startingPosition = frontLeft.getCurrentPosition();
        currentPosition = startingPosition;
        setMotorPower(power);

        if (position>0) {
            while (opModeIsActive() && (currentPosition < startingPosition + position) ){
                currentPosition = frontLeft.getCurrentPosition();
                telemetry.addData("frontLeft", currentPosition);
                sleep(10);
            }
        } else {
            while (opModeIsActive() && (currentPosition > startingPosition + position) ){
                currentPosition = frontLeft.getCurrentPosition();
                telemetry.addData("frontLeft", currentPosition);
                sleep(10);
            }
        }
        setMotorPower(0);
        sleep(10);
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


