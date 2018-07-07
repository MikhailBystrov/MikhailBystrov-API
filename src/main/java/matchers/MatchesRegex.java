package matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.regex.Pattern;

/**
 * Created by Mikhail on 07.07.2018
 */
public class MatchesRegex extends TypeSafeMatcher<String> {
    private final Pattern pattern;

    public MatchesRegex(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    protected boolean matchesSafely(String item) {
        return pattern.matcher(item).matches();
    }
    @Override
    public void describeTo(Description description) {
        description.appendText("a string matching the pattern '" + pattern + "'");
    }

    public static Matcher<String> matchesPattern(Pattern pattern) {
        return new MatchesRegex(pattern);
    }

    public static Matcher<String> matchesPattern(String regex) {
        return new MatchesRegex(Pattern.compile(regex));
    }
}