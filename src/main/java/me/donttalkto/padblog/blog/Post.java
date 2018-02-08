package me.donttalkto.padblog.blog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Post implements Comparable<Post> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("E, MMM d, yyyy HH:mm a");

    private int id;
    private String title, text;
    private LocalDateTime timestamp;
    private User user;

    public Post(int id, String title, String text, User user, LocalDateTime timestamp) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.user = user;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return user.getName();
    }

    public String getDate() {
        return FORMATTER.format(timestamp);
    }

    public int getID() {
        return id;
    }

    @Override
    public int compareTo(Post post) {
        return timestamp.compareTo(post.timestamp);
    }
}