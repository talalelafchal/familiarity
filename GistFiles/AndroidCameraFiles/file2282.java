package org.firstinspires.ftc.robotcontroller.internal.testcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.ColorSensor;

@Autonomous(name = "blue", group="test")
public class ThirdTimeCharmBlue extends LinearOpMode {
    private DcMotor FLeft;
    private DcMotor FRight;
    private DcMotor BLeft;
    private DcMotor BRight;
    private DcMotor shooter;
    private DcMotor lift;
    ColorSensor colorSensor;

    private ElapsedTime runtime = new ElapsedTime();

    static final double oneRev = 1120;    // eg: TETRIX Motor Encoder
    static final double diameter = 4.0;     // For figuring circumference
    static final double count_per_inch = (oneRev) /
            (diameter * 3.1415);
    static final double driveSpeed = 1;
    static final double turnSpeed = 1;

    @Override
    public void runOpMode() throws InterruptedException {
        FLeft= hardwareMap.dcMotor.get("leftMotor1");
        FRight = hardwareMap.dcMotor.get("leftMotor2");
        BLeft = hardwareMap.dcMotor.get("rightMotor1");
        BRight = hardwareMap.dcMotor.get("rightMotor2");
        colorSensor = hardwareMap.colorSensor.get("color sensor"); //REMIND MYSELF TO CHANGE CONFIGURATION ON ROBOT TO COLORSENSOR

        FRight.setDirection(DcMotor.Direction.REVERSE);
        BRight.setDirection(DcMotor.Direction.REVERSE);
        FLeft.setDirection(DcMotor.Direction.FORWARD);
        BLeft.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        FLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        FLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0", "Starting at %7d :%7d",
                FLeft.getCurrentPosition(),
                FRight.getCurrentPosition());
        telemetry.update();

        waitForStart();
        //reset position
        FLeft.setTargetPosition(0);
        BLeft.setTargetPosition(0);
        FRight.setTargetPosition(0);
        BRight.setTargetPosition(0);

        FLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FLeft.setPower(1);
        FRight.setPower(1);
        BLeft.setPower(1);
        BRight.setPower(1);
        sleep(1000);
        FLeft.setPower(0);
        FRight.setPower(0);
        BLeft.setPower(0);
        BRight.setPower(0);

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        encoderDrive(driveSpeed,8,8,5);//forward 8 inches
        encoderDrive(turnSpeed,-4,4,5);//turn left
        encoderDrive(driveSpeed,3,3,5);//forward to check color
        if(colorSensor.red()>=5){
            encoderDrive(turnSpeed,0,-4,5);//rotate back
            encoderDrive(driveSpeed,3,3,5);//drive forward 3 inches
        }

    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) throws InterruptedException {
        int newLeftTarget;
        int newRightTarget;
        int newLeftTarget1;
        int newRightTarget1;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = FLeft.getCurrentPosition() + (int) (leftInches * count_per_inch);
            newRightTarget = FRight.getCurrentPosition() + (int) (rightInches * count_per_inch);
            newLeftTarget1 = BLeft.getCurrentPosition() + (int) (leftInches * count_per_inch);
            newRightTarget1 = BRight.getCurrentPosition() + (int) (rightInches * count_per_inch);
            FLeft.setTargetPosition(newLeftTarget);
            BLeft.setTargetPosition(newLeftTarget1);
            FRight.setTargetPosition(newRightTarget);
            BRight.setTargetPosition(newRightTarget1);

            // Turn On RUN_TO_POSITION
            FLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            FRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            BLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            BRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            FLeft.setPower(Math.abs(speed));
            FRight.setPower(Math.abs(speed));
            BLeft.setPower(Math.abs(speed));
            BRight.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (FLeft.isBusy() || FRight.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d",
                        FLeft.getCurrentPosition(),
                        FRight.getCurrentPosition(),
                        BLeft.getCurrentPosition(),
                        BRight.getCurrentPosition());
                telemetry.update();


            }

            // Stop all motion;
            //FLeft.setPower(0);
            //FRight.setPower(0);
            //BLeft.setPower(0);
            //BRight.setPower(0);

            // Turn off RUN_TO_POSITION
            FLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            FRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(1000);   // optional pause after each move
        }
    }
}