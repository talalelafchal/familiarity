public class HelloWorld {
  public static void main(String[] args) {
    System.out.println("Hello World");
  }
}

---------------------------------------------
  
                                                          Variables:

                                                      byte (number, 1 byte)
                                                      short (number, 2 bytes)
                                                      int (number, 4 bytes)
                                                      long (number, 8 bytes)
                                                      float (float number, 4 bytes)
                                                      double (float number, 8 bytes)
                                                      char (a character, 2 bytes)
                                                      boolean (true or false, 1 byte)
 
  Get length of string:

String myString = "adsasdregfh234jh23kj4";
System.out.println(myString.length());


public class integers
{
	public static void main(String[] args)
	{
		String myName= "Edvards";
		String mySurname = "Ozols";
		
		int a = myName.length();
		int b = mySurname.length();
		int total = a + b;
		
		
		
		System.out.println("Total number of chars in my name and surname is = " + total);
	}
}
 -----------------------------------------------
   
lists php [1,2,3,4]
maps php ['name' => 'Janis', 'age' => 22]
   
   Array[];Array[];Array[];Array[];Array[];Array[];Array[];Array[];Array[];Array[];Array[];Array[];Array[];Array[];Array[];Array[];


Access third element of array = 5
Display length of array = System.out.println(primeNumbers.length);

public class Hello
{
	public static void main(String[] args)
	{
		int[] primeNumbers = {2, 3, 5, 7, 11, 13};
		
		System.out.println(primeNumbers[2]);
		System.out.println(primeNumbers.length);
	}
}



import java.util.Arrays;

public class HelloWorld {
  public static void main(String[] args) {

	int[] array = new int[3];
	int num = 0;
	
	for(int i = 0; i < array.length; i++)
	{
		array[i] = i;
			
	}
		
	System.out.println(Arrays.toString(array));

  }
}


------------------------------------------------------
	
lists php [1,2,3,4]
maps php ['name' => 'Janis', 'age' => 22]

	List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;List;

Create new list - List list = new ArrayList();
Add items to list - list.add(2);
Remove items from list - list.remove(1);
Output whole list - System.out.println(list.toString());

import java.util.*;

public class Hello
{
	public static void main(String[] args)
	{
		List list = new ArrayList();
		list.add(2);
		list.add(3);
		list.add(5);
		
		list.remove(1);
		
		System.out.println(list.toString());
	}
}


public class Hello
{
	public static void main(String[] args)
	{
		List countries = new ArrayList();
		
		countries.add("Latvia");
		countries.add("Estonia");
		countries.add("Lithuania");
		
		countries.remove(2);
		countries.add("Russia");
		
		System.out.println(countries.toString());
	}
}



---------------------------------------------------------------
	
MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;MAP;

Create new map = Map nameOfMap = new HashMap();
Add to map = map.put("Father", "Andris");
Remove from map = map.remove("Random");
Output map element = System.out.println(map.get("Brother"));
Output whole map list = System.out.println(nameOfMap.toString());
Display number of items in map = System.out.println(map.size());

import java.util.*;

public class Hello
{
	public static void main(String[] args)
	{
		Map map = new HashMap();
		
		map.put("Father", "Andris");
		map.put("Mother", "Indra");
		map.put("Brother", "Emīls");
		map.put("Sister", "Kristiāna");
		map.put("Random", "Something");
		
		map.remove("Random");
		
		System.out.println(map.get("Brother"));
		
	}
}


--------------------------------------------------------
	
	if;else;else if;if;else;else if;if;else;else if;if;else;else if;if;else;else if;if;else;else if;if;else;else if;

if(age = 18) = means settings age to 18, cant do if tests like that;
if(age == 18) = correct way to compare;

public class HelloWorld {
  public static void main(String[] args) {
    
    	int[] array = {2, 3};    	
      	    	    	
    	if(array[0] > array[1])
    	{
    		System.out.println(array[0]);
    	}
    	else if (array[1] > array[0])
    	{
    		System.out.println(array[1]);
    	}
    	else
    	{
    		System.out.println("The numbers are the same")
    	}
    
  }
}



---------------------------------------------------
	
	LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;LOOPS;


public class loops
{
	public static void main(String[] args)
	{
		int x = 1;
		
		while(x <= 5)
		{		
			System.out.println(2 * x);
			x++;
		}
		
		
		for(int y = 1; y <= 5; y++)
		{
			System.out.println(2 * y);
		}
		
		// Reverse loop
		
		for(int y = 6; y > 0; y--)
		{
			System.out.println(2 * y);
		}
	}

}

// Create a loop which will display first 10 triangular numbers.

public class loops
{	
	public static void main(String[] args)
	{
		int number = 0;
		
		for(int i = 1; i <= 10; i++)
		{
			number++;
			int triangular = number * (number+1)/2;			
			System.out.println("Triangular number of "+number+" is "+triangular);	
		}	
		
		System.out.println("-----------------------");
		
		int x = 1;
		int triangularNumber = 1;
		
		while(x <= 10)
		{
			System.out.println("Triangular number of "+x+" is "+triangularNumber);
			x++;
			
			triangularNumber = triangularNumber + x;
			
		}
	}

}

	FOREACH LOOP FOR ARRAY OF STRINGS
	
public static void main(String[] args)
	{
		String[] family = {"Andris", "Indra", "Kristiana", "Edvards", "Ruta", "Emils"};
		int memmberCount = family.length;		
		System.out.println(memmberCount);
		
		for (String name : family)
		{
			System.out.println(name);
		}
	}


	FOREACH LOOP FOR ARRAY LIST OF STRINGS
	
	
import java.util.*;

public class loops
{	
	public static void main(String[] args)
	{
	
		List<String> family = new ArrayList<String>();
		family.add("Andris");
		family.add("Indra");
		family.add("Kristiāna");
		family.add("Ruta");
		family.add("Emīls");
		
		System.out.println(family.toString()); // No need for toString(); as List is defined that it will have string elementes.
		
		
		for  (String name : family)
		{
			System.out.println(name);
		}
	
	}	

}

---------------------------------------------------------
	
	CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;CLASSES;

public class HelloWorld {
  public static void main(String[] args) {
    
  	class User 
  	{
  		int score;
  		
  		public boolean hasWon()
  		{
  			if(score >= 100)
  			{
  				return true;
  			}
  			else
  			{
  				return false;
  			}
  		}
  	}
  	
  	User edvards = new User();
  	
  	edvards.score = 101;
  	
  	System.out.println(edvards.score);
  	System.out.println(edvards.hasWon());
  }
}



---------------------------------
	
	public class HelloWorld {
  public static void main(String[] args) {
    
  	class Number
  	{   	  
  	  int num;
  	  
  	  public boolean isPositive()
  	  {
  	  	if(num > 0)
  	  	{
  	  		return true;
  	  	}
  	  	else
  	  	{
  	  		return false;
  	  	}
  	  }
  	  
	  	
  	}
  	
  		Number myNumber = new Number();
  	
  		myNumber.num = -1;
  		System.out.println(myNumber.isPositive());
  		
  		if(myNumber.isPositive())
  		{
  			System.out.println(myNumber.num + " is positive");
  		}
  		else
  		{
  			System.out.println(myNumber.num + " is not positive");
  		}
  }
}

NumberShapeApp;NumberShapeApp;NumberShapeApp;NumberShapeApp;NumberShapeApp;NumberShapeApp;NumberShapeApp;NumberShapeApp;NumberShapeApp;

public class NumberShapes
{
	public static void main(String[] args)
	{
		class Number
		{
			int number;	
			
			public boolean isSquare()
			{
				double squareRoot = Math.sqrt(number);
				if (squareRoot == Math.floor(squareRoot))
				{
					return true;
				}
				else
				{
					return false;
				}
			}		
			
			public boolean isTriangular()
			{
				int triangularNumber = 1;
				int x = 1;
				
				while (triangularNumber < number)
				{
					x++;
					triangularNumber = triangularNumber + x; 
				}
				
				if (triangularNumber == number)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
	
	Number myNumber = new Number();
	myNumber.number = 4;
	
	System.out.println(myNumber.isTriangular());
	System.out.println(myNumber.isSquare());
	
	
	}


}

-----------------------------------------------------------------------------------------------------------------------------------

Save activity state:Save activity state:Save activity state:Save activity state:Save activity state:Save activity state:Save activity state:

protected void on SaveInstanceState(Bundle outState)
{
super.onRestoreInstanceState(savedInstanceState);
String text = savedInstanceState.getString("text");
mEmailSignInButton.setText(text);	
}


------------------------------------------------------------------------------------------------------------------------------------

Try-Catch Exceptation:Try-Catch Exceptation:Try-Catch Exceptation:Try-Catch Exceptation:Try-Catch Exceptation:Try-Catch Exceptation:

Catch generic exceptions

import java.util.Arrays;

public class HelloWorld {
  public static void main(String[] args) {

	int[] array = new int[3];
	
	try
	{
	
		for(int i = 0; i < 4; i++)
		{
			array[i] = i;
				
		}
	
	}
	catch(Exception e)
	{
		System.out.println(e);
	}
		
	System.out.println(Arrays.toString(array));

  }
}


To catch specific exceptions

import java.util.Arrays;

public class HelloWorld {
  public static void main(String[] args) {

	int[] array = new int[3];
	
	try
	{
	
		for(int i = 0; i < 4; i++)
		{
			array[i] = i;
				
		}
	
	}
	catch(ArrayIndexOutOfBoundsException e)
	{
		System.out.println("Index Out Of Bounds");
	}
	catch(Exception e)
	{
		System.out.println(e);
	}
		
	System.out.println(Arrays.toString(array));

  }
}


-----------------------------------------------------------------------------------------------------------------------------
	
	
DOWNLOAD WEB CONTENT:DOWNLOAD WEB CONTENT:DOWNLOAD WEB CONTENT:DOWNLOAD WEB CONTENT:DOWNLOAD WEB CONTENT:DOWNLOAD WEB CONTENT:DOWNLOAD WEB CONTENT:

	