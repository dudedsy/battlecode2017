package bulletdodge;

import battlecode.common.*;

/**
 * This class control functions related to bullet sensing and dodging.
 * A robot is no good to us dead, first movement priority is to dodge bullets and danger.
 * 
 * Must set rc before use.
 */
public strictfp class BulletDodge {
	public static BulletInfo[] bullets;
	public static RobotController rc;
	public static RobotType rt = rc.getType();
	
	/**
	 * Stores the sensor data on bullets to a local array.
	 */
	public static void bulletScan(){
		bullets = rc.senseNearbyBullets();
	}
	
	/**
	 * detects whether the current location is in danger
	 * assume the current bot's radius
	 * must run bulletScan() first.
	 * 
	 * @return true if a bullet will hit the current robot location next round . false otherwise.
	 */
	public static boolean incoming(){
		return incoming(rc.getLocation());
	}
	/**
	 * detects whether the current location (+bot radius) is in danger
	 * automagically scans first.
	 * 
	 * @param any boolean for a scan first.  To omit scan, use form with no parameter.
	 * 
	 * @return true if a bullet will hit the current robot location next round . false otherwise.
	 */
	public static boolean incoming(boolean scanfirst){
		bulletScan();
		return incoming();
	}
	/**
	 * detects whether the location is in danger
	 * assume the current bot's radius
	 * 
	 * @param location - the location to check
	 * 
	 */
	public static boolean incoming(MapLocation location){
		float x = location.x;
		float y = location.y;
		float radius = rt.bodyRadius;
		
		
		
		return false;
	}
	
}
