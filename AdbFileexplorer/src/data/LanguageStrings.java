package data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;



public class LanguageStrings 
{
	private static Properties probs;
	private String language;
	
	public LanguageStrings(String language)
	{
		this.language = language;
		probs = new Properties();
		try 
		{		
			probs.loadFromXML(this.getClass().getResourceAsStream("/xml/" + language + ".xml"));		
		} 
		catch (InvalidPropertiesFormatException e) 
		{
			Logger.writeToLog(e.getMessage());
			e.printStackTrace();
		} 
		catch (FileNotFoundException e) 
		{
			Logger.writeToLog(e.getMessage());
		} 
		catch (IOException e) 
		{
			Logger.writeToLog(e.getMessage());
		}
		
		
	}
	
	public static String getProperty(String key)
	{
		return probs.getProperty(key);
	}
	
	public String getLanguage()
	{
		return language;
	}

}
