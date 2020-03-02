import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class StructPrediction {

	/*
	 * This is a function to calculate the max legal paring of a sequence, and fill
	 * a table. This method accepts a 2 dimensional table, the sequence we want to
	 * find pairs to, a first index and a last index
	 * 
	 * PredictionFunction(i,j) = | PredictionFunction(i+1,j-1) if legal pairing |
	 * max i<=k<j {(PredictionFunction(i,k), PredictionFunction(k+1,j)}
	 */
	public static Cell PredictionFunction(Cell[][] table, String word, int first_index, int last_index) {
		Cell c = new Cell(0, 0);

		// Base case: check if we are passing the diagonal
		if (first_index >= last_index) {
			c.setValue(0);
			c.setK(-2); // Setting to -2, Just so I can see the diagonal when printing
			table[first_index + 1][last_index + 1] = c;
			return c;
		}
		// Comments aren't real because our eyes aren't real
		//  (\_/)
		// (' . ')
		// (")-(")o
		//Mr. Bun buns :)

		// Creating auxiliary values (Did like that to facilitate debugging.
		int result = 0;
		int r11 = 0;
		int r21 = 0;
		int r22 = 0;
		Cell max = new Cell(0, 0);

		// We have to check if it is a legal pairing {AU, UA, CG, GC}
		if ((word.charAt(first_index) == 'A' && word.charAt(last_index) == 'U')
				|| (word.charAt(first_index) == 'U' && word.charAt(last_index) == 'A')
				|| (word.charAt(first_index) == 'C' && word.charAt(last_index) == 'G')
				|| (word.charAt(first_index) == 'G' && word.charAt(last_index) == 'C')) {

			// If it is a legal pairing we recursively try to find a pairing of a
			// sub-string without head nor tail
			r11 = PredictionFunction(table, word, first_index + 1, last_index - 1).getValue();

			// Add one, because we found one more pairing
			result = r11 + 1;

			// Set our object cell, that is How many matches we have, and the K means where
			// we have a cut, since we dont have a cut, I am setting to -1
			c.setValue(result);
			c.setK(-1);

			// Insert the cell object into table
			table[first_index + 1][last_index + 1] = c;
			return c;

		} else {

			// If there is no legal pairing, then we need to find a place to split our
			// sequence
			for (int k = first_index; k < last_index; k++) {

				// We are dividing our sequence into two and recursively looking for pairs
				r21 = PredictionFunction(table, word, first_index, k).getValue();
				r22 = PredictionFunction(table, word, k + 1, last_index).getValue();

				// That cell's value is the sum of the recursive splits
				result = r21 + r22;

				// Set our object cell, that is How many matches we have, and the K means where
				// we have a cut, so we can trace back to the matches
				c.setValue(result);
				c.setK(k);

				// We are trying to find the split that leads to the greater number of matches
				if (result >= max.getValue()) {
					max.setValue(c.getValue());
					max.setK(c.getK());
				}
			}

			// After we find the split that yelds the maximum number of matches , we add
			// that to the table
			table[first_index + 1][last_index + 1] = max;
			return max;
		}
	}

	/*
	 * This method simply initialize a table of Objects Cell to zeroes
	 */
	public static void initTable(Cell[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				table[i][j] = new Cell(0, 0);
			}
		}
	}

	/*
	 * Prints table This method Will print the table. Accepts the Table, the
	 * sequence word and a integer value (m = 1 print the cell value, else print
	 * cell k) Example when M=1: 
	 *       G   U   A 
	 * | 0 | 0 | 0 | 0 | 
	 * | 0 | 0 | 0 | 1 | G 
	 * | 0 | 0 | 0 | 1 | U
	 * | 0 | 0 | 0 | 0 | A
	 */
	public static void printTable(Cell[][] table, String word, int m) {
		for (int i = 0; i < word.length(); i++) {
			System.out.print("    " + word.charAt(i) + "\t");
		}
		System.out.println();
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				if (m == 1) {
					System.out.print("|" + table[i][j].getValue() + "\t");
				} else
					System.out.print("|" + table[i][j].getK() + "\t");
			}
			System.out.print("| " + word.charAt(i) + "\n");
		}
	}

	/*
	 * Accepts a String sequence and return a string of same length with dots
	 */
	public static char[] Dotify(String sequence) {
		char[] word = new char[sequence.length()];
		for (int i = 0; i < sequence.length(); i++) {
			word[i] = '.';
		}
		return word;
	}

	public static void Traceback(char[] sequence, Cell[][] table, int i, int j) {
		// Get variable K,
		int k = table[i][j].getK();

		// If it is pointing to the diagonal or after diagonal
		if (i >= j) {
			return;
		}

		// If K = -1, we know that the sequence was not split, but we took the head and
		// tail out, same as going to the diagonal
		if (k == -1) {
			// System.out.print(i + " " + j + " : " + k + " ");
			sequence[i - 1] = '{';
			sequence[j - 1] = '}';
			Traceback(sequence, table, i + 1, j - 1);
			return;
		}
/*
		// If K = 0, it means that we cut the sequence into two, but the left side has
		// only one letter, so no pairings,
		// we can then just recursively check for the right side of the sequence
		if (k == i-1) {
			Traceback(sequence, table, k + 1 + 1, j);
			return;
		}
		// This handle the reverse of the last case, when we split into two, but now
		// just the right side have one letter
		// we can then just check for the left side of the sequence
		else if (k + 1 + 1 == j) {

			Traceback(sequence, table, i, k + 1);
			return;
		}
*/
		// We found a pairing but there is more pairings on the sequence, so we look for
		// it recursively
		else {
			/*// System.out.print(i + " " + j + " : " + k + " ");
			sequence[i - 1] = '{';
			sequence[j - 1] = '}';*/
			Traceback(sequence, table, i, k + 1);
			Traceback(sequence, table, k + 1 + 1, j);
			return;
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		String choice;
		do {
			// Menu to select input method
			System.out.println("Select your prefered input method");
			System.out.print("1- Read a file \n2- Type from keybord\n");
			System.out.print("Slection: ");
			Scanner sc = new Scanner(System.in);
			int selection = Integer.parseInt(sc.nextLine());
			String sequence;

			//If user wants to read from file
			if (selection == 1) {
				System.out.println(
						"Enter the path to your file:\n(Example: C:\\\\Users\\\\Rodrigo\\\\Desktop\\\\eclipse-workspace\\\\Algorithms\\\\src\\\\Sequence.txt {Must have \\\\ as separator})");
				System.out.print("Path: ");
				String url = sc.nextLine();
				File file = new File(url);
				Scanner scan = new Scanner(file);
				sequence = scan.nextLine();
				sequence = sequence.toUpperCase();
				scan.close();
			} else { //If user wants to type sequence
				System.out.print("Enter sequence: ");
				sequence = sc.nextLine();
				sequence = sequence.toUpperCase();

			}

			char[] pairs;
			// Create a table of type Cell
			Cell[][] table = new Cell[sequence.length() + 1][sequence.length() + 1];

			// Initially have a table of zeroes
			initTable(table);

			pairs = Dotify(sequence);

			// Calculation calling the recursive function
			Cell maxPair = new Cell(0, 0);
			maxPair = PredictionFunction(table, sequence, 0, (sequence.length() - 1));
			table[1][(sequence.length())] = maxPair;

			// Print the maximum number of parings
			System.out.println("\nMax count of pairs: " + maxPair.getValue());

			// printTable(table, " " + sequence, 1); //Print table calculated
			// Print the input sequence
			System.out.println(sequence);
			
			// Call traceback function to fill our String with curly braces
			Traceback(pairs, table, 1, (sequence.length()));
			// System.out.println("\n" + sequence);

			for (int i = 0; i < sequence.length(); i++) {
				System.out.print(pairs[i]);
			}

			System.out.print("\n\nWould you like to print table? (y/n)\nInsert y or n: ");
			choice = sc.nextLine();
			if (choice.compareToIgnoreCase("y") == 0) {
				printTable(table, " " + sequence, 1);
				System.out.println(sequence);
				for (int i = 0; i < sequence.length(); i++) {
					System.out.print(pairs[i]);
				}
			}

			System.out.print("\n\nWould you like to test a new sequence? (y/n)\nInsert y or n: ");
			choice = sc.nextLine();
			System.out.print("\n");
		} while (choice.compareToIgnoreCase("y") == 0);

	}

}

class Cell {
	int value;
	int k;
	public Cell(int value, int k) {
		super();
		this.value = value;
		this.k = k;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	
}
