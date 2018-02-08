package me.donttalkto.padblog;

import me.donttalkto.padblog.util.Path;

import static spark.Spark.*;

public class Application {

    private Application() {

        staticFiles.location("/public");
        staticFiles.expireTime(1000L);

        // setting up routes
        setupRoutes();

    }

    private void setupRoutes() {
        // main page
        get(Path.WebGet.INDEX.toString(), Path.WebGet.INDEX.getRoute());

        // login page
        get(Path.WebGet.LOGIN.toString(), Path.WebGet.LOGIN.getRoute());

        // register page
        get(Path.WebGet.REGISTER.toString(), Path.WebGet.REGISTER.getRoute());

        // new post page
        get(Path.WebGet.NEW_POST.toString(), Path.WebGet.NEW_POST.getRoute());

        // edit post page
        get(Path.WebGet.EDIT_POST.toString(), Path.WebGet.EDIT_POST.getRoute());

        // login task
        post(Path.WebPost.LOGIN.toString(), Path.WebPost.LOGIN.getRoute());

        // register task
        post(Path.WebPost.REGISTER.toString(), Path.WebPost.REGISTER.getRoute());

        // new post task
        post(Path.WebPost.NEW_POST.toString(), Path.WebPost.NEW_POST.getRoute());

        // edit post task
        post(Path.WebPost.EDIT_POST.toString(), Path.WebPost.EDIT_POST.getRoute());

        // logout page
        get(Path.WebGet.LOGOUT.toString(), Path.WebGet.LOGOUT.getRoute());
    }

    public static void main(String[] args) {
        new Application();
    }

}
