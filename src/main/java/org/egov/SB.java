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

}
