# Minesweeper Game Project

## Problem Statement

The objective of this project is to build a Minesweeper game using Object-Oriented Programming concepts. The game is designed to be interactive, involving tiles and mines.

## Problem Description

In the game, a regular tile (without a mine) reveals numbers indicating the number of mines in its neighborhood. Clicking on a mine reveals all mines, ending the game. A leaderboard, stored in a text file, displays the top five players.

### TileClickListener Interface

Responsible for handling left and right click events on MineTiles.

### MineTile Class

- Represents a clickable button on the game board.
- Specialized version of JButton.

### GameBoard Abstract Class

- Abstract class representing the game board.
- Contains common attributes and methods for different types of game boards.

### MinesweeperBoard Class (extends GameBoard)

- Concrete implementation of the game board for Minesweeper.
- Initializes the board, places mines, and provides access to tiles and mine list.

### MinesweeperGame Class (implements TileClickListener)

- Orchestrates the Minesweeper game.
- Manages the game window, user interactions, and game logic.
- Uses instances of MinesweeperBoard and interacts with MineTiles.

### LeaderboardEntry Class

- Nested class within MinesweeperGame for representing entries in the leaderboard.
- Stores player names and their completion times.
