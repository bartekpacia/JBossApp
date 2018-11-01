package baftek.pl.jbossapp.internal;

public class RepoData
{
    private String name;
    private String url;
    private int stars;
    private String description;

    public RepoData(String name, String url, int stars, String description)
    {
        this.name = name;
        this.url = url;
        this.stars = stars;
        this.description = description;
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

    public String getDescription()
    {
        return description;
    }
}
