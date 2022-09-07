package uk.org.thehickses.coins;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class DefinitiveCoinParser
{
    public static void main(String[] args)
    {
        new DefinitiveCoinParser().parse()
                .forEach(System.out::println);
    }

    static final String baseUri = "https://www.ecb.europa.eu";

    public Stream<DefinitiveCoinData> parse()
    {
        try
        {
            var doc = Jsoup.connect(baseUri + "/euro/coins/1euro/html/index.en.html")
                    .get();
            return doc.select("a[href^=/euro/coins/html/]")
                    .stream()
                    .map(e -> e.attr("href"))
                    .flatMap(this::parse);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    Stream<DefinitiveCoinData> parse(String nationalPage)
    {
        try
        {
            var doc = Jsoup.connect(baseUri + nationalPage)
                    .get();
            var country = doc.selectFirst("h1")
                    .text()
                    .replaceAll("\\(.*", "")
                    .trim();
            return doc.select(".box")
                    .stream()
                    .flatMap(e -> parse(e, country));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    Stream<DefinitiveCoinData> parse(Element elem, String country)
    {
        var valueStr = elem.selectFirst("h3")
                .text();
        var factor = valueStr.contains("cent") ? 1 : 100;
        var value = factor * Integer.parseInt(valueStr.replaceAll("\\D+", ""));
        AtomicInteger series = new AtomicInteger();
        return elem.select("img[src^=/euro/coins/common/shared/img]")
                .stream()
                .map(e -> e.attr("src"))
                .map(u -> new DefinitiveCoinData(country, series.getAndIncrement(), value,
                        baseUri + u));
    }
}
