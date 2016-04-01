package com.teleport.client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable
{
    @FXML private ListView lstViewContacts;
    @FXML private ListView lstViewUsername;
    @FXML private TextField txtSearch;
    @FXML private Button butInbox;
    @FXML private Label lblMsg;
    @FXML private Button butSend;
    @FXML private ProgressBar pbSendFile;

    private PostLoginInterface log;

    public MainPageController() throws IOException
    {
        log = new PostLoginInterface();
        lstViewUsername = new ListView();
    }

    public void MainPage(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("MainPage");
        stage.setScene(scene);
        stage.show();
    }

    @FXML public void AddFriend(ActionEvent event) throws Exception
    {
        Stage stage = new Stage();
        AddFriend(stage);
    }

    public void AddFriend(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("AddFriend.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Add Friend");
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void SendFriendRequest(ActionEvent e) throws IOException, ParseException
    {
        String status = log.addFriend(lstViewUsername.getSelectionModel().getSelectedItem().toString());
        lblMsg.setText(status);
        lblMsg.setTextFill(Color.FIREBRICK);
    }

    @FXML protected void PushedButton()
    {
        butSend.setVisible(true);
    }

    @FXML protected void SwitchScreen(ActionEvent e)throws IOException
    {
        InboxController c =new InboxController();
        c.Inbox();
    }

    @FXML protected void SearchFriend() throws IOException, ParseException
    {
        lstViewUsername.setItems(FXCollections.observableList(log.getUsername(txtSearch.getText())));
    }

    public void ChoseUsernameToSend(MouseEvent mouseEvent) throws IOException, ParseException
    {
        if(lstViewContacts != null && lstViewContacts.getItems().size() > 0 && mouseEvent.getButton().equals(MouseButton.PRIMARY))
        {
            if(mouseEvent.getClickCount() == 2)
            {
                Stage stage = (Stage) butInbox.getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(stage);
                if (file != null)
                {
                    log.send(lstViewContacts.getSelectionModel().getSelectedItem().toString(), file.getPath());
                }
            }
        }
    }

    @Override public void initialize(URL url, ResourceBundle rb)
    {
        if(url.toString().contains("MainPage"))
        {
            try
            {
                lstViewContacts.setItems(FXCollections.observableList(log.getFriends()));
            } catch (ParseException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
