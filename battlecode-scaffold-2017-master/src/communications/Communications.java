package communications;
import battlecode.common.*;

/**
 * a class to handle all broadcast communications,
 * send and recieve.
 * 
 * Dynamic channel allocation wut!!!1!
 * 
 * Stack based... so there's a top pointer in a static channel.
 *  
 * Fixed size chunks, say 10 spaces?
 * When a bot wants to dynamically allocate a channel,
 * read the top pointer. Top pointer starts at the top of the array, 
 * int newChunk(): decrements stack pointer by chunk size and returns that location as the start of the chunk.
 * void freeChunk(): 
 * To delete the top segment, increment the stack pointer by chunksize.
 * To delete any other segment: Move stack top to that segment. Update pointer to segment.
 * then increment pointer by ten.
 * 
 * For tracking bots or bullets, fixed size list in the fixed channels
 * first channel is number of items in the list
 * last channel is a pointer to an additional list.
 * 0 for null.  Otherwise the number will be in the high range of the channels, 
 * indicating the first index of the next chunk of the list.
 * The first index of the next chunk stores the calling address.
 * 
 * Overall entities will be somewhat limited in number,
 * but we don't know the proportion of each type in advance.
 * 
 * This way, we can globally track locations of arbirtrary numbers of whatever 
 * items we are tracking and communicating about.
 * 
 * We probably really need to store items by id... or with id at least.
 * We can pack a condensed RobotInfo into another int.
 * so four per 10 int chunk. Could go with 20 int chunks for less overhead.
 * in fact, we might want to optimize on chunk size, so keep it a variable.
 * don't let other classes assume a specific size.
 * 
 * iteminfo packets come in two ints.  
 * @author brian
 *
 */
public strictfp class Communications {
	/**
	 * Defines the useage for the various communication channels.
	 * 1000 channels available. 
	 * Channels are assigned in chunks of ten.
	 * the first MIN_DYNAMIC_POINTER-10 chunks are reserved for fixed channels
	 * the first few of any type, any team, and other globals.
	 * 
	 *  
	 * 
	 * @author brian
	 *
	 */
	public static class CHANNELS{
		
		//pointers to the first byte in a chunk of fixed memory.
		public static final int STACKDATA = 0;
		public static final int ENEMY_ARCHONS = 10;
		public static final int ENEMY_GARDENERS = 20;
		public static final int ENEMY_TANKS = 30;
		public static final int ENEMY_SPIES = 40;
		public static final int ENEMY_LUMBERJACKS = 50;
		public static final int ENEMY_SOLDIERS = 60;
		public static final int ENEMY_TREES = 70;
		
		public static final int EMPTY_NEUTRAL_TREES = 100;
		public static final int TREES_WITH_ROBOTS = 110;
		public static final int TREES_WITH_BULLETS = 120;
		
		public static final int MY_TREES = 200;
		public static final int MY_ARCHONS = 210;
		public static final int MY_GARDENERS = 220;
		public static final int MY_SOLDIERS = 230;
		public static final int MY_SPIES = 240;
		public static final int MY_TANKS = 250;
		public static final int MY_LUMBERJACKS = 260;
		
		//dynamic allocation constants
		public static final int FIXED_CHUNK_SIZE = 10;
		public static final int DYNAMIC_CHUNK_SIZE = 10;
		public static final int MIN_DYNAMIC_POINTER = 300;
		
	}
	public static class LocTime{
		public final int roundNumber;
		public final MapLocation location;
		
		public LocTime(int roundNumber, MapLocation location){
			this.roundNumber = roundNumber;
			this.location = location;
		}
	}
	
	public static RobotController rc;
	
	public static void init(RobotController robc){
		rc = robc;
	}
	
	public static int packLocation(MapLocation location){
		//coordinates vary from 0-600, as floats. (maps 30x30 to 100x100 and offsets 0-500
		//we can store x rounded to an int in the first 650
		//then y rounded to an int in the next 650
		return (int)(location.x+.5) + ((int)(location.y+.5))*650;
	}
	public static MapLocation unpackLocation(int packed){
		//inverse of the packing operation.
		return new MapLocation((float)packed%650,(float)((packed%422500)/650));
	}
	/**
	 * store x in the first 651
	 * then y will be stored * 651, so 423801 is the top of y
	 * 423801 * 3000 is only ~1.3 billion so it all just fits.
	 * 
	 * 
	 * @param location
	 * @param roundNumber
	 * @return integer encoding of roundNumber and location
	 */
	public static int packTimeLoc(MapLocation location, int roundNumber){
		return (int)(roundNumber)*423801 + packLocation(location);
	}
	public static LocTime unPackTimeLoc(int packed){
		return new LocTime(packed/423801, unpackLocation(packed));
	}
	/**
	 * Allocates a chunk on the dynamic memory stack.
	 * chunks must have a parent pointer associated. The parent
	 * will be updated if the chunk moves due to a restructuring operation.
	 * No gaurantees to any pointers held outside of this parent being updated.
	 * but if the memory is moved or freed, the parent will be updated with the new location
	 * or a null (0) if the memory is freed.
	 * 
	 * If we don't have any chunks to allocate, return 0 (null), otherwise the index of the first
	 * int of the chunk.
	 * 
	 * parent is stored in position zero of any dynamically allocated chunk.
	 * 
	 * @param parent the definitive ptr location for the new node.
	 * @return pointer to new chunk, or 0 for null (no memory available)
	 */
	public static int allocateChunk(int parent) throws GameActionException{
		int stackptr = rc.readBroadcast(CHANNELS.STACKDATA);
		stackptr -= CHANNELS.DYNAMIC_CHUNK_SIZE;
		if (stackptr<CHANNELS.MIN_DYNAMIC_POINTER){return 0;}
		rc.broadcast(CHANNELS.STACKDATA,stackptr);
		rc.broadcast(parent, stackptr);
		rc.broadcast(stackptr, parent);
		return stackptr;
	}
	
	/**
	 * Deallocates the chunk at ptr.
	 * Updates the parent of that chunk to show a null.
	 * There is a risk of memory leaks, if this chunk had a child
	 * and there are no other pointers to the child locations.
	 * 
	 * If any pointers other than parent existed, they might get broken now. 
	 * 
	 * Also, this had better be a ptr to the beginning of
	 * a proper, previously allocated chunk,
	 * or else shit gets corrupted.
	 * 
	 * If the freed chunk was not the head of the stack, takes
	 * DYNAMIC_CHUNK_SIZE*2 extra read/write operations to copy the head to the new empty
	 * spot.
	 * 
	 * @param ptr
	 * @throws GameActionException
	 */
	public static void freeChunk(int ptr) throws GameActionException{
		int parent = rc.readBroadcast(ptr); 
		int stackptr = rc.readBroadcast(CHANNELS.STACKDATA);
		rc.broadcast(parent, 0); //null the parent reference
		if(ptr != stackptr){ //if we're deleting something other than the head of the stack
			//we have to copy the head of the stack into it's place
			//and tell the copied chunk's parent the new location
			int topParent = rc.readBroadcast(stackptr);
			rc.broadcast(topParent, ptr);
			for(int i = 0; i < CHANNELS.DYNAMIC_CHUNK_SIZE; i++){
				rc.broadcast(ptr+i, rc.readBroadcast(stackptr+i));
			}
		}
		//then we can just move the stack pointer
		stackptr += CHANNELS.DYNAMIC_CHUNK_SIZE;
		rc.broadcast(CHANNELS.STACKDATA, stackptr);
			
	}
	
	public static void freeChunkAndChildren(int ptr) throws GameActionException{
		int child = rc.readBroadcast(ptr+CHANNELS.DYNAMIC_CHUNK_SIZE-1);
		int parent=ptr;
		int stackptr = rc.readBroadcast(CHANNELS.STACKDATA);
		while (child != 0){
			parent = child;
			child = rc.readBroadcast(parent+CHANNELS.DYNAMIC_CHUNK_SIZE-1);
		}
		while(parent != ptr){
			child = parent;
			parent = rc.readBroadcast(child);
			if(child != stackptr){
				int stackParent = rc.readBroadcast(stackptr);
				rc.broadcast(stackParent, child);
				for(int i = 0; i < CHANNELS.DYNAMIC_CHUNK_SIZE; i++){
					rc.broadcast(child+i, rc.readBroadcast(stackptr+i));
				}
			}
			stackptr += CHANNELS.DYNAMIC_CHUNK_SIZE;
		}
		freeChunk(ptr);
	}
}
