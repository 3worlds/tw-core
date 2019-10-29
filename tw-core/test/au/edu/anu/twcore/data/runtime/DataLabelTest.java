package au.edu.anu.twcore.data.runtime;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
		List<DataLabel> list = DataLabel.expandIndexes(dl,dim1,dim2);
		for (DataLabel d:list)
			System.out.println(d);
		assertEquals(list.size(),16);
		dl = DataLabel.valueOf("ref");
		list = DataLabel.expandIndexes(dl);
		for (DataLabel d:list)
			System.out.println(d);
		assertEquals(list.size(),1);		
	}

}
