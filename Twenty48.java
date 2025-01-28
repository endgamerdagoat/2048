import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;


public class Twenty48 extends JPanel implements KeyListener
{
	JFrame frame;
	Font font;

	int[][] grid;
	boolean[][] merged;
	Color[] colors;

	int score;
	boolean gameOver;
	int[] topTenScore;
	String[] topTenName;
	boolean done;

	public Twenty48()
	{
		frame=new JFrame("2048 - THE Experience");
		frame.add(this);
		frame.setSize(1500,700);

		font=new Font("Haettenschweiler",Font.PLAIN,50);
		setup();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		frame.addKeyListener(this);




	}

	//painting canvas
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		g.setFont(font);

		g.setColor(Color.BLACK);
		g.fillRect(0,0,frame.getWidth(), frame.getHeight());

		Graphics2D g2=(Graphics2D)g;
		g2.setStroke(new BasicStroke(8));
		for(int r=0; r<4;r++)
		{
			for(int c=0; c<4; c++)
			{
				if(grid[r][c]>0)
				{
					int num = (int)(Math.log(grid[r][c]) / Math.log(2))-1;
					g.setColor(colors[num]);
					g.fillRect(100+c*125,75+r*125,125,125);



					int adj=0;
					if(grid[r][c]>9)
						adj=5;
					if(grid[r][c]>99)
						adj=15;
					if(grid[r][c]>999)
						adj=25;
					if(grid[r][c]>9999)
						adj=35;
					g.setColor(Color.WHITE);
					g.drawString(""+grid[r][c],+150+c*125-adj,155+r*125);
				}
				g.setColor(new Color(13, 89, 181));
				g.drawRect(100+c*125,75+r*125,125,125);
			}
		}
		g.setColor(new Color(13, 89, 181));
		g.drawString("Score: "+score, 700, 200);

		if(gameOver)
		{
			g.setColor(Color.RED);
			g.drawString("GAME OVER!", 275,325);
			g.drawString("Press Enter to Play Again", 175,650);
			
			g.setColor(new Color(13,89,181));
			g.drawString("Top Ten", 1000,150);
			
			for(int x=0; x<topTenScore.length; x++)
			{
				g.drawString(String.format("%5s%-5s%s",
				""+(x+1)+": ",topTenName[x].toUpperCase(),topTenScore[x]),
				1000,200+45*x);
			}
			
		}

	}
	
	public void hiScoreGetter()
	{
		try
		{
			File file = new File("topTen.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String text;
			int index = 0;
			boolean notPlaced = true;
			while((text=reader.readLine())!=null)
			{
				String[] pieces = text.split("\t");
				int oldScore = Integer.parseInt(pieces[1]);
				if(score>oldScore && notPlaced)
				{
					topTenScore[index]=score;
					String name;
					do
					{
						name=JOptionPane.showInputDialog("You have a high score! Enter a 3 character name");
						if(name.length()>3)
							JOptionPane.showMessageDialog(
							null, "Your input exceeds the character limit. Please enter again.",
							"Failure", JOptionPane.ERROR_MESSAGE);
					}
					while(name.length()>3);
					topTenName[index]=name;
					notPlaced=false;
					index++;
				}
				
				if(index<10)
				{
					topTenScore[index]=oldScore;
					topTenName[index]=pieces[0];
					index++;
				}
			}
				
			String output="";
			for(int x=0; x<10; x++)
			{
				output+=topTenName[x].toUpperCase()+"\t"+topTenScore[x];
				if(x<9)
					output+="\n";
			}
			reader.close();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(output);
			writer.close();
				
		}
		
		catch(IOException e)
		{
		}
		done=true;
		repaint();
	}

	public void setup()
	{
		score=0;
		gameOver=false;
		done=false;
		grid=new int[4][4];
		merged=new boolean[4][4];
		topTenScore=new int[10];
		topTenName=new String[10];
		colors = new Color[16];
		int r=200;
		int g=30;
		int b=5;
		for(int x=0; x<colors.length; x++)
		{
			colors[x] = new Color(r,g,b);
			r-=10;
			g+=15;
			b+=10;
		}
		add2();
		add2();
	}

	public void add2()
	{
		if(!isFull())
		{
			int row;
			int col;
			do
			{
				row=(int)(Math.random()*4);
				col=(int)(Math.random()*4);
			} while(grid[row][col]!=0);
			grid[row][col]=2;
		}

	}

	public boolean isFull()
	{
		for(int r=0; r<4; r++)
		{
			for(int c=0; c<4; c++)
			{
				if(grid[r][c]==0)
					return false;
			}
		}
		return true;
	}


	public void keyPressed(KeyEvent e)
	{
		if(canMove())
		{
			System.out.println(e.getKeyCode());
			switch(e.getKeyCode())
			{
				case 65: //A
				case 37: //left
					for(int r=0;r<4;r++)
					{
						for(int c=1; c<4; c++)
						{
							int x=c;
							while(x>0 && grid[r][x-1]==0)
							{
								grid[r][x-1] = grid[r][x];
								grid[r][x]=0;
								x--;
							}
							if(x>0 && grid[r][x]==grid[r][x-1] && !merged[r][x-1])
							{
								grid[r][x-1]*=2;
								grid[r][x]=0;
								merged[r][x-1]=true;
								score+=grid[r][x-1];
							}
						}
					}
					break;
				case 87: //W
				case 38: //up
					for(int c=0;c<4;c++)
					{
						for(int r=1; r<4; r++)
						{
							int x=r;
							while(x>0 && grid[x-1][c]==0)
							{
								grid[x-1][c] = grid[x][c];
								grid[x][c]=0;
								x--;
							}
							if(x>0 && grid[x][c]==grid[x-1][c] && !merged[x-1][c])
							{
								grid[x-1][c]*=2;
								grid[x][c]=0;
								merged[x-1][c]=true;
								score+=grid[x-1][c];
							}
						}
					}
					break;
				case 68: //D
				case 39: //right
					for(int r=0;r<4;r++)
					{
						for(int c=2; c>=0; c--)
						{
							int x=c;
							while(x<3 && grid[r][x+1]==0)
							{
								grid[r][x+1] = grid[r][x];
								grid[r][x]=0;
								x++;
							}
							if(x<3 && grid[r][x+1]==grid[r][x] && !merged[r][x+1])
							{
								grid[r][x+1]*=2;
								grid[r][x]=0;
								merged[r][x+1]=true;
								score+=grid[r][x+1];
							}
						}
					}
					break;
				case 83: //W
				case 40: //down
					for(int c=0;c<4;c++)
					{
						for(int r=2; r>=0; r--)
						{
							int x=r;
							while(x<3 && grid[x+1][c]==0)
							{
								grid[x+1][c] = grid[x][c];
								grid[x][c]=0;
								x++;
							}
							if(x<3 && grid[x+1][c]==grid[x][c] && !merged[x+1][c])
							{
								grid[x+1][c]*=2;
								grid[x][c]=0;
								merged[x+1][c]=true;
								score+=grid[x+1][c];
							}
						}
					}
					break;
			}


			for(int r=0; r<4; r++)
			{
				for(int c=0; c<4; c++)
				{
					merged[r][c]=false;
					if(grid[r][c]==2048)
						gameOver=true;
				}
			}

			if(!isFull())
				add2();
		}

		else
		{
			gameOver=true;
			if(!done)
				hiScoreGetter();
			if(e.getKeyCode()==10)//Enter
				setup();
		}
		repaint();


	}

	public boolean canMove()
	{
		if(!isFull())
			return true;

		for(int r=0; r<4; r++)
		{
			for(int c=0; c<3; c++)
			{
				if(grid[r][c]==grid[r][c+1])
					return true;
			}
		}

		for(int c=0; c<4; c++)
		{
			for(int r=0; r<3; r++)
			{
				if(grid[r][c]==grid[r+1][c])
					return true;
			}
		}

		return false;
	}

	public void keyReleased(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
	{
	}



	public static void main(String[] args)
	{
		Twenty48 app=new Twenty48();
	}
}


