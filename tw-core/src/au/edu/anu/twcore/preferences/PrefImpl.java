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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Author Ian Davies
 *
 * Date Dec 11, 2018
 */
public class PrefImpl implements Preferable {
	private Preferences prefs;

	public PrefImpl(Object item) {
		this.prefs = Preferences.userRoot().node(item.getClass().getName());
		System.out.println(prefs.absolutePath().toString());
	}

	private String keyAppend(String key, int i) {
		return key + "_" + i;
	}

	@Override
	public void putInt(String key, int value) {
		prefs.putInt(key, value);
	}

	@Override
	public void putInts(String key, int... values) {
		for (int i = 0; i < values.length; i++)
			prefs.putInt(keyAppend(key, i), values[i]);
	}

	@Override
	public void putLong(String key, long value) {
		prefs.putLong(key, value);

	}

	@Override
	public void putLongs(String key, long... values) {
		for (int i = 0; i < values.length; i++)
			prefs.putLong(keyAppend(key, i), values[i]);
	}

	@Override
	public void putBoolean(String key, boolean value) {
		prefs.putBoolean(key, value);
	}

	@Override
	public void putBooleans(String key, boolean... values) {
		for (int i = 0; i < values.length; i++)
			prefs.putBoolean(keyAppend(key, i), values[i]);
	}

	@Override
	public void putFloat(String key, float value) {
		prefs.putFloat(key, value);
	}

	@Override
	public void putFloats(String key, float... values) {
		for (int i = 0; i < values.length; i++)
			prefs.putFloat(keyAppend(key, i), values[i]);
	}

	@Override
	public void putString(String key, String value) {
		prefs.put(key, value);
	}

	@Override
	public void putStrings(String key, String... values) {
		for (int i = 0; i < values.length; i++)
			prefs.put(keyAppend(key, i), values[i]);
	}

	@Override
	public int getInt(String key, int def) {
		return prefs.getInt(key, def);
	}

	@Override
	public int[] getInts(String key, int... defs) {
		int[] res = new int[defs.length];
		for (int i = 0; i < defs.length; i++)
			res[i] = prefs.getInt(keyAppend(key, i), defs[i]);
		return res;
	}

	@Override
	public long getLong(String key, long def) {
		return prefs.getLong(key, def);
	}

	@Override
	public long[] getLongs(String key, long... defs) {
		long[] res = new long[defs.length];
		for (int i = 0; i < defs.length; i++)
			res[i] = prefs.getLong(keyAppend(key, i), defs[i]);
		return res;
	}

	@Override
	public boolean getBoolean(String key, boolean def) {
		return prefs.getBoolean(key, def);
	}

	@Override
	public boolean[] getBooleans(String key, boolean... defs) {
		boolean[] res = new boolean[defs.length];
		for (int i = 0; i < defs.length; i++)
			res[i] = prefs.getBoolean(keyAppend(key, i), defs[i]);
		return res;
	}

	@Override
	public float getFloat(String key, float def) {
		return prefs.getFloat(key, def);
	}

	@Override
	public float[] getFloats(String key, float... defs) {
		float[] res = new float[defs.length];
		for (int i = 0; i < defs.length; i++)
			res[i] = prefs.getFloat(keyAppend(key, i), defs[i]);
		return res;
	}

	@Override
	public String getString(String key, String def) {
		return prefs.get(key, def);
	}

	@Override
	public String[] getStrings(String key, String... defs) {
		String[] res = new String[defs.length];
		for (int i = 0; i < defs.length; i++)
			res[i] = prefs.get(keyAppend(key, i), defs[i]);
		return res;
	}

	@Override
	public void remove(String key) {
		//if (prefs.keys())
		prefs.remove(key);		
	}

	@Override
	public void flush() throws BackingStoreException {
		prefs.flush();
	}

}
