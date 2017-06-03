package com.discovery;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;

public class Discovery {
	
	public static HashSet <String> hnames = new HashSet <String>();
	private static final Pattern PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	
	public static void main (String args[]) throws IOException
	{
		String line;
		String[] line_to_print=new String[10];
		ArrayList <String> users = new ArrayList<String>();
		ArrayList <String> directory = new ArrayList<String>();
		//String command1="cut -d: -f1 /etc/passwd";
		//String command2="who";
		String command3="cat /etc/passwd";
		Process p=Runtime.getRuntime().exec(command3);
		try
		{
			BufferedReader bufferedReader=new BufferedReader (new InputStreamReader (p.getInputStream()));
		
			while ((line=bufferedReader.readLine()) !=null)
			{
				line_to_print=line.split(":");				
				users.add(line_to_print[0]);
				directory.add(line_to_print[5]);
			}
			p.waitFor();
			
			p.destroy();
			bufferedReader.close();
			
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		for (int i=0;i<users.size();i++)
		{			
			readHostsFrom_sshconfig(users.get(i),directory.get(i));
			readHostsFrom_AuthorizedKeys(users.get(i),directory.get(i));
			readHostsFrom_ssh_known_hosts(users.get(i),directory.get(i));
		}
		readHostsFrom_etcHosts ();
		readHostsFrom_etc_ssh_config();
		readHostsFrom_etc_ssh_known_hosts();
		display_host_names();
	}
	
	public static void readHostsFrom_etcHosts()
	{
		String filename="/etc/hosts";
		String line=null;
		String line_to_print=null;
		String [] name = new String[10];
		
		try
		{
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while ((line=bufferedReader.readLine())!=null)
			{
				if (!line.startsWith("#"))
				{
						line_to_print=line;
						if (!(line_to_print.isEmpty()))
						{								
								name=line_to_print.split("\\s+");
								for (int i=1;i<name.length;i++)
								{
									if (!(validateIP(name[i])))
									{
										if (!(name[i].contains("::")))
										{
											if (!(validateIP(name[i])))
											{
												hnames.add(name[i]);
											}
										}
									}
								}
							
						}
					}
				}
				bufferedReader.close();
				fileReader.close();
		}
		catch(Exception e)
		{
			//System.out.println(e);
		}
		
	}
	
	public static void readHostsFrom_sshconfig (String user, String directory)
	{
		String filename=directory+"/.ssh/config";
		String line=null;
		String [] name = new String[10];
		
		try
		{
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while ((line=bufferedReader.readLine())!=null)
			{
				if (!(line.startsWith("#")))
				{
					if ((line.startsWith("Hostname")) || (line.startsWith("HostName")) || (line.startsWith("HOSTNAME")) || (line.startsWith("hostname")) || (line.startsWith("hostName")) || (line.startsWith("Host") || (line.startsWith("host"))))
							{
								name=line.split("\\s+");
								for (int i=1;i<name.length;i++)
								{
									if (!(name[i].equals("*")))
									{
										if (!(validateIP(name[i])))
										{
											hnames.add(name[i]);
										}
									}
								}
								
							}
				}
			}
			bufferedReader.close();
			fileReader.close();
		
		}
		
		catch(Exception e)
		{
			//System.out.println(e);
		}
		
	}
	
	
	public static void readHostsFrom_AuthorizedKeys (String user, String directory)
	{
		String line="";
		String line1="";
		String filename=directory+"/.ssh/authorized_keys";
		String line_to_print = "";
		String line_to_print1 = "";
		String[] name = new String[10];
		String[] name1 = new String [10];
		String[] name2 = new String [10];
		String[] name3 = new String [10];
		
		try {
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				if (!(line.startsWith("#"))) {
					if (!(line.isEmpty()))
					{
						if (line.contains("from")) {
							Pattern p = Pattern.compile("\"([^\"]*)\"");
							Matcher m = p.matcher(line);
							while (m.find()) {
								line_to_print = (m.group(1));
							}
							name = line_to_print.split(",");
							for (int i = 0; i < name.length; i++) {
								
								if (!(validateIP(name[i])))
								{		
									hnames.add(name[i]);
								}

							}
						}
						
						if (line.contains("permitopen"))
						{
							name2 = line.split(",");
							for (int i=0;i<name2.length;i++)
							{
								Pattern p1 = Pattern.compile("\"([^\"]*)\"");
								Matcher m1 = p1.matcher(name2[i]);
								while (m1.find())
								{
									line_to_print1=(m1.group(1));
								}
								if (line_to_print1.contains(":"))
								{
									name3=line_to_print1.split(":");
									if (!(validateIP(name3[0])))
									{
										hnames.add(name3[0]);
									}
								}
								
							}
						}
						
						if (line.contains("@"))
						{
							name1=line.split("@");
							if (!(validateIP(name1[1])))
							{
								hnames.add(name1[1]);
							}
						}
					}
				}
				
			}
			bufferedReader.close();
		}
			catch (Exception e) 
			{
				//System.out.println(e);
			}
		}
	
	public static void readHostsFrom_etc_ssh_config ()
	{
		String [] name = new String [10];
		String line=null;
		String filename="/etc/ssh/ssh_config";
		
		try
		{
			FileReader fileReader = new FileReader (filename);
			BufferedReader bufferedReader = new BufferedReader (fileReader);
			
			while ((line=bufferedReader.readLine())!=null)
			{
				if (!(line.startsWith("#")))
						{
							if ((line.startsWith("Hostname")) || (line.startsWith("HostName")) || (line.startsWith("HOSTNAME")) || (line.startsWith("hostname")) || (line.startsWith("hostName")) || (line.startsWith("Host")) || (line.startsWith("host")))
									{
										name=line.split("\\s+");
										for (int i=1;i<name.length;i++)
										{
											if (!(name[i].equals("*")))
											{
												if (!(validateIP(name[i])))
												{
													hnames.add(name[i]);
												}
											}
										}
										
									}
						}
			}
			
			fileReader.close();
			bufferedReader.close();		
	}
		catch(Exception e)
		{
			//System.out.println(e);
		}
	}
	
	public static void readHostsFrom_ssh_known_hosts(String user, String directory)
	{
		String filename=directory+"/.ssh/known_hosts";
		String [] name=new String [10];
		String line=null;
		String [] ip=new String[10];
		String [] sep=new String[10];
		try
		{
			FileReader fileReader = new FileReader (filename);
			BufferedReader bufferedReader = new BufferedReader (fileReader);
			
			while ((line = bufferedReader.readLine()) != null) {
				if (!(line.startsWith("#"))) {
					if (!(line.startsWith("@"))) {
						if (!(line.startsWith("|"))) {
							if (!(line.startsWith("["))) {
								if (!(line.startsWith("("))) {							
									name = line.split(",");
									for (int i = 0; i < name.length; i++) {
										ip = name[i].split(".");
										if (ip.length != 4) {
											if (i == name.length - 1) {
												sep = name[i].split(" ");
												if (!(validateIP(sep[0])))
													hnames.add(sep[0]);
											} else {
												hnames.add(name[i]);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			//System.out.println(e);
		}
	}

	
	public static void readHostsFrom_etc_ssh_known_hosts ()
	{
		String line;
		String filename="/etc/ssh/ssh_known_hosts";
		String [] name = new String [10];
		String []ip=new String[10];
		String [] sep=new String[10];
		try
		{
			FileReader fileReader = new FileReader (filename);
			BufferedReader bufferedReader = new BufferedReader (fileReader);
			
			while ((line = bufferedReader.readLine()) != null) {
				if (!(line.startsWith("#"))) {
					if (!(line.startsWith("@"))) {
						if (!(line.startsWith("|"))) {
							if (!(line.startsWith("["))) {
								if (!(line.startsWith("("))) {
									name = line.split(",");
									for (int i = 0; i < name.length; i++) {
										ip = name[i].split(".");
										if (ip.length != 4) {
											if (i == name.length - 1) {
												sep = name[i].split(" ");
												if (!(validateIP(sep[0])))
													hnames.add(sep[0]);
											} else {
												hnames.add(name[i]);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			//System.out.println(e);
		}
	}
	
	public static void display_host_names ()
	{
		Iterator<String> i = hnames.iterator();
		
			while (i.hasNext())
			{
				{
				System.out.println(i.next());
				}
			}
		
		
		
	}
	
	
	public static boolean validateIP(String ip) {
	    return PATTERN.matcher(ip).matches();
	}
	

}
