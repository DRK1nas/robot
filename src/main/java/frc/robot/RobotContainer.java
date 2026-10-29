// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;
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
 *   <li>Left stick Y = left side of drive, right stick Y = right side of drive
 *   <li>Right trigger (hold) = run shooter and kicker together
 *   <li>B button (hold) = reverse shooter and kicker to clear a jam
 * </ul>
 */
public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem m_drive = new DriveSubsystem();
  private final ShooterSubsystem m_shooter = new ShooterSubsystem();

  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
    // Tank drive is the default command. Xbox Y axes are negative when pushed forward.
    m_drive.setDefaultCommand(
        m_drive.tankDriveCommand(
            () -> -MathUtil.applyDeadband(m_driverController.getLeftY(), OperatorConstants.kDeadband),
            () ->
                -MathUtil.applyDeadband(m_driverController.getRightY(), OperatorConstants.kDeadband)));

    // Right trigger runs the shooter and the kicker together.
    m_driverController
        .rightTrigger(OperatorConstants.kTriggerThreshold)
        .whileTrue(m_shooter.shootCommand());

    // B button reverses everything to clear a jam.
    m_driverController.b().whileTrue(m_shooter.unjamCommand());
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return Autos.driveForward(m_drive);
  }
}
