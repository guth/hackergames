import java.io.*;
import java.util.*;

public class Judge
{
	public static void main(String[] args) throws Exception
	{
		Judge j = new Judge();
		j.go();
	}
	
	public void go() throws Exception
	{
		int turn = 1;
		char winner = 'E';
		
		Point robber = new Point(19, 19);
		
		Point[] cops = new Point[3];
		cops[0] = new Point(0,0);
		cops[1] = new Point(0,0);
		cops[2] = new Point(0,0);
		
		Runtime run = Runtime.getRuntime();
		writeNewInput('C', robber, cops, "input");
		
		
		while(true)
		{
			if(turn > 300)
			{
				winner = 'R';
				break;
			}
			
			if(robberIsCaught(robber, cops))
			{
				winner = 'C';
				break;
			}
			
			run.exec("java Solution < input > output");
			
			
			BufferedReader br = new BufferedReader(new FileReader("output"));
			String line = br.readLine();
			String[] parts = line.split(" ");
			for(int k=0; k<3; k++)
			{
				cops[k] = new Point(Integer.parseInt(parts[2*k]), Integer.parseInt(parts[2*k+1]));
			}
			br.close();
			
			
			
			
			turn++;
		}
	}
	
	private void writeNewInput(char player, Point robber, Point[] cops, String fileName) throws Exception
	{
		FileWriter fw = new FileWriter(fileName);
		fw.write(player + "\n");
		fw.write(robber.r + " " + robber.c + " " + cops[0].r + " " + cops[0].c + " " + cops[1].r + " " + cops[1].c + " " + cops[2].r + " " + cops[2].c);
		fw.close();
	}
	
	private boolean robberIsCaught(Point robber, Point[] cops)
	{
		for(int k=0; k<cops.length; k++)
		{
			if(robber.r==cops[k].r && robber.c==cops[k].c)
				return true;
		}
		return false;
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
	}
}
