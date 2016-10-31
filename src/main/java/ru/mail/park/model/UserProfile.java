package ru.mail.park.model;

public class UserProfile implements BaseDaoEntity {
    private String login;
    private String email;
    private String password;
    private int rank;

    public UserProfile(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    public UserProfile(String login, String email, String password, int rank) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.rank = rank;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
