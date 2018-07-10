import beans.YandexSpellerAnswer;
import core.YandexSpellerApi;
import io.restassured.RestAssured;
import matchers.AnswersMatcher;
import matchers.MatchesRegex;
import org.testng.annotations.Test;

import java.util.List;

import static core.YandexSpellerConstants.ASSERT_REASON;
import static core.YandexSpellerConstants.NO_MISTAKES_RESPONSE;
import static core.YandexSpellerConstants.PARAM_LANG;
import static core.YandexSpellerConstants.PARAM_OPTIONS;
import static core.YandexSpellerConstants.PARAM_TEXT;
import static core.YandexSpellerConstants.WORD_WITH_NO_MISTAKES;
import static data.CheckTextData.ACCIDENTAL_SPACE;
import static data.CheckTextData.LOWERCASE_AND_UPPERCASE;
import static data.CheckTextData.MISSED_LETTER;
import static data.CheckTextData.NO_SPACES_WORDS;
import static data.CheckTextData.REPEAT_WORDS;
import static data.CheckTextData.SUPERFLUOUS_LETTER;
import static data.CheckTextData.WORD_NAME;
import static data.CheckTextData.WORD_WITH_DIGITS;
import static data.CheckTextData.WRONG_URL;
import static enums.Languages.EN;
import static enums.Languages.RU;
import static enums.Options.FIND_REPEAT_WORDS;
import static enums.Options.IGNORE_DIGITS;
import static enums.Options.IGNORE_URLS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.Assert.assertTrue;

/**
 * Created by Mikhail on 05.07.2018
 */
public class TestYandexSpellerJSON {

    private AnswersMatcher answersMatcher = new AnswersMatcher();
    // 1. checkText with no mistakes
    @Test(description = "Api should return empty body if there are no mistakes")
    public void wordWithNoMistakes() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_TEXT, WORD_WITH_NO_MISTAKES)
                .get().prettyPeek()
                .then()
                .body(MatchesRegex.matchesPattern(NO_MISTAKES_RESPONSE))
                .specification(YandexSpellerApi.successResponse());
    }

    // 2. checkText with no space between words
    @Test(description = "Api should return right words if there are no spaces between words")
    public void wordWithNoSpaces(){
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .language(RU.languageCode)
                                .text(NO_SPACES_WORDS.word)
                                .callApi());
            assertTrue(answersMatcher.matchesAnswers(answers, 1, "невероятные приключения"));
    }

    // 3. checkText with accidental space
    @Test(description = "Api should return right words with no spaces in them")
    public void wordWithAccidentalSpace() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .language(RU.languageCode)
                                .text(ACCIDENTAL_SPACE.word)
                                .callApi());
        assertTrue(answersMatcher.matchesAnswers(answers, 2, "электрическая"));
        assertTrue(answersMatcher.matchesAnswers(answers, 2, "дуга"));
    }

    // 4. checkText with alternating lowercase and uppercase in one word
    // BUG: Speller can't change lower- and uppercase
    @Test(description = "Api should return words in lower case")
    public void alternatingWordCase(){
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .language(RU.languageCode)
                                .text(LOWERCASE_AND_UPPERCASE.word)
                                .callApi());
        assertTrue(answersMatcher.matchesAnswers(answers, 3, "написание"));
    }

    // 5. checkText with digits attached to words
    @Test(description = "Api should correct words with attached digits")
    public void correctWordsDigits(){
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi
                                .with()
                                .language(RU.languageCode)
                                .text(WORD_WITH_DIGITS.word)
                                .callApi());
        assertTrue(answersMatcher.matchesAnswers(answers, 1, "33 желания"));
    }

    // 6. checkText with digits attached to words and option equal '2'
    @Test(description = "Api should ignore words with attached digits when option equal '2'")
    public void ignoreWordsDigits(){
        YandexSpellerApi
                .with()
                .language(RU.languageCode)
                .options(IGNORE_DIGITS.code)
                .text(WORD_WITH_DIGITS.word)
                .callApi().
                then()
                .assertThat()
                .body(MatchesRegex.matchesPattern(NO_MISTAKES_RESPONSE));
    }

    // 7. checkText with names
    // BUG: Speller can't replace first letter of name
    @Test(description = "Api should return right capital letter")
    public void wordsNames() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi
                                .with()
                                .language(EN.languageCode)
                                .text(WORD_NAME.word)
                                .callApi());
        assertThat(ASSERT_REASON, answers.size(), equalTo(3));
        for(String rightWord : WORD_NAME.rightWord) {
            assertThat(answers.get(WORD_NAME.rightWord.indexOf(rightWord)).s.get(0), equalTo(rightWord));
        }
    }

    // 8. checkText with missed letter
    @Test(description = "Api should return corrected word with missed letter")
    public void wordMissedLetter() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi
                                .with()
                                .language(RU.languageCode)
                                .text(MISSED_LETTER.word)
                                .callApi());
        assertThat(ASSERT_REASON, answers.size(), equalTo(1));
        assertThat(answers.get(0).s.get(0), equalTo(MISSED_LETTER.rightWord.get(0)));
    }

    // 9. checkText with superfluous letter
    @Test(description = "Api should return corrected word without superfluous letter")
    public void superfluousLetterWord() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi
                                .with()
                                .language(RU.languageCode)
                                .text(SUPERFLUOUS_LETTER.word)
                                .callApi());
        assertThat(ASSERT_REASON, answers.size(), equalTo(1));
        assertThat(answers.get(0).s.get(0), equalTo(SUPERFLUOUS_LETTER.rightWord.get(0)));
    }

    // 10. checkText correct wrong url
    @Test(description = "Api should correct URL with mistake")
    public void urlWithMistakes() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi
                                .with()
                                .language(EN.languageCode)
                                .text(WRONG_URL.word)
                                .callApi());
        assertTrue(answersMatcher.matchesAnswers(answers, 1, "http"), ASSERT_REASON);
    }

    // 11. checkText ignore wrong url
    // BUG: Speller should passed url with mistake then option equals '4'
    @Test(description = "Api should ignore URL with mistake when option is equal '4'")
    public void ignoreUrlWithMistakes() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_OPTIONS, IGNORE_URLS.code)
                .param(PARAM_LANG, EN)
                .param(PARAM_TEXT, WRONG_URL.word)
                .get().prettyPeek()
                .then()
                .body(MatchesRegex.matchesPattern(NO_MISTAKES_RESPONSE))
                .specification(YandexSpellerApi.successResponse());
    }

    // 12. checkText API should delete repeated word(s)
    // BUG: Speller can't delete repeated words
    @Test(description = "Api should delete repeated words")
    public void deleteRepeatWords() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(REPEAT_WORDS.word)
                        .language(EN.languageCode)
                        .options(FIND_REPEAT_WORDS.code)
                        .callApi());
        assertThat(ASSERT_REASON, answers.size(), greaterThan(1));
        assertThat(answers.get(0).s.get(0), equalTo(REPEAT_WORDS.rightWord.get(0)));
    }
}
