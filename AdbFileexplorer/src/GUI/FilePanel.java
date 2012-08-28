package GUI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import data.DataReciever;
import data.FileObj;
import data.LanguageStrings;
import data.Logger;

public class FilePanel extends Panel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4692855035526278691L;
	private Label dirLabel;
	private JTable adbFileTable;
	private DataReciever reciever;
	private MyTableModel model;
	
	public FilePanel(final DataReciever reciever)
	{
		this.reciever = reciever;
		
		Button backButton = new Button(LanguageStrings.getProperty("backButton"));
		backButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String dir = dirLabel.getText();
				String[] split = dir.split("/");
				if(split.length > 1)
				{
					String value = "";
					for(int i = 0; i < split.length-1; i++)
					{
						value += split[i] + "/";
					}
					updateADB(reciever.getDirContent(value), value);
				}
				else
				{
					updateADB(reciever.getDirContent("/"), "/");
				}
			}
		});
		
		Panel backButtonPanel = new Panel(new GridLayout(1,2));
		backButtonPanel.add(backButton);
		backButtonPanel.add(new Panel());
		
		dirLabel = new Label();
		Panel dirPanel = new Panel(new GridLayout(1,2));
		dirPanel.add(backButtonPanel);
		dirPanel.add(dirLabel);


		
		
		model = new MyTableModel();
		model.addColumn(LanguageStrings.getProperty("filenameString"));
		model.addColumn(LanguageStrings.getProperty("sizeString"));
		model.addColumn(LanguageStrings.getProperty("lastEditString"));
		
		adbFileTable = new JTable(model);
		adbFileTable.setColumnSelectionAllowed(false);
		adbFileTable.setAutoCreateRowSorter(true);
				
		adbFileTable.addMouseListener(new MouseListener() 
		{			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				boolean left = ( e.getButton() == MouseEvent.BUTTON1 );   
//			    boolean middle = ( e.getButton() == MouseEvent.BUTTON2) ;  
//			    boolean right = ( e.getButton() == MouseEvent.BUTTON3 ); //context menu?
			    boolean doubleClick = e.getClickCount() > 1;   
				
				if(left && doubleClick)
				{
					adbFileTable.repaint();
					String value = (String) model.getValueAt(adbFileTable.convertRowIndexToModel(adbFileTable.getSelectedRow()) , 0);
					
					if(value.contains("->"))
					{
						String[] split = value.split(" ");
						value = split[2];
					}
					else
					{
						value = dirLabel.getText() + value + "/";
					}
					updateADB(reciever.getDirContent(value), value);						
				}
			}
		});

		JScrollPane fileTableScrollPane = new JScrollPane(adbFileTable);	
		
		
		this.setLayout(new BorderLayout());
		this.add(dirPanel, BorderLayout.NORTH);
		this.add(fileTableScrollPane, BorderLayout.CENTER);
	}
	
	public void updateADB(ArrayList<FileObj> in, String dir)
	{
		if(in != null)
		{
			dirLabel.setText(dir);

			while(model.getRowCount() > 0)
			{
				model.removeRow(0);
			}
			for(int i = 0; i < in.size(); i++)
			{
				model.addRow(in.get(i).getDataForTable());
			}
		}
		else
		{
			openFile(dir);
		}
	}
	
	private void openFile(String path)
	{
		Logger.writeToLog(LanguageStrings.getProperty("pullingLog"));
		File file = reciever.pullFile(path);
		
		try 
		{			
			new ProcessBuilder("explorer", file.getAbsolutePath()).start();
		} 
		catch (IOException e) 
		{
			Logger.writeToLog(LanguageStrings.getProperty("openFailedLog"));
			e.printStackTrace();
		}
	}
	
	public MyTableModel getFileTableModel()
	{
		return model;
	}
	
	public JTable getTable()
	{
		return adbFileTable;
	}
	
	public Label getDirLabel()
	{
		return dirLabel;
	}
	
	public class MyTableModel extends DefaultTableModel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1332744571780301856L;

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) 
		{
			return false;
		}		
	}
	
}
