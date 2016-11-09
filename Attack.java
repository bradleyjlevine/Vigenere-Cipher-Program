/**********************************************************************************
* Authors: Bradley Levine, Dakota Findley				          				  *	
* Project 1: What's the frequency, Kenneth?				         				  *
* Files: Attack.java							         						  *
* Description:  This recovers the key from a documnet encypted by Vigenere Cipher *
*									          									  *
***********************************************************************************/

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
public class Attack {

	public static void main(String[] args) throws IOException {
		Scanner reader = new Scanner(System.in);
		String fileName = null;
		String ciphertext = "";

		System.out.print("Please enter file to analyze: "); //get the ciphertext
		fileName = reader.nextLine();
		File filetest = new File(fileName); //place in file so that a file not found check can be done
		while(!filetest.exists()) //as long as the file doesn't exist
		{
			System.out.println("Error: File does not exit! Please enter file to analyze: ");
			fileName = reader.nextLine();
			filetest = new File(fileName);
		}
		Scanner input = new Scanner(new File(fileName)); //use to read in the ciphertext

		while(input.hasNextLine()) //read each line
			ciphertext += input.nextLine();
		ciphertext = ciphertext.toUpperCase(); //change to uppercase
		int keylength = findKeyLength(ciphertext); //get the keylength

		findKeyText(fileName, ciphertext, keylength);		
	}
	public static int findKeyLength(String ciphertext)
	{
		double ICAverage = 0.0;
		int keylength = 0;
		double ICDiff = 0.0;

		//what length we are testing for
		for(int j = 1; j < 16; j++)
		{
			//Initialization
			int[][] letterCount = new int[j][26];
			int[] countCharacters = new int[j];
			double[] IC = new double[j];
			double summation = 0.0;

			//loops the process to get IC for each grouping of characters
			for(int k = 0; k < j; k++)
			{
				summation = 0.0;

				//reads the entire text
				for(int i = 0 + k; i < ciphertext.length(); i = i + j)
				{
					//checks if the current character is alphabetical and what group it gets added to
					//in (i % (k + 1) == 0), 1 is added to k to avoid dividing by 0
					if(Character.isAlphabetic(ciphertext.charAt(i)))
					{
						// [k] is the group and [] is the parallel array where it is 25 elements long corresponding to a through z 
						letterCount[k][(int)(ciphertext.charAt(i) - 'A')]++;
						countCharacters[k]++;
					}
				}

				//this gets the summation for every group up to but, not including j
				for(int i = 0; i < 26; i++)
				{
					summation += letterCount[k][i] * (letterCount[k][i] - 1);
				}

				//this calculates the rest of the IC for each group
				IC[k]=  (summation / (double)(countCharacters[k] * (countCharacters[k] - 1))) * 26;
			}

			//this then calculates the average
			for(int i = 0; i < j; i++)
				ICAverage += IC[i];

			ICAverage /= j;

			if(Math.abs(ICAverage-1.73) < Math.abs(ICDiff-1.73))
			{
				ICDiff = ICAverage;
				keylength = j;
			}

			ICAverage = 0.0;
		}
		return keylength;
	}
	public static void findKeyText(String fileName, String ciphertext, int keylength) throws IOException
	{
		double[] freqvalues = new double[26]; //place all values into an array to measure frequency distribution
		freqvalues[0] = 0.08167;
		freqvalues[1] = 0.01492;
		freqvalues[2] = 0.02782;
		freqvalues[3] = 0.04253;
		freqvalues[4] = 0.12702;
		freqvalues[5] = 0.02228;
		freqvalues[6] = 0.02015;
		freqvalues[7] = 0.06094;
		freqvalues[8] = 0.06966;
		freqvalues[9] = 0.00153;
		freqvalues[10] = 0.00772;
		freqvalues[11] = 0.04025;
		freqvalues[12] = 0.02406;
		freqvalues[13] = 0.06749;
		freqvalues[14] = 0.07507;
		freqvalues[15] = 0.01929;
		freqvalues[16] = 0.00095;
		freqvalues[17] = 0.05987;
		freqvalues[18] = 0.06327;
		freqvalues[19] = 0.09056;
		freqvalues[20] = 0.02758;
		freqvalues[21] = 0.00978;
		freqvalues[22] = 0.0236;
		freqvalues[23] = 0.0015;
		freqvalues[24] = 0.01974;
		freqvalues[25] = 0.00074;

		String key = ""; //holder for the learned key
		for(int i = 0; i < keylength; i++) //go through the commands for each letter of the character in the key
		{
			String kcgroup = ""; //group of letters in ciphertext proportioned for each key character
			for(int j = i; j<ciphertext.length();) //divide the cipher text into divisions for each key character
			{ 
				kcgroup += (ciphertext.charAt(j));
				j+=keylength; //the place in the ciphertext should correspond to where the selected character in the key would be
			}
			double[][] bestfreq = new double[1][2]; //hold the best frequency and its corresponding letter
			bestfreq[0][0] = -1; //start with a holder
			for(int z = 0; z < 26; z++) //go through for each letter of the alphabet
			{
				String groupshift = ""; //each group shifted by a specified letter
				for(int t = 0; t < kcgroup.length(); t++)
				{
					char letter = (char) ((kcgroup.charAt(t) - (z+'A') + 26) % 26); //decrypt the character with a possible key letter
					letter += 'A'; //bring the amount to the correct ASCII value
					groupshift += letter; //the potentionally decrypted text 
				}
				double chisqr = 0.0; //holder for a chi-square
				for(int x = 0; x < groupshift.length(); x++) //go through each potentionally decrypted text
				{
					int count = 0;
					for(int y = 0; y < groupshift.length(); y++) //get a count for how often each character occurs
					{
						if(groupshift.charAt(y) == groupshift.charAt(x))
						{
							count++;
						}
					} 
					chisqr += (Math.pow(count-freqvalues[groupshift.charAt(x)-'A'],2)/freqvalues[groupshift.charAt(x)-'A']);
					//calculate the chi-square for the letter in question and add it to the overall amount for the text 
				}
				if(bestfreq[0][0] == -1 || chisqr < bestfreq[0][1]) //if the best yet chi-square amount is found, record in array
				{
					bestfreq[0][0] = z;
					bestfreq[0][1] = chisqr;
				}

			}
			char keyletter = (char) (bestfreq[0][0]+'A'); //calculate and record the best key letter
			key+=keyletter; //add the letter to the key
		}
		String outputnm = fileName + ".cracked"; //make filename
		Vigenere calldecrypt = new Vigenere(); //make new instance of the decrypt method
		calldecrypt.decrypt(fileName, key, outputnm); //call method
		System.out.println("Recovered key: " + key); //print the recovered key
		System.out.println("Decrypted content written to " + outputnm); //where the decrypted text was written to
	}
}
