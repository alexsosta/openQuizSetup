
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {

    @FXML
    TextField directoryTextField;
    @FXML
    TextField jdkDirectoryTextField;
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
        Installer.getInstance().install(path,jdkPath);

    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
