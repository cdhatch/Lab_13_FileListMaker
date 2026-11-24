
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileListMaker {

    private static ArrayList<String> myArrList = new ArrayList<>();
    private static Scanner in = new Scanner(System.in);

    private static boolean needsToBeSaved = false;
    private static String currentFileName = "";

    public static void main(String[] args) {

        boolean quit = false;

        while (!quit) {

            displayMenu();

            String choice = SafeInput.getRegExString(
                    in,
                    "Enter your choice",
                    "[AaDdIiMmOoSsCcVvQq]"
            );

            try {

                switch (choice.toUpperCase()) {

                    case "A":
                        addItem();
                        break;

                    case "D":
                        deleteItem();
                        break;

                    case "I":
                        insertItem();
                        break;

                    case "M":
                        moveItem();
                        break;

                    case "O":
                        openList();
                        break;

                    case "S":
                        saveList();
                        break;

                    case "C":
                        clearList();
                        break;

                    case "V":
                        viewList();
                        break;

                    case "Q":
                        if (quitProgram()) {
                            quit = true;
                        }
                        break;
                }

            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }

        System.out.println("Goodbye!");
        in.close();
    }

    // Display the menu and current file information
    private static void displayMenu() {

        System.out.println("\n===== FILE LIST MAKER =====");

        if (currentFileName.isEmpty()) {
            System.out.println("Current file: New List");
        } else {
            System.out.println("Current file: " + currentFileName);
        }

        if (needsToBeSaved) {
            System.out.println("Status: Unsaved changes");
        } else {
            System.out.println("Status: Saved");
        }

        System.out.println("\nA - Add an item");
        System.out.println("D - Delete an item");
        System.out.println("I - Insert an item");
        System.out.println("M - Move an item");
        System.out.println("O - Open a list file");
        System.out.println("S - Save the list");
        System.out.println("C - Clear the list");
        System.out.println("V - View the list");
        System.out.println("Q - Quit");
    }

    // Add an item to the end
    private static void addItem() {

        String item = SafeInput.getNonZeroLenString(
                in, "Enter an item to add"
        );

        myArrList.add(item);
        needsToBeSaved = true;

        System.out.println("Item added!");
    }

    // Delete an item
    private static void deleteItem() {

        if (myArrList.size() == 0) {
            System.out.println("The list is empty.");
            return;
        }

        viewList();

        int number = SafeInput.getRangedInt(
                in,
                "Enter the item number to delete",
                1,
                myArrList.size()
        );

        myArrList.remove(number - 1);
        needsToBeSaved = true;

        System.out.println("Item deleted!");
    }

    // Insert an item at a selected location
    private static void insertItem() {

        int location = SafeInput.getRangedInt(
                in,
                "Enter the location to insert the item",
                1,
                myArrList.size() + 1
        );

        String item = SafeInput.getNonZeroLenString(
                in, "Enter the item"
        );

        myArrList.add(location - 1, item);
        needsToBeSaved = true;

        System.out.println("Item inserted!");
    }

    // Move an item to another location
    private static void moveItem() {

        if (myArrList.size() < 2) {
            System.out.println("You need at least two items to move an item.");
            return;
        }

        viewList();

        int oldLocation = SafeInput.getRangedInt(
                in,
                "Enter the item number to move",
                1,
                myArrList.size()
        );

        int newLocation = SafeInput.getRangedInt(
                in,
                "Enter the new location",
                1,
                myArrList.size()
        );

        String item = myArrList.remove(oldLocation - 1);
        myArrList.add(newLocation - 1, item);

        needsToBeSaved = true;

        System.out.println("Item moved!");
    }

    // View the current list
    private static void viewList() {

        System.out.println("\n===== YOUR LIST =====");

        if (myArrList.size() == 0) {
            System.out.println("Your list is empty.");
        } else {
            for (int i = 0; i < myArrList.size(); i++) {
                System.out.println((i + 1) + ". " + myArrList.get(i));
            }
        }
    }

    // Clear the current list
    private static void clearList() {

        if (myArrList.size() == 0) {
            System.out.println("The list is already empty.");
            return;
        }

        if (SafeInput.getYNConfirm(
                in, "Are you sure you want to clear the list?")) {

            myArrList.clear();
            needsToBeSaved = true;

            System.out.println("List cleared!");
        }
    }

    // Save the current list to a file
    private static void saveList() throws IOException {

        if (currentFileName.isEmpty()) {

            String fileName = SafeInput.getNonZeroLenString(
                    in, "Enter a filename (without .txt)"
            );

            if (fileName.toLowerCase().endsWith(".txt")) {
                currentFileName = fileName;
            } else {
                currentFileName = fileName + ".txt";
            }
        }

        Path filePath = Paths.get(currentFileName);

        Files.write(
                filePath,
                myArrList,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        needsToBeSaved = false;

        System.out.println("List saved to " + currentFileName);
    }

    // Open a list from a file
    private static void openList() throws IOException {

        if (needsToBeSaved) {

            System.out.println("You have unsaved changes.");

            if (SafeInput.getYNConfirm(
                    in, "Would you like to save before opening another file?")) {

                saveList();

            } else {

                if (!SafeInput.getYNConfirm(
                        in, "Are you sure you want to discard your changes?")) {

                    System.out.println("Opening canceled.");
                    return;
                }
            }
        }

        String fileName = SafeInput.getNonZeroLenString(
                in, "Enter the filename to open"
        );

        if (!fileName.toLowerCase().endsWith(".txt")) {
            fileName = fileName + ".txt";
        }

        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath)) {
            System.out.println("File not found.");
            return;
        }

        List<String> fileLines = Files.readAllLines(filePath);

        myArrList.clear();
        myArrList.addAll(fileLines);

        currentFileName = fileName;
        needsToBeSaved = false;

        System.out.println("File opened successfully!");
    }

    // Check for unsaved changes before quitting
    private static boolean quitProgram() throws IOException {

        if (needsToBeSaved) {

            System.out.println("You have unsaved changes.");

            if (SafeInput.getYNConfirm(
                    in, "Would you like to save before quitting?")) {

                saveList();
                return true;

            } else {

                return SafeInput.getYNConfirm(
                        in, "Are you sure you want to quit without saving?"
                );
            }
        }

        return SafeInput.getYNConfirm(
                in, "Are you sure you want to quit?"
        );
    }
}
