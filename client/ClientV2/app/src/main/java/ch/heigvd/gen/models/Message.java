package ch.heigvd.gen.models;

import java.util.Date;

/**
 * Created by guillaume on 27.05.16.
 */
public class Message {
    private int id;
    private int from;
    private String content;
    private Date date;

    public Message(int from, String content, Date date, int id) {
        this.from = from;
        this.content = content;
        this.date = date;
        this.id = id;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
