package com.example.easyfix.Classes;

/**
 * The Object User
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	08/02/2024
 * Represents a User in the EasyFix application.
 * A User can be associated with an organization, have a unique ID, name, last year of school, and user level.
 * This class encapsulates the details of a user.
 */
public class User {
    private String KeyId; // Organization keyId
    private String uId; // users uId
    private String userName;
    private int LastYear; // last year for school
    private int UserLevel; // for example 10 for teacher 100 for worker....


    /**
     * Instantiates a new User with the specified details and sets the user level to 0 (Whenn he register).
     *
     * @param keyId    the organization key ID
     * @param uId      the user's unique ID
     * @param userName the user's name
     * @param lastYear the last year of school
     */
    public User(String keyId, String uId, String userName, int lastYear) {
        KeyId = keyId;
        this.uId = uId;
        this.userName = userName;
        LastYear = lastYear;
        UserLevel = 0;
    }

    /**
     * Instantiates a new User with the specified details.
     * for special users, barely used.
     * @param keyId     the organization key ID
     * @param uId       the user's unique ID
     * @param userName  the user's name
     * @param lastYear  the last year of school
     * @param userLevel the user level
     */
    public User(String keyId, String uId, String userName, int lastYear, int userLevel) {
        KeyId = keyId;
        this.uId = uId;
        this.userName = userName;
        LastYear = lastYear;
        UserLevel = userLevel;
    }

    /**
     * Instantiates a new User.
     */
    public User() {
    }

    /**
     * Gets the user's name.
     *
     * @return the user's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user's name.
     *
     * @param userName the user's name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the organization key ID.
     *
     * @return the organization key ID
     */
    public String getKeyId() {
        return KeyId;
    }

    /**
     * Gets the user's unique ID.
     *
     * @return the user's unique ID
     */
    public String getuId() {
        return uId;
    }

    /**
     * Sets the user's unique ID.
     *
     * @param uId the user's unique ID
     */
    public void setuId(String uId) {
        this.uId = uId;
    }

    /**
     * Sets the organization key ID.
     *
     * @param keyID the organization key ID
     */
    public void setKeyId(String keyID) {
        KeyId = keyID;
    }

    /**
     * Gets the last year of school.
     *
     * @return the last year of school
     */
    public int getLastYear() {
        return LastYear;
    }

    /**
     * Sets the last year of school.
     *
     * @param lastYear the last year of school
     */
    public void setLastYear(int lastYear) {
        LastYear = lastYear;
    }

    /**
     * Gets the user level.
     *
     * @return the user level
     */
    public int getUserLevel() {
        return UserLevel;
    }

    /**
     * Sets the user level.
     *
     * @param userLevel the user level
     */
    public void setUserLevel(int userLevel) {
        UserLevel = userLevel;
    }
}
