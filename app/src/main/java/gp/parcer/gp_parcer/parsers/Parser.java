package gp.parcer.gp_parcer.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Random;

import gp.parcer.gp_parcer.Generator;
import gp.parcer.gp_parcer.ModelHolder;

public class Parser extends BaseParser {

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

    @Override
    public void stop() {
        isStopped = true;
    }

    @Override
    public void parse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                parsePrivate(startingUrl);
            }
        }).start();
    }

    public boolean parsePrivate(String url) {
        if (isStopped) {
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

        parceAppPage(href, null);

        long size = ModelHolder.getSize();
        callback.onProgressChanged(size);

        //add random
//        if (Generator.doRandom()){
//            String rand = Generator.generate();
//            if (parseSearchWord(rand)){
//                return true;
//            }
//        }

        boolean has = parsePrivate(href);
        if (!has) {
            parsePrivate(url);
        }

        return true;
    }

    public boolean parseSearchWord(String searchWord) {
        Document doc = getDocument("https://play.google.com/store/search?q=" + searchWord + "&c=appss&hl=en");
        return parseResponse(doc, searchWord);
    }

    private boolean parseResponse(Document doc, String searchWord){

        if (doc == null){
            return false;
        }
        Elements elements = doc.select("span.stars-container > a[href]");
        for (Element el : elements) {
            String href = "https://play.google.com" + el.attr("href");

            boolean has = parsePrivate(href);
            if (!has) {
                parsePrivate(href);
            }

            return true;
//            parceAppPage(href, searchWord);
        }

        return false;
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