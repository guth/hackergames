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

	private final int[] robberDR = {-1, -1, -1, 0, 0,  1, 1, 1};
	private final int[] robberDC = {-1,  0,  1,-1, 1, -1, 0, 1};

	private final int[] copDR = {0, -1,  0, 0, 1};
	private final int[] copDC = {0,  0, -1, 1, 0};
	private final int DEPTH = 6;
	private char PLAYER;
	private char OPPONENT;
	
	private boolean DEBUG = false;
	
	public void go()
	{
		int[] a;
		Arrays.sort(a)
		
		List<Integer> b;
		Collections.sort(b);
	
		Scanner s = new Scanner(System.in);

		char player = s.next().trim().charAt(0);
		PLAYER = player;
		OPPONENT = (PLAYER == COP) ? ROBBER : COP;
		//System.err.println("Player: " + PLAYER);
		//System.err.println("Opponent: " + OPPONENT);

		Point robber = new Point(s.nextInt(), s.nextInt());
		//System.err.println("Robber is at: " + "(" + robber.r + ", " + robber.c + ")");
		Point[] cops = new Point[NUM_COPS];
		for(int k=0; k<NUM_COPS; k++)
		{
			cops[k] = new Point(s.nextInt(), s.nextInt());
			//System.err.println("Cop #" + k + ": (" + cops[k].r + ", " + cops[k].c + ")");
		}
		//ClosestData close = getClosestData(robber, cops);
		//printMatrix(close.matrix);

		if(player == ROBBER)
		{
			//alphabeta(robber, cops, DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
			//Point p = globalBestRobber;
			Point p = doRobberMove(robber, cops);
			System.out.println(p.r + " " + p.c);
		}
		else if(player == COP)
		{
//			Point[] p = globalBestCops;
			Point[] p = doCopMove(robber, cops);
			System.out.printf("%d %d %d %d %d %d\n",
						p[0].r, p[0].c, p[1].r, p[1].c, p[2].r, p[2].c);
		}

		// if(player == ROBBER)
		// {
		// 	Point p = doRobberMove(robber, cops);
		// 	System.out.println(p.r + " " + p.c);
		// }
		// else if(player == COP)
		// {
		// 	Point[] p = doCopMove(robber, cops);
		// 	System.out.printf("%d %d %d %d %d %d\n",
		// 				p[0].r, p[0].c, p[1].r, p[1].c, p[2].r, p[2].c);
		// }
	}
/*
	function alphabeta(node, depth, a, b, Player)         
	    if  depth = 0 or node is a terminal node
	        return the heuristic value of node
	    if  Player = MaxPlayer
	        for each child of node
	            a := max(a, alphabeta(child, depth-1, a, b, not(Player) ))     
	            if b <= a
	                break                             (* Beta cut-off *)
	        return a
	    else
	        for each child of node
	            b := min(b, alphabeta(child, depth-1, a, b, not(Player) ))     
	            if b <= a
	                break                             (* Alpha cut-off *)
	        return b
	
	(* Initial call *)
	alphabeta(origin, depth, -infinity, +infinity, MaxPlayer)
*/
	private Point globalBestRobber = null;
	private Point[] globalBestCops = null;
	private int alphabeta(Point robber, Point[] cops, int depth, int alpha, int beta, char player)
	{
		char otherPlayer = (player == COP) ? ROBBER : COP;

		//if(depth == 0 || (player == COP && robberIsCaught(robber, cops)))
		if(depth == 0 || robberIsCaught(robber, cops))
		{
			int val = eval(player, robber, cops);
			return val;
		}

		Point bestRobber = null;
		Point[] bestCops = null;
		if(player == PLAYER) // max player
		{
			if(player == COP)
			{
				List<Point[]> newCopsList = getNextCopStates(cops);
				for(Point[] newCops : newCopsList)
				{
					int temp = alphabeta(robber, newCops, depth-1, alpha, beta, otherPlayer);
					if(temp > alpha)
					{
						alpha = temp;
						bestCops = newCops;
					}
					if(temp == alpha && bestCops == null)
						bestCops = newCops;
					if(beta <= alpha)
						break;
				}
			}
			else if(player == ROBBER)
			{
				List<Point> newRobberList = getNextRobberStates(robber);
				for(Point newRobber : newRobberList)
				{
					int temp = alphabeta(newRobber, cops, depth-1, alpha, beta, otherPlayer);
					if(temp > alpha)
					{
						alpha = temp;
						bestRobber = newRobber;
					}
					if(temp == alpha && bestRobber == null)
						bestRobber = newRobber;
					if(beta <= alpha)
						break;
				}
			}
			globalBestCops = bestCops;
			globalBestRobber = bestRobber;
			return alpha;
		}
		else //otherplayer == PLAYER // min player
		{
			if(otherPlayer == COP)
			{
				List<Point[]> newCopsList = getNextCopStates(cops);
				for(Point[] newCops : newCopsList)
				{
					int temp = alphabeta(robber, newCops, depth-1, alpha, beta, otherPlayer);

					if(temp < beta)
					{
						beta = temp;
						bestCops = newCops;
					}
					if(temp == beta && bestCops == null)
						bestCops = newCops;
					if(beta <= alpha)
						break;
				}
			}
			else if(otherPlayer == ROBBER)
			{
				List<Point> newRobberList = getNextRobberStates(robber);
				for(Point newRobber : newRobberList)
				{
					int temp = alphabeta(newRobber, cops, depth-1, alpha, beta, otherPlayer);
					beta = Math.min(beta, temp);
					if(temp < beta)
					{
						beta = temp;
						bestRobber = newRobber;
					}
					if(temp == beta && bestRobber == null)
						bestRobber = newRobber;
					if(beta <= alpha)
						break;
				}
			}
			globalBestCops = bestCops;
			globalBestRobber = bestRobber;
			return beta;
		}
	}

	// Moves to a random point that's not a cop
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
	/*
		Point ret = null;
		loop: while(ret == null)
		{
			Random rnd = new Random();
			int val = rnd.nextInt(8); 	// Between [0, 8)

			if(val < 0 || val >= 8)
				continue;
			int dr = robberDR[val];
			int dc = robberDC[val];

			int nr = robber.r + dr;
			int nc = robber.c + dc;
			if(isInBounds(nr, nc))
			{
				for(Point cop : cops)
				{
					if(nr == cop.r && nc == cop.c)
						continue loop;
				}
				ret = new Point(nr, nc);
			}
		}
		return ret;
		*/
	}

	// Collectively moves towards the robber
	private Point[] doCopMove(Point robber, Point[] cops)
	{
		Point[] ret = new Point[NUM_COPS];

		for(int k=0; k<NUM_COPS; k++)
		{
			int rDiff = robber.r - cops[k].r;
			int cDiff = robber.c - cops[k].c;

			int dr = rDiff == 0 ? 0 : rDiff/Math.abs(rDiff);
			int dc = cDiff == 0 ? 0 : cDiff/Math.abs(cDiff);

			if(k == 0)
			{
				// if(Math.abs(rDiff) > Math.abs(cDiff))
				// 	ret[k] = new Point(cops[k].r+dr, cops[k].c);
				// else
				// 	ret[k] = new Point(cops[k].r, cops[k].c+dc);
				if(dr == 0)
					ret[k] = new Point(cops[k].r, cops[k].c+dc);
				else
					ret[k] = new Point(cops[k].r+dr, cops[k].c);
			}
			else if(k==1)
			{
				if(dc == 0)
					ret[k] = new Point(cops[k].r+dr, cops[k].c);
				else
					ret[k] = new Point(cops[k].r, cops[k].c+dc);
			}
			else // k == 2
			{
				if(Math.abs(rDiff) > Math.abs(cDiff))
					ret[k] = new Point(cops[k].r+dr, cops[k].c);
				else
					ret[k] = new Point(cops[k].r, cops[k].c+dc);
			}
		}
		return ret;
	}

	// Always higher value for the current player.
	private int eval(char player, Point robber, Point[] cops)
	{
		ClosestData data = getClosestData(robber, cops);
		
		if(player == COP)
		{
			int totalDistFromRobber = 0;
			for(int k=0; k<NUM_COPS; k++)
			{
				totalDistFromRobber += copDist(cops[k], robber.r, robber.c);
			}
			int avgDistFromRobber = totalDistFromRobber / NUM_COPS;
			return 1000-totalDistFromRobber;
		}
		else //if(player == ROBBER)
		{
			if(robberIsCaught(robber, cops))
				return Integer.MIN_VALUE;
			//int distFromCenter = Math.abs(robber.r-R/2)+Math.abs(robber.c-C/2);
			
			//return totalDistFromCops;
			return data.numRobberCloser;
		}
//		int diff = data.numCopsCloser - data.numRobberCloser;

//		// High is good for robber, bad for cops
//		int distToRobber = minCopDist(cops, robber.r, robber.c);
//
//		if(player == ROBBER)
//		{
//			diff = -diff;
//			diff += distToRobber;
//		}
//		else // player == cop
//		{
//			diff -= distToRobber;
//		}
//
//		return diff;
	}

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

	private int copDist(Point cop, int r, int c)
	{
		return (Math.abs(cop.r-r) + Math.abs(cop.c-c));
	}

	private int minCopDist(Point[] cops, int r, int c)
	{
		int min = Integer.MAX_VALUE;
		for(int k=0; k<NUM_COPS; k++)
		{
			min = Math.min(min, copDist(cops[k], r, c));
		}
		return min;
	}

	private int robberDist(Point robber, int r, int c)
	{
		return Math.max(Math.abs(robber.r-r), Math.abs(robber.c-c));
	}

	private boolean isInBounds(int r, int c)
	{
		return (r >= 0 && c >= 0 && r < R && c < C);
	}

	private boolean isInBounds(Point p)
	{
		return isInBounds(p.r, p.c);
	}

	private boolean robberIsCaught(Point robber, Point[] cops)
	{
		for(Point cop : cops)
		{
			if(cop.r == robber.r && cop.c == robber.c)
				return true;
		}
		return false;
	}
	
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
