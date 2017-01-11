package botcontrollers;

import battlecode.common.*;

public class BasicMove {
	public static RobotController rc;
	public static RobotType rt;
	public static float bodyRadius;
	public static float strideRadius;
	
	public static void init(RobotController rc){
		BasicMove.rc = rc;
		BasicMove.rt = rc.getType();
		BasicMove.bodyRadius = rt.bodyRadius;
		BasicMove.strideRadius = rt.strideRadius;
	}
	
	/**
	 * 
	 * tries to move a certain direction
	 * at full stride
	 * true if it has moved.
	 * false if it decided it couldn't move.
	 * 
	 * @return success or failure
	 */
	public static boolean tryMove(Direction direction) throws GameActionException{
		if(rc.canMove(direction)&&!rc.hasMoved()){
			rc.move(direction);
			return true;
		}
		return false;
	}
}
