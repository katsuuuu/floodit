import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// use tester.jar to test methods
import tester.*;

import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// store arraylist functions
class ArrayUtils {

  // creates a 2D arraylist of cells of the given size
  ArrayList<ArrayList<Cell>> buildgame(int size, Random newrand) {
    int i;
    ArrayList<ArrayList<Cell>> celllist = new ArrayList<ArrayList<Cell>>(size);

    for (i = 0; i < size; i++) {

      celllist.add(this.buildlist(size, i, newrand));
    }
    return celllist;
  } 

  // creates a new random row of Cells
  ArrayList<Cell> buildlist(int size, int row, Random newrand) {
    int cellsize = 50;

    ArrayList<Cell> x = new ArrayList<Cell>();
    int i;

    for (i = 0; i < size; i++) {
      x.add(new Cell(i * cellsize + 75, row * cellsize + 75, newrand));
    }
    return x;
  }

  // EFFECT: goes through the 2D arraylist "connecting" all of the links
  public void linkall(ArrayList<ArrayList<Cell>> array) {
    ArrayUtils au = new ArrayUtils();
    int i;
    int j;
    // goes through linking all rows left to right
    for (i = 0; i < array.size(); i++) {
      au.link(array.get(i));
    }
    // links all rows top to bottom
    for (j = 0; j < array.size() - 1; j++) {
      au.link(array.get(j), array.get(j + 1));
    }

  }

  // EFFECT: goes through the 2D arraylist of cells "connecting" left to right
  public void link(ArrayList<Cell> row) {
    int i;
    for (i = 1; i < row.size(); i++) {
      row.get(i).left = row.get(i - 1);
      row.get(i - 1).right = row.get(i);
    }

  }

  // EFFECT goes through the two given arraylist of cells "conecting" upper and
  // lower links
  public void link(ArrayList<Cell> toprow, ArrayList<Cell> bottomrow) {
    int i;
    for (i = 0; i < toprow.size(); i++) {

      toprow.get(i).bottom = bottomrow.get(i);
      bottomrow.get(i).top = toprow.get(i);
    } 
  }

  // returns the color of the cell that is in that space given the posn
  public Color getColor(Posn posn, ArrayList<ArrayList<Cell>> board) {
    int i;
    int j;
    int range = 24;
    Color result = board.get(0).get(0).color;
    for (i = 0; i < board.size(); i++) {

      for (j = 0; j < board.size(); j++) {
        int lowx = board.get(i).get(j).x - range;
        int highx = board.get(i).get(j).x + range;
        int lowy = board.get(i).get(j).y - range;
        int highy = board.get(i).get(j).y + range;
        int cellx = posn.x;
        int celly = posn.y;
        if (cellx < highx && cellx > lowx && celly < highy && celly > lowy) {
          result = board.get(i).get(j).color;
          return result;
        }

      }
    }
    return result;
  } 
  

  //EFFECT: goes through the board flooding the cells based on neighbors
  public void flood(ArrayList<ArrayList<Cell>> board) {
    int i;
    int j;
    board.get(0).get(0).flooded = true;
    Color color = board.get(0).get(0).color;

    // floods the top first
    for (i = 1; i < board.size(); i++) {
      board.get(0).get(i).flood(true, false);
    }
    // flood the left edge
    for (i = 1; i < board.size(); i++) {
      board.get(i).get(0).flood(false, true);
    }

    // flood the middle
    for (i = 1; i < board.size(); i++) {
      for (j = 1; j < board.size(); j++) {
        board.get(i).get(j).flood(false, false);
      }
    }

    // changes colors
    for (i = 0; i < board.size(); i++) {
      for (j = 0; j < board.size(); j++) {
        if (board.get(i).get(j).flooded) {
          board.get(i).get(j).color = color;
        }
      }
    }

  } 

  // determines whether the board's cell contains all the same color
  boolean fullboard(ArrayList<ArrayList<Cell>> board) {
    int i;
    int j;
    boolean isfull = true;
    for (i = 1; i < board.size(); i++) {
      for (j = 0; j < board.size(); j++) {

        if (!board.get(i).get(j).color.equals(board.get(i - 1).get(j).color)) {
          isfull = false;
        }

      }
    }
    return isfull;
  }

} 

// represents a single square of the game 
class Cell {

  // avaliable number of colors
  final int NUM_COLORS = 8;
  final int CELL_SIZE = 50;

  // list of usable colors
  List<Color> colors = Arrays.asList(Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.YELLOW,
      Color.DARK_GRAY, Color.BLACK, Color.MAGENTA);

  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // constructor given coordinates only
  Cell(int x, int y) {
    this.x = x;
    this.y = y;
    this.color = colors.get(new Random().nextInt(NUM_COLORS)); 
    this.flooded = false;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // seeded constructor given coordinates only 
  // (seeded constructor is for the test purposes)
  Cell(int x, int y, Random rand) {
    this.x = x;
    this.y = y;
    this.color = colors.get(rand.nextInt(NUM_COLORS)); /
    this.flooded = false;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // constructor given coordinates and color (mostly for testing purposes)
  Cell(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color; 
    this.flooded = false;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // draws this cell
  WorldImage cellImage(int size) {
    return new RectangleImage(size, size, OutlineMode.SOLID, this.color);
  }

  // EFFECT:
  // floods this cell based on its neighbors
  public void flood(boolean top, boolean edge) {

    if (top) { // if this cell is in the top row
      if (this.left.flooded && this.left.color.equals(this.color)) {
        this.flooded = true;
      }

      if (this.bottom.flooded && this.bottom.color.equals(this.color)) {
        this.flooded = true;
      }

      if (this.flooded && this.left.color.equals(this.color)) {
        this.left.flooded = true;
      }
      if (this.flooded && this.bottom.color.equals(this.color)) {
        this.bottom.flooded = true;
      }
    } 

    // if cell is on the left edge and not on the top row
    if (edge && !top) {
      if (this.top.flooded && this.top.color.equals(this.color)) {
        this.flooded = true;
      }
      if (this.flooded && this.top.color.equals(this.color)) {
        this.top.flooded = true;
      }
      if (this.flooded && this.right.color.equals(this.color)) {
        this.right.flooded = true;
      }
    } 

    // if cell is in the middle portion of board
    if (!top && !edge) {
      if (this.left.flooded && this.left.color.equals(this.color)) {
        this.flooded = true;
      }
      if (this.top.flooded && this.top.color.equals(this.color)) {
        this.flooded = true;
      }
      if (this.flooded && this.left.color.equals(this.color)) {
        this.left.flooded = true;
      }
      if (this.flooded && this.top.color.equals(this.color)) {
        this.top.flooded = true;
      }

    } 
  }

} 



// holds the world of the floodit game
class FloodItWorld extends World {

  ArrayList<ArrayList<Cell>> board;
  int size;
  int turn;
  int maxturn;
  Random rand = new Random();

  public FloodItWorld(ArrayList<ArrayList<Cell>> board, int size, int turn) {
    this.board = new ArrayUtils().buildgame(size, this.rand);
    this.size = size;
    this.turn = turn;
    // sets the max number of turns based on the size of board
    if (this.size >= 7) {
      this.maxturn = this.size + 19;
    }
    if (this.size < 7) {
      if (this.size % 2 == 0) {
        this.maxturn = this.size / 2 + 12;
      }
      else {
        this.maxturn = (this.size - 1) / 2 + 12;
      }
    }
  }

  // seeded constructor
  public FloodItWorld(ArrayList<ArrayList<Cell>> board, int size, int turn, Random rand) {
    this.board = new ArrayUtils().buildgame(size, rand);
    this.size = size;
    this.turn = turn;
    this.rand = rand;
    // sets the max number of turns based on the size of board
    if (this.size >= 7) {
      this.maxturn = this.size + 19;
    }
    if (this.size < 7) {
      if (this.size % 2 == 0) {
        this.maxturn = this.size / 2 + 12;
      }
      else {
        this.maxturn = (this.size - 1) / 2 + 12;
      }
    }

  }

  // draws the board from the given 2D arraylist of cells
  public WorldScene drawAllCells(ArrayList<ArrayList<Cell>> cells) {
    WorldScene bg = this.getEmptyScene();
    int cellsize = 50;
    int i;
    int j;
    for (i = 0; i < cells.size(); i++) {
      for (j = 0; j < cells.size(); j++) {
        bg.placeImageXY(cells.get(i).get(j).cellImage(cellsize), cells.get(i).get(j).x,
            cells.get(i).get(j).y);
      }
    }
    return bg;
  }

  // draws the final scene if the player lost or won
  public WorldScene endscene(WorldScene board, boolean won) {
    TextImage youlost = new TextImage("YOU RAN OUT OF TURNS", 24, FontStyle.BOLD, Color.RED);
    TextImage youwon = new TextImage("YOU WON", 24, FontStyle.BOLD, Color.GREEN);

    if (won) {
      board.placeImageXY(youwon, board.height / 2, board.height / 2);
    }

    if (!won) {
      board.placeImageXY(youlost, board.height / 2, board.height / 2);
    }
    return board;
  } 

  // returns the final scene if the player won or lost
  public WorldEnd worldEnds() {
    ArrayUtils au = new ArrayUtils();

    if (au.fullboard(this.board)) {
      return new WorldEnd(true, this.endscene(this.drawAllCells(this.board), true));
    }
    else if (this.turn == this.maxturn) {
      return new WorldEnd(true, this.endscene(this.drawAllCells(this.board), false));
    }

    else {
      return new WorldEnd(false, this.drawAllCells(board));
    }
  }

  // draws the world
  public WorldScene makeScene() {
    return this.drawAllCells(this.board);
  }

  // on tick floods the board and links all of the cells
  public void onTick() {
    ArrayUtils au = new ArrayUtils();
    au.linkall(this.board);
    au.flood(this.board);

  } 

  // EFFECT: changes the color of the top left cell to that of the color of the
  // cell clicked
  public void onMouseClicked(Posn posn) {
    ArrayUtils au = new ArrayUtils();

    // if the click makes colors change it counts as a turn
    if (!au.getColor(posn, this.board).equals(board.get(0).get(0).color)) {
      this.turn = this.turn + 1;
    }

    board.get(0).get(0).color = au.getColor(posn, this.board);

  } 

  // EFFECT : creates a new game if the pressed key is r
  // resets turn number and creates a new board
  public void onKeyEvent(String k) {
    if (k.equals("r")) {
      board = new ArrayUtils().buildgame(size, this.rand);
      turn = 0;
    }
  }

} 

// examples and tests
class Examplesflood {

  ArrayUtils au = new ArrayUtils();

  // example cells
  Cell bluecell = new Cell(75, 75, Color.BLUE); // in row 1
  Cell redcell = new Cell(125, 75, Color.RED); // in row 1
  ArrayList<Cell> drawallr1 = new ArrayList<Cell>();
  Cell bluecell2 = new Cell(125, 125, Color.BLUE); // in row 2
  Cell redcell2 = new Cell(75, 125, Color.RED); // in row 2
  ArrayList<Cell> drawallr2 = new ArrayList<Cell>();

  // another example cells (with the seeded random)
  Cell bluecell3 = new Cell(75, 75, new Random(8)); // in row 1
  Cell redcell3 = new Cell(125, 75, new Random(8)); // in row 1
  ArrayList<Cell> drawallr3 = new ArrayList<Cell>();
  Cell bluecell4 = new Cell(125, 125, new Random(8)); // in row 2
  Cell redcell4 = new Cell(75, 125, new Random(8)); // in row 2
  ArrayList<Cell> drawallr4 = new ArrayList<Cell>();

  ArrayList<ArrayList<Cell>> drawalltester0 = new ArrayList<ArrayList<Cell>>();
  FloodItWorld testworld0 = new FloodItWorld(new ArrayUtils().buildgame(2, new Random(8)), 2, 0,
      new Random(8));
  WorldScene testbg0 = testworld0.getEmptyScene();

  void init() {
    
    drawallr3.add(bluecell3);
    drawallr3.add(redcell3);
    drawallr4.add(redcell4);
    drawallr4.add(bluecell4);
    drawalltester0.add(drawallr3);
    drawalltester0.add(drawallr4);
    // ^created 2d array should be blue red top row and red blue bottom row
    FloodItWorld testworld0 = new FloodItWorld(new ArrayUtils().buildgame(2, new Random(8)), 2, 0,
        new Random(20));
    WorldScene testbg0 = testworld0.getEmptyScene();
    testbg0.placeImageXY(bluecell3.cellImage(50), bluecell3.x, bluecell3.y);
    testbg0.placeImageXY(bluecell4.cellImage(50), bluecell4.x, bluecell4.y);
    testbg0.placeImageXY(redcell3.cellImage(50), redcell3.x, redcell3.y);
    testbg0.placeImageXY(redcell4.cellImage(50), redcell4.x, redcell4.y);

  }

  void testFloodWorld(Tester t) {
    // test build list
    ArrayList<Cell> buildlisttest = au.buildlist(3, 0, new Random(8));
    ArrayList<Cell> buildlisttest2 = au.buildlist(4, 1, new Random(8));
    t.checkExpect(buildlisttest.size(), 3);
    t.checkExpect(buildlisttest2.size(), 4); // size of newly created rows are the given input

    // test build game
    ArrayList<ArrayList<Cell>> testbuildgame = au.buildgame(2, new Random(8));
    ArrayList<ArrayList<Cell>> testbuildgame2 = au.buildgame(4, new Random(8));
    t.checkExpect(testbuildgame.size(), 2); // size of newly created 2d arraylist is given input
    t.checkExpect(testbuildgame2.size(), 4); // size of newly created 2d arraylist is given input

    // test cellImage
    t.checkExpect(bluecell.cellImage(50),
        new RectangleImage(50, 50, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(redcell.cellImage(25), new RectangleImage(25, 25, OutlineMode.SOLID, Color.RED));

    // test drawall
    ArrayList<ArrayList<Cell>> drawalltester = new ArrayList<ArrayList<Cell>>(); // creates new 2d
    // array
    drawallr1.add(bluecell);
    drawallr1.add(redcell);
    drawallr2.add(redcell2);
    drawallr2.add(bluecell2);
    drawalltester.add(drawallr1);
    drawalltester.add(drawallr2);
    // ^created 2d array should be blue red top row and red blue bottom row
    FloodItWorld testworld = new FloodItWorld(new ArrayUtils().buildgame(2, new Random(8)), 2, 0,
        new Random(8));
    WorldScene testbg = testworld.getEmptyScene();
    testbg.placeImageXY(bluecell.cellImage(50), bluecell.x, bluecell.y);
    testbg.placeImageXY(bluecell2.cellImage(50), bluecell2.x, bluecell2.y);
    testbg.placeImageXY(redcell.cellImage(50), redcell.x, redcell.y);
    testbg.placeImageXY(redcell2.cellImage(50), redcell2.x, redcell2.y);
    t.checkExpect(testworld.drawAllCells(drawalltester), testbg);

    // testing link (connects left to right)
    // makes a new ArrayList of cells that has 4 cells in it
    ArrayList<Cell> testrow = new ArrayUtils().buildlist(4, 0, new Random(8)); 
    au.link(testrow); // connect all of the cells
    t.checkExpect(testrow.get(0).left, null); // the left of the first cell should still be null
    t.checkExpect(testrow.get(1).left, testrow.get(0)); // the left of the 2nd cell is the 1st
    t.checkExpect(testrow.get(2).left, testrow.get(1)); // the left of the 3rd cell is the second
    t.checkExpect(testrow.get(3).left, testrow.get(2)); // the left of the 4th cell is the 3rd
    t.checkExpect(testrow.get(0).right, testrow.get(1)); // the right of the first cell should be
    // the 2nd cell
    t.checkExpect(testrow.get(1).right, testrow.get(2)); // the right of the 2nd cell is the 3rd
    t.checkExpect(testrow.get(2).right, testrow.get(3)); // the right of the 3rd cell is the 4th
    t.checkExpect(testrow.get(3).right, null); // the right of the 4th cell is null

    // testing link (connects top to bottom)
    // TOP AND BOTTOM ARE ON THE EDGE SO TOP'S TOP AND BOTTOM'S BOTTOM LINKS SHOULD
    // BE NULL
    ArrayList<Cell> testrowtop = new ArrayUtils().buildlist(2, 0, new Random(8)); 
    
    ArrayList<Cell> testrowmid = new ArrayUtils().buildlist(2, 1, new Random(8)); 
    ArrayList<Cell> testrowbottom = new ArrayUtils().buildlist(2, 2, new Random(8)); 

    au.link(testrowtop, testrowmid); // connect top to mid
    au.link(testrowmid, testrowbottom); // connect mid to bottom
    t.checkExpect(testrowtop.get(0).bottom, testrowmid.get(0)); // TESTING LINKS TOP TO BOTTOM
    t.checkExpect(testrowtop.get(1).bottom, testrowmid.get(1));
    t.checkExpect(testrowmid.get(0).bottom, testrowbottom.get(0));
    t.checkExpect(testrowmid.get(1).bottom, testrowbottom.get(1));
    t.checkExpect(testrowbottom.get(0).bottom, null);
    t.checkExpect(testrowbottom.get(1).bottom, null);
    t.checkExpect(testrowbottom.get(0).top, testrowmid.get(0)); // TESTING LINKS BOTTOM TO TOP
    t.checkExpect(testrowbottom.get(1).top, testrowmid.get(1));
    t.checkExpect(testrowmid.get(0).top, testrowtop.get(0));
    t.checkExpect(testrowmid.get(1).top, testrowtop.get(1));
    t.checkExpect(testrowtop.get(0).top, null);
    t.checkExpect(testrowtop.get(1).top, null);

    // testing linkall on a 3x3 2d array
    
    ArrayList<Cell> testlinkalltop = new ArrayUtils().buildlist(3, 0, new Random(8)); 
    ArrayList<Cell> testlinkallmid = new ArrayUtils().buildlist(3, 1, new Random(8)); 
    ArrayList<Cell> testlinkallbottom = new ArrayUtils().buildlist(3, 2, new Random(8)); 
    ArrayList<ArrayList<Cell>> linkalltester = new ArrayList<ArrayList<Cell>>(); // creates new 2d
    // array
    linkalltester.add(testlinkalltop);
    linkalltester.add(testlinkallmid);
    linkalltester.add(testlinkallbottom); // add rows to array
    au.linkall(linkalltester); // connect all the cells
    // TESTS LEFT TO RIGHT LINKS FOR TOP
    t.checkExpect(testlinkalltop.get(0).left, null); // the left of the first cell should still be
    // null
    t.checkExpect(testlinkalltop.get(1).left, testlinkalltop.get(0)); // the left of the 2nd cell is
    // the 1st
    t.checkExpect(testlinkalltop.get(2).left, testlinkalltop.get(1)); // the left of the 3rd cell is
    // the 2nd
    t.checkExpect(testlinkalltop.get(0).right, testlinkalltop.get(1)); // the right of the first
    // cell is the 2nd
    t.checkExpect(testlinkalltop.get(1).right, testlinkalltop.get(2)); // the right of the 2nd cell
    // is the 3rd
    t.checkExpect(testlinkalltop.get(2).right, null); // the right of the 3rd cell is the 4th
    // TESTS LEFT AND RIGHT LINKS FOR MIDDLE ROW
    t.checkExpect(testlinkallmid.get(0).left, null); // the left of the first cell should still be
    // null
    t.checkExpect(testlinkallmid.get(1).left, testlinkallmid.get(0)); // the left of the 2nd cell is
    // the 1st
    t.checkExpect(testlinkallmid.get(2).left, testlinkallmid.get(1)); // the left of the 3rd cell is
    // the second
    t.checkExpect(testlinkallmid.get(0).right, testlinkallmid.get(1)); // the right of the first
    // cell should be the 2nd cell
    t.checkExpect(testlinkallmid.get(1).right, testlinkallmid.get(2)); // the right of the 2nd cell
    // is the 3rd
    t.checkExpect(testlinkallmid.get(2).right, null); // the right of the 3rd cell is the 4th
    // TEST LEFT AND RIGHT LINKS FOR BOTTOM
    t.checkExpect(testlinkallbottom.get(0).left, null); // the left of the first cell should still
    // be null
    t.checkExpect(testlinkallbottom.get(1).left, testlinkallbottom.get(0)); // the left of the 2nd
    // cell is the 1st
    t.checkExpect(testlinkallbottom.get(2).left, testlinkallbottom.get(1)); // the left of the 3rd
    // is the 2nd
    t.checkExpect(testlinkallbottom.get(0).right, testlinkallbottom.get(1)); // the right of the 1st
    // is the 2nd
    t.checkExpect(testlinkallbottom.get(1).right, testlinkallbottom.get(2)); // the right of the 2nd
    // is the 3rd
    t.checkExpect(testlinkallbottom.get(2).right, null); // the right of the 3rd cell is null
    // TESTS TOP AND BOTTOM LINKS FOR TOP ROW
    t.checkExpect(testlinkalltop.get(0).bottom, testlinkallmid.get(0));
    t.checkExpect(testlinkalltop.get(1).bottom, testlinkallmid.get(1));
    t.checkExpect(testlinkalltop.get(2).bottom, testlinkallmid.get(2));
    t.checkExpect(testlinkalltop.get(0).top, null);
    t.checkExpect(testlinkalltop.get(1).top, null);
    t.checkExpect(testlinkalltop.get(2).top, null);
    // TESTS TOP AND BOTTOM LINKS FOR MIDDLE ROW
    t.checkExpect(testlinkallmid.get(0).top, testlinkalltop.get(0));
    t.checkExpect(testlinkallmid.get(1).top, testlinkalltop.get(1));
    t.checkExpect(testlinkallmid.get(2).top, testlinkalltop.get(2));
    t.checkExpect(testlinkallmid.get(0).bottom, testlinkallbottom.get(0));
    t.checkExpect(testlinkallmid.get(1).bottom, testlinkallbottom.get(1));
    t.checkExpect(testlinkallmid.get(2).bottom, testlinkallbottom.get(2));
    // TESTS ALL TOP AND BOTTOM LINKS FOR BOTTOM ROW
    t.checkExpect(testlinkallbottom.get(0).top, testlinkallmid.get(0));
    t.checkExpect(testlinkallbottom.get(1).top, testlinkallmid.get(1));
    t.checkExpect(testlinkallbottom.get(2).top, testlinkallmid.get(2));
    t.checkExpect(testlinkallbottom.get(0).bottom, null);
    t.checkExpect(testlinkallbottom.get(1).bottom, null);
    t.checkExpect(testlinkallbottom.get(2).bottom, null);

    // tests fullboard()
    ArrayList<ArrayList<Cell>> isfulltester = new ArrayList<ArrayList<Cell>>(); // creates new 2d
    ArrayList<Cell> isfullr1 = new ArrayList<Cell>();
    ArrayList<Cell> isfullr2 = new ArrayList<Cell>();
    isfullr1.add(bluecell);
    isfullr1.add(bluecell);
    isfullr2.add(redcell);
    isfullr2.add(bluecell);
    isfullr2.add(bluecell);
    isfulltester.add(isfullr1);
    isfulltester.add(isfullr1); /// right now this is a 2x2 full of blue cells
    t.checkExpect(au.fullboard(isfulltester), true);
    isfullr1.add(bluecell);
    isfulltester.add(isfullr2); // add a row with a red cell in it
    //this is now a 3x3 with a single red cell in it
    t.checkExpect(au.fullboard(isfulltester), false);
    //a one celled board will always be all the same color
    t.checkExpect(au.fullboard(au.buildgame(1, new Random(8))), true);
    //it is EXTREMELY unlikely that a big board will be generated all same color
    t.checkExpect(au.fullboard(au.buildgame(10, new Random(8))), false);

    ///////// PUT ANY NEW TESTS HERE

    // Tests the onMouseClick method
    testworld.onMouseClicked(new Posn(5, 5));
    t.checkExpect(testworld.board.get(0).get(0).color, Color.DARK_GRAY);
    testworld.onMouseClicked(new Posn(0, 0));
    t.checkExpect(testworld.board.get(0).get(0).color, Color.DARK_GRAY);

  } 

  // Tests the onMouseClick method
  // Can be tested inside the big test as well, this also test init data
  void testGetColor(Tester t) {
    this.init();
    au.getColor(new Posn(10, 20), drawalltester0);
    t.checkExpect(au.getColor(new Posn(10, 20), drawalltester0), Color.DARK_GRAY);
    au.getColor(new Posn(200, 500), drawalltester0);
    t.checkExpect(au.getColor(new Posn(10, 20), drawalltester0), Color.DARK_GRAY);
    au.getColor(new Posn(200, 110), drawalltester0);
    t.checkExpect(au.getColor(new Posn(10, 20), drawalltester0), Color.DARK_GRAY);
  }

  void testCellFlood(Tester t) {
    this.init();
    t.checkExpect(bluecell3.flooded, false);
    t.checkExpect(redcell3.flooded, false);
    t.checkExpect(redcell4.flooded, false);
    t.checkExpect(bluecell4.flooded, false);

    bluecell3.flooded = true;
    redcell3.flooded = true;
    redcell4.flooded = true;
    bluecell4.flooded = true;

    t.checkExpect(bluecell3.flooded, true);
    t.checkExpect(redcell3.flooded, true);
    t.checkExpect(redcell4.flooded, true);
    t.checkExpect(bluecell4.flooded, true);
  }

  void testAuFlood(Tester t) {
    this.init();
    t.checkExpect(testworld0.board.get(0).get(0).flooded, false);
    t.checkExpect(testworld0.board.get(0).get(1).flooded, false);
    t.checkExpect(testworld0.board.get(1).get(0).flooded, false);
    t.checkExpect(testworld0.board.get(1).get(1).flooded, false);

    testworld0.board.get(0).get(0).flooded = true;
    testworld0.board.get(0).get(1).flooded = true;
    testworld0.board.get(1).get(0).flooded = true;
    testworld0.board.get(1).get(1).flooded = true;

    t.checkExpect(testworld0.board.get(0).get(0).flooded, true);
    t.checkExpect(testworld0.board.get(0).get(1).flooded, true);
    t.checkExpect(testworld0.board.get(1).get(0).flooded, true);
    t.checkExpect(testworld0.board.get(1).get(1).flooded, true);
  }

  void testOnKeyEvent(Tester t) {
    this.init();
    testworld0.turn = 10;
    testworld0.onKeyEvent("w");
    t.checkExpect(testworld0.turn, 10);
    testworld0.onKeyEvent(" ");
    t.checkExpect(testworld0.turn, 10);
    testworld0.onKeyEvent("r");
    t.checkExpect(testworld0.turn, 0);
  }

  // LAUNCH THE GAME

  void testFloodWorld2(Tester t) {
    // size of the square 2d array
    int SIZE = 7;

    int height = SIZE * 70;

    FloodItWorld w = new FloodItWorld(new ArrayUtils().buildgame(SIZE, new Random(8)), SIZE, 0);
    w.bigBang(height, height, 0.1);

  }

} 
