// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final double kDeadband = 0.08;
    public static final double kTriggerThreshold = 0.2;

    // Scales stick input for driving (0..1). Lower = slower and easier to control.
    public static final double kDriveScale = 0.6;
  }

  public static class DriveConstants {
    // CAN IDs (Talon SRX)
    public static final int kRightLeaderId = 1;
    public static final int kRightFollowerId = 2;
    public static final int kLeftLeaderId = 3;
    public static final int kLeftFollowerId = 4;

    // Flip these if a side drives backwards
    public static final boolean kRightInverted = true;
    public static final boolean kLeftInverted = false;

    // Scale drive output (lower for testing)
    public static final double kMaxOutput = 1.0;

    // Max speed for each side, 0.0 to 1.0 (1.0 = full). Lower the side that is faster.
    // If the robot leans LEFT, lower kRightTrim (try 0.95). If it leans RIGHT, lower kLeftTrim.
    public static final double kLeftTrim = 1.0;
    public static final double kRightTrim = 1.0;
  }

  public static class AutoConstants {
    // Autonomous: shoot, then back up. Everything is timed (no encoders).
    public static final double kSpinupSeconds = 1.5; // shooter runs alone this long first
    public static final double kFeedSeconds = 2.0; // then kickers feed for this long

    // Negative = backward (same as pulling the sticks back). Flip the sign if the robot
    // drives toward the goal instead. The drive squares this value, so 0.6 is about 36% power.
    public static final double kBackUpSpeed = -0.6;
    public static final double kBackUpSeconds = 1.0;

    // "Forward and Back" auto: drive forward, then backward the same way.
    // The drive squares this value, so 0.6 is about 36% power.
    public static final double kForwardBackSpeed = 0.6;
    public static final double kForwardSeconds = 1.0;
    public static final double kBackwardSeconds = 1.0;
  }

  public static class ShooterConstants {
    // CAN IDs (Talon SRX)
    public static final int kKickerId = 5;
    public static final int kShooterId = 6;
    public static final int kUpperKickerId = 7;

    // Percent output, 0..1
    public static final double kShooterSpeed = 0.65;
    public static final double kKickerSpeed = 0.60;
    public static final double kUnjamShooterSpeed = -0.40;
    public static final double kUnjamKickerSpeed = -0.60;

    // Upper kicker (CAN 7) has its own speeds so you can tune it separately
    public static final double kUpperKickerSpeed = 0.80;
    public static final double kUnjamUpperKickerSpeed = -0.60;

    // Flip these if a motor spins the wrong way
    public static final boolean kShooterInverted = false;
    public static final boolean kKickerInverted = true;
    public static final boolean kUpperKickerInverted = false;
  }
}
