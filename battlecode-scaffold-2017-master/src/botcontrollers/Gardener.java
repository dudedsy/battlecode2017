package botcontrollers;

import battlecode.common.*;

public class Gardener{
	private static RobotController rc;
	
	public static void run(RobotController rc){
		Gardener.rc = rc;
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

