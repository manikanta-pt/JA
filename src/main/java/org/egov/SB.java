package org.egov;

public class SB {

	StringBuilder sb;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public SB()
	{
		sb=new StringBuilder();
	}

	public SB  a(String s)
	{
		sb.append(s);
		return this;
	}
	public String str()
	{
		return sb.toString();
	}
   public SB a(int count)
   {
	   for(int i=0;i<=count;i++)
		   sb.append(Utility.TAB);
	   return this;
   }
   public SB n(int count)
   {
	   for(int i=0;i<=count;i++)
		   sb.append(Utility.TAB);
	   return this;
   }
  

}
