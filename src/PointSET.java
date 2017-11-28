import java.util.ArrayList;
import java.util.Scanner;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {
  
  SET<Point2D> mSet;
  
  public PointSET() {
    // construct an empty set of points
    mSet = new SET<Point2D>();
  }

  public boolean isEmpty() {
    // is the set empty? 
    return mSet.isEmpty();
  }

  public int size() {
    // number of points in the set
    return mSet.size();
  }

  public void insert(Point2D p) {
    // add the point to the set (if it is not already in the set)
    if (p == null) {
      throw new java.lang.IllegalArgumentException("null argument received");
    }
    mSet.add(p);
  }

  public boolean contains(Point2D p) {
    // does the set contain point p?
    if (p == null) {
      throw new java.lang.IllegalArgumentException("null argument received");
    }
    return mSet.contains(p);
  }
  
  /**
   * Draw all points to standard draw. 
   */
  public void draw() {
    for (Point2D p : mSet) {
      p.draw();
    }
  }
  
  /**
   * All points that are inside the rectangle (or on the boundary).
   * @param rect the rectangle
   * @return an Iterable containing all points that are inside the rectangle
   */
  public Iterable<Point2D> range(RectHV rect) {
    if (rect == null) {
      throw new java.lang.IllegalArgumentException("null argument received");
    }
    Queue<Point2D> q = new Queue<Point2D>();
    for (Point2D p : mSet) {
      if (p.x() >= rect.xmin() && p.x() <= rect.xmax() && p.y() >= rect.ymin() 
          && p.y() <= rect.ymax()) {
        q.enqueue(p);
      }
    }
    return q;
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
    if (mSet.isEmpty()) {
      return null;
    }
    Point2D nearest = null;
    for (Point2D pt : mSet) {
      if (nearest == null) {
        nearest = pt;
      } else {
        if (nearest.distanceTo(p) > pt.distanceTo(p)) {
          nearest = pt;
        }
      }
    }
    return nearest;
  }

  /**
   * Main method for PointSET class. User for unit tests.
   * @param args command-line parameter
   */
  public static void main(String[] args) {
    // unit testing of the methods (optional)
    // testing range
    System.out.println("Testing Range method. Look at the StdDraw canvas and verify...");

    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 1);
    StdDraw.setYscale(0, 1);

    
    ArrayList<Point2D> points = new ArrayList<Point2D>();
    points.add(new Point2D(0.1, 0.1));
    points.add(new Point2D(0.15, 0.8));
    points.add(new Point2D(0.3, 0.5));
    points.add(new Point2D(0.7, 0.1));
    points.add(new Point2D(0.95, 0.8));
    points.add(new Point2D(0.3, 0.2));
    points.add(new Point2D(0.6, 0.5));
    points.add(new Point2D(0.2, 0.7));
    points.add(new Point2D(0.4, 0.9));
    points.add(new Point2D(0.7, 0.2));

    PointSET pset = new PointSET();
    for (Point2D p : points) {
      p.draw();
      pset.insert(p);
    }
    
    RectHV rect = new RectHV(0.1, 0.5, 0.3, 0.8);
    rect.draw();
    
    for (Point2D p : pset.range(rect)) {
      StdDraw.point(p.x(), p.y());
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.circle(p.x(), p.y(), 0.005);
      StdDraw.setPenColor(StdDraw.BLACK);
    }
    
    StdDraw.show();

    System.out.println("Press \"ENTER\" to continue...");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();
    
    // testing nearest
    StdDraw.clear();

    System.out.println("Testing nearest method. Look at the StdDraw canvas and verify...");
    for (Point2D p : points) {
      p.draw();
    }

    Point2D newP = new Point2D(0.2, 0.5);
    newP.draw();
    StdDraw.setPenColor(StdDraw.BLUE);
    StdDraw.circle(newP.x(), newP.y(), 0.005);
    StdDraw.setPenColor(StdDraw.BLACK);
    
    Point2D nearest = pset.nearest(newP);
    nearest.draw();
    nearest.drawTo(newP);
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.circle(nearest.x(), nearest.y(), 0.005);
    StdDraw.setPenColor(StdDraw.BLACK);
    
    StdDraw.show();
    
    // testing nearest to a empty set
    PointSET pset2 = new PointSET();
    Point2D nearest2 = pset2.nearest(newP);
    if (nearest2 == null) {
      System.out.println("Testing nearest to an empty set: [OK]");
    } else {
      System.out.println("Testing nearest to an empty set: [FAIL]");
    }
  }
}