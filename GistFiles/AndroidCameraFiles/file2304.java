package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;


@TeleOp(name="ColorSensorTest", group="Iterative Opmode")

public class ColorTest extends OpMode {
    ColorSensor color;

    public void init(){
        color = hardwareMap.colorSensor.get("color sensor");
        color.enableLed(false);
    }
    public void loop() {
        color.enableLed(false);
        telemetry.addData("Red: ", color.red());
        telemetry.addData("Blue: ", color.blue());
    }
}

