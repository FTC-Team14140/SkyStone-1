package org.firstinspires.ftc.teamcode.TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="TestTubingIntake")
public class TestTubingIntake extends LinearOpMode {

    DcMotor intakeMotor;
    double INTAKE_POWER = -1.0;


    @Override
    public void runOpMode() throws InterruptedException {

        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        waitForStart();

        while(opModeIsActive() && !this.isStopRequested()){
            intakeMotor.setPower(INTAKE_POWER);
        }

    }
}