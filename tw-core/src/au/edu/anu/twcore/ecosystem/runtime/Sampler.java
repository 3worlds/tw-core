package au.edu.anu.twcore.ecosystem.runtime;

/**
 * Interface for objects which track a sample from another population
 * 
 * @author Jacques Gignoux - 14 oct. 2020
 *
 * @param <T>
 */
public interface Sampler<T> {

	public void updateSample();

	public void removeFromSample(T wasTracked);
	
	public void addToSample(T toTrack);
	
	public boolean isTracked(T item);

}
