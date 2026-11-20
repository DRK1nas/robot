// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Hand-counted score for OCCRA Over-the-Top (2026 manual, section 2.4). Press a button when
 * something scores; the points are added for you. Shown on the dashboard (Elastic) under "Score/".
 * Not saved after a robot restart.
 */
public class ScoreTracker {
  /** Every way to score, with its point value. */
  public enum Action {
    MOBILITY("Mobility", 1),
    AUTO_LOW("Auto Ball Low", 2),
    AUTO_HIGH("Auto Ball High", 3),
    YELLOW_LOW("Yellow Ball Low", 1),
    YELLOW_HIGH("Yellow Ball High", 2),
    BONUS_LOW("Bonus Ball Low", 2),
    BONUS_HIGH("Bonus Ball High", 3),
    OPPONENT_FOUL("Opponent Foul", 3);

    final String label;
    final int points;
    int count = 0;

    Action(String label, int points) {
      this.label = label;
      this.points = points;
    }
  }

  private int m_total = 0;
  private final Deque<Action> m_history = new ArrayDeque<>();

  public ScoreTracker() {
    publish("(none)");
    SmartDashboard.putData("Score/Undo", undoCommand());
    SmartDashboard.putData(
        "Score/Reset", Commands.runOnce(this::reset).ignoringDisable(true).withName("Reset"));
  }

  /** Adds one of the given action. */
  public Command scoreCommand(Action action) {
    return Commands.runOnce(() -> add(action)).ignoringDisable(true).withName(action.label);
  }

  /**
   * A ball in the Low goal: counts as an auto ball (2) while the Driver Station is in Autonomous,
   * otherwise a yellow ball (1).
   */
  public Command lowGoalCommand() {
    return Commands.runOnce(
            () -> add(DriverStation.isAutonomous() ? Action.AUTO_LOW : Action.YELLOW_LOW))
        .ignoringDisable(true)
        .withName("Low Goal");
  }

  /**
   * A ball in the High goal: counts as an auto ball (3) while the Driver Station is in Autonomous,
   * otherwise a yellow ball (2).
   */
  public Command highGoalCommand() {
    return Commands.runOnce(
            () -> add(DriverStation.isAutonomous() ? Action.AUTO_HIGH : Action.YELLOW_HIGH))
        .ignoringDisable(true)
        .withName("High Goal");
  }

  /** Takes back the last thing that was added. */
  public Command undoCommand() {
    return Commands.runOnce(this::undo).ignoringDisable(true).withName("Undo");
  }

  private void add(Action action) {
    action.count++;
    m_total += action.points;
    m_history.push(action);
    publish("+" + action.points + " " + action.label);
  }

  private void undo() {
    Action action = m_history.poll();
    if (action == null) {
      return;
    }
    action.count--;
    m_total -= action.points;
    publish("Undo " + action.label);
  }

  private void reset() {
    for (Action action : Action.values()) {
      action.count = 0;
    }
    m_total = 0;
    m_history.clear();
    publish("Reset");
  }

  private void publish(String last) {
    SmartDashboard.putNumber("Score/Points", m_total);
    SmartDashboard.putString("Score/Last", last);
    for (Action action : Action.values()) {
      SmartDashboard.putNumber("Score/Count/" + action.label, action.count);
    }
  }
}
