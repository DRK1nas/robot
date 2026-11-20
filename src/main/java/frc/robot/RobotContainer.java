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
 *
 * <p>Score controller (Xbox, port 1), press once per score. Left side = US, right side = THEM:
 *
 * <ul>
 *   <li>Low goal ball (2 in auto, 1 in teleop): US D-pad down, THEM A
 *   <li>High goal ball (3 in auto, 2 in teleop): US D-pad up, THEM Y
 *   <li>Bonus ball Low (2): US D-pad left, THEM X
 *   <li>Bonus ball High (3): US D-pad right, THEM B
 *   <li>Mobility (1): US left bumper, THEM right bumper
 *   <li>Foul (3 to the other team): THEY fouled = left trigger, WE fouled = right trigger
 *   <li>Undo last score: US Back, THEM Start
 * </ul>
 */
public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem m_drive = new DriveSubsystem();
  private final ShooterSubsystem m_shooter = new ShooterSubsystem();
  private final KickerSubsystem m_kicker = new KickerSubsystem();
  private final UpperKickerSubsystem m_upperKicker = new UpperKickerSubsystem();

  // Hand-counted score for both alliances, shown on the dashboard under "Score/".
  private final ScoreBoard m_score = new ScoreBoard();

  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  // Second controller: only counts the score.
  private final CommandXboxController m_scoreController =
      new CommandXboxController(OperatorConstants.kScoreControllerPort);

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

    configureScoreController();

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
   * Second controller (port 1) counts the score. Press once per score. Left side = US, right side
   * = THEM. Low/High count as auto balls while the Driver Station is in Autonomous, and as yellow
   * balls otherwise.
   */
  private void configureScoreController() {
    // US: D-pad, left bumper, left trigger, Back.
    m_scoreController.povDown().onTrue(m_score.us.lowGoalCommand());
    m_scoreController.povUp().onTrue(m_score.us.highGoalCommand());
    m_scoreController.povLeft().onTrue(m_score.us.scoreCommand(ScoreBoard.Action.BONUS_LOW));
    m_scoreController.povRight().onTrue(m_score.us.scoreCommand(ScoreBoard.Action.BONUS_HIGH));
    m_scoreController.leftBumper().onTrue(m_score.us.scoreCommand(ScoreBoard.Action.MOBILITY));
    m_scoreController
        .leftTrigger(OperatorConstants.kTriggerThreshold)
        .onTrue(m_score.us.scoreCommand(ScoreBoard.Action.FOUL)); // THEY fouled: +3 for us
    m_scoreController.back().onTrue(m_score.us.undoCommand());

    // THEM: face buttons, right bumper, right trigger, Start.
    m_scoreController.a().onTrue(m_score.them.lowGoalCommand());
    m_scoreController.y().onTrue(m_score.them.highGoalCommand());
    m_scoreController.x().onTrue(m_score.them.scoreCommand(ScoreBoard.Action.BONUS_LOW));
    m_scoreController.b().onTrue(m_score.them.scoreCommand(ScoreBoard.Action.BONUS_HIGH));
    m_scoreController.rightBumper().onTrue(m_score.them.scoreCommand(ScoreBoard.Action.MOBILITY));
    m_scoreController
        .rightTrigger(OperatorConstants.kTriggerThreshold)
        .onTrue(m_score.them.scoreCommand(ScoreBoard.Action.FOUL)); // WE fouled: +3 for them
    m_scoreController.start().onTrue(m_score.them.undoCommand());
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
