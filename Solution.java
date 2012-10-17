import java.util.*;
import java.io.*;

public class Solution
{
	public static void main(String[] args)
	{
		Solution m = new Solution();
		m.go();
	}

	private final char COP = 'C';
	private final char ROBBER = 'R';
	private final char EQUAL = 'E';
	private final int R = 20;
	private final int C = 20;
	private final int NUM_COPS = 3;

	private final int[] robberDR = {0, -1, -1, -1, 0, 0,  1, 1, 1};
	private final int[] robberDC = {0, -1,  0,  1,-1, 1, -1, 0, 1};

	private final int[] copDR = {0, -1,  0, 0, 1};
	private final int[] copDC = {0,  0, -1, 1, 0};
	private final int DEPTH = 6;
	private char PLAYER;
	private char OPPONENT;
	
	/**
	 * Makes a move.
	 * Reads input in the form of '%c %d %d %d %d %d %d %d %d'
	 * The first character is the player to perform a move for (R for robber, C for cops)
	 * The next two integers is the row and column of the robber.
	 * The next six integers is the row and columns of each of the cops.
	 * If the player is the cops, then the six integers are the current positions of their cops.
	 * If the player is the robber, then the positions are the last known locations of the cops.
	 * Output is the next move of the current player. One (row, column) pair for the robber,
	 * or three for each of the cops.
	 */
	public void go()
	{
		Scanner s = new Scanner(System.in);

		char player = s.next().trim().charAt(0);
		PLAYER = player;
		OPPONENT = (PLAYER == COP) ? ROBBER : COP;

		Point robber = new Point(s.nextInt(), s.nextInt());
		
		Point[] cops = new Point[NUM_COPS];
		for(int k=0; k<NUM_COPS; k++)
		{
			cops[k] = new Point(s.nextInt(), s.nextInt());
		}

		if(player == ROBBER)
		{
			Point p = doRobberMove(robber, cops);
			System.out.printf("%d %d\n", p.r, p.c);
		}
		else if(player == COP)
		{
			Point[] p = doCopMove(robber, cops);
			System.out.printf("%d %d %d %d %d %d\n",
						p[0].r, p[0].c, p[1].r, p[1].c, p[2].r, p[2].c);
		}
	}

	/**
	 * Moves the robber to the nearest point with the highest weight.
	 * Weight is calculated as the number of squares the robber can reach
	 * from that state before all the cops.
	 * The robber prefers states that are at least 2 squares away from any cop.
	 */
	private Point doRobberMove(Point robber, Point[] cops)
	{
		List<Point> newRobberList = getNextRobberStates(robber);
		Point best = null;
		int max = -1;
		for(Point newRobber : newRobberList)
		{
			if(robberIsCaught(newRobber, cops))
				continue;
			if(minCopDist(cops, newRobber.r, newRobber.c) < 2)
				continue;
			ClosestData data = getClosestData(newRobber, cops);
			int val = data.numRobberCloser;
			if(val > max)
			{
				max = val;
				best = newRobber;
			}
		}
		
		
		if(best == null)
		{
			for(Point newRobber : newRobberList)
			{			
				if(robberIsCaught(newRobber, cops))
					continue;
				ClosestData data = getClosestData(newRobber, cops);
				int val = data.numRobberCloser;
				if(val > max)
				{
					max = val;
					best = newRobber;
				}
			}
		}
		
		if(best == null)
		{
			for(Point newRobber : newRobberList)
			{
				ClosestData data = getClosestData(newRobber, cops);
				int val = data.numRobberCloser;
				if(val > max)
				{
					max = val;
					best = newRobber;
				}
			}
		}
		
		return best;
	}

	/**
	 * Moves the cops.
	 * If the cops are not in the horizontal line, shift into one.
	 * Once in a horizontal line, the cops will progressively move
	 * towards the robber, keeping the robber centered when possible.
	 */
	private Point[] doCopMove(Point robber, Point[] cops)
	{
		Point[] ret = new Point[NUM_COPS];

		boolean allZero = true;
		for(int k=0; k<NUM_COPS; k++)
		{
			if(cops[k].r + cops[k].c != 0)
			{
				allZero = false;
				break;
			}
		}

		if(allZero)
		{
			// First turn, all are at the origin
			cops[2] = new Point(0, 1);
			return cops;
		}
		else if(cops[0].r+cops[0].c==0 && cops[1].r+cops[1].c==0 && cops[2].r==0 && cops[2].c==1)
		{
			// Second turn. First 2 are at the origin, 3rd cop is at (0,1)
			cops[1] = new Point(0,1);
			cops[2] = new Point(0,2);
			return cops;
		}

		// Cops are in a horizontal row. Now do stuff.
		int rDiff = robber.r - cops[1].r;
		int cDiff = robber.c - cops[1].c;

		int dr = rDiff == 0 ? 0 : rDiff/Math.abs(rDiff);
		int dc = cDiff == 0 ? 0 : cDiff/Math.abs(cDiff);

		Point midCop = cops[1];
		// If robber is exactly one above or below the middle cop
		if(robber.c==midCop.c && robber.r==midCop.r+dr)
		{
			double rand = Math.random();
			if(rand <= 0.5)
				return cops;
		}

		if(robber.c==midCop.c)
		{
			for(int k=0; k<NUM_COPS;k++)
			{
				ret[k] = new Point(cops[k].r+dr, cops[k].c);
			}
		}
		else
		{
			if(rDiff > cDiff)
			{
				for(int k=0; k<NUM_COPS; k++)
				{
					ret[k] = new Point(cops[k].r+dr, cops[k].c);
				}
			}
			else // rDiff <= cDiff
			{
				for(int k=0; k<NUM_COPS; k++)
				{
					ret[k] = new Point(cops[k].r, cops[k].c+dc);
				}
			}
		}

		return ret;
	}

	/**
	 * Returns the list of all possible states for the robber to transition into.
	 */
	private List<Point> getNextRobberStates(Point robber)
	{
		List<Point> ret = new ArrayList<Point>();
		for(int i=0; i<robberDR.length; i++)
		{
			int nr = robber.r + robberDR[i];
			int nc = robber.c + robberDC[i];
			if(isInBounds(nr, nc))
			{
				ret.add(new Point(nr, nc));
			}
		}
		return ret;
	}

	/**
	 * Returns the list of possible states for the cops to transition into.
	 * Does not deal with duplicates (i.e. 2 cops switching positions is
	 * stored twice as two different states)
	 */
	private List<Point[]> getNextCopStates(Point[] cops)
	{
		List<Point[]> ret = new ArrayList<Point[]>();
		int N = copDR.length;
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<N; j++)
			{
				for(int k=0; k<N; k++)
				{
					Point[] newCops = new Point[NUM_COPS];
					newCops[0] = new Point(cops[0].r+copDR[i], cops[0].c+copDC[i]);
					newCops[1] = new Point(cops[1].r+copDR[j], cops[1].c+copDC[j]);
					newCops[2] = new Point(cops[2].r+copDR[k], cops[2].c+copDC[k]);
					boolean allInBounds = true;
					for(int a=0; a<NUM_COPS; a++)
					{
						if(!isInBounds(newCops[a]))
						{
							allInBounds = false;
							break;
						}
					}
					if(allInBounds)
						ret.add(newCops);
				}
			}
		}
		return ret;
	}

	/**
	 * Calculates and returns the ClosestData for the given robber and cops.
	 * ClosestData contains information about which player reaches each square
	 * first and how many squares each player can reach before the other player.
	 */
	private ClosestData getClosestData(Point robber, Point[] cops)
	{
		char[][] matrix = new char[R][C];
		int numRobber = 0;
		int numCop = 0;
		for(int r=0; r<R; r++)
		{
			for(int c=0; c<C; c++)
			{
				int rDist = robberDist(robber, r, c);
				int cDist = minCopDist(cops, r, c);

				if(rDist < cDist)
				{
					matrix[r][c] = ROBBER;
					numRobber++;
				}
				else if(cDist < rDist)
				{
					matrix[r][c] = COP;
					numCop++;
				}
				else // cDist == rDist
				{
					matrix[r][c] = EQUAL;
				}
			}
		}
		return new ClosestData(matrix, numRobber, numCop);
	}

	
	/**
	 * Returns the number of steps it will take the given cop
	 * to reach the given position.
	 */
	private int copDist(Point cop, int r, int c)
	{
		return (Math.abs(cop.r-r) + Math.abs(cop.c-c));
	}

	/**
	 * Returns the number of steps it will take the closest cop
	 * to reach the given position.
	 */
	private int minCopDist(Point[] cops, int r, int c)
	{
		int min = Integer.MAX_VALUE;
		for(int k=0; k<NUM_COPS; k++)
		{
			min = Math.min(min, copDist(cops[k], r, c));
		}
		return min;
	}

	/**
	 * Returns the number of steps it will take the robber to reach
	 * the given position.
	 */
	private int robberDist(Point robber, int r, int c)
	{
		return Math.max(Math.abs(robber.r-r), Math.abs(robber.c-c));
	}

	/**
	 * Returns true if the row and column is in bounds, false otherwise.
	 */
	private boolean isInBounds(int r, int c)
	{
		return (r >= 0 && c >= 0 && r < R && c < C);
	}

	/**
	 * Returns true if the point is in bounds, false otherwise.
	 */
	private boolean isInBounds(Point p)
	{
		return isInBounds(p.r, p.c);
	}

	/*
	 * Returns true if the robber is currently standing on any
	 * of the cops, false otherwise.
	 */
	private boolean robberIsCaught(Point robber, Point[] cops)
	{
		for(Point cop : cops)
		{
			if(cop.r == robber.r && cop.c == robber.c)
				return true;
		}
		return false;
	}
	
	/**
	 * Prints the given character array.
	 */
	private void printMatrix(char[][] matrix)
	{
		for(int r=0; r<R; r++)
		{
			for(int c=0; c<C; c++)
			{
				System.out.print(matrix[r][c] + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Simple Point class with a row and column.
	 */
	public class Point
	{
		public int r;
		public int c;

		public Point(int r, int c)
		{
			this.r = r;
			this.c = c;
		}

		public Point(Point p)
		{
			this.r = p.r;
			this.c = p.c;
		}
	}

	/**
	 * Contains a grid that shows which player can get to each square the quickest
	 * and the total number of squares each player can reach first.
	 */
	public class ClosestData
	{
		public char[][] matrix;
		public int numRobberCloser;
		public int numCopsCloser;

		public ClosestData(char[][] matrix, int numR, int numC)
		{
			this.matrix = matrix;
			numRobberCloser = numR;
			numCopsCloser = numC;
		}
	}
}
