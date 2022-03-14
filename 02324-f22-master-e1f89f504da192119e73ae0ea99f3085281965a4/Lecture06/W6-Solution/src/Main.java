import java.util.*;
import java.util.function.Predicate;


class Main {
    static class Palindrome {
        public static void printPalindromes(List<IWord> words) {
            try {
                System.out.println("The following words are palindromes:");
                IWord.printWords(words, (IWord w) -> w.isPalindrome());
            } catch (Exception e) {
                System.out.println("There was an exception:" + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        try {
            List<IWord> words = new ArrayList<>(3);
            System.out.println("Enter 3 words:");
            for (int i = 0; i < 3; i++) {
                System.out.print((i+1) + ":");
                String str = scan.next();
                words.add(new Word(str));
            }
            System.out.println("*".repeat(30));
            Palindrome.printPalindromes(words);
        } catch (Exception e) {
            System.out.println("There was an exception:" + e.getMessage());
        } finally {
            scan.close();
        }

    }

}