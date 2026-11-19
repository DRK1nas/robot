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

    // Straight-driving correction. Adds a little turn whenever the robot drives, in proportion
    // to how fast it drives. Positive = turn RIGHT (fixes a robot that drifts LEFT).
    // Negative = turn LEFT (fixes drifting RIGHT). 0 = off. Change in small steps (0.02).
    public static final double kDriftCorrection = 0.05;
  }

  public static class AutoConstants {
    // Autonomous: shoot, then drive. Everything is timed (no encoders).
    // Shooter speed in auto (teleop uses ShooterConstants.kShooterSpeed). Raise if shots are low.
    public static final double kAutoShooterSpeed = 0.65;
    public static final double kSpinupSeconds = 2.5; // shooter runs alone this long first

    // Then feed one ball at a time: kickers run for kFeedPulseSeconds, then stop for
    // kBallPauseSeconds so the shooter wheel gets back up to speed before the next ball.
    public static final int kBallCount = 3;
    public static final double kFeedPulseSeconds = 0.4; // longer if a ball doesn't make it out
    public static final double kBallPauseSeconds = 1.0; // longer if later balls still go low

    // Between shots, run both kickers backward this long (at their unjam speeds) to pull the
    // next ball back out from under the lower kicker so it sits steady. 0 = off.
    // This happens at the start of the pause above, and the shooter keeps spinning forward.
    public static final double kReverseSeconds = 0.2;

    // Negative = backward (same as pulling the sticks back). Flip the sign if the robot
    // drives toward the goal instead. The drive squares this value, so 0.6 is about 36% power.
    public static final double kBackUpSpeed = -0.6;
    public static final double kBackUpSeconds = 1.0;

    // "Shoot and Go Forward" auto: after shooting, drive forward.
    // The drive squares this value, so 0.6 is about 36% power.
    public static final double kForwardSpeed = 0.6;
    public static final double kForwardSeconds = 1.0;
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
