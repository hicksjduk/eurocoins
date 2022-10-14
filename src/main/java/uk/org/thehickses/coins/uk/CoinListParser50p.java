package uk.org.thehickses.coins.uk;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class CoinListParser50p
{
    public static void main(String[] args)
    {
        new CoinListParser50p().parse()
                .sorted()
                .forEach(System.out::println);
    }

    private final URI basePage = URI.create(
            "https://www.royalmint.com/discover/uk-coins/coin-design-and-specifications/fifty-pence-coin/");

    public Stream<CoinData> parse()
    {
        try
        {
            return Jsoup.connect(basePage.toString())
                    .get()
                    .select("tr:has(img[src*=fifty-pence-coin])")
                    .stream()
                    .skip(3)
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
                var descrElem = row.child(2);
                var description = extractDescription(descrElem);
                var imageElem = row.selectFirst("img");
                return new CoinData(50, Integer.parseInt(year), sequence.getAndIncrement(),
                        description, basePage.resolve(imageElem.attr("src")));
            };
    }

    String extractDescription(Element elem)
    {
        var firstParagraph = elem.selectFirst("p");
        if (firstParagraph != null)
            return firstParagraph.text();
        var child = elem.childNode(0);
        return child.toString();
        // return elem.selectFirst(":matches(.+)").text();
    }
}
