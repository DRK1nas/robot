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
import java.util.EnumMap;
import java.util.Map;

/**
 * Hand-counted score for both alliances, OCCRA Over-the-Top (2026 manual, section 2.4). Press a
 * button when something scores; the points are added for you. Shown on the dashboard (Elastic)
 * under "Score/". Not saved after a robot restart.
 */
public class ScoreBoard {
  /** Every way to score, with its point value. */
  public enum Action {
    MOBILITY("Mobility", 1),
    AUTO_LOW("Auto Ball Low", 2),
    AUTO_HIGH("Auto Ball High", 3),
    YELLOW_LOW("Yellow Ball Low", 1),
    YELLOW_HIGH("Yellow Ball High", 2),
    BONUS_LOW("Bonus Ball Low", 2),
    BONUS_HIGH("Bonus Ball High", 3),
    FOUL("Foul by Other Team", 3); // a foul gives 3 points to the team that did NOT commit it

    final String label;
    final int points;

    Action(String label, int points) {
      this.label = label;
      this.points = points;
    }
  }

  /** One alliance's score. */
  public class Team {
    private final String m_name;
    private final Map<Action, Integer> m_counts = new EnumMap<>(Action.class);
    private final Deque<Action> m_history = new ArrayDeque<>();
    private int m_total = 0;

    private Team(String name) {
      m_name = name;
      for (Action action : Action.values()) {
        m_counts.put(action, 0);
      }
      SmartDashboard.putData("Score/" + m_name + "/Undo", undoCommand());
    }

    /** Adds one of the given action. */
    public Command scoreCommand(Action action) {
      return Commands.runOnce(() -> add(action))
          .ignoringDisable(true)
          .withName(m_name + " " + action.label);
    }

    /** Ball in the Low goal: auto ball (2) while in Autonomous, otherwise yellow ball (1). */
    public Command lowGoalCommand() {
      return Commands.runOnce(
              () -> add(DriverStation.isAutonomous() ? Action.AUTO_LOW : Action.YELLOW_LOW))
          .ignoringDisable(true)
          .withName(m_name + " Low Goal");
    }

    /** Ball in the High goal: auto ball (3) while in Autonomous, otherwise yellow ball (2). */
    public Command highGoalCommand() {
      return Commands.runOnce(
              () -> add(DriverStation.isAutonomous() ? Action.AUTO_HIGH : Action.YELLOW_HIGH))
          .ignoringDisable(true)
          .withName(m_name + " High Goal");
    }

    /** Takes back this team's last score. */
    public Command undoCommand() {
      return Commands.runOnce(this::undo).ignoringDisable(true).withName(m_name + " Undo");
    }

    private void add(Action action) {
      m_counts.merge(action, 1, Integer::sum);
      m_total += action.points;
      m_history.push(action);
      publish("+" + action.points + " " + action.label);
    }

    private void undo() {
      Action action = m_history.poll();
      if (action == null) {
        return;
      }
      m_counts.merge(action, -1, Integer::sum);
      m_total -= action.points;
      publish("Undo " + action.label);
    }

    private void reset() {
      m_counts.replaceAll((action, count) -> 0);
      m_history.clear();
      m_total = 0;
      publish("Reset");
    }

    private void publish(String last) {
      SmartDashboard.putNumber("Score/" + m_name + "/Points", m_total);
      SmartDashboard.putString("Score/" + m_name + "/Last", last);
      for (Action action : Action.values()) {
        SmartDashboard.putNumber("Score/" + m_name + "/Count/" + action.label, m_counts.get(action));
      }
      SmartDashboard.putNumber("Score/Lead", us.m_total - them.m_total);
    }
  }

  /** Your alliance. */
  public final Team us;

  /** The other alliance. */
  public final Team them;

  public ScoreBoard() {
    us = new Team("Us");
    them = new Team("Them");
    us.publish("(none)");
    them.publish("(none)");
    SmartDashboard.putData("Score/Reset", resetCommand());
  }

  /** Sets both teams back to 0. */
  public Command resetCommand() {
    return Commands.runOnce(
            () -> {
              us.reset();
              them.reset();
            })
        .ignoringDisable(true)
        .withName("Reset");
  }
}
