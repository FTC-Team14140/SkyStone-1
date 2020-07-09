package org.firstinspires.ftc.teamcode.basicLibs;


import com.qualcomm.robotcore.hardware.Servo;

public class ServoControl {

    Servo servo;
    final double RANGE_OF_SERVO_IN_DEGREES = 180;

    double maxUnloadSpeedFor60deg; //0.16 sec to travel 60 degrees(taken off of servocity website spec styles)
    double maxDegreesPerSec; //60 deg/0.16 sec = 375 degrees per sec

    public ServoControl(Servo servo, double maxUnloadSpeedFor60deg) {
        this.servo = servo;
        this.maxUnloadSpeedFor60deg = maxUnloadSpeedFor60deg;
        maxDegreesPerSec = 60 / maxUnloadSpeedFor60deg;
    }

    public void runToPosition(double degreesPerSec, double targetPosition) {
        if (degreesPerSec > maxDegreesPerSec) {
            //log speed to high :C
            return;
        } else if(targetPosition > Servo.MAX_POSITION || targetPosition < Servo.MIN_POSITION){
            //log provided invalid position
            return;
        }
        double initialPosition = servo.getPosition(); //number from 0.0 to 1.0
        double distance = targetPosition - initialPosition;
        boolean positiveDirection = (distance > 0 ? true : false);
        double totalTime = Math.abs(((distance * RANGE_OF_SERVO_IN_DEGREES) / degreesPerSec) * 1000); //in milliseconds, hence the 1000
        double changeInPosition;
        long startTime = System.currentTimeMillis();
        long now = System.currentTimeMillis();

        while (now < startTime + totalTime) {
            changeInPosition = ((degreesPerSec * (now - startTime)/1000) / RANGE_OF_SERVO_IN_DEGREES);
            if(positiveDirection){
                servo.setPosition(initialPosition + changeInPosition);
            } else {
                servo.setPosition(initialPosition - changeInPosition);
            }
            now = System.currentTimeMillis();
        }

    }

    public void runToPositionNoWait(final double degreesPerSec, final double targetPosition) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                runToPosition(degreesPerSec, targetPosition);
            }
        });
        thread.start();
    }
}


