package de.rrzn.mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

public class Main 
{

	static String everything=null;
	static Date evdatestart=null,evdateend=null;
	static DateFormat dflong = new SimpleDateFormat("yyyyMMdd'T'HHmmss");       //("E, MMM dd yyyy HH:mm:ss");  //20151023T083000
	static DateFormat dfshort = new SimpleDateFormat("yyyyMMdd"); 
	static int freq=0,count=0;
	static boolean ganztag=false;
	static int summes=0,summeg=0,summe=0;
	static String cal="";
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		cal=args[0];
		try(FileInputStream inputStream = new FileInputStream(args[0])) {
		    //Session IOUtils;
			everything = IOUtils.toString(inputStream);
		}		
		
		parseCalDAV(everything);
	}
	
	public static void parseCalDAV(String caldav)
	{
		
		StringTokenizer st=new StringTokenizer(caldav,"\n\r",false);
		
		while(st.hasMoreElements())
		{
			String line=st.nextToken();
			
			if(line.contains("BEGIN:VCALENDAR"))
			{
//				System.out.println("Calendar start");
			
				while(st.hasMoreElements())
				{
					String calline=st.nextToken();
					
					if(calline.contains("END:VCALENDAR"))
					{
//						System.out.println("Calendar end");
						break;
					}
					
					if(calline.contains("BEGIN:VEVENT"))
					{
						String summary="";
						evdatestart=null;
						evdateend=null;
						freq=0;
						count=0;
						ganztag=false;
						
//						System.out.println("Event start");
						
						while(st.hasMoreElements())
						{
							String evline=st.nextToken();
							
							if(evline.contains("END:VEVENT"))
							{
								if(evdatestart!=null && evdateend!=null)
								{
	//								System.out.println("Event end");
									if(!ganztag && (evdateend.getDay()!=evdatestart.getDay()) && freq>0)
									{	
										summes++;
										System.out.println("S "+summary+" / start="+evdatestart+" end="+evdateend+" freq="+freq+" count="+count+" ganztag="+ganztag);
									}
									if(ganztag && (evdateend.getDay()>evdatestart.getDay()+1) && freq>0)
									{			
										summeg++;
										System.out.println("G "+summary+" / start="+evdatestart+" end="+evdateend+" freq="+freq+" count="+count+" ganztag="+ganztag);
									}
								}
								break;
							}
							
							if(evline.contains("SUMMARY:"))
							{
								summary=evline.substring(evline.indexOf("SUMMARY:")+8);
//								System.out.println("Summary: "+summary);
							}
							
							if(evline.contains("DTSTART:")||evline.contains("DTSTART;"))
							{
								String datestring=evline.substring(evline.indexOf(":")+1);
								
//								System.out.print("Date start "+datestring);
						        try
						        {
						            //format() method Formats a Date into a date/time string. 
						            if(datestring.contains("T"))
						            	evdatestart = dflong.parse(datestring);
						            else
						            {
						            	evdatestart = dfshort.parse(datestring);
						            	ganztag=true;
						            }
						           						            
//						            System.out.println(evdatestart.toString());
						            
						        }catch(Exception e)
						        {
						    	    System.out.println(" parse start date failed ("+evline+","+datestring+")");
						        }						        
							}
							
							if(evline.contains("DTEND:")||evline.contains("DTEND;"))
							{
								String datestring=evline.substring(evline.indexOf(":")+1);
								
//								System.out.print("Date end "+datestring);
						        try
						        {
						            //format() method Formats a Date into a date/time string. 
						            if(datestring.contains("T"))
						            	evdateend = dflong.parse(datestring);
						            else
						            	evdateend = dfshort.parse(datestring);
						           						            
//						            System.out.println(evdatestart.toString());
						            
						        }catch(Exception e)
						        {
						    	    System.out.println(" parse end date failed ("+evline+","+datestring+")");
						        }						        
							}
							
							if(evline.contains("RRULE:"))
							{
//								System.out.println("rrule "+evline);
								StringTokenizer st2=new StringTokenizer(evline.substring(6),";:\n\r",false);
								
								while(st2.hasMoreTokens())
								{									
									String rule=st2.nextToken();
//									System.out.println(rule);
									
									if(rule.startsWith("FREQ="))
									{
										String value=rule.substring(5);
//										System.out.println(value);
										if(value.equals("DAILY"))
											freq=1;
										else if(value.equals("WEEKLY"))
											freq=2;
										else if(value.equals("MONTHLY"))
											freq=3;
										else if(value.equals("YEARLY"))
											freq=4;
										
//										System.out.println(freq);
									}
									
									if(rule.startsWith("COUNT="))
									{
										String value=rule.substring(6);
										
										try{
											count=Integer.parseInt(value);
										}catch(Exception e)
										{
											System.out.println("parse count failed");
										}
										
									}
									
									if(rule.startsWith("INTERVAL="))
									{
										String value=rule.substring(9);
										
										try{
											count=Integer.parseInt(value);
										}catch(Exception e)
										{
											System.out.println("parse count failed");
										}
										
									}
										
								}
							}
						}
					}
				}
			}
			
			
		}
		System.out.println("s "+summes+" "+cal);
		System.out.println("g "+summeg+" "+cal);
		System.out.println("# "+(summes+summeg)+" "+cal);
	}

}
