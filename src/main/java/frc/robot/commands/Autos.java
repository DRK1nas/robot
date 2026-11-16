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
   * Spins up the shooter, feeds with both kickers while the shooter keeps running, then backs up.
   */
  public static Command shootThenBackUp(
      DriveSubsystem drive,
      ShooterSubsystem shooter,
      KickerSubsystem kicker,
      UpperKickerSubsystem upperKicker) {
    Command shoot =
        Commands.deadline(
            Commands.waitSeconds(AutoConstants.kSpinupSeconds)
                .andThen(
                    Commands.parallel(kicker.kickCommand(), upperKicker.kickCommand())
                        .withTimeout(AutoConstants.kFeedSeconds)),
            shooter.shootCommand());

    Command backUp =
        drive
            .arcadeDriveCommand(() -> AutoConstants.kBackUpSpeed, () -> 0.0)
            .withTimeout(AutoConstants.kBackUpSeconds);

    return shoot.andThen(backUp);
  }

  private Autos() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}
