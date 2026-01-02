package at.letto.plugins;

import at.letto.plugins.tools.ServerStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.jar.Manifest;

@SpringBootApplication
public class PluginDemoJavaApplication {

    public static void main(String[] args) {
        ServerStatus.mainClass   = PluginDemoJavaApplication.class;
        ServerStatus.mainContext = SpringApplication.run(ServerStatus.mainClass, args);
        try {
            ServerStatus.mainManifest = new Manifest(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
        } catch (Exception ex) {}
    }

}
