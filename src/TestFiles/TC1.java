package TestFiles;

public class TC1 {
	int foo(int x)
	{
		int a = x;
		x = 10;
		a = x;
		return a;
	}
}
