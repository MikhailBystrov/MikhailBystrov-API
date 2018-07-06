package data;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mikhail on 06.07.2018
 */
@AllArgsConstructor
public class CheckTextData {
    public String word;
    public List<String> rightWord;

    public static final CheckTextData NO_SPACES_WORDS = new CheckTextData( "невероятныеприключения",
            Collections.singletonList("невероятные приключения"));
    public static final CheckTextData ACCIDENTAL_SPACE = new CheckTextData( "электри ческая ду га",
            Arrays.asList("электрическая", "дуга"));
    public static final CheckTextData LOWERCASE_AND_UPPERCASE = new CheckTextData(
            "нАпИсАнИе тЕкСтА зАбОрОм", Arrays.asList("написание", "текста", "забором"));
    public static final CheckTextData WORD_WITH_DIGITS = new CheckTextData( "33желания",
            Collections.singletonList("33 желания"));
    public static final CheckTextData WORD_NAME = new CheckTextData( "alex russia kate",
            Arrays.asList("alex", "Russia", "Kate"));
    public static final CheckTextData MISSED_LETTER = new CheckTextData( "корбль",
            Collections.singletonList("корабль"));
    public static final CheckTextData SUPERFLUOUS_LETTER = new CheckTextData( "счаастье",
            Collections.singletonList("счасть"));
    public static final CheckTextData WRONG_URL = new CheckTextData( "htps://tech.yandex.ru/speller/",
            Collections.singletonList("https"));
    public static final CheckTextData REPEAT_WORDS = new CheckTextData( "I want want holiday",
            Collections.singletonList("I want holiday"));

}
