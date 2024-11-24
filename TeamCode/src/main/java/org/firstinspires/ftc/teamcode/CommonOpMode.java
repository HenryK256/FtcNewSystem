package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public abstract class CommonOpMode extends LinearOpMode {
    public void runOpMode() {

        /*
            All code that initializes motors and such should be placed here. This code will run for
            every OpMode ran since they all inherit this one.
        */

        initialize();

        waitForStart();

        while (opModeIsActive()) {
            runner();
        }
    }
    public abstract void runner();
    public abstract void initialize();
}
