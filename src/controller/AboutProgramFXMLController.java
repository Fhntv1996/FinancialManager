package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/*
 * Класс по управлению окном с описанием программы.
 */
public class AboutProgramFXMLController {
    @FXML
    private TextArea aboutProgramTextArea;

    public void setText(String aboutProgram) {
        aboutProgramTextArea.appendText(aboutProgram);
        aboutProgramTextArea.setEditable(false);
    }
}
