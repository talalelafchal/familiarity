/*
Copyright (c) 2016 Robert Atkinson


All rights reserved.


Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:


Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.


Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.


Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.


NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.robotcontroller.internal;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */


@TeleOp(name="TestTeleOp", group="Iterative Opmode")  // @Autonomous(...) is the other common choice


public class OfficialVertigoTeleOp extends OpMode
{
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();


    DcMotor backLeft;
    DcMotor backRight;
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor shooter;
    DcMotor lift1;
    DcMotor lift2;
    DcMotor whisker;
    ColorSensor colorSensor;
    public CRServo pressButton = null;
    Servo servoBall;
    double ballServoPos = 0.0;

    double power = 1.0;
    boolean LEDState = true;
    boolean lastGamepad1B = false;
    int shooterTargetPosition = 0;
    OpticalDistanceSensor odsSensor;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");


      /* eg: Initialize the hardware variables. Note that the strings used here as parameters
       * to 'get' must correspond to the names assigned during the robot configuration
       * step (using the FTC Robot Controller app on the phone).
       */
        backLeft  = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        whisker = hardwareMap.dcMotor.get("whisker");
        shooter = hardwareMap.dcMotor.get("shooter");
        lift1 = hardwareMap.dcMotor.get("lift1");
        lift2 = hardwareMap.dcMotor.get("lift2");
        //ballLift = hardwareMap.dcMotor.get("ball lift");
        colorSensor = hardwareMap.colorSensor.get("color sensor");
        pressButton = hardwareMap.crservo.get("button");
        odsSensor = hardwareMap.opticalDistanceSensor.get("ods");
        servoBall = hardwareMap.servo.get("servoBall");
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        servoBall.setPosition(ballServoPos);
        // eg: Set the drive motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        // leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        //  rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        // telemetry.addData("Status", "Initialized");
    }


  /*
   * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
   */
 /* @Override
  public void init_loop() {
  } */


    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
        shooter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }


    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("Status", "Running: " + runtime.toString());
        colorSensor.enableLed(!LEDState);


        // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards)
        float powerL = gamepad1.left_stick_y;
        float powerR = gamepad1.right_stick_y;
        if (gamepad2.dpad_left){
            colorSensor.enableLed(true);
        }
        if(gamepad1.left_bumper){
            powerL = powerL/4;
            powerR = powerR/4;
        }
        if(gamepad1.right_bumper){
            powerL = powerL/2;
            powerR = powerR/2;
        }
        backLeft.setPower(-powerL);
        backRight.setPower(powerR);
        frontLeft.setPower(-powerL);
        frontRight.setPower(powerR);
        lift1.setPower(gamepad2.left_stick_y);
        lift2.setPower(gamepad2.right_stick_y);

        //shoot when b on gamepad1
        boolean currentGamepad1B = gamepad1.b;
        if (currentGamepad1B && !lastGamepad1B) {
            shooterTargetPosition += 1120; // 1440 for Tetrix motor
            shooter.setTargetPosition( shooterTargetPosition );
            shooter.setPower(1);
        }
        lastGamepad1B = currentGamepad1B;

        //gamepad2 press b to raise ball
        if (gamepad2.b){
            whisker.setPower(-power);
        }else if (gamepad2.a){
            whisker.setPower(power);
        }else{
            whisker.setPower(0);
        }

        //gamepad2 bumpers to extend button pusher
        if (gamepad2.right_bumper){
            pressButton.setPower(1);
        } else if (gamepad2.left_bumper) {
            pressButton.setPower(-1);
        }else{
            pressButton.setPower(0);
        }

        //gamepad1 dpad to adjust ball clench servo
        if (gamepad1.dpad_up){
            ballServoPos = 0;
        } else if (gamepad1.dpad_down) {
            ballServoPos = 1;
        } else if (gamepad1.dpad_left){
            ballServoPos = 2;
        } else if (gamepad1.dpad_right){
            ballServoPos = 3;
        }
        if (ballServoPos == 0){
            servoBall.setPosition(-1);
        } else if (ballServoPos == 1) {
            servoBall.setPosition(-.5);
        } else if (ballServoPos == 2){
            servoBall.setPosition(0);
        } else if (ballServoPos == 3){
            servoBall.setPosition(.5);
        }

            //different telemetry values
            telemetry.addData("servo position", pressButton.getPower());
            telemetry.addData("Front Left Encoder", backLeft.getCurrentPosition());
            telemetry.addData("Front Right Encoder", backRight.getCurrentPosition());
            telemetry.addData("Back Left Encoder", frontLeft.getCurrentPosition());
            telemetry.addData("Back Right Encoder", frontRight.getCurrentPosition());
            telemetry.addData("red",colorSensor.red());
            telemetry.addData("green",colorSensor.green());
            telemetry.addData("blue",colorSensor.blue());
            telemetry.addData("Raw",    odsSensor.getRawLightDetected());
            telemetry.addData("Normal", odsSensor.getLightDetected());
            telemetry.update();
    }

    @Override
    public void stop() {
    }

}






