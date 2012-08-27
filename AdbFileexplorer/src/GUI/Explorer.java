package GUI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import data.DataReciever;
import data.FileObj;
import data.LanguageStrings;
import data.Logger;

public class Explorer extends Frame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5908837654492782371L;
	private DefaultTableModel model;
	private Label dirLabel;
	private List deviceList;
	private static DataReciever reciever;
	private Panel filePanel;
	private JTable adbFileTable;
	private Logger logger;
	private TextField connectField, destination, source;
	
	
	public Explorer()
	{
		List logList = new List();
		logger = new Logger(logList);		
		reciever = new DataReciever();
		
		
		
		dirLabel = new Label();
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
		


		
		
		model = new MyTableModel();
		model.addColumn(LanguageStrings.getProperty("filenameString"));
		model.addColumn(LanguageStrings.getProperty("sizeString"));
		model.addColumn(LanguageStrings.getProperty("lastEditString"));
		
		adbFileTable = new JTable(model);
		adbFileTable.setColumnSelectionAllowed(false);
		adbFileTable.setAutoCreateRowSorter(true); //wrong selection after sorting
				
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
		
		
		deviceList = new List();
		deviceList.addItemListener(new ItemListener() 
		{	
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{
				String selection = reciever.getDevices(false).get(Integer.parseInt(arg0.getItem().toString()));
				
				if(reciever.setSelectedDevice(selection))
				{
					updateADB(reciever.getDirContent("/"), "/");
				}
			}
		});
		
		
		Button refreshButton = new Button(LanguageStrings.getProperty("refreshButton"));
		refreshButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				updateDevices(reciever.getDevices(true));
			}
		});
		
	
		connectField = new TextField();
		Button connectButton = new Button(LanguageStrings.getProperty("connectButton"));
		connectButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				reciever.connectDevice(connectField.getText());
				updateDevices(reciever.getDevices(false));
			}
		});
		
	
		
		destination = new TextField();
		destination.setText(reciever.getSaveLocation());		
		
		Button chooseDestination = new Button(LanguageStrings.getProperty("chooseDestButton"));
		chooseDestination.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(fc.showOpenDialog(Explorer.this) == JFileChooser.APPROVE_OPTION)
				{
					File loc = fc.getSelectedFile();
					reciever.setSaveLocation(loc);
					destination.setText(reciever.getSaveLocation());
					Logger.writeToLog(loc.getAbsolutePath() + LanguageStrings.getProperty("destSetLog"));
				}
				
				
				
			}
		});
		
		
		
		Button pull = new Button(LanguageStrings.getProperty("pullButton"));
		pull.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String selection = (String)model.getValueAt(adbFileTable.convertRowIndexToModel(adbFileTable.getSelectedRow()) , 0); 
				reciever.pullFile(dirLabel.getText() + selection);
				try
				{
					new ProcessBuilder("explorer", reciever.getSaveLocation() + selection).start();
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
					Logger.writeToLog(LanguageStrings.getProperty("fileOpenFailLog"));
				}
			}
		});
		
		
		
		source = new TextField();
		source.setSize(500, source.getHeight());
		
		Button chooseSource = new Button(LanguageStrings.getProperty("chooseSourceButton"));
		chooseSource.addActionListener(new ActionListener() 
		{
			
			@SuppressWarnings("static-access")
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(fc.FILES_AND_DIRECTORIES);
				
				if(fc.showOpenDialog(Explorer.this) == JFileChooser.APPROVE_OPTION)
				{
					File selection = fc.getSelectedFile();
					source.setText(selection.getAbsolutePath());
				}
			}
		});
				
		
		Button pushButton = new Button(LanguageStrings.getProperty("pushButton"));
		pushButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(source.getText() != null && source.getText().length() > 0)
				{
					reciever.pushFile(source.getText(), dirLabel.getText());
					updateADB(reciever.getDirContent(dirLabel.getText()), dirLabel.getText());
				}
				else
				{
					Logger.writeToLog(LanguageStrings.getProperty("selectSourceLog"));
				}
			}
		});
		
		
		Button deleteButton = new Button(LanguageStrings.getProperty("deleteButton"));
		deleteButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				reciever.deleteFile(dirLabel.getText() + model.getValueAt(adbFileTable.convertRowIndexToModel(adbFileTable.getSelectedRow()) , 0));
				updateADB(reciever.getDirContent(dirLabel.getText()), dirLabel.getText());
			}
		});
		
		
		
		Panel backButtonPanel = new Panel(new GridLayout(1,2));
		backButtonPanel.add(backButton);
		backButtonPanel.add(new Panel());
		
		Panel dirPanel = new Panel(new GridLayout(1,2));
		dirPanel.add(backButtonPanel);
		dirPanel.add(dirLabel);
		
		filePanel = new Panel(new BorderLayout());
		filePanel.add(dirPanel, BorderLayout.NORTH);
		filePanel.add(fileTableScrollPane, BorderLayout.CENTER);
		
		
		Panel refreshLine = new Panel();	
		refreshLine.add(refreshButton);
		
		Panel subConnectPanel = new Panel(new GridLayout(1,3));
		subConnectPanel.add(connectField);
		subConnectPanel.add(connectButton);
		
		Panel connectPanel = new Panel(new BorderLayout());		
		connectPanel.add(subConnectPanel, BorderLayout.NORTH);
		connectPanel.add(refreshLine, BorderLayout.EAST);
		connectPanel.add(new Label(LanguageStrings.getProperty("pullDestString")), BorderLayout.SOUTH);
		
		
		Panel subSourcePanel = new Panel(new GridLayout(1,2));
		subSourcePanel.add(source);
		subSourcePanel.add(chooseSource);
		
		Panel pushPanel = new Panel();
		pushPanel.add(pushButton);
		
		Panel anotherPanel = new Panel(new GridLayout(1,3));
		anotherPanel.add(new Label());
		anotherPanel.add(new Label()); 
		anotherPanel.add(deleteButton);
		
		
		Panel sourcePanel = new Panel(new BorderLayout());
		sourcePanel.add(pushPanel, BorderLayout.EAST);
		sourcePanel.add(anotherPanel, BorderLayout.SOUTH);
		sourcePanel.add(subSourcePanel, BorderLayout.NORTH);
		
		Panel subDestinationPanel = new Panel(new GridLayout(1,2));
		subDestinationPanel.add(destination);
		subDestinationPanel.add(chooseDestination);
		
		Panel pullPanel = new Panel();
		pullPanel.add(pull);
		
		Panel destinationPanel = new Panel(new BorderLayout());
		destinationPanel.add(subDestinationPanel, BorderLayout.NORTH);
		destinationPanel.add(pullPanel, BorderLayout.EAST);		
		destinationPanel.add(new Label(LanguageStrings.getProperty("pushSourceString")), BorderLayout.SOUTH);
		
		Panel devicePanel = new Panel(new GridLayout(5,1));
		devicePanel.add(deviceList);
		devicePanel.add(connectPanel);
		devicePanel.add(destinationPanel);
		devicePanel.add(sourcePanel);
		devicePanel.add(logList);
		

		
		this.setMenuBar(new Menubar());
		this.add(devicePanel);
		this.add(filePanel);		
		this.setLayout(new GridLayout(1,3));		
		this.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent we)
			{
				close();
			}
		});
		this.setSize(800, 500);
		this.setTitle("ADB Fileexplorer");
		this.setVisible(true);
		updateDevices(reciever.getDevices(true));
		
		
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
	
	public void updateDevices(ArrayList<String> in)
	{
		deviceList.removeAll();
		if(in.size() == 0)
		{
			deviceList.setEnabled(false);
		}
		else
		{
			deviceList.setEnabled(true);
			for(int i = 0; i < in.size(); i++)
			{
				deviceList.add(in.get(i));	
			}
		}
	}
	
	public static DataReciever getReciever()
	{
		return reciever;
	}
	
	
	
	
	private void close()
	{
		reciever.close();
		logger.close();
		System.exit(0);
	}
	
	private class MyTableModel extends DefaultTableModel
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
