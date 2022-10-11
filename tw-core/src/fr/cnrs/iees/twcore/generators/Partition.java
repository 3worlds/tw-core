package fr.cnrs.iees.twcore.generators;

import java.util.EnumSet;

/**
 * Interface needed to generate a category Enum class
 * 
 * @author Jacques Gignoux - 7 oct. 2022
 *
 * @param <T>
 */
public interface Partition<T extends Enum<T>> {
	
	public EnumSet<T> partition();

}
