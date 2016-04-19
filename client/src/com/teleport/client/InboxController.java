package com.teleport.client;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InboxController implements Initializable
{
    @FXML private ListView lstIncoming;
    @FXML private ListView lstOutgoing;
    @FXML private Label lblMsg;
    @FXML private Button butConfirm;
    @FXML private Button butDenial;
    private PostLoginInterface log;
    private Client client;

    public InboxController() throws IOException
    {
        log = new PostLoginInterface();
        client = new Client();
    }

    public void Inbox() throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Inbox.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Inbox");
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void PushedButton()
    {
        if(lstIncoming.getItems().size() > 0)
        {
            butConfirm.setVisible(true);
            butDenial.setVisible(true);
        }
    }

    public void RespondToRequest(ActionEvent event) throws IOException, ParseException
    {
        String friend = lstIncoming.getSelectionModel().getSelectedItem().toString();
        String status = log.respondToRequest(friend, event.getSource().toString().contains("Confirm"));
        lblMsg.setText(status);
        lblMsg.setTextFill(Color.FIREBRICK);
    }

    @Override public void initialize(URL url, ResourceBundle rb)
    {
        try
        {
            lstIncoming.setItems(FXCollections.observableArrayList(client.getIncomingFriendRequests()));
            lstOutgoing.setItems(FXCollections.observableArrayList(client.getOutgoingFriendRequests()));
        }
        catch (ParseException | IOException  e)
        {
            e.printStackTrace();
        }
    }
}
