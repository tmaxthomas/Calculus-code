package tmaxthomas.lib;

import java.lang.Math;

class StringParser 
{
	String equation;
	int PRECISION;
	
	StringParser(String equation, int PRECISION)
	{
		this.equation = format(equation);
		this.PRECISION = PRECISION;
	}
	
	//The enum is used, with a switch, to choose which operation to execute based on the operator found.
	private enum Operators
	{
		ADD, SUBTRACT, MULTIPLY, DIVIDE, EXPONENTIATE, SINE, COSINE, TANGENT, SECANT, COSECANT, COTANGENT, ARCSINE, ARCCOSINE, ARCTANGENT, SQUARE_ROOT, NATURAL_LOGARITHM, NULL
	}
	
	//This method reformats the input string into a form the evaluate method can use.
	private String format(String input)
	{
		String output = input;
		for(int a = 0; a < output.length(); a++)
		{
			if(output.charAt(a) == ' ')
			{
				output = shunt(1, a, output, ' ');
			}
		}
		
		output = output.replaceAll("asin\\(", "\\(1q");
		output = output.replaceAll("acos\\(", "\\(1w");
		output = output.replaceAll("atan\\(", "\\(1o");
		output = output.replaceAll("sin\\(", "\\(1&");
		output = output.replaceAll("cos\\(", "\\(1$");
		output = output.replaceAll("tan\\(", "\\(1#");
		output = output.replaceAll("sec\\(", "\\(1y");
		output = output.replaceAll("csc\\(", "\\(1u");
		output = output.replaceAll("cot\\(", "\\(1i");
		output = output.replaceAll("sqrt\\(", "\\(1s");
		output = output.replaceAll("ln\\(", "\\(1p");
		
		if(output.charAt(0) != '(' || output.charAt(output.length() - 1) != ')')
		{
			String temp1 = "(";
			output = temp1.concat(output.concat(")"));
		}
		
		int closure = 1;
		
		for(int a = 1; a < output.length(); a++)
		{
			if(output.charAt(a) == 'x' || output.charAt(a) == '(')
			{
				if(isANumber(output.charAt(a - 1)) || (output.charAt(a) == '(' && output.charAt(a - 1) == ')'))
				{
					output = shunt(0, a, output, '*');
					a++;
				}
			}
		}
		//this for loop deals with functions at the top of the order of operations-the functions that end up the lowest on the evaluation hierarchy.
		for(int a = 0; a < output.length(); a++)
		{
			if(output.charAt(a) == '^' || output.charAt(a) == '&' || output.charAt(a) == '$' || output.charAt(a) == '#' || output.charAt(a) == 's' || output.charAt(a) == 'q' || output.charAt(a) == 'w' || output.charAt(a) == 'e' || output.charAt(a) == 'r' || output.charAt(a) == 't' || output.charAt(a) == 'y' || output.charAt(a) == 'u' || output.charAt(a) == 'i' || output.charAt(a) == 'o')
			{
				boolean needparentheses = false;
				int pos = a;
				while(((output.charAt(pos) != '(') && (output.charAt(pos) != ')')) && !needparentheses)
				{
					pos++;
					if(!isANumber(output.charAt(pos)) && output.charAt(pos) != 'x' && ((output.charAt(pos) != '(') && (output.charAt(pos) != ')')))
					{
						needparentheses = true;
					}
				}
				
				if(!needparentheses && output.charAt(pos) == '(')
				{
					while(closure != 0)
					{
						pos++;
						if(output.charAt(pos) == '(')
						{
							closure++;
						}
						
						else if(output.charAt(pos) == ')')
						{
							closure--;
						}
					}
					
					if(pos + 1 != output.length())
					{
						if(output.charAt(pos + 1) != ')')
						{
							needparentheses = true;
						}
					}
				}
				
				pos = a;
				
				while(((output.charAt(pos) != '(') && (output.charAt(pos) != ')')) && !needparentheses)
				{
					pos--;
					if(!isANumber(output.charAt(pos)) && output.charAt(pos) != 'x' && ((output.charAt(pos) != '(') && (output.charAt(pos) != ')')))
					{
						needparentheses = true;
					}
				}
				
				if(!needparentheses && output.charAt(pos) == ')')
				{
					closure = 1;
					while(closure != 0)
					{
						pos--;
						if(output.charAt(pos) == ')')
						{
							closure++;
						}
						
						else if(output.charAt(pos) == '(')
						{
							closure--;
						}
					}
					if(output.charAt(pos - 1) != '(')
					{
						needparentheses = true;
					}
				}
				
				if(needparentheses)
				{
					pos = a + 1;
					while(isANumber(output.charAt(pos)) || output.charAt(pos) == '(' || output.charAt(pos) == 'x')
					{
						if(output.charAt(pos) == '(')
						{
							closure = 1;
							while(closure != 0)
							{
								pos++;
								if(output.charAt(pos) == '(')
								{
									closure++;
								}
								
								else if(output.charAt(pos) == ')')
								{
									closure--;
								}
							}
						}
						
						else
						{
							pos++;
						}
					}
					output = shunt(0, pos, output, ')');
					pos = a - 1;
					while(isANumber(output.charAt(pos)) || output.charAt(pos) == ')' || output.charAt(pos) == 'x')
					{
						if(output.charAt(pos) == ')')
						{
							closure = 1;
							while(closure != 0)
							{
								pos--;
								if(output.charAt(pos) == ')')
								{
									closure++;
								}
								
								else if(output.charAt(pos) == '(')
								{
									closure--;
								}
							}
						}
						
						else
						{
							pos--;
						}
					}
					output = shunt(0, pos + 1, output, '(');
					a++;
				}
			}
		}
		
		//This for loop deals with the next step of the order of operations-multiplication and division. In all other regards, it is an exact copy of the initial loop.
		for(int a = 0; a < output.length(); a++)
		{
			if(output.charAt(a) == '*' || output.charAt(a) == '/')
			{
				boolean needparentheses = false;
				int pos = a;
				
				while(((output.charAt(pos) != '(') && (output.charAt(pos) != ')')) && !needparentheses)
				{
					pos++;
					if(!isANumber(output.charAt(pos)) && output.charAt(pos) != 'x' && ((output.charAt(pos) != '(') && (output.charAt(pos) != ')')))
					{
						needparentheses = true;
					}
				}
				
				
				if(!needparentheses && output.charAt(pos) == '(')
				{
					closure = 1;
					while(closure != 0)
					{
						pos++;
						if(output.charAt(pos) == '(')
						{
							closure++;
						}
						
						else if(output.charAt(pos) == ')')
						{
							closure--;
						}
						
					}
					if(pos + 1 == output.length())
					{
						needparentheses = true;
					}
					else if(output.charAt(pos + 1) != ')')
					{
						needparentheses = true;
					}
				}
				
				pos = a;
				
				while(((output.charAt(pos) != '(') && (output.charAt(pos) != ')')) && !needparentheses)
				{
					pos--;
					if(!isANumber(output.charAt(pos)) && output.charAt(pos) != 'x' && ((output.charAt(pos) != '(') && (output.charAt(pos) != ')')))
					{
						needparentheses = true;
					}
				}
				
				
				if(!needparentheses && output.charAt(pos) == ')')
				{
					closure = 1;
					while(closure != 0)
					{
						pos--;
						if(output.charAt(pos) == ')')
						{
							closure++;
						}
						
						else if(output.charAt(pos) == '(')
						{
							closure--;
						}
						
					}
					if(output.charAt(pos - 1) != '(')
					{
						needparentheses = true;
					}
				}
				
				if(needparentheses)
				{
					
					pos = a + 1;
					while(isANumber(output.charAt(pos)) || output.charAt(pos) == '(' || output.charAt(pos) == 'x')
					{
						
						if(output.charAt(pos) == '(')
						{
							
							closure = 1;
							while(closure != 0)
							{
								pos++;
								if(output.charAt(pos) == '(')
								{
									closure++;
								}
								
								else if(output.charAt(pos) == ')')
								{
									closure--;
								}
								
							}
						}
						
						else
						{
							pos++;
						}
					}
					output = shunt(0, pos, output, ')');
					pos = a - 1;
					while(isANumber(output.charAt(pos)) || output.charAt(pos) == ')' || output.charAt(pos) == 'x')
					{
						
						if(output.charAt(pos) == ')')
						{
							
							closure = 1;
							while(closure != 0)
							{
								pos--;
								if(output.charAt(pos) == ')')
								{
									closure++;
								}
								
								else if(output.charAt(pos) == '(')
								{
									closure--;
								}
								
							}
						}
						
						else
						{
							pos--;
						}
					}
					output = shunt(0, pos + 1, output, '(');
					a++;
				}
			}
		}
		
		//This for loop deals with the operators at the bottom of the order of operations-addition and subtraction. In all other regards it is identical to the two previous for loops.
		for(int a = 0; a < output.length(); a++)
		{
			
			if(output.charAt(a) == '+' || output.charAt(a) == '-')
			{
				boolean needparentheses = false;
				int pos = a;
				while(((output.charAt(pos) != '(') && (output.charAt(pos) != ')')) && !needparentheses)
				{
					pos++;
					if(!isANumber(output.charAt(pos)) && output.charAt(pos) != 'x' && ((output.charAt(pos) != '(') && (output.charAt(pos) != ')')))
					{
						needparentheses = true;
					}
				}
				
				if(!needparentheses && output.charAt(pos) == '(')
				{
					closure = 1;
					while(closure != 0)
					{
						pos++;
						if(output.charAt(pos) == '(')
						{
							closure++;
						}
						
						else if(output.charAt(pos) == ')')
						{
							closure--;
						}
					}
					if(output.charAt(pos + 1) != ')')
					{
						needparentheses = true;
					}
				}
				
				pos = a;
				
				while(((output.charAt(pos) != '(') && (output.charAt(pos) != ')')) && !needparentheses)
				{
					pos--;
					if(!isANumber(output.charAt(pos)) && output.charAt(pos) != 'x' && ((output.charAt(pos) != '(') && (output.charAt(pos) != ')')))
					{
						needparentheses = true;
					}
				}
				
				if(!needparentheses && output.charAt(pos) == ')')
				{
					closure = 1;
					while(closure != 0)
					{
						pos--;
						if(output.charAt(pos) == ')')
						{
							closure++;
						}
						
						else if(output.charAt(pos) == '(')
						{
							closure--;
						}
					}
					if(output.charAt(pos - 1) != '(')
					{
						needparentheses = true;
					}
				}
				
				if(needparentheses)
				{
					pos = a + 1;
					while(isANumber(output.charAt(pos)) || output.charAt(pos) == '(' || output.charAt(pos) == 'x')
					{
						if(output.charAt(pos) == '(')
						{
							closure = 1;
							while(closure != 0)
							{
								pos++;
								if(output.charAt(pos) == '(')
								{
									closure++;
								}
								
								else if(output.charAt(pos) == ')')
								{
									closure--;
								}
							}
						}
						
						else
						{
							pos++;
						}
					}
					output = shunt(0, pos, output, ')');
					pos = a - 1;
					while(isANumber(output.charAt(pos)) || output.charAt(pos) == ')' || output.charAt(pos) == 'x')
					{
						if(output.charAt(pos) == ')')
						{
							closure = 1;
							while(closure != 0)
							{
								pos--;
								if(output.charAt(pos) == ')')
								{
									closure++;
								}
								
								else if(output.charAt(pos) == '(')
								{
									closure--;
								}
							}
						}
						
						else
						{
							pos--;
						}
					}
					output = shunt(0, pos + 1, output, '(');
					a++;
				}
			}
		}
		output = output.substring(1, output.length() - 1);
		return output;
	}
	
	private String shunt(int direction, int index, String input, char insertchar)
	{
		String output = "";
		if(direction == 0)
		{
			String temp1, temp2 = Character.toString(insertchar), temp3;
			temp1 = input.substring(0, index);
			temp3 = input.substring(index);
			output = output.concat(temp1.concat(temp2.concat(temp3)));
		}
		
		else
		{
			String temp1, temp2;
			temp1 = input.substring(0, index);
			temp2 = input.substring(index + 1);
			output = output.concat(temp1.concat(temp2));
		}
		
		return output;
	}
	
	private boolean isANumber(char input)
	{
		boolean result;
		if((input < 48 || input > 57) && input != 46)
		{
			result = false;
		}
		else
		{
			result = true;
		}
		return result;
	}
	
	private String chomp(String input)
	{
		return input.substring(1, input.length() - 1);
	}
	
	public double evaluateString(double x)
	{
		double output = 0, leftside, rightside;
		Operators operator = Operators.NULL;
		int masteropat = 0;
		boolean opfound = false;
		String equationtemp = equation;
		equationtemp = equation.replaceAll("x", String.valueOf(x));
		while(!opfound)
		{
			if(equationtemp.charAt(masteropat) == '(')
			{
				int closure = 1;
				while(closure != 0)
				{
					masteropat++;
					if(equationtemp.charAt(masteropat) == '(')
					{
						closure++;
					}
					
					else if(equationtemp.charAt(masteropat) == ')')
					{
						closure--;
					}
				}
			}
			
			else if(!isANumber(equationtemp.charAt(masteropat)))
			{
				opfound = true;
				if(equationtemp.charAt(masteropat) == '+') {operator = Operators.ADD;}
				else if(equationtemp.charAt(masteropat) == '-') {operator = Operators.SUBTRACT;}
				else if(equationtemp.charAt(masteropat) == '*') {operator = Operators.MULTIPLY;}
				else if(equationtemp.charAt(masteropat) == '/') {operator = Operators.DIVIDE;}
				else if(equationtemp.charAt(masteropat) == '^') {operator = Operators.EXPONENTIATE;}
				else if(equationtemp.charAt(masteropat) == '&') {operator = Operators.SINE;}
				else if(equationtemp.charAt(masteropat) == '$') {operator = Operators.COSINE;}
				else if(equationtemp.charAt(masteropat) == '#') {operator = Operators.TANGENT;}
				else if(equationtemp.charAt(masteropat) == 'y') {operator = Operators.SECANT;}
				else if(equationtemp.charAt(masteropat) == 'u') {operator = Operators.COSECANT;}
				else if(equationtemp.charAt(masteropat) == 'i') {operator = Operators.COTANGENT;}
				else if(equationtemp.charAt(masteropat) == 'q') {operator = Operators.ARCSINE;}
				else if(equationtemp.charAt(masteropat) == 'w') {operator = Operators.ARCCOSINE;}
				else if(equationtemp.charAt(masteropat) == 'o') {operator = Operators.ARCTANGENT;}
				else if(equationtemp.charAt(masteropat) == 's') {operator = Operators.SQUARE_ROOT;}
				else if(equationtemp.charAt(masteropat) == 'p') {operator = Operators.NATURAL_LOGARITHM;}
				else {operator = Operators.NULL;}
				masteropat--;
			}
			masteropat++;
		}
		
		if(equationtemp.charAt(masteropat - 1) == ')')
		{
			int closure = 1, temppos = masteropat - 1;
			while(closure != 0)
			{
				temppos--;
				if(equationtemp.charAt(temppos) == ')')
				{
					closure++;
				}
				
				else if(equationtemp.charAt(temppos) == '(')
				{
					closure--;
				}
			}
			StringParser recurseparser = new StringParser(chomp(equationtemp.substring(temppos, masteropat)), PRECISION);
			leftside = recurseparser.evaluateString(x);
		}
		
		else
		{
			int temppos = masteropat - 1;
			boolean numendfound = false;
			while(!numendfound)
			{
				if(isANumber(equationtemp.charAt(temppos)) && temppos != 0)
				{
					temppos--;
				}
				else
				{
					numendfound = true;
				}
			}
			leftside = Double.valueOf(equationtemp.substring(temppos, masteropat));
		}
		
		if(equationtemp.charAt(masteropat + 1) == '(')
		{
			int closure = 1, temppos = masteropat + 1;
			while(closure != 0)
			{
				temppos++;
				if(equationtemp.charAt(temppos) == '(')
				{
					closure++;
				}
				
				else if(equationtemp.charAt(temppos) == ')')
				{
					closure--;
				}
			}
			StringParser recurseparser = new StringParser(chomp(equationtemp.substring(masteropat + 1, temppos + 1)), PRECISION);
			rightside = recurseparser.evaluateString(x);
		}
		
		else
		{
			int temppos = masteropat + 1;
			boolean numendfound = false;
			while(!numendfound)
			{
				if(isANumber(equationtemp.charAt(temppos)) && temppos != equationtemp.length() - 1)
				{
					temppos++;
				}
				else
				{
					numendfound = true;
				}
			}
			rightside = Double.valueOf(equationtemp.substring(masteropat + 1, temppos + 1));
		}
		
		switch (operator)
		{
			case ADD:
				output = leftside + rightside;
				break;
			case SUBTRACT:
				output = leftside - rightside;
				break;
			case MULTIPLY:
				output = leftside * rightside;
				break;
			case DIVIDE:
				output = leftside / rightside;
				break;
			case EXPONENTIATE:
				output = Math.pow(leftside, rightside);
				break;
			case SINE:
				output = Math.sin(rightside);
				break;
			case COSINE:
				output = Math.cos(rightside);
				break;
			case TANGENT:
				output = Math.tan(rightside);
				break;
			case SECANT:
				output = 1 / Math.cos(rightside);
				break;
			case COSECANT:
				output = 1 / Math.sin(rightside);
				break;
			case COTANGENT:
				output = 1 / Math.tan(rightside);
				break;
			case ARCSINE:
				output = Math.asin(rightside);
				break;
			case ARCCOSINE:
				output = Math.acos(rightside);
				break;
			case ARCTANGENT:
				output = Math.atan(rightside);
				break;
			case SQUARE_ROOT:
				output = Math.sqrt(rightside);
				break;
			case NATURAL_LOGARITHM:
				output = Math.log(rightside);
				break;
			case NULL:
				output = 0;
				break;
		}
		return output;
	}
	
	public double integrate(double lowbound, double upbound)
	{
		double result = 0, interval = (upbound - lowbound) / PRECISION, gettingthere = lowbound;
		for(int a = 0; a < PRECISION; a++)
		{
			result += (interval * ((evaluateString(gettingthere) + evaluateString(gettingthere + interval)) / 2));
			gettingthere += interval;
		}
		return result;
	}
	
	public double differentiate(double index)
	{
		double output;
		output = (evaluateString(index + 0.0001) - evaluateString(index)) / 0.0001;
		return output;
	}
}