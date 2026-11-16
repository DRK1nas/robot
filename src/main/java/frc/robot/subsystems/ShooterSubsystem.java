// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * A single Talon SRX running at a fixed percent output while a button is held. Used for the shooter
 * wheel and for each kicker, so each motor can be run on its own button at the same time as the
 * others.
 */
public class ShooterSubsystem extends SubsystemBase {
  private final WPI_TalonSRX m_motor;
  private final double m_runSpeed;
  private final double m_unjamSpeed;

  /**
   * @param canId CAN ID of the Talon SRX
   * @param inverted flip if the motor spins the wrong way
   * @param runSpeed percent output when running, 0..1
   * @param unjamSpeed percent output when clearing a jam (normally negative)
   */
  public ShooterSubsystem(int canId, boolean inverted, double runSpeed, double unjamSpeed) {
    m_motor = new WPI_TalonSRX(canId);
    m_runSpeed = runSpeed;
    m_unjamSpeed = unjamSpeed;

    m_motor.configFactoryDefault();
    m_motor.setInverted(inverted);
    m_motor.setNeutralMode(NeutralMode.Coast);
  }

  /** Runs the motor forward while scheduled, then stops it. */
  public Command runCommand() {
    return startEnd(() -> m_motor.set(ControlMode.PercentOutput, m_runSpeed), this::stop);
  }

  /** Runs the motor backwards to clear a jam while scheduled, then stops it. */
  public Command unjamCommand() {
    return startEnd(() -> m_motor.set(ControlMode.PercentOutput, m_unjamSpeed), this::stop);
  }

  public void stop() {
    m_motor.set(ControlMode.PercentOutput, 0.0);
  }
}
