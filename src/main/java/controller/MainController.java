package controller;

import controller.admission.AdmissionController;
import controller.communication.CommunicationsController;
import controller.overview.OverviewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

@SuppressWarnings("unused")
public class MainController {

	private Parent root;

	// UI Elements
	@FXML
	private Label systemMessage;
	@FXML
	private ComboBox<?> employeeId;
	@FXML
	private Button btnLogout;
	// Controllers
	@FXML
	private AdmissionController admissionController; //line 17 in MainForm.fxml
	@FXML
	private OverviewController overviewController; //line 22 in MainForm.fxml
	@FXML
	private CommunicationsController communicationsController; //line 27 in MainForm.fxml

	@FXML
	private void initialize() {
	}

	public void setRoot(Parent root) {
		this.root = root;
	}

	public Parent getRoot() {
		return root;
	}
}
