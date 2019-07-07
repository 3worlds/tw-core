package au.edu.anu.twcore.ecosystem.runtime.system;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.CategorySet;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.LocalScope;

/**
 * 
 * @author Jacques Gignoux - 1 juil. 2019
 *
 */
class CategorizedContainerTest {
	
	// a little test class for Categorized
	private class categorizedAdapter implements Categorized<Identity> {
		private SortedSet<Category> categories = new TreeSet<>();
		private String categoryId = null;
		private categorizedAdapter(Collection<Category> cats) {
			super();
			categories.addAll(cats);
			categoryId = buildCategorySignature();
		}
		@Override
		public Set<Category> categories() {
			return categories;
		}
		@Override
		public String categoryId() {
			return categoryId;
		}
	}
	// a little test class for CategorizedContainer<Identity>
	private class icontainer extends CategorizedContainer<Identity> {
		private icontainer(Categorized<Identity> cats, String proposedId, 
				CategorizedContainer<Identity> parent) {
			super(cats,proposedId,parent,null,null);
		}
		@Override
		public Identity newInstance() {
			return scope().newId();
		}
		@Override
		public Identity clone(Identity item) {
			Identity id = scope().newId(item.id());
			return id;
		}
	}
	
	private Category c1, c2, c3, c4, c5, c6;
	private CategorySet cs1, cs2, cs3;
	private categorizedAdapter ca, ca2;
	private CategorizedContainer<Identity> cc, cc2;
	private IdentityScope scope;
	
	@BeforeEach
	private void init() {
		TreeGraphFactory f = new TreeGraphFactory();
		cs1 = (CategorySet) f.makeNode(CategorySet.class, "Mammals");
		c1 = (Category) f.makeNode(Category.class, "Whale");
		c1.connectParent(cs1);
		c2 = (Category) f.makeNode(Category.class, "Mouse");
		c2.connectParent(cs1);
		cs2 = (CategorySet) f.makeNode(CategorySet.class, "Size class");
		c3 = (Category) f.makeNode(Category.class, "Big");
		c3.connectParent(cs2);
		c4 = (Category) f.makeNode(Category.class, "Small");
		c4.connectParent(cs2);
		cs3 = (CategorySet) f.makeNode(CategorySet.class, "Whale species");
		cs3.connectParent(c1);
		c5 = (Category) f.makeNode(Category.class, "Sperm whale");
		c5.connectParent(cs3);
		c6 = (Category) f.makeNode(Category.class, "Blue whale");
		c6.connectParent(cs3);
		List<Category> l = new LinkedList<>();
		l.add(c1); l.add(c3);
		ca = new categorizedAdapter(l);
		cc = new icontainer(ca,"bropz",null);
		l.clear(); l.add(c5);
		ca2 = new categorizedAdapter(l);
		scope = new LocalScope("test");
		for (int i=0; i<3; i++)
			cc.addItem(scope.newId("whale_"+i));
		cc.effectChanges();
	}
	
	private void init2(String proposedId) {
		cc2 = new icontainer(ca2,proposedId,cc);
		for (int i=0; i<4; i++)
			cc2.addItem(scope.newId("bw_"+i));
		cc2.effectChanges();

	}
	
	private void show(String method,String text) {
		System.out.println(method+": "+text);
	}

	@Test
	final void testCategorizedContainer() {
		assertNotNull(cc);
	}

	@Test
	final void testCategoryInfo() {
		show("testCategoryInfo",cc.categoryInfo().categoryId());
		assertEquals(cc.categoryInfo(),ca);
	}

	@Test
	final void testParameters() {
		fail("Not yet implemented");
	}

	@Test
	final void testVariables() {
		fail("Not yet implemented");
	}

	@Test
	final void testAddItem() {
		int currentCount = cc.count();
		for (int i=0; i<10; i++)
			cc.addItem(scope.newId("whale "+i));
		assertEquals(cc.count(),currentCount);
		cc.effectChanges();
		assertEquals(cc.count(),currentCount+10);
	}

	@Test
	final void testRemoveItem() {
		int currentCount = cc.count();
		for (int i=0; i<3; i++)
			cc.removeItem("whale_"+i);
		assertEquals(cc.count(),currentCount);
		cc.effectChanges();
		assertEquals(cc.count(),currentCount-3);
	}

	@Test
	final void testItem() {
		show("testItem",cc.item("whale_1").toString());
		assertEquals(cc.item("whale_1").toString(),"whale_1");
	}

	@Test
	final void testItems() {
		int n=0;
		for (Identity item:cc.items()) {
			show("testItems",item.toString());
			n++;
		}
		assertEquals(n,3);
	}

	@Test
	final void testSubContainer() {
		init2("glurtch");
		int n=0;
		for (Identity item:cc.subContainer("glurtch").items()) {
			show("testSubContainer",item.id());
			n++;
		}
		assertEquals(n,4);
	}

	@Test
	final void testSubContainers() {
		init2("broupk");
		for (CategorizedContainer<Identity> c:cc.subContainers())
			show("testSubContainers",c.id());
	}

	@Test
	final void testAllItems() {
		init2("zefklop");
		int n=0;
		for (Identity item:cc.allItems()) {
			show("testAllItems",item.id());
			n++;
		}
		assertEquals(n,7);
	}

	@Test
	final void testEffectChanges() {
		cc.resetCounters();
		cc.addItem(scope.newId("whale 127"));
		assertEquals(cc.nAdded(),0);
		assertEquals(cc.count(),3);
		assertEquals(cc.nRemoved(),0);
		cc.effectChanges();
		assertEquals(cc.nAdded(),1);
		assertEquals(cc.count(),4);
		cc.removeItem("whale_1");
		assertEquals(cc.count(),4);
		assertEquals(cc.nRemoved(),0);
		cc.effectChanges();
		assertEquals(cc.count(),3);
		assertEquals(cc.nRemoved(),1);
	}

	@Test
	final void testCount() {
		assertEquals(cc.count(),3);
	}

	@Test
	final void testNAdded() {
		cc.resetCounters();
		for (int i=0; i<2; i++) {
			cc.addItem(scope.newId("whale "+i));
			cc.effectChanges();
			show("testNAdded",Integer.toString(cc.nAdded())+"/"+Integer.toString(cc.count()));
		}
		assertEquals(cc.nAdded(),2);
		assertEquals(cc.count(),5);
	}

	@Test
	final void testNRemoved() {
		cc.resetCounters();
		for (int i=0; i<2; i++) {
			cc.removeItem("whale_"+i);
			cc.effectChanges();
			show("testNRemoved",Integer.toString(cc.nRemoved())+"/"+Integer.toString(cc.count()));
		}
		assertEquals(cc.nRemoved(),2);
		assertEquals(cc.count(),1);
	}

	@Test
	final void testResetCounters() {
		cc.removeItem("whale_2");
		cc.effectChanges();
		assertEquals(cc.nAdded(),3);
		assertEquals(cc.nRemoved(),1);
		assertEquals(cc.count(),2);
		cc.resetCounters();
		assertEquals(cc.nAdded(),0);
		assertEquals(cc.nRemoved(),0);
		assertEquals(cc.count(),2);
	}

	@Test
	final void testId() {
		show("testId",cc.id());
	}

	@Test
	final void testReset() {
		init2("zorgl");
		for (Identity item:cc.items())
			show("testReset",item.id());
		assertEquals(cc.nAdded(),3);
		assertEquals(cc.count(),3);
		assertEquals(cc.nRemoved(),0);
		cc.setInitialItems(cc.subContainer("zorgl").items());
		cc.reset();
		for (Identity item:cc.items())
			show("testReset",item.id());
		assertEquals(cc.nAdded(),0);
		assertEquals(cc.count(),4);
		assertEquals(cc.nRemoved(),0);
	}

}