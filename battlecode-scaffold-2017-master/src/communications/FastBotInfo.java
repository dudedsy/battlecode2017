package communications;
import battlecode.common.*;

public class FastBotInfo implements Packable {
	public static final RobotType[] robotTypeValues = RobotType.values();
	
	public double health; //
	public int round;
	public int ID;
	public RobotType rt;
	public MapLocation loc;
	
	public FastBotInfo(RobotInfo ri,int roundNum){
		health = ri.health;
		round = roundNum;
		ID = ri.ID;
		loc = ri.location;
		rt = ri.type;
	}
	public FastBotInfo(RobotController rc){
		health = rc.getHealth();
		round = rc.getRoundNum();
		ID = rc.getID();
		loc = rc.getLocation();
		rt = rc.getType();
	}
	FastBotInfo(int[] packedForm){
		unpack(packedForm);
	}
	

	@Override
	public int[] pack() {
		int[] packedForm = new int[2];
		//ID 0-32000, rt 0-7, hp 0-400
		packedForm[0] = ID+rt.ordinal()*32001+(int)health*224007;
		//loc.x,loc.y 0-651; round 0-3000
		packedForm[1] = round + 3001*(int)loc.x + 1953651*(int)loc.y;
		return packedForm;
	}
	public static int[] pack(RobotInfo ri,int round){
		return new FastBotInfo(ri,round).pack();
	}
	public static int[] pack(int round,int ID,float health,RobotType rt, MapLocation loc){
		int[] packedForm = new int[2];
		//ID 0-32000, rt 0-7, hp 0-400
		packedForm[0] = ID+rt.ordinal()*32001+(int)health*224007;
		//loc.x,loc.y 0-651; round 0-3000
		packedForm[1] = round + 3001*(int)loc.x + 1953651*(int)loc.y;
		return packedForm;
	}

	@Override
	public void unpack(int[] packedForm) {
		ID = packedForm[0]%32001;
		rt = robotTypeValues[(packedForm[0]%224007)/32001];
		health = packedForm[0]/224007;
		
		round = packedForm[1]%3001;
		loc = new MapLocation((packedForm[1]%1953651)/3001, packedForm[1]/1953651);
	}


}
