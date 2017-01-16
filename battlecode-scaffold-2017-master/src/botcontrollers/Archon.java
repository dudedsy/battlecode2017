package botcontrollers;

import battlecode.common.*;
import communications.Comms;
import communications.FastBotInfo;
import movement.*;

public class Archon{
	private static RobotController rc;
	public static int[] myPackedInfo;
	private static RobotType rt;
	public static MapLocation location;
	

	public static void run(RobotController rc){
		System.out.println("I'm an Archon!");
		Archon.rc = rc;
		int nArchons;
		boolean chief = false;
		rt = rc.getType();
		location = rc.getLocation();
		try{//Code here runs once on the first turn only.			
			myPackedInfo = FastBotInfo.pack(rc.getRoundNum(), rc.getID(), rc.getHealth(), rt, location);
			nArchons = Comms.listLength(Comms.MY_ARCHONS);
			switch(nArchons){
			case 0:
				System.out.println("I am chief!");
				chief = true;
				break;
			case 1:
				System.out.println("I'm first officer!");
				break;
			default:
				System.out.println("I'm the third Archon...");
			}
			Comms.listAdd(Comms.MY_ARCHONS,myPackedInfo,nArchons);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		while(true){
			try{//mainloop code here
				hireIfNeeded();
				BulletDodge.dodge();
				Move.tryMove(Move.randomDirection());
				/*if(chief){
					System.out.print("There are ");
					System.out.print(Comms.listLength(Comms.MY_GARDENERS));
					System.out.println(" friendly Gardeners");
				}*/
				Donations.ifReady();
				Clock.yield();
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
	static boolean hireIfNeeded() throws GameActionException{
		if(!rc.isBuildReady()||!rc.hasRobotBuildRequirements(RobotType.GARDENER)){return false;}
		if(Comms.listLength(Comms.MY_GARDENERS)>5){return false;}
		Direction buildDir = Move.randomDirection();
		int smallTurn = 0;
		while(!rc.canBuildRobot(RobotType.GARDENER, buildDir.rotateLeftDegrees(smallTurn))){
			smallTurn += 10;
			if(smallTurn == 360){return false;}
		}
		rc.hireGardener(buildDir.rotateLeftDegrees(smallTurn));
		return true;
	}
}
