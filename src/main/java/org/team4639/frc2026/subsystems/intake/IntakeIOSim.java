/* Copyright (c) 2025-2026 FRC 4639. */

package org.team4639.frc2026.subsystems.intake;

import static edu.wpi.first.units.Units.Inches;

import org.ironmaple.simulation.IntakeSimulation;
import org.ironmaple.simulation.IntakeSimulation.IntakeSide;
import org.team4639.frc2026.SimRobot;

public class IntakeIOSim implements IntakeIO {
    private IntakeSimulation sim;

    public IntakeIOSim() {
        this.sim = IntakeSimulation.OverTheBumperIntake(
                "Fuel",
                SimRobot.getInstance().getSwerveDriveSimulation(),
                Inches.of(27),
                Inches.of(12),
                IntakeSide.BACK,
                48);
        SimRobot.getInstance().setIntakeSim(sim);
    }

    @Override
    public void updateInputs(IntakeIOInputs inputs) {
        inputs.isRunning = sim.isRunning();
    }

    @Override
    public void intakeOn() {
        if (!this.sim.isRunning()) {
            this.sim.startIntake();
        }
    }

    @Override
    public void intakeOff() {
        if (this.sim.isRunning()) {
            this.sim.stopIntake();
        }
    }
}
