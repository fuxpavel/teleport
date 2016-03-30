package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;
import org.junit.FixMethodOrder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller1 implements Initializable
{
    @FXML private ListView lstViewContacts;
    @FXML private ListView lstViewUsername;
    @FXML private TextField txtSearch;
    @FXML private Button butInbox;
    @FXML private Label lblMsg;
    @FXML private Button butSend;

    private PostLoginInterface log;

    public Controller1() throws IOException
    {
        log = new PostLoginInterface();
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

    @FXML protected void SendFriendRequest(ActionEvent e) throws IOException, org.json.simple.parser.ParseException
    {
        String status = log.addFriend(lstViewUsername.getFocusModel().getFocusedItem().toString());
        lblMsg.setText(status);
        lblMsg.setTextFill(Color.FIREBRICK);
    }

    @FXML protected void PushedButton()
    {
        butSend.setVisible(true);
    }

    @FXML protected void SwitchScreen(ActionEvent e)throws IOException
    {
        Controller2 c =new Controller2();
        c.Inbox();
    }

    @FXML
    protected void SearchFriend() throws IOException, ParseException, InterruptedException
    {
        lstViewUsername.setItems(FXCollections.observableList(log.getUsername(txtSearch.getText())));
    }

    public void ChoseUsername() throws IOException
    {
        Stage stage = (Stage)butInbox.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        String file = fileChooser.showOpenDialog(stage).getPath();
        log.send(file);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
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
