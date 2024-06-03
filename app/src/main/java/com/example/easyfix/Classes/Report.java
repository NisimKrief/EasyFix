package com.example.easyfix.Classes;

/**
 * The Object Report
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	08/02/2024
 * This class represents a report with various details such as reporter, type, urgency level, malfunction area, and more.
 * This class is used to encapsulate the details of a report in the EasyFix application.
 */
public class Report {
    private String reporter; // The User who reported
    private String ReportMainType; // Report Title
    private String UrgencyLevel; // UrgencyLevel decided by technician
    private Integer MalfunctionArea; // The Area of the problem
    private Integer MalfunctionRoom; // The Room which the problem is in
    private String TimeReported; // Time Reported
    private String ExtraInformation; // Description
    private String ReportPhoto; // Photo
    private String MalfunctionFixer; // Technician that Fixed the problem
    private String TimeFixed; // Fixed Time
    private String FixedPhoto; // Photo of the Fixed Problem

    /**
     * Instantiates a new Report.
     */
    public Report() {

    }


    /**
     * Instantiates a new Report.
     * Full Builder, if the user don't add extraInformation or photo it will be "null".
     * @param reporter         the reporter
     * @param reportMainType   the report main type
     * @param malfunctionArea  the malfunction area
     * @param malfunctionRoom  the malfunction room
     * @param timeReported     the time reported
     * @param extraInformation the extra information
     * @param reportPhoto      the report photo
     */
    public Report(String reporter, String reportMainType, Integer malfunctionArea, Integer malfunctionRoom, String timeReported, String extraInformation, String reportPhoto) {
        // Full Builder, if the user don't add extraInformation or photo it will be "null".
        this.reporter = reporter;
        ReportMainType = reportMainType;
        MalfunctionArea = malfunctionArea;
        MalfunctionRoom = malfunctionRoom;
        TimeReported = timeReported;
        ExtraInformation = extraInformation;
        ReportPhoto = reportPhoto;

        UrgencyLevel = "Null"; //  Those values will get updated by the construction worker.
        MalfunctionFixer = "Null";
        TimeFixed = "Null";
        FixedPhoto = "Null";
    }

    /**
     * Gets the reporter of the report.
     *
     * @return the reporter
     */
    public String getReporter() {
        return reporter;
    }

    /**
     * Sets the reporter of the report.
     *
     * @param reporter the reporter
     */
    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    /**
     * Gets the title of the report.
     *
     * @return the report main type
     */
    public String getReportMainType() {
        return ReportMainType;
    }

    /**
     * Sets the title of the report.
     *
     * @param reportMainType the report main type
     */
    public void setReportMainType(String reportMainType) {
        ReportMainType = reportMainType;
    }

    /**
     * Gets the urgency level of the report.
     *
     * @return the urgency level
     */
    public String getUrgencyLevel() {
        return UrgencyLevel;
    }

    /**
     * Sets the urgency level of the report.
     *
     * @param urgencyLevel the urgency level
     */
    public void setUrgencyLevel(String urgencyLevel) {
        UrgencyLevel = urgencyLevel;
    }

    /**
     * Gets the malfunction area of the report.
     *
     * @return the malfunction area
     */
    public Integer getMalfunctionArea() {
        return MalfunctionArea;
    }

    /**
     * Sets the malfunction area of the report.
     *
     * @param malfunctionArea the malfunction area
     */
    public void setMalfunctionArea(Integer malfunctionArea) {
        MalfunctionArea = malfunctionArea;
    }

    /**
     * Gets the time the report was made.
     *
     * @return the time reported
     */
    public String getTimeReported() {
        return TimeReported;
    }

    /**
     * Sets the time the report was made.
     *
     * @param timeReported the time reported
     */
    public void setTimeReported(String timeReported) {
        TimeReported = timeReported;
    }

    /**
     * Gets the extra information of the report.
     *
     * @return the extra information
     */
    public String getExtraInformation() {
        return ExtraInformation;
    }

    /**
     * Sets the extra information of the report.
     *
     * @param extraInformation the extra information
     */
    public void setExtraInformation(String extraInformation) {
        ExtraInformation = extraInformation;
    }

    /**
     * Gets the photo of the report.
     *
     * @return the report photo
     */
    public String getReportPhoto() {
        return ReportPhoto;
    }

    /**
     * Sets the photo of the report.
     *
     * @param reportPhoto the report photo
     */
    public void setReportPhoto(String reportPhoto) {
        ReportPhoto = reportPhoto;
    }

    /**
     * Gets the technicians name who fixed the problem.
     *
     * @return the malfunction fixer
     */
    public String getMalfunctionFixer() {
        return MalfunctionFixer;
    }

    /**
     * Sets the technicians name who fixed the problem.
     *
     * @param malfunctionFixer the malfunction fixer
     */
    public void setMalfunctionFixer(String malfunctionFixer) {
        MalfunctionFixer = malfunctionFixer;
    }

    /**
     * Gets the time the problem was fixed.
     *
     * @return the time fixed
     */
    public String getTimeFixed() {
        return TimeFixed;
    }

    /**
     * Sets the time the problem was fixed.
     *
     * @param timeFixed the time fixed
     */
    public void setTimeFixed(String timeFixed) {
        TimeFixed = timeFixed;
    }

    /**
     * Gets the photo of the fixed problem.
     *
     * @return the fixed photo
     */
    public String getFixedPhoto() {
        return FixedPhoto;
    }

    /**
     * Sets the photo of the fixed problem.
     *
     * @param fixedPhoto the fixed photo
     */
    public void setFixedPhoto(String fixedPhoto) {
        FixedPhoto = fixedPhoto;
    }

    /**
     * Gets the room where the malfunction is.
     *
     * @return the malfunction room
     */
    public Integer getMalfunctionRoom() {
        return MalfunctionRoom;
    }

    /**
     * Sets the room where the malfunction is.
     *
     * @param malfunctionRoom the malfunction room
     */
    public void setMalfunctionRoom(Integer malfunctionRoom) {
        MalfunctionRoom = malfunctionRoom;
    }
}

