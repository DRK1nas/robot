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

/** Kicker wheel: CAN 5. Separate from the shooter and upper kicker so each can run on its own. */
public class KickerSubsystem extends SubsystemBase {
  private final WPI_TalonSRX m_kicker = new WPI_TalonSRX(ShooterConstants.kKickerId);

  /** Creates a new KickerSubsystem. */
  public KickerSubsystem() {
    m_kicker.configFactoryDefault();
    m_kicker.setInverted(ShooterConstants.kKickerInverted);
    m_kicker.setNeutralMode(NeutralMode.Coast);
  }

  /** Runs the kicker wheel. Stops it when the command ends. */
  public Command kickCommand() {
    return startEnd(
        () -> m_kicker.set(ControlMode.PercentOutput, ShooterConstants.kKickerSpeed), this::stop);
  }

  /** Runs the kicker wheel backwards to clear a jam. Stops it when the command ends. */
  public Command unjamCommand() {
    return startEnd(
        () -> m_kicker.set(ControlMode.PercentOutput, ShooterConstants.kUnjamKickerSpeed),
        this::stop);
  }

  public void stop() {
    m_kicker.set(ControlMode.PercentOutput, 0.0);
  }
}
