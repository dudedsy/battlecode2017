package botcontrollers;

import battlecode.common.RobotController;

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
				
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
}