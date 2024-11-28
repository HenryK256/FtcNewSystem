package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class DrivingThread extends Thread {
    DcMotor motor;
    double t;
    public DrivingThread(DcMotor motor, double t) {
        this.motor = motor;
        this.t = t;
    }
    public void run() {
        while (Math.abs(motor.getCurrentPosition() - motor.getTargetPosition()) > 5) {
            motor.setPower(DrivingCurve.curvedPower(motor, t));
        }
    }
}
