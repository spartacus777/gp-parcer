package gp.parcer.gp_parcer.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import gp.parcer.gp_parcer.Model;
import gp.parcer.gp_parcer.ModelHolder;

public abstract class BaseParser {

    public abstract void parse();

    public abstract void stop();

    protected Document getDocument(String url) {
        Document doc = null;

        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .maxBodySize(1024 * 1024 * 1) // Size in Bytes - 10 MB
                    .referrer("http://www.google.com").get();
        } catch (IOException e) {
        }

        return doc;
    }

    protected void parceAppPage(String url, String searchWord) {

        Model model = new Model();
        if (searchWord != null){
            model.category = searchWord;
        }

        Document doc = getDocument(url);
        if (doc == null) {
            return;
        }

        /**
         * Parsing Email
         */
        Elements elements = doc.select("a[href].hrTbp");
        if (elements != null) {
            for (Element el : elements) {
                String href = el.attr("href");

                if (href.startsWith("mailto:")) {
                    model.email = el.text();
                }
            }
        }

        if (model.email == null) {
            return;
        }

        if (ModelHolder.contains(model.email)) {
            return;
        }

        /**
         * Parsing Rating
         */
        Elements metaTags = doc.getElementsByTag("meta");
        if (metaTags != null) {
            for (Element metaTag : metaTags) {
                String content = metaTag.attr("content");
                String itemprop = metaTag.attr("itemprop");

                if ("ratingValue".equals(itemprop)) {
                    try {
                        model.rating = Float.parseFloat(content);
                    } catch (Throwable t) {
                    }
                }
            }
        }

        /**
         * Parsing App Name
         */
        Element elementAppName = doc.select("h1.AHFaub").first();
        if (elementAppName != null) {
            model.appName = elementAppName.text();
        }

        /**
         * Parsing Publisher
         */
        Element element = doc.select("span.T32cc > a[href].hrTbp").first();
        if (element != null) {
            model.publisher = element.text();
        }

        if (model.email != null) {
            ModelHolder.add(model);
        }

    }

}
