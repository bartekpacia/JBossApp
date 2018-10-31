package baftek.pl.jbossapp;

public class RepoData
{
    private String name;
    private String url;
    private int stars;

    public RepoData(String name, String url, int stars)
    {
        this.name = name;
        this.url = url;
        this.stars = stars;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }

    public int getStars()
    {
        return stars;
    }
}
