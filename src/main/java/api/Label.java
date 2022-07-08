package api;

public enum Label {

    User("user"),
    Created("created"),
    Created_At("created_at"),
    Language("language");

    public final String value;
    Label(String label){
        this.value = label;
    }
}
