package uk.org.thehickses.coins.uk;

import java.net.URI;
import java.util.Comparator;

public record CoinData(int penceValue, int year, int sequence, String description, URI image)
        implements Comparable<CoinData>
{

    private final static Comparator<CoinData> COMP = Comparator.comparing(CoinData::year)
            .thenComparing(CoinData::sequence);

    @Override
    public int compareTo(CoinData o)
    {
        return COMP.compare(this, o);
    }

}
