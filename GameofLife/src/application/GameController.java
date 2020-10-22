package application;

public class GameController {

	public int[][] runGame(int[][] board) {
		boolean isTopOutlier = false;//Is there a new live cell outside the existing board in the top direction
		boolean isBottomOutlier = false;//Is there a new live cell outside the existing board in the bottom direction
		boolean isLeftOutlier = false;//Is there a new live cell outside the existing board in the left direction
		boolean isRightOutlier = false;//Is there a new live cell outside the existing board in the right direction
		int rowOffset = 0;//Total count of new rows we need
		int colOffset = 0;//Total count of new columns we need
		int[][] newBoard = new int[board.length][board[0].length];
		
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				int liveNeighbours = getTotalLiveNeighbours(r, c, board);
				if (board[r][c] == 1 && (liveNeighbours == 2 || liveNeighbours == 3)) {
					newBoard[r][c] = 1;
				} else if (board[r][c] == 0 && liveNeighbours == 3) {//Reproduction rule, new cells can be generated outside the board
					newBoard[r][c] = 1;
					if (r == 0 && !isTopOutlier) {
						isTopOutlier = true;//Found new live cell outside the board in the top direction
						rowOffset += 1;//Need one more row to place this new cell
					} else if (r == board.length - 1 && !isBottomOutlier) {
						isBottomOutlier = true;
						rowOffset += 1;
					} else if (c == 0 && !isLeftOutlier) {
						isLeftOutlier = true;
						colOffset += 1;
					} else if (c == board[r].length - 1 && !isRightOutlier) {
						isRightOutlier = true;
						colOffset += 1;
					}
				} else {
					newBoard[r][c] = 0;
				}
			}
		}

		//If there is a new live cell outside the existing board we need a larger board (extended board)
		if (isTopOutlier && !isLeftOutlier) {
			newBoard = buildExtendedBoard(1, 0, rowOffset, colOffset, newBoard);
		} else if (isLeftOutlier && !isTopOutlier) {
			newBoard = buildExtendedBoard(0, 1, rowOffset, colOffset, newBoard);
		} else if (isTopOutlier && isLeftOutlier) {
			newBoard = buildExtendedBoard(1, 1, rowOffset, colOffset, newBoard);
		} else if (isBottomOutlier || isRightOutlier) {
			newBoard = buildExtendedBoard(0, 0, rowOffset, colOffset, newBoard);
		}
		return newBoard;
	}

	private int getTotalLiveNeighbours(int r, int c, int[][] board) {
		int liveNeighbours = 0;
		liveNeighbours += getNodeState(r - 1, c, board);
		liveNeighbours += getNodeState(r - 1, c + 1, board);
		liveNeighbours += getNodeState(r, c + 1, board);
		liveNeighbours += getNodeState(r + 1, c + 1, board);
		liveNeighbours += getNodeState(r + 1, c, board);
		liveNeighbours += getNodeState(r + 1, c - 1, board);
		liveNeighbours += getNodeState(r, c - 1, board);
		liveNeighbours += getNodeState(r - 1, c - 1, board);
		return liveNeighbours;
	}

	private int getNodeState(int r, int c, int[][] board) {
		if (r < 0 || r >= board.length) {
			return 0;
		} else if (c < 0 || c >= board[0].length) {
			return 0;
		} else if (board[r][c] == 1) {
			return 1;
		}
		return 0;
	}

	private int[][] buildExtendedBoard(int rowAdd, int colAdd, int rowOffset, int colOffset, int[][] board) {
		int[][] extendedBoard = new int[board.length + rowOffset][board[0].length + colOffset];

		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				extendedBoard[r + rowAdd][c + colAdd] = board[r][c];
			}
		}
		return extendedBoard;
	}
}
