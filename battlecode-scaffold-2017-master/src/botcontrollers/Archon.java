package botcontrollers;

import battlecode.common.*;
import communications.Comms;
import communications.FastBotInfo;
import movement.Move;

public class Archon{
	private static RobotController rc;
	public static int[] myPackedInfo;
	private static RobotType rt;
	public static MapLocation location;
	

	public static void run(RobotController rc){
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
				System.out.println("I'm just an Archon.");
			}
			Comms.listAdd(Comms.MY_ARCHONS,myPackedInfo,nArchons);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		while(true){
			try{//mainloop code here
				
				Clock.yield();
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
}
