import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Word implements IWord {

    class NotWordException extends Exception {
        NotWordException(String str) {
            super("String '" + str + "' is not a valid word!");
        }
    }

    private List<String> value;

    @Override
    public int size() {
        return value.size();
    }

    public Word(String word) throws NotWordException {
        if (!word.matches("[A-Za-z]+")) {
            throw new NotWordException(word);
        }
        value = Arrays.asList(word.split(""));
    }

    private Word(List<String> list) {
        value = list;
    }

    @Override
    public void printWord() {
        value.forEach((c) -> System.out.print(c));
        System.out.println();
    }

    @Override
    public Word reverseWord() {
        List<String> reverse = new ArrayList<String>(value.size());
        value.forEach((e) -> reverse.add(0, e));
        return new Word(reverse);
    }

    @Override
    public boolean isPalindrome() {
        Word rev = reverseWord();
        Iterator<String> wordItr = value.iterator();
        Iterator<String> reverseItr = rev.value.iterator();
        // compare
        while (wordItr.hasNext()) {
            if (!wordItr.next().equals(reverseItr.next())) return false;
        }
        return true;

    }


}
