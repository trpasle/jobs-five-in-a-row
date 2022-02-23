package cz.ondrejguth.cz.jobs.piskvorky.player;

//TODO
public class FiveInARowMinmaxPlayer {
    public static final int NEEDED_FOR_WIN = 5;
    public static final byte WIN_SCORE = 10;
    public enum CellState {
        EMPTY,
        O,
        X
    }

    public enum Direction {
        RIGHT(1, 0),
        DIAGONAL_DOWN_RIGHT(1,1),
        DOWN(0,1),
        DIAGONAL_DOWN_LEFT(-1,1);
        public final int incI, incJ;
        Direction(int i, int j) {
            incI = i; incJ = j;
        }
    }

    private byte howManyInRow(CellState [][] board, int iFrom, int jFrom, CellState who, Direction direction) {
        int i = iFrom+direction.incI;
        int j = jFrom+direction.incJ;
        if (board[i][j] == who)
            return (byte)(1 + howManyInRow(board, i, j, who, direction));
        else
            return 0;
    }

    private byte evaluate(CellState [][] board, CellState me, int iFrom, int jFrom) {
        int maxOuter = board.length;
        int maxInner = board[0].length;

        for (int i = iFrom; i < board.length; i++) {
            for (int j = jFrom; j < board[i].length; j++) {
                if (board[i][j] != CellState.EMPTY) {
                    byte right = howManyInRow(board, i, j, board[i][j], Direction.RIGHT);
                    byte down = howManyInRow(board, i, j, board[i][j], Direction.DOWN);
                    byte downRight = howManyInRow(board, i, j, board[i][j], Direction.DIAGONAL_DOWN_RIGHT);
                    byte downLeft = howManyInRow(board, i, j, board[i][j], Direction.DIAGONAL_DOWN_LEFT);
                    if (right >= NEEDED_FOR_WIN-1 || downRight >= NEEDED_FOR_WIN-1 || down >= NEEDED_FOR_WIN-1 || downLeft >= NEEDED_FOR_WIN-1)
                        return me == board[i][j] ? WIN_SCORE : -WIN_SCORE;
                }
            }
        }
        return 0;
    }

    private boolean isMovesLeft(CellState [][] board) {
//        https://www.geeksforgeeks.org/minimax-algorithm-in-game-theory-set-3-tic-tac-toe-ai-finding-optimal-move/
        return false;
    }
}
