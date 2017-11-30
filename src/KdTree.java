import java.util.Scanner;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

  private static final double XMIN = 0;
  private static final double XMAX = 1;
  private static final double YMIN = 0;
  private static final double YMAX = 1;

  private final Node root;
  private int size;
  private int count;
  
  private static class Node {
    private Point2D p; // the point
    private Node lb; // the left/bottom subtree
    private Node rt; // the right/top subtree
    
    public Node(Point2D pt) {
      p = pt;
    }
    
    public String toString() {
      return (new StringBuilder()
          .append("(")
          .append(Double.toString(p.x()))
          .append(", ")
          .append(Double.toString(p.y()))
          .append(")"))
          .toString();
    }
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

  private void draw(Node n, boolean vertical, double xmin, double ymin, double xmax,
      double ymax) {
    if (n == null) {
      return;
    }

    // draw the node's point 
    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor(StdDraw.BLACK);
    n.p.draw();
    StdDraw.text(n.p.x() + 0.01, n.p.y() + 0.01, Integer.toString(++count));
    StdDraw.show();
    
    // draw the line passing through the point
    if (vertical) {
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.setPenRadius();
      Point2D pt = new Point2D(n.p.x(), ymin);
      pt.drawTo(new Point2D(n.p.x(), ymax));
      StdDraw.show();
    } else {
      StdDraw.setPenColor(StdDraw.BLUE);
      StdDraw.setPenRadius();
      Point2D pt = new Point2D(xmin, n.p.y());
      pt.drawTo(new Point2D(xmax, n.p.y()));
      StdDraw.show();
    }
    
    // draw its left/bottom node
    draw(n.lb, !vertical, xmin, ymin, 
        (vertical ? n.p.x() : xmax), (vertical ? ymax : n.p.y()));
    
    // draw its right/top node
    draw(n.rt, !vertical, (vertical ? n.p.x() : xmin), (vertical ? ymin : n.p.y()),
        xmax, ymax);
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
    draw(root, true, XMIN, YMIN, XMAX, YMAX);
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
    
    // create a rectangle for the part of the canvas that we are investigating
    // check if there is an intersection with the rect argument
    // search the tree first going to left/bottom and then going to right/top
    // every point of each node of the tree that intersects the rectangle, we add to a queue
    
    Queue<Point2D> points = new Queue<Point2D>();
    
    range(rect, points, root, true, XMIN, YMIN, XMAX, YMAX);
    
    return points;
  }

  private void range(RectHV rect, Queue<Point2D> points, Node n, boolean vertical, double xmin,
      double ymin, double xmax, double ymax) {
    if (n == null) {
      return;
    }
    if (n.p == null) {
      return;
    }
    
    boolean added = false;
    // investigate the point, first left/bottom
    RectHV lbRect = new RectHV(xmin, ymin,
        (vertical ? n.p.x() : xmax), (vertical ? ymax : n.p.y()));
    
    if (rect.intersects(lbRect)) {
      // ok, we know there is a part of the rect that intersects the left/bottom plane
      // let's check if rect contains n.p
      if (rect.contains(n.p)) {
        // if it contains enqueue that point
        points.enqueue(n.p);
        added = true;
      }
      // investigate left/bottom subtree
      if (n.lb != null) {
        range(rect, points, n.lb, !vertical, xmin, ymin,
            (vertical ? n.p.x() : xmax), (vertical ? ymax : n.p.y()));
      }
    }

    // now investigate 
    RectHV rtRect = new RectHV((vertical ? n.p.x() : xmin), (vertical ? ymin : n.p.y()),
        xmax, ymax);
    
    if (rect.intersects(rtRect)) {
      // ok, we know there is a part of the rect that intersects the right/top plane
      // let's check if rect contains n.p (and if it was not added previously)
      if (!added && rect.contains(n.p)) {
        // if it contains enqueue that point
        points.enqueue(n.p);
      }
      // investigate right/top subtree
      if (n.rt != null) {
        range(rect, points, n.rt, !vertical, (vertical ? n.p.x() : xmin),
            (vertical ? ymin : n.p.y()), xmax, ymax);
      }
    }
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
    
    if (root.p == null) {
      return null;
    }
    
    return nearest(root, p, true);
  }

  private Point2D nearest(Node n, Point2D p, boolean vertical) {
    Point2D pt = n.p;
    // verify if we start to the left/bottom or to the right/top
    if ((vertical ? n.p.x() >= p.x() : n.p.y() >= p.y())) {
      if (n.lb != null) {
        pt = nearest(n.lb, p, !vertical);
      }
      if ((Math.pow((vertical ? p.x() : p.y()) - (vertical ? n.p.x() : n.p.y()), 2)
          < pt.distanceSquaredTo(p)) && n.rt != null) {
        // that means we can have a point at the other half of the plane that
        // can be nearer than the nearest point at this side of the plane
        Point2D rtPt = nearest(n.rt, p, !vertical);
        if (!rtPt.equals(n.p)) {
          if (rtPt.distanceSquaredTo(p) < pt.distanceSquaredTo(p)) {
            pt = rtPt;
          }
        }
      }
    } else {
      if (n.rt != null) {
        pt = nearest(n.rt, p, !vertical);
      }
      if ((Math.pow((vertical ? p.x() : p.y()) - (vertical ? n.p.x() : n.p.y()), 2) 
          < pt.distanceSquaredTo(p)) && n.lb != null) {
        // that means we can have a point at the other half of the plane that
        // can be nearer than the nearest point at this side of the plane
        Point2D lbPt = nearest(n.lb, p, !vertical);
        if (!lbPt.equals(n.p)) {
          if (lbPt.distanceSquaredTo(p) < pt.distanceSquaredTo(p)) {
            pt = lbPt;
          }
        }
      }
    }
    if (!pt.equals(n.p)) {
      if (pt.distanceSquaredTo(p) > n.p.distanceSquaredTo(p)) {
        pt = n.p;
      }
    }
    return pt;
  }

  /**
   * Main method for KdTree class. User for unit tests.
   * @param args command-line parameter
   */
  public static void main(String[] args) {
    
    System.out.println("\"Unit\" tests for kdTree");
    
    KdTree tree = new KdTree();
    Point2D p = new Point2D(0.7, 0.2);
    System.out.println("Testing if tree contains point p (should NOT contain): "
        + (!tree.contains(p) ? "[OK]" : "[FAIL]"));
    tree.insert(p);
    System.out.println("Testing if tree contains point p (should contain): "
        + (tree.contains(p) ? "[OK]" : "[FAIL]"));
    
//    tree.insert(new Point2D(0.7, 0.2));
    tree.insert(new Point2D(0.5, 0.4));
    tree.insert(new Point2D(0.2, 0.3));
    tree.insert(new Point2D(0.4, 0.7));
    tree.insert(new Point2D(0.9, 0.6));
//    tree.insert(new Point2D(0.4, 0.15));
    
    System.out.println("Testing if tree contains point p (should contain): "
        + (tree.contains(new Point2D(0.35, 0.05)) ? "[OK]" : "[FAIL]"));
    System.out.println("Testing if tree contains point p (should NOT contain): "
        + (!tree.contains(new Point2D(0.5, 0.1)) ? "[OK]" : "[FAIL]"));
    System.out.println("Testing if tree contains point p (should NOT contain): "
        + (!tree.contains(new Point2D(0.21, 0.1)) ? "[OK]" : "[FAIL]"));
    
    System.out.println("Drawing the points and the lines separating the rectangles");
    
    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 1);
    StdDraw.setYscale(0, 1);
    tree.draw();
    
    StdDraw.show();
    
    System.out.println("testing range method");
    System.out.println("Press \"ENTER\" to continue...");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();

    RectHV selection = new RectHV(0.28, 0.78, 0.72, 0.9);
    
    StdDraw.setPenColor(StdDraw.GREEN);
    StdDraw.setPenRadius();
    selection.draw();
    StdDraw.show();
    Queue<Point2D> result = (Queue<Point2D>) tree.range(selection);
    for (Point2D presult : result) {
      System.out.println(presult.toString());
    }

    Point2D nearestTest = new Point2D(0.2, 0.17);
    StdDraw.setPenColor(StdDraw.MAGENTA);
    StdDraw.setPenRadius(0.01);
    nearestTest.draw();
    StdDraw.show();
    
    System.out.println("testing nearest method");
    System.out.println("Press \"ENTER\" to continue...");
    scanner.nextLine();
    scanner.close();

    Point2D nearest = tree.nearest(nearestTest);
    System.out.println("nearest point" + nearest.toString());
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius();
    nearest.drawTo(nearestTest);
    StdDraw.show();

    System.out.println("Finished tests!");
    
  }

}
