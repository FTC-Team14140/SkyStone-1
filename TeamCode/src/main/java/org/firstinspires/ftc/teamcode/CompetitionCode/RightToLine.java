package org.firstinspires.ftc.teamcode.CompetitionCode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Assemblies.Robot;
import org.firstinspires.ftc.teamcode.basicLibs.Blinkin;
import org.firstinspires.ftc.teamcode.basicLibs.teamUtil;

@Autonomous(name="RightToLine", group ="x")
public class RightToLine extends LinearOpMode {

    Robot robot;

    public void initialize() {

        teamUtil.init(this);
        teamUtil.telemetry.addLine("Initializing Op Mode...please wait");
        teamUtil.telemetry.update();
        teamUtil.theBlinkin.setSignal(Blinkin.Signals.INIT);

        robot = new Robot(this);



        robot.init(false);
        robot.latch.latchUp();
        teamUtil.theBlinkin.setSignal(Blinkin.Signals.LEVEL_10);
        teamUtil.initPerf();

    }

    @Override
    public void runOpMode() throws InterruptedException {

        initialize();

        teamUtil.telemetry.addLine("Ready to Start");
        teamUtil.telemetry.update();
        waitForStart();

        while(!robot.drive.bottomColor.isOnTape()){
            teamUtil.theBlinkin.setSignal(Blinkin.Signals.LEVEL_10);
            robot.drive.driveRight(0.35);
        }
        robot.drive.stopMotors();

    }
}
