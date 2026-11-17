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

/** Upper kicker wheel: CAN 7. Separate from the kicker so each can run on its own. */
public class UpperKickerSubsystem extends SubsystemBase {
  private final WPI_TalonSRX m_upperKicker = new WPI_TalonSRX(ShooterConstants.kUpperKickerId);

  /** Creates a new UpperKickerSubsystem. */
  public UpperKickerSubsystem() {
    m_upperKicker.configFactoryDefault();
    m_upperKicker.setInverted(ShooterConstants.kUpperKickerInverted);
    m_upperKicker.setNeutralMode(NeutralMode.Coast);
  }

  /** Runs the upper kicker wheel. Stops it when the command ends. */
  public Command kickCommand() {
    return startEnd(
        () -> m_upperKicker.set(ControlMode.PercentOutput, ShooterConstants.kUpperKickerSpeed),
        this::stop);
  }

  /** Runs the upper kicker wheel backwards to clear a jam. Stops it when the command ends. */
  public Command unjamCommand() {
    return startEnd(
        () ->
            m_upperKicker.set(ControlMode.PercentOutput, ShooterConstants.kUnjamUpperKickerSpeed),
        this::stop);
  }

  public void stop() {
    m_upperKicker.set(ControlMode.PercentOutput, 0.0);
  }
}
