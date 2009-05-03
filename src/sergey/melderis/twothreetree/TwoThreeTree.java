/**
 *  A 2-3 tree is a balanced search tree where each node can have either two children and one value (2-node),
 * or three children and two values (3-node).
 *
 * References:
 * http://scienceblogs.com/goodmath/2009/03/two-three_trees_a_different_ap.php
 * http://cs.wellesley.edu/~cs230/spring07/2-3-trees.pdf
 *
 *
 * Author: Sergejs Melderis (sergey.melderis@gmail.com)
 */


package sergey.melderis.twothreetree;


import java.util.*;

class Node<T extends Comparable> {
    private Node<T> parent;
    private Node<T> leftChild;
    private Node<T> rightChild;
    private Node<T> middleChild;

    // When node is 2-node, leftVal is the values, and rightVal is null.
    private T leftVal;
    private T rightVal;

    private boolean twoNode;


    protected Node() {

    }

    public static <T extends Comparable> Node<T> newTwoNode(T value) {
        Node<T> node = new Node<T>();
        node.leftVal = value;
        node.twoNode = true;
        return node;
    }


    public static <T extends Comparable> Node<T> newThreeNode(T leftVal, T rightVal) {
        Node<T> node = new Node<T>();
        if (leftVal.compareTo(rightVal) > 0) {
            node.rightVal = leftVal;
            node.leftVal = rightVal;
        } else {
            node.leftVal = leftVal;
            node.rightVal = rightVal;
        }
        node.twoNode = false;
        return node;
    }


    public static HoleNode newHole() {
        return new HoleNode();
    }




    public void setLeftChild(Node<T> leftChild) {
        this.leftChild = leftChild;
        if (leftChild != null)
            leftChild.setParent(this);
    }

    public void removeChildren() {
        this.leftChild = null;
        this.rightChild = null;
    }


    public void setRightChild(Node<T> rightChild) {
        this.rightChild = rightChild;
        if (rightChild != null)
            rightChild.setParent(this);
    }

    public void setMiddleChild(Node<T> middleChild) {
        assert isThreeNode();
        this.middleChild = middleChild;
        if (middleChild != null) {
            middleChild.setParent(this);
        }
    }


    public final Node<T> parent() {
        return parent;
    }

    public final void setParent(Node<T> parent) {
        this.parent = parent;
    }


    public boolean isTerminal() {
        return leftChild == null && rightChild == null;
    }


    public T val() {
        assert isTwoNode();
        return leftVal;
    }
    

    public T leftVal() {
        assert isThreeNode();
        return leftVal;
    }

    public void setVal(T val) {
        assert isTwoNode();
        leftVal = val;
    }


    public T rightVal() {
        assert isThreeNode();
        return rightVal;
    }

    public void setLeftVal(T leftVal) {
        assert isThreeNode();
        this.leftVal = leftVal;
    }

    public void setRightVal(T rightVal) {
        assert isThreeNode();
        this.rightVal = rightVal;
    }

    public boolean isTwoNode() {
       // return rightVal == null;
        return twoNode;
    }

    public boolean isThreeNode() {
        return !isTwoNode();
    }

    public Node<T> leftChild() {
        return leftChild;
    }

    public Node<T> rightChild() {
        return rightChild;
    }

    public Node<T> middleChild() {
        assert isThreeNode();
        return middleChild;
    }

    @SuppressWarnings("unchecked")
    public void replaceChild(Node currentChild, Node newChild) {
        if (currentChild == leftChild) {
            leftChild = newChild;
        } else if (currentChild == rightChild) {
            rightChild = newChild;
        } else {
            assert  middleChild == currentChild;
            middleChild = newChild;
        }
        newChild.setParent(this);
        currentChild.setParent(null);
    }
}


/**
 * A hole node does not have any values, and have only one child.
 */
final class HoleNode<T extends Comparable> extends Node {
    private Node<T> child;

    HoleNode() {
        super();
    }

    public boolean isTwoNode() {
        return false;
    }

    public Node sibling() {
        if (parent() != null) {
            return parent().leftChild() == this ? parent().rightChild(): parent().leftChild();
        }
        return null;
    }

    @Override
    public void setLeftChild(Node leftChild) {
    }

    @Override
    public void removeChildren() {
        child = null;
    }


    @Override
    public void setRightChild(Node rightChild) {
    }

    public Node<T> child() {
        return child;
    }

    public void setChild(Node<T> child) {
        this.child = child;
    }
}


@SuppressWarnings("unchecked")
public class TwoThreeTree<T extends Comparable> extends AbstractSet<T> implements SortedSet<T> {

    Node<T> root;
    int size = 0;

    public boolean add(T value) {
        if (root == null)
            root = Node.newTwoNode(value);
        else {
            try {
                Node<T> result = insert(value, root);
                if (result != null) {
                    root = result;
                }
            } catch (DuplicateException e) {
                return false;
            }
        }
        size ++;
        return true;
    }


    public boolean contains(T value) {
        return findNode(root, value) != null;
    }


    private Node<T> findNode(Node<T> node, T value) {
        if (node == null) return null;

        if (node.isThreeNode()) {
            int leftComp = value.compareTo(node.leftVal());
            int rightComp = value.compareTo(node.rightVal());
            if (leftComp == 0 || rightComp == 0) {
                return node;
            }
            if (leftComp < 0) {
                return findNode(node.leftChild(), value);
            } else if (rightComp < 0) {
                return findNode(node.middleChild(), value);
            } else {
                return findNode(node.rightChild(), value);
            }
        } else {
            int comp = value.compareTo(node.val());
            if (comp == 0)
                return node;
            if (comp < 0)
                return findNode(node.leftChild(), value);
            else
                return findNode(node.rightChild(), value);
        }
    }


    private static final class DuplicateException extends RuntimeException {};
    private static final DuplicateException DUPLICATE = new DuplicateException();


    private Node<T> insert(T value, Node<T> node) throws DuplicateException {
        Node<T> returnValue = null;
        if (node.isTwoNode()) {
            int comp = value.compareTo(node.val());

            if (node.isTerminal()) {
                if (comp == 0)
                    throw DUPLICATE;
                Node<T> thnode = Node.newThreeNode(value, node.val());
                Node<T> parent = node.parent();
                if (parent != null)
                    parent.replaceChild(node, thnode);
                else
                    root = thnode;
            } else {
                if (comp < 0) {
                    Node<T> result = insert(value, node.leftChild());
                    if (result != null) {
                        Node<T> threeNode = Node.newThreeNode(result.val(), node.val());
                        threeNode.setRightChild(node.rightChild());
                        threeNode.setMiddleChild(result.rightChild());
                        threeNode.setLeftChild(result.leftChild());
                        if (node.parent() != null) {
                            node.parent().replaceChild(node, threeNode);
                        } else {
                            root = threeNode;
                        }
                        unlinkNode(node);
                    }
                } else if (comp > 0) {
                    Node<T> result = insert(value, node.rightChild());
                    if (result != null) {
                        Node<T> threeNode = Node.newThreeNode(result.val(), node.val());
                        threeNode.setLeftChild(node.leftChild());
                        threeNode.setMiddleChild(result.leftChild());
                        threeNode.setRightChild(result.rightChild());
                        if (node.parent() != null) {
                            node.parent().replaceChild(node, threeNode);
                        } else {
                            root = threeNode;
                        }
                        unlinkNode(node);
                    }
                } else {
                    throw DUPLICATE;
                }
            }

        } else { // three node
            Node<T> threeNode = node;

            int leftComp = value.compareTo(threeNode.leftVal());
            int rightComp = value.compareTo(threeNode.rightVal());
            if (leftComp == 0 || rightComp == 0) {
                throw DUPLICATE;
            }

            if (threeNode.isTerminal()) {

                returnValue = splitNode(threeNode, value);

            } else {
                if (leftComp < 0) {
                    Node<T> result = insert(value, threeNode.leftChild());
                    if (result != null) {
                        returnValue = splitNode(threeNode, result.val());
                        returnValue.leftChild().setLeftChild(result.leftChild());
                        returnValue.leftChild().setRightChild(result.rightChild());
                        returnValue.rightChild().setLeftChild(threeNode.middleChild());
                        returnValue.rightChild().setRightChild((threeNode.rightChild()));
                        unlinkNode(threeNode);
                    }
                } else if (rightComp < 0) {
                    Node<T> result = insert(value, threeNode.middleChild());
                    if (result != null) {
                        returnValue = splitNode(threeNode, result.val());
                        returnValue.leftChild().setLeftChild(threeNode.leftChild());
                        returnValue.leftChild().setRightChild(result.leftChild());
                        returnValue.rightChild().setLeftChild(result.rightChild());
                        returnValue.rightChild().setRightChild(threeNode.rightChild());
                        unlinkNode(threeNode);
                    }
                } else  {
                    Node<T> result = insert(value, threeNode.rightChild());
                    if (result != null) {
                        returnValue = splitNode(threeNode, result.val());
                        returnValue.leftChild().setLeftChild(threeNode.leftChild());
                        returnValue.leftChild().setRightChild(threeNode.middleChild());
                        returnValue.rightChild().setLeftChild(result.leftChild());
                        returnValue.rightChild().setRightChild(result.rightChild());
                        unlinkNode(threeNode);
                    }
                } 
            }
        }
        return returnValue;
    }



    public boolean remove(T value) {
        if (value == null)
            return false;
      //  System.out.println("removing " + value);
        Node<T> node = findNode(root, value);
        if (node == null)
            return false;

        HoleNode hole = null;
        Node<T> terminalNode;
        T holeValue;
        if (node.isTerminal()) {
            terminalNode = node;
            holeValue = value;
        } else {
            // Replace by successor.
            if (node.isThreeNode()) {
                if (node.leftVal().equals(value)) {
                    Node<T> pred = predecessor(node, value);
                    holeValue = pred.isThreeNode() ? pred.rightVal() : pred.val();
                    node.setLeftVal(holeValue);
                    terminalNode = pred;
                } else {
                    Node<T> succ = successor(node, value);
                    holeValue = succ.isThreeNode() ? succ.leftVal() : succ.val();
                    node.setRightVal(holeValue);
                    terminalNode = succ;
                }
            } else {
                Node<T> succ = successor(node, value);
                holeValue = succ.isThreeNode() ? succ.leftVal() : succ.val();
                node.setVal(holeValue);
                terminalNode = succ;
            }
        }

        assert terminalNode.isTerminal();

        if (terminalNode.isThreeNode()) {
            // Easy case. Replace 3-node by 2-node
            T val = terminalNode.leftVal().equals(holeValue) ? terminalNode.rightVal() : terminalNode.leftVal();
            Node<T> twoNode = Node.newTwoNode(val);
            if (terminalNode.parent() != null) {
                terminalNode.parent().replaceChild(terminalNode, twoNode);
            } else {
                root = twoNode;
            }
        } else {
            if (terminalNode.parent() != null) {
                hole = Node.newHole();
                terminalNode.parent().replaceChild(terminalNode, hole);
            } else {
                root = null;
            }
        }

        // For description of each case see
        // "2-3 Tree Deletion: Upward Phase" in  http://cs.wellesley.edu/~cs230/spring07/2-3-trees.pdf
        while (hole != null) {
            // Case 1. The hole has a 2-node as parent and 2-node as sibling.
            if (hole.parent().isTwoNode() && hole.sibling().isTwoNode()) {
                //System.out.println("Case 1");
                Node<T> parent = hole.parent();
                Node<T> sibling = hole.sibling();

                Node<T> threeNode = Node.newThreeNode(parent.val(), sibling.val());
                if (parent.leftChild() == hole) {
                    threeNode.setLeftChild(hole.child());
                    threeNode.setMiddleChild(sibling.leftChild());
                    threeNode.setRightChild(sibling.rightChild());
                } else {
                    threeNode.setLeftChild(sibling.leftChild());
                    threeNode.setMiddleChild(sibling.rightChild());
                    threeNode.setRightChild(hole.child());
                }

                if (parent.parent() == null) {
                    unlinkNode(hole);
                    root = threeNode;
                    hole = null;
                } else {
                    hole.setChild(threeNode);
                    parent.parent().replaceChild(parent, hole);
                }
                unlinkNode(parent);
                unlinkNode(sibling);

            }
            // Case 2. The hole has a 2-node as parent and 3-node as sibling.
            else if (hole.parent().isTwoNode() && hole.sibling().isThreeNode()) {
                //System.out.println("Case 2 ");
                Node<T> parent = hole.parent();
                Node<T> sibling = hole.sibling();

                if (parent.leftChild() == hole) {
                    Node<T> leftChild = Node.newTwoNode(parent.val());
                    Node<T> rightChild = Node.newTwoNode(sibling.rightVal());
                    parent.setVal(sibling.leftVal());
                    parent.replaceChild(hole, leftChild);
                    parent.replaceChild(sibling, rightChild);
                    leftChild.setLeftChild(hole.child());
                    leftChild.setRightChild(sibling.leftChild());
                    rightChild.setLeftChild(sibling.middleChild());
                    rightChild.setRightChild(sibling.rightChild());
                } else {
                    Node<T> leftChild = Node.newTwoNode(sibling.leftVal());
                    Node<T> rightChild = Node.newTwoNode(parent.val());
                    parent.setVal(sibling.rightVal());
                    parent.replaceChild(sibling, leftChild);
                    parent.replaceChild(hole, rightChild);
                    leftChild.setLeftChild(sibling.leftChild());
                    leftChild.setRightChild(sibling.middleChild());
                    rightChild.setLeftChild(sibling.rightChild());
                    rightChild.setRightChild(hole.child());
                }
                unlinkNode(hole);
                unlinkNode(sibling);
                hole = null;
            }

            // Case 3. The hole has a 3-node as parent and 2-node as sibling.
            else if (hole.parent().isThreeNode()) {
                Node<T> parent = hole.parent();

                // subcase (a), hole is in the middle
                if (parent.middleChild() == hole && parent.leftChild().isTwoNode()) {
                    //System.out.println("Case 3 (a) hole in the middle");
                    Node<T> leftChild = parent.leftChild();
                    Node<T> newParent = Node.newTwoNode(parent.rightVal());
                    Node<T> newLeftChild = Node.newThreeNode(leftChild.val(), parent.leftVal());
                    newParent.setLeftChild(newLeftChild);
                    newParent.setRightChild(parent.rightChild());
                    if (parent != root) {
                        parent.parent().replaceChild(parent, newParent);
                    } else {
                        root = newParent;
                    }

                    newLeftChild.setLeftChild(leftChild.leftChild());
                    newLeftChild.setMiddleChild(leftChild.rightChild());
                    newLeftChild.setRightChild(hole.child());

                    unlinkNode(parent);
                    unlinkNode(leftChild);
                    unlinkNode(hole);
                    hole = null;
                }
                // subcase (b), hole is in the middle
                else if (parent.middleChild() == hole && parent.rightChild().isTwoNode()) {
                    //System.out.println("Case 3(b) hole in the middle");
                    Node<T> rightChild = parent.rightChild();
                    Node<T> newParent = Node.newTwoNode(parent.leftVal());
                    Node<T> newRightChild = Node.newThreeNode(parent.rightVal(), rightChild.val());
                    newParent.setLeftChild(parent.leftChild());
                    newParent.setRightChild(newRightChild);
                    if (parent != root) {
                        parent.parent().replaceChild(parent, newParent);
                    } else {
                        root = newParent;
                    }
                    newRightChild.setLeftChild(hole.child());
                    newRightChild.setMiddleChild(rightChild.leftChild());
                    newRightChild.setRightChild(rightChild.rightChild());
                    unlinkNode(parent);
                    unlinkNode(rightChild);
                    unlinkNode(hole);
                    hole = null;
                }
                else if (parent.middleChild().isTwoNode()) {
                    Node<T> middleChild = parent.middleChild();

                    // subcase (a). hole is the left child.
                    if (parent.leftChild() == hole) {
                        //System.out.println("Case 3 (a) hole is left child");
                        Node<T> newParent = Node.newTwoNode(parent.rightVal());
                        Node<T> leftChild = Node.newThreeNode(parent.leftVal(), middleChild.val());
                        newParent.setLeftChild(leftChild);
                        newParent.setRightChild(parent.rightChild());
                        if (parent != root) {
                            parent.parent().replaceChild(parent, newParent);
                        } else {
                            root = newParent;
                        }

                        leftChild.setLeftChild(hole.child());
                        leftChild.setMiddleChild(middleChild.leftChild());
                        leftChild.setRightChild(middleChild.rightChild());

                        unlinkNode(parent);
                        unlinkNode(hole);
                        unlinkNode(middleChild);
                        hole = null;
                    }
                    // subcase (a). hole is the right child.
                    else if (parent.rightChild() == hole) {
                        //System.out.println("Case 3 (a) hole is right child");
                        Node<T> newParent = Node.newTwoNode(parent.leftVal());
                        Node<T> rightChild = Node.newThreeNode(middleChild.val(), parent.rightVal());
                        newParent.setRightChild(rightChild);
                        newParent.setLeftChild(parent.leftChild());
                        if (parent != root) {
                            parent.parent().replaceChild(parent, newParent);
                        } else {
                            root = newParent;
                        }

                        rightChild.setLeftChild(middleChild.leftChild());
                        rightChild.setMiddleChild(middleChild.rightChild());
                        rightChild.setRightChild(hole.child());

                        unlinkNode(parent);
                        unlinkNode(hole);
                        unlinkNode(middleChild);
                        hole = null;
                    }
                }

                // Case 4. The hole has a 3-node as parent and 3-node as sibling.

                else if (parent.middleChild().isThreeNode()) {
                    Node<T> middleChild = parent.middleChild();
                    // subcase (a) hole is the left child
                    if (hole == parent.leftChild()) {
                        //System.out.println("Case 4 (a) hole is left child");
                        Node<T> newLeftChild = Node.newTwoNode(parent.leftVal());
                        Node<T> newMiddleChild = Node.newTwoNode(middleChild.rightVal());
                        parent.setLeftVal(middleChild.leftVal());
                        parent.setLeftChild(newLeftChild);
                        parent.setMiddleChild(newMiddleChild);
                        newLeftChild.setLeftChild(hole.child());
                        newLeftChild.setRightChild(middleChild.leftChild());
                        newMiddleChild.setLeftChild(middleChild.middleChild());
                        newMiddleChild.setRightChild(middleChild.rightChild());

                        unlinkNode(hole);
                        unlinkNode(middleChild);
                        hole = null;
                    }
                    // subcase (b) hole is the right child
                    else if (hole == parent.rightChild()) {
                       // System.out.println("Case 4 (b) hole is right child");
                        Node<T> newMiddleChild = Node.newTwoNode(middleChild.leftVal());
                        Node<T> newRightChild = Node.newTwoNode(parent.rightVal());
                        parent.setRightVal(middleChild.rightVal());
                        parent.setMiddleChild(newMiddleChild);
                        parent.setRightChild(newRightChild);
                        newMiddleChild.setLeftChild(middleChild.leftChild());
                        newMiddleChild.setRightChild(middleChild.middleChild());
                       // newMiddleChild.setParent(middleChild.middleChild());
                        newRightChild.setLeftChild(middleChild.rightChild());
                        newRightChild.setRightChild(hole.child());

                        unlinkNode(hole);
                        unlinkNode(middleChild);
                        hole = null;

                    } else if (hole == parent.middleChild() && parent.leftChild().isThreeNode()) {
                       // System.out.println("Case 4 (a) hole is middle child, left is 3-node");
                        Node<T> leftChild = parent.leftChild();
                        Node<T> newLeftChild = Node.newTwoNode(leftChild.leftVal());
                        Node<T> newMiddleChild = Node.newTwoNode(parent.leftVal());
                        parent.setLeftVal(leftChild.rightVal());
                        parent.setLeftChild(newLeftChild);
                        parent.setMiddleChild(newMiddleChild);
                        newLeftChild.setLeftChild(leftChild.leftChild());
                        newLeftChild.setRightChild(leftChild.middleChild());
                        newMiddleChild.setLeftChild(leftChild.rightChild());
                        newMiddleChild.setRightChild(hole.child());

                        unlinkNode(hole);
                        unlinkNode(leftChild);
                        hole = null;
                    } else {
                       assert  (hole == parent.middleChild() && parent.rightChild().isThreeNode());
                       // System.out.println("Case 4 (b) hole is middle child, right is 3-node");
                        Node<T> rightChild = parent.rightChild();
                        Node<T> newRightChild = Node.newTwoNode(rightChild.rightVal());
                        Node<T> newMiddleChild = Node.newTwoNode(parent.rightVal());
                        parent.setRightVal(rightChild.leftVal());
                        parent.setMiddleChild(newMiddleChild);
                        parent.setRightChild(newRightChild);
                        newRightChild.setRightChild(rightChild.rightChild());
                        newRightChild.setLeftChild(rightChild.middleChild());
                        newMiddleChild.setRightChild(rightChild.leftChild());
                        newMiddleChild.setLeftChild(hole.child());

                        unlinkNode(hole);
                        unlinkNode(rightChild);
                        hole = null;
                    }
                }

            }
        }

        size--;
        return true;
    }


    private void unlinkNode(Node node) {
        node.removeChildren();
        node.setParent(null);
    }


    private Node<T> successor(Node<T> node, T value) {
        if (node == null)
            return null;

        if (!node.isTerminal()) {
            Node<T> p;
            if (node.isThreeNode() && node.leftVal().equals(value)) {
                p = node.middleChild();
            } else {
                p = node.rightChild();
            }
            while (p.leftChild() != null) {
                p = p.leftChild();
            }
            return p;
        } else {
            Node<T> p = node.parent();
            if (p == null) return null;
            
            Node<T> ch = node;
            while (p != null && ch == p.rightChild()) {
                ch = p;
                p = p.parent();
            }
            return p != null ? p : null;
        }
    }

    private Node<T> predecessor(Node<T> node, T value) {
        if (node == null)
            return null;

        Node<T> p;
        if (!node.isTerminal()) {
            if (node.isThreeNode() && node.rightVal().equals(value)) {
                p = node.middleChild();
            } else {
                p = node.leftChild();
            }

            while (p.rightChild() != null) {
                p = p.rightChild();
            }
            return p;
        } else {
            throw new UnsupportedOperationException("Implement predecessor parent is not terminal node");
        }
       
    }


    private Node<T> splitNode(Node<T> threeNode, T value) {
        T min;
        T max;
        T middle;
        if (value.compareTo(threeNode.leftVal()) < 0) {
            min = value;
            middle = threeNode.leftVal();
            max = threeNode.rightVal();
        } else if (value.compareTo(threeNode.rightVal()) < 0) {
            min = threeNode.leftVal();
            middle = value;
            max = threeNode.rightVal();
        } else {
            min = threeNode.leftVal();
            max = value;
            middle = threeNode.rightVal();
        }

        Node<T> parent = Node.newTwoNode(middle);
        parent.setLeftChild(Node.newTwoNode(min));
        parent.setRightChild(Node.newTwoNode(max));
        return parent;
    }


    public interface Function<T> {
        public void apply(T t);
    }


    /**
     * Preorder search.
     * Visit the node.
     * Visit the left subtree
     * Visit the middle subtree
    
     */
    public void preOrder(Node<T> node, Function<T> f) {
        if (node.isThreeNode()) {
            f.apply(node.leftVal());
            f.apply(node.rightVal());
        }
        if (node.isTerminal())
            return;


        preOrder(node.leftChild(), f);
        if (node.isThreeNode()) {
            assert node.middleChild() != null;
            preOrder(node.middleChild(), f);
        }
        preOrder(node.rightChild(), f);
    }



    public  void inorderSearch(Node<T> node, Function<T> func) {
        if (node == null)
            return;
        inorderSearch(node.leftChild(), func);
        if (node.isThreeNode()) {
            Node<T> threeNode = node;
            func.apply(threeNode.leftVal());
            inorderSearch(threeNode.middleChild(), func);
            func.apply(threeNode.rightVal());
        } else {
            func.apply(node.val());
        }
        inorderSearch(node.rightChild(), func);
    }


    // Set operations.


    /**
     * The returning iterator does not support remove.
     */
    public Iterator<T> iterator() {

        return new Iterator<T>() {
            Node<T> nextNode;

            // Stack to keep three nodes
            Deque<Node<T>> threeNodes = new ArrayDeque<Node<T>>();
            T next;
            {
                Node<T> node = root;
                while(node != null && node.leftChild() != null) {
                    node = node.leftChild();
                }
                nextNode = node;
            }
            public boolean hasNext() {
                return next != null || nextNode != null;
            }

            public T next() {
                T prev;
                if (next != null) {
                    prev = next;
                    next = null;
                    nextNode = successor(nextNode, prev);
                    return prev;
                }
                if (nextNode.isThreeNode()) {
                    if (nextNode.isTerminal()) {
                        next = nextNode.rightVal();
                        prev = nextNode.leftVal();
                    } else {
                        if (threeNodes.peekFirst() == nextNode) {
                            threeNodes.pollFirst();
                            prev = nextNode.rightVal();
                            nextNode = successor(nextNode, prev);
                        } else {
                            prev = nextNode.leftVal();
                            threeNodes.addFirst(nextNode);
                            nextNode = successor(nextNode, prev);
                        }
                    }
                } else {
                    prev = nextNode.val();
                    nextNode = successor(nextNode, prev);
                }
                return prev;
            }


            public void remove() {
               throw new UnsupportedOperationException();
            }
        };
        
    }





    public Comparator<? super T> comparator() {
        return null;
    }

    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }

    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }


    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }

    public T first() {
        Node<T> node = root;
        while (node.leftChild() != null) {
            node = node.leftChild();
        }
        return node.isThreeNode() ? node.leftVal() : node.val();
    }

    public T last() {
        Node<T> node = root;
        while (node.rightChild() != null) {
            node = node.rightChild();
        }
        return node.isThreeNode() ? node.rightVal() : node.val();
    }

    public int size() {
        return size;
    }


    @Override
    public boolean contains(Object o) {
        try {
            return contains ((T) o);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            return remove((T) o);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public void clear() {
        root = null;
    }


    @Override
    public Object[] toArray() {
        final Object arr[] = new Object[size];
        inorderSearch(root, new Function() {
            int index = 0;

            public void apply(Object t) {
                arr[index++] = (T) t;
            }
        });
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        T[] r = a.length >= size ? a :
                (T[]) java.lang.reflect.Array
                        .newInstance(a.getClass().getComponentType(), size);

        return _toArray(r);
    }


    public <T> T[]  _toArray(final T[] a) {
        inorderSearch(root, new Function() {
            int index = 0;

            public void apply(Object t) {
                a[index++] = (T) t;
            }
        });
        return a;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removed = false;
        for (Object o : c) {
            removed |= remove(o);
        }
        return removed;
    }




    @Override
    public String toString() {
        if (size == 0)
            return "[]";
        final StringBuilder sb = new StringBuilder("[");
        inorderSearch(root, new Function<T>() {
            public void apply(T t) {
                sb.append(t);
                sb.append(", ");
            }
        });
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

}
