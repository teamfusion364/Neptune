package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.defaultcommands.Periodic;
import frc.robot.RobotMap;
import frc.robot.util.PIDCalc;
import frc.robot.util.prefabs.subsystems.*;
import frc.robot.util.prefabs.commands.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.States;
public class SuperStructure extends Subsystem {
  // public TalonBase rightDrive;
  // public TalonBase leftDrive;
  // public TalonBase lift;
  public TalonBase arm;
  // public TalonBase intake;

  public TalonBase rightDrive;
  public TalonBase leftDrive;
  public TalonBase lift;
  public TalonBase intake;
  
  private TalonSRX rDrive;
  private TalonSRX lDrive;
  private TalonSRX lt;
  private TalonSRX a;
  private TalonSRX in;


  public DriveTrain driveTrain;

  private VictorSPX lRearDriveSlave;
  private VictorSPX lFrontDriveSlave;
  private VictorSPX rRearDriveSlave;
  private VictorSPX rFrontDriveSlave;
  private VictorSPX liftSlave;
  private VictorSPX intakeSlave;

  public Piston claw;
  public Piston lever;
  public Piston back;
  public Piston front;
  public Piston shifter;

  private DoubleSolenoid cl;
  private DoubleSolenoid le;
  private DoubleSolenoid ba;
  private DoubleSolenoid wh;
  private DoubleSolenoid sh;

  public AHRS navX;
  public PIDCalc pidNavX;

  public DigitalInput iL;
  public DigitalInput aL;
  public DigitalInput lLL;
  public DigitalInput uLL;

  /**Access limit switches as follows
   * <p>0: Cargo
   * <p>1: Arm
   * <p>2: Lower Lift
   * <p>3: Upper Lift
   */
  public boolean[] limitArray = {false, false, false, false};

  public SuperStructure(){
    //masters
    rDrive = new TalonSRX(RobotMap.rightTopDrive);
    lDrive = new TalonSRX(RobotMap.leftTopDrive);
    lt = new TalonSRX(RobotMap.leftLift);
    a = new TalonSRX(RobotMap.arm);
    in = new TalonSRX(RobotMap.rightClaw);

    //followers
    lRearDriveSlave = new VictorSPX(RobotMap.leftRearDrive);
    lFrontDriveSlave = new VictorSPX(RobotMap.leftFrontDrive);
    rRearDriveSlave = new VictorSPX(RobotMap.rightRearDrive);
    rFrontDriveSlave = new VictorSPX(RobotMap.rightFrontDrive);
    liftSlave = new VictorSPX(RobotMap.rightLift);
    intakeSlave = new VictorSPX(RobotMap.leftClaw);

    //Pistons
    //PCM 1
    cl = new DoubleSolenoid(RobotMap.primaryPCM, RobotMap.intakePort1, RobotMap.intakePort2);
    le = new DoubleSolenoid(RobotMap.primaryPCM, RobotMap.leverPort1, RobotMap.leverPort2);
    sh = new DoubleSolenoid(RobotMap.primaryPCM, RobotMap.shifterPort1, RobotMap.shifterPort2);
    //PCM 2
    ba = new DoubleSolenoid(RobotMap.secondaryPCM, RobotMap.climbPort1, RobotMap.climbPort2);
    wh = new DoubleSolenoid(RobotMap.secondaryPCM, RobotMap.climbPort3, RobotMap.climbPort4);
   

    //Right Drive Train
    rightDrive = new TalonBase(        
        rDrive, 
        RobotMap.driveNominalOutputForward, 
        RobotMap.driveNominalOutputReverse, 
        RobotMap.drivePeakOutputForward, 
        RobotMap.drivePeakOutputReverse, 
        RobotMap.driveCruiseVelocity, 
        RobotMap.driveAcceleration, 
        RobotMap.driveDampen, 
        "RightDrive");
    rRearDriveSlave.follow(rDrive);
    rFrontDriveSlave.follow(rDrive);

    //Left Drive Train
    leftDrive = new TalonBase(        
        lDrive, 
        RobotMap.driveNominalOutputForward, 
        RobotMap.driveNominalOutputReverse, 
        RobotMap.drivePeakOutputForward, 
        RobotMap.drivePeakOutputReverse, 
        RobotMap.driveCruiseVelocity, 
        RobotMap.driveAcceleration, 
        RobotMap.driveDampen, 
        "LeftDrive");
    lRearDriveSlave.follow(lDrive);
    lFrontDriveSlave.follow(lDrive);

    driveTrain = new DriveTrain(leftDrive, rightDrive);
    
    //Lift
    lift = new TalonBase(        
        lt, 
        RobotMap.liftNominalOutputForward, 
        RobotMap.liftNominalOutputReverse, 
        RobotMap.liftPeakOutputForward, 
        RobotMap.liftPeakOutputReverse, 
        RobotMap.liftCruiseVelocity, 
        RobotMap.liftAcceleration, 
        RobotMap.liftBounded, 
        RobotMap.liftLowerBound, 
        RobotMap.liftUpperBound, 
        RobotMap.liftDampen, 
        "Lift"){
          public void initDefaultCommand(){
            lift.setDefaultCommand(new OpenLoop(lift, RobotMap.liftAxis, RobotMap.liftDeadband));
          }
        };
    liftSlave.follow(lt);
    lLL = new DigitalInput(RobotMap.lowerLiftLimitSwitch);
    uLL = new DigitalInput(RobotMap.upperLiftLimitSwitch);
    
    //Arm
    arm = new TalonBase(
        a, 
        RobotMap.armNominalOutputForward, 
        RobotMap.armNominalOutputReverse, 
        RobotMap.armPeakOutputForward, 
        RobotMap.armPeakOutputReverse, 
        RobotMap.armCruiseVelocity, 
        RobotMap.armAcceleration, 
        RobotMap.armBounded, 
        RobotMap.armLowerBound, 
        RobotMap.armUpperBound, 
        RobotMap.armDampen, 
        "Arm"){
      public void initDefaultCommand(){
        arm.setDefaultCommand(new OpenLoop(arm, RobotMap.armAxis, RobotMap.armDeadBand));
      }
    };
    aL = new DigitalInput(RobotMap.armLimitSwitch);

    //Intake 
    // intake = new TalonBase(in, 0, 0, 0.25, -0.25, 3750, 1500, false, 0, 0, 0.67);
    intake = new TalonBase(in, RobotMap.intakeDampen, "Intake");
    intakeSlave.follow(in);
    iL = new DigitalInput(RobotMap.ballLimitSwitch);

    //Pistons
    claw = new Piston(cl, "Claw");
    lever = new Piston(le, "Lever");
    back = new Piston(ba, "Back");
    front = new Piston(wh, "Front");
    shifter = new Piston(sh, "Shifter");

    //Gyro
    navX = new AHRS(SPI.Port.kMXP);
    pidNavX = new PIDCalc(RobotMap.navXPterm, RobotMap.navXIterm, RobotMap.navXDterm, RobotMap.navXFterm, "NavX");
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
     setDefaultCommand(new Periodic());
  }
  //Drive Train
  public void driveOpenLoop(double left, double right){
    driveTrain.openLoop(left, right);
  }

  public void resetDriveEncoders(){
    // rightDrive.zero();
    // leftDrive.zero();
  }
  //Gyro
  public double getYaw(){
    return navX.getYaw();
  }
  public void zeroYaw(){
    navX.reset();
  }
  //Misc
  /**Sets enocders of arm and lift to zero */
  public void resetEncoders(){
    lift.zero();
    arm.zero();
  }
  /**Because none of the grip runs default commands,
   * the grip is inactive when no commands are being run
   * by any three of the subsystems of the larger apparatus
   */
  public boolean gripInactive(){
      return (Robot.superStructure.intake.noCommand() && Robot.superStructure.lever.noCommand() && Robot.superStructure.claw.noCommand());
  }
  /**Posts MotionMagic Trajectory Data to SmartDashboard for each ComplexTalon */
  public void postImplementation(){
    lift.instrumentation();
    arm.instrumentation();
    rightDrive.instrumentation();
    leftDrive.instrumentation();
  }

  public void postSmartDashVars(){
    intake.postSmartDashVars();
    arm.postSmartDashVars();
    claw.postSmartDashVars();
    lever.postSmartDashVars();
    back.postSmartDashVars();
    front.postSmartDashVars();
    shifter.postSmartDashVars();
    SmartDashboard.putString("Object State:", States.objState.toString());
    SmartDashboard.putString("Action State:", States.actionState.toString());
    SmartDashboard.putString("Loop State:", States.loopState.toString());
    SmartDashboard.putString("Drive State:", States.driveState.toString());
    SmartDashboard.putString("Drive Motion State:", States.driveMotionState.toString());
    SmartDashboard.putString("Score State:", States.scoreState.toString());
    SmartDashboard.putString("Climb State:", States.climbState.toString());

    SmartDashboard.putBoolean("Intake Limit: ", limitArray[0]);
  }
  }

