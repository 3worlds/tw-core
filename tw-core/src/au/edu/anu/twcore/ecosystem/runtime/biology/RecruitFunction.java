package au.edu.anu.twcore.ecosystem.runtime.biology;

/**
 * 
 * @author Jacques Gignoux - 7 janv. 2021
 *
 */
public interface RecruitFunction {

	public String transition(int i);

	public String transition(boolean change);

}
