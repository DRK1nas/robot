// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

/** Shooter wheel: CAN 6. */
public class ShooterSubsystem extends SubsystemBase {
  private final WPI_TalonSRX m_shooter = new WPI_TalonSRX(ShooterConstants.kShooterId);

  /** Creates a new ShooterSubsystem. */
  public ShooterSubsystem() {
    m_shooter.configFactoryDefault();
    m_shooter.setInverted(ShooterConstants.kShooterInverted);
    m_shooter.setNeutralMode(NeutralMode.Coast);
  }

  /** Runs the shooter wheel. Stops it when the command ends. */
  public Command shootCommand() {
    return startEnd(
        () -> m_shooter.set(ControlMode.PercentOutput, ShooterConstants.kShooterSpeed), this::stop);
  }

  /** Runs the shooter wheel backwards to clear a jam. Stops it when the command ends. */
  public Command unjamCommand() {
    return startEnd(
        () -> m_shooter.set(ControlMode.PercentOutput, ShooterConstants.kUnjamShooterSpeed),
        this::stop);
  }

  /** Shows "Shooter Spinning" on the dashboard (Elastic): true while the shooter motor runs. */
  @Override
  public void periodic() {
    SmartDashboard.putBoolean("Shooter Spinning", Math.abs(m_shooter.getMotorOutputPercent()) > 0.05);
  }

  public void stop() {
    m_shooter.set(ControlMode.PercentOutput, 0.0);
  }
}
