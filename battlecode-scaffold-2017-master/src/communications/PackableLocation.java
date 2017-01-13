package communications;
import battlecode.common.*;

public class PackableLocation implements Packable {
	MapLocation loc;
	
	public PackableLocation(MapLocation location){
		loc = location;
	}
	public PackableLocation(int[] packedForm){
		this.unpack(packedForm);
	}
	public PackableLocation(int packedForm){
		int[] listForm = {packedForm};
		this.unpack(listForm);
	}
	@Override
	public int[] pack() {
		int[] retval = {(int)loc.y*650+(int)loc.x};
		return retval;
	}

	@Override
	public void unpack(int[] packedForm) {
		
	}

}
