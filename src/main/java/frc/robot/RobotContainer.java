// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.KickerSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.UpperKickerSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 *
 * <p>Controls (Xbox, port 0):
 *
 * <ul>
 *   <li>Left stick Y = drive forward/back, right stick X = turn (arcade drive)
 *   <li>Right trigger (press to toggle on/off) = shooter wheel (CAN 6)
 *   <li>Right bumper (hold) = kicker wheel (CAN 5)
 *   <li>Left bumper (hold) = upper kicker wheel (CAN 7)
 *   <li>A button (hold) = shooter, kicker and upper kicker together
 *   <li>B button (hold) = reverse shooter, kicker and upper kicker to clear a jam
 * </ul>
 */
public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem m_drive = new DriveSubsystem();
  private final ShooterSubsystem m_shooter = new ShooterSubsystem();
  private final KickerSubsystem m_kicker = new KickerSubsystem();
  private final UpperKickerSubsystem m_upperKicker = new UpperKickerSubsystem();

  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  // Pick the autonomous routine from the dashboard (Elastic: "Auto Chooser").
  private final SendableChooser<Command> m_autoChooser = new SendableChooser<>();

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    configureBindings();

    m_autoChooser.setDefaultOption(
        "Shoot and Back Up", Autos.shootAndBackUp(m_drive, m_shooter, m_kicker, m_upperKicker));
    m_autoChooser.addOption(
        "Shoot and Go Forward",
        Autos.shootAndGoForward(m_drive, m_shooter, m_kicker, m_upperKicker));
    m_autoChooser.addOption("Do Nothing", Commands.none());
    SmartDashboard.putData("Auto Chooser", m_autoChooser);
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

    // Right trigger toggles the shooter wheel on and off.
    m_driverController
        .rightTrigger(OperatorConstants.kTriggerThreshold)
        .toggleOnTrue(m_shooter.shootCommand());

    // Right bumper runs the kicker wheel (CAN 5) on its own.
    m_driverController.rightBumper().whileTrue(m_kicker.kickCommand());

    // Left bumper runs the upper kicker wheel (CAN 7) on its own.
    m_driverController.leftBumper().whileTrue(m_upperKicker.kickCommand());

    // A button runs the shooter, kicker and upper kicker together.
    m_driverController
        .a()
        .whileTrue(
            Commands.parallel(
                m_shooter.shootCommand(), m_kicker.kickCommand(), m_upperKicker.kickCommand()));

    // B button reverses everything to clear a jam.
    m_driverController
        .b()
        .whileTrue(
            Commands.parallel(
                m_shooter.unjamCommand(),
                m_kicker.unjamCommand(),
                m_upperKicker.unjamCommand()));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return m_autoChooser.getSelected();
  }
}
