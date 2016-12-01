package TestFiles;

public class TC5 {
	int bar(int x, int y)
	{
		int a = 0;
		int res = 10;
		if(res>10)
		{
			a = x+y;
		}
		else
		{
			a = 20;
		}
		if(a==20)
		{
			res = a;
			return a;
		}
		return res;
	}
}
