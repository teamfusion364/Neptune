package frc.robot.defaultcommands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;

public class Manual extends Command {

    private double power;
    private double adjustedPosition;

    public Manual() {
        requires(Robot.elevatorSystem);
        setInterruptible(true);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void execute() {
        
        power = -Robot.oi2.controller2.getRawAxis(1)*0.5;
        if(power > 0.1){
            if(!Periodic.manualControl){
                adjustedPosition = Robot.superStructure.lift.getPosition();
                Periodic.manualControl = true;
            }
            adjustedPosition += 700;
        }else if(power < -0.1){
            adjustedPosition -= 700;
        }
        Robot.superStructure.lift.MoveToPosition(adjustedPosition);
       
    SmartDashboard.putNumber("Adjusted Position", adjustedPosition);
    }
    @Override
    protected void interrupted() {
        end();
    }
    @Override
    protected void end() {
    }

    @Override
    protected boolean isFinished() {
        /* This command will only end when interrupted during teleop mode
        by buttons in the Operator Interface*/
        return false;
    }

}
