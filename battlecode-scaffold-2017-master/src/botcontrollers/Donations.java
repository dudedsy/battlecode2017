package botcontrollers;

import battlecode.common.*;

public class Donations {
	public static RobotController rc;
	public static float MINTODONATE;
	public static int DONATIONAMT;
	
	public static void init(RobotController rc){
		Donations.rc = rc;
		MINTODONATE = 300;
		DONATIONAMT = 30;
	}
	
	public static boolean ifReady() throws GameActionException{
		if(rc.getTeamBullets()>MINTODONATE){
			rc.donate(DONATIONAMT);
			return true;
		}else{return false;}
	}
	public static boolean ifReady(float amt, float thresh) throws GameActionException{
		if(rc.getTeamBullets()>thresh){
			rc.donate(amt);
			return true;
		}else{return false;}
	}
}
