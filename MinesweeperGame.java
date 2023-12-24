import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import javax.swing.*;

interface TileClickListener {
    void onLeftClick(MineTile tile);
    void onRightClick(MineTile tile);
}
//jbutton- to create clickable buttons
//mine tile is a specalized version of a button
class MineTile extends JButton {
    int r;
    int c;
    //tileclicklistener listener- here it indicates that an object of a class that implements tile click listener can be passed
    public MineTile(int r, int c, TileClickListener listener) {
        this.r = r;
        this.c = c;

        setFocusable(false); //no keyboard access
        setMargin(new Insets(0, 0, 0, 0)); //margin pixels-0
        setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
        //to handke the mouse actions
        addMouseListener(new MouseAdapter() {
            
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    listener.onLeftClick(MineTile.this);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    listener.onRightClick(MineTile.this);
                }
            }
        });
    }
}

abstract class GameBoard {
    protected int numRows;
    protected int numCols;
    protected MineTile[][] board; //holds objects of mine tile class
    protected ArrayList<MineTile> mineList;
    protected int mineCount;

    public GameBoard(int numRows, int numCols, int mineCount, TileClickListener listener) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.mineCount = mineCount;
        this.board = new MineTile[numRows][numCols];
        this.mineList = new ArrayList<>();
        initializeBoard(listener);
        setMines();
    }

    abstract void initializeBoard(TileClickListener listener);

    abstract void setMines();

    abstract MineTile getTile(int r, int c);

    abstract ArrayList<MineTile> getMineList();
}

class MinesweeperBoard extends GameBoard {
    public MinesweeperBoard(int numRows, int numCols, int mineCount, TileClickListener listener) {
        super(numRows, numCols, mineCount, listener);
    }

   
    void initializeBoard(TileClickListener listener) {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                board[r][c] = new MineTile(r, c, listener);
            }
        }
    }

 
    void setMines() {
        Random random = new Random();
        int mineLeft = mineCount;

        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft--;
            }
        }
    }


    MineTile getTile(int r, int c) {
        return board[r][c];
    }

    ArrayList<MineTile> getMineList() {
        return mineList;
    }
}

class MinesweeperGame implements TileClickListener {
    private int numRows;
    private int numCols;
    private int mineCount;
    private MinesweeperBoard board;
    private int tilesClicked;
    private boolean gameOver;
    private long startTime;
    private long endTime;

    private String playerName;

    private JFrame frame = new JFrame("Minesweeper");
    private JLabel textLabel = new JLabel(); //to show game related info

    private JPanel textPanel = new JPanel();//to contain the text label
    private JPanel boardPanel = new JPanel();//to hold mine tile buttons

    public MinesweeperGame(int numRows, int numCols, int mineCount) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.mineCount = mineCount;
        this.tilesClicked = 0;
        this.gameOver = false;

        playerName = JOptionPane.showInputDialog(frame, "Enter your name:", "Player Name", JOptionPane.INFORMATION_MESSAGE);
        //if player does not enter a name, make it anonymous
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Anonymous";
        }

        frame.setSize(numCols * 70, numRows * 70); //70 pixels
        frame.setLocationRelativeTo(null); //puts the game window at the center
        frame.setResizable(false); //game window can't be resized
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //program exits when game window is closed
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + mineCount);
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel);

        board = new MinesweeperBoard(numRows, numCols, mineCount, this);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                boardPanel.add(board.getTile(r, c));
            }
        }

        startTime = System.currentTimeMillis();

        frame.setVisible(true); //make the frame visible on the screen (by default it is invisible)
    }

    private void revealMines() {
        ArrayList<MineTile> mineList = board.getMineList();

        for (MineTile tile : mineList) {
            tile.setText("💣"); //sets the text of each mine tile to a bomb emoji
        }

        gameOver = true;
        textLabel.setText("Game Over!");

        endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        updateLeaderboard(playerName, timeTaken);
    }

    private void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols || !board.getTile(r, c).isEnabled()) {
            return;
        }

        MineTile tile = board.getTile(r, c);
        tile.setEnabled(false); //disables the current mine tile cause it has been checked
        tilesClicked++;

        int minesFound = countMine(r, c); //to check the number of mines adjacent to the current mine

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    checkMine(r + i, c + j);
                }
            }
        }

        if (tilesClicked == numRows * numCols - board.getMineList().size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
        }
    }

    private int countMine(int r, int c) {
        int minesFound = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                minesFound += (r + i >= 0 && r + i < numRows && c + j >= 0 && c + j < numCols
                        && board.getMineList().contains(board.getTile(r + i, c + j))) ? 1 : 0;
            }
        }

        return minesFound;
    }

    private void updateLeaderboard(String playerName, long timeTaken) {
        ArrayList<LeaderboardEntry> leaderboard = loadLeaderboard();
        leaderboard.add(new LeaderboardEntry(playerName, timeTaken));
        Collections.sort(leaderboard, Comparator.comparingLong(LeaderboardEntry::getTimeTaken).reversed());//
    
        StringBuilder leaderboardText = new StringBuilder("Leaderboard:\n");
        for (int i = 0; i < Math.min(leaderboard.size(), 5); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            leaderboardText.append(i + 1).append(". ").append(entry.getPlayerName()).append(": ")
                    .append(entry.getTimeTaken()).append("ms\n");
        }
    
        try (FileWriter writer = new FileWriter("leaderboard.txt")) {
            writer.write(leaderboardText.toString());
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    
        JOptionPane.showMessageDialog(frame, leaderboardText.toString(), "Leaderboard",
                JOptionPane.INFORMATION_MESSAGE);
    }
    


    private ArrayList<LeaderboardEntry> loadLeaderboard() {
        ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();
        leaderboard.add(new LeaderboardEntry("Rahul", 10000));
        leaderboard.add(new LeaderboardEntry("Lokesh", 11000));
        leaderboard.add(new LeaderboardEntry("Riyaz", 14500));
        return leaderboard;
    }

    private static class LeaderboardEntry {
        private String playerName;
        private long timeTaken;

        public LeaderboardEntry(String playerName, long timeTaken) {
            this.playerName = playerName;
            this.timeTaken = timeTaken;
        }

        public String getPlayerName() {
            return playerName;
        }

        public long getTimeTaken() {
            return timeTaken;
        }
    }

 
    public void onLeftClick(MineTile tile) {
        if (gameOver) {
            return;
        }

        if (board.getMineList().contains(tile)) {
            revealMines();
        } else {
            checkMine(tile.r, tile.c);
        }
    }


    public void onRightClick(MineTile tile) {
        if (gameOver) {
            return;
        }


        if (tile.getText().equals("")) {
            tile.setText("🚩");
        } else if (tile.getText().equals("🚩")) {
            tile.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MinesweeperGame(8, 8, 10)); //Event dispatch thread to call all the gui elements in the right order
    }
}
