package GUI;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URISyntaxException;

import main.Start;
import data.DataReciever;
import data.LanguageStrings;

public class Menubar extends MenuBar 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1647687440250180882L;
	private Menu language;

	public Menubar()
	{
		super();
		
		
		language = new Menu("Language");
		
		CheckboxMenuItem english = new CheckboxMenuItem("English");
		english.addItemListener(new LanguageItemListener("english"));
		
		CheckboxMenuItem german = new CheckboxMenuItem("German");
		german.addItemListener(new LanguageItemListener("german"));
		
		CheckboxMenuItem slovak = new CheckboxMenuItem("Slovak");
		slovak.addItemListener(new LanguageItemListener("slovak"));
		
		language.add(english);
		language.add(german);
		language.add(slovak);
		
		
		
		this.add(language);
	}
	
	
	private class LanguageItemListener implements ItemListener
	{
		private String languageString;
		public LanguageItemListener(String languageString)
		{
			this.languageString = languageString;
		}
		

		@Override
		public void itemStateChanged(ItemEvent e) 
		{
			DataReciever.setLanguageString(new LanguageStrings(languageString));
			
			try 
			{
				Start.restartApplication();
			} 
			catch (URISyntaxException e1) 
			{}
			catch (IOException e1) 
			{}
			
		}
		
	}

	public void setItemState(String languageString)
	{
		for(int i = 0; i < language.getItemCount(); i++)
		{
			if(language.getItem(i) instanceof CheckboxMenuItem && language.getItem(i).getLabel().toLowerCase() == languageString.toLowerCase())
			{
				CheckboxMenuItem item = (CheckboxMenuItem)language.getItem(i);
				item.setState(true);
			}
		}
	}

}
