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
		BulletPath[] danger = new BulletPath[MAXBULLETPATHS];
		int dcount = 0;
		BulletInfo bullet;

        float safeRadius = rt.bodyRadius+rt.strideRadius;
        BulletPath bulletPath;
        LineMath.Vector startToRobot,finishToRobot;
        
		for(int i = 0;i<bullets.length;i++){
			// Get relevant bullet information
			bullet = bullets[i];
			if(bullet==null){break;}

			//calculate vectors
	        startToRobot = new LineMath.Vector(bullet.location,myLocation);
	        bulletPath = new BulletPath(bullet);
	        
	        // If bullet doesn't pass close enough for us to get in danger, ignore it.
	        bulletPath.perpDist = LineMath.dot(startToRobot,bulletPath.perpendicular);
	        if(Math.abs(bulletPath.perpDist)>safeRadius){continue;}
	        
	        //TODO: account for start 
	        //TODO: account for endpoint
	        //TODO:account for trees?

	        //if we made it this far, add it to the danger list
	        //then iff we've filled the list we can just break out... fuck it
	        danger[dcount++] = bulletPath;
	        if(dcount>MAXBULLETPATHS){break;}
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
	public static class BulletPath{
		public LineMath.UnitVector perpendicular, parallel;
		public MapLocation start,finish;
		public float speed, damage;
		public int ID;
		public float perpDist;
		public float endDist;
		public float startDist;
		
		public BulletPath(BulletInfo bullet){
			start = bullet.location;
			parallel = new LineMath.UnitVector(bullet.dir);
			perpendicular = LineMath.UnitVector.perpUnit(parallel);
			finish = new MapLocation(start.x+bullet.speed*parallel.dx,start.y+bullet.speed*parallel.dy);
			speed = bullet.speed;
			damage = bullet.damage;
			ID = bullet.ID;
		}
	}
	
}
