// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

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
   * <p>1. Shooter spins up alone. 2. Kickers join in and feed. 3. Everything stops. 4. Robot
   * drives backward for a fixed time.
   */
  public static Command shootAndBackUp(
      DriveSubsystem drive,
      ShooterSubsystem shooter,
      KickerSubsystem kicker,
      UpperKickerSubsystem upperKicker) {
    // The shooter runs the whole time; the kickers start after the spin-up delay.
    // The timeout ends the group, which stops all three motors.
    Command shoot =
        Commands.parallel(
                shooter.shootCommand(),
                Commands.sequence(
                    Commands.waitSeconds(AutoConstants.kSpinupSeconds),
                    Commands.parallel(kicker.kickCommand(), upperKicker.kickCommand())))
            .withTimeout(AutoConstants.kSpinupSeconds + AutoConstants.kFeedSeconds);

    Command backUp =
        drive
            .tankDriveCommand(() -> AutoConstants.kBackUpSpeed, () -> AutoConstants.kBackUpSpeed)
            .withTimeout(AutoConstants.kBackUpSeconds);

    return Commands.sequence(shoot, backUp);
  }

  /** Drives forward for a fixed time, then backward for a fixed time. No shooting. */
  public static Command forwardAndBack(DriveSubsystem drive) {
    Command forward =
        drive
            .arcadeDriveCommand(() -> AutoConstants.kForwardBackSpeed, () -> 0.0)
            .withTimeout(AutoConstants.kForwardSeconds);

    Command backward =
        drive
            .arcadeDriveCommand(() -> -AutoConstants.kForwardBackSpeed, () -> 0.0)
            .withTimeout(AutoConstants.kBackwardSeconds);

    return Commands.sequence(forward, backward);
  }

  private Autos() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}
