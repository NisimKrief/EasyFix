package com.example.easyfix;

public class User {
    String KeyId; //מספר מפתח השייך לארגון
    int LastYear; //שנת סיום למידה
    int ClassNumber; // מספר כיתה / אזור עבודה
    int UserLevel; // לדוגמה עבור תלמיד 10 מורה 20.....

    public User(String keyID, int lastYear, int classNumber, int userLevel) {
        KeyId = keyID;
        LastYear = lastYear;
        ClassNumber = classNumber;
        UserLevel = userLevel;
    }

    public String getKeyID() {
        return KeyId;
    }

    public void setKeyID(String keyID) {
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
