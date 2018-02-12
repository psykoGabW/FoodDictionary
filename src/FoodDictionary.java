import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class FoodDictionary {

	public final static String DICTIONARY_FILE = "dbFood.txt";
	public final static String SEPARATOR = ";";

	private final static int NB_OF_ATTRIBUTES = 6;

	public final static String[] ATTRIBUTE_NAMES = { "Name", "Category", "Energetic value (kcal)", "Protein rate (g/100g)",
			"Glucid rate (g/100g)", "Lipid rate (g/100g)" };

	public final static ArrayList<String[]> foodDictionary = new ArrayList<String[]>();

	public static boolean isDictionaryModified = false;

	private static Scanner scanner = new Scanner(System.in);

	private static void loadDatabase() throws FileNotFoundException {

		Scanner fileScanner = new Scanner(new File(DICTIONARY_FILE));
		foodDictionary.clear();
		while (fileScanner.hasNextLine()) {			
			foodDictionary.add(fileScanner.nextLine().split(SEPARATOR, -1));
		}
		fileScanner.close();
	}

	private static void saveDataBase() throws IOException {

		// Backup of database only if database is modified
		if (!isDictionaryModified) {
			return;
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(DICTIONARY_FILE, false));

		Iterator<String[]> itFood = foodDictionary.iterator();

		while (itFood.hasNext()) {
			String[] foodRecords = itFood.next();
			String lineToWrite = "";

			for (int i = 0; i < foodRecords.length; i++) {
				lineToWrite += foodRecords[i] + (i < foodRecords.length - 1 ? SEPARATOR : "");
			}
			writer.write(lineToWrite);
			writer.newLine();
		}

		writer.close();
		isDictionaryModified = false;

	}

	public static void printFood(String[] food) {

		System.out.print("[ ");
		for (int i = 0; i < NB_OF_ATTRIBUTES; i++) {
			System.out.print(ATTRIBUTE_NAMES[i] + ": " + food[i] + ((i!=NB_OF_ATTRIBUTES-1)?" - ":""));
		}
		System.out.println(" ]");

	}

	public static void printFoodDictionary() {

		if (foodDictionary.isEmpty()) {
			try {
				loadDatabase();
			} catch (FileNotFoundException e) {
				// File is not existing... We create it...
			}
		}

		if (foodDictionary.isEmpty()) {
			System.out.println("List is empty");
		} else {

			Iterator<String[]> itFoodDictionary = foodDictionary.iterator();

			while (itFoodDictionary.hasNext()) {
				printFood(itFoodDictionary.next());
				System.out.println();

			}
		}
	}

	public static void addFoodToDataBase() {

		String[] food = new String[NB_OF_ATTRIBUTES];

		for (int i = 0; i < NB_OF_ATTRIBUTES; i++) {
			System.out.print("Enter " + ATTRIBUTE_NAMES[i] + ": ");
			food[i] = scanner.nextLine();
		}

		foodDictionary.add(food);
		isDictionaryModified = true;

	}

	public static void removeFoodFromDatabase() {
		Iterator<String[]> itDictionary = foodDictionary.iterator();
		
		String menuChoice = "";
		System.out.println("Select food to delete (Enter to exit): ");
		
			for (int i=0; i<foodDictionary.size();i++) {
				System.out.println("["+i+"] " + foodDictionary.get(i)[0]);
			}
		
		menuChoice = scanner.nextLine();
		
		if (!menuChoice.equals("")) {
			int iFood = -1;
			iFood = Integer.parseInt(menuChoice);
			
			if (iFood>=0 && iFood<foodDictionary.size()) {
				foodDictionary.remove(iFood);
				isDictionaryModified=true;
			}
			
		}
		
		}
		
		
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Main Menu

		String menuChoice;
		do {
			// Affichage du menu dans la console.
			System.out.println("------------------- Menu -------------------");
			System.out.println("1) List food database");
			System.out.println("2) Add food");
			System.out.println("3) Delete food");
			System.out.println("0) Quit");

			// Récupération de l'entrée clavier qui sera utilisée pour définir l'action à
			// faire.
			menuChoice = scanner.nextLine();
			switch (menuChoice) {
			case "1":
				// cas non détaillé pour ne pas surcharger
				printFoodDictionary();
				break;
			case "2":
				// cas non détaillé pour ne pas surcharger
				addFoodToDataBase();
				break;
			case "3":
				// cas non détaillé pour ne pas surcharger
				removeFoodFromDatabase();
				break;
			case "0":
				// Check of dictionary state to write database if needed
				if (isDictionaryModified) {
					try {
						saveDataBase();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			default:
				break;
			}
		} while (!menuChoice.equals("0"));

		// On ferme l'objet scanner car il ne servira plus (obligatoire pour les objets
		// qui manipulent les Streams).
		scanner.close();

	}

}
