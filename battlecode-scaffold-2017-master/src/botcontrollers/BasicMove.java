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
		if(rc.hasMoved()){return false;}
		return tryMove(direction, 10,5);
	}
	
	static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {
		if(rc.hasMoved()){return false;}
        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }
}
