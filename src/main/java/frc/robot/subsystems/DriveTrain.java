package frc.robot.subsystems;

import static frc.robot.Conversions.deadband;
import static frc.robot.RobotMap.BLANGLE;
import static frc.robot.RobotMap.BLDRIVE;
import static frc.robot.RobotMap.BRANGLE;
import static frc.robot.RobotMap.BRDRIVE;
import static frc.robot.RobotMap.FLANGLE;
import static frc.robot.RobotMap.FLDRIVE;
import static frc.robot.RobotMap.FRANGLE;
import static frc.robot.RobotMap.FRDRIVE;
import static frc.robot.RobotMap.MOD0OFFSET;
import static frc.robot.RobotMap.MOD1OFFSET;
import static frc.robot.RobotMap.MOD2OFFSET;
import static frc.robot.RobotMap.MOD3OFFSET;
import static frc.robot.RobotMap.TRACKWIDTH;
import static frc.robot.RobotMap.WHEELBASE;
import static frc.robot.RobotMap.gyroSet;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Neptune;
import frc.robot.commands.DrivetrainCommand;
import frc.robot.misc.math.Rotation2;
import frc.robot.misc.math.Vector2;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import static frc.robot.Conversions.*;
 

public class Drivetrain extends Subsystem {

    private static Drivetrain Instance = null;
	/*
	 * 0 is Front Right
	 * 1 is Front Left
	 * 2 is Back Left
	 * 3 is Back Right
	 */
    private SwerveMod[] mSwerveModules;


    public Drivetrain() {
            mSwerveModules = new SwerveMod[] {
                    new SwerveMod(0,
                            new Vector2(-TRACKWIDTH / 2.0, WHEELBASE / 2.0),
                            new TalonSRX(FRANGLE),
                            new TalonSRX(FRDRIVE),
                            false, 
                            false,
                            MOD0OFFSET),
                    new SwerveMod(1,
                            new Vector2(TRACKWIDTH / 2.0, WHEELBASE / 2.0),
                            new TalonSRX(FLANGLE),
                            new TalonSRX(FLDRIVE),
                            true,
                            true,
                            MOD1OFFSET),
                    new SwerveMod(2,
                            new Vector2(TRACKWIDTH / 2.0, -WHEELBASE / 2.0),
                            new TalonSRX(BLANGLE),
                            new TalonSRX(BLDRIVE),
                            true,
                            false,
                            MOD2OFFSET),
                    new SwerveMod(3,
                            new Vector2(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0),
                            new TalonSRX(BRANGLE),
                            new TalonSRX(BRDRIVE),
                            true,
                            false,
                            MOD3OFFSET)
            };
         
            
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

    public void holonomicDrive(Vector2 translation, double rotation, boolean speed) {
            Vector2 velocity;
            for(SwerveMod mod : getSwerveModules()){
                Vector2 newTranslation = null;
                newTranslation = translation.rotateBy(Rotation2.fromDegrees(Neptune.elevator.getGyro()));

                velocity = mod.getModulePosition().normal().scale(deadband(rotation)).add(newTranslation);
                mod.setTargetVelocity(velocity, speed, rotation);
            }        
    }
    public void updateKinematics(){
        for (SwerveMod mod : getSwerveModules()){
            mod.setTargetAngle(mod.targetAngle);
            mod.setTargetSpeed(mod.targetSpeed);
        }
    }
    public void setZero(){
        for(SwerveMod mod : getSwerveModules()){
            mod.getAngleMotor().set(ControlMode.Position, mod.mZeroOffset);
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

    public double closestGyroSetPoint(){
        double checkPoint = 0;
        for(Double setPoint : gyroSet){
            double initial = setPoint - modulate360(Neptune.elevator.getYaw());
            if(checkPoint == 0) checkPoint = initial;
            if(Math.abs(initial) < Math.abs(checkPoint)) checkPoint = initial;
        }
                SmartDashboard.putNumber("acutal point ", checkPoint + Neptune.elevator.getYaw());

        return checkPoint;
    }

    public void setTrackingMode(){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3); //Turns LED off
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(0); //Begin Processing Vision
    }

    public void setDriverCamMode(){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1); //Turns LED off
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(1); //Disable Vision Processing
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new DrivetrainCommand(this));
    }
    
}
