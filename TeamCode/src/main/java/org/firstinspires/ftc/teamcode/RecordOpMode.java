package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/*
    This OpMode allows you to record some set of movements that you do.

    There are 2 slots to record into by default, red and blue

    It is difficult to fit more options that can be controlled by a single controller,
    but I hope to add a UI that can be opened on a computer to make custom file names and
    automatically generate files.
*/
@TeleOp(name="Record OpMode")

public class RecordOpMode extends ManualControlOpMode {
    private BufferedOutputStream bos;
    private ObjectOutputStream oos;
    private ArrayList<DcMotor> motorList = new ArrayList<>();
    private ArrayList<Servo> servoList = new ArrayList<>();
    private ArrayList<ArrayList> mainArr = new ArrayList<>(),
            motorPosArr = new ArrayList<>(),
            servoPosArr = new ArrayList<>();
    private String title = "";
    public void initialize() {
        motorList.addAll(hardwareMap.getAll(DcMotor.class));
        servoList.addAll(hardwareMap.getAll(Servo.class));

        for (DcMotor motor : motorList) {
            motorPosArr.add(new ArrayList<Integer>());
        }
        for (Servo servo : servoList) {
            servoPosArr.add(new ArrayList<Double>());
        }
    }
    public void saveWithTitle(String t) {
        title = t;

        telemetry.addLine("Programming in [" + title + "] slot...");
        telemetry.update();

        sleep(1500);

        try {
            while (motorPosSum(0) == motorPosSum(1) && servoPosSum(0) == servoPosSum(1)) {
                for (int i = 0; i < motorPosArr.size(); i++) {
                    motorPosArr.get(i).remove(0);
                }
                for (int i = 0; i < servoPosArr.size(); i++) {
                    servoPosArr.get(i).remove(0);
                }
            }
        } catch (Exception e) {}

        mainArr.add(motorPosArr);
        mainArr.add(servoPosArr);

        ArrayList<String> motorInfos = new ArrayList<>(), servoInfos = new ArrayList<>();
        for (DcMotor motor : motorList) {
            motorInfos.add(motor.getConnectionInfo());
        }
        for (Servo servo : servoList) {
            servoInfos.add(servo.getConnectionInfo());
        }

        mainArr.add(motorInfos);
        mainArr.add(servoInfos);

        try {
            bos = new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/auto_data.ser." + title));
            oos = new ObjectOutputStream(bos);
            oos.writeObject(mainArr);
            oos.close();
        } catch (IOException e) {
            telemetry.addLine(e.getStackTrace().toString());
        }

        requestOpModeStop();
    }
    @Override
    public void runner() {
        super.runner();

        if (gamepad1.share) {
            for (int i = 0; i < motorList.size(); i++) {
                motorPosArr.get(i).add(motorList.get(i).getCurrentPosition());
            }
            for (int i = 0; i < servoList.size(); i++) {
                servoPosArr.get(i).add(servoList.get(i).getPosition());
            }
            sleep(2000);
        }

        if (gamepad1.share && gamepad1.x) {
            saveWithTitle("blue"); // Saved into blue file
        }
        if (gamepad1.share && gamepad1.y) {
            saveWithTitle("red"); // Saved into red file
        }
    }
    public int motorPosSum(int index) {
        int sum = 0;

        for (int i = 0; i < motorPosArr.size(); i++) {
            sum += (int)motorPosArr.get(i).get(index);
        }

        return sum;
    }
    public double servoPosSum(int index) {
        double sum = 0;

        for (int i = 0; i < servoPosArr.size(); i++) {
            sum += (double)servoPosArr.get(i).get(index);
        }

        return sum;
    }
}
