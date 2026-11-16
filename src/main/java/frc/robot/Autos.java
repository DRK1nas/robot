// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;

public final class Autos {
  /** Drives straight forward for a fixed time, then stops. */
  public static Command driveForward(DriveSubsystem drive) {
    return drive
        .tankDriveCommand(() -> DriveConstants.kAutoDriveSpeed, () -> DriveConstants.kAutoDriveSpeed)
        .withTimeout(DriveConstants.kAutoDriveSeconds);
  }

  private Autos() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}
