package me.donttalkto.padblog.util;

import me.donttalkto.padblog.blog.Post;
import me.donttalkto.padblog.storage.DBManager;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.*;

public class Path {



    public enum WebGet {

        INDEX("/", (Request req, Response res) -> {
            Map<String, Object> arguments = new HashMap<>();
            List<Post> posts = DBManager.get().getAllPosts();
            arguments.put("posts", posts);
            arguments.put("template", "templates/index.vm");
            arguments.put("currentUser", req.session().attribute("currentUser"));
            return render(arguments);
        }),

        LOGIN("/login", (req, res) -> map("login")),

        REGISTER("/register", (req, res) -> map("register")),

        NEW_POST("/newpost", (req, res) -> {
            if (checkIfNotLoggedIn(req, res))
                return render(new HashMap<>());

            Map<String, Object> arguments = new HashMap<>();
            arguments.put("template", "templates/newpost.vm");
            arguments.put("currentUser", req.session().attribute("currentUser"));
            return render(arguments);
        }),

        EDIT_POST("/editpost", (req, res) -> {
            if (checkIfNotLoggedIn(req, res))
                return render(new HashMap<>());

            if ("Remove".equals(req.queryParams("Remove"))) {
                System.out.println("test");
                DBManager.get().removePost(Integer.parseInt(req.queryParams("idInput")));
                res.redirect("/");
                return "";
            }

            Map<String, Object> arguments = new HashMap<>();
            String[] args = {"idInput", "titleInput", "contentInput"};
            for (String arg : args)
                arguments.put(arg, req.queryParams(arg));
            arguments.put("template", "templates/editpost.vm");
            arguments.put("currentUser", req.session().attribute("currentUser"));
            return render(arguments);
        }),

        LOGOUT("/logout", (req, res) -> {
            req.session().removeAttribute("currentUser");
            res.redirect(WebGet.INDEX.toString());
            return render(new HashMap<>());
        });

        private final String path;
        private final Route route;
        WebGet(String path, Route route) {
            this.path = path;
            this.route = route;
        }
        @Override
        public String toString() {
            return path;
        }
        public Route getRoute() {
            return route;
        }
    }

    public enum WebPost {

        LOGIN("/login", (req, res) -> {
            String result = DBManager.get().login(req, res, req.queryParams("email"), req.queryParams("password"));
            if (result == null) {
                res.redirect("/");
                return "";
            } else {
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("badLogin", true);
                arguments.put("template", "templates/login.vm");
                return render(arguments);
            }
        }),

        REGISTER("/register", (req, res) -> {
            String result =  DBManager.get().makeNewAccount(
                    req.queryParams("email"),
                    req.queryParams("name"),
                    req.queryParams("password"),
                    req.queryParams("repassword")
            );
            if (result == null) {
                res.redirect("/login");
                return map("/login");
            } else {
                Map<String, Object> arguments = new HashMap<>();
                arguments.put(result, true);
                arguments.put("template", "templates/register.vm");
                return render(arguments);
            }
        }),

        EDIT_POST("/editpost", (req, res) -> {

            if (checkIfNotLoggedIn(req, res))
                return render(new HashMap<>());

            DBManager.get().editPost(Integer.parseInt(
                    req.queryParams("idInput")),
                    req.queryParams("newTitle"),
                    req.queryParams("content"));

            res.redirect("/");
            return "/";
        }),

        NEW_POST("/newpost", (req, res) -> {

            if (checkIfNotLoggedIn(req, res))
                return render(new HashMap<>());

            DBManager.get().makeNewPost(req,
                    req.queryParams("newTitle"),
                    req.queryParams("content"));
            res.redirect("/");
            return WebGet.INDEX.getRoute();

        });

        private final String path;
        private final Route route;
        WebPost(String path, Route route) {
            this.path = path;
            this.route = route;
        }
        @Override
        public String toString() {
            return path;
        }
        public Route getRoute() {
            return route;
        }
    }

    private static boolean checkIfNotLoggedIn(Request req, Response res) {
        if (req.session().attribute("currentUser") == null) {
            res.redirect("/login");
            return true;
        }
        return false;
    }

    private static String map(String file) {
        file = file.startsWith("/") ? file.substring(1) : file;
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("template", "templates/" + file + ".vm");
        return render(arguments);
    }

    private static String render(Map<String, Object> arguments) {
        return new VelocityTemplateEngine().render(new ModelAndView(arguments, "templates/layout.vm"));
    }

}
