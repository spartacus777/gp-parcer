package gp.parcer.gp_parcer;

public class Model {

    public String email;

    public String appName;

    public String publisher;

    public float rating;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Email : " + email +"  "+
                "AppName : " + appName +"  "+
                "Publisher : " + publisher +"  "+
                "rating : " + String.format("%.1f", rating));

        return stringBuilder.toString();
    }
}
