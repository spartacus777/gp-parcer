package gp.parcer.gp_parcer.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class FirstTenParser extends BaseParser {

    @Override
    public void parse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO
//                parse(searchWord);
            }
        }).start();
    }

    @Override
    public void stop() {

    }

    public void parse(List<String> searchWord) {
        for (String search : searchWord){
            parseSearchWord(search);
        }
    }

    public void parseSearchWord(String searchWord) {
        Document doc = getDocument("https://play.google.com/store/search?q=" + searchWord + "&c=appss&hl=en");
        parseResponse(doc, searchWord);
    }

    private void parseResponse(Document doc, String searchWord){
        Elements elements = doc.select("span.stars-container > a[href]");
        for (Element el : elements) {
            String href = "https://play.google.com" + el.attr("href");

//            Log.d(Constants.TAG, href);

            parceAppPage(href, searchWord);
        }
    }

}
