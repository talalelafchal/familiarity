package org.firstinspires.ftc.robotcontroller.internal;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Encoder Test", group = "Tests")

public class WAuto extends LinearOpMode {
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.dcMotor.get("leftMotor1");
        frontRight = hardwareMap.dcMotor.get("rightMotor1");
        backLeft = hardwareMap.dcMotor.get("leftMotor2");
        backRight = hardwareMap.dcMotor.get("rightMotor2");
        DcMotor shooter = hardwareMap.dcMotor.get("shooter");
        DcMotor lift = hardwareMap.dcMotor.get("lift");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        sleep(5000);
        /*frontLeft.setPower(0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(0.5);
        backRight.setPower(0.5);
        sleep(400);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
*/
  //      sleep(1000);

        shooter.setPower(1);
        sleep(1200);
        shooter.setPower(0);

        lift.setPower(1);
        sleep(4000);
        lift.setPower(0);

        //sleep(1000);

        /*frontLeft.setPower(1);
        frontRight.setPower(1);
        backLeft.setPower(1);
        backRight.setPower(1);
        sleep(150);
       frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);*/

        sleep(1000);

        shooter.setPower(1);
        sleep(1000);
        shooter.setPower(0);


        sleep(1000);

        frontLeft.setPower(1);
        frontRight.setPower(1);
        backLeft.setPower(1);
        backRight.setPower(1);
        sleep(2700);
       frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        /*setMotorModes(DcMotor.RunMode.RUN_USING_ENCODER);

        setMotorModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setTargetPosition(1120);
        frontRight.setTargetPosition(1120);
        backLeft.setTargetPosition(1120);
        backRight.setTargetPosition(1120);

        setMotorModes(DcMotor.RunMode.RUN_TO_POSITION);

        setMotorPower(.5);

        while(frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy()) {
            Thread.sleep(1);
            telemetry.addData("Front Left Encoder", frontLeft.getCurrentPosition());
            telemetry.addData("Front Right Encoder", frontRight.getCurrentPosition());
            telemetry.addData("Back Left Encoder", backLeft.getCurrentPosition());
            telemetry.addData("Back Right Encoder", backRight.getCurrentPosition());
        }

        setMotorPower(0);

        setMotorModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);*/
    }

    private void setMotorPower(double power) {
        frontLeft.setPower(power);
        backLeft.setPower(power);
        frontRight.setPower(power);
        backRight.setPower(power);
    }

    private void setMotorModes(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }
}

