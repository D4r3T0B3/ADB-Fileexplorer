package GUI;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
		
		language.add(english);
		language.add(german);
		
		
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
			for(int i = 0; i < language.getItemCount(); i++)
			{
				if(language.getItem(i) instanceof CheckboxMenuItem)
				{
					CheckboxMenuItem item = (CheckboxMenuItem) language.getItem(i);
					item.setState(false);
				}
			}
			
			
			CheckboxMenuItem source = (CheckboxMenuItem) e.getSource();
			source.setState(true);
			DataReciever.setLanguageString(new LanguageStrings(languageString));		
			
		}
		
	}

}
