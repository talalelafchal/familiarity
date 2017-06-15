This is my Java note from caveofprogramming.com by John. 
This is not a program.
All credit goes to John. 
/*
|----------------------------------------
|Ten tips to make you a better programmer.
|----------------------------------------
*/ 
10. Learn to touch type. 
9. Name variables and subroutines descriptively
8. Type rather than read/watch and get your program to work.
7. Write software that interest you. 
6. Read stack traces from the top line down. 
5. Aim to write the smallest working program possible. 
4. Google like crazy, copy the error
3. Build programs one at the time, one line at a time, clicking run button again and again
2. Ensure braces always pair up
1. Format code correctly (cmd+shift+I)



/*
|----------------------------------------
|Introduction
|----------------------------------------
*/
I. Introduction and Installation
1. Download and install jre first from oracle. 
2. Second install jdk - software to create java programs yourself. 
3. Third install "Eclipse IDE for Java EE Developers"


--------------------------------------------------------------------------------
II. What Java is and how it works
1. Multipurpose programming language. 
2. javac converts the .java file into binary file. 
- javac is a part of JDK, that is why we installed JDK. 
3. Usually you write a program in Eclipse and press the green button 
& that creates binary file. Which eclipse will then run. So, you 
don't have to know about javac.
4. You get one binary file for every java file/class you create. 
For every class you get binary file. Binary files are with .class
extension. .class files are still not understood by the computer. 
You can provide jvm for different platforms and that can run any 
java programs. 
5. In java you create these binary files and these run in JVM. 
6. If you are creating a program for android, then you will use a different
JDK. However, Java language is the same. 

--------------------------------------------------------------------------------

III. Getting a Joba and What to Study after completing Basic Java
1. Search for jobs
jobserve.com

guru.com

2. Servlets and JSPS: Creating Web Applications With Java

4. Learn Spring framework to create java web programs
- Most popular framework 

--------------------------------------------------------------------------------

IV. How to get the most out of this Course
1. Try to apply everything you learn that demonstrates that you are learning. 
Re-arrange and try to do it little different. 


2. Touch typing - learning to type without looking at the keyboard. 
Search for amazon touch typing

3. Search general tutorials before writing programs. 

/*
|----------------------------------------
|Programming Core Java
|----------------------------------------
*/
/*
|----------------------------------------
|1. A Hello World Program
|----------------------------------------
*/
1. File > New > Project > Java Project
Project Name: Tutorial1
-Accept default
Finish

2. Rt click on project folder > New Class>  
Name: Application
Tick the box to create main method 
public static void main 

- Click on Finish

3. Get rid of the comments

4. system.out.println("Hello World!");

5. Click on the greeen run button & see the output down here. 


/*
|----------------------------------------
|2. Using Variables
|----------------------------------------
*/

1. Integer
int myNumber = 88;
System.out.println(myNumber);

2. In Java there are 8 primitive types of variables:
a. int - is a 32 bit value, if you want whole number use it
b. short - 16 bit value 
c. long - 64 bit value 
d. double - extra precision
e. float - short version of double
   -To get float you have to put f at the end of a number
   float myfloat = 324.3f;
f. char - single character 
   char myChar = 'y';
g. boolean 
   boolean myBoolean = true;
h. byte - can hold 8 bits of data negative or positive.
   byte myByte = 127;


/*
|----------------------------------------
|3. Strings: Working with Text
|----------------------------------------
*/

1. Non-primitive type:
String

2. Create a new Java project . 
File > New > Project > Java Project > Project3

Rt click on the project directory & create an 
Application class. 

DO NOT allow Eclipse to create application automatically. 

3. Type main inside the class Application. 
hit control & space , hit enter 

4. Select the main method and hit tab for indentation. 

5. To create string type. 
String is a class so use upper case S, it's not a primitive type. 
String text = "Hello";


6. Here String is a class. 
text is a variable refering to a thing that has type string. 
"Hello" is an object. 


7. Adding strings
String text = "Hello";

String blank = " ";

String name = "Bob";

String greeting = text + blank + name;

System.out.println(greeting);


/*
|----------------------------------------
|4. Boolean Loops
|----------------------------------------
*/
1. 
boolean loop = 4 < 5;
System.out.println(loop);

2. Easy stuff

/*
|----------------------------------------
|5. For Loops
|----------------------------------------
*/

1. Infinite loop
for(;;)

2. printf and println difference 
- printf is a method that requires two arguments 
and the first one is string, format specifier 
-second one is value to be passed
-format specifier contains special characters 
that will be replaced by variables , begins with % and
ends with a letter
System.out.printf()

%d, - integer specifier

int i = 23;
System.out.prinf("The value of i is %d\n",i);

\n print new line


/*
|----------------------------------------
|6. IF
|----------------------------------------
*/

/*
|----------------------------------------
|7. Getting User Input
|----------------------------------------
*/
1. Scanner class
- 

2. System.in is predefiened standard input object

3. print asks to do something. 

		//Create scanner object
		Scanner input = new Scanner(System.in);
		
		//Output the prompt
		System.out.println("Enter a line of text: ");
		
		//Wait for user to enter a text
		String line = input.nextLine(); 
		
		//Output the prompt
		System.out.println("\nEnter an integert: ");
		
		//Wait for user to enter an integer
		int value = input.nextInt();
		
		//Tell them what they entered. 
		System.out.println("You have entered the string: " + line);
		System.out.println("You have entered the integer: " + value);


4. If a user enters different data type, then the prog crashes. 


5. Scanner java in google to learn more. 


/*
|----------------------------------------
|8. Do while
|----------------------------------------
*/

1. Keep asking user to enter a number until they enter 5. 
- First thing you need a Scanner object. 

2. If you declare a variable inside do{} block, you cannot 
acces it inside while block althoug it's do while block.

3. 
Scanner userinput = new Scanner(System.in);
int value; 

do{
	System.out.println("Enter an integer value: ");
	value = userinput.nextInt();
}while(value !=5);



System.out.println("Got 5!");

/*
|----------------------------------------
|9. Switch
|----------------------------------------
*/

1. Not used by professional developers. 


/*
|----------------------------------------
|10. Arrays
|----------------------------------------
*/

1. int [] values; 
this is a reference variable, it's not a bucket, its like a label. 


2. int value = 7; 
this is a value 

3. int[] values;
values = new int[3];
point the values reference at the stuff after equals to sign.
Hold three integers. 

4. Java assigns 0 as the default value for all items in an array 
which is not yet initialzed with values but has certain size. 

5. Assign a value.
values[0] = 10;

6. size of an array, where values is the name of an array
values.length


/*
|----------------------------------------
|11. Array of strings
|----------------------------------------
*/

1. String[] words = new String[3]; //array with three elements

Again words here is just a reference to the String. 
String is not a primitive type.This is not allocating memory, this is only 
allocating space for the reference. Address of your string. Default value of reference is null. 
null means it's pointing no where.

2. By default java initializes each of the string to null in the above example. 



/*
|----------------------------------------
|12. Multidimensional Array
|----------------------------------------
*/

1. system.out.print
print on the same line

2. Specify 2 rows
String[][] words = new String[2][];

-Setup up data manaually like below:
words[0] = new String[3];


/*
|----------------------------------------
|13. Classes and Objects
|----------------------------------------
*/

1. Capitalize the name of the class. 

2. Create a class called "RedFruit"

3. Name of the file must be the same as the 
name of the class. 

4. You can have as many non pulic classes as you like in one file.
However, you must have one public class in the same file. 

5. Classes can contain:
a. data that represents the state of an object. For example your 
heart rate, etc. 

b. Data or state in a class is called instance variable. 


/*
|----------------------------------------
|14. Methods
|----------------------------------------
*/

1. no need to add $this to access properties/attributes of a class in
JAVA you can acess it directly. This is similar to PHP. 


/*
|----------------------------------------
|15. Getters and Return values
|----------------------------------------
*/
1. Often in computing create method to retrieve something or 
caluclate something. 

2. We covered getter methods here

3. Also I did coding in the same file. Project 13

/*
|----------------------------------------
|16. Method Parameters
|----------------------------------------
*/
1. Create a new project called Project14

/*
|----------------------------------------
|17. Setters and this
|----------------------------------------
*/
1. If you declare your variables public, then you have to know 
what your variables are. This is not desirable. 

2. Never worry about internal variables of a class. Always use
setters and getters. Users must not know about internal variables
of a class. 

3. Also remember that setter must not return anything. Setter method
must be a void method always. 

4. This is called encapsulation. 
use the keyword private. Now you cannot direclty access it outside the class. 


5. To refer to the instance variable use the following syntax:
Use this to avoid ambuiguity. For eg. if param name and property name in a 
class is the same as shown below
class Frog{
	private String name;

	public void setName(String name)
	{
		this.name = name;
	}
	
}

/*
|----------------------------------------
|18. Constructors
|----------------------------------------
*/
1. Name of the constructor has to be the same name as that of your class. 
Also the name should be in teh uppser case 

2. Example
class Machine{
	public Machine()
	{
		System.out.println("Constructor is running!");
	}
}
public class App {
	public static void main(String[] args) {
		Machine machine1 = new Machine();	
	}
}


3. Mulitple constructors
- different methods with the same name but different number of parameters


4. Calling another constructor from a constructor
//call the constructor that accepts one parameter
//this must be the first line
this("Romi"); 


/*
|----------------------------------------
|19. Variables and Methods
|----------------------------------------
*/

1. static member variable also known as class variable is a single copy. 
Each object does not get individual copy of this variable. 

This is the difference between instance/class and static variable. 

2. Static method can acces static data. 

3. Static method cannot output instance variables like
private String name;
This will give an error because static values exits before you even create objects. 

4. Instance methods can access static data. 

5. You only need instance method if they some how deal with
instance data/properties, so make other methods static methods. 
Anything that is not dealing with instance variable should be made
static. 
For example, Math class has constants attached to it like Pi
System.out.println(Math.PI);


6. PI is a constant, so you cannot do the following ,you will get an error
Math.PI = 4;//ERROR

7. Constants are represented by UPPERCASE letters. 
Create the following


System.out.prinln(Thing.LUCKY_NUMBER);

final public static int LUCKY_NUMBER;

//final is java's version of constatn

8. You can use static to count the total number of objects created so far 
in conjunction with a default constructor. 
Code:




/*
|----------------------------------------
|20. String bulider and string formatting
|----------------------------------------
*/

1. + :concatentate in string

2. In Java strings are immutable, once you create a string
you can never change it. It looks like you changed it , but it's not true. 
You are creating a new string, you are not appnding to the existing string
if you are using +. Use StringBuilder class instead.

2. String is immutable in Java. This is not efficient. 
You are not changing the original string, you are 
creating a new string which is inefficient. If it's a 
big program it's inefficient. 

3. StringBuilder appends new content, it does not 
create a new string builder object which is more efficient. 
-light weight because it's not thread safe. 

4. StringBuffer
-thread safe version of StringBuilder.
-

5. Advanced String formatting
-tab and a new line
System.out.println("Here is some text \t that was tab \n and this is a new line");

5. Embedding special formatting characters
	10 cahracters wide on left
	%10d

	10 characters wide on right 
	%-10d


6. Useful formatting
-floating point formatter

System.out.println("Total value is : %f \n" , 5.6);

7. Get two decimal places only , display 5.64 only and round off
-Note this rounds off 
System.out.println("Total value is : %.2f \n" , 5.634534534);

8. 10 here means how many characters to use before decimal point
-This will create extra space 
-Put - to have left align
System.out.println("Total value is : %.2f \n" , 5.634534534);




/*
|----------------------------------------
|21. toString()
|----------------------------------------
*/


1. All objects in java have the Object class as their parent. 

2. Standard methods the Object class has. 

3. Create an object of the parent class Object
Object obj = new Object();


4. Create an empty class at the top
class Frog{
	
}

5. Inside the main class create an object of the empty Frog class.
Frog frog1 = new Frog();

- Now if you do frog1. then you can see that it list all methods
of Object class. By default, all classes inherit Parent class - Object
methods. 


6. Object is the secret parent of all classes in Java.

7. toString() method is really helpful to debug your application. 

8. 
class Frog{
	public String toString()
	{
		return "Hello";
	}
}


System.out.println(frog1);
When you try to do System.out.println() on an object, it tries to 
invoke toString() method to get the string representation of the object. 
And if you dont have it defined, then you can still do the 
System.out.println(frog1);
Comment out the toString() method from the above class. 

9. You should see something like below:
Frog@677327b6

that number is called hash code, a unique identifier for your object. 


10. can be used to crate a string representation of your object that 
enables you to identify the object. 

11. Every time you are putting a plus between two string you are creating
a new string. 


/*
|----------------------------------------
|22. Inheritance
|----------------------------------------
*/

1. Create a class called Machine and App

2. You cannot extend some classes like String class. 

3. Use the keyword extends to inherit attributes and properties

4. Shortcut - Eclipse way of overriding
Rt click on the body of child class > Source > Override/Implement Methods

@Override is called an annotataion

5. If you miss the @Override, then when creating a method it will check
if the method exist in the parent class, if the method exists then it will 
not complain, else it will throw an error saying the method does not exist 
in the parent. class. 

6. No @Override means create a new method. 

7. If a variable/attribute/propety of a class is declared private, then it 
can only be accessed within that class. Even child class cannot access it. 

If you try to access it from a child class, it will give an error saying
the field name is not visible. 


/*
|----------------------------------------
|23. Interface
|----------------------------------------
*/ 

1. Create Machine, Person, and App class. 

2. Constructor
Rt click > Source > Generate constructor using fields

2. Suppose if you think that both Machine and Person class 
should have the same method. For example, showInfo, Java gives a mechanism
for this. Rt click on the Project > New > Interface > 
Creating an interface is similar to creating a class. 
Name: Info

3. Give a method called showInfo() in this Interface. The important thing 
is we are not going to define method body. 
public void showInfo();

Not curly brackets, only header. 

4. Go to the Machine class and type - implements 
public class Machine implements Info{
}

-Add unimplemented methods. 
-It forces you to implement the methods. 

@Override annotation is optional, but we are just checking. 

5. Do the same with Person class use the keyword - implements

6.  Separate multiple interfaces using comma

7. But class can only extend one class. 

8. Some people create interface first. 

9. When you open a bank account. You look at the following things:
You will expect the following things from the bank:


/*
|----------------------------------------
|24. Packages
|----------------------------------------
*/ 

1. Packages
- helps to organize your code. 
- stops conflict between class name. If you have two classess with 
the same name in different packages, then it's ok. 
- 

2. package name are all lower case and no space, no underscore

3. Fish class in the ocean package

4. Package name should be at the top of your file. 

5. To get the location of your project 
Rt click > Properties > Resource > Location: 

All your codes are in src folder not in bin folder

6. After package statement you have to type:
import ocean.Fish; //ocean is the package and Fish is the class. 
Now you can create an object of the Fish class in App.java

7. import all classes from a package
import Ocean.*;

8. cmd+shif+o = import automatically shortcut


9. packages are hierarichal in java

10. sub package called plants in ocean package
file > new > class > 
Package: Ocean.plants
Name: Algae 


Note: you are telling that you want plants page to be inside the 
package called Ocean

You can check it in your hard disk.

11. Separate the difference of package by a dot. 

12. This is an easy one. 

13. Make sure that your package name is unique. 
com.nijjwal.oceangame


/*
|----------------------------------------
|25. Public Private and Protected
|----------------------------------------
*/

1. Bad practice
public String name;
-You should not declare instance variable public. 
-Instance variables must always be private. Encapsulate them, hide
them away from the world. Make people access them through methods. 


2. final : means cannot be changed. 

3. As long as it is final the following practice is ok. 
//Acceptable practice --- it's final/constant
public final static int ID = 3;


4. Constructor does not return anything, void is not required. 


5. Usually you want to declare variables private. Private means
you can only access it from within this class. 

6. If you want it to be accessed from the child  class, then 

7. If a property is protected, and the class is in another package
then it will not work. 

8. Protected - same package and child class can only access it. 

9. int height; 
has package level visibility. 
i.e; default is public. 

10. summary
private : only within same class
public : from anywhere
protected : subclass and same package, same package
no modifier : same package only

11. You can only have one public class in a file that has the 
same name as the file. 

12. You cannot declare private class. Public, private, and protected
does not apply to classes, it only applies to the instance variables. 



/*
|----------------------------------------
|26. Polymorphism
|----------------------------------------
*/

1. OOP concept, means many shape. 
Create App class, Plant class and Tree class. 
-Tree class should have Plant as the 
Superclass:  click on browse
-Type plant in the box

Alternatively, use extends keyword to extend Tree class to Plant class
public class Tree extends Plant{
	

}


2. What polymorphism is - when you have a child class of a parent class, 
then you can normally use the child class anywhere where you can use 
the parent class. 

3. Two references to the same object. 

Plant plant1 = new Plant();
Plant plant2 = plant1;

-Here, both references (plant1 and plant2) are now refering to the same
object. 

- There is actually only one plant object. 

4. Although plant2 is Plant object, but it is 
pointing to tree object. So, when you call the 
grow method it will call grow method from Tree class. 
What matters when calling the program is where the reference is pointing 
to. 

Plant plant1 = new Plant();
Tree tree = new Tree();

Plant plant2 = tree; 
plant2.grow();

5. Variable is not tied to who created it, it is connected to where it is 
currently pointing. 

6. Complication
-Very important
-It the type of the variable that decides what methods you can call. 


7. Polymorphism usage:

8. Polymorpism guarantess that wherever a parent class is expected, you 
can pass in the child class. 


/*
|----------------------------------------
|27. Encapsulation and the API Docs
|----------------------------------------
*/

1. Getters and setters - public methods that can be used from outside
the class. 
Rt click > Source > Generate getters and setters. 

2. Only static final should be made public. 
public static final int ID = 7;
If you do expose a class variable/static that must be a constant. 

3. Similarly, if you have a method that is supposed to be used only 
inside the class, then you should make it private. 

4. Idea is make everything private if you can. Except for constant 
make everything private. Avoid tangling. This prevents classes from 
being tightly coupled, a horrible tangle. 

5. Documentation on some standard java class. 
-Type Java 7 and string 


6. When you look at the API documentation, you don't have to look at 
private methods because, you cannot use them. You want to know about 
the methods that are declared public. That is designed for public 
consumption. 


/*
|----------------------------------------
|28. Casting Numerical Values
|----------------------------------------
*/

1. 

Byte can hold:
-128 to 127 total of 257

2. Conver to a float value important
float floatValue = 384.33f;

4. class version
Double

5. System.out.println(Byte.MAX_VALUE);

6. casting 
intValue = (int) longValue;

7. Rounding 
Math.round();

8. The followign won't work as we expec it to !
//128 is too big for a byte. 
byteValue = (byte) 128;
System.out.println(byteValue);


/*
|----------------------------------------
|28. Upcasting and downcasting
|----------------------------------------
*/

1.  upcasting
-This is an example of upcasting because we have gone 
up from camera to machine. READ FROM RIGHT TO LEFT. 

Machine machine2 = new Camera();
or
Machine machine2 = camera1;

2. Downcasting
-when you downcast java wants confirmation, so you need 
to provide the variable type

Machine machine3 = new Camera();
Camera camera2 = (Camera) machine3;
camera.start();
camera2.snap();

-Downcasting in not safe. 

3. Read more/once one more time. 


/*
|----------------------------------------
|30. Using Generics
|----------------------------------------
*/

1. Generic class is a class that can work with other object. 
And you specify what type of objects it can work with. 

2. Learn how to use array list class. 

3. ArrayList : manages an array internally, but you don't have 
to worry about the size of the array. 

4. Old style of doing. 
ArrayList list = new ArrayList();

- You now have an object that can store other objects. 
- Store string objects

list.add("apple");
list.add("banana");

5. Get item from the list
String fruit = (String) list.get(0); //pass the index, get returns an object so you have to downcast


6. In Java 5 generics was introduced, the idea of generics is 
if you have a class 



/*
|----------------------------------------
|32. Anonymous Classes
|----------------------------------------
*/

1. Way of extending existing class or implementing interface in such a way that you can do it one 
time or one thing. 

2. Create a class called Machine

class Machine{
	public void start()
	{
		System.out.println("Starting machine. ...");
	}

}

interface Plant{
	public void grow();
}

public class App{
	public static void main (String[] args)
	{
		Machine machine1 = new Machine();
		machine1.start();

	}
}


3. Let's say you want to overwrite the start() method, then you can do 
the following:

Machine machine1 = new Machine()
{
	@Override public void start()
	{
		System.out.println("Camera snapping .... ");
	}
};

-This is the child class of machine that does not actually have a name, 
so it's called an anonymous class. 


4. You cannot instantiate object from an interface. You are saying 
Plant is a class below, but it is not. 

Plant plant1 = new Plant();

But you can do the following
Plant plant1 = new Plant()
{
	@Override
	public void grow(){


	}
	
};

Now, 
plant1.grow();


/*
|----------------------------------------
|33. Reading Text Files
|----------------------------------------
*/

1. Create a text file called - "example.txt" with some line  on it. 

2. Get path to the file. 

3. In eclipse set the path to the file using the following syntax:
String fileName = "/Users/Nijjwal/Desktop/example.txt";

4. If you are on windows, then back slash \ will create a problem. 
\ means a speacial control . Use two back slashes where you normally
use one. Good option is use forward slash. This should work fine. 

5. Create a file object. 
File textFile = new File(fileName);

6. Simpler way to read a file in Java is to use scanner class. 
Scanner in = new Scanner(textFile);

-This could throw an error, so put it in try catch block.

- Throw exception, main program will throw error. To demonstrator
change name of the file and run it. 

7. When you open a file, you must close it. That's what the error 
is all about. 

in.close();

8. You can read data from file using variety of methods. 
For the moment, let's loop the whole file lin eby line. 
while there isn't another line to read.

		while(in.hasNextLine())
		{
			String line = in.nextLine();
			System.out.println(line);
		}

9. Run the program

10. Now, if you want to work with your file and read the other
integers from the file. For example, the first number in your file 
might be a number 3. And that might indicate you have to read 
three lines from the text. 

-Read the number and loop the number times. Instead while you use 
for and read the next three lines. The next three lines could be 
the name of books. 
To read number just for the moment. 

int value = in.nextInt();
System.out.println("Read value: "+value);  


11. Problem:
nextInt() does not read the invisible line and nextLine reads it however. 

12. Sometimes you don't want to read a file from your absolut path. 
Drag the file to your project folder. Put the file in the root 
directory of your project. Very important. 
-Copy files
-Open it on the Eclipse and add the line Zebra



/*
|----------------------------------------
|38. Abstract Classes
|----------------------------------------
*/

1. Base class is not going to do anything. It's going to act as as a 
base class only. For example, if Machine is a base class, then we 
could have Car, Camera as two classes that extends Machine class. 
Both these classes can have something in common. For example, they might 
have id, if they have something in common, then that common functionality 
can be added to the parent class. 

private int id; 

generate getters and setters. 

- So, far this is a completely conventional class hierarchy. 



2. There is purpose in having a car object, there is purpose in having a
Camera object, but there is not purpose in having a machine object. Because Machine
is just a base class. In real world car is real, camera is real. We only have specific
class objects in the real world. 

3. So, we can prevent users from instantiating this parent class. And to do that we make 
it abstract. 
public abstract class Machine{
	private int id;

	//setter code


	//getter code 

}

4. Now, what this abstract keyword does is, now if you try t type 
Machine machine1 = new machine(); 
You cannot do it. You cannot instantiate machine. Which is what we wanted. 

Abstract class can have abstract method. 

5. Now, Machine is an abstract class, abstract class can have abstract methods. 
Because if you now want to force all machines to have a method called start because
every machine should start whether it's car or camera. To do that we should have an 
abstract method. If you want to force all child class, but you don't want it for 
the Machine class then use this :

public abstract void start()
{
	
}

6. Now you must implement the start() method in all child classes, otherwise you 
will get an error. This is the cause for implementing interface. 

7. You can also call another abstract method within the base clas
public abstract void run()
{
	start();
	doStuff();
	shutdown();
}

public abstract void doStuff();
public abstract void shutdown();

8. These are some methods you want to force child classes to implement. 

9. That's pretty much there is to abstract classes. 

10. This is similar to interface. In interface also you force. 

11. Confused? when to use abstract class and when to use interface?

- When you make child of an abstract class, you are making very strong statement. 
- extends means you are making a very strong statement. Parent class determines
it's identity. 
- A car is a machine. 
- Much stronger than interface. 
- A class can only implement one abstract class, but you can implement any number
of interface. 
- Abstract is more about identity.
- Interface determines what the child class does. 
- In Interface you cannot have default functionaltiy, but in Abstract class you 
can have functionality. 


/*
|----------------------------------------
|41. Writing Text Files
|----------------------------------------
*/

1. In java 7 >, you dont' have to close the file.

/*
|----------------------------------------
|42. Equals
|----------------------------------------
*/

1. Create App class and within this class create another class called Person 
at the top. 


2. Create these two properties
	private int id;
	private String name;

3. Rt click inside teh Person class > Source > Generate Constructor using fields
-Select both fields and click on ok.

4. Rt click inside teh Person class > Source > Generate toString() > Ok

5. Now we have our class ready, create two person object for comparing. 
		Person person1 = new Person(5, "Bob");
		Person person2 = new Person(5, "Bob");

System.out.println(person1 == person2);
This will return false although semantically they look the same because person1 
is a reference to an object and person2 is a reference to different object. 

6. Use the following syntax to compare objects:
System.out.println(person1.equals(person2));

-Here equals is Object superclass's method. You can overwrite the "equals" method
to match your needs. 

7. To overwrite 
Rt click > Source > Generate hashCode() and equals()

- Tick the fields you want to compare. 

- Run, now it should return true.

8. Use == for primitive data type and 
.equals for object comparison. 

9. Hash code is unique id that each object should have. 
System.out.println(new Object());


/*
|----------------------------------------
|43. Inner Classes
|----------------------------------------
*/

1. Create Robot class
with the follwing properties and methods

2. We have already look at anonymous classes, anonymous classes
are like inner class. We will look into three more. 

3. In Java you can declare classes anywhere, the only rule is 
you can only have one public top level class in each file and the 
name of the file should match the name of the class. 

4. Non-static inner class or also known as nested class. 

5. Declare a class inside Robot class like below:
-This class will now have access to the instance variable of the
Robot class. 


public class Robot {
	private int id;
	
	
	private class Brain{
		public void think()
		{
			System.out.println("Robot"+id+"is thinking");
		}
	}
	
	public Robot(int id)
	{
		this.id = id;
	}
	
	public void start()
	{
		System.out.println("Starting robot");
		Brain brain  = new Brain();
		brain.think();
	}
	
	
}


6. This is used for logically grouping. 

7. If you make the inner class Brain public, 
then in your app class you can do something like this:

Robot.Brain = robot.new Brain();
brain.think();

Robot.Brain is the type here

8. When you create an instance of a robot you don't create
an insatnce of Brain automatically, you create the instance
inside some method of the Robot outer class. 


9. static inner class are used basically where your normal class
is not associated with enclosing outer class. But for some reason 
you want to group it with outer class. 

10. non static inner class is used to group in some functionality. 
And you need the class to have access to the non-static variables
of the outer class. 

/*
|----------------------------------------
|44. Enum
|----------------------------------------
*/ 

1. An enum type is a special data type that
enables for a variable to be a set of predefined constants. 
The variable must be equal to one of the values that have
been predefined for it. Common examples include compass 
direction(values of North, South, East and West) and the 
days of the week. (Source Oracle)

2. Only public static variable should be final. 

3. Enum stands for enumerated. You can count through it. Enum 
type represents some fixed although nine in particular order. 

4. Rt click > Enum 

public enum Animal {
	CAT, DOG, MOUSE;
}

5. In your main function call
Animal animal = Animal.CAT;

6. Add missing case statements. 

7. Normally you have to type :
Animal.DOG

8. Advance usage of ENUM

9. Don't put 
	case Animal.DOG:
	System.out.println("CAT");

10. Find class name of the enum constant
System.out.println(Animal.DOG.getClass());

11. So, these constants are actually objects of the class Animal. 

- Verify if the Animal.Dog object is an instance of the class Animal or not. 

System.out.println(Animal.DOG instanceof Animal);

12. Verify if the object is an object of Enum class
System.out.println(Animal.DOG instanceof Animal);

-You should get true because objects of child classes are instance of parent 
class as well. 

13. You can give Enum class constructor and methods


14. If you have a constructor in Enum, then the constants must be instantiated
in the constructor. 
-You don't use new with enum.
-Use ()
-Generate getter and setter

	CAT("Fergus"), DOG("Fido"), MOUSE("jERRY");
	
	private String name;
	
	Animal(String name)
	{
		this.name = name;
	}

15. You can also override the toString() method of Java in enum
	public String toString()
	{
		return "This animal is called: " + name;
		
	}

16. Using the name method turn the sepical enum constants/not strings 
into the string. 
System.out.println("Enum name as a string: "+Animal.Dog.name());

17. Supply string corresponding to ENUM constant
Animal animal2 = Animal.valueOf("CAT");
System.out.println(animal2);


/*
|----------------------------------------
|45. Recursion
|----------------------------------------
*/ 

1. It's an algorithm rather than related to Java. Better to know. 

2. The following code will  generate an error 
public class App {

	public static void main(String[] args) {
		int value = 4;
		calculate(value);
	}
	
	private static void calculate(int value)
	{
		value = value-1;
		System.out.println(value);
		calculate(value);
	}

}

- Because in Java when you call a method from within another method
there is special area in memory of JVM called stack. And stack is used
for local variables and for rememebering which method called which method. 
So, we know the value after the method returns something. That's distinct from heap
where object are allocated in memory. 

- We got stack overflow error because we call the function infinitely. 

- Not recommened because of stack overflow error. But in some situations it is very 
useful to use the recursive method. 

3. Put control check 
		if(value ==1 )
		{
			return;
		}

4. You can use this to calculat factorial of a number. 

5. factorial function
	private static int factorial(int value)
	{
		if( value == 0)
		{
			return 1;
		}
		
		return factorial(value-1)*value;
	}

6. Towers of hanoi puzzle

/*
|----------------------------------------
|46. Serialization
|----------------------------------------
*/ 

1. What serialization is?
If you take an object and serialize it, that means turning that object into binary form
or binary data. De serialize means take binary data and turn it back into an object. 

2. We will look at serializing to file, which is often what people mean when they refer
to serializing in Java. 

3. Take objects of the Person class you have created. Write into the file and read back 
from the file. Taking the objects back from the file and turning them back into objects 
in Java program. 

4. Normally reading and writing will both be the part of the program. 

5. In this tutorial one part will read and one part will write from the file. Normally 
both functionalities are in one file. 

6. Person class with constructor and toString() method. 

7. Current code of Person.java
public class Person {
		private int id;
		private String name;

		public Person(int id, String name)
		{
			this.id = id;
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return "Person [id="+ id + ", name=" + name  + "]";
		}
}


8. Current code of WriteObjects.java
public class WriteObjects {
	public static void main(String[] args){
		System.out.println("Writing objects...");
		
		Person mike = new Person(543, "Mike");
		Person sue = new Person(123, "Sue");
		System.out.println(mike);
		System.out.println(sue);
		
	}
}

9. Stream data to file.
-Streaming means sending data sequentially to a file. 

10. Extension does not matter, .bin typically for binary file. 
This will be the working directory. The proejct root/base. 

11. Java 7 way of handling errors
import and put the curly brackets in like below:
		try(FileOutputStream fs = new FileOutputStream("people.bin")){
			
		}

-click on warning 
-add catch clause to surround try 
-Advantage: this syntax will automatically call the close method of this object.

12. If you use Java 6 or before you need to call fs.close() method. 

13.
-FileNotFoundException will be thrown if the file people.bin cannot be created. 
- IOException will be thrown if you cannot write to a file. 
- Handle exception gracefully by using pop up box. For now, let's just use 
printStackTrace. 
-

14. Next thing we need is ObjectOutputStream. Pass the FileOutputStream to it's 
constructor. 

ObjectOutputStream os = new ObjectOutputStream(fs);
os.close();  

- import it 
- you need to close the object, otherwise you will get an error
- not enclosed automatically

15. You can write any object first. 
os.writeObject(mike);
os.writeObject(sue);


16. If you run this now, you will get not serializableException

17. This is classic interview question. 
To make a class serializable all you have to do is implement serializable interface. 
public class Person implements Serializable{
	

}


-Note that the interface does not have any interface in it. All we have to do is 
implement the interafce.

18. Run WriteObjects.java file again. You should not get any erro. 

19. Rt click on the project and click on Refresh. You should now see the people.bin 
file  created by eclipse. 

20. We have written code to write objects to the file. 
Now let's write code to read objects from the file.

try(FileInputStream fi = new FileInputStream("people.bin")){

}

21. Follow similar steps. Notice the name of the class is different above. 

22. We are casting because it returns Person object 
Person person1 = (Person) os.readObject();

- readObject() method might read an object of the class that does not even exits on 
this project. If that happens it should throw ClassNotFoundException. 

- "Add catch clause to surrounding try" 

23. Add another object
-sysout the two objects which you just read

24. Run the file now. 
You should now read them in the same order you wrote them.  

25. Get rid of the serialization warning from Person.java.
-Note it will add the serilversion id of type long. 
-If you change this number and and try to run the read file, then you will get 
an error saying you wrote with one version and you are now reading with another version. 
-All this is, this is used to deserizalize the objects inorder to read objects from the
file. 
-If you change the Person class then you can no longer use it to read it with new version 
of the class. 
For example, if you delete id, name. 

/*
|----------------------------------------
|47. Serialization Multiple Objects
|----------------------------------------
*/ 

1. In WriteObjecs.java file 
Create an array of Person class from the pervious tutorial. 
Person[] people = {new Person(), new Per};

2. An array in Java is just an object. 
-  And we can simply serialize it or deserialize it as with any object. 
- Arrays in Java are serializable if the objects of the array are serializable. 


3. Now go to Read objects class 
			ObjectInputStream os = new ObjectInputStream(fi);
			Person[] people = (Person[])os.readObject();
			
			for(Person person: people)
			{
				System.out.println(person);
			}
			os.close();

4. Checkout Arraylist video. You can serialize and deserialize array list.
ArrayList is a resizable array, very very useful. 

5. Create an array list of Person objects.
-In angled brackets type of class goes in 

ArrayList<Person> peoplelist = new ArrayList<Person>(Arrays.asList(people)); 

6. Serialize the arraylist as werll
os.writeObject(peoplelist);

7. In ReadObject class
You might get the following warning-
Type safety unsafe cast: means Eclispe is not sure about the type of casting
done, because it might not be present
ArrayList<Person> peoplelist = (ArrayList<Person>)os.readObject();
-Classes passed in diamond bracket, they suffer from type eresia, the information 
about the type is lost. This is kind of grammatical thing. When you deserialize the
type is erased, so we get this warning. You can just suppress
@SuppressWarnings("unchecked")

8. get size
for array : array.length  //no brackets because it's the property of the array object
for arrayList: array.size()  //yes brackets

9. Read and write in the exact same order

10. write int to a file and use for loop to insert values of arrayList into the file
	os.writeInt(peoplelist.size());
	for(Person person: peoplelist)
	{
		os.writeObject(person);
	}

11. read from a file an integer and all the objects 
	int num = os.readInt();
	for(int i=0; i<num; i++)
	{
		Person person = (Person)os.readObject();
		System.out.println(person);
	}
	
/*
|----------------------------------------
|48. Debugging 
|----------------------------------------
*/ 

1. Set breakpoint. Break point is a point in your code at which 
you want your program to stop during it's execution. So that you can 
go through it and go line by line. 

2. Double click on the left hand side or 
   rt click > toggle break point. 

3. Run > Debug

4. Resume - runs the program until the next break point or until 
the end if there are no more break points. 

5. Stop - stops your program. 


6. Step over (f6) runs your program line by line. 


7. Create three sysout lines and put a break point in the first 
   sysout line. 

8. 		int value = 7;
		
		
		System.out.println("Starting.");
	
		System.out.println("Incrementing values.");
		
		value++;  //set breakpoint 2 here
		
		value = value - 8;
		
		System.out.println(value);
		
		System.out.println("Finishing.");


9. Watch a particula variable:


10. Downside 
- Cant' work with web programs. 


11. Stepping into methods. 

12. Create a new class called Test


13. Give some simple methods to the Test class. 


14. You cannot see the number now, because the number 
only existed within the scope of the method. 


15. Conditional breakpoints
> Insert a breakpoint
> Rt click > Break point properties
> Conditional  <<<< Ask Sweekar 