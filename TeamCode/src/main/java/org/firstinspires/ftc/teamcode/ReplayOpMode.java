package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public abstract class ReplayOpMode extends CommonOpMode {
    private double replaySpeed = 1; // Change this value for how quickly you want to replay the recorded code
    // It has a limit and should not be too high
    private int index = 0;
    ArrayList<String> motorInfos, servoInfos;
    ArrayList<ArrayList> mainArr = new ArrayList<>(),
        motorPowerArr,
        motorPosArr,
        servoPosArr;

    ArrayList<DcMotor> motorList = new ArrayList<>();
    ArrayList<Servo> servoList = new ArrayList<>();
    public void initialize() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/auto_data.ser." + title())));
            mainArr = (ArrayList) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        motorPosArr = mainArr.get(0);
        servoPosArr = mainArr.get(1);
        motorInfos = mainArr.get(2);
        servoInfos = mainArr.get(3);

        for (String info : motorInfos) {
            for (DcMotor motor : hardwareMap.getAll(DcMotor.class)) {
                if (motor.getConnectionInfo().equals(info) && motor.getDeviceName().equals("Motor")) {
                    motorList.add(motor);
                }
            }
        }

        for (String info : servoInfos) {
            for (Servo servo : hardwareMap.getAll(Servo.class)) {
                if (servo.getConnectionInfo().equals(info) && servo.getDeviceName().equals("Servo")) {
                    servoList.add(servo);
                }
            }
        }

        for (DcMotor motor : motorList) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setTargetPosition(0);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }
    public void runner() {
        if (index == motorPowerArr.get(0).size()) requestOpModeStop();
        else {
            for (int i = 0; i < motorList.size(); i++) {
                motorList.get(i).setTargetPosition((int) motorPosArr.get(i).get(index));
            }

            int diffSum = 11;

            double[] tList = new double[motorList.size()];

            for (int i = 0; i < motorList.size(); i++) {
                tList[i] = DrivingCurve.getT(motorList.get(i));
                DrivingThread subThread = new DrivingThread(motorList.get(i), tList[i]);
                Thread mainThread = new Thread(subThread);

                mainThread.start();
            }

            for (int i = 0; i < servoList.size(); i++) {
                servoList.get(i).setPosition((double) servoPosArr.get(i).get(index));
            }

            while (diffSum > 10) {
                for (int i = 0; i < motorList.size(); i++) {
                    diffSum += Math.abs(motorList.get(i).getTargetPosition() - motorList.get(i).getCurrentPosition());
                }
            }

            index++;
        }
    }
    public abstract String title();
}