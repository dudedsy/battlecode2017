package botcontrollers;

import battlecode.common.RobotController;
import movement.BulletDodge;
import movement.Move;

public class Soldier {
private static RobotController rc;
	
public static void run(RobotController rc){
		Soldier.rc = rc;
		try{//Code here runs once.
			
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		while(true){
			try{//mainloop code here
				BulletDodge.dodge();
				Move.tryMove(Move.randomDirection());
				Donations.ifReady();
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
}