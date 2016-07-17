package tmaxthomas.lib;

public class CalcTree 
{
	/*
	 * Objects of type CalcTree are used by the CalculusMachine class to construct recursive parse trees
	 * There are three types of CalcTree nodes-two input operator nodes, one-input operator nodes, and number nodes (leaves)
	 * The two-input operator nodes consist of a double val, set to 0, an Operator to represent the operator,
	 * and two branches, leftbranch and rightbranch for the expressions to either side of the operator.
	 * Two-input operator nodes are used for operators such as + or *.
	 * One-input operator nodes consist of a double val, set to 0, an Operator to represent the operator, and
	 * one branch. The leftbranch input is used, and the RightBranch instance variable is set to NULL.
	 * One input operator nodes are used for operators such as sin() and ln() which only take one input.
	 * Numbers are the leaves of the tree. They consist solely of a double val, equal to the number, with
	 * the other instance variables set to NULL. (Note that NULL in this case is a predefined constant 
	 * within the Operators enum, and not just NULL, and is used to trigger the treatment of a leaf node
	 * as such.
	 */
	String val;
	Operators operator;
	CalcTree leftbranch;
	CalcTree rightbranch;
	//Two-input operator constructor
	public CalcTree(Operators operator, CalcTree leftbranch, CalcTree rightbranch)
	{
		val = "0";
		this.operator = operator;
		this.leftbranch = leftbranch;
		this.rightbranch = rightbranch;
	}
	//One-input operator constructor
	public CalcTree(Operators operator, CalcTree leftbranch)
	{
		val = "0";
		this.operator = operator;
		this.leftbranch = leftbranch;
		rightbranch = null;
	}
	//Number (leaf) constructor
	public CalcTree(String val)
	{
		this.val = val;
		operator = Operators.NULL;
		leftbranch = null;
		rightbranch = null;
	}
	//Enum for operators
	public enum Operators
	{
		ADD, SUBTRACT, MULTIPLY, DIVIDE, EXPONENTIATE, NATURAL_LOGARITHM, SINE, COSINE, TANGENT, NULL
	}
	//Method to recursively generate the tree.
	static CalcTree generateTree(String input)
	{
		CalcTree branch;
		String instring = input;
		int closure = 0;
		//If the input string is enclosed by parentheses (Ex: (x+4) but not (x+4)*(x-5) ), removes the parentheses
		for(int a = 0; a < instring.length(); a++)
		{
			if(instring.charAt(a) == '(')
			{
				closure++;
			}
			if(instring.charAt(a) == ')')
			{
				closure--;
			}
			if(closure == 0 && a != instring.length() - 1)
			{
				break;
			}
			if(closure == 0 && a == instring.length() - 1 && instring.charAt(0) == '(')
			{
				instring = instring.substring(1, instring.length() - 1);
				break;
			}
		}
		//Checks to see if it should generate a leaf
		int temp_counter = 0, nop_counter = 0;
		for(int a = 0; a < instring.length(); a++)
		{
			if(CalculusMachine.isANumber(instring.charAt(a)))
			{
				temp_counter++;
			}
			if(temp_counter == instring.length() || (instring.length() == 1 && instring.charAt(0) == 'x') || (instring.length() == 2 && instring.charAt(1) == 'x'))
			{
				branch = new CalcTree(instring);
				return branch;
			}
		}
		/*Each of these for loops deals with one operator, starting with addition and moving up the order of operations.
		 * Doing it in this order ensures that the order of operations is maintained.
		 */
		for(int a = 0; a < instring.length(); a++) //Addition
		{
			if(instring.charAt(a) == '(') //This block of code is used to circumvent areas of the input string within
			{                             //parentheses
				closure = 1;
				while(closure != 0)
				{
					a++;
					if(instring.charAt(a) == '(') { closure++; }
					else if(instring.charAt(a) == ')') { closure--; }
				}
			}
			if(instring.charAt(a) == '+')
			{
				branch = new CalcTree(Operators.ADD, generateTree(instring.substring(0, a)), generateTree(instring.substring(a + 1)));
				return branch;
			}
		}
		/*The loops for subtraction and division parse the input string right to left,
		 * instead of left to right like addition and multiplication. This is because subtraction
		 * and division are not commutative.
		 */
				
		for(int a = instring.length() - 1; a > 0; a--) //Subtraction
		{
			if(instring.charAt(a) == ')')
			{
				closure = 1;
				while(closure != 0)
				{
					a--;
					if(instring.charAt(a) == ')') { closure++; }
					else if(instring.charAt(a) == '(') { closure--; }
				}
			}
			if(instring.charAt(a) == '-' && (CalculusMachine.isANumber(instring.charAt(a - 1)) || instring.charAt(a - 1) == 'x' || instring.charAt(a - 1) == ')'))//Extra logic stuff to not confuse subtraction signs with negative signs
			{
				branch = new CalcTree(Operators.SUBTRACT, generateTree(instring.substring(0, a)), generateTree(instring.substring(a + 1)));
				return branch;
			}
		}
		
		for(int a = 0; a < instring.length(); a++) //Multiplication
		{
			if(instring.charAt(a) == '(')
			{
				closure = 1;
				while(closure != 0)
				{
					a++;
					if(instring.charAt(a) == '(') { closure++; }
					else if(instring.charAt(a) == ')') { closure--; }
				}
			}
			if(instring.charAt(a) == '*')
			{
				branch = new CalcTree(Operators.MULTIPLY, generateTree(instring.substring(0, a)), generateTree(instring.substring(a + 1)));
				return branch;
			}
		}
		
		for(int a = instring.length() - 1; a >= 0; a--) //Division
		{
			if(instring.charAt(a) == ')')
			{
				closure = 1;
				while(closure != 0)
				{
					a--;
					if(instring.charAt(a) == ')') { closure++; }
					else if(instring.charAt(a) == '(') { closure--; }
				}
			}
			if(instring.charAt(a) == '/')
			{
				branch = new CalcTree(Operators.DIVIDE, generateTree(instring.substring(0, a)), generateTree(instring.substring(a + 1)));
				return branch;
			}
		}
		
		for(int a = 0; a < instring.length(); a++) //Exponentiation
		{
			if(instring.charAt(a) == '(')
			{
				closure = 1;
				while(closure != 0)
				{
					a++;
					if(instring.charAt(a) == '(') { closure++; }
					else if(instring.charAt(a) == ')') { closure--; }
				}
			}
			if(instring.charAt(a) == '^')
			{
				branch = new CalcTree(Operators.EXPONENTIATE, generateTree(instring.substring(0, a)), generateTree(instring.substring(a + 1)));
				return branch;
			}
		}
		
		String op = instring.substring(0, 3);
		
		switch(op)
		{
			case "ln(":
				branch = new CalcTree(Operators.NATURAL_LOGARITHM, generateTree(instring.substring(2)));
				return branch;
			case "sin":
				branch = new CalcTree(Operators.SINE, generateTree(instring.substring(3)));
				return branch;
			case "cos":
				branch = new CalcTree(Operators.COSINE, generateTree(instring.substring(3)));
				return branch;
			case "tan":
				branch = new CalcTree(Operators.TANGENT, generateTree(instring.substring(3)));
				return branch;
		}
		branch = new CalcTree("0");
		return branch;
	}
	
	static CalcTree computeDerivative(CalcTree intree)
	{
		CalcTree derivtree = null;
		if(CalculusMachine.isANumber(intree.val.charAt(0)))
		{
			if((intree.val.length() == 1 && Double.valueOf(intree.val) != 0) || (intree.val.length() > 1 && CalculusMachine.isANumber(intree.val.charAt(1))))
			{
				derivtree = new CalcTree("0");
				return derivtree;
			}
		}
		if(!CalculusMachine.isANumber(intree.val.charAt(0)))
		{
			derivtree = new CalcTree("1");
		}
		else if(intree.operator == Operators.ADD)
		{
			derivtree = new CalcTree(Operators.ADD, computeDerivative(intree.leftbranch), computeDerivative(intree.rightbranch));
		}
		else if(intree.operator == Operators.SUBTRACT)
		{
			derivtree = new CalcTree(Operators.SUBTRACT, computeDerivative(intree.leftbranch), computeDerivative(intree.rightbranch));
		}
		else if(intree.operator == Operators.MULTIPLY)
		{
			derivtree = new CalcTree(Operators.ADD, new CalcTree(Operators.MULTIPLY, intree.leftbranch, computeDerivative(intree.rightbranch)), new CalcTree(Operators.MULTIPLY, computeDerivative(intree.leftbranch), intree.rightbranch));
		}
		else if(intree.operator == Operators.DIVIDE)
		{
			derivtree = new CalcTree(Operators.DIVIDE, new CalcTree(Operators.SUBTRACT, new CalcTree(Operators.MULTIPLY, computeDerivative(intree.leftbranch), intree.rightbranch), new CalcTree(Operators.MULTIPLY, intree.leftbranch, computeDerivative(intree.rightbranch))), new CalcTree(Operators.EXPONENTIATE, intree.rightbranch, new CalcTree("2")));
		}
		else if(intree.operator == Operators.EXPONENTIATE)
		{
			if(intree.leftbranch.operator == Operators.NULL && CalculusMachine.isANumber(intree.leftbranch.val.charAt(0)))
			{
				derivtree = new CalcTree(Operators.MULTIPLY, new CalcTree(Operators.MULTIPLY, intree, new CalcTree(Double.toString(Math.log(Double.valueOf(intree.leftbranch.val))))), computeDerivative(intree.rightbranch));
			}
			else if(intree.rightbranch.operator == Operators.NULL && CalculusMachine.isANumber(intree.rightbranch.val.charAt(0)))
			{
				derivtree = new CalcTree(Operators.MULTIPLY, new CalcTree(Operators.MULTIPLY, new CalcTree(intree.rightbranch.val), new CalcTree(Operators.EXPONENTIATE, intree.leftbranch, new CalcTree(Double.toString(Double.valueOf(intree.rightbranch.val) - 1)))), computeDerivative(intree.leftbranch));
			}
		}
		else if(intree.operator == Operators.NATURAL_LOGARITHM)
		{
			derivtree = new CalcTree(Operators.MULTIPLY, new CalcTree(Operators.DIVIDE, new CalcTree("1"), intree.leftbranch), computeDerivative(intree.leftbranch));
		}
		else if(intree.operator == Operators.SINE)
		{
			derivtree = new CalcTree(Operators.MULTIPLY, new CalcTree(Operators.COSINE, intree.leftbranch), computeDerivative(intree.leftbranch));
		}
		else if(intree.operator == Operators.COSINE)
		{
			derivtree = new CalcTree(Operators.MULTIPLY, new CalcTree(Operators.MULTIPLY, new CalcTree("-1"), new CalcTree(Operators.SINE, intree.leftbranch)), computeDerivative(intree.leftbranch));
		}
		else if(intree.operator == Operators.TANGENT)
		{
			derivtree = new CalcTree(Operators.MULTIPLY, new CalcTree(Operators.DIVIDE, new CalcTree("1"), new CalcTree(Operators.EXPONENTIATE, new CalcTree(Operators.COSINE, intree.leftbranch), new CalcTree("2"))), computeDerivative(intree.leftbranch));
		}
		else
		{
			throw new IllegalArgumentException();
		}
		return reduceTree(derivtree);
	}
	
	static CalcTree reduceTree(CalcTree intree)
	{
		CalcTree reducedtree = null;
		if(intree.operator == Operators.NATURAL_LOGARITHM || intree.operator == Operators.SINE || intree.operator == Operators.COSINE || intree.operator == Operators.TANGENT)
		{
			CalcTree temptree = reduceTree(intree.leftbranch);
			if(CalculusMachine.isANumber(temptree.val.charAt(0)))
			{
				switch(intree.operator)
				{
				case NATURAL_LOGARITHM:
					if(Double.valueOf(temptree.val) != 0) reducedtree = new CalcTree(Double.toString(Math.log(Double.valueOf(temptree.val))));
					else throw new IllegalArgumentException("You tried to take the antural log of zero, you idiot");
					break;
				case SINE:
					reducedtree = new CalcTree(Double.toString(Math.sin(Double.valueOf(temptree.val))));
					break;
				case COSINE:
					reducedtree = new CalcTree(Double.toString(Math.cos(Double.valueOf(temptree.val))));
					break;
				case TANGENT:
					reducedtree = new CalcTree(Double.toString(Math.tan(Double.valueOf(temptree.val))));
					break;
				}
			}
			else
			{
				reducedtree = new CalcTree(intree.operator, temptree);
			}
		}
		else if(intree.operator == Operators.ADD)
		{
			CalcTree temptreeL = reduceTree(intree.leftbranch), temptreeR = reduceTree(intree.rightbranch);
			if(temptreeL.operator == Operators.NULL && CalculusMachine.isANumber(temptreeL.val.charAt(0)) && Double.valueOf(temptreeL.val) == 0)
			{
				reducedtree = temptreeR;
			}
			else if(temptreeR.operator == Operators.NULL && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && Double.valueOf(temptreeR.val) == 0)
			{
				reducedtree = temptreeL;
			}
			else if(CalculusMachine.isANumber(temptreeL.val.charAt(0)) && temptreeL.val.charAt(0) != '0' && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && temptreeR.val.charAt(0) != '0')
			{
				reducedtree = new CalcTree(Double.toString(Double.valueOf(temptreeL.val) + Double.valueOf(temptreeR.val)));
			}
			else reducedtree = new CalcTree(Operators.ADD, temptreeL, temptreeR);
		}
		else if(intree.operator == Operators.SUBTRACT)
		{
			CalcTree temptreeL = reduceTree(intree.leftbranch), temptreeR = reduceTree(intree.rightbranch);
			if(temptreeL.operator == Operators.NULL && CalculusMachine.isANumber(temptreeL.val.charAt(0)) && Double.valueOf(temptreeL.val) == 0)
			{
				temptreeR.val = "-" + temptreeR.val;
				reducedtree = temptreeR;
			}
			else if(temptreeR.operator == Operators.NULL && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && Double.valueOf(temptreeR.val) == 0)
			{
				reducedtree = temptreeL;
			}
			else if(CalculusMachine.isANumber(temptreeL.val.charAt(0)) && temptreeL.val.charAt(0) != '0' && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && temptreeR.val.charAt(0) != '0')
			{
				reducedtree = new CalcTree(Double.toString(Double.valueOf(temptreeL.val) - Double.valueOf(temptreeR.val)));
			}
			else reducedtree = new CalcTree(Operators.SUBTRACT, temptreeL, temptreeR);
		}
		else if(intree.operator == Operators.MULTIPLY)
		{
			CalcTree temptreeL = reduceTree(intree.leftbranch), temptreeR = reduceTree(intree.rightbranch);
			if(temptreeL.operator == Operators.NULL && CalculusMachine.isANumber(temptreeL.val.charAt(0)) && Double.valueOf(temptreeL.val) == 0)
			{
				reducedtree = new CalcTree("0");
			}
			else if(temptreeR.operator == Operators.NULL && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && Double.valueOf(temptreeR.val) == 0)
			{
				reducedtree = new CalcTree("0");
			}
			else if(temptreeL.operator == Operators.NULL && CalculusMachine.isANumber(temptreeL.val.charAt(0)) && Double.valueOf(temptreeL.val) == 1)
			{
				reducedtree = temptreeR;
			}
			else if(temptreeR.operator == Operators.NULL && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && Double.valueOf(temptreeR.val) == 1)
			{
				reducedtree = temptreeL;
			}
			else if(CalculusMachine.isANumber(temptreeL.val.charAt(0)) && temptreeL.val.charAt(0) != '0' && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && temptreeR.val.charAt(0) != '0')
			{
				reducedtree = new CalcTree(Double.toString(Double.valueOf(temptreeL.val) * Double.valueOf(temptreeR.val)));
			}
			else reducedtree = new CalcTree(Operators.MULTIPLY, temptreeL, temptreeR);
		}
		else if(intree.operator == Operators.DIVIDE)
		{
			CalcTree temptreeL = reduceTree(intree.leftbranch), temptreeR = reduceTree(intree.rightbranch);
			if(temptreeL.operator == Operators.NULL && CalculusMachine.isANumber(temptreeL.val.charAt(0)) && Double.valueOf(temptreeL.val) == 0)
			{
				reducedtree = new CalcTree("0");
			}
			else if(temptreeR.operator == Operators.NULL && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && Double.valueOf(temptreeR.val) == 0)
			{
				throw new IllegalArgumentException("You tried to divide by zero, you idiot");
			}
			else if(temptreeR.operator == Operators.NULL && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && Double.valueOf(temptreeR.val) == 1)
			{
				reducedtree = temptreeL;
			}
			else if(CalculusMachine.isANumber(temptreeL.val.charAt(0)) && temptreeL.val.charAt(0) != '0' && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && temptreeR.val.charAt(0) != '0')
			{
				reducedtree = new CalcTree(Double.toString(Double.valueOf(temptreeL.val) * Double.valueOf(temptreeR.val)));
			}
			else reducedtree = new CalcTree(Operators.DIVIDE, temptreeL, temptreeR);
		}
		else if(intree.operator == Operators.EXPONENTIATE)
		{
			CalcTree temptreeL = reduceTree(intree.leftbranch), temptreeR = reduceTree(intree.rightbranch);
			if(temptreeL.operator == Operators.NULL && CalculusMachine.isANumber(temptreeL.val.charAt(0)) && Double.valueOf(temptreeL.val) == 0)
			{
				reducedtree = new CalcTree("0");
			}
			else if(temptreeR.operator == Operators.NULL && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && Double.valueOf(temptreeR.val) == 0)
			{
				reducedtree = new CalcTree("1");
			}
			else if(temptreeR.operator == Operators.NULL && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && Double.valueOf(temptreeR.val) == 1)
			{
				reducedtree = temptreeL;
			}
			else if(CalculusMachine.isANumber(temptreeL.val.charAt(0)) && temptreeL.val.charAt(0) != '0' && CalculusMachine.isANumber(temptreeR.val.charAt(0)) && temptreeR.val.charAt(0) != '0')
			{
				reducedtree = new CalcTree(Double.toString(Math.pow(Double.valueOf(temptreeL.val), Double.valueOf(temptreeR.val))));
			}
			else reducedtree = new CalcTree(Operators.EXPONENTIATE, temptreeL, temptreeR);
		}
		else
		{
			reducedtree = intree;
		}
		return reducedtree;
	}
	
	static String deconstructTree(CalcTree intree, int layer)
	{
		String output = "";
		if(!CalculusMachine.isANumber(intree.val.charAt(0)) || Double.valueOf(intree.val) != 0)
		{
			output = intree.val;
		}
		if(intree.operator == Operators.NATURAL_LOGARITHM || intree.operator == Operators.SINE || intree.operator == Operators.COSINE || intree.operator == Operators.TANGENT)
		{
			switch(intree.operator)
			{
			case NATURAL_LOGARITHM:
				output = "ln(" + deconstructTree(intree.leftbranch, 0) + ")";
				break;
			case SINE:
				output = "sin(" + deconstructTree(intree.leftbranch, 0) + ')';
				break;
			case COSINE:
				output = "cos(" + deconstructTree(intree.leftbranch, 0) + ")";
				break;
			case TANGENT:
				output = "cos(" + deconstructTree(intree.leftbranch, 0) + ")";
				break;
			}
		}
		if(intree.operator == Operators.ADD)
		{
			if(layer == 0) output =  deconstructTree(intree.leftbranch, 0) + "+" + deconstructTree(intree.rightbranch, 0);
			
			else output = "(" + deconstructTree(intree.leftbranch, 0) + "+" + deconstructTree(intree.rightbranch, 0) + ")";
		}
		else if(intree.operator == Operators.SUBTRACT)
		{
			if(layer < 2) output = deconstructTree(intree.leftbranch, 1) + "-" + deconstructTree(intree.rightbranch, 1);
			
			else output = "(" + deconstructTree(intree.leftbranch, 1) + "-" + deconstructTree(intree.rightbranch, 1) + ")";
		}
		else if(intree.operator == Operators.MULTIPLY)
		{
			if(layer < 3) output = deconstructTree(intree.leftbranch, 2) + "*" + deconstructTree(intree.rightbranch, 2);
			
			else output = "(" + deconstructTree(intree.leftbranch, 2) + "*" + deconstructTree(intree.rightbranch, 2) + ")";
		}
		else if(intree.operator == Operators.DIVIDE)
		{
			if(layer < 4) output = deconstructTree(intree.leftbranch, 3) + "/" + deconstructTree(intree.rightbranch, 3);
			
			else output = "(" + deconstructTree(intree.leftbranch, 3) + "/" + deconstructTree(intree.rightbranch, 3) + ")";
		}
		else if(intree.operator == Operators.EXPONENTIATE)
		{
			if(layer < 5) output = deconstructTree(intree.leftbranch, 4) + "^" + deconstructTree(intree.rightbranch, 4);
			
			else output = "(" + deconstructTree(intree.leftbranch, 4) + "^" + deconstructTree(intree.rightbranch, 4) + "(";
		}
		
		for(int a = 0; a < output.length(); a++)
		{
			if(output.charAt(a) == '*')
			{
				if(output.charAt(a+1) == '(' || output.charAt(a+1) == 'x')
				{
					output = CalculusMachine.shunt(1, a, output, '\0');
				}
			}
		}
		return output;
	}
}
