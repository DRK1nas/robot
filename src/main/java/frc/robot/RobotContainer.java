// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.Autos;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 *
 * <p>Controls (Xbox, port 0):
 *
 * <ul>
 *   <li>Left stick Y = drive forward/back, right stick X = turn
 *   <li>Right trigger (hold) = shooter wheel
 *   <li>Right bumper (hold) = lower kicker
 *   <li>Left bumper (hold) = upper kicker
 *   <li>B button (hold) = run shooter and both kickers backwards to clear a jam
 * </ul>
 */
public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem m_drive = new DriveSubsystem();
  private final ShooterSubsystem m_shooter =
      new ShooterSubsystem(
          ShooterConstants.kShooterId,
          ShooterConstants.kShooterInverted,
          ShooterConstants.kShooterSpeed,
          ShooterConstants.kUnjamShooterSpeed);
  private final ShooterSubsystem m_lowerKicker =
      new ShooterSubsystem(
          ShooterConstants.kLowerKickerId,
          ShooterConstants.kLowerKickerInverted,
          ShooterConstants.kLowerKickerSpeed,
          ShooterConstants.kUnjamLowerKickerSpeed);
  private final ShooterSubsystem m_upperKicker =
      new ShooterSubsystem(
          ShooterConstants.kUpperKickerId,
          ShooterConstants.kUpperKickerInverted,
          ShooterConstants.kUpperKickerSpeed,
          ShooterConstants.kUnjamUpperKickerSpeed);

  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
    // Arcade drive is the default command. Xbox Y axes are negative when pushed forward;
    // X axes are positive when pushed right, but arcadeDrive treats positive rotation as
    // counterclockwise, so negate it so pushing right turns right.
    m_drive.setDefaultCommand(
        m_drive.arcadeDriveCommand(
            () ->
                -MathUtil.applyDeadband(m_driverController.getLeftY(), OperatorConstants.kDeadband)
                    * OperatorConstants.kDriveScale,
            () ->
                -MathUtil.applyDeadband(m_driverController.getRightX(), OperatorConstants.kDeadband)
                    * OperatorConstants.kDriveScale));

    // Each shooter motor has its own button, so they can be held together.
    m_driverController
        .rightTrigger(OperatorConstants.kTriggerThreshold)
        .whileTrue(m_shooter.runCommand());
    m_driverController.rightBumper().whileTrue(m_lowerKicker.runCommand());
    m_driverController.leftBumper().whileTrue(m_upperKicker.runCommand());

    // B button reverses everything to clear a jam.
    m_driverController
        .b()
        .whileTrue(
            Commands.parallel(
                m_shooter.unjamCommand(),
                m_lowerKicker.unjamCommand(),
                m_upperKicker.unjamCommand()));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return Autos.shootThenBackUp(m_drive, m_shooter, m_lowerKicker, m_upperKicker);
  }
}
