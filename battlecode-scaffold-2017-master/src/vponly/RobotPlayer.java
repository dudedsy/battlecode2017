package vponly;
import battlecode.common.*;
import botcontrollers.*;
import communications.*;
import movement.*;

public strictfp class RobotPlayer {
    public static RobotController rc;
    public static RobotType rt;
    public static Team myTeam;
    public static float bodyRadius, strideRadius;
    public static RobotInfo[] nearbyTeammates;
    public static int numNearby;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    public static void run(RobotController rc) throws GameActionException {
    	System.out.println("I'm alive!");

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;
        rt = rc.getType();
        myTeam = rc.getTeam();
        bodyRadius = rt.bodyRadius;
        strideRadius = rt.strideRadius;
        
      //Code here runs once. Initialize various objects.
        try{
			Move.init(rc);
			Comms.init(rc);
			BulletDodge.init(rc);
			Donations.init(rc);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                archonRun();
                break;
            case GARDENER:
                gardenerRun();
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
    static void archonRun(){
    	MapLocation center;
    	while(true){
    		try{
    			BulletDodge.dodge();
    			nearbyTeammates = rc.senseNearbyRobots(3, myTeam);
    			numNearby = nearbyTeammates.length;
            	if(numNearby == 0){
            		if(rc.hasRobotBuildRequirements(RobotType.GARDENER) && rc.getRoundNum()%10==0){
                		buildGardener();
                	}
            		Move.tryMove(Move.randomDirection(),3,10);
            	}
            	else{
            		nearbyTeammates = rc.senseNearbyRobots(5, myTeam);
        			numNearby = nearbyTeammates.length;
            		center = findCenterNearbyTeam();
            		Move.tryMove(new Direction(center,rc.getLocation()),3,15);
            	}
            	Donations.ifReady(100,200);
            	
            	Clock.yield();
    		}catch(Exception e){
    			System.out.println(e);
    			e.printStackTrace();
    		}
    		
    	}
    	
    }
    static void gardenerRun(){
    	try{
    		//MapLocation center = rc.getLocation();
    		//setupCircle();
    	}catch(Exception e){
    		System.out.println(e);
    		e.printStackTrace();
    	}
    	
    	while(true){
    		try{
		    	tryWater();
		    	BulletDodge.dodge();
		    	Donations.ifReady(100,200);
		    	tryWater();
		    	tryPlant();
		    	tryWater();
		    	Clock.yield();
    		}catch(Exception e){
    			System.out.println(e);
    			e.printStackTrace();
    		}
    	}
    }
    
    static MapLocation findCenterNearbyTeam(){
		if(numNearby == 1){return nearbyTeammates[0].location;}
    	float xsum = 0,ysum = 0;
    	for(RobotInfo ri : nearbyTeammates){
    		xsum += ri.location.x;
    		ysum += ri.location.y;
    	}
    	return new MapLocation(xsum/numNearby,ysum/numNearby);
    }
    
    static boolean tryPlant() throws GameActionException{
		if(!rc.isBuildReady()||!rc.hasTreeBuildRequirements()){return false;}
		Direction buildDir = Move.randomDirection();
		int smallTurn = 0;
		while(!rc.canPlantTree(buildDir.rotateLeftDegrees(smallTurn))){
			smallTurn += 10;
			if(smallTurn > 350){return false;}
		}
		rc.plantTree(buildDir.rotateLeftDegrees(smallTurn));
		return true;
	}
    
    static void buildGardener() throws GameActionException{
    	float dir = Move.randomDirection().radians;
    	float add = 0;
    	while(!rc.canBuildRobot(RobotType.GARDENER, new Direction(dir+add)) && add < 6.3){
    		add+=.1;
    	}if(add>6.2){return;}
    	rc.buildRobot(RobotType.GARDENER, new Direction(dir+add));
    }
    
    static boolean tryWater() throws GameActionException{
		if(!rc.canWater()){return false;}
		TreeInfo[] myTrees = rc.senseNearbyTrees(bodyRadius+strideRadius, myTeam);
		int i = 0;
		int treeLength = myTrees.length;
		if(treeLength == 0){return false;}
		while(i< treeLength && myTrees[i]!=null){
			if(rc.canWater(myTrees[i].ID)&&myTrees[i].health<47){
				rc.water(myTrees[i].ID);
				return true;
			}
			i++;
		}
		return false;
	}
    
}