package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

public class DataReciever 
{
	private String selectedDevice = "";
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
	private File saveLocation; 
	private Properties probs;
	
	public DataReciever()
	{
		BufferedReader reader = null;
		InputStream processIn = null;
		try 
		{	
			new ProcessBuilder("adb", "root").start();
			Logger.writeToLog("adb started as root");
			
			Process process = new ProcessBuilder("adb", "version").start();
			processIn = process.getInputStream();
			reader = new BufferedReader(new InputStreamReader(processIn));
			Logger.writeToLog(reader.readLine());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			Logger.writeToLog(e.getMessage());
		}	
		finally
		{
			try
			{
				if(reader != null)
					reader.close();
				if(processIn != null)
					processIn.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		probs = new Properties();
		try 
		{
			probs.load(new FileReader(new File("explorer.proberties")));
			saveLocation = new File(probs.getProperty("saveLocation"));
		} 
		catch (FileNotFoundException e) { Logger.writeToLog("propertie file not found. Creating new one"); setSaveLocation(new File(getSaveLocation()));} 
		catch (IOException e) {	e.printStackTrace(); }
	}
	
	
	public ArrayList<String> getDevices(boolean log)
	{
		 ArrayList<String> ret = new ArrayList<String>();
		 InputStream processIN = null;
		 BufferedReader br = null;
		 try 
		 {
			Process process = new ProcessBuilder("adb", "devices").start();			
			processIN = process.getInputStream();
			br = new BufferedReader(new InputStreamReader(processIN));
			
			br.readLine();
			String line;
			while((line = br.readLine() )!= null && line.length() > 0)
			{
				String[] split = line.split("\t");
				if(split.length >= 1)
				{
					ret.add(split[0]);
				}
			}
		 } 
		 catch (IOException e) 
		 {
			e.printStackTrace();
			Logger.writeToLog(e.getMessage());
		 }
		 finally
		 {
			 try
			 {
				 if(br != null)
					 br.close();
				 if(processIN != null)
					 processIN.close();
			 }
			 catch(IOException e)
			 {
				 e.printStackTrace();
			 }
		 }
		 if(log)
		 {
			 Logger.writeToLog(ret.size() + " devices found");
		 }
		 return ret;
	}
	
	public void connectDevice(final String ip)
	{
		if(ip != null && ip.length() > 0)
		{
			try 
			{
				new ProcessBuilder("adb", "connect", ip).start();
				Logger.writeToLog(ip + " connected");
			}
			catch (IOException e) 
			{
				e.printStackTrace();
				Logger.writeToLog(e.getMessage());
			}	
		}
	}
	
	public ArrayList<FileObj> getDirContent(String dir) 
	{
		if(selectedDevice.length() <= 0)
		{
			return null;
		}
		ArrayList<FileObj> ret = new ArrayList<FileObj>();
		
		Process process;
		BufferedReader br = null;
		InputStream processIN = null;
		try 
		{
			process = new ProcessBuilder("adb", "-s", selectedDevice, "ls" , dir).start();
			processIN = process.getInputStream();
			br = new BufferedReader(new InputStreamReader(processIN));
			
			String line;

			if(br.readLine() == null)
			{
				return null;
			}
			br.readLine();
			
			while((line = br.readLine()) != null)
			{
				if(line.length() > 24)
				{
					
					String[] split = line.split(" ");
					
					
					long lastEdit = Long.parseLong(split[2], 16);
					Calendar date = Calendar.getInstance(); 
					date.setTimeInMillis((lastEdit * 1000));
					
					ret.add(new FileObj(split[3], dir, sdf.format(date.getTime()), Long.parseLong(split[1], 16), false, false));					
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			Logger.writeToLog(e.getMessage());
		}
		finally
		{
			try
			{
				if(br != null)
					br.close();
				if(processIN != null)
					processIN.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return ret;
	}


	public boolean setSelectedDevice(String selectedDevice) 
	{
		if(!this.selectedDevice.equals(selectedDevice))
		{
			this.selectedDevice = selectedDevice;		
			return true;
		}
		return false;
	}

	public File pullFile(String path)
	{
		try 
		{
			String dest = saveLocation.getAbsolutePath();
			if(!saveLocation.exists())
			{
				saveLocation.mkdirs();
			}
			String[] splits = path.split("/");
			if(path.endsWith("/"))
			{
				String interrimsPath = "";
				for(int i = 0; i < splits.length-1; i++)
				{
					interrimsPath += splits[i] + "/";
				}
				path = interrimsPath + splits[splits.length-1];
			}
			
			Process p = new ProcessBuilder("adb", "-s", selectedDevice, "pull", path, dest).start();
			p.waitFor();
			Logger.writeToLog(path + " pulled to " + dest);
			
			return new File(dest + "\\" + splits[splits.length-1]);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			Logger.writeToLog("failed to pull " + path);
			Logger.writeToLog(e.getMessage());
			return null;
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
			Logger.writeToLog(e.getMessage());
			return null;
		}
	}
	
	public void pushFile(String source, String destination)
	{
		try 
		{
			Process process = new ProcessBuilder("adb", "-s", selectedDevice, "push", source, destination).start();
			process.waitFor();
			Logger.writeToLog(source + " pushed to " + destination);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			Logger.writeToLog("failed to push " + source);
			Logger.writeToLog(e.getMessage());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void deleteFile(String path)
	{
		Process p;
		InputStream processIN = null;
		BufferedReader reader = null;
		try 
		{
			p = new ProcessBuilder("adb", "-s", selectedDevice, "ls", path).start();
			processIN = p.getInputStream();
			reader = new BufferedReader(new InputStreamReader(processIN));
			String line = reader.readLine();
			if(line == null)
			{
				Logger.writeToLog("deleting " + path);
				Process process = new ProcessBuilder("adb", "-s", selectedDevice, "shell", "rm", path).start();
				process.waitFor();
			}
			else
			{
				reader.readLine();
				ArrayList<String> filesToDelete = new ArrayList<String>();
				
				while((line = reader.readLine()) != null)
				{
					String[] splits = line.split(" ");
					filesToDelete.add(path + "/" + splits[3]);
				}
				if(filesToDelete.size() == 0)
				{
					Logger.writeToLog("deleting " + path);
					Process process = new ProcessBuilder("adb", "-s", selectedDevice, "shell", "rmdir", path).start();
					process.waitFor();
					return;
				}
				else
				{
					for(String toDel : filesToDelete)
					{
						deleteFile(toDel);
					}
					deleteFile(path);
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			Logger.writeToLog("failed to delete " + path);
			Logger.writeToLog(e.getMessage());
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(reader != null)
					reader.close();
				if(processIN != null)
					processIN.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void close()
	{
		
	}


	public String getSaveLocation() 
	{
		if(saveLocation != null)
		{return saveLocation.getAbsolutePath();}
		else { return System.getProperty("user.dir") + "\\pull";}
	}


	public void setSaveLocation(File saveLocation) 
	{
		this.saveLocation = saveLocation;
		probs.setProperty("saveLocation", this.saveLocation.getAbsolutePath());
		try 
		{
			probs.store(new FileWriter(new File("explorer.proberties")), "");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			Logger.writeToLog("failed to save properties");
			Logger.writeToLog(e.getMessage());
		}
	}	
}
