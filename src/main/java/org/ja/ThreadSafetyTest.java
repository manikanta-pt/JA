package org.ja;

public class ThreadSafetyTest implements  Runnable {
	public void run() {
		StringBufferVsBuilder bb=new StringBufferVsBuilder();
		bb.bufferTest(1000);
		bb.builderTest(1000);
		bb.stringTest(1000);
		
	}

	public static void main(String[] args)
	{
		ThreadSafetyTest t1=new ThreadSafetyTest();
		t1.run();
		
		ThreadSafetyTest t2=new ThreadSafetyTest();
		t2.run();
		
		ThreadSafetyTest t3=new ThreadSafetyTest();
		t3.run();
	}
	
	public void bufferTest()
	{
		ThreadSafetyTest t1=new ThreadSafetyTest();
		t1.run();
		
		ThreadSafetyTest t2=new ThreadSafetyTest();
		t2.run();
		
		ThreadSafetyTest t3=new ThreadSafetyTest();
		t3.run();
	}
	
	public void builderTest()
	{
		ThreadSafetyTest t1=new ThreadSafetyTest();
		t1.run();
		
		ThreadSafetyTest t2=new ThreadSafetyTest();
		t2.run();
		
		ThreadSafetyTest t3=new ThreadSafetyTest();
		t3.run();
	}
	
	

}
