import beans.YandexSpellerAnswer;
import core.YandexSpellerApi;
import core.YandexSpellerConstants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static core.YandexSpellerConstants.Languages.EN;
import static core.YandexSpellerConstants.PARAM_LANG;
import static core.YandexSpellerConstants.PARAM_OPTIONS;
import static core.YandexSpellerConstants.PARAM_TEXT;
import static core.YandexSpellerConstants.RIGHT_WORD_EN;
import static core.YandexSpellerConstants.WORD_WITH_NO_MISTAKES;
import static core.YandexSpellerConstants.WRONG_WORD_EN;
import static core.YandexSpellerConstants.YANDEX_SPELLER_API_URI;
import static data.CheckTextData.ACCIDENTAL_SPACE;
import static data.CheckTextData.LOWERCASE_AND_UPPERCASE;
import static data.CheckTextData.MISSED_LETTER;
import static data.CheckTextData.NO_SPACES_WORDS;
import static data.CheckTextData.REPEAT_WORDS;
import static data.CheckTextData.SUPERFLUOUS_LETTER;
import static data.CheckTextData.WORD_NAME;
import static data.CheckTextData.WORD_WITH_DIGITS;
import static data.CheckTextData.WRONG_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

/**
 * Created by Mikhail on 05.07.2018
 */
public class TestYandexSpellerJSON {

    // simple usage of RestAssured library: direct request call and response validations in test.
    @Test
    public void simpleSpellerApiCall() {
        RestAssured
                .given()
                .queryParam(PARAM_TEXT, WRONG_WORD_EN)
                .params(PARAM_LANG, EN, "CustomParameter", "valueOfParam")
                .accept(ContentType.JSON)
                .auth().basic("abcName", "abcPassword")
                .header("custom header1", "header1.value")
                .and()
                .body("some body payroll")
                .log().everything()
                .when()
                .get(YANDEX_SPELLER_API_URI)
                .prettyPeek()
                .then()

                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(Matchers.allOf(
                        Matchers.stringContainsInOrder(Arrays.asList(WRONG_WORD_EN, RIGHT_WORD_EN)),
                        Matchers.containsString("\"code\":1")))
                .contentType(ContentType.JSON)
                .time(lessThan(20000L)); // Milliseconds
    }

    // different http methods calls
    @Test
    public void spellerApiCallsWithDifferentMethods() {
        //GET
        RestAssured
                .given()
                .param(PARAM_TEXT, WRONG_WORD_EN)
                .log().everything()
                .get(YANDEX_SPELLER_API_URI)
                .prettyPeek();
        System.out.println("\n=====================================================================");

        //POST
        RestAssured
                .given()
                .param(PARAM_TEXT, WRONG_WORD_EN)
                .log().everything()
                .post(YANDEX_SPELLER_API_URI)
                .prettyPeek();
        System.out.println("\n=====================================================================");

        //HEAD
        RestAssured
                .given()
                .param(PARAM_TEXT, WRONG_WORD_EN)
                .log().everything()
                .head(YANDEX_SPELLER_API_URI)
                .prettyPeek();
        System.out.println("\n=====================================================================");

        //OPTIONS
        RestAssured
                .given()
                .param(PARAM_TEXT, WRONG_WORD_EN)
                .log().everything()
                .options(YANDEX_SPELLER_API_URI)
                .prettyPeek();
        System.out.println("\n=====================================================================");

        //PUT
        RestAssured
                .given()
                .param(PARAM_TEXT, WRONG_WORD_EN)
                .log().everything()
                .put(YANDEX_SPELLER_API_URI)
                .prettyPeek();
        System.out.println("\n=====================================================================");

        //PATCH
        RestAssured
                .given()
                .param(PARAM_TEXT, WRONG_WORD_EN)
                .log()
                .everything()
                .patch(YANDEX_SPELLER_API_URI)
                .prettyPeek();
        System.out.println("\n=====================================================================");

        //DELETE
        RestAssured
                .given()
                .param(PARAM_TEXT, WRONG_WORD_EN)
                .log()
                .everything()
                .delete(YANDEX_SPELLER_API_URI).prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .statusLine("HTTP/1.1 405 Method not allowed");
    }


    // use base request and response specifications to form request and validate response.
    // 1. checkText with no mistakes
    @Test(description = "Api returns empty body if there are no mistakes")
    public void wordWithNoMistakes() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_TEXT, WORD_WITH_NO_MISTAKES)
                .get().prettyPeek()
                .then()
                .body(Matchers.equalTo("[]"))
                .specification(YandexSpellerApi.successResponse());
    }

    // 2. checkText with no space between words
    @Test(description = "Api returns right words if there are no spaces between words")
    public void wordWithNoSpaces(){
        YandexSpellerApi.with()
                .language(YandexSpellerConstants.Languages.RU)
                .text(NO_SPACES_WORDS.word)
                .callApi()
                .then()
                .assertThat()
                .body(Matchers.equalTo(NO_SPACES_WORDS.rightWord))
                .specification(YandexSpellerApi.successResponse());
    }

    //validate an object we've got in API response
    // 3. checkText with accidental space
    @Test(description = "accidental space in words")
    public void wordWithAccidentialSpace() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .language(YandexSpellerConstants.Languages.RU)
                                .text(ACCIDENTAL_SPACE.word)
                                .callApi());
        assertThat("expected number of answers is right.", answers.size(), equalTo(2));
        for(String rightWord : ACCIDENTAL_SPACE.rightWord) {
            assertThat(answers.get(ACCIDENTAL_SPACE.rightWord.indexOf(rightWord)).s.get(0), equalTo(rightWord));
        }
    }

    // 4. checkText with alternating lowercase and uppercase in one word
    // BUG: Speller can't change lower- and uppercase
    @Test(description = "alternating lowercase and uppercase")
    public void alternatingWordCase(){
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi.with()
                                .language(YandexSpellerConstants.Languages.RU)
                                .text(LOWERCASE_AND_UPPERCASE.word)
                                .callApi());
        assertThat("expected number of answers is wrong.", answers.size(), equalTo(0));
    }

    // 5. checkText with digits attached to words and option equal '2'
    @Test(description = "Ignore words with attached digits when option equal '2'")
    public void ignoreWordsDigits(){
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi
                                .with()
                                .language(YandexSpellerConstants.Languages.RU)
                                .text(WORD_WITH_DIGITS.word)
                                .options("2")
                                .callApi());
        assertThat("expected number of answers is right.", answers.size(), equalTo(0));
    }

    // 6. checkText with names
    // BUG: Speller can't replace first letter of name
    @Test(description = "Api returns right uppercase first letter")
    public void wordsNames() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi
                                .with()
                                .language(EN)
                                .text(WORD_NAME.word)
                                .callApi());
        assertThat("expected number of answers is right.", answers.size(), equalTo(1));
        for(String rightWord : WORD_NAME.rightWord) {
            assertThat(answers.get(WORD_NAME.rightWord.indexOf(rightWord)).s.get(0), equalTo(rightWord));
        }
    }

    // 7. chackText with missed letter
    @Test(description = "Api returns right word")
    public void wordMissedLetter() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerApi.getYandexSpellerAnswers(
                        YandexSpellerApi
                                .with()
                                .language(YandexSpellerConstants.Languages.RU)
                                .text(MISSED_LETTER.word)
                                .callApi());
        assertThat("expected number of answers is right.", answers.size(), equalTo(1));
        assertThat(answers.get(0).s.get(0), equalTo(MISSED_LETTER.rightWord.get(0)));
    }

    // 8. checkText with superfluous letter
    @Test(description = "superfluous letter in word")
    public void superfluousLetterWord() {
        YandexSpellerApi.with()
                .language(YandexSpellerConstants.Languages.RU)
                .text(SUPERFLUOUS_LETTER.word)
                .callApi()
                .then()
                .assertThat()
                .body(Matchers.equalTo(SUPERFLUOUS_LETTER.rightWord))
                .specification(YandexSpellerApi.successResponse());
    }

    // 9. checkText ignore wrong url
    // BUG: Speller should passed url with mistake then option equals '4'
    @Test(description = "Ignore URL with mistake. Option 4")
    public void urlWithMistakes() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_OPTIONS, "4")
                .param(PARAM_LANG, EN)
                .param(PARAM_TEXT, WRONG_URL.word)
                .get().prettyPeek()
                .then()
                .body(Matchers.equalTo("[]"))
                .specification(YandexSpellerApi.successResponse());
    }

    // 10. checkText API should delete repeated word(s)
    // BUG: Speller can't delete repeated words
    @Test(description = "repeated words")
    public void deleteRepeatWords() {
        List<YandexSpellerAnswer> answers = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with()
                        .text(REPEAT_WORDS.word)
                        .language(EN)
                        .options("8")
                        .callApi());
        assertThat("expected number of answers is right.", answers.size(), equalTo(1));
        assertThat(answers.get(0).s.get(0), equalTo(REPEAT_WORDS.rightWord.get(0)));
    }
}
