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
    private String title = "";
    private boolean firstTime = true;
    Thread recorder;
    Recorder subRecorder;
    public void initialize() {
        motorList.addAll(hardwareMap.getAll(DcMotor.class));
        servoList.addAll(hardwareMap.getAll(Servo.class));
        subRecorder = new Recorder(motorList, servoList);
        recorder = new Thread(subRecorder);
    }
    public void saveWithTitle(String t) {
        recorder.interrupt();

        title = t;

        telemetry.addLine("Programming in [" + title + "] slot...");
        telemetry.update();

        sleep(1500);

        try {
            bos = new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/auto_data.ser." + title));
            oos = new ObjectOutputStream(bos);
            oos.writeObject(subRecorder.compileMainArr());
            oos.close();
        } catch (IOException e) {
            telemetry.addLine(e.getStackTrace().toString());
        }

        requestOpModeStop();
    }
    @Override
    public void runner() {
        if (firstTime) {
            recorder.start();

            firstTime = false;
        }
        
        super.runner();

        if (gamepad1.share && gamepad1.x) {
            saveWithTitle("blue"); // Saved into blue file
        }
        if (gamepad1.share && gamepad1.y) {
            saveWithTitle("red"); // Saved into red file
        }
    }
}
