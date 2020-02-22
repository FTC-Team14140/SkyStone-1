package org.firstinspires.ftc.teamcode.CompetitionCode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Assemblies.Grabber;
import org.firstinspires.ftc.teamcode.basicLibs.TeamGamepad;
import org.firstinspires.ftc.teamcode.basicLibs.teamUtil;

@TeleOp(name = "MoveLiftBase", group="a")
public class MoveLiftBase extends OpMode {

    private DcMotorEx liftBase;
    double maxVelocity = 0.0;

    public void init() {
        liftBase = hardwareMap.get(DcMotorEx.class, "liftBase");
        liftBase.setDirection(DcMotorSimple.Direction.REVERSE);
        liftBase.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void loop() {
        double currentVelocity;

        currentVelocity = liftBase.getVelocity();



        if (gamepad1.y) {
            liftBase.setPower(0.5);
        } else if (gamepad1.a) {
            liftBase.setPower(-0.5);
        } else {
            liftBase.setPower(0);
        }

        if (Math.abs(currentVelocity) > maxVelocity) {
            maxVelocity = Math.abs(currentVelocity);
        }

        teamUtil.log("MaxVelocity: " + maxVelocity);
        telemetry.addData("MaxVelocity: ", maxVelocity);


        telemetry.addLine("encoder:" + liftBase.getCurrentPosition());
        telemetry.update();
    }
}
