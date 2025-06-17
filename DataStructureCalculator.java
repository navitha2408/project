import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class DataStructureCalculator {

    private static Scanner scanner = new Scanner(System.in); // Initialized Scanner for console input
    private static ArrayList<Double> initialInputs; // To store the user's initial numbers

    public static void main(String[] args) {
        System.out.println("Welcome to the Data Structure Calculator!");

        collectInitialInputs();

        while (true) {
            displayMainMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    performArrayOperations();
                    break;
                case 2:
                    performLinkedListOperations();
                    break;
                case 3:
                    performQueueOperations();
                    break;
                case 4:
                    performBasicCalculations();
                    break;
                case 5:
                    System.out.println("Exiting Data Structure Calculator. Goodbye!");
                    scanner.close(); // Close the scanner when done
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Consume newline after user sees messages and before next menu
        }
    }

    private static void collectInitialInputs() {
        System.out.print("How many numbers do you want to enter for data structure operations? (Enter 0 if none): ");
        int count = getIntInput(); // Uses the getIntInput() method

        initialInputs = new ArrayList<>();
        if (count <= 0) {
            System.out.println("No initial numbers will be collected for data structures. You can still perform direct calculations.");
            return;
        }

        System.out.println("Please enter " + count + " numbers:");
        for (int i = 0; i < count; i++) {
            System.out.print("Enter number " + (i + 1) + ": ");
            initialInputs.add(getDoubleInput()); // Uses the getDoubleInput() method
        }
        System.out.println("Initial numbers collected: " + initialInputs);
    }

    private static void displayMainMenu() {
        System.out.println("\n--- Choose an Operation Category ---");
        System.out.println("1. Array Operations");
        System.out.println("2. Linked List Operations");
        System.out.println("3. Queue Operations");
        System.out.println("4. Basic Two-Number Calculator");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        return getIntInput(); // Uses the getIntInput() method
    }

    // --- CONSOLE INPUT HELPER METHODS ---
    private static int getIntInput() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter an integer: ");
                scanner.next(); // Consume the invalid input
            } finally {
                scanner.nextLine(); // Consume the rest of the line (e.g., the Enter key press)
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.next(); // Consume the invalid input
            } finally {
                scanner.nextLine(); // Consume the rest of the line
            }
        }
    }
    // --- END CONSOLE INPUT HELPER METHODS ---


    // --- Basic Two-Number Calculator ---
    private static void performBasicCalculations() {
        System.out.println("\n--- Basic Two-Number Calculator ---");
        System.out.println("You can choose numbers from your initial inputs or enter new ones.");

        while (true) {
            System.out.println("\nCalculator Menu:");
            System.out.println("1. Use numbers from initial inputs");
            System.out.println("2. Enter new numbers");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int selectionChoice = getUserChoice();
            double num1, num2;

            if (selectionChoice == 1) {
                if (initialInputs.size() < 2) {
                    System.out.println("Not enough initial numbers (" + initialInputs.size() + ") to select from. Please enter new numbers or go back.");
                    continue;
                }
                System.out.println("Initial numbers available: " + initialInputs);
                System.out.print("Enter index of first number (0-indexed): ");
                int index1 = getIntInput();
                System.out.print("Enter index of second number (0-indexed): ");
                int index2 = getIntInput();

                if (index1 >= 0 && index1 < initialInputs.size() &&
                    index2 >= 0 && index2 < initialInputs.size()) {
                    num1 = initialInputs.get(index1);
                    num2 = initialInputs.get(index2);
                } else {
                    System.out.println("Invalid indices. Please try again.");
                    continue;
                }
            } else if (selectionChoice == 2) {
                System.out.print("Enter first number: ");
                num1 = getDoubleInput();
                System.out.print("Enter second number: ");
                num2 = getDoubleInput();
            } else if (selectionChoice == 3) {
                return;
            } else {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }

            System.out.println("\nSelected numbers: " + num1 + " and " + num2);
            System.out.println("Choose an operation:");
            System.out.println("1. Addition (+)");
            System.out.println("2. Subtraction (-)");
            System.out.println("3. Multiplication (*)");
            System.out.println("4. Division (/)");
            System.out.print("Enter operation choice: ");

            int operationChoice = getUserChoice();
            double result = 0;
            boolean operationPerformed = true;

            switch (operationChoice) {
                case 1:
                    result = num1 + num2;
                    System.out.println(num1 + " + " + num2 + " = " + result);
                    break;
                case 2:
                    result = num1 - num2;
                    System.out.println(num1 + " - " + num2 + " = " + result);
                    break;
                case 3:
                    result = num1 * num2;
                    System.out.println(num1 + " * " + num2 + " = " + result);
                    break;
                case 4:
                    if (num2 == 0) {
                        System.out.println("Error: Division by zero is not allowed.");
                        operationPerformed = false;
                    } else {
                        result = num1 / num2;
                        System.out.println(num1 + " / " + num2 + " = " + result);
                    }
                    break;
                default:
                    System.out.println("Invalid operation choice.");
                    operationPerformed = false;
            }

            if (operationPerformed) {
                System.out.print("Add result (" + result + ") to initial inputs? (y/n): ");
                String addResultChoice = scanner.nextLine().trim().toLowerCase(); // Use scanner.nextLine() for string
                if (addResultChoice.equals("y")) {
                    initialInputs.add(result);
                    System.out.println("Result added to initial inputs: " + initialInputs);
                }
            }

            System.out.println("\nPress Enter to continue Basic Calculator operations...");
            scanner.nextLine();
        }
    }

    // Helper method to get numbers for data structure if initialInputs is empty
    private static ArrayList<Double> getNumbersForNewDataStructure(String type) {
        System.out.println("\nNo initial numbers were provided, or you chose to use new numbers for this " + type + ".");
        System.out.print("How many numbers do you want to use for this " + type + "? ");
        int count = getIntInput();
        ArrayList<Double> tempInputs = new ArrayList<>();
        if (count <= 0) {
            System.out.println("No numbers will be used for this " + type + " operation.");
            return tempInputs;
        }
        System.out.println("Please enter " + count + " numbers for " + type + ":");
        for (int i = 0; i < count; i++) {
            System.out.print("Enter number " + (i + 1) + ": ");
            tempInputs.add(getDoubleInput());
        }
        return tempInputs;
    }


    // --- Array Operations ---
    private static void performArrayOperations() {
        ArrayList<Double> currentArray;
        if (initialInputs.isEmpty()) {
            currentArray = getNumbersForNewDataStructure("Array");
            if (currentArray.isEmpty()) return;
        } else {
            currentArray = new ArrayList<>(initialInputs);
        }

        System.out.println("\n--- Array Operations ---");
        System.out.println("Current Array: " + currentArray);

        while (true) {
            System.out.println("\nArray Menu:");
            System.out.println("1. Display Array");
            System.out.println("2. Sum of Elements");
            System.out.println("3. Average of Elements");
            System.out.println("4. Find Minimum Element");
            System.out.println("5. Find Maximum Element");
            System.out.println("6. Sort Array (Ascending)");
            System.out.println("7. Search for an Element");
            System.out.println("8. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    System.out.println("Array: " + currentArray);
                    break;
                case 2:
                    if (currentArray.isEmpty()) {
                        System.out.println("Array is empty, cannot calculate sum.");
                    } else {
                        double sum = currentArray.stream().mapToDouble(Double::doubleValue).sum();
                        System.out.println("Sum of elements: " + sum);
                    }
                    break;
                case 3:
                    if (currentArray.isEmpty()) {
                        System.out.println("Array is empty, cannot calculate average.");
                    } else {
                        double avg = currentArray.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                        System.out.println("Average of elements: " + String.format("%.2f", avg));
                    }
                    break;
                case 4:
                    if (currentArray.isEmpty()) {
                        System.out.println("Array is empty, no minimum element.");
                    } else {
                        System.out.println("Minimum element: " + Collections.min(currentArray));
                    }
                    break;
                case 5:
                    if (currentArray.isEmpty()) {
                        System.out.println("Array is empty, no maximum element.");
                    } else {
                        System.out.println("Maximum element: " + Collections.max(currentArray));
                    }
                    break;
                case 6:
                    Collections.sort(currentArray);
                    System.out.println("Array sorted: " + currentArray);
                    break;
                case 7:
                    System.out.print("Enter element to search: ");
                    double searchElement = getDoubleInput();
                    if (currentArray.contains(searchElement)) {
                        System.out.println(searchElement + " found at index: " + currentArray.indexOf(searchElement));
                    } else {
                        System.out.println(searchElement + " not found in the array.");
                    }
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\nPress Enter to continue Array operations...");
            scanner.nextLine();
        }
    }


    // --- Linked List Operations ---
    private static void performLinkedListOperations() {
        LinkedList<Double> currentList;
        if (initialInputs.isEmpty()) {
            currentList = new LinkedList<>(getNumbersForNewDataStructure("Linked List"));
            if (currentList.isEmpty()) return;
        } else {
            currentList = new LinkedList<>(initialInputs);
        }

        System.out.println("\n--- Linked List Operations ---");
        System.out.println("Current List: " + currentList);

        while (true) {
            System.out.println("\nLinked List Menu:");
            System.out.println("1. Display List");
            System.out.println("2. Add Element at Start");
            System.out.println("3. Add Element at End");
            System.out.println("4. Add Element at Specific Position");
            System.out.println("5. Remove Element from Start");
            System.out.println("6. Remove Element from End");
            System.out.println("7. Remove Element at Specific Position");
            System.out.println("8. Search for an Element");
            System.out.println("9. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    System.out.println("List: " + currentList);
                    break;
                case 2:
                    System.out.print("Enter element to add at start: ");
                    currentList.addFirst(getDoubleInput());
                    System.out.println("Element added. Current List: " + currentList);
                    break;
                case 3:
                    System.out.print("Enter element to add at end: ");
                    currentList.addLast(getDoubleInput());
                    System.out.println("Element added. Current List: " + currentList);
                    break;
                case 4:
                    System.out.print("Enter element to add: ");
                    double elementToAdd = getDoubleInput();
                    System.out.print("Enter position (0-indexed): ");
                    int posToAdd = getIntInput();
                    if (posToAdd >= 0 && posToAdd <= currentList.size()) {
                        currentList.add(posToAdd, elementToAdd);
                        System.out.println("Element added. Current List: " + currentList);
                    } else {
                        System.out.println("Invalid position.");
                    }
                    break;
                case 5:
                    if (!currentList.isEmpty()) {
                        System.out.println("Removed from start: " + currentList.removeFirst());
                        System.out.println("Current List: " + currentList);
                    } else {
                        System.out.println("List is empty.");
                    }
                    break;
                case 6:
                    if (!currentList.isEmpty()) {
                        System.out.println("Removed from end: " + currentList.removeLast());
                        System.out.println("Current List: " + currentList);
                    } else {
                        System.out.println("List is empty.");
                    }
                    break;
                case 7:
                    if (currentList.isEmpty()) {
                        System.out.println("List is empty, nothing to remove.");
                        break;
                    }
                    System.out.print("Enter position to remove (0-indexed): ");
                    int posToRemove = getIntInput();
                    if (posToRemove >= 0 && posToRemove < currentList.size()) {
                        System.out.println("Removed from position " + posToRemove + ": " + currentList.remove(posToRemove));
                        System.out.println("Current List: " + currentList);
                    } else {
                        System.out.println("Invalid position.");
                    }
                    break;
                case 8:
                    System.out.print("Enter element to search: ");
                    double searchElement = getDoubleInput();
                    if (currentList.contains(searchElement)) {
                        System.out.println(searchElement + " found at index: " + currentList.indexOf(searchElement));
                    } else {
                        System.out.println(searchElement + " not found in the list.");
                    }
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\nPress Enter to continue Linked List operations...");
            scanner.nextLine();
        }
    }


    // --- Queue Operations ---
    private static void performQueueOperations() {
        Queue<Double> currentQueue;
        if (initialInputs.isEmpty()) {
            currentQueue = new LinkedList<>(getNumbersForNewDataStructure("Queue"));
            if (currentQueue.isEmpty()) return;
        } else {
            currentQueue = new LinkedList<>(initialInputs);
        }

        System.out.println("\n--- Queue Operations ---");
        System.out.println("Current Queue: " + currentQueue);

        while (true) {
            System.out.println("\nQueue Menu:");
            System.out.println("1. Display Queue");
            System.out.println("2. Enqueue (Add Element)");
            System.out.println("3. Dequeue (Remove Element from Front)");
            System.out.println("4. Peek (View Front Element)");
            System.out.println("5. Shrink Queue (Remove multiple from front)");
            System.out.println("6. Rotate Queue (Shift elements)");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    System.out.println("Queue: " + currentQueue);
                    break;
                case 2:
                    System.out.print("Enter element to enqueue: ");
                    currentQueue.offer(getDoubleInput());
                    System.out.println("Element enqueued. Current Queue: " + currentQueue);
                    break;
                case 3:
                    if (!currentQueue.isEmpty()) {
                        System.out.println("Dequeued: " + currentQueue.poll());
                        System.out.println("Current Queue: " + currentQueue);
                    } else {
                        System.out.println("Queue is empty.");
                    }
                    break;
                case 4:
                    if (!currentQueue.isEmpty()) {
                        System.out.println("Front element (peek): " + currentQueue.peek());
                    } else {
                        System.out.println("Queue is empty.");
                    }
                    break;
                case 5:
                    shrinkQueue(currentQueue);
                    break;
                case 6:
                    rotateQueue(currentQueue);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\nPress Enter to continue Queue operations...");
            scanner.nextLine();
        }
    }


    private static void shrinkQueue(Queue<Double> queue) {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty, nothing to shrink.");
            return;
        }
        System.out.print("How many elements to remove from the front? ");
        int numToShrink = getIntInput();

        if (numToShrink <= 0) {
            System.out.println("No elements removed.");
            return;
        }

        int removedCount = 0;
        for (int i = 0; i < numToShrink; i++) {
            if (!queue.isEmpty()) {
                System.out.println("Removed: " + queue.poll());
                removedCount++;
            } else {
                System.out.println("Queue became empty. Cannot remove more elements.");
                break;
            }
        }
        System.out.println(removedCount + " element(s) removed.");
        System.out.println("Remaining elements: " + queue);
    }

    private static void rotateQueue(Queue<Double> queue) {
        if (queue.isEmpty() || queue.size() == 1) {
            System.out.println("Queue is too small to rotate effectively.");
            return;
        }
        System.out.print("How many positions to rotate (positive for left/forward, negative for right/backward)? ");
        int positions = getIntInput();

        int actualRotations = positions % queue.size();
        if (actualRotations < 0) {
            actualRotations += queue.size();
        }

        System.out.println("Initial Queue: " + queue);
        for (int i = 0; i < actualRotations; i++) {
            Double front = queue.poll();
            if (front != null) {
                queue.offer(front);
            }
        }
        System.out.println("Queue rotated by " + positions + " positions. New Queue: " + queue);
    }
}