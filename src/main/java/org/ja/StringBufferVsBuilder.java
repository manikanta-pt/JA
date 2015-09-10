package org.ja;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class StringBufferVsBuilder {
	public SimpleDateFormat withMillisec= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	Random r=new Random();
	
	public void builderTest(int max)
	{
		StringBuilder builder=new StringBuilder();
		
		r.nextFloat();
		Date startTime = new Date();
		System.out.println("Builder Start time :"+withMillisec.format(startTime));
		for(int i=0;i<=max;i++)
		{
			builder.append(r.nextFloat());
		}
		Date endTime = new Date();
		//System.out.println("Text"+builder);
		System.out.println("Builder end time :"+withMillisec.format(endTime));
		System.out.println("Time Difference :"+(endTime.getTime()-startTime.getTime()));
		System.out.println("=======================================");
		
	}
	
	
	public void stringTest(int max)
	{
		String builder="";
		Date startTime = new Date();
		System.out.println("String Start time :"+withMillisec.format(startTime));
		for(int i=0;i<=max;i++)
		{
			builder=builder+r.nextFloat();
		}
		Date endTime = new Date();
		//System.out.println("Text"+builder);
		System.out.println("String end time :"+withMillisec.format(endTime));
		System.out.println("Time Difference :"+(endTime.getTime()-startTime.getTime()));
		System.out.println("=======================================");
		
	}
	
	
	public void bufferTest(int max)
	{
		
		StringBuffer buffer=new StringBuffer();
		Date startTime = new Date();
		System.out.println("buffer Start time :"+withMillisec.format(startTime));
		for(int i=0;i<=max;i++)
		{
			buffer.append(r.nextFloat());
		}
		Date endTime = new Date();
		//System.out.println("Text"+buffer);
		System.out.println("buffer end time :"+withMillisec.format(endTime));
		System.out.println("Time Difference :"+(endTime.getTime()-startTime.getTime()));
		System.out.println("=======================================");
		
	}

	public static void main(String[] args) {

		StringBufferVsBuilder bb=new StringBufferVsBuilder();
		System.out.println("Running for 1000");
		bb.bufferTest(1000);
		bb.builderTest(1000);
		bb.stringTest(1000);
		System.out.println("Running for 100000");
		bb.bufferTest(100000);
		bb.builderTest(100000);
		bb.stringTest(100000);

	}

}
