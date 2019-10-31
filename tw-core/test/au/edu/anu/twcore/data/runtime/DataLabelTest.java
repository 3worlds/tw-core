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
		System.out.println(dl);
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
		for (DataLabel d:list)
			System.out.println(d);
		assertEquals(list.size(),16);
		dl = DataLabel.valueOf("ref");
		map.clear();
		map.put("ref",dim1);
		list = IndexedDataLabel.expandIndexes(dl,map);
		for (DataLabel d:list)
			System.out.println(d);
		assertEquals(list.size(),4);
		map.clear();
		list = IndexedDataLabel.expandIndexes(dl,null);
		for (DataLabel d:list)
			System.out.println(d);
		assertEquals(list.size(),1);
	}

}
