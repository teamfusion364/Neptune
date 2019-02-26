package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.defaultcommands.DriveOpenLoop;
import frc.robot.misc.subsystems.Piston;
import frc.robot.RobotMap;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain extends Subsystem {

  private static DriveTrain Instance = null;
  private TalonSRX leftDrive;
  private TalonSRX rightDrive;
  private VictorSPX leftRearDriveSlave;
  private VictorSPX leftTopDriveSlave;
  private VictorSPX rightRearDriveSlave;
  private VictorSPX rightTopDriveSlave;

  private VictorSPX climbDrive;

  private DoubleSolenoid sh;
  public Piston shifter;
  public AHRS navX;

  public DriveTrain() {

    rightDrive = new TalonSRX(RobotMap.rightFrontDrive);
    leftDrive = new TalonSRX(RobotMap.leftFrontDrive);

    leftRearDriveSlave = new VictorSPX(RobotMap.leftRearDrive);
    leftTopDriveSlave = new VictorSPX(RobotMap.leftTopDrive);
    rightRearDriveSlave = new VictorSPX(RobotMap.rightRearDrive);
    rightTopDriveSlave = new VictorSPX(RobotMap.rightTopDrive);

    climbDrive = new VictorSPX(2);

    rightRearDriveSlave.follow(rightDrive);
    rightTopDriveSlave.follow(rightDrive);
    leftRearDriveSlave.follow(leftDrive);
    leftTopDriveSlave.follow(leftDrive);

    rightDrive.setNeutralMode(NeutralMode.Brake);
    leftDrive.setNeutralMode(NeutralMode.Brake);
    leftRearDriveSlave.setNeutralMode(NeutralMode.Brake);
    leftTopDriveSlave.setNeutralMode(NeutralMode.Brake);
    rightRearDriveSlave.setNeutralMode(NeutralMode.Brake);
    rightTopDriveSlave.setNeutralMode(NeutralMode.Brake);

    sh = new DoubleSolenoid(RobotMap.primaryPCM, RobotMap.shifterPort1, RobotMap.shifterPort2);
    shifter = new Piston(sh, "Shifter");

    try {
      navX = new AHRS(SPI.Port.kMXP);
    } catch (RuntimeException ex) {
      DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
    }

    rightDrive.configFactoryDefault();
    leftDrive.configFactoryDefault();

    rightDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.PIDLoopIdx,
        RobotMap.TimeoutMs);
    leftDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.PIDLoopIdx,
        RobotMap.TimeoutMs);

    rightDrive.setSensorPhase(RobotMap.rightDriveReverse);
    rightDrive.setInverted(RobotMap.rightDriveReverseEncoder);
    leftDrive.setSensorPhase(RobotMap.leftDriveReverse);
    leftDrive.setInverted(RobotMap.leftDriveReverseEncoder);

    rightDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, RobotMap.TimeoutMs);
    rightDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, RobotMap.TimeoutMs);
    leftDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, RobotMap.TimeoutMs);
    leftDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, RobotMap.TimeoutMs);

    rightDrive.configNominalOutputForward(RobotMap.driveNominalOutputForward, RobotMap.TimeoutMs);
    rightDrive.configNominalOutputReverse(RobotMap.driveNominalOutputReverse, RobotMap.TimeoutMs);
    rightDrive.configPeakOutputForward(RobotMap.drivePeakOutputForward, RobotMap.TimeoutMs);
    rightDrive.configPeakOutputReverse(RobotMap.drivePeakOutputReverse, RobotMap.TimeoutMs);
    leftDrive.configNominalOutputForward(RobotMap.driveNominalOutputForward, RobotMap.TimeoutMs);
    leftDrive.configNominalOutputReverse(RobotMap.driveNominalOutputReverse, RobotMap.TimeoutMs);
    leftDrive.configPeakOutputForward(RobotMap.drivePeakOutputForward, RobotMap.TimeoutMs);
    leftDrive.configPeakOutputReverse(RobotMap.drivePeakOutputReverse, RobotMap.TimeoutMs);

    rightDrive.selectProfileSlot(RobotMap.SlotIdx, RobotMap.PIDLoopIdx);
    rightDrive.config_kF(RobotMap.SlotIdx, RobotMap.driveFgain, RobotMap.TimeoutMs);
    rightDrive.config_kP(RobotMap.SlotIdx, RobotMap.drivePgain, RobotMap.TimeoutMs);
    rightDrive.config_kI(RobotMap.SlotIdx, RobotMap.driveIgain, RobotMap.TimeoutMs);
    rightDrive.config_kD(RobotMap.SlotIdx, RobotMap.driveDgain, RobotMap.TimeoutMs);
    leftDrive.selectProfileSlot(RobotMap.SlotIdx, RobotMap.PIDLoopIdx);
    leftDrive.config_kF(RobotMap.SlotIdx, RobotMap.driveFgain, RobotMap.TimeoutMs);
    leftDrive.config_kP(RobotMap.SlotIdx, RobotMap.drivePgain, RobotMap.TimeoutMs);
    leftDrive.config_kI(RobotMap.SlotIdx, RobotMap.driveIgain, RobotMap.TimeoutMs);
    leftDrive.config_kD(RobotMap.SlotIdx, RobotMap.driveDgain, RobotMap.TimeoutMs);

    rightDrive.configMotionCruiseVelocity(RobotMap.driveCruiseVelocity, RobotMap.TimeoutMs);
    rightDrive.configMotionAcceleration(RobotMap.driveAcceleration, RobotMap.TimeoutMs);
    leftDrive.configMotionCruiseVelocity(RobotMap.driveCruiseVelocity, RobotMap.TimeoutMs);
    leftDrive.configMotionAcceleration(RobotMap.driveAcceleration, RobotMap.TimeoutMs);

    rightDrive.setSelectedSensorPosition(0);
    leftDrive.setSelectedSensorPosition(0);

  }
  public synchronized static DriveTrain getInstance() {
    if (Instance == null) {
        Instance = new DriveTrain();
    }
    return Instance;
}

  public void openLoop(double left, double right) {
    leftDrive.set(ControlMode.PercentOutput, left);
    rightDrive.set(ControlMode.PercentOutput, -right);
  }
  
  public void climb(){
    climbDrive.set(ControlMode.PercentOutput, 1);
  }

  public void stop() {
    leftDrive.set(ControlMode.PercentOutput, 0);
    rightDrive.set(ControlMode.PercentOutput, 0);
  }

  public double getGyroAngle() {
    return navX.getYaw();
  }

  public void zeroGyro() {
    navX.reset();
  }
  public double getDisplacementX(){
    return navX.getDisplacementX();
  }
  public double getDisplacementY(){
    return navX.getDisplacementY();
  }
  public void postSmartDashVars(){
    SmartDashboard.putNumber("Left Drive Cur", leftDrive.getOutputCurrent());
    SmartDashboard.putNumber("Right Drive Cur: ", rightDrive.getOutputCurrent());
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new DriveOpenLoop());
  }
}
