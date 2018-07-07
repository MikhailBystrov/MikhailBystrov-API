package core;

/**
 * Created by Mikhail on 05.07.2018
 */
public class YandexSpellerConstants {

    //useful constants for API under test
    public static final String YANDEX_SPELLER_API_URI = "https://speller.yandex.net/services/spellservice.json/checkText";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_OPTIONS = "options";
    public static final String PARAM_LANG = "lang";
    public static final String WORD_WITH_NO_MISTAKES = "perfect";
    public static final String NO_MISTAKES_RESPONSE = "\\W+";
    public static final String ASSERT_REASON = "expected number of answers is right.";
}
