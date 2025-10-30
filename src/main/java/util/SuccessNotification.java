package util;

import controller.admission.PatientController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class SuccessNotification {

    private static final int DEFAULT_TIME = 5;

    private final Notifications factory;

    private SuccessNotification(Object owner, String title, TextFlow body, int time) {
        this.factory = Notifications.create()
                                    .title(title)
                                    .graphic(createGraphic(body))
                                    .owner(owner)
                                    .hideAfter(Duration.seconds(time));
    }

    public static void show(Object owner, String title, TextFlow body) {
        show(owner, title, body, DEFAULT_TIME);
    }
    public static void show(Object owner, String title, String body) {
        show(owner, title, body, DEFAULT_TIME);
    }

    public static void show(Object owner, String title, String body, int time) {
        show(owner, title, new TextFlow(new Text(body)), time);
    }
    public static void show(Object owner, String title, TextFlow body, int time) {
        new SuccessNotification(owner, title, body, time).show();
    }

    private static Node createGraphic(TextFlow textFlow) {
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(new ImageView(PatientController.class.getResource("/dialog-information.png").toExternalForm()));
        textFlow.setTextAlignment(TextAlignment.CENTER);
        //textFlow.setPadding(new Insets(15, 0, 5, 5));
        VBox vBox = new VBox(textFlow);
        vBox.setAlignment(Pos.CENTER_LEFT);
        borderPane.setRight(vBox);
        return borderPane;
    }

    public void show() {
        factory.show();
    }
}
