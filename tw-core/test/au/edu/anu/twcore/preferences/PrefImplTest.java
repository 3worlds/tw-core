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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PrefImplTest {

	@Test
	public void test() {
		PrefImpl prefs = new PrefImpl(this);
		String key1 = "Test1";
		String key2 = "Test2";
		String key3 = "Test3";
		String key4 = "Test4";
		String key5 = "Test5";
		String key6 = "Test6";
		String key7 = "Test7";
		String key8 = "Test8";
		String key9 = "Test9";
		String key10 = "Test10";
		int[] defInts = {0,0,0,0,0};
		long[] defLongs = {0,0,0,0,0};
		boolean[] defBooleans = {false,false,false,false};
		float[] defFloats = {0,0,0,0,0};
		String[] defStrings = {"s","s","s","s","s"};

		prefs.putInt(key1, 0);
		prefs.putInts(key2, defInts);
		prefs.putLong(key3, 0);
		prefs.putLongs(key4, defLongs);
		prefs.putBoolean(key5, false);
		prefs.putBooleans(key6, defBooleans);
		prefs.putFloat(key7, 0);
		prefs.putFloats(key8, defFloats);
		prefs.putString(key9, "s");
		prefs.putStrings(key10, defStrings);

		assertTrue(prefs.getInt(key1, 0) == 0);
		assertTrue(prefs.getInts(key2, defInts)[0] == 0);
		assertTrue(prefs.getLong(key3, 0) == 0L);
		assertTrue(prefs.getLongs(key4, defLongs)[0] == 0L);
		assertTrue(prefs.getBoolean(key5, false) == false);
		assertTrue(prefs.getBooleans(key6, defBooleans)[0] == false);
		assertTrue(prefs.getFloat(key7, 0) == 0.0f);
		assertTrue(prefs.getFloats(key8, defFloats)[0] == 0.0f);
		assertTrue(prefs.getString(key9, "s") == "s");
		assertTrue(prefs.getStrings(key10, defStrings)[0] == "s");

		prefs.remove(key1);
		prefs.remove(key2);
		prefs.remove(key3);
		prefs.remove(key4);
		prefs.remove(key5);
		prefs.remove(key6);
		prefs.remove(key7);
		prefs.remove(key8);
		prefs.remove(key9);
		prefs.remove(key10);

	}

}
