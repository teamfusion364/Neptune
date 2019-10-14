package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Neptune;
import frc.robot.commands.DrivetrainCommand;
import frc.robot.misc.math.Rotation2;
import frc.robot.misc.math.Vector2;

import static frc.robot.RobotMap.*;
import static frc.robot.Neptune.*;
import frc.robot.subsystems.SwerveMod.*;

public class Drivetrain extends Subsystem {

    private static Drivetrain Instance = null;
	/*
	 * 0 is Front Right
	 * 1 is Front Left
	 * 2 is Back Left
	 * 3 is Back Right
	 */
    private SwerveMod[] mSwerveModules;
    private int w = WHEELBASE;
    private int t = TRACKWIDTH;
    private Command zero;

    public Drivetrain() {
            mSwerveModules = new SwerveMod[] {
                    new SwerveMod(0,
                            new Vector2(TRACKWIDTH / 2.0, WHEELBASE / 2.0),
                            new TalonSRX(FRANGLE),
                            new TalonSRX(FRDRIVE),
                            MOD0OFFSET),
                    new SwerveMod(1,
                            new Vector2(-TRACKWIDTH / 2.0, WHEELBASE / 2.0),
                            new TalonSRX(FLANGLE),
                            new TalonSRX(FLDRIVE),
                            MOD1OFFSET),
                    new SwerveMod(2,
                            new Vector2(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0),
                            new TalonSRX(BLANGLE),
                            new TalonSRX(BLDRIVE),
                            MOD2OFFSET),
                    new SwerveMod(3,
                            new Vector2(TRACKWIDTH / 2.0, -WHEELBASE / 2.0),
                            new TalonSRX(BRANGLE),
                            new TalonSRX(BRDRIVE),
                            MOD3OFFSET)
            };

            mSwerveModules[0].setDriveInverted(true);
            mSwerveModules[2].setDriveInverted(true);
            mSwerveModules[3].setDriveInverted(true);
            //mSwerveModules[2].setSensorPhase(true);

            updateKinematics();
    } 

    public synchronized static Drivetrain getInstance() {
        if (Instance == null) {
          Instance = new Drivetrain();
        }
        return Instance;
      }


    public SwerveMod getSwerveModule(int i) {
        return mSwerveModules[i];
    }

    public void holonomicDrive(Vector2 translation, double rotation, boolean fieldOriented) {
        if (fieldOriented) {
            // need to get pigeon vector
            translation = translation.rotateBy(Rotation2.fromDegrees(Neptune.elevator.getYaw()).inverse());
        }

        for (SwerveMod mod : getSwerveModules()) {
            Vector2 velocity = mod.getModulePosition().normal().scale(rotation).add(translation);
            mod.setTargetVelocity(velocity);
        }
    }
    public void setKinematics(){
        for (SwerveMod mod : getSwerveModules()){
            double targetAngle;
            double targetSpeed;

                targetAngle = mod.targetAngle;
                targetSpeed = mod.targetSpeed;

            final double currentAngle = mod.getCurrentAngle();

            // Change the target angle so the delta is in the range [-pi, pi) instead of [0, 2pi)
            double delta = targetAngle - currentAngle;
            if (delta >= Math.PI) {
                targetAngle -= 2.0 * Math.PI;
            } else if (delta < -Math.PI) {
                targetAngle += 2.0 * Math.PI;
            }

            // Deltas that are greater than 90 deg or less than -90 deg can be inverted so the total movement of the module
            // is less than 90 deg by inverting the wheel direction
            delta = targetAngle - currentAngle;
            if (delta > Math.PI / 2.0 || delta < -Math.PI / 2.0) {
                // Only need to add pi here because the target angle will be put back into the range [0, 2pi)
                targetAngle += Math.PI;

                targetSpeed *= -1.0;
            }

            // Put target angle back into the range [0, 2pi)
            targetAngle %= 2.0 * Math.PI;
            if (targetAngle < 0.0) {
                targetAngle += 2.0 * Math.PI;
            }
            mod.setTargetAngleVal(targetAngle);
            mod.setTargetSpeedVal(targetSpeed);
        }
    }
    public void updateKinematics(){
        for (SwerveMod mod : getSwerveModules()){
            mod.setTargetAngle(mod.targetAngle);
            mod.setTargetSpeed(mod.targetSpeed);
        }
    }

    public void stopDriveMotors() {
        for (SwerveMod module : mSwerveModules) {
            module.setTargetSpeed(0);
        }
    }

    public SwerveMod[] getSwerveModules() {
        return mSwerveModules;
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new DrivetrainCommand(this));
    }
    
}
