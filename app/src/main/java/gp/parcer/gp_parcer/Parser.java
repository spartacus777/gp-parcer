package gp.parcer.gp_parcer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

public class Parser {

    private ParcerCallback callback;

    private Random random = new Random();

    private String startingUrl;

    private boolean isStopped = false;

    public interface ParcerCallback {
        void onProgressChanged(long size);
    }

    public Parser(String startingUrl, ParcerCallback callback) {
        this.callback = callback;
        this.startingUrl = startingUrl;
    }

    private Document getDocument(String url) {
        Document doc = null;

        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .maxBodySize(1024 * 1024 * 1) // Size in Bytes - 10 MB
                    .referrer("http://www.google.com").get();
        } catch (IOException e) {
        }

        return doc;
    }

    public void stop(){
        isStopped = true;
    }

    public void parse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                parsePrivate(startingUrl);
            }
        }).start();
    }

    public boolean parsePrivate(String url) {
        if (isStopped){
            return true;
        }

        Document doc = getDocument(url);

        if (doc == null) {
            return false;
        }

        Elements elements = doc.select("div.b8cIId > a[href]");
        doc = null;
        if (elements == null || elements.size() == 0) {
            return false;
        }

        Element el = elements.get(random.nextInt(elements.size()));

        String href = el.attr("href");
        if (!href.startsWith("http")) {
            href = "https://play.google.com" + href;
        }

        elements = null;
        el = null;

        parceAppPage(href);

        long size = ModelHolder.getSize();
        callback.onProgressChanged(size);

        boolean has = parsePrivate(href);
        if (!has) {
            parsePrivate(url);
        }

        return true;
    }

    public void parceAppPage(String url) {

        Model model = new Model();

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


//    private Document getAppsDocument1(String url) {
//        Document doc = null;
//
//        try {
//            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36")
//                    .requestBody("start=0&num=15&numChildren=0&pagTok=-p6BnQMCCDs%3D%3AS%3AANO1ljJ3Ffw&clp=ggEVChNiaXRjb2luIGZhdWNldD1hcHBz%3AS%3AANO1ljIxiWA&pagtt=3&cctcss=square-cover&cllayout=NORMAL&ipf=1&xhr=1&token=LnRhO_MnV-12LiNDv28wwR9zKSM%3A1527805108422&hl=en")
//                    .post();
//
//        } catch (IOException e) {
//            Log.e(Constants.TAG, e.toString());
//        }
//
//        return doc;
//    }
//
//    private Document getAppsDocument(String url) {
//        Document doc = null;
//
//        try {
//            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36")
//                    .requestBody("start=0&num=0&numChildren=0&pagTok=-p6BnQMCCAo%3D%3AS%3AANO1ljKPHXk&clp=ggEVChNiaXRjb2luIGZhdWNldD1hcHBz%3AS%3AANO1ljIxiWA&pagtt=3&cctcss=square-cover&cllayout=NORMAL&ipf=1&xhr=1&token=Ax2Qjeh4NYdU9gTzqyg7trMkuxQ%3A1527808267170&hl=en")
//                    .post();
//
//        } catch (IOException e) {
//            Log.e(Constants.TAG, e.toString());
//        }
//
//        return doc;
//    }
//
//
//    public void parse() {
//        parseFistTen();
//
//        Document doc = getAppsDocument("https://play.google.com/store/apps/collection/search_results_cluster_apps?authuser=0");
//        parseResponse(doc);
//    }
//
//    private void parseFistTen(){
//        Document doc = getDocument("https://play.google.com/store/search?q=bitcoin+faucet=apps&hl=en");
//        parseResponse(doc);
//    }
//
//    private void parseResponse(Document doc){
//        Elements elements = doc.select("span.stars-container > a[href]");
//        for (Element el : elements) {
//            String href = "https://play.google.com" + el.attr("href");
//
//            Log.d(Constants.TAG, href);
//        }
//    }