package TestFiles;

public class TC6 {
	void doit(int x)
	{
		int a = 10;
		x = x+a;
		a = x-a;
		x = x-a;

		x = x+a;
		a = x-a;
		x = x-a;

		doit (x);
		
		System.out.print (a);
	}
}
