/* Copyright (c) 2025-2026 FRC 4639. */

package org.team4639.frc2026.subsystems.intake;

import org.littletonrobotics.junction.AutoLog;

public interface IntakeIO {
    @AutoLog
    public class IntakeIOInputs {
        public boolean isRunning;
    }

    public default void updateInputs(IntakeIOInputs inputs) {}

    public default void intakeOn() {}

    public default void intakeOff() {}
}
