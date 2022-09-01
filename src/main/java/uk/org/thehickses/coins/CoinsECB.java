package uk.org.thehickses.coins;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class CoinsECB
{
    public static void main(String[] args) throws Exception
    {
        IntStream.rangeClosed(2004, 2022)
                .boxed()
                .flatMap(new CoinsECB()::parse)
                .sorted(comp)
                .forEach(System.out::println);
    }

    static final String baseUri = "https://www.ecb.europa.eu";
    static final String yearPage = "/euro/coins/comm/html/comm_%d.en.html";
    static final Comparator<CoinData> comp = Comparator.comparing(CoinData::country)
            .thenComparing(CoinData::year)
            .thenComparing(CoinData::seq, Comparator.reverseOrder());

    static UnaryOperator<String> norm()
    {
        var mappings = Map.of("The Netherlands", "Netherlands");
        return c -> mappings.getOrDefault(c, c);
    }

    static final UnaryOperator<String> normaliser = norm();

    Stream<CoinData> parse(int year)
    {
        try
        {
            var url = baseUri + yearPage.formatted(year);
            var str = new URL(url).openStream();
            var doc = Jsoup.parse(str, "UTF-8", url);
            return doc.select(".box")
                    .stream()
                    .map(parser(year));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    Function<Element, CoinData> parser(int year)
    {
        AtomicInteger seq = new AtomicInteger();
        return elem ->
            {
                var country = normaliser.apply(elem.selectFirst("h3")
                        .text());
                var description = elem.selectFirst("strong:contains(Feature:)")
                        .parent()
                        .ownText();
                var imageUri = elem.selectFirst("img")
                        .attr("src");
                return new CoinData(country, year, seq.getAndIncrement(), description, imageUri);
            };
    }

}
