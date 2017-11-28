import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

  Node root;
  int size;
  int count;
  
  private static final double XMIN = 0;
  private static final double XMAX = 1;
  private static final double YMIN = 0;
  private static final double YMAX = 1;
  
  
  private static class Node {
    private Point2D p; // the point
    private RectHV rect; // the axis-aligned rectangle corresponding to this node
    private Node lb; // the left/bottom subtree
    private Node rt; // the right/top subtree
    
    public Node(Point2D pt) {
      p = pt;
    }

//    public int size() {
//      int size = 0;
//      if (p != null) {
//        if (lb != null) {
//          size += lb.size();
//        }
//        if (rt != null) {
//          size += rt.size();
//        }
//        size++;
//      }
//      return size;
//    }
  }

  /**
   * KdTree constructor. Initializes the root node with an empty node and sets the tree size
   * to 0.
   */
  public KdTree() {
    // construct an empty set of points
    root = new Node(null);
    size = 0;
  }

  public boolean isEmpty() {
    // is the set empty?
    return (root.p == null);
  }

  public int size() {
    // number of points in the set
    return size;
  }

  /**
   * Recursive search method to be used by contains and insert. It returns a node that
   * is either the point if found or a new dummy node with the lb or rt link filled 
   * with the parent node where the point p will have to be inserted if the call to
   * this method was originated from an insert operation. The caller just needs to 
   * check if the return node.p is null or not.
   * @param p the point to be found
   * @param n the node to search the point p within its subtrees
   * @param x a boolean indicating if we need to compare the x or the y coordinate
   * @return a Node object that is the Node where the point p was found, if node.p
   *         is not null of a dummy node with its lb or rt links pointing to the 
   *         parent node, where the point p will have to be inserted exactly in the
   *         filled link.
   */
  private Node search(Point2D p, Node n, boolean x) {
    if (n.p.equals(p)) {
      return n;
    }
    if ((x ? p.x() : p.y()) < (x ? n.p.x() : n.p.y())) {
      if (n.lb != null) {
        return search(p, n.lb, !x);
      } else {
        // if we found a null position where the point p is supposed to be if inserted,
        // we return a dummy node with that link pointing to the future parent node.
        Node n2 = new Node(null);
        n2.lb = n;
        return n2;
      }
    } else {
      if (n.rt != null) {
        return search(p, n.rt, !x);
      } else {
        // if we found a null position where the point p is supposed to be if inserted,
        // we return a dummy node with that link pointing to the future parent node.
        Node n2 = new Node(null);
        n2.rt = n;
        return n2;
      }
    }
  }
  
  /**
   * Recursive method to insert into the tree.
   * @param p the point to be inserted.
   * @param n the node to be searched or inserted
   * @param left if we consider the x coordinate (true) or y coordinate (false) to decide
   *        what subtree to insert the point
   */
  //  private Node insert(Point2D p, Node n, boolean x) {
  //    if (n == null) {
  //      // we found the position to insert the point p
  //      size++;
  //      return new Node(p);
  //    }
  //    if (n.p.equals(p)) {
  //      // we found the same point, so we do not insert anything
  //      // we return the same node, so the link will not be changed
  //      return n;
  //    }
  //    if (x) {
  //      if (p.x() < n.p.x()) {
  //        n.lb = insert(p, n.lb, !x);
  //      } else {
  //        n.rt = insert(p, n.rt, !x);
  //      }
  //    } else {
  //      if (p.y() < n.p.y()) {
  //        n.lb = insert(p, n.lb, !x);
  //      } else {
  //        n.rt = insert(p, n.rt, !x);
  //      }
  //    }
  //    return null;
  //  }
  
  /**
   * Recursive method to insert a new point into the KdTree. First it performs a search
   * to find the right spot for the point. If we have a hit, then we do not add the same
   * point again. Otherwise, we insert it as a child node.
   * @param p the point to be inserted
   */
  public void insert(Point2D p) {
    // add the point to the set (if it is not already in the set)
    if (p == null) {
      throw new java.lang.IllegalArgumentException("null argument received");
    }
    if (root.p == null) {
      size++;
      root.p = p; // if the tree is empty, then we add its first node into the existing
      return;
    }
    if (root.p.equals(p)) {
      // the same point, we do not insert it again
      return;
    }
    
    Node n = search(p, root, true);
    
    if (n.p == null) {
      // then we have to insert the new point p at the parent node
      // otherwise, do nothing because the point p is already in the tree
      n.p = p; // to reuse this already initialized point p
      if (n.lb != null) {
        // we have to insert p into the left/bottom link of the node pointed by n.lb
        n.lb.lb = n;
        n.lb = null;
      } else {
        n.rt.rt = n;
        n.rt = null;
      }
      size++;
    }
  }

  /**
   * Check if the set contains the point p.
   * @param p the point to be searched
   * @return true, if point p is found in the tree, false otherwise
   */
  public boolean contains(Point2D p) {
    // does the set contain point p?
    if (p == null) {
      throw new java.lang.IllegalArgumentException("null argument received");
    }
    if (root.p == null) {
      // tree is empty
      return false;
    }
    return (search(p, root, true).p != null);
  }

  private void draw(Node n, Point2D parent, boolean vertical, double xMin, double yMin, double xMax, double yMax) {
    if (n == null) {
      return;
    }

    // draw the node's point 
    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor(StdDraw.BLACK);
    n.p.draw();
    StdDraw.text(n.p.x() + 0.01, n.p.y() + 0.01, Integer.toString(++count));
    
    // draw the line passing through the point
    if (vertical) {
      double ymin = yMin;
      double ymax = yMax;
      
      if (parent != null) {
        if (n.p.y() > parent.y()) {
          ymin = parent.y();
        } else {
          ymax = parent.y();
        }
      }
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.setPenRadius();
      Point2D pt = new Point2D(n.p.x(), ymin);
      pt.drawTo(new Point2D(n.p.x(), ymax));
    } else {
      double xmin = xMin;
      double xmax = xMax;
      
      if (parent != null) {
        if (n.p.x() > parent.x()) {
          xmin = parent.x();
        } else {
          xmax = parent.x();
        }
      }
      StdDraw.setPenColor(StdDraw.BLUE);
      StdDraw.setPenRadius();
      Point2D pt = new Point2D(xmin, n.p.y());
      pt.drawTo(new Point2D(xmax, n.p.y()));
    }
    
    // draw its left/bottom node
    draw(n.lb, n.p, !vertical, xMin, yMin, xMax, yMax);
    
    // draw its right/top node
    draw(n.rt, n.p, !vertical, xMin, yMin, xMax, yMax);
  }
  
  /**
   * Draw all points to standard draw. 
   */
  public void draw() {
    // draw all points to standard draw 
    // starting from root, start drawing one red vertical line for the root node, then go to 
    // left subtree and draw at first, a blue horizontal line from a border to the x of parent
    // point and then recursively draw lines alternating blue horizontal and red vertical
    // lines depending on the level. After that, do the same for the right subtree, also 
    // alternating colors and directions
    if (root.p == null) {
      return;
    }
    count = 0;
    draw(root, null, true, XMIN, YMIN, XMAX, YMAX);
  }

  /**
   * All points that are inside the rectangle (or on the boundary).
   * @param rect the rectangle
   * @return an Iterable containing all points that are inside the rectangle
   */
  public Iterable<Point2D> range(RectHV rect) {
    // all points that are inside the rectangle (or on the boundary)
    if (rect == null) {
      throw new java.lang.IllegalArgumentException("null argument received");
    }
    return null;
  }

  /**
   * Finds the nearest neighbor to a point p; null if the set is empty.
   * @param p the point to find the nearest
   * @return the nearest point to p
   */
  public Point2D nearest(Point2D p) {
    // a nearest neighbor in the set to point p; null if the set is empty
    if (p == null) {
      throw new java.lang.IllegalArgumentException("null argument received");
    }
    return null;
  }

  /**
   * Main method for KdTree class. User for unit tests.
   * @param args command-line parameter
   */
  public static void main(String[] args) {
    System.out.println("\"Unit\" tests for kdTree");
    
    KdTree tree = new KdTree();
    Point2D p = new Point2D(0.1, 0.1);
    System.out.println("Testing if tree contains point p (should NOT contain): "
        + (!tree.contains(p) ? "[OK]" : "[FAIL]"));
    tree.insert(p);
    System.out.println("Testing if tree contains point p (should contain): "
        + (tree.contains(p) ? "[OK]" : "[FAIL]"));
    
    tree.insert(new Point2D(0.3, 0.2));
    tree.insert(new Point2D(0.25, 0.3));
    tree.insert(new Point2D(0.35, 0.05));
    tree.insert(new Point2D(0.2, 0.35));
    tree.insert(new Point2D(0.15, 0.25));
    tree.insert(new Point2D(0.4, 0.15));
    
    System.out.println("Testing if tree contains point p (should contain): "
        + (tree.contains(new Point2D(0.3, 0.1)) ? "[OK]" : "[FAIL]"));
    System.out.println("Testing if tree contains point p (should NOT contain): "
        + (!tree.contains(new Point2D(0.5, 0.1)) ? "[OK]" : "[FAIL]"));
    System.out.println("Testing if tree contains point p (should contain): "
        + (!tree.contains(new Point2D(0.21, 0.1)) ? "[OK]" : "[FAIL]"));
    
    System.out.println("Drawing the points and the lines separating the rectangles");
    
    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 1);
    StdDraw.setYscale(0, 1);
    tree.draw();
    
    StdDraw.show();
  }

}
