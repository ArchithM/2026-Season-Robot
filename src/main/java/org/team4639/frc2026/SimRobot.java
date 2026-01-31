/* Copyright (c) 2025-2026 FRC 4639. */

package org.team4639.frc2026;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Kilograms;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;
import org.ironmaple.simulation.IntakeSimulation;
import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.simulation.drivesims.COTS;
import org.ironmaple.simulation.drivesims.SwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;
import org.ironmaple.simulation.seasonspecific.rebuilt2026.Arena2026Rebuilt;
import org.littletonrobotics.junction.Logger;
import org.team4639.frc2026.Constants.Mode;
import org.team4639.frc2026.subsystems.drive.Drive;
import org.team4639.lib.util.VirtualSubsystem;

public class SimRobot extends VirtualSubsystem {
    @Getter
    private static volatile SimRobot instance = new SimRobot();

    @Setter
    @Getter
    private SwerveDriveSimulation swerveDriveSimulation = null;

    @Getter
    @Setter
    private IntakeSimulation intakeSim = null;

    private Pose3d[] ballsHeldByRobot = new Pose3d[] {};
    private Translation3d startingPoseRobotRelative =
            new Translation3d(Units.inchesToMeters(-20), Units.inchesToMeters(-9), Units.inchesToMeters(7));

    public static final DriveTrainSimulationConfig mapleSimConfig = DriveTrainSimulationConfig.Default()
            .withRobotMass(Kilograms.of(Constants.RobotConstants.ROBOT_MASS_KG))
            .withCustomModuleTranslations(Drive.getModuleTranslations())
            .withGyro(COTS.ofPigeon2())
            .withSwerveModule(COTS.ofMark4i(
                    DCMotor.getKrakenX60(1), DCMotor.getKrakenX60(1), Constants.RobotConstants.WHEEL_COF, 3))
            .withBumperSize(Inches.of(32), Inches.of(32));

    @Override
    public void periodic() {
        // DO nothing
    }

    @Override
    public void periodicAfterScheduler() {
        if (Constants.currentMode == Mode.SIM) { // Only do if it is simulation AND not replay
            handleIntakeSim();
            SimulatedArena.getInstance().simulationPeriodic();
            Logger.recordOutput("Sim/SimulatedDrivetrainPose", swerveDriveSimulation.getSimulatedDriveTrainPose());
            Logger.recordOutput("Sim/Fuel", SimulatedArena.getInstance().getGamePiecesArrayByType("Fuel"));
            Logger.recordOutput("Sim/RobotFuel", ballsHeldByRobot);
        }
    }

    public void setupDriveSim() {
        this.swerveDriveSimulation = new SwerveDriveSimulation(mapleSimConfig, new Pose2d(3, 3, Rotation2d.kZero));
        var arena = new Arena2026Rebuilt(false);
        arena.setEfficiencyMode(false);
        SimulatedArena.overrideInstance(arena);
        SimulatedArena.getInstance().addDriveTrainSimulation(this.swerveDriveSimulation);
        SimulatedArena.getInstance().resetFieldForAuto();
    }

    public void resetPose(Pose2d pose) {
        this.swerveDriveSimulation.setSimulationWorldPose(pose);
    }

    private void handleIntakeSim() {
        var balls = new HashSet<Pose3d>();
        if (intakeSim != null) {
            var number = intakeSim.getGamePiecesAmount();
            int total = number;
            // this is somewhat inefficient but im tired
            for (int z = 0; z < 3; z++) { // fill up z level starting from bottom
                for (int x = 0; x < 4; x++) { // fill up x levels from back to front
                    for (int y = 0; y < 4; y++) { // fill up y levels right to left
                        if (number-- > 0) {
                            balls.add(new Pose3d(
                                    startingPoseRobotRelative
                                            .plus(new Translation3d(
                                                    Units.inchesToMeters(x * 5.91),
                                                    Units.inchesToMeters(y * 5.91),
                                                    Units.inchesToMeters(z * 5.91)))
                                            .plus(new Pose3d(swerveDriveSimulation.getSimulatedDriveTrainPose())
                                                    .getTranslation())
                                            .rotateAround(
                                                    new Pose3d(swerveDriveSimulation.getSimulatedDriveTrainPose())
                                                            .getTranslation(),
                                                    new Rotation3d(swerveDriveSimulation
                                                            .getSimulatedDriveTrainPose()
                                                            .getRotation())),
                                    Rotation3d.kZero));
                        }
                    }
                }
            }
            this.ballsHeldByRobot = balls.toArray(new Pose3d[intakeSim.getGamePiecesAmount()]);
        }
    }
}
