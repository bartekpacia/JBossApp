package baftek.pl.jbossapp;

public class RepoData
{
    private String name;
    private int stars;

    public RepoData(String name)
    {
        this.name = name;
        //this.stars = stars;
    }

    public String getName()
    {
        return name;
    }

    public int getStars()
    {
        return stars;
    }
}
