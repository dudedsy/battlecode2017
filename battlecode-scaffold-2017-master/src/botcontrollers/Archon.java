package botcontrollers;

import battlecode.common.*;

public class Archon{
	private static RobotController rc;

	static void run(RobotController rc){
		Archon.rc = rc;
		try{//Code here runs once.
			
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		while(true){
			try{//mainloop code here
				if(!BasicMove.tryMove(new Direction(0))){
					BasicMove.tryMove(new Direction((float)Math.PI));
				}
				Clock.yield();
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
}
