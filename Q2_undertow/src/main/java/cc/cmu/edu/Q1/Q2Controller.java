package cc.cmu.edu.Q1;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import javax.servlet.ServletException;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;
import io.undertow.Handlers;

public class Q2Controller {
    public Q2Controller() throws Exception{

    }
    public static final String PATH = "/q2";


    public static void main(String[] args) throws Exception{
        try {
            DeploymentInfo servletBuilder = deployment()
                    .setClassLoader(Q2Controller.class.getClassLoader())
                    .setContextPath(PATH)
                    .setDeploymentName("handler.war")
                    .addServlets(
                            servlet("Q2MySQLServlet", Q2MySQLServlet.class)
                            .addMapping("/")
                    );


            DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
            manager.deploy();

            HttpHandler servletHandler = manager.start();
            PathHandler path = Handlers.path(Handlers.redirect(PATH))
                    .addPrefixPath(PATH, servletHandler);

            Undertow server = Undertow.builder()
                    .addHttpListener(80, "0.0.0.0")
                    .setHandler(path)
                    .build();
            server.start();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
