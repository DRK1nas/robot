// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants.AutoConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.KickerSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.UpperKickerSubsystem;

public final class Autos {
  /**
   * Shoots, then backs up a little.
   *
   * <p>1. Shooter spins up alone. 2. Kickers feed one ball at a time with a pause between balls.
   * 3. Everything stops. 4. Robot drives backward for a fixed time.
   */
  public static Command shootAndBackUp(
      DriveSubsystem drive,
      ShooterSubsystem shooter,
      KickerSubsystem kicker,
      UpperKickerSubsystem upperKicker) {
    Command backUp =
        drive
            .arcadeDriveCommand(() -> AutoConstants.kBackUpSpeed, () -> 0.0)
            .withTimeout(AutoConstants.kBackUpSeconds);

    return Commands.sequence(shoot(shooter, kicker, upperKicker), backUp);
  }

  /**
   * Shoots, then drives forward.
   *
   * <p>1. Shooter spins up alone. 2. Kickers feed one ball at a time with a pause between balls.
   * 3. Everything stops. 4. Robot drives forward for a fixed time.
   */
  public static Command shootAndGoForward(
      DriveSubsystem drive,
      ShooterSubsystem shooter,
      KickerSubsystem kicker,
      UpperKickerSubsystem upperKicker) {
    Command goForward =
        drive
            .arcadeDriveCommand(() -> AutoConstants.kForwardSpeed, () -> 0.0)
            .withTimeout(AutoConstants.kForwardSeconds);

    return Commands.sequence(shoot(shooter, kicker, upperKicker), goForward);
  }

  /**
   * Spins up the shooter, then feeds kBallCount balls one at a time. Between balls the kickers
   * briefly run backward (kReverseSeconds) to settle the next ball, then wait so the shooter wheel
   * recovers its speed. The shooter runs the whole time and stops when the last pause ends.
   */
  private static Command shoot(
      ShooterSubsystem shooter, KickerSubsystem kicker, UpperKickerSubsystem upperKicker) {
    List<Command> steps = new ArrayList<>();
    steps.add(Commands.waitSeconds(AutoConstants.kSpinupSeconds));

    for (int ball = 1; ball <= AutoConstants.kBallCount; ball++) {
      // Feed one ball.
      steps.add(
          Commands.parallel(kicker.kickCommand(), upperKicker.kickCommand())
              .withTimeout(AutoConstants.kFeedPulseSeconds));

      // Before the next ball: back it out from under the lower kicker.
      boolean moreBalls = ball < AutoConstants.kBallCount;
      if (moreBalls && AutoConstants.kReverseSeconds > 0) {
        steps.add(
            Commands.parallel(kicker.unjamCommand(), upperKicker.unjamCommand())
                .withTimeout(AutoConstants.kReverseSeconds));
      }

      // Let the shooter wheel get back up to speed (and the last ball leave).
      steps.add(Commands.waitSeconds(AutoConstants.kBallPauseSeconds));
    }

    // The feeding sequence decides when shooting is over; the shooter stops with it.
    return Commands.deadline(
        Commands.sequence(steps.toArray(new Command[0])),
        shooter.shootCommand(AutoConstants.kAutoShooterSpeed));
  }

  private Autos() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}
