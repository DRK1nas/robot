// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

import java.util.function.DoubleSupplier;

/** Tank drive: CAN 1,2 = right side, CAN 3,4 = left side. */
public class DriveSubsystem extends SubsystemBase {
  private final WPI_TalonSRX m_rightLeader = new WPI_TalonSRX(DriveConstants.kRightLeaderId);
  private final WPI_TalonSRX m_rightFollower = new WPI_TalonSRX(DriveConstants.kRightFollowerId);
  private final WPI_TalonSRX m_leftLeader = new WPI_TalonSRX(DriveConstants.kLeftLeaderId);
  private final WPI_TalonSRX m_leftFollower = new WPI_TalonSRX(DriveConstants.kLeftFollowerId);

  private final DifferentialDrive m_drive = new DifferentialDrive(m_leftLeader, m_rightLeader);

  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    m_rightLeader.configFactoryDefault();
    m_rightFollower.configFactoryDefault();
    m_leftLeader.configFactoryDefault();
    m_leftFollower.configFactoryDefault();

    m_rightFollower.follow(m_rightLeader);
    m_leftFollower.follow(m_leftLeader);

    m_rightLeader.setInverted(DriveConstants.kRightInverted);
    m_rightFollower.setInverted(DriveConstants.kRightInverted);
    m_leftLeader.setInverted(DriveConstants.kLeftInverted);
    m_leftFollower.setInverted(DriveConstants.kLeftInverted);

    m_rightLeader.setNeutralMode(NeutralMode.Brake);
    m_rightFollower.setNeutralMode(NeutralMode.Brake);
    m_leftLeader.setNeutralMode(NeutralMode.Brake);
    m_leftFollower.setNeutralMode(NeutralMode.Brake);

    m_drive.setMaxOutput(DriveConstants.kMaxOutput);
  }

  /** Tank drive command: left and right are -1..1 (forward positive). Runs until interrupted. */
  public Command tankDriveCommand(DoubleSupplier left, DoubleSupplier right) {
    return run(() -> m_drive.tankDrive(left.getAsDouble(), right.getAsDouble(), true))
        .finallyDo(interrupted -> m_drive.stopMotor());
  }

  public void stop() {
    m_drive.stopMotor();
  }
}
