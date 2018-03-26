import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class FoodDictionary {

    public final static String DEFAULT_DICTIONARY_FILE = "dbFood.txt";
    // public final static String DEFAULT_DICTIONARY_FILE = "aliments.csv";
    public final static String SEPARATOR = ";";

    private final static int NB_OF_ATTRIBUTES = 6;
    private final static int MAX_NB_OF_SAVING_TRIES = 3;

    public final static String[] ATTRIBUTE_NAMES = {"Name", "Category", "Energetic value (kcal)",
            "Protein rate (g/100g)", "Glucid rate (g/100g)", "Lipid rate (g/100g)"};

//	public final static ArrayList<String[]> foodDictionary = new ArrayList<String[]>();

    public final static ArrayList<Food> foodDictionary = new ArrayList<Food>();

    public static boolean isDictionaryModified = false;

    private static Scanner scanner = new Scanner(System.in);

    /**
     * @param fileName name of the file containing database records
     * @throws FileNotFoundException
     */
    private static boolean loadDatabase(String fileName) {

        boolean isLoadingOk = true;

        Scanner fileScanner;

        try {
            fileScanner = new Scanner(new File(fileName), "UTF-8");

            foodDictionary.clear();


            while (fileScanner.hasNextLine()) {
                String rowData = fileScanner.nextLine();
                Food food = new Food();
                food.initFromFileLine(rowData);
                foodDictionary.add(food);
                //foodDictionary.add(fileScanner.nextLine().split(SEPARATOR, -1));
            }
            fileScanner.close();
        } catch (FileNotFoundException f) {
            System.out.println(f.getMessage());
            isLoadingOk = false;
        } finally {
            isDictionaryModified = !isLoadingOk;
        }

        return isLoadingOk;
    }

    /**
     * @param fileName name of the file in which we will save records of food
     * @throws IOException
     */
    private static boolean saveDataBase(String fileName) {

        boolean isDbSaved = true;
        // Backup of database only if database is modified
        if (!isDictionaryModified) {
            return true;
        }

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileName, false));

            Iterator<Food> itFood = foodDictionary.iterator();

            while (itFood.hasNext()) {
                Food food = itFood.next();
                if (food.isFulfilled()) {
                    String lineToWrite = food.getRowDatasForFileStorage();
                    writer.write(lineToWrite);
                    writer.newLine();
                }

            }
            writer.close();
        } catch (IOException e) {
            // Unable to initialize FileWriter
            System.out.println("Issue during database saving : " + e.getMessage());
            isDbSaved = false;
        }

        isDictionaryModified = !isDbSaved;

        return isDbSaved;
    }

    public static void printFoodDictionary() {

        if (foodDictionary.isEmpty()) {
            System.out.println("List is empty");
        } else {

            Iterator<Food> itFoodDictionary = foodDictionary.iterator();

            while (itFoodDictionary.hasNext()) {
                System.out.println(itFoodDictionary.next().toString());
            }
        }
        System.out.println();

    }

    public static void addFoodToDataBase() {

        Food food = new Food();
        while (!food.isFulfilled()){
            Food.eFoodAttributes foodAttribute = food.getNextAttributeToFill();
            try{
                System.out.print("Please enter " + foodAttribute + " : ");
                food.setAttribute(foodAttribute, scanner.nextLine());
            }catch (DataFormatException e){
                System.out.println(e.toString());
            }

        }

        foodDictionary.add(food);
        isDictionaryModified = true;

/*
        String[] food = new String[NB_OF_ATTRIBUTES];

        for (int i = 0; i < NB_OF_ATTRIBUTES; i++) {
            System.out.print("Enter " + ATTRIBUTE_NAMES[i] + ": ");
            food[i] = scanner.nextLine();
        }

        //foodDictionary.add(food);
        isDictionaryModified = true;
*/

    }

    public static void removeFoodFromDatabase() {

        int menuChoice = -1;
        System.out.println("Select food to delete (Enter to exit): ");

        for (int i = 0; i < foodDictionary.size(); i++) {
            System.out.println("[" + i + "] " + foodDictionary.get(i));
        }

        menuChoice = getUserChoice(0, foodDictionary.size(), true);

        if (menuChoice != -1) {
            // value returned by getUserChoice is in foodDictionary array range

            foodDictionary.remove(menuChoice);
            isDictionaryModified = true;

        }

    }

    public static ArrayList<Food> getSearchResult( String strSearchedValue) {
        ArrayList<Food> lstResult = new ArrayList<Food>();


            Iterator<Food> itFood = foodDictionary.iterator();

            while (itFood.hasNext()) {
                Food food = itFood.next();

                // Search on lexical attribute (toUpperCase)
                if (food.toString().toUpperCase().contains(strSearchedValue.toUpperCase())) {
                    lstResult.add(food);
                }

            }
        return lstResult;
    }

    public static void searchIntoDatabase() {
        // Affichage du menu dans la console.
        System.out.println("------------------- Search -------------------");

        System.out.print("Searched value: ");

        ArrayList<Food> foodResult = getSearchResult( scanner.nextLine());
        System.out.println(String.valueOf(foodResult.size()) + " food found.");

        if (foodResult.size() > 0) {

            if (getUserAcceptance("Do you want to display search Result ?")) {
                Iterator<Food> itFood = foodResult.iterator();
                while (itFood.hasNext()) {
                    System.out.println(itFood.next());
                }
            }

        }

    }

    /**
     * @param currentFileName
     * @return
     */
    public static String getDbFileName(String currentFileName) {
        String newFileName = "";

        System.out.println("------------ Choose database file name ");
        if (!currentFileName.isEmpty()) {
            System.out.println("Current file name : " + currentFileName);
        } else {
            if (getUserAcceptance("Do you want to use database by default (" + DEFAULT_DICTIONARY_FILE + ")?")) {
                newFileName = DEFAULT_DICTIONARY_FILE;
            }
        }

        if (newFileName.isEmpty()) {

            do {
                System.out.print("Enter new database file name : ");
                newFileName = scanner.nextLine().trim();
                if (newFileName.isEmpty() && !currentFileName.isEmpty()) {
                    newFileName = currentFileName;
                }

            } while (newFileName.isEmpty());

        }
        return newFileName;

    }

    private static int getUserChoice(int minAcceptedValue, int maxAcceptedValue) {
        return getUserChoice(minAcceptedValue, maxAcceptedValue, false);
    }

    /**
     * @param minAcceptedValue (int >= 0)
     * @param maxAcceptedValue (int >= 0)
     * @param isEmptyAccepted  (boolean) if true, user can input nothing and we return -1;
     * @return -1 if isEmptyAccepted && user input "" int between minAcceptedValue
     * and maxAcceptedValue
     */
    private static int getUserChoice(int minAcceptedValue, int maxAcceptedValue, boolean isEmptyAccepted) {
        int userChoice = -1;
        String literalChoice = "";
        // get User choice
        do {
            try {
                System.out.println("Your choice : ");
                literalChoice = scanner.nextLine();
                userChoice = Integer.parseInt(literalChoice);
            } catch (NumberFormatException n) {
                userChoice = -1;
            } finally {
                if ((!(userChoice >= minAcceptedValue && userChoice <= maxAcceptedValue))
                        || (literalChoice.isEmpty() && !isEmptyAccepted)) {
                    System.out.println(
                            "Please enter a number in [ " + minAcceptedValue + " ; " + maxAcceptedValue + " ]");
                }
            }
        } while (userChoice == -1 || (isEmptyAccepted && literalChoice.isEmpty()));

        if (literalChoice.isEmpty() && isEmptyAccepted) {
            userChoice = -1;
        }
        return userChoice;

    }

    /**
     * @param questionToAsk Answer has to be Yes(Y) or No(N)
     * @return true if user accept (userChoice == Y) false if user deny (userChoice
     * == N)
     */
    private static boolean getUserAcceptance(String questionToAsk) {

        System.out.println(questionToAsk);
        String userChoice;
        do {
            System.out.print("Your choice (Y/N) :");
            userChoice = scanner.nextLine().toUpperCase();
        } while (!userChoice.equals("Y") && !userChoice.equals("N"));

        return (userChoice.equals("Y"));
    }

    /**
     * Main menu of application
     *
     * @param args : None expected
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String fileName = "";

        // Main Menu
        int menuChoice = -1;
        do {
            // Main menu
            System.out.println("------------------- Menu -------------------");
            System.out.println("1) Food list " + (isDictionaryModified ? "<not saved>" : ""));
            System.out.println("2) Add food");
            System.out.println("3) Delete food");
            System.out.println("4) Search into food database");
            System.out.println();
            System.out.println("5) Load food list from file");
            System.out.println("6) Change database file name");
            System.out.println("7) Save food list");
            System.out.println();
            System.out.println("0) Quit");

            // get User choice

            menuChoice = getUserChoice(0, 7);
            switch (menuChoice) {
                case 1:
                    printFoodDictionary();
                    break;
                case 2:
                    addFoodToDataBase();
                    break;
                case 3:
                    removeFoodFromDatabase();
                    break;
                case 4:
                    searchIntoDatabase();
                    break;
                case 5:
                    if (isDictionaryModified) {
                        System.out.println("<WARNING> Current list has been modified since last save !!! ");
                        System.out.println("Current food list will be replace by file content...");
                        if (!getUserAcceptance("Do you want to continue anyway?")) {
                            menuChoice = -1;
                            break;
                        }
                    }
                    if (fileName.isEmpty()) {
                        fileName = getDbFileName("");
                    }
                    if (!loadDatabase(fileName)) {
                        System.out.println("An error occured during database loading... Please check database " + fileName);
                    }
                    break;
                case 6:
                    fileName = getDbFileName(fileName);
                    isDictionaryModified = true;
                    break;
                case 0:
                    if (!isDictionaryModified) {
                        break;
                    } else {
                        System.out.println("<WARNING> Current list has been modified since last save !!! ");
                        if (getUserAcceptance("Do you want to quit without saving anyway?")) {
                            menuChoice = -1;
                            break;
                        }
                    }

                case 7:
                    // Check of dictionary state to write database if needed
                    if (fileName.isEmpty()) {
                        fileName = getDbFileName(fileName);
                    }
                    boolean isSavingAborted = false;
                    int tryCount = MAX_NB_OF_SAVING_TRIES;
                    while (isDictionaryModified && !isSavingAborted && tryCount > 0) {

                        if (!saveDataBase(fileName)) {
                            System.out.println("Saving of " + fileName + " failed!");
                            System.out.println("What do you want to do ? ");
                            System.out.println("1) Retry saving " + fileName + " " + tryCount + "try lefts");
                            System.out.println("2) Change file name and save again");
                            System.out.println("0) Quit (Warning !! Data may be lost");

                            switch (getUserChoice(0, 2)) {
                                case 1:
                                    tryCount--;
                                    break;
                                case 2:
                                    fileName = getDbFileName(fileName);
                                    tryCount = MAX_NB_OF_SAVING_TRIES;
                                    break;
                                case 0:
                                    isSavingAborted = true;
                            }
                        }
                    }
                default:
                    break;
            }
        } while (menuChoice != 0);

        // Scanner closure... Mandatory
        scanner.close();

    }

}
