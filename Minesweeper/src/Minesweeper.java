import  java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class Minesweeper {
    private class MineTile extends JButton{
        int r;
        int c;

        public MineTile(int r, int c){
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 70;
    int numRows = 8;
    int numCols = numRows;
    int boardWidth = numCols * tileSize; //8*70=560pixels
    int boardHeight = numRows * tileSize;


    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    int mineCount = 7;
    //2D array of class MineTile which extends JButton class
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;

    //constructor
    Minesweeper(){
        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel);
        //instead of using Jbutton for every tile, use a class that inherits Jbutton
        //need to add extra properties to JButton
        for(int r=0; r<numRows; r++){
            for(int c=0; c<numCols; c++){
                MineTile tile = new MineTile(r,c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0,0,0,0));
                //Arial Unicode so emojis can be displayed
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                tile.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        if (gameOver){
                            return;
                        }
                        MineTile tile = (MineTile)e.getSource();

                        //left click check
                        if(e.getButton()==MouseEvent.BUTTON1){
                            if(tile.getText()==""){
                                if(mineList.contains(tile)){
                                    revealMines();
                                }
                                else{
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        //right click
                        else if(e.getButton() == MouseEvent.BUTTON3){
                            if(tile.getText()=="" && tile.isEnabled()){
                                tile.setText("🚩");
                            }
                            else if(tile.getText()== "🚩"){
                                tile.setText("");
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }
        frame.setVisible(true);

        setMines();
        }

        void setMines(){
            mineList = new ArrayList<MineTile>();
            int mineLeft = mineCount;
            while(mineLeft > 0){
                int r = random.nextInt(numRows);
                int c = random.nextInt(numCols);

                //to make sure each mine is in a different position
                MineTile tile = board[r][c];
                if(!mineList.contains(tile)){
                    mineList.add(tile);
                    mineLeft -= 1;
                }

            }
        }
        void revealMines(){
            for(int i=0; i<mineList.size(); i++){
                MineTile tile = mineList.get(i);
                tile.setText("💣");
            }

            gameOver = true;
            textLabel.setText("Game Over!");
        }
        void checkMine(int r, int c){
            if(r<0 || r>=numRows || c<0 || c>=numCols){
                return;
           }
            MineTile tile = board[r][c];
            if(!tile.isEnabled()){
                return;
            }
            tile.setEnabled(false);
            tilesClicked += 1;

            int minesFound = 0;

            //top neighbors
            minesFound += countMine(r-1, c-1);  //top left
            minesFound += countMine(r-1, c);    //top
            minesFound += countMine(r-1, c+1);  //top right

            //left and right
            minesFound += countMine(r, c-1);
            minesFound += countMine(r, c+1);

            //bottom 3
            minesFound += countMine(r+1, c-1);  //bottom left
            minesFound += countMine(r+1, c);    //bottom
            minesFound += countMine(r+1, c+1);  //bottom right

            if(minesFound > 0){
                tile.setText(Integer.toString(minesFound)); //displays number of mines in area
            }
            else{
                tile.setText("");
                
                //top 3
                checkMine(r-1, c-1);
                checkMine(r-1, c);
                checkMine(r-1, c+1);

                //left and right
                checkMine(r, c-1);
                checkMine(r, c+1);

                //bottom 3
                checkMine(r+1, c-1);
                checkMine(r+1, c);
                checkMine(r+1, c+1);

            }

            if (tilesClicked == numRows*numCols - mineList.size()){
                gameOver = true;
                textLabel.setText("Mines Cleared!");
            }
        }
        int countMine(int r, int c){
           //check if tile neighbors are within bounds of board
            if(r<0 || r>=numRows || c<0 || c>=numCols){
                return 0;
           }
           if (mineList.contains(board[r][c])){
            return 1;
           }
           return 0;
        }
}
