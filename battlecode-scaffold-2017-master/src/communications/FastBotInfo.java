package communications;
import battlecode.common.*;

public class FastBotInfo implements Packable {
	public static RobotController rc;
	public static RobotType[] robotTypeValues;
	public static void init(RobotController r){
		rc = r;
		robotTypeValues = RobotType.values();
	}
	
	float hp;
	int round, ID;
	RobotType rt;
	MapLocation loc;
	
	FastBotInfo(RobotInfo ri){
		hp = ri.health;
		round = rc.getRoundNum();
		ID = ri.ID;
		loc = ri.location;
		rt = ri.type;
	}
	FastBotInfo(int[] packedForm){
		unpack(packedForm);
	}

	@Override
	public int[] pack() {
		int[] packedForm = new int[2];
		//ID 0-32000, rt 0-7, hp 0-400
		packedForm[0] = ID+rt.ordinal()*32001+(int)hp*224007;
		//loc.x,loc.y 0-651; round 0-3000
		packedForm[1] = round + 3001*(int)loc.x + 1953651*(int)loc.y;
		return packedForm;
	}
	public static int[] pack(RobotInfo ri){
		return new FastBotInfo(ri).pack();
	}

	@Override
	public void unpack(int[] packedForm) {
		ID = packedForm[0]%32001;
		rt = robotTypeValues[(packedForm[0]%224007)/32001];
		hp = packedForm[0]/224007;
		
		round = packedForm[1]%3001;
		loc = new MapLocation((packedForm[1]%1953651)/3001, packedForm[1]/1953651);
	}


}
