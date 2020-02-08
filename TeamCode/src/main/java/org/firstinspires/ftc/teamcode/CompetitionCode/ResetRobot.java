package org.firstinspires.ftc.teamcode.CompetitionCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Assemblies.Grabber;
import org.firstinspires.ftc.teamcode.Assemblies.Robot;
import org.firstinspires.ftc.teamcode.basicLibs.Blinkin;
import org.firstinspires.ftc.teamcode.basicLibs.TeamGamepad;
import org.firstinspires.ftc.teamcode.basicLibs.teamUtil;

@TeleOp(name="ResetRobot", group="a")

public class ResetRobot extends LinearOpMode {
    public static final double SCALE_DOWN_CONSTANT = 0.3;
    int level = 0;
    Grabber.GrabberRotation grabberRotation;
    TeamGamepad teamGamePad;

    Robot robot;

    public void initialize() {

        teamUtil.init(this);
        teamUtil.telemetry.addLine("Initializing Op Mode...please wait");
        teamUtil.telemetry.update();
        teamUtil.theBlinkin.setSignal(Blinkin.Signals.YELLOW);

        robot = new Robot(this);

        teamGamePad = new TeamGamepad(this);

        robot.init(false);
        robot.latch.latchUp();
        teamUtil.theBlinkin.setSignal(Blinkin.Signals.READY_TO_START);
        teamUtil.initPerf();

    }

    @Override
    public void runOpMode() throws InterruptedException {



        initialize();
        grabberRotation = Grabber.GrabberRotation.INSIDE;

//        teamUtil.nukeRobot();
        waitForStart();
    }
}
