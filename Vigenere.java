/**********************************************************************************
* Authors: Bradley Levine, Dakota Findley				          				  *	
* Project 1: What's the frequency, Kenneth?				         				  *
* Files: Vigenere.java							         						  *
* Description:  This preforms encrytion and decryption using Vigenere Cipher      *
*									          									  *
***********************************************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Vigenere 
{

	public static void main(String[] args) throws IOException 
	{
		//Initializing variables and instantiating objects
		Scanner input = new Scanner(System.in);
		String file = null;
		String key = null;
		String choice = null;

		//getting information from user
		System.out.print("Encrypt or decrypt a file (e/d): ");
		choice = input.nextLine();

		while(choice.charAt(0)!= 'e' && choice.charAt(0) != 'd')
		{   //if the user selected an invalid choice
			System.out.println("Invalid choice!");
			System.out.print("Encrypt or decrypt a file (e/d): ");
			choice = input.nextLine();
		}

		if (choice.charAt(0) == 'e') //get the file name to encrypt
			System.out.print("Please enter file to encrypt: ");
		else //get the file name to decrypt
			System.out.print("Please enter file to decrypt: ");

		file = input.nextLine(); //record file name
		File filetest = new File(file);
		while(!filetest.exists()) //check if the file exists
		{
			if (choice.charAt(0) == 'e')
				System.out.print("Error: File does not exist! Please enter file to encrypt: ");
			else
				System.out.print("Error: File does not exist! Please enter file to decrypt: ");
			file = input.nextLine();
			filetest = new File(file);
		}

		System.out.print("Enter key phrase (no spaces): "); //get the key
		key = input.nextLine();
		key = key.toUpperCase(); //turn key to uppercase
		System.out.print("Enter output file: ");
		String outputfilenm =  input.nextLine(); //get file name for output



		//Section for handling encryption
		if (choice.charAt(0) == 'e')
		{
			encrypt(file, key, outputfilenm);
		}
		else if (choice.charAt(0) == 'd')//handles decryption
		{
			decrypt(file, key, outputfilenm);
		}

		input.close();
	}
	public static void encrypt(String file, String key, String outputfilenm) throws IOException
	{
		//Initializing variables and instantiating objects
		FileReader fileInput = new FileReader(file);
		BufferedReader buffer = new BufferedReader(fileInput);

		StringBuilder plainText = new StringBuilder();
		StringBuilder cypherText = new StringBuilder();
		int phraseIndex = 0;
		char enter = 13;

		String line = "";
		//getting text from document
		while((line = buffer.readLine()) != null)
			plainText.append(line + enter) ;

		//closing file
		fileInput.close();
		buffer.close();

		//keeps all letters in the same range of values
		plainText = new StringBuilder(plainText.toString().toUpperCase());

		//this goes through the document and encrypts it
		for(int i = 0; i < plainText.length(); i++)
		{
			//checks if it is an alphabetical  character
			if(Character.isAlphabetic(plainText.toString().charAt(i)))
			{	

				//shifts letter
				char letter = (char) (((plainText.toString().charAt(i) - 'A')  +  (key.charAt(phraseIndex) - 'A')) % 26);
				letter += 'A';

				phraseIndex = (phraseIndex + 1) % key.length();
				cypherText.append(letter + "");
			}
			else if(plainText.charAt(i) == 13)			//adds newline character
				cypherText.append(plainText.toString().charAt(i));
		}

		//writes the encrypted text to secified file
		PrintWriter output = new PrintWriter(new File(outputfilenm));

		output.print(cypherText.toString());

		output.close();		
	}
	public static void decrypt(String file, String key, String outputfilenm) throws IOException
	{

		//Initializing variables and instantiating objects
		FileReader fileInput = new FileReader(file);
		BufferedReader buffer = new BufferedReader(fileInput);

		StringBuilder plainText = new StringBuilder(); //variable for the decrypted text
		StringBuilder cipherText = new StringBuilder(); //variable for the ciphertext
		int phraseIndex = 0; //which place in the phrase
		char eneter = 13; //carriage return

		String line = "";
		//getting text from document

		while((line = buffer.readLine()) != null) //place in carriage returns to that each line is on its own line
			cipherText.append(line + eneter);

		//closing file
		fileInput.close();
		buffer.close();

		//keeps all letters in the same range of values
		cipherText = new StringBuilder(cipherText.toString().toUpperCase());

		for(int i = 0; i < cipherText.length(); i++) //go through the ciphertext
		{

			if(Character.isAlphabetic(cipherText.toString().charAt(i))) //ensuring the character is a letter
			{   //decrypt the cipher letter and set it to a variable
				char letter = (char) ((cipherText.toString().charAt(i) - key.charAt(phraseIndex) + 26) % 26); 
				letter += 'A';
				phraseIndex = (phraseIndex + 1) % key.length(); //go to the next spot in the key
				plainText.append(letter + "");
			}
			else if(cipherText.toString().charAt(i) == 13) //add carriage returns
				plainText.append(cipherText.toString().charAt(i));
		}

		PrintWriter output = new PrintWriter(new File(outputfilenm)); //write the decrypte text to a file
		output.print(plainText.toString());
		output.close();

	} 

}
