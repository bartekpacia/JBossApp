package baftek.pl.jbossapp.internal;

//TODO Implement Parcelable
public class RepoData
{
    private String name;
    private String url;
    private int stars;
    private int forks;
    private String language;
    private String description;
    private String contributorsUrl;
    private String license;

    public RepoData(String name, String url, int stars, int forks, String language, String description, String contributorsUrl, String license)
    {
        this.name = name;
        this.url = url;
        this.stars = stars;
        this.forks = forks;
        this.language = language;
        this.description = description;
        this.contributorsUrl = contributorsUrl;
        this.license = license;

        //avoid nulls
        if (this.language.equals("null"))
        {
            this.language = "";
        }

        if (this.description.equals("null"))
        {
            this.description = "";
        }
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

    public int getForks()
    {
        return forks;
    }

    public String getLanguage()
    {
        return language;
    }

    public String getDescription()
    {
        return description;
    }

    public String getContributorsUrl()
    {
        return contributorsUrl;
    }

    public String getLicense()
    {
        return license;
    }
}
