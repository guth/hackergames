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
	private final char EQUAL = 'R';
	private final int R = 20;
	private final int C = 20;
	private final int NUM_COPS = 3;
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
		
		// if(robber.r == -1 && robber.c == -1) // first turn to place cops & robbers
		// {
		// 	doInitialPlacement();
		// 	return;
		// }

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

	private Point doRobberMove()
	{
		Point ret = null;
		while(ret == null)
		{
			int dr = 0;
			int dc = 0;

			Random rnd = new Random();
			int val = rnd.nextInt(8); 	// Between [0, 8)
			switch(val)
			{
				case 0:
					dr = -1;
					dc = -1;
					break;
				case 1:
					dr = -1;
					dc = 0;
					break;
				case 2:
					dr = -1;
					dc = 1;
					break;
				case 3:
					dr = 0;
					dc = -1;
					break;
				case 4:
					dr = 0;
					dc = 1;
					break;
				case 5:
					dr = 1;
					dc = -1;
					break;
				case 6:
					dr = 1;
					dc = 0;
					break;
				case 7:
					dr = 1;
					dc = 1;
					break;
				default:
					dr = 1;
					dc = 1;
			}
			int nr = robber.r + dr;
			int nc = robber.c + dc;
			if(isInBounds(nr, nc))
				ret = new Point(nr, nc);
		}
		return ret;
		// char[][] closest = getClosestMatrix();
		// Point ret = new Point(robber.r, robber.c);
		// for(int r=0; r<R; r++)
		// {
		// 	for(int c=0; c<C; c++)
		// 	{
		// 		if(closest[r][c] == ROBBER)
		// 		{

		// 		}
		// 	}
		// }
	}

	private Point[] doCopMove()
	{
		Point[] ret = new Point[NUM_COPS];
		for(int k=0; k<NUM_COPS; k++)
		{
			int rDiff = robber.r - cops[k].r;
			int cDiff = robber.c - cops[k].c;

			int dr = rDiff/Math.abs(rDiff);
			int dc = cDiff/Math.abs(cDiff);

			if(Math.abs(rDiff) > Math.abs(cDiff))
				ret[k] = new Point(cops[k].r+dr, cops[k].c);
			else
				ret[k] = new Point(cops[k].r, cops[k].c+dc);
		}
		return ret;
	}

	private char[][] getClosestMatrix()
	{
		char[][] ret = new char[R][C];
		for(int r=0; r<R; r++)
		{
			for(int c=0; c<C; c++)
			{
				int rDist = robberDist(r, c);
				int cDist = minCopDist(r, c);

				if(rDist < cDist)
				{
					ret[r][c] = ROBBER;
				}
				else if(cDist < rDist)
				{
					ret[r][c] = COP;
				}
				else // cDist == rDist
				{
					ret[r][c] = EQUAL;
				}
			}
		}
		return ret;
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

	private void doInitialPlacement()
	{
		if(player == COP)
		{
			System.out.printf("%d %d %d %d\n", 4, 4, 14, 14);
		}
		else if(player == ROBBER)
		{
			int max = -1;
			Point maxPoint = new Point(0, 0);
			for(int r=0; r<R; r++)
			{
				for(int c=0; c<C; c++)
				{
					int dist = Math.abs(r-cops[0].r)+Math.abs(c-cops[0].c);
					dist = Math.max(dist, Math.abs(r-cops[0].r)+Math.abs(c-cops[0].c));
					if(dist > max)
					{
						max = dist;
						maxPoint = new Point(r, c);
					}
				}
			}
			System.out.printf("%d %d\n", maxPoint.r, maxPoint.c);
		}
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
}