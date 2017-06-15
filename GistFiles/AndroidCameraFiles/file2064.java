package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.ColorSensor;
/**
 * Created by hkv1 on 11/28/2016.
 */
@Autonomous
public class VertigoAutonomousBlue extends LinearOpMode {
    //Declaring Op Mode Members
    //private ElapsedTime runtime = new ElapsedTime();

    public DcMotor leftMotorF = null;
    public DcMotor leftMotorB = null;
    public DcMotor rightMotorF = null;
    public DcMotor rightMotorB = null;
    public DcMotor shooter = null;
    ColorSensor colorSensor;
    ColorSensor colorSensorGround;

    @Override
    public void runOpMode() throws InterruptedException {
        colorSensor = hardwareMap.colorSensor.get("color sensor");
        colorSensor = hardwareMap.colorSensor.get("color sensor ground");
        shooter = hardwareMap.dcMotor.get("shooter");
        leftMotorF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotorF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotorB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotorB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();

        leftMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();
        while (opModeIsActive()) {
            //1120 is one rotation
            //forward
            leftDrive(3);
            rightDrive(3);

            sleep(500);

            //turn to face button
            leftDrive(0.5);
            rightDrive(0);

            sleep(500);

            //drive to check
            leftDrive(0.3);
            rightDrive(0.3);

            telemetry.addData("niceu", colorSensor.red());
            if (colorSensor.red() > 5) {
                //forward to hit button
                leftDrive(0.5);
                rightDrive(0.5);
                sleep(500);
                telemetry.addData("very", colorSensor.red());
                //go back
                leftDrive(-0.5);
                rightDrive(-0.5);
                sleep(500);
                //turn to shoot
                leftDrive(-1.75);
                rightDrive(0);
                sleep(500);
                shooter.setTargetPosition(1);
                shooter.setPower(1);
                idle();

            } else {
                sleep(500);

                //back up
                leftDrive(-0.5);
                rightDrive(-0.5);

                sleep(500);

                //turn
                leftDrive(0);
                rightDrive(-0.5);

                sleep(500);

                //go back a little
                leftDrive(-0.2);
                rightDrive(-0.2);

                sleep(500);

                //turn to check again
                rightDrive(0.5);
                leftDrive(0);

                sleep(500);

                //forward to hit button
                leftDrive(0.8);
                rightDrive(0.8);
                sleep(500);

                leftDrive(0);
                rightDrive(-1.75);
                sleep(500);
                shooter.setTargetPosition(1);
                shooter.setPower(1);
                idle();
            }
        }
    }
    public void leftDrive(double x){
        x = x*1120;
        leftMotorB.setTargetPosition((int)x);
        leftMotorB.setPower(0.5);
        leftMotorF.setTargetPosition((int)x);
        leftMotorF.setPower(0.5);
    }
    public void rightDrive(double x){
        x= x*1120;
        rightMotorB.setTargetPosition((int)x);
        rightMotorB.setPower(0.5);
        rightMotorF.setTargetPosition((int)x);
        rightMotorF.setPower(0.5);
    }


}
