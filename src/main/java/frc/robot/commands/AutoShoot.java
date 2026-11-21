// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.KickerSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.UpperKickerSubsystem;

/** Automatic shooting for teleop. Bind with whileTrue: it shoots until the button is released. */
public final class AutoShoot {
  /**
   * Spins up the shooter (teleop speed), then repeats: feed one ball, briefly reverse the kickers
   * to settle the next ball, pause so the wheel recovers. Runs until interrupted (button
   * released), then stops all three motors. Uses the same feed/reverse/pause times as autonomous.
   */
  public static Command shootWhileHeld(
      ShooterSubsystem shooter, KickerSubsystem kicker, UpperKickerSubsystem upperKicker) {
    Command feedLoop =
        Commands.repeatingSequence(
            // Feed one ball.
            Commands.parallel(kicker.kickCommand(), upperKicker.kickCommand())
                .withTimeout(AutoConstants.kFeedPulseSeconds),
            // Back the next ball out from under the lower kicker.
            Commands.parallel(kicker.unjamCommand(), upperKicker.unjamCommand())
                .withTimeout(AutoConstants.kReverseSeconds),
            // Let the shooter wheel get back up to speed.
            Commands.waitSeconds(AutoConstants.kBallPauseSeconds));

    return Commands.parallel(
            shooter.shootCommand(ShooterConstants.kShooterSpeed),
            Commands.sequence(
                Commands.waitSeconds(ShooterConstants.kTeleopSpinupSeconds), feedLoop))
        .withName("Auto Shoot");
  }

  private AutoShoot() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}
