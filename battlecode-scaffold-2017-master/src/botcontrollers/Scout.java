package botcontrollers;

import battlecode.common.RobotController;
import movement.BulletDodge;
import movement.Move;

public class Scout {
	private static RobotController rc;

	public static void run(RobotController rc){
		Scout.rc =rc;
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
