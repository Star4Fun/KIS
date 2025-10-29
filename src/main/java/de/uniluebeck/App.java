package de.uniluebeck.kis;

import de.uniluebeck.kis.db.Db;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Minimal JavaFX app with SLF4J logging and DB bootstrap. */
public class App extends Application {
  private static final Logger log = LoggerFactory.getLogger(App.class);

  @Override
  public void start(Stage stage) {
    stage.setTitle("KIS / JavaFX");
    stage.setScene(new Scene(new Label("Hello JavaFX"), 320, 120));
    stage.show();

    // Initialize DB off the FX thread to avoid UI stutter.
    new Thread(
            () -> {
              try {
                log.info("Bootstrapping database …");
                Db.init(); // Flyway migrate + Hikari pool + health check
                log.info("Database ready.");
              } catch (Exception e) {
                log.error("DB bootstrap failed", e);
                // Optional: close the app on fatal DB error
                // Platform.runLater(
                //     () -> {
                //       stage.close();
                //       Platform.exit();
                //     });
              }
            },
            "db-bootstrap")
        .start();
  }

  @Override
  public void stop() {
    log.info("Stopping KIS application.");
    // Gracefully shutdown connection pool
    de.uniluebeck.kis.db.Db.shutdown();
  }

  public static void main(String[] args) {
    log.info("Starting KIS …");
    launch(args);
  }
}
