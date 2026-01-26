/* Copyright (c) 2025-2026 FRC 4639. */

package org.team4639.frc2026.subsystems.vision;

import static org.team4639.frc2026.subsystems.vision.VisionConstants.*;

import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.team4639.frc2026.subsystems.vision.VisionIO.PoseObservation;

@AllArgsConstructor
public enum VisionFilters {
    AMBIGUITY(observation -> observation.tagCount() == 1 && observation.ambiguity() > maxAmbiguity),
    TAG_COUNT(observation -> observation.tagCount() == 0),
    MAX_Z_ERROR(observation -> Math.abs(observation.pose().getZ()) > maxZError),
    FIELD_BOUNDARIES(observation -> observation.pose().getX() < 0.0
            || observation.pose().getX() > aprilTagLayout.getFieldLength()
            || observation.pose().getY() < 0.0
            || observation.pose().getY() > aprilTagLayout.getFieldWidth());

    /**
     * Returns true if we want to reject the pose and false if we keep it
     */
    @Getter
    private final Predicate<PoseObservation> test;
}
