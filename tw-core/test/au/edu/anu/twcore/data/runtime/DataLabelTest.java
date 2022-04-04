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
package au.edu.anu.twcore.data.runtime;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author Jacques Gignoux - 29 oct. 2019
 *
 */
class DataLabelTest {

	@Test
	final void testValueOf() {
		DataLabel dl = DataLabel.valueOf("x>tab[1:2]>y>gru[3|]");
//		System.out.println(dl);
		assertEquals(dl.toString(),"x>tab[1:2]>y>gru[3|]");
	}

	@Test
	final void testExpandIndexes() {
		DataLabel dl = DataLabel.valueOf("x>tab[1:2]>y>gru[3|]");
		int[] dim1 = {4};
		int[] dim2 =  {5,8};
		Map<String,int[]> map = new HashMap<>();
		map.put("tab",dim1);
		map.put("gru",dim2);
		List<IndexedDataLabel> list = IndexedDataLabel.expandIndexes(dl,map);
//		for (DataLabel d:list)
//			System.out.println(d);
		assertEquals(list.size(),16);
		dl = DataLabel.valueOf("ref");
		map.clear();
		map.put("ref",dim1);
		list = IndexedDataLabel.expandIndexes(dl,map);
//		for (DataLabel d:list)
//			System.out.println(d);
		assertEquals(list.size(),4);
		map.clear();
		list = IndexedDataLabel.expandIndexes(dl,null);
//		for (DataLabel d:list)
//			System.out.println(d);
		assertEquals(list.size(),1);
	}

}
