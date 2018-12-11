/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/

package au.edu.anu.twcore.preferences;

/**
 * Author Ian Davies
 *
 * Date Dec 11, 2018
 */

/**
 * Java has a preferences API {@link java.util.prefs.Preferences} . Therefore, I
 * hope we no longer need the graph-dependent implementation for preference saving.
 * However, the Java system only deals with primitives and so this code is an
 * interface to a preference helper class for 3Worlds
 */

public interface Preferable {
	public void putInt(String key, int value);
	public void putInts(String key, int... values);
	public void putLong(String key, long value);
	public void putLongs(String key, long... values);
	public void putBoolean(String key, boolean value);
	public void putBooleans(String key, boolean... values);
	public void putFloat(String key, float value);
	public void putFloats(String key, float... values);
	public void putString(String key, String value);
	public void putStrings(String key, String... values);

	public int getInt(String key,int def);
	public int[] getInts(String key, int... defs);
	public long getLong(String key,long def);
	public long[] getLongs(String key, long... defs);
	public boolean getBoolean(String key,boolean def);
	public boolean[] getBooleans(String key, boolean... defs);
	public float getFloat(String key,float def);
	public float[] getFloats(String key, float... defs);
	public String getString(String key,String def);
	public String[] getStrings(String key, String... defs);
	
	public void remove(String key);
	public void flush();
	public boolean isEmpty();

}
