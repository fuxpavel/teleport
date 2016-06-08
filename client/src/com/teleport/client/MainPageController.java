package com.teleport.client;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @FXML private Text lblIncoming;
    private String senderName;
    private PostLoginInterface log;

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
        Scene scene = new Scene(root, width / 2, height);
        stage.setTitle("MainPage");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void AddFriend() throws Exception
    {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("AddFriend.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Add Friend");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void OpenSettings() throws IOException
    {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Settings.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Settings");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void SendFriendRequest() throws IOException, ParseException
    {
        if (lstViewUsername.getSelectionModel().getSelectedItem() != null)
        {
            String status = log.addFriend(lstViewUsername.getSelectionModel().getSelectedItem().toString());
            lblMsg.setText(status);
            lblMsg.setTextFill(Color.FIREBRICK);
        }
    }

    @FXML
    protected void PressedButton()
    {
        butSend.setVisible(true);
    }

    public void Logout() throws IOException, ParseException
    {
        if (log.logout())
        {
            butInbox.getScene().getWindow().hide();
            Stage stage = new Stage();
            new LoginRegisterController().Login(stage);
        }
    }

    @FXML
    protected void SwitchScreenInbox() throws IOException
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

    @FXML
    protected void SearchFriend() throws IOException, ParseException
    {
        lstViewUsername.setItems(FXCollections.observableList(log.getUsername(txtSearch.getText())));
    }

    public void SendFileBeUsername(MouseEvent mouseEvent) throws IOException, ParseException
    {
        if (lstViewContacts.getSelectionModel().getSelectedItem() != null && lstViewContacts.getItems().size() > 0 && (mouseEvent.getButton().equals(MouseButton.PRIMARY) || mouseEvent.getButton().equals(MouseButton.SECONDARY)))
        {
            if (mouseEvent.getClickCount() >= 2)
            {
                String receiver = lstViewContacts.getSelectionModel().getSelectedItem().toString();
                List<String> paths = new ArrayList<>();
                Stage stage = (Stage) butInbox.getScene().getWindow();
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
                {//if send files
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialDirectory(javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory());
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
                    fileChooser.getExtensionFilters().add(extFilter);
                    List<File> files = fileChooser.showOpenMultipleDialog(stage);
                    if (files != null)
                    {
                        for (File file : files)
                        {
                            paths.add(file.getPath());
                        }
                        lblSendFile.setText(log.send(receiver, pbSendFile, lblSendFile, paths));
                    }
                }
                else if (mouseEvent.getButton().equals(MouseButton.SECONDARY))
                {//if send folders
                    DirectoryChooser directoryChooserChooser = new DirectoryChooser();
                    directoryChooserChooser.setInitialDirectory(javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory());
                    File files = directoryChooserChooser.showDialog(stage);
                    if (files != null)
                    {
                        paths.add(files.getPath());
                        lblSendFile.setText(log.send(receiver, pbSendFile, lblSendFile, paths));
                    }
                }
            }
        }
    }

    public void DeleteFriend(KeyEvent event) throws IOException, ParseException
    {
        if (lstViewContacts.getSelectionModel().getSelectedItem() != null && event.getCode().name().equals("DELETE"))
        {
            String contact = lstViewContacts.getSelectionModel().getSelectedItem().toString();
            Alert alert = new Alert(Alert.AlertType.NONE, "Remove '" + contact + "' from your friends list?", ButtonType.APPLY, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.APPLY)
            {
                log.removeFriend(contact);
                lstViewContacts.setItems(FXCollections.observableList(log.getFriends()));
            }
        }
    }

    public void ReceiveFile() throws IOException, ParseException
    {
        String sender = senderName;
        log.receive(sender, pbSendFile, lblSendFile);
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
                        String id = "0";
                        senderName = "";
                        List<String> inProgress = new ArrayList<>();
                        List<String> newIncoming;
                        List<String> requests;
                        Client client = new Client();
                        while (true)
                        {
                            requests = client.getIncomingFriendRequests();
                            if(requests.size() > 0)
                            {
                                butInbox.setStyle("-fx-background-color:green;");
                            }
                            else
                            {
                                butInbox.setStyle("-fx-background-color:-fx-base;");
                            }
                            newIncoming = client.getIncomingTransfers();
                            if (!newIncoming.isEmpty())
                            {
                                for (String newSender : newIncoming)
                                {
                                    senderName = newSender.split("\\.")[0];
                                    id = newSender.split("\\.")[1];
                                    if (!inProgress.contains(id))
                                    {
                                        inProgress.add(id);
                                        ReceiveFile();
                                    }
                                }
                            }
                            else if (inProgress.contains(id) && !newIncoming.contains(senderName + "." + id))
                            {
                                inProgress.remove(id);
                            }
                            else
                            {
                                updateMessage("");
                            }
                            Thread.sleep(5000);
                        }
                    }
                };

//                lblIncoming.textProperty().bind(task.messageProperty());
               // task.setOnSucceeded(e -> lblIncoming.textProperty().unbind());
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }

            catch (ParseException | IOException e)
            {
                lblMsg.setText("Error in initialize");
            }
        }
    }
}
