package tmaxthomas.lib;

class Main 
{	
	public static void main(String[] args)
	{
		String foo = "sin(x)^x";
		System.out.println(CalcTree.deconstructTree(CalcTree.computeDerivative(CalcTree.generateTree(CalculusMachine.format(foo))), 0));
	}
}