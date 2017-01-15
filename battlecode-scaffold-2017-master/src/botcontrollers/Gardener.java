package botcontrollers;

import battlecode.common.*;
import communications.*;
import movement.Move;

public class Gardener{
	private static RobotController rc;
	static RobotType rt;
	static final RobotType[] buildList = {RobotType.SCOUT,RobotType.LUMBERJACK,RobotType.LUMBERJACK,RobotType.TANK};
	static int buildNum;
	static float bodyRadius;
	static float strideRadius;
	static Team myTeam;
	static MapLocation MyLocation;
	static TreeInfo[] nearbyTrees;
	
	public static void run(RobotController rc){
		Gardener.rc = rc;
		rt = RobotType.GARDENER;
		int round;
		bodyRadius = rt.bodyRadius;
		strideRadius = rt.strideRadius;
		myTeam = rc.getTeam();
		
		try{//Code here runs once.
			register();
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		while(true){
			round = rc.getRoundNum();
			try{//mainloop code here
				MyLocation = rc.getLocation();
				nearbyTrees = rc.senseNearbyTrees(-1,myTeam);
				System.out.println("Bullet Tree Dists: ");
				if(nearbyTrees.length == 0){System.out.println("none");}
				else{
					for(TreeInfo tree : nearbyTrees){
						System.out.println(MyLocation.distanceTo(tree.location));
					}
				}
				tryWater();
				Move.tryMove(chooseDestination());
				tryWater();
				if(round%7==0){tryBuild();}
				if(round%14==0){tryPlant();}
				Donations.ifReady();
				Clock.yield();
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
	
	static Direction chooseDestination(){
		TreeInfo[] myTrees = rc.senseNearbyTrees(-1, myTeam);
		int i = 0;
		int treeLength = myTrees.length;
		if(treeLength == 0){return Move.randomDirection();}
		TreeInfo toWater = myTrees[0];
		float distToWater = toWater.location.distanceTo(MyLocation);
		float healthToWater = toWater.health;
		TreeInfo current;
		while(i< treeLength && myTrees[i]!=null){
			current = myTrees[i];
			i++;
			if(current.health>45){continue;}
			if(current.location.distanceTo(MyLocation)>5){continue;}
		}
		//TODO: make it actually do something...
		return Move.randomDirection();
		
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
	
	static boolean tryBuild() throws GameActionException{
		RobotType toBuild = buildList[buildNum];
		if(!rc.isBuildReady()||!rc.hasRobotBuildRequirements(toBuild)){return false;}
		Direction buildDir = Move.randomDirection();
		int smallTurn = 0;
		while(!rc.canBuildRobot(toBuild, buildDir.rotateLeftDegrees(smallTurn))){
			smallTurn += 10;
			if(smallTurn == 360){return false;}
		}
		rc.buildRobot(toBuild, buildDir.rotateLeftDegrees(smallTurn));
		buildNum = (buildNum+1)%4;
		return true;
	}
	
	static boolean tryPlant() throws GameActionException{
		if(!rc.isBuildReady()||!rc.hasTreeBuildRequirements()){return false;}
		Direction buildDir = Move.randomDirection();
		int smallTurn = 0;
		while(!rc.canPlantTree(buildDir.rotateLeftDegrees(smallTurn))){
			smallTurn += 10;
			if(smallTurn == 360){return false;}
		}
		rc.plantTree(buildDir.rotateLeftDegrees(smallTurn));
		return true;
	}
	
	static void register() throws GameActionException{
		Comms.listAdd(Comms.MY_GARDENERS, new FastBotInfo(rc));
	}
}

