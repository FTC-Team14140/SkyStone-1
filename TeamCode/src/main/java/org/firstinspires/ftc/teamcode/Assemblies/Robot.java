package org.firstinspires.ftc.teamcode.Assemblies;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.basicLibs.Blinkin;
import org.firstinspires.ftc.teamcode.basicLibs.SkystoneDetector;
import org.firstinspires.ftc.teamcode.basicLibs.runningVoteCount;
import org.firstinspires.ftc.teamcode.basicLibs.teamUtil;

// A class to encapsulate the entire 
// This class is designed to be used ONLY in a linearOpMode (for Auto OR Teleop)
public class Robot {
    public static final double DISTANCE_TO_BLOCK = 1.125;
    public static final int MIN_DISTANCE_FOR_AUTO_DROPOFF = 6;
    public final double DISTANCE_TO_FOUNDATION = 2.5;
    public static final double AUTOINTAKE_POWER = 0.33;
    public static final double AUTOINTAKE_SIDEWAYS_POWER = 0.33;
    int path;
    public boolean hasBeenInitialized = false;
    private static boolean justRanAuto;

    static {
        justRanAuto = false;
    }

    public LiftSystem liftSystem;
    public RobotDrive drive;
    public Latch latch;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    SkystoneDetector detector;
    boolean timedOut = false;

    public final int MIN_DISTANCE_FOR_AUTO_PICKUP = 0;
    public final int MAX_DISTANCE_FOR_AUTO_DROPOFF = 6;

    public Robot(LinearOpMode opMode) {
        teamUtil.log("Constructing Robot");
        // stash some context for later
        teamUtil.theOpMode = opMode;
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        teamUtil.log("Constructing Assemblies");
        liftSystem = new LiftSystem(hardwareMap, telemetry);
        drive = new RobotDrive(hardwareMap, telemetry);
        latch = new Latch(hardwareMap, telemetry);
        teamUtil.log("Constructing Assemblies - Finished");
        teamUtil.log("Constructed Robot - Finished");
    }

    // Call this before first use!
    public void init(boolean usingDistanceSensors) {
        teamUtil.log("Initializing Robot");
        liftSystem.initLiftSystem(!justRanAuto);
        justRanAuto = false;
        drive.initImu();
        drive.initDriveMotors();
        if (usingDistanceSensors) {
            drive.initSensors(false);
        } else {
            drive.initSensors(true);
        }
        drive.resetHeading();
        latch.initLatch();
        teamUtil.log("Initializing Robot - Finished");
    }

    ////////////////////////////////////////////////////
    // Automagically align on a stone, pick it up, and stow it.
    // stop if we don't finish maneuvering within timeOut msecs
    public void autoIntake(long timeOut) {
        long timeOutTime = System.currentTimeMillis() + timeOut;

        // Rotate is not that accurate at the moment, so we are relying on the driver


        // Get the lift system ready to grab if it isn't already...
        liftSystem.prepareToGrabNoWait(7000, Grabber.GrabberRotation.INSIDE);

        // determine which side we are lined up on

        // line up using the front left sensor


        if (drive.frontLeftDistance.getDistance() < MIN_DISTANCE_FOR_AUTO_PICKUP) {
            teamUtil.log("autointake -- see something in front left!");
            drive.moveToDistance(drive.frontLeftDistance, DISTANCE_TO_BLOCK, AUTOINTAKE_POWER, 5000);
            while ((drive.frontLeftDistance.getDistance() < MIN_DISTANCE_FOR_AUTO_PICKUP) && teamUtil.keepGoing(timeOutTime) && teamUtil.theOpMode.opModeIsActive()) {
                drive.driveLeft(AUTOINTAKE_POWER);
            }
            drive.moveInchesLeft(AUTOINTAKE_SIDEWAYS_POWER, 2, timeOutTime - System.currentTimeMillis());

            // line up using the front right sensor
        } else if (drive.frontRightDistance.getDistance() < MIN_DISTANCE_FOR_AUTO_PICKUP) {
            teamUtil.log("autointake -- see something in front right!");
            drive.moveToDistance(drive.frontRightDistance, DISTANCE_TO_BLOCK, AUTOINTAKE_POWER, 5000);
            while ((drive.frontRightDistance.getDistance() < MIN_DISTANCE_FOR_AUTO_PICKUP) && teamUtil.keepGoing(timeOutTime) && teamUtil.theOpMode.opModeIsActive()) {
                drive.driveRight(AUTOINTAKE_POWER);
            }
            drive.moveInchesRight(AUTOINTAKE_SIDEWAYS_POWER, 2, timeOutTime - System.currentTimeMillis());
        }
        drive.moveToPickUpDistance(5000);
        teamUtil.sleep(375);
        drive.moveToPickUpDistance(2000);
        // In case we were still getting the lift system ready to grab
        while (liftSystem.state != LiftSystem.LiftSystemState.IDLE) {
            teamUtil.sleep(100);
        }

        liftSystem.grabAndStowNoWait(7000); // return control to driver
        teamUtil.sleep(1000); // after a short pause to make sure we have picked up the stone

    }

    public void autoDropOffLeft(int level, long timeOut) {
        long timeOutTime = System.currentTimeMillis() + timeOut;

        // Rotate is not that accurate at the moment, so we are relying on the driver


        // Get the lift system ready to grab if it isn't already...


        // determine which side we are lined up on

        // line up using the front left sensor
        teamUtil.log("autointake -- see something in front left!");
        liftSystem.hoverOverFoundationNoWait(level, Grabber.GrabberRotation.INSIDE, 8500);
        teamUtil.sleep(2000);
        drive.moveToDistance(drive.frontLeftDistance, DISTANCE_TO_FOUNDATION, AUTOINTAKE_POWER, 5000);

        while ((drive.frontLeftDistance.getDistance() < MAX_DISTANCE_FOR_AUTO_DROPOFF) && teamUtil.keepGoing(timeOutTime) && teamUtil.theOpMode.opModeIsActive()) {
            drive.driveLeft(AUTOINTAKE_POWER);
        }
        drive.moveInchesLeft(AUTOINTAKE_SIDEWAYS_POWER, 1, 8000);
        //drive.moveInchesLeft(AUTOINTAKE_SIDEWAYS_POWER, 5, timeOutTime - System.currentTimeMillis());

        // line up using the front right sensor
    }

    // In case we were still getting the lift system ready to grab


    public void autoDropOffRight(int level, long timeOut) {
        long timeOutTime = System.currentTimeMillis() + timeOut;

        // Rotate is not that accurate at the moment, so we are relying on the driver


        // Get the lift system ready to grab if it isn't already...


        // determine which side we are lined up on

        // line up using the front left sensor
        teamUtil.log("autointake -- see something in front left!");
        liftSystem.hoverOverFoundationNoWait(level, Grabber.GrabberRotation.INSIDE, 8500);
        teamUtil.sleep(2000);
        drive.moveToDistance(drive.frontRightDistance, DISTANCE_TO_FOUNDATION, AUTOINTAKE_POWER, 5000);

        while ((drive.frontRightDistance.getDistance() < MAX_DISTANCE_FOR_AUTO_DROPOFF) && teamUtil.keepGoing(timeOutTime) && teamUtil.theOpMode.opModeIsActive()) {
            drive.driveRight(AUTOINTAKE_POWER);
        }
        drive.moveInchesRight(AUTOINTAKE_SIDEWAYS_POWER, 1, 8000);
        //drive.moveInchesLeft(AUTOINTAKE_SIDEWAYS_POWER, 5, timeOutTime - System.currentTimeMillis());

        // line up using the front right sensor
    }

    public void foundation() {
        boolean RED = (teamUtil.alliance == teamUtil.Alliance.RED);

        if (RED) {
            drive.moveInchesLeft(0.5, 11, 4000);

        } else {
            drive.moveInchesRight(0.5, 11, 4000);
        }

        drive.newAccelerateInchesBackward(2200, 25, 0, 3000);
        drive.newAccelerateInchesBackward(1500, 5, 0, 3000);
        latch.latchDown();
        teamUtil.pause(750);

        drive.rotateToZero();

        if (RED) {
            drive.newAccelerateInchesForward(2200, 25, 5, 3000);
            drive.newAccelerateInchesForward(1500, 11, 0, 3000);
        } else {
            drive.newAccelerateInchesForward(2200, 25, 355, 3000);
            drive.newAccelerateInchesForward(1500, 11, 0, 3000);
        }

        latch.latchUp();
        teamUtil.pause(1000);

        if (RED) {
            drive.moveInchesRight(0.5, 10, 2300);
        } else {
            drive.moveInchesLeft(0.5, 10, 2300);
        }
        while (!drive.bottomColor.isOnTape()) {
            if (RED) {
                drive.driveRight(0.6);

            } else {
                drive.driveLeft(0.6);

            }
        }

        if (RED) {
            drive.moveInchesLeft(0.5, 3, 2000);
        } else {
            drive.moveInchesRight(0.5, 3, 2000);
        }
        drive.stopMotors();
        justRanAuto = true;


    }

    public void dragFoundationBackSkystonePath() {
        boolean RED = (teamUtil.alliance == teamUtil.Alliance.RED);
        latch.latchDown();
        teamUtil.pause(750);

        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_FIELD);

        if (RED) {
            drive.newAccelerateInchesForward(2200, 25, 185, 3000);
            drive.newAccelerateInchesForward(1500, 11, 180, 3000);
        } else {
            drive.newAccelerateInchesForward(2200, 25, 175, 3000);
            drive.newAccelerateInchesForward(1500, 11, 180, 3000);
        }

        latch.latchUp();
        teamUtil.pause(1000);

        if (RED) {
            drive.moveInchesRight(0.5, 10, 2300);
        } else {
            drive.moveInchesLeft(0.5, 10, 2300);
        }
        while (!drive.bottomColor.isOnTape()) {
            if (RED) {
                drive.driveRight(0.6);

            } else {
                drive.driveLeft(0.6);

            }
        }
        drive.stopMotors();
        justRanAuto = true;

    }


    public void positionToFoundation() {
        liftSystem.hoverOverFoundationNoWait(0, Grabber.GrabberRotation.INSIDE, 5000);
        drive.closeToDistanceOr(drive.frontLeftDistance, drive.frontRightDistance, 4, 0.3, 4000);

        if ((drive.frontRightDistance.getDistance() < MAX_DISTANCE_FOR_AUTO_DROPOFF) && (drive.frontLeftDistance.getDistance() > MAX_DISTANCE_FOR_AUTO_DROPOFF)) {
            drive.moveToDistance(drive.frontRightDistance, DISTANCE_TO_FOUNDATION, AUTOINTAKE_POWER, 5000);

            while ((drive.frontRightDistance.getDistance() < MAX_DISTANCE_FOR_AUTO_DROPOFF) && (drive.frontLeftDistance.getDistance() > MAX_DISTANCE_FOR_AUTO_DROPOFF) && teamUtil.keepGoing(6500 + System.currentTimeMillis()) && teamUtil.theOpMode.opModeIsActive()) {
                drive.driveRight(0.5);
                teamUtil.log("FrontLeftDistance: " + drive.frontLeftDistance.getDistance());
            }
            drive.moveInchesRight(0.6, 7.5, 4500);

        } else if ((drive.frontRightDistance.getDistance() > MAX_DISTANCE_FOR_AUTO_DROPOFF) && (drive.frontLeftDistance.getDistance() < MAX_DISTANCE_FOR_AUTO_DROPOFF)) {
            drive.moveToDistance(drive.frontLeftDistance, DISTANCE_TO_FOUNDATION, AUTOINTAKE_POWER, 5000);

            while ((drive.frontRightDistance.getDistance() > MAX_DISTANCE_FOR_AUTO_DROPOFF) && (drive.frontLeftDistance.getDistance() < MAX_DISTANCE_FOR_AUTO_DROPOFF) && teamUtil.keepGoing(6500 + System.currentTimeMillis()) && teamUtil.theOpMode.opModeIsActive()) {
                drive.driveLeft(0.5);
                teamUtil.log("FrontLeftDistance: " + drive.frontLeftDistance.getDistance());
            }
            drive.moveInchesLeft(0.6, 11.5, 4500);
        } else {
            teamUtil.log("didn't see anything on distance sensors");
            return;
        }
    }

    public void doubleSkystonePathOne() {
        boolean RED = (teamUtil.alliance == teamUtil.Alliance.RED);

        liftSystem.lift.slightlyMoveLiftBaseUp(1, 2000);
        liftSystem.grabber.slightlyOpenGrabber();
        teamUtil.sleep(750);

        /////////////////////////////////////////////////////////
        // drive back and position for the second stone
        liftSystem.lift.moveLiftBaseDownNoWait(0.5, 3000);
        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_DEPOT);

        drive.newAccelerateInchesForward(2200, 69, RED ? 91 : 269, 6000);
        drive.moveToDistanceFailover(7, 2200, RED ? 89 : 271, true, 5000);
        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_WALL);
//
        if (RED) {
            drive.moveInchesRight(0.5, 2, 3000);
        } else {
            drive.moveInchesLeft(0.5, 4.5, 3000);
        }
        drive.newAccelerateInchesForward(-650, 9, 180, 4000);

        if (RED) {
            latch.latchTwoPushbot();
        } else {
            latch.latchOnePushbot();
        }

        teamUtil.pause(500);
        drive.newAccelerateInchesForward(650, 5.5, 180, 4000);
        drive.newRotateTo(RED ? 70 : 290);
        latch.latchUp();
        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_DEPOT);
        drive.newAccelerateInchesForward(-2200, 75, RED ? 89 : 269, 6097);



        while (!drive.bottomColor.isOnTape()) {
            drive.driveForward(0.5);
        }
        drive.stopMotors();

    }

    public void foundationStone() {
        boolean RED = (teamUtil.alliance == teamUtil.Alliance.RED);
        boolean useDistanceSensorsALot = false;
        double distance = 0;
        detector = new SkystoneDetector(telemetry, hardwareMap);
        detector.initDetector();
        detector.activateDetector();
        runningVoteCount voteCount = new runningVoteCount(3000);


        teamUtil.telemetry.addLine("Ready to Start");
        teamUtil.telemetry.update();

        // Start detecting but wait for start of match to move
        while (!teamUtil.theOpMode.opModeIsActive() && !teamUtil.theOpMode.isStopRequested()) {
            teamUtil.sleep(200);

            int vote = RED ? detector.detectRed() : detector.detectBlue();
            if (vote > 0) {
                voteCount.vote(vote);
            }
            int[] pathVotes = voteCount.getTotals();


            teamUtil.log(" Totals:" + pathVotes[1] + "/" + pathVotes[2] + "/" + pathVotes[3]);

            if (pathVotes[1] > 0 && pathVotes[2] == 0 && pathVotes[3] == 0) { //if we get NO path 2 or path 3 votes, it's path 1
                if (RED) {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_1);
                } else {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_1);
                }
                path = 1;
                teamUtil.log("PATH: " + 1);
                telemetry.addLine("path 1");
            } else if (pathVotes[3] > pathVotes[2] * 2) { //if we get tons more path 3 than path 2 votes, it's path 3
                if (RED) {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_3);
                } else {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_3);
                }
                path = 3;
                teamUtil.log("PATH: " + 3);
                telemetry.addLine("path 3");

            } else { //path 2 gets crappy data so if no other conditions fit, it's path 2
                if (RED) {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_2);
                } else {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_2);
                }
                path = 2;
                teamUtil.log("PATH: " + 2);
                telemetry.addLine("path 2");

            }

//            int detected = (RED ? detector.detectRed() : detector.detectBlue());
//            if (detected > 0) {
//                path = detected;
//            }
//            if (RED) {
//                switch (path) {
//                    case 1 : teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_1); break;
//                    case 2 : teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_2); break;
//                    case 3 : teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_3); break;
//                }
//            } else {
//                switch (path) {
//                    case 1:
//                        teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_1);
//                        break;
//                    case 2:
//                        teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_2);
//                        break;
//                    case 3:
//                        teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_3);
//                        break;
//
//                }
//            }


        }
        if (RED) {
            teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_AUTO);
        } else {
            teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_AUTO);
        }

        detector.shutdownDector();

        if (teamUtil.theOpMode.isStopRequested()) {
            return;
        }

        /////////////////////////////////////////////////////////
        // Move to the first Skystone and grab it
        liftSystem.prepareToGrabNoWait(4000, Grabber.GrabberRotation.INSIDE);
        switch (path) {
            case 3:
                // move straight forward
                break;
            case 2:
                if (RED)
                    drive.moveInchesLeft(0.35, 7, 2300);
                else
                    drive.moveInchesRight(0.35, 7, 2300);

                //TODO: FIX FOR BLUE
                break;
            case 1:
                if (RED) {
                    drive.moveInchesLeft(0.35, 15, 2300);
                } else {
                    //move right to stone to line up, then back up('cause we drift forward a little :C)
                    drive.moveInchesRight(0.35, 15, 5000);
//                    drive.newAccelerateInchesForward(-2200, 1, 0, 3000);
                }
                //TODO: FIX FOR BLUE
        }
        // TODO: will the strafes to the left/right above effect the location of the robot relative to the wall (and later the skybridge)?
        // TODO: If so, perhaps the rear distance sensor could come in handy here to normalize that distance before we move forward
        drive.newAccelerateInchesForward(1100, 32, 0, 3000);
        liftSystem.grabAndStowNoWait(4500);
        teamUtil.pause(750);

        /////////////////////////////////////////////////////////
        // Deliver the first stone to the building side of the field
        drive.newAccelerateInchesForward(-2200, (RED ? 5 : 7/*TODO*/), 0, 3000);

        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_BUILDING);
        switch (path) { // TODO: OR, we could go back to finding the tape line as we cross it and moving a set distance from there...
            case 3:
                distance = (RED ? 75.5 : 75.5/*TODO*/);
                break;
            case 2:
                distance = (RED ? 83.5 : 83.5/*TODO*/);
                break;// TODO RED + 8?
            case 1:
                distance = (RED ? 95.5 : 95.5/*TODO*/);
                break;// TODO RED + 8?
        }
        drive.newAccelerateInchesForward(2200, distance, RED ? 268 : 88, 5000);
        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_FIELD);
        drive.newAccelerateInchesForward(2200, RED ? 5 : 10, 0, 3000);
//        liftSystem.lift.slightlyMoveLiftBaseUp(1, 2000);
        liftSystem.grabber.slightlyOpenGrabber();

        drive.newAccelerateInchesForward(-2200, 7, 0, 3000);
//        liftSystem.lift.moveLiftBaseDownNoWait(0.5, 3000);
        liftSystem.grabber.grabberStow();
        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_WALL);
        drive.newAccelerateInchesForward(-2200, 8.5, 180, 3000);

        latch.latchDown();
        teamUtil.pause(750);

        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_WALL);

        if (RED) {
            drive.newAccelerateInchesForward(2200, 25, 185, 3000);
            drive.newAccelerateInchesForward(1000, 9.5, 180, 3000);
        } else {
            drive.newAccelerateInchesForward(2200, 25, 175, 3000);
            drive.newAccelerateInchesForward(1000, 9.5, 180, 3000);
        }

        latch.latchUp();
        teamUtil.pause(1000);

        if (RED) {
            drive.moveInchesRight(0.5, 36, 2300);
        } else {
            drive.moveInchesLeft(0.5, 36, 2300);
        }

        drive.newAccelerateInchesForward(-1500, RED ? 26 : 25, 180, 3500);

        while (!drive.bottomColor.isOnTape()) {
            if (RED) {
                drive.driveRight(0.6);

            } else {
                drive.driveLeft(0.6);

            }
        }
        drive.stopMotors();
        justRanAuto = true;


    }

    public void doubleSkystone() {

        boolean RED = (teamUtil.alliance == teamUtil.Alliance.RED);
        boolean useDistanceSensorsALot = false;
        double distance = 0;
        detector = new SkystoneDetector(telemetry, hardwareMap);
        detector.initDetector();
        detector.activateDetector();
        runningVoteCount voteCount = new runningVoteCount(3000);


        teamUtil.telemetry.addLine("Ready to Start");
        teamUtil.telemetry.update();

        // Start detecting but wait for start of match to move
        while (!teamUtil.theOpMode.opModeIsActive() && !teamUtil.theOpMode.isStopRequested()) {
            teamUtil.sleep(200);

            int vote = RED ? detector.detectRed() : detector.detectBlue();
            if (vote > 0) {
                voteCount.vote(vote);
            }
            int[] pathVotes = voteCount.getTotals();


            teamUtil.log(" Totals:" + pathVotes[1] + "/" + pathVotes[2] + "/" + pathVotes[3]);

            if (pathVotes[1] > 0 && pathVotes[2] == 0 && pathVotes[3] == 0) { //if we get NO path 2 or path 3 votes, it's path 1
                if (RED) {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_1);
                } else {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_1);
                }
                path = 1;
                teamUtil.log("PATH: " + 1);
                telemetry.addLine("path 1");
            } else if (pathVotes[3] > pathVotes[2] * 2) { //if we get tons more path 3 than path 2 votes, it's path 3
                if (RED) {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_3);
                } else {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_3);
                }
                path = 3;
                teamUtil.log("PATH: " + 3);
                telemetry.addLine("path 3");

            } else { //path 2 gets crappy data so if no other conditions fit, it's path 2
                if (RED) {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_2);
                } else {
                    teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_2);
                }
                path = 2;
                teamUtil.log("PATH: " + 2);
                telemetry.addLine("path 2");

            }

//            int detected = (RED ? detector.detectRed() : detector.detectBlue());
//            if (detected > 0) {
//                path = detected;
//            }
//            if (RED) {
//                switch (path) {
//                    case 1 : teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_1); break;
//                    case 2 : teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_2); break;
//                    case 3 : teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_PATH_3); break;
//                }
//            } else {
//                switch (path) {
//                    case 1:
//                        teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_1);
//                        break;
//                    case 2:
//                        teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_2);
//                        break;
//                    case 3:
//                        teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_PATH_3);
//                        break;
//
//                }
//            }


        }
        if (RED) {
            teamUtil.theBlinkin.setSignal(Blinkin.Signals.RED_AUTO);
        } else {
            teamUtil.theBlinkin.setSignal(Blinkin.Signals.BLUE_AUTO);
        }

        detector.shutdownDector();

        if (teamUtil.theOpMode.isStopRequested()) {
            return;
        }

        /////////////////////////////////////////////////////////
        // Move to the first Skystone and grab it
        liftSystem.prepareToGrabNoWait(4000, Grabber.GrabberRotation.INSIDE);
        switch (path) {
            case 3:
                // move straight forward
                break;
            case 2:
                if (RED)
                    drive.moveInchesLeft(0.35, 7, 2300);
                else
                    drive.moveInchesRight(0.35, 7, 2300);

                //TODO: FIX FOR BLUE
                break;
            case 1:
                if (RED) {
                    drive.moveInchesLeft(0.35, 15, 2300);
                } else {
                    //move right to stone to line up, then back up('cause we drift forward a little :C)
                    drive.moveInchesRight(0.35, 15, 5000);
//                    drive.newAccelerateInchesForward(-2200, 1, 0, 3000);
                }
                //TODO: FIX FOR BLUE
        }
        // TODO: will the strafes to the left/right above effect the location of the robot relative to the wall (and later the skybridge)?
        // TODO: If so, perhaps the rear distance sensor could come in handy here to normalize that distance before we move forward
        drive.newAccelerateInchesForward(1100, 32, 0, 3000);
        liftSystem.grabAndStowNoWait(4500);
        teamUtil.sleep(750);

        /////////////////////////////////////////////////////////
        // Deliver the first stone to the building side of the field
        drive.newAccelerateInchesForward(-2200, (RED ? 5 : 7/*TODO*/), 0, 3000);

        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_BUILDING);
        switch (path) { // TODO: OR, we could go back to finding the tape line as we cross it and moving a set distance from there...
            case 3:
                distance = (RED ? 44.5 : 44.5/*TODO*/);
                break;
            case 2:
                distance = (RED ? 52.5 : 52.5/*TODO*/);
                break;// TODO RED + 8?
            case 1:
                distance = (RED ? 60.5 : 62.5/*TODO*/);
                break;// TODO RED + 8?
        }
        drive.newAccelerateInchesForward(2200, distance, RED ? 268 : 88, 5000);

        if (path == 1) {
            doubleSkystonePathOne();
            return;
        }

        //TODO: PATH ONE DIVERGES   <---------------------------------------------------------------------------------------------------------------------------------------------------------------
        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_FIELD);

        //If Blue, move forward a little 'cause long strafe will be towards center of field
        if (!RED && path != 1) {
            drive.newAccelerateInchesForward(2200, 4, 0, 2000);
        }
        //lift base up a teensy bit and drop off stone
        liftSystem.lift.slightlyMoveLiftBaseUp(1, 2000);
        liftSystem.grabber.slightlyOpenGrabber();
        teamUtil.sleep(750);

        /////////////////////////////////////////////////////////
        // drive back and position for the second stone
        liftSystem.lift.moveLiftBaseDownNoWait(0.5, 3000);

        //if it's path 1, we give up on double skystone ¯\_(ツ)_/¯
        if (path == 1) {
//            drive.moveInchesRight(0.5, 18, 3000); //TODO: OUT OF ORDER AUTO
            while (!drive.bottomColor.isOnTape()) {
                if (RED) {
                    drive.driveLeft(0.75);
                } else {
                    drive.driveRight(0.75);
                }
            }
            if (!RED) {
                drive.moveInchesLeft(0.5, 3, 2000);
            }
            drive.stopMotors();
            return;
        }
        //if RED, move backward a little to avoid collision with skybridge
        if (RED) {
            drive.newAccelerateInchesForward(-2200, 1, 0, 5000);
        }

        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_DEPOT);
        liftSystem.moveLiftBaseUpWithDelay(1000, 4500);
        switch (path) { // TODO: OR, we could go back to finding the tape line as we cross it and moving a set distance from there...
            case 3:
            case 2:
                distance = (RED ? 67 : 67/*TODO*/);
                break;// TODO RED + 8?
//            case 1 : distance = (RED ? 60  :60/*TODO*/); break;// TODO Need to think about this case carefully!
        }
        drive.newAccelerateInchesForward(2200, distance, RED ? 89 : 265.5, 5000); // TODO: It would be nice to combine this movement with the close to distance...it might just work...
        switch (path) {
            case 3:
            case 2:
                distance = (RED ? 13.5 : 13.5/*TODO*/);
                break; // TODO RED - 8?
//            case 1 : distance = (RED ? 10.5  :0/*TODO*/); break; // TODO Need to think about this case carefully!
        }
        drive.moveToDistanceFailover(distance, 1500, RED ? 89 : 271, true, 4000); //TODO: line up to wall before picking up skystone
        liftSystem.prepareToGrabNoWait(3500, Grabber.GrabberRotation.INSIDE);
        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_FIELD);
        //strafe a tad if we're doing path 2 to line up to the stone


        if (path == 2) {
            if (RED) {
                drive.moveInchesLeft(0.35, 7, 2300);
            } else {
                drive.moveInchesRight(0.35, 5.5, 2300);
            }
        }

        /////////////////////////////////////////////////////////
        // Grab the second stone

        if (useDistanceSensorsALot) {
            drive.newMoveToDistance(drive.frontLeftDistance, 5, 1500, 0, true, 4000);
            drive.newAccelerateInchesForward(2200, 7, 0, 5000);
            liftSystem.grabAndStowNoWait(4500);
            teamUtil.sleep(750);
            drive.newAccelerateInchesForward(-2200, (RED ? 5 : 5/*TODO*/), 0, 5000);

        } else {
            drive.newAccelerateInchesForward(2200, 11, 0, 5000);
            liftSystem.grabAndStowNoWait(4500);
            teamUtil.sleep(750);
            drive.newAccelerateInchesForward(-2200, (RED ? 7 : 9/*TODO*/), 0, 5000);
        }

        //strafe a tad to avoid collision with the wall when rotating towards building site
        if (path == 2) {
            if (RED) {
                drive.moveInchesRight(0.5, 4, 2000);
            } else {
                drive.moveInchesLeft(0.5, 4, 2000);
            }
        }
        drive.newRotateTo(RobotDrive.RobotRotation.TOWARDS_BUILDING);

        /////////////////////////////////////////////////////////
        // Deliver the second stone
        switch (path) { // TODO: OR, we could go back to finding the tape line as we cross it and moving a set distance from there...
            case 3:
                distance = (RED ? 66.5 : 66.5/*TODO*/); //TODO: OUT OF ORDER AUTO: 78.5 both sides
                break;
            case 2:
                distance = (RED ? 70.5 : 70.5/*TODO*/); //TODO: OUT OF ORDER AUTO: 82.5 both sides
                break;// TODO RED + 8?
            case 1:
                distance = (RED ? 60.5 : 60.5/*TODO*/);
                break;// TODO RED + 8?
        }
        drive.newAccelerateInchesForward(2200, distance, RED ? 268 : 88, 5000);

        liftSystem.lift.slightlyMoveLiftBaseUp(1, 2000);
        liftSystem.grabber.slightlyOpenGrabber();
        teamUtil.sleep(750);
        liftSystem.lift.moveLiftBaseDownNoWait(0.5, 3000);
        teamUtil.sleep(500);
        while (!drive.bottomColor.isOnTape()) {
            drive.driveBackward(0.75);
        }
        drive.stopMotors();
        justRanAuto = true;
        /////////////////////////////////////////////////////////
        // Park
    }


}


