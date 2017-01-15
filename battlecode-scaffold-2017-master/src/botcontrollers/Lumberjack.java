package botcontrollers;
import battlecode.common.*;
import movement.Move;

public class Lumberjack {
	private static RobotController rc;

	public static void run(RobotController rc){
		Lumberjack.rc =rc;
		try{//Code here runs once.
			
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		while(true){
			try{//mainloop code here
				Move.tryMove(Move.randomDirection());
				Donations.ifReady();
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
}
