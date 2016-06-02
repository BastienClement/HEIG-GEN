package ch.heigvd.gen.models;

import java.util.Date;

/**
 * TODO
 */
public class Message {
    private int id;
    private int from;
    private String content;
    private Date date;

    /**
     * TODO
     *
     * @param from
     * @param content
     * @param date
     * @param id
     */
    public Message(int from, String content, Date date, int id) {
        this.from = from;
        this.content = content;
        this.date = date;
        this.id = id;
    }

    /**
     * TODO
     *
     * @return
     */
    public int getFrom() {
        return from;
    }

    /**
     * TODO
     *
     * @param from
     */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * TODO
     *
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * TODO
     *
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * TODO
     *
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     * TODO
     *
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * TODO
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * TODO
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
}
