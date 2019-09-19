package com.paascloud.provider.model;


public class SysUserVo {

    private Integer id;

    private String username;

    private String password;

    private String twitterid;

    private String facebookid;

    private String githubid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getTwitterid() {
        return twitterid;
    }

    public void setTwitterid(String twitterid) {
        this.twitterid = twitterid == null ? null : twitterid.trim();
    }

    public String getFacebookid() {
        return facebookid;
    }

    public void setFacebookid(String facebookid) {
        this.facebookid = facebookid == null ? null : facebookid.trim();
    }

    public String getGithubid() {
        return githubid;
    }

    public void setGithubid(String githubid) {
        this.githubid = githubid == null ? null : githubid.trim();
    }
}