package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class DrivingCurve {
    private static double p = 50; // Adjust for smoothing
    public static double getT(DcMotor motor) {
        return Math.abs(motor.getCurrentPosition() - motor.getTargetPosition());
    }
    public static double curvedPower(DcMotor motor, double t) {
        int x = Math.abs(motor.getCurrentPosition() - motor.getTargetPosition());
        double power = 0;

        // Graph of function >> https://www.desmos.com/calculator/wrmonhg4q0

        if (t <= p && 0 <= x && x <= t) {
            power = ((0.9*t)/(2.0*p))*Math.cos((2.0*Math.PI*(x/t+0.5))+1.0) + 0.1;
        } else {
            if (0 <= x && x <= p/2) {
                power = 0.9/2.0*Math.cos((2*Math.PI*(x/p+0.5)))+0.55;
            } else if (t - p/2 <= x && x <= t) {
                power = 0.9/2.0*Math.cos(2*Math.PI/p*(x-t-p/2.0))+0.55;
            } else {
                power = 1;
            }
        }

        return power;
    }
}
