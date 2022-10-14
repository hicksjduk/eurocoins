package uk.org.thehickses.coins.uk;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class CoinListParser2Pound
{
    public static void main(String[] args)
    {
        new CoinListParser2Pound().parse()
                .sorted()
                .forEach(System.out::println);
    }

    private final URI basePage = URI.create(
            "https://www.royalmint.com/discover/uk-coins/coin-design-and-specifications/two-pound-coin/");

    public Stream<CoinData> parse()
    {
        try
        {
            return Jsoup.connect(basePage.toString())
                    .get()
                    .select("tr:has(img[src*=two-pound-coin])")
                    .stream()
                    .skip(2)
                    .map(parser());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    Function<Element, CoinData> parser()
    {
        AtomicInteger sequence = new AtomicInteger();
        return row ->
            {
                var year = row.selectFirst("td:matches(\\d{4}.*)")
                        .text()
                        .substring(0, 4);
                var descrElem = row.child(3);
                var description = extractDescription(descrElem);
                var imageElem = row.selectFirst("img");
                return new CoinData(2000, Integer.parseInt(year), sequence.getAndIncrement(),
                        description, basePage.resolve(imageElem.attr("src")));
            };
    }

    String extractDescription(Element elem)
    {
        var firstParagraph = elem.selectFirst("p");
        var html = firstParagraph == null ? elem.html() : firstParagraph.html();
        var pos = html.indexOf("<");
        if (pos == -1)
            return html.trim();
        return html.substring(0, pos).trim();
    }
}
