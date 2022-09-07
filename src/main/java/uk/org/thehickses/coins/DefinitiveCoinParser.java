package uk.org.thehickses.coins;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DefinitiveCoinParser
{
    public static void main(String[] args)
    {
        new DefinitiveCoinParser().parse()
                .forEach(System.out::println);
    }

    final static String baseUrl = "https://www.bundesbank.de";

    Stream<DefinitiveCoinData> parse()
    {
        try
        {
            var doc = Jsoup.connect(baseUrl
                    + "/en/tasks/cash-management/euro-coins/regular-coins/regular-coins-623842")
                    .get();
            return doc.select(".collection__link")
                    .stream()
                    .map(e -> e.attr("href"))
                    .filter(l -> l.startsWith("/en"))
                    .flatMap(this::parse);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    Stream<DefinitiveCoinData> parse(String countryPage)
    {
        try
        {
            var doc = Jsoup.connect(baseUrl + countryPage)
                    .get();
            return IntStream.of(200, 100, 50, 20, 10, 5, 2, 1)
                    .boxed()
                    .flatMap(i -> parse(i, doc));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    Stream<DefinitiveCoinData> parse(int value, Document doc)
    {
        return doc.select(".picture__img")
                .stream()
                .map(e -> match(e, value))
                .filter(Objects::nonNull);
    }

    private DefinitiveCoinData match(Element e, int value)
    {
        var val = value > 99 ? value / 100 : value;
        var unit = value > 99 ? "euro" : "cent";
        var descr = e.attr("alt");
        var regex = "National back side of the (.+)%s coin in circulation in (.+)"
                .formatted(unit);
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(descr);
        if (!matcher.matches())
            return null;
        var values = matcher.group(1);
        if (Stream.of(",", " ", "-").map(s -> val + s).noneMatch(values::contains))
            return null;
        var country = matcher.group(2);
        var series = 0;
        var matcher2 = Pattern.compile("(.+),\\s?(.+) series.*").matcher(country);
        if (matcher2.matches())
        {
            country = matcher2.group(1);
            var seriesStr = matcher2.group(2).replaceAll("\\.", "");
            if (seriesStr.equals("second"))
                series = 2;
            else
                series = Integer.parseInt(seriesStr);
        }
        if (country.startsWith("the "))
            country = country.substring(4);
        var answer = new DefinitiveCoinData(country, series, value, e.attr("src"));
        return answer;
    }
}
