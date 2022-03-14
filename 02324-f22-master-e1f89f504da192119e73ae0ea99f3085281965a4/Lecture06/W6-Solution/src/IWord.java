import java.util.List;
import java.util.function.Predicate;

public interface IWord {
    int size();

    void printWord();

    IWord reverseWord();

    boolean isPalindrome();

    // print using words.forEach method and predicate in a lambda expression
    static void printWords(List<IWord> words, Predicate<IWord> predicate) {
        words.forEach(word -> {
            if (predicate.test(word))
                word.printWord();
        });
    }
}
