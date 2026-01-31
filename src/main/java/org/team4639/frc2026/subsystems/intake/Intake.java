/* Copyright (c) 2025-2026 FRC 4639. */

package org.team4639.frc2026.subsystems.intake;

import edu.wpi.first.wpilibj2.command.Command;
import lombok.RequiredArgsConstructor;
import org.littletonrobotics.junction.Logger;
import org.team4639.frc2026.subsystems.intake.IntakeIO.IntakeIOInputs;
import org.team4639.lib.util.FullSubsystem;

@RequiredArgsConstructor
public class Intake extends FullSubsystem {
    private final IntakeIO io;
    private final IntakeIOInputs inputs = new IntakeIOInputs();

    public Command runIntake() {
        return this.run(() -> io.intakeOn()).finallyDo(() -> io.intakeOff());
    }

    @Override
    public void periodic() {
        io.updateInputs(inputs);
    }

    @Override
    public void periodicAfterScheduler() {
        Logger.recordOutput("/Internal/IntakeRunning", inputs.isRunning);
    }
}
