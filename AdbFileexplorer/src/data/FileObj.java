package data;


public class FileObj 
{
	private String path, name, lastEdit;
	private long size;
	private boolean dir, link;
	
	public FileObj(String name, String path, String lastEdit, long size, boolean dir, boolean link)
	{
		this.name = name;
		this.path = path;
		this.lastEdit = lastEdit;
		this.size = size;
		this.dir = dir;
		this.link = link;
	}


	public boolean isLink() {
		return link;
	}


	public boolean isDir() {
		return dir;
	}


	public String getPath() {
		return path;
	}


	public String getName() {
		return name;
	}


	public String getLastEdit() {
		return lastEdit;
	}


	public long getSize() {
		return size;
	}
	
	public Object[] getDataForTable()
	{
		return new Object[] {name , size, lastEdit};
	}

}
