package communications;

/**
 * 
 * The Packable interface is used for objects which can be densely packed into 
 * one or two ints for broadcast. 
 * 
 * Usually implemented by reduced size BodyInfo analogs.
 * 
 * @author Brian
 *
 */
public interface Packable{
	/**
	 * packs the object into some small number of ints
	 * @return packed form as a list of ints
	 */
	public int[] pack();	
	/**
	 * unpacks the given int into this object. Error or bad data
	 * if the int was not a packed form of the calling object type.
	 * @param packedForm
	 */
	public void unpack(int[] packedForm);
}
