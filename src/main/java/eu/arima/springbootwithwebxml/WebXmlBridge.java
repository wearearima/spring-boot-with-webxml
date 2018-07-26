package eu.arima.springbootwithwebxml;

import org.apache.tomcat.util.descriptor.web.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.xml.sax.InputSource;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Implementation of {@link ServletContextInitializer} to register the servlets, listeners and filters defined
 * in the application's web.xml on startup.
 *
 * TODO servlets' init-params are not supported
 * TODO filters are not registered
 */
public class WebXmlBridge implements ServletContextInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebXmlBridge.class);

    private static final String WEB_XML_PATH = "/WEB-INF/web.xml";

    @Override
    public void onStartup(ServletContext servletContext) {
        WebXml webXml = parseWebXml(servletContext);

        registerServlets(webXml, servletContext);
        registerFilters(webXml, servletContext);
        registerListeners(webXml, servletContext);
    }

    private WebXml parseWebXml(ServletContext servletContext) {
        URL resource;

        try {
            resource = servletContext.getResource(WEB_XML_PATH);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }

        WebXml webXml = new WebXml();

        WebXmlParser parser = new WebXmlParser(false, false, false);
        parser.setClassLoader(WebXmlBridge.class.getClassLoader());

        try (InputStream is = resource.openStream()) {
            boolean success = parser.parseWebXml(new InputSource(is), webXml, false);
            if (!success) {
                throw new IllegalStateException("Error parsing " + WEB_XML_PATH);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading " + WEB_XML_PATH, e);
        }

        return webXml;
    }

    /**
     * It reads servlets defined in web.xml and registers them into the application.
     */
    private void registerServlets(WebXml webXml, ServletContext servletContext) {
        Map<String, String> servletMappings = webXml.getServletMappings();

        for (ServletDef def :  webXml.getServlets().values()) {
            String servletName = def.getServletName();

            List<String> mappings = findMappings(servletMappings, servletName);

            if (mappings.isEmpty()) {
                throw new IllegalStateException("Not mapping defined for " + servletName);
            }

            for (String mapping : mappings) {
                ServletRegistration.Dynamic reg = servletContext.addServlet(servletName, def.getServletClass());
                reg.addMapping(mapping);
            }

            LOGGER.info("Registered servlet with name [{}] Class[{}] Mappings[{}]", servletName, def.getServletClass(),
                    mappings);
        }
    }

    private List<String> findMappings(Map<String, String> servletMappings, String servletName) {
        List<String> mappings = new ArrayList<>();

        for (Map.Entry<String, String> mapping : servletMappings.entrySet()) {
            if (mapping.getValue().equals(servletName)) {
                mappings.add(mapping.getKey());
            }
        }

        return mappings;
    }

    /**
     * Method to read filters defined in web.xml and to register them into the application.
     */
    private void registerFilters(WebXml webXml, ServletContext servletContext) {
        LOGGER.info("Filters are not supported");
    }

    /**
     * It reads listeners defined in web.xml and registers them into the application.
     */
    private void registerListeners(WebXml webXml, ServletContext servletContext) {
        for (String listener : webXml.getListeners()) {
            servletContext.addListener(listener);
            LOGGER.info("Registered listener class [{}]", listener);
        }
    }

}
