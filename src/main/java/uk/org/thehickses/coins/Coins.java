package uk.org.thehickses.coins;

import java.util.stream.Collectors;

public class Coins
{

    public static void main(String[] args)
    {
        var byCountry = new CoinParser().parse()
                .sorted()
                .collect(Collectors.groupingBy(CoinData::country));
        byCountry.values()
                .stream()
                .forEach(new CoinPageOutputter()::output);
    }

}
