package ch.heigvd.gen.models;

import java.util.Date;

/**
 * Model for a message
 */
public class Message {
    private int id;
    private int from;
    private String content;
    private Date date;

    /**
     * Message constructor
     *
     * @param from    the messages sender id
     * @param content the messages content
     * @param date    the message's date
     * @param id      the message's id
     */
    public Message(int from, String content, Date date, int id) {
        this.from = from;
        this.content = content;
        this.date = date;
        this.id = id;
    }

    /**
     * Get the sender id
     *
     * @return the sender id
     */
    public int getFrom() {
        return from;
    }

    /**
     * Set the sender id
     *
     * @param from the sender id
     */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * Get the message's content
     *
     * @return the text of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the message's content
     *
     * @param content the message's content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Get the message's date
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the message's date
     *
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get the message's id
     *
     * @return the message's id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the message's id
     *
     * @param id the message's id
     */
    public void setId(int id) {
        this.id = id;
    }
}
