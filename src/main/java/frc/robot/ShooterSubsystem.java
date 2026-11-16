// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

/**
 * Shooter: CAN 6 = shooter wheel, CAN 5 = second wheel that adds power to the launch. Both wheels
 * run together.
 */
public class ShooterSubsystem extends SubsystemBase {
  private final WPI_TalonSRX m_shooter = new WPI_TalonSRX(ShooterConstants.kShooterId);
  private final WPI_TalonSRX m_kicker = new WPI_TalonSRX(ShooterConstants.kKickerId);

  /** Creates a new ShooterSubsystem. */
  public ShooterSubsystem() {
    m_shooter.configFactoryDefault();
    m_kicker.configFactoryDefault();

    m_shooter.setInverted(ShooterConstants.kShooterInverted);
    m_kicker.setInverted(ShooterConstants.kKickerInverted);

    m_shooter.setNeutralMode(NeutralMode.Coast);
    m_kicker.setNeutralMode(NeutralMode.Coast);
  }

  /** Runs the shooter and the kicker together. Stops both motors when the command ends. */
  public Command shootCommand() {
    return startEnd(
        () -> {
          m_shooter.set(ControlMode.PercentOutput, ShooterConstants.kShooterSpeed);
          m_kicker.set(ControlMode.PercentOutput, ShooterConstants.kKickerSpeed);
        },
        this::stop);
  }

  /** Runs shooter and kicker backwards to clear a jam. Stops when the command ends. */
  public Command unjamCommand() {
    return startEnd(
        () -> {
          m_shooter.set(ControlMode.PercentOutput, ShooterConstants.kUnjamShooterSpeed);
          m_kicker.set(ControlMode.PercentOutput, ShooterConstants.kUnjamKickerSpeed);
        },
        this::stop);
  }

  public void stop() {
    m_shooter.set(ControlMode.PercentOutput, 0.0);
    m_kicker.set(ControlMode.PercentOutput, 0.0);
  }
}
