package me.donttalkto.padblog.storage;

import me.donttalkto.padblog.blog.Post;
import me.donttalkto.padblog.blog.User;
import me.donttalkto.padblog.util.PasswordAuthentication;
import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBManager {

    private Connection conn;

    private static final DBManager INSTANCE = new DBManager();

    private DBManager() {
        String[] credentials = readCredentials();

        Properties connectionProps = new Properties();
        connectionProps.put("user", credentials[0]);
        connectionProps.put("password", credentials[1]);
        String url = "jdbc:mysql://54.37.228.40:3306/PADBlog";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, connectionProps);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String[] readCredentials() {
        String[] credentials = new String[2];
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/credentials.txt")))) {
            credentials[0] = reader.readLine();
            credentials[1] = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credentials;
    }

    public static DBManager get() {
        return INSTANCE;
    }

    public String login(Request req, Response res, String email, String password) {
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE email=? AND deactivated=false")){
            statement.setString(1, email);
            ResultSet set = statement.executeQuery();
            if (set == null || set.isClosed())
                return "/login";
            while (set.next()) {
                byte[] hashedPassword = set.getBytes("password");
                byte[] hashedSalt = set.getBytes("salt");
                boolean match = PasswordAuthentication.compare(password.toCharArray(), hashedSalt, hashedPassword);
                if (!match)
                    continue;
                req.session().attribute("currentUser", new User(set.getInt("user_id"), set.getString("name")));
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "/login";
    }

    public String makeNewAccount(String email, String name, String password, String repassword) {
        if (email.length() == 0 || name.length() == 0 || password.length() == 0) {
            return "emptyFields";
        }
        if (!repassword.equals(password)) {
            return "noPassMatch";
        }
        if (password.length() < 8) {
            return "shortPassword";
        }
        int size = 1;
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE email=?")){
            statement.setString(1, email);
            ResultSet set = statement.executeQuery();
            if (!set.isClosed() && set.next())
                size = 1;
            else
                size = 0;
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (size > 0) {
            return "emailExists";
        }

        // hash password with salt
        byte[][] hashed = PasswordAuthentication.hash(password.toCharArray());

        // valid info, create account
        try (PreparedStatement statement = conn.prepareStatement("INSERT INTO `PADBlog`.`users` (`name`, `email`, `password`, `salt`) VALUES (?, ?, ?, ?);")) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setBytes(3, hashed[0]);
            statement.setBytes(4, hashed[1]);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void makeNewPost(Request req, String title, String content) {

        Object objUser = req.session().attribute("currentUser");
        if (!(objUser instanceof User))
            return;
        User user = (User) objUser;

        try (PreparedStatement statement = conn.prepareStatement("INSERT INTO posts (`title`, `content`, `user_id`) VALUES (?, ?, ?)")) {
            statement.setString(1, title);
            statement.setString(2, content);
            statement.setInt(3, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM posts INNER JOIN users ON users.user_id = posts.user_id WHERE removed = false ORDER BY timestamp DESC")) {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                posts.add(new Post(
                        set.getInt("post_id"),
                        set.getString("title"),
                        set.getString("content"),
                        new User(set.getInt("user_id"), set.getString("name")),
                        set.getTimestamp("timestamp").toLocalDateTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public void editPost(int id, String title, String content) {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE posts SET removed=0, title=?, content=? WHERE post_id=?")) {
            statement.setString(1, title);
            statement.setString(2, content);
            statement.setInt(3, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePost(int id) {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE posts SET removed=1 WHERE post_id = ?;")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
