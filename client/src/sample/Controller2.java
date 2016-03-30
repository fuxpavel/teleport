package sample;

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
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller2 implements Initializable
{
    @FXML private ListView lstIncoming;
    @FXML private ListView lstOutgoing;
    @FXML private Label lblMsg;
    private PostLoginInterface log;
    private Client client;

    public Controller2() throws IOException
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

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        try
        {
            lstIncoming.setItems(FXCollections.observableArrayList(client.getIncomingFriendRequests()));
            lstOutgoing.setItems(FXCollections.observableArrayList(client.getOutgoingFriendRequests()));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
