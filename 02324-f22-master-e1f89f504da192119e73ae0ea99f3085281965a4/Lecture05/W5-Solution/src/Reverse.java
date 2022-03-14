import java.util.*;

class NotWordException extends Exception {
    NotWordException(String str) {
        super("String '" + str + "' is not a valid word!");
    }
}

class Reverse {

    //Generic method returns the reverse of the list
    public static <T> List<T> reverseList(List<T> list) {
        //Create another list to store elements in reverse order
        List<T> reverse = new ArrayList<>(list.size());
        //Iterate through input list in reverse order
        for (int i = list.size() - 1; i >= 0; i--) {
            //add item to the new list
            reverse.add(list.get(i));
        }
        //Return the reversed list
        return reverse;
    }

    public static boolean isPalindrome(String word) throws NotWordException {
        if (!word.matches("[A-Za-z]+")) {
            throw new NotWordException(word);
        }
        List<String> wordList = Arrays.asList(word.split(""));
        List<String> reverseWordList = reverseList(wordList);
        // check if teh same size
        if (reverseWordList.size() != wordList.size()) return false;
        // get iterators
        Iterator<String> wordItr = wordList.iterator();
        Iterator<String> reverseItr = reverseWordList.iterator();
        // compare
        while (wordItr.hasNext()) {
            if (!wordItr.next().equals(reverseItr.next())) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        int valg = 0;
        Scanner scan = new Scanner(System.in);
        try {
            while (valg != 2) {
                System.out.println("MENU TEST PALINDROME");
                System.out.println("Test new word: 1");
                System.out.println("EXIT program: 2");
                System.out.print("Enter option:");
                valg = scan.nextInt();
                try {
                    switch (valg) {
                        case 1: {
                            System.out.print("Enter word:");
                            String str = scan.next();
                            System.out.println("*".repeat(30));
                            System.out.println("Word is" + (!isPalindrome(str) ? " not " : " ") + "palindrome!");
                            System.out.println("*".repeat(30));
                        }
                        break;
                        case 2:
                            System.out.println("Bye");
                            break;
                        default:
                            System.out.println("Ugyldigt tal");
                            break;
                    }
                } catch (NotWordException e) {
                    System.out.println(e.getMessage());
                    System.out.println("The program will try again, enter 2 if you want to stop program!");
                } catch (Exception e) {
                    System.out.println("There was an exception:" + e.getMessage());
                    System.out.println("The program will try again, enter 2 if you want to stop program!");
                }
            }

        } catch (Exception e) {
            System.out.println("There was an exception:" + e.getMessage());
        } finally {
            scan.close();
        }

    }

}