package matchers;

import beans.YandexSpellerAnswer;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;



/**
 * Created by Mikhail on 09.07.2018
 */
public class AnswersMatcher extends TypeSafeMatcher {

    @Override
    protected boolean matchesSafely(Object o) {
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("num of answers");
    }

    public boolean matchesAnswers (List<YandexSpellerAnswer> answers, int numberOfChanges, String text) {
        for(YandexSpellerAnswer answer : answers) {
            if(answers.size() == numberOfChanges && answer.s.contains(text)) {
                return true;
            }
        }
        return false;
    }
}
