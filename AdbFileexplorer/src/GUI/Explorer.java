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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import data.DataReciever;
import data.LanguageStrings;
import data.Logger;

public class Explorer extends Frame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5908837654492782371L;
	private List deviceList;
	private static DataReciever reciever;
	private final FilePanel filePanel;
	private Logger logger;
	private TextField connectField, destination, source;
	
	
	
	public Explorer()
	{
		List logList = new List();
		logger = new Logger(logList);		
		reciever = new DataReciever();
		
		filePanel = new FilePanel(reciever);
		
		deviceList = new List();
		deviceList.addItemListener(new ItemListener() 
		{	
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{
				String selection = reciever.getDevices(false).get(Integer.parseInt(arg0.getItem().toString()));
				
				if(reciever.setSelectedDevice(selection))
				{
					filePanel.updateADB(reciever.getDirContent("/"), "/");
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
				String selection = (String)filePanel.getFileTableModel().getValueAt(filePanel.getTable().convertRowIndexToModel(filePanel.getTable().getSelectedRow()) , 0); 
				reciever.pullFile(filePanel.getDirLabel().getText() + selection);
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
					reciever.pushFile(source.getText(), filePanel.getDirLabel().getText());
					filePanel.updateADB(reciever.getDirContent(filePanel.getDirLabel().getText()), filePanel.getDirLabel().getText());
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
				reciever.deleteFile(filePanel.getDirLabel().getText() +
						filePanel.getFileTableModel().getValueAt(filePanel.getTable().convertRowIndexToModel(filePanel.getTable().getSelectedRow()) , 0));
				filePanel.updateADB(reciever.getDirContent(filePanel.getDirLabel().getText()), filePanel.getDirLabel().getText());
			}
		});
		
		
		
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
		
		Menubar menu = new Menubar();
		menu.setItemState(reciever.getLanguage());
		
		this.setMenuBar(menu);
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
	
}
