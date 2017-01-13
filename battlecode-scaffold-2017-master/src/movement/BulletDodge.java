package movement;

import battlecode.common.*;
import vectormath.LineMath;

/**
 * This class control functions related to bullet sensing and dodging.
 * A robot is no good to us dead, first movement priority is to dodge 
 * bullets and danger.
 * 
 * Must run init(rc) before use
 */
public strictfp class BulletDodge {
	public final static int MAXBULLETPATHS = 10;
	public static BulletInfo[] bullets;
	public static RobotController rc;
	public static RobotType rt;
	public static float bodyRadius;
	public static float strideRadius;
	
	public static void init(RobotController robc){
		rc = robc;
		rt = rc.getType();
		bodyRadius = rt.bodyRadius;
		strideRadius = rt.strideRadius;
	}
	
	/**
	 * Stores the sensor data on bullets to a local array.
	 */
	public static void bulletScan(){
		//TODO: integrate comms
		bullets = rc.senseNearbyBullets();
	}
	
	/**
	 * detects whether the current location is in danger
	 * assume the current bot's radius
	 * must run bulletScan() first.
	 * 
	 * @return true if a bullet will hit the current robot location next round . false otherwise.
	 */
	public static BulletPath[] incoming(){
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
	public static BulletPath[] incoming(boolean scanfirst){
		bulletScan(); //use scanbullets to integrate comms
		return incoming();
	}
	/**
	 * detects whether the location is in danger 
	 * bullet would hit bot at this location. 
	 * assume the current bot's radius.
	 * 
	 * TODO: perhaps a function that calculates that 
	 * we can't get out of the way of a particular bullet in time
	 * and... suicides before getting killed?  Spends a bunch of compute processing
	 * pending freeChannel or other messaging commands? Only for use when low on health.
	 * 
	 * Perhaps tries to decide whether that bullet has better liklihood of hitting
	 * and enemy or a friend if it didn't hit him.
	 * suicide to get out of it's way if it's gonna hit and enemy
	 * take it if it'd hit a friend.
	 * That's only if it's strong enough to kill the bot though...
	 * If it's an archon it should try to make one last gardener before dying.
	 * 
	 * @param location - the location to check
	 * 
	 * @return list of the bullets headed here, as indexes for
	 * the bullet list.
	 * 
	 */
	
	public static BulletPath[] incoming(MapLocation myLocation){
		
		BulletPath[] danger = new BulletPath[10];
		int dcount = 0;
		BulletInfo bullet;
		Direction propagationDirection;
        MapLocation bulletLocation;
        Direction directionToRobot;
        float distToRobot;
        MapLocation finish;
        float theta;
        float bulletSpeed;
        float robotToFinish;
		for(int i = 0;i<bullets.length;i++){
			// Get relevant bullet information
			bullet = bullets[i];
			if(bullet==null){break;}
	        propagationDirection = bullet.dir;
	        bulletLocation = bullet.location;
	        bulletSpeed = bullet.speed;
			
			//calculate relative angles and such
	        directionToRobot = bulletLocation.directionTo(myLocation);
	        theta = propagationDirection.radiansBetween(directionToRobot);
	        
	        // If theta > 90 degrees, then the bullet is traveling away from us, ignore it.
	        if (Math.abs(theta) > Math.PI/2) {continue;}
	        
	        // Calulate distance and endpoint
	        //TODO: account for trees, human shields even?  Trees dont move...
	        distToRobot = bulletLocation.distanceTo(myLocation);
	        finish = bulletLocation.add(propagationDirection,bulletSpeed);
	        robotToFinish = finish.distanceTo(myLocation);
	        
	        //TODO: if the endpoint is far enough away, it can't hit us this turn. Ignore it.
	        //TODO: (or if it's gonna hit a tree before getting in range)
	        //TODO: or if we're far enough behind the start of the path

	        
	        //calculate the prependicular distance to the robot from the line of the bullet
	        //and append it to the danger list
	        //if there's more than MAXBULLETPATHS, fuck it.
	        if((float)Math.abs(distToRobot * Math.sin(theta))<=bodyRadius){
	        	//TODO: make this right...
	        	danger[dcount++] = danger[i];
	        	if(dcount >=5){break;}
	        }
			
		}
		return danger;
	}
	/**
	 * bulletPath represents the path a bullet will take
	 * and stores the perpendicular unit vector from the bullet path.
	 * 
	 * we can move in the direction of the unit vector to escape the bullets path.
	 * 
	 * the path has a start, representing the current location of the bullet.
	 * 
	 * stay at least radius*(perpendicular unit vector) away from the line
	 * 
	 * at least in areas the bullet will travel that turn.
	 * 
	 * represent a start and an endpoint, so we can travel around the ends potentially...
	 * 
	 * parallel is the unit vector for that metric.
	 */
	public class BulletPath{
		LineMath.UnitVector perpendicular, parallel;
		MapLocation start,finish;
	}
	
}
