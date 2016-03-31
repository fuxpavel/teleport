package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;


public class LoginRegisterController
{
    private LoginInterface log;
    @FXML private Label lblMsg;
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private TextField txtConfirmPassword;
    @FXML private Button butSwitch;

    public LoginRegisterController() throws IOException
    {
        log = new LoginInterface();
    }
    public void Login(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
    public void Register(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void SwitchScreen(ActionEvent e)throws IOException
    {
        Stage stage = (Stage) butSwitch.getScene().getWindow();
        if(((Button)e.getSource()).getText().equals("Register"))
        {
            Register(stage);
        }
        else
        {
            Login(stage);
        }
    }

    @FXML protected void SubmitLogin(ActionEvent e) throws IOException, org.json.simple.parser.ParseException
    {
        String status = log.login(txtUsername.getText(), txtPassword.getText());
        lblMsg.setText(status);
        lblMsg.setTextFill(Color.FIREBRICK);
        if(status.equals("Logged in successfully"))
        {
            MainPageController c = new MainPageController();
            c.MainPage((Stage) butSwitch.getScene().getWindow());
        }
    }
    @FXML protected void SubmitRegister(ActionEvent e) throws IOException, org.json.simple.parser.ParseException
    {
        String status = log.register(txtUsername.getText(), txtPassword.getText());
        lblMsg.setText(status);
        lblMsg.setTextFill(Color.FIREBRICK);
    }
}
