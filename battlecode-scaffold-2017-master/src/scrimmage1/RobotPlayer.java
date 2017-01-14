package scrimmage1;
import battlecode.common.*;
import botcontrollers.*;
import communications.*;
import movement.*;

public strictfp class RobotPlayer {
    public static RobotController rc;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;
        
      //Code here runs once. Initialize various objects.
        try{
			Move.init(rc);
			Comms.init(rc);
			BulletDodge.init(rc);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                Archon.run(rc);
                break;
            case GARDENER:
                Gardener.run(rc);
                break;
            case SOLDIER:
                Soldier.run(rc);
                break;
            case LUMBERJACK:
                Lumberjack.run(rc);
                break;
            case SCOUT:
            	Scout.run(rc);
            	break;
            case TANK:
            	Tank.run(rc);
            	break;
            default:
            	System.out.println(rc.getType());
        }
	}
}