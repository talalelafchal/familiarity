package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by hkv1 on 11/28/2016.
 */
@Autonomous
public class VertigoAutAlt extends LinearOpMode {
    //Declaring Op Mode Members
    //private ElapsedTime runtime = new ElapsedTime();

    public DcMotor leftMotorF = null;
    public DcMotor leftMotorB = null;
    public DcMotor rightMotorF = null;
    public DcMotor rightMotorB = null;
    public DcMotor shooter = null;

    @Override
    public void runOpMode() throws InterruptedException {
        leftMotorF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotorF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotorB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotorB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();

        leftMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooter = hardwareMap.dcMotor.get("shooter");

        waitForStart();
        while (opModeIsActive()) {

            shooter.setPower(1);
            sleep(250);
            shooter.setPower(-1);

            sleep(10000);

            //turn to drive by TriHard
            leftDrive(0);
            rightDrive(1);

            sleep(500);

            //drive by
            leftDrive(1);
            rightDrive(1);

            sleep(500);

            //turn to back up
            leftDrive(0);
            rightDrive(-1);
            sleep(500);

            //ram it
            leftDrive(1.5);
            rightDrive(1.5);

            //back up to square
            leftDrive(-3);
            rightDrive(-3);

            idle();
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

