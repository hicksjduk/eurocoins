package uk.org.thehickses.coins;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class CommemorativeCoinParserCoinDB
{
    public static void main(String[] args) throws Exception
    {
        new CommemorativeCoinParserCoinDB().parse()
                .filter(c -> c.country()
                        .equals("Greece"))
                .forEach(System.out::println);
    }

    static final String baseUri = "https://www.coin-database.com";
    static final String dataPage = "/series/eurozone-commemorative-2-euro-coins-2-euro.html?";

    public Stream<CommemorativeCoinData> parse()
    {
        var url = baseUri + dataPage;
        try
        {
            var doc = Jsoup.connect(url)
                    .get();
            return doc.select("img")
                    .stream()
                    .map(parser())
                    .filter(Objects::nonNull)
                    .filter(c -> !c.country().equals("Eurozone"));
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    Function<Element, CommemorativeCoinData> parser()
    {
        Pattern titlePattern = Pattern.compile("2 euro coin (.+)\\| (.+) (\\d+)", Pattern.DOTALL);
        AtomicInteger seq = new AtomicInteger();
        return e ->
            {
                var title = e.attr("title");
                var matcher = titlePattern.matcher(title);
                if (!matcher.matches())
                    return null;
                var imageUri = baseUri + e.attr("src");
                return new CommemorativeCoinData(matcher.group(2),
                        Integer.parseInt(matcher.group(3)), seq.getAndIncrement(), matcher.group(1)
                                .trim(),
                        imageUri);
            };
    }

}
