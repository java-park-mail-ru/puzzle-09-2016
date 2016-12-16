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

    @SuppressWarnings("all")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UserProfile userProfile = (UserProfile) o;
        if (rank != userProfile.rank) return false;
        if (!login.equals(userProfile.login)) return false;
        if (!email.equals(userProfile.email)) return false;
        return password.equals(userProfile.password);
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + rank;
        return result;
    }
}
