package api;

public enum GitQualifiers {
    CreationDateGreaterThan(Label.Created, ":>"),
    CreationDateLessThan(Label.Created, ":<"),
    CreationDateEqualTo(Label.Created, ":"),
    User(Label.User, ":"),
    Language(Label.Language, ":");

    final String value;


    GitQualifiers(Label label, String suffix) {
        this.value = label.value + suffix;
    }


}
