
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {
    @FXML
    TextField directoryTextField;
    @FXML
    TextField jdkDirectoryTextField;
    @FXML
    TextField portTextField;
    @FXML
    TextField progressTextField;
    @FXML
    Button cancelButton;

    public void directoryButtonAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(source.getScene().getWindow());
        if(selectedDirectory == null){
            //No Directory selected
        }else{
            directoryTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void jdkDirectoryButtonAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(source.getScene().getWindow());
        if(selectedDirectory == null){
            //No Directory selected
        }else{
            jdkDirectoryTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void okButtonAction(ActionEvent actionEvent) {
        String path = directoryTextField.getText();
        String jdkPath = jdkDirectoryTextField.getText();
        int port = Integer.parseInt(portTextField.getText());
        Controller controller = this;

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Installer.getInstance().install(path, jdkPath, port, controller);
                return null ;
            }
        };
        task.setOnSucceeded(e -> finishAlert());
        new Thread(task).start();
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void finishAlert(){
        String adminPage = "http://localhost/simpleQuiz/admin.html";
        Hyperlink link = new Hyperlink();
        link.setText(adminPage);
        link.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.getInstance().getHostServices().showDocument(adminPage);
            }
        });
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("openQuizSetup");
        DialogPane pane = new DialogPane();
        pane.setHeaderText("Application installed. You can go to the administrator page by the link: ");
        pane.setContent(link);
        pane.getButtonTypes().addAll(ButtonType.OK);
        alert.setDialogPane(pane);
        alert.showAndWait();
    }
}
