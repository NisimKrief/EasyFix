package com.example.easyfix;

public class User {
    private String KeyId; //מספר מפתח השייך לארגון
    private String uId; // מספר מפתח של המשתמש
    private int LastYear; //שנת סיום למידה
    private int ClassNumber; // מספר כיתה / אזור עבודה
    private int UserLevel; // לדוגמה עבור תלמיד 10 מורה 20.....



    public User(String keyId, String uId, int lastYear, int classNumber) {
        KeyId = keyId;
        this.uId = uId;
        LastYear = lastYear;
        ClassNumber = classNumber;
        UserLevel = 0;
    }

    public User(String keyId, String uId, int lastYear, int classNumber, int userLevel) {
        KeyId = keyId;
        this.uId = uId;
        LastYear = lastYear;
        ClassNumber = classNumber;
        UserLevel = userLevel;
    }



    public User() {
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
