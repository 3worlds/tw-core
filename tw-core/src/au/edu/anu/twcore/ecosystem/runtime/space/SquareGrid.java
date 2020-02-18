package au.edu.anu.twcore.ecosystem.runtime.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTrackerSpace;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.twcore.constants.EdgeEffects;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.generic.utils.Duple;

/**
 * A spatial representation of a rectangular 2D grid with square cells
 * Assumes the origin of the grid is (0,0).
 * 
 * @author Jacques Gignoux - 30 janv. 2020
 *
 */
//todo: toroidal correction
public class SquareGrid extends SpaceAdapter<SystemComponent> {
	
	private static final int ndim = 2;

	private class squareGridLocation implements Location {
		protected int[] loc = new int[2];
		private Point ploc = null;
		protected squareGridLocation(Located lc,double...xyloc) {
			super();
			loc[0] = (int) Math.floor(xyloc[0]/cellSize);
			loc[1] = (int) Math.floor(xyloc[1]/cellSize);
			if ((loc[0]>nx)|(loc[1]>ny))
				throw new TwcoreException("New spatial coordinates for item "
					+lc.toString()+" out of range "+boundingBox().toString());
			double x = loc[0]*cellSize;
			double y = loc[1]*cellSize;
			ploc = Point.newPoint(x,y);
		}
		@Override
		public Point asPoint() {
			return ploc;
		}
		@Override
		public String toString() {
			return "["+loc[0]+","+loc[1]+"]";
		}
	}
	
	private int nx = 0;
	private int ny = 0;
	private double cellSize = 0.0;
	
	private Map<SystemComponent,Location> locatedItems = new HashMap<>();
	private List<SystemComponent> grid[][];
	// precomputed inverted table of distances vs table indices
	private SortedMap<Long,List<Duple<Integer,Integer>>> distanceMap = new TreeMap<>();
	
	@SuppressWarnings("unchecked")
	public SquareGrid(double cellSize, int nx, int ny, double prec, String units, 
			EdgeEffects ee, DataTrackerSpace dt) {
		super(Box.boundingBox(Point.newPoint(0.0,0.0),Point.newPoint(nx*cellSize,ny*cellSize)),
			prec, units, ee, dt);
		this.cellSize = cellSize;
		this.nx = nx;
		this.ny = ny;
		grid = new ArrayList[nx+1][ny+1];
		for (int i=0; i<grid.length; i++)
			for (int j=0; j<grid.length; j++)
				grid[i][j] = new ArrayList<SystemComponent>();
		// build a map of all cell index DIFFERENCES that are at the same distance
		// may become a problem for big maps?
		for (int di=0; di<nx+1; di++)
			for (int dj=0; dj<ny+1; dj++) {
				long dist = squareDist(di,dj);
				Duple<Integer,Integer> dup =new Duple<>(di,dj);
				if (!distanceMap.containsKey(dist))
					distanceMap.put(dist,new ArrayList<Duple<Integer,Integer>>());
				distanceMap.get(dist).add(dup);
		}
	}

	private long squareDist(long difx, long dify) {
		return difx*difx+dify*dify;
	}

	@Override
	public int ndim() {
		return ndim;
	}

	@Override
	public Graph<? extends Node, ? extends Edge> asGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void locate(SystemComponent focal, double...xyloc) {
		squareGridLocation at = new squareGridLocation(focal,xyloc);
		locatedItems.put(focal,at);
		grid[at.loc[0]][at.loc[1]].add(focal);
	}

	@Override
	public void locate(SystemComponent focal, Point location) {
		locate(focal,location.x(),location.y());		
	}

	@Override
	public void unlocate(SystemComponent focal) {
		squareGridLocation at = (squareGridLocation) locatedItems.get(focal);
		grid[at.loc[0]][at.loc[1]].remove(focal);
	}

	@Override
	public Iterable<SystemComponent> getNearestItems(SystemComponent item) {
		squareGridLocation refloc = (squareGridLocation) locatedItems.get(item);
		List<SystemComponent> result = new ArrayList<>(); 
		result.addAll(grid[refloc.loc[0]][refloc.loc[1]]);
		result.remove(item);
		Iterator<List<Duple<Integer,Integer>>> it = distanceMap.values().iterator();
		while (result.isEmpty() && it.hasNext()) {
			List<Duple<Integer,Integer>> l = it.next();
			getItemsAtSameDistance(refloc,l,result);
		}
		return result;
	}
		
	private void getItemsAtSameDistance(squareGridLocation refloc, 
			List<Duple<Integer,Integer>> l,
			List<SystemComponent> result) {
		for (Duple<Integer,Integer> dup:l) {
			int di = dup.getFirst();
			int dj = dup.getSecond();
			int i = refloc.loc[0]+di;
			int j = refloc.loc[1]+dj;
			if ((i>=0)&(i<nx+1)&(j>=0)&(j<ny+1))
				result.addAll(grid[i][j]);
			i = refloc.loc[0]-di;
			if ((i>=0)&(i<nx+1)&(j>=0)&(j<ny+1))
				result.addAll(grid[i][j]);
			j = refloc.loc[1]-dj;
			if ((i>=0)&(i<nx+1)&(j>=0)&(j<ny+1))
				result.addAll(grid[i][j]);
			i = refloc.loc[0]+di;
			if ((i>=0)&(i<nx+1)&(j>=0)&(j<ny+1))
				result.addAll(grid[i][j]);
		}
	}

	@Override
	public Iterable<SystemComponent> getItemsWithin(SystemComponent item, double distance) {
		squareGridLocation refloc = (squareGridLocation) locatedItems.get(item);
		List<SystemComponent> result = new ArrayList<>(); 
		long sqDist = (long) Math.floor((distance/cellSize)*(distance/cellSize));		
		for(long sqd:distanceMap.keySet()) {
			if (sqd<=sqDist)
				getItemsAtSameDistance(refloc,distanceMap.get(sqd),result);
			else 
				break;
			if (sqd==0L)
				result.remove(item);
		}
		return result;
	}

	@Override
	public Point locationOf(SystemComponent focal) {
		if (locatedItems.get(focal)!=null)
			return locatedItems.get(focal).asPoint();
		else
			return null;
	}

	@Override
	public void unlocate(Collection<SystemComponent> items) {
		for (SystemComponent sc:items) {
			squareGridLocation loc = (squareGridLocation) locatedItems.get(sc);
			grid[loc.loc[0]][loc.loc[1]].remove(sc);
		}
		locatedItems.keySet().removeAll(items);
	}

}
