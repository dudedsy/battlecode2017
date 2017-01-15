package vectormath;
import java.lang.Math;
import battlecode.common.*;

public class LineMath{

	
	
	
	/**
	 * vector dot product.
	 * length of A projected on B.
	 * 
	 * @param A
	 * @param B
	 * 
	 * @return A dot B
	 */
	public static final float dot(Line A, Line B){
		return A.length*((float)Math.cos(A.dir.radiansBetween(B.dir)));
	}
	
	public static final float dot(Vector A, Vector B){
		return A.dx*B.dx+A.dy*B.dy;
	}
	
	/**
	 * perpendicular distance
	 * distance to location from the line
	 * 
	 * 
	 * @param loc: the location
	 * @param B: the line
	 * @return: the distance
	 */
	static final float perpDist(MapLocation loc, Line B){
		Line startToLoc = new Line(B.start,loc);
		return startToLoc.length*(float)Math.sin(B.dir.radiansBetween(startToLoc.dir));
	}
	static final float perpDist(MapLocation loc, Vector B, MapLocation start){
		B = new UnitVector(B);
		return perpDist(loc,B,start);
	}
	static final float perpDist(MapLocation loc, UnitVector B, MapLocation start){
		return B.dy*(loc.x-start.x)-B.dx*(loc.y-start.y);
	}
	
	public static class Line{
		public float length;
		public Direction dir;
		public MapLocation start,finish;
		
		public Line(float length, Direction direction){
			this.start = new MapLocation(0,0);
			this.length = length;
			this.dir = direction;
			this.finish = start.add(direction, length);
		}
		public Line(float length, Direction direction, MapLocation start){
			this.start = start;
			this.dir = direction;
			this.length = length;
			this.finish = start.add(direction, length);
		}
		public Line(MapLocation start, MapLocation finish){
			this.start = start;
			this.finish = finish;
			this.dir = new Direction(start,finish);
			this.length = start.distanceTo(finish);
		}
		public Line(Line A){
			start = A.start;
			finish = A.finish;
			dir = A.dir;
			length = A.length;
		}
	}
	public static class Vector {
		public float dx,dy;
		
		Vector(){}
		
		public Vector(Line A){
			if(A.finish == null){}
			dx = (A.finish.x-A.start.x)/A.length;
			dy = (A.finish.y-A.start.y)/A.length;	
		}
		public Vector(float dx, float dy){
			this.dx = dx;
			this.dy = dy;
		}
		public Vector(MapLocation start, MapLocation finish){
			dx = finish.x-start.x;
			dy = finish.y-start.y;
		}
		public Vector scalarMult(float scalar){
			return new Vector(this.dx*scalar,this.dy*scalar);
		}
	}
	public static class UnitVector extends Vector{
		public static UnitVector perpUnit(UnitVector v){
			UnitVector retval = new UnitVector();
			retval.dx = v.dy;
			retval.dy = -v.dx;
			return retval;
		}
		
		public UnitVector(Line A){
			super((A.finish.x-A.start.x)/A.length,
					(A.finish.y-A.start.y)/A.length);
		}
		public UnitVector(Vector A){
			float length = (float)Math.sqrt(A.dx*A.dx+A.dy*A.dy);
			dx = A.dx/length;
			dy = A.dy/length;
		}
		public UnitVector(float dx, float dy){
			Direction dir = new Direction(dx,dy);
			this.dx = dir.getDeltaX(1);
			this.dy = dir.getDeltaY(1);
		}
		public UnitVector(Direction dir){
			dx = dir.getDeltaX(1);
			dy = dir.getDeltaY(1);
		}
		private UnitVector(){
		}
	}
}
