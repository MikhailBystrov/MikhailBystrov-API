package enums;

/**
 * Created by Mikhail on 07.07.2018
 */
public enum Languages {
    RU("ru"),
    UK("uk"),
    EN("en");

    public String languageCode;

    Languages(String languageCode) {
        this.languageCode = languageCode;
    }
}
