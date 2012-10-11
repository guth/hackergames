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

	int[] robberDR = {-1, -1, -1, 0, 0,  1, 1, 1};
	int[] robberDC = {-1,  0,  1,-1, 1, -1, 0, 1};

	int[] copDR = {-1,  0, 0, 1};
	int[] copDC = { 0, -1, 1, 0};
	char player;
	Point robber;
	Point[] cops;

	public void go()
	{
		Scanner s = new Scanner(System.in);
		
		player = s.next().trim().charAt(0);
		//System.err.println("Player: " + player);
		
		robber = new Point(s.nextInt(), s.nextInt());
		//System.err.println("Robber is at: " + "(" + robber.r + ", " + robber.c + ")");
		cops = new Point[NUM_COPS];
		for(int k=0; k<NUM_COPS; k++)
		{
			cops[k] = new Point(s.nextInt(), s.nextInt());
			//System.err.println("Cop #" + k + ": (" + cops[k].r + ", " + cops[k].c + ")");
		}

		if(player == ROBBER)
		{
			Point p = doRobberMove();
			System.out.println(p.r + " " + p.c);
		}
		else if(player == COP)
		{
			Point[] p = doCopMove();
			System.out.printf("%d %d %d %d %d %d\n",
						p[0].r, p[0].c, p[1].r, p[1].c, p[2].r, p[2].c);
		}
	}

	// Moves to a random point that's not a cop
	private Point doRobberMove()
	{
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
	}

	// Collectively moves towards the robber
	private Point[] doCopMove()
	{
		Point[] ret = new Point[NUM_COPS];

		
		Random r = new Random();


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
	private int eval()
	{
		ClosestData data = getClosestData();
		int diff = data.numCopsCloser - data.numRobberCloser;
		
		// High is good for robber, bad for cops
		int distToRobber = minCopDist(robber.r, robber.c);

		if(player == ROBBER)
		{
			diff = -diff;
			diff += distToRobber;
		}
		else // player == cop
		{
			diff -= distToRobber;
		}

		return diff;
	}

	private List<Point> getNextRobberStates()
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

	private List<Point[]> getNextCopStates()
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
					ret.add(newCops);
				}
			}
		}
		return ret;
	}

	private ClosestData getClosestData()
	{
		char[][] matrix = new char[R][C];
		int numRobber = 0;
		int numCop = 0;
		for(int r=0; r<R; r++)
		{
			for(int c=0; c<C; c++)
			{
				int rDist = robberDist(r, c);
				int cDist = minCopDist(r, c);

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

	private int copDist(int ci, int r, int c)
	{
		return (Math.abs(cops[ci].r-r) + Math.abs(cops[ci].c-c));
	}

	private int minCopDist(int r, int c)
	{
		int min = Integer.MAX_VALUE;
		for(int k=0; k<NUM_COPS; k++)
		{
			min = Math.min(min, copDist(k, r, c));
		}
		return min;
	}

	private int robberDist(int r, int c)
	{
		return Math.max(Math.abs(robber.r-r), Math.abs(robber.c-c));
	}

	private boolean isInBounds(int r, int c)
	{
		return (r >= 0 && c >= 0 && r < 20 && c < 20);
	}

	private boolean isInBounds(Point p)
	{
		return isInBounds(p.r, p.c);
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