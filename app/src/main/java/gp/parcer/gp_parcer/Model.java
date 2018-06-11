package gp.parcer.gp_parcer;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "email", unique = true)
})
public class Model {

    @Id
    public String email;

    public String appName;

    public String publisher;

    public String category;

    public float rating;

@Generated(hash = 2100533769)
public Model(String email, String appName, String publisher, String category,
        float rating) {
    this.email = email;
    this.appName = appName;
    this.publisher = publisher;
    this.category = category;
    this.rating = rating;
}

@Generated(hash = 2118404446)
public Model() {
}

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Email : " + email +"  "+
                "AppName : " + appName +"  "+
                "Publisher : " + publisher +"  "+
                "rating : " + String.format("%.1f", rating));

        return stringBuilder.toString();
    }

public String getEmail() {
    return this.email;
}

public void setEmail(String email) {
    this.email = email;
}

public String getAppName() {
    return this.appName;
}

public void setAppName(String appName) {
    this.appName = appName;
}

public String getPublisher() {
    return this.publisher;
}

public void setPublisher(String publisher) {
    this.publisher = publisher;
}

public float getRating() {
    return this.rating;
}

public void setRating(float rating) {
    this.rating = rating;
}

public String getCategory() {
    return this.category;
}

public void setCategory(String category) {
    this.category = category;
}
}
