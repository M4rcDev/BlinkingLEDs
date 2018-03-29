package de.marcmaurer;

import java.util.*;
import java.util.regex.Pattern;

public class Main {
    public static Map<Character, Integer> register = new HashMap<>();
    public static ArrayList<String> history = new ArrayList<>();


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useDelimiter(Pattern.compile("(\\n)"));
        while (true) {
            String input = scanner.next();
            addToHistory(input);
            processCommandString(input);
        }
    }

    /**
     * processes the command
     * @param input
     */
    public static void processCommandString(String input) {
        String[] instruction = input.toString().split("\\s+");

        if (instruction.length == 1) {
            //First Check if its an valid command, if not than it must be a loop
            if (instruction[0].equalsIgnoreCase("exit"))
                System.exit(0);


            if (instruction[0].equalsIgnoreCase("rlca")) {
                if (register.get('a') == null) {
                    System.out.println("Register a was not set yet, cannot execute command");
                    return;
                }
                register.put('a', shiftBits(register.get('a'), false));
                return;
            }
            if (instruction[0].equalsIgnoreCase("rrca")) {
                if (register.get('a') == null) {
                    System.out.println("Register a was not set yet, cannot execute command");
                    return;
                }
                register.put('a', shiftBits(register.get('a'), true));
                //  register.put('a', Integer.rotateRight(register.get('a'), 1));
                return;
            }

            if (input.endsWith(":"))
                return;

            System.out.println("wrong instruction");
            return;

        } else if (instruction.length != 2) {
            System.out.println("wrong instruction length");
            return;
        }


        if (!processCommand(instruction))
            System.out.println("could not process command, maybe wrong arguments?");
    }


    /**
     * Processing the command
     *
     * @param commandArray takes an array with the length of 2
     * @return boolean - True the execution was successfully
     */
    public static boolean processCommand(String[] commandArray) {
        //Everything splitted by a comma
        String[] commandArgs = commandArray[1].split(",");

        if (commandArray[0].equalsIgnoreCase("ld")) {
            register.put(commandArgs[0].charAt(0), Integer.valueOf(commandArgs[1]));
            return true;
        }
        if (commandArray[0].equalsIgnoreCase("out")) {
            printOut(commandArgs[1].charAt(0));
            return true;
        }
        if (commandArray[0].equalsIgnoreCase("djnz")) {
            //Stop addToLoop Mode to avoid infinitely execution
            executeLoop(commandArgs[0], commandArgs[1].charAt(0));
            return true;
        }
        return false;
    }

    /**
     * Prints a simulated rows of LEDs
     *
     * @param registerChar
     */
    public static void printOut(Character registerChar) {
        if (!register.containsKey(registerChar)) {
            System.out.println("Register " + registerChar + " was not set yet");
            return;
        }
        Integer number = register.get(registerChar);
        StringBuilder sb = new StringBuilder();
        //Check for each Bit of the Integer if its on or off
        for (int i = 7; i > -1; i--) {
            if (getBit(i, number) == 1) {
                sb.append("*");
            } else {
                sb.append(".");
            }
        }
        System.out.println(sb.toString());
    }


    /**
     * executes a loop by name
     * @param loopName
     * @param registerChar
     */
    public static void executeLoop(String loopName, Character registerChar) {
        //Check if register exists
        if (register.get(registerChar) == null) {
            System.out.print("Register " + registerChar + " not set, cannot execute command");
            return;
        }
        //if its 0 the loop is finished
        int registerCharValue = register.get(registerChar);
        if (registerCharValue < 0) {
            return;
        }


        register.put(registerChar, registerCharValue - 1);

        //Find Start Position
        int loopPosition = -1;
        for (int i = history.size() - 1; i > 0; i--) {
            if (history.get(i).equalsIgnoreCase(loopName + ":")) {
                loopPosition = i;
                break;
            }
        }

        if (loopPosition == -1) {
            System.out.println("Cannot find loop with name " + loopName + " in history");
            return;
        }


        //Execute all commands from label
        for (int i = loopPosition; history.size() > i; i++) {
            processCommandString(history.get(i));
        }


        //call this function again until it reaches 0 in register and gets aborted by the if statement above
        executeLoop(loopName, registerChar);
    }

    /**
     * Adds a command to the history
     * @param command
     */
    public static void addToHistory(String command) {
        if (command.equalsIgnoreCase(""))
            return;
        history.add(command);
    }


    /**
     * Checks if a bit is set on a specific position of an integer
     *
     * @param position position of the bit that needs to be checked
     * @param input    number to check
     * @return int - returns 1 if it is set.
     */
    public static int getBit(int position, int input) {
        return (byte) (input >> position) & 1;
    }

    /**
     * shift bits in a given direction by one
     * @param input
     * @param toTheRight
     * @return
     */
    public static Integer shiftBits(int input, boolean toTheRight) {
        String s = String.format("%8s", Integer.toBinaryString(input)).replace(' ', '0');
        return Integer.parseInt(toTheRight ? rotateStringLeft(s, -1) : rotateStringLeft(s, 1), 2);

    }

    /**
     * rotate a string to the left
     * @param string
     * @param shift - how much it should get shifted by left
     * @return
     */
    public static String rotateStringLeft(String string, final int shift) {
        final int length = string.length();
        if (length == 0) return "";
        final int offset = ((shift % length) + length) % length; // get a positive offset

        return string.substring(offset, length) + string.substring(0, offset);
    }
}
