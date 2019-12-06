package org.firstinspires.ftc.teamcode.CompetitionCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Assemblies.Grabber;
import org.firstinspires.ftc.teamcode.Assemblies.Latch;
import org.firstinspires.ftc.teamcode.Assemblies.LiftSystem;
import org.firstinspires.ftc.teamcode.Assemblies.RobotDrive;
import org.firstinspires.ftc.teamcode.basicLibs.TeamGamepad;
import org.firstinspires.ftc.teamcode.basicLibs.teamUtil;

public class RobotTeleopLinear extends LinearOpMode {
    public static final double SCALE_DOWN_CONSTANT = 0.3;
    RobotDrive robot;
    Latch latch;
    int level = 0;
    TeamGamepad teamGamePad;
    LiftSystem liftSystem;
    Grabber.GrabberRotation grabberRotation;

    public void initialize() {
        teamUtil.theOpMode = this;
        robot = new RobotDrive(hardwareMap, telemetry);
        liftSystem = new LiftSystem(hardwareMap, telemetry);
        latch = new Latch(hardwareMap, telemetry);
        teamGamePad = new TeamGamepad(this);


        robot.initDriveMotors();
        robot.initImu();

        latch.initLatch();

        liftSystem.initLiftSystem();

        teamUtil.initPerf();
        robot.resetHeading();

    }
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Initializing Op Mode");
        telemetry.update();
        initialize();

        telemetry.addLine("Ready to Start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            //robot.scaleMovement(MAX_POWER, DRIVE_POWER);
            telemetry.addData("Heading:", robot.getHeading());
///////////////////////////////////////////////////////////////////////
            teamGamePad.gamepadLoop();


///////////////////////////////////////////////////////////////////////
            //this code is for the drive
            if (gamepad1.left_trigger > 0.5) {
                robot.universalJoystick(gamepad1.left_stick_x,
                        gamepad1.left_stick_y,
                        gamepad1.right_stick_x, SCALE_DOWN_CONSTANT,
                        robot.getHeading());

            } else {
                robot.universalJoystick(gamepad1.left_stick_x,
                        gamepad1.left_stick_y,
                        gamepad1.right_stick_x, 1,
                        robot.getHeading());

            }


            if (gamepad1.left_stick_button && gamepad1.right_stick_button) {
                robot.resetHeading();
            }

//////////////////////////////////////////////////////////////////////
            //this code is for the foundation latch
            if (gamepad1.dpad_down) {
                latch.latchDown();

            } else if (gamepad1.dpad_up) {
                latch.latchUp();
            }

            if (gamepad1.dpad_left || gamepad1.dpad_right) {
                latch.latchMiddle();
            }
/////////////////////////////////////////////////////////////////////
            //this code is for the lift system assembly

            if (teamGamePad.gamepad2dpad_upBounced()) {
                level += 1;
                teamUtil.log("Level Up :D *cue final fantasy music");
            }
            if (teamGamePad.gamepad2dpad_downBounced()) {
                level -= 1;
                teamUtil.log("Level decreased :C");
            }
            if (gamepad2.y) {
                grabberRotation = Grabber.GrabberRotation.OUTSIDE;
            }
            if (gamepad2.b) {
                grabberRotation = Grabber.GrabberRotation.MIDDLE;
            }
            if (gamepad2.a) {
                grabberRotation = Grabber.GrabberRotation.INSIDE;
            }
            if (gamepad2.left_stick_button) {
                liftSystem.hoverOverFoundationNoWait(level, grabberRotation, 7000);
                teamUtil.log("tried to deploy");
                teamUtil.log("level: " + level);
            }
            if (gamepad2.left_bumper) {
                liftSystem.prepareToGrabNoWait(9000);
            }
            if (gamepad2.right_trigger > 0.5) {
                liftSystem.grabAndStowNoWait("narrow", 7000);
            }
            if (gamepad2.right_bumper) {
                liftSystem.grabAndStowNoWait("wide", 5000);
            }
            if (gamepad2.right_stick_button) {
                liftSystem.drop();
            }
            if (gamepad2.x) {
                liftSystem.liftDown();
            }

/////////////////////////////////////////////////////////////////////
            //this code is the telemetry
//        latch.latchTelemetry();
//        robot.driveTelemetry();
            telemetry.addData("level:", level);
            telemetry.addData("grabber rotation", grabberRotation);
            telemetry.update();

            teamUtil.trackPerf();


        }
    }

}
