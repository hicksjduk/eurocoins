package uk.org.thehickses.coins;

import java.util.stream.Collectors;

public class Coins
{

    public static void main(String[] args)
    {
        var defsByCountry = new DefinitiveCoinParserECB().parse().sorted()
                .collect(Collectors.groupingBy(DefinitiveCoinData::country));
        var commemsByCountry = new CommemorativeCoinParser().parse()
                .sorted()
                .collect(Collectors.groupingBy(CommemorativeCoinData::country));
        var outputter = new CoinPageOutputter();
        defsByCountry.keySet().stream().forEach(c -> outputter.output(c, defsByCountry.get(c), commemsByCountry.get(c)));
    }

}
