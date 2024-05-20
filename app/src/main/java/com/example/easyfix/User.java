package com.example.easyfix;
public class User {
    private String KeyId; //מספר מפתח השייך לארגון
    private String uId; // מספר מפתח של המשתמש
    private String userName;
    private int LastYear; //שנת סיום למידה
    private int ClassNumber; // מספר כיתה / אזור עבודה
    private int UserLevel; // לדוגמה עבור תלמיד 10 מורה 20.....



    public User(String keyId, String uId, String userName, int lastYear, int classNumber) {
        KeyId = keyId;
        this.uId = uId;
        this.userName = userName;
        LastYear = lastYear;
        ClassNumber = classNumber;
        UserLevel = 0;
    }

    public User(String keyId, String uId, String userName, int lastYear, int classNumber, int userLevel) {
        KeyId = keyId;
        this.uId = uId;
        this.userName = userName;
        LastYear = lastYear;
        ClassNumber = classNumber;
        UserLevel = userLevel;
    }
    public User(User user1){
        String KeyId = user1.getKeyId();
        String uId = user1.getuId();
        int LastYear = user1.getLastYear();
        int ClassNumber = user1.getClassNumber();
        int UserLevel = user1.getUserLevel();
    }



    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getKeyId() {
        return KeyId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setKeyId(String keyID) {
        KeyId = keyID;
    }

    public int getLastYear() {
        return LastYear;
    }

    public void setLastYear(int lastYear) {
        LastYear = lastYear;
    }

    public int getClassNumber() {
        return ClassNumber;
    }

    public void setClassNumber(int classNumber) {
        ClassNumber = classNumber;
    }

    public int getUserLevel() {
        return UserLevel;
    }

    public void setUserLevel(int userLevel) {
        UserLevel = userLevel;
    }
}
/*
לאיזה ארגון הוא שייך (KeyId) - מסוג String
בהתחברות יופיע ArrayList עבור הארגון המתאים, לדוגמה בית ספר יופיע כיתות ולעובד עירייה יופיעו דרגות בהתאם.
שנת סיום יב', על מנת לגלות את כיתתו, (למורה כללי יהיה 0) - מסוג Int
מספר הכיתה (במידה ו0 - רכז שכבה) - מסוג Integer
הדרגה שלו - מסוג Integer

 */
