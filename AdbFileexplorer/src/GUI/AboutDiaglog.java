package GUI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AboutDiaglog extends Frame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3164710721309054245L;



	public AboutDiaglog()
	{
		
		Panel infoPanel = new Panel(new GridLayout(4,1));
		
		Label one = new Label("Application development, german translation:");
		Label two = new Label("D4r3T0B3");
		two.setForeground(Color.BLUE);
		two.addMouseListener(new MouseListener() 
		{	
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				try 
				{
					new ProcessBuilder("explorer", "mailto:", "598@gmx.net").start();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}				
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
		});
		Label three = new Label("Slovak translation:");
		Label four = new Label("XDA-Developer Member: mGi00");
		four.setForeground(Color.BLUE);
		four.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				try 
				{
					new ProcessBuilder("cmd", "/c", "start", "http://forum.xda-developers.com/member.php?u=2042465").start();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}				
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
		});
		
		
		infoPanel.add(one);
		infoPanel.add(two);
		infoPanel.add(three);
		infoPanel.add(four);
		
		Button ok = new Button("Close");
		ok.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				close();
			}
		});	
		
		this.setLayout(new BorderLayout());
		this.add(infoPanel, BorderLayout.CENTER);
		this.add(ok, BorderLayout.SOUTH);
		
		this.setSize(300, 200);
		this.setTitle("About - Credits");
		this.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent we)
			{
				close();
			}
		});
		this.setVisible(true);
	}
	
	
	
	private void close()
	{
		this.setVisible(false);
	}
	

}
