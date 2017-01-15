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
public strictfp class Comms {
	//Constants
	//pointers to the first byte in a chunk of fixed memory.
	public static final int STACKPTR = 0; //one int for the dynamic channel stack head pointer
	public static final int CORNERS = 1; //two channels for the corners/edges. x1,y1,x2,y2 
	public static final int ENEMY_ARCHONS = 20;
	public static final int ENEMY_GARDENERS = 40;
	public static final int ENEMY_TANKS = 60;
	public static final int ENEMY_SPIES = 80;
	public static final int ENEMY_LUMBERJACKS = 100;
	public static final int ENEMY_SOLDIERS = 120;
	public static final int ENEMY_TREES = 140;
	
	public static final int EMPTY_NEUTRAL_TREES = 160;
	public static final int TREES_WITH_ROBOTS = 180;
	public static final int TREES_WITH_BULLETS = 200;
	
	public static final int MY_TREES = 220;
	public static final int MY_ARCHONS = 240;
	public static final int MY_GARDENERS = 260;
	public static final int MY_SOLDIERS = 280;
	public static final int MY_SPIES = 300;
	public static final int MY_TANKS = 320;
	public static final int MY_LUMBERJACKS = 340;
	
	//dynamic allocation constants
	public static final int CHUNKSIZE = 20;
	public static final int MIN_DYNAMIC_POINTER = 360;
	
	//initialize
	public static RobotController rc;
	public static void init(RobotController robc){
		rc = robc;
	}
	
	public static int listLength(int channel) throws GameActionException{
		return rc.readBroadcast(channel);
	}
	public static boolean listAdd(int channel, Packable item) throws GameActionException{
		return listAdd(channel,item.pack());
	}
	public static boolean listAdd(int channel, int[] packedItem) throws GameActionException{
		return listAdd(channel,packedItem,listLength(channel));
	}
	public static boolean listAdd(int channel, int[] packedItem, int prevLength) throws GameActionException{
		int itemLength = packedItem.length;
		int start = prevLength*itemLength + 1;
		int ptrloc;
		rc.broadcast(channel, ++prevLength);
		while(start >= CHUNKSIZE-1){
			ptrloc = channel+CHUNKSIZE-1;
			channel = rc.readBroadcast(ptrloc);
			if(channel == 0){
				channel = allocateChunk(ptrloc);
				if(channel == 0){return false;}	
			}
			start -= CHUNKSIZE-2;
		}
		for(int i = 0; i < itemLength; i++){
			rc.broadcast(channel+i+1, packedItem[i]);
		}
		return true;
	}
	/**
	 * get the ith packed item from the list starting at channel
	 * 
	 * @param channel - the starting point of the list (in fixed memory)
	 * @param itemLength - the length of the packed items in the list.
	 * @return the packed item!
	 */
	public static int[] getPackedItem(int channel, int index, int itemLength) 
			throws GameActionException, IndexOutOfBoundsException{
		
		int[] packedItem = new int[itemLength];
		int start = index*itemLength + 1;
		while(start >= CHUNKSIZE-1){
			channel = rc.readBroadcast(channel+CHUNKSIZE-1);
			if(channel == 0){throw new IndexOutOfBoundsException("Didn't find the expected pointer!");}
			start -= CHUNKSIZE-2;
		}
		for(int i = 0; i < itemLength; i++){
			packedItem[i]=rc.readBroadcast(channel+i+1);
		}
		
		return packedItem;
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
		int stackptr = rc.readBroadcast(STACKPTR);
		stackptr -= CHUNKSIZE;
		if (stackptr<MIN_DYNAMIC_POINTER){return 0;}
		rc.broadcast(STACKPTR,stackptr);
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
		int stackptr = rc.readBroadcast(STACKPTR);
		rc.broadcast(parent, 0); //null the parent reference
		if(ptr != stackptr){ //if we're deleting something other than the head of the stack
			//we have to copy the head of the stack into it's place
			//and tell the copied chunk's parent the new location
			int topParent = rc.readBroadcast(stackptr);
			rc.broadcast(topParent, ptr);
			for(int i = 0; i < CHUNKSIZE; i++){
				rc.broadcast(ptr+i, rc.readBroadcast(stackptr+i));
			}
		}
		//then we can just move the stack pointer
		stackptr += CHUNKSIZE;
		rc.broadcast(STACKPTR, stackptr);
			
	}
	
	/**
	 * Frees the chunk at ptr, as well as any children chunks (and their children, etc)
	 * 
	 * See freeChunk for warnings and caveats.
	 * 
	 * This doesn't risk leaving children hanging,
	 * but it does risk fucking a lot of shit up if it encounters a corrupted chunk.
	 * 
	 * @param ptr
	 * @throws GameActionException
	 */
	public static void freeChunkAndChildren(int ptr) throws GameActionException{
		int child = rc.readBroadcast(ptr+CHUNKSIZE-1);
		int parent=ptr;
		int stackptr = rc.readBroadcast(STACKPTR);
		int stackParent;
		while (child != 0){
			parent = child;
			child = rc.readBroadcast(parent+CHUNKSIZE-1);
		}
		while(parent != ptr){
			child = parent;
			parent = rc.readBroadcast(child);
			if(child != stackptr){
				stackParent = rc.readBroadcast(stackptr);
				rc.broadcast(stackParent, child);
				for(int i = 0; i < CHUNKSIZE; i++){
					rc.broadcast(child+i, rc.readBroadcast(stackptr+i));
				}
			}
			stackptr += CHUNKSIZE;
		}
		freeChunk(ptr);
	}
}
