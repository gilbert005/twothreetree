package sergey.melderis.twothreetree;


import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;



/**
 * Author: Sergejs Melderis (sergey.melderis@gmail.com)
 */
public class TwoThreeTest {



    // Test is simple. We just match TwoThreeTree agains TreeSet from JDK.
    @Test
    public void compareToThreeSet() throws Throwable {
        SortedSet<Integer> twoThreeTree = new TwoThreeTree<Integer>();
        SortedSet<Integer> treeSet = new TreeSet<Integer>();
        long timestamp = System.currentTimeMillis();
        Random random = new Random(timestamp);
        int size = 500;
        Set<Integer> values = new HashSet<Integer>();
        for (int i = 0; i < size; i++) {
            int value = random.nextInt(100000);
            treeSet.add(value);
            twoThreeTree.add(value);

            values.add(value);
            assertSetEquals(treeSet, twoThreeTree);
            assertEquals(treeSet.first(), twoThreeTree.first());
            assertEquals(treeSet.last(), twoThreeTree.last());
        }

        assertEquals(twoThreeTree, treeSet);

        for (Integer value : values) {

            assertEquals(treeSet.first(), twoThreeTree.first());
            assertEquals(treeSet.last(), twoThreeTree.last());

            assertTrue(treeSet.contains(value));
            assertTrue(twoThreeTree.contains(value));
            assertEquals(treeSet.first(), twoThreeTree.first());
            assertEquals(treeSet.last(), twoThreeTree.last());
            treeSet.remove(value);
            twoThreeTree.remove(value);

            assertEquals(treeSet, twoThreeTree);
            assertFalse(treeSet.contains(value));
            assertFalse(twoThreeTree.contains(value));
        }
    }


    public static void assertSetEquals(SortedSet expected, SortedSet actual) throws Throwable {
        try {
            assertEquals(expected, actual);
            assertArrayEquals(expected.toArray(), actual.toArray());
        } catch (Throwable e) {
            System.out.println("Set 1: " + expected);
            System.out.println("Set 2: " + actual);
            System.out.println("Arr 1: " + Arrays.toString(expected.toArray()));
            System.out.println("Arr 2: " + Arrays.toString(actual.toArray()));
            throw e;
        }
    }


    @Test
    public void testIterator() {
        SortedSet<Integer> twoThreeTree = new TwoThreeTree<Integer>();
        SortedSet<Integer> treeSet = new TreeSet<Integer>();
        long timestamp = System.currentTimeMillis();
        Random random = new Random(timestamp);
        int size = 50;
        for (int i = 0; i < size; i++) {
            int value = random.nextInt(1000);
            twoThreeTree.add(value);
            treeSet.add(value);
        }

        int[] treeSetValues = new int[treeSet.size()];

        int i = 0;
        for (Integer integer : treeSet) {
            treeSetValues[i++] = integer;
            System.out.print(integer + ", ");
        }

        System.out.println("");


        int[] twoThreeValues = new int[twoThreeTree.size()];
        i = 0;
        for (Integer integer : twoThreeTree) {
            twoThreeValues[i++] = integer;
            System.out.print(integer + ", ");
            if (i > size * 2)
                break;
        }

        System.out.println("");
        assertArrayEquals(treeSetValues, twoThreeValues);
    }
}
