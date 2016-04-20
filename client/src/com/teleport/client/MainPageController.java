package com.teleport.client;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class MainPageController implements Initializable
{
    @FXML private ListView lstViewContacts;
    @FXML private ListView lstViewUsername;
    @FXML private TextField txtSearch;
    @FXML private Button butInbox;
    @FXML private Label lblMsg;
    @FXML private Text lblSendFile;
    @FXML private Button butSend;
    @FXML private ProgressBar pbSendFile;
    @FXML private Button butDenial;
    @FXML private Button butReceive;
    @FXML private Text lblIncoming;
    private String senderName;
    private PostLoginInterface log;
    private boolean clicked;

    public MainPageController() throws IOException
    {
        log = new PostLoginInterface();
    }

    public void MainPage(Stage stage) throws IOException
    {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth() - 20;
        int height = gd.getDisplayMode().getHeight() - 100;
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
        Scene scene = new Scene(root, width/2, height);
        stage.setTitle("MainPage");
        stage.setScene(scene);
        stage.show();
    }

    @FXML public void AddFriend() throws Exception
    {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("AddFriend.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Add Friend");
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void SendFriendRequest() throws IOException, ParseException
    {
        String status = log.addFriend(lstViewUsername.getSelectionModel().getSelectedItem().toString());
        lblMsg.setText(status);
        lblMsg.setTextFill(Color.FIREBRICK);
    }

    @FXML protected void PushedButton()
    {
        butSend.setVisible(true);
    }

    @FXML protected void SwitchScreenInbox() throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("Inbox.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Inbox");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(we -> {
            try
            {
                lstViewContacts.setItems(FXCollections.observableList(log.getFriends()));
            }
            catch (IOException | ParseException e)
            {
                e.printStackTrace();
            }
        });
    }

    @FXML protected void SearchFriend() throws IOException, ParseException
    {
        lstViewUsername.setItems(FXCollections.observableList(log.getUsername(txtSearch.getText())));
    }

    public void ChoseUsernameToSend(MouseEvent mouseEvent) throws IOException, ParseException
    {
        if (lstViewContacts != null && lstViewContacts.getItems().size() > 0 && mouseEvent.getButton().equals(MouseButton.PRIMARY))
        {
            if (mouseEvent.getClickCount() == 2 && lstViewContacts.getSelectionModel().getSelectedItem() != null)
            {
                Stage stage = (Stage) butInbox.getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory());
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
                fileChooser.getExtensionFilters().add(extFilter);
                List<File> files = fileChooser.showOpenMultipleDialog(stage);
                List<String> paths = new ArrayList<>();

                for (File file : files)
                {
                    paths.add(file.getPath());
                }
                String receiver = lstViewContacts.getSelectionModel().getSelectedItem().toString();
                //pbSendFile.setProgress(0);
                lblSendFile.setText("");
                //pbSendFile.setStyle("-fx-accent: blue;");
                log.send(receiver, pbSendFile, lblSendFile, paths);
            }
        }
    }

    public void VisibleButton(boolean visible)
    {
        butDenial.setVisible(visible);
        butReceive.setVisible(visible);
        clicked = !visible;
    }

    public void ReceiveFile(Event e) throws IOException, ParseException
    {
        VisibleButton(true);
        String sender = senderName;
        if (e.getSource().toString().contains("Receive"))
        {
            if (lblSendFile != null && pbSendFile != null)
            {
                lblSendFile.setText("");
                // pbSendFile.setProgress(0);
                //pbSendFile.setStyle("-fx-accent: blue;");
                log.receive(sender, pbSendFile, lblSendFile, true);
            }
            else System.out.println("in ain page controller 5");
        }
        else
        {
            log.receive(sender, pbSendFile, lblSendFile, false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        if (url.toString().contains("MainPage"))
        {
            try
            {
                lstViewContacts.setItems(FXCollections.observableList(log.getFriends()));
                Task<Void> task = new Task<Void>()
                {
                    @Override
                    public Void call() throws InterruptedException, IOException, ParseException
                    {
                        List<String> newIncoming;
                        Client client = new Client();
                        while (true)
                        {
                            newIncoming = client.getIncomingTransfers();
                            if (!newIncoming.isEmpty())
                            {
                                if (!clicked)
                                {
                                    for (String newSender : newIncoming)
                                    {
                                        senderName = newSender;
                                    }
                                }
                                updateMessage(" " + senderName + " want send u file");
                                VisibleButton(true);
                            }
                            else
                            {
                                updateMessage("");
                                VisibleButton(false);
                            }
                            Thread.sleep(15000);
                        }
                    }
                };

                lblIncoming.textProperty().bind(task.messageProperty());
                task.setOnSucceeded(e -> lblIncoming.textProperty().unbind());
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }
            catch (ParseException | IOException e)
            {
                System.out.println("error in initialize");
                lblMsg.setText("Error in initialize");
            }
        }
    }
}
