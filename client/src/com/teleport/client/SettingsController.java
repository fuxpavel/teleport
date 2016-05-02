package com.teleport.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SettingsController implements Initializable
{
    @FXML private TextField txtDefaultPath;
    @FXML private CheckBox cbxSaveZip;
    @FXML private CheckBox cbxOpenPath;
    @FXML private Button butBrowse;
    @FXML private SplitMenuButton ddlTimeout;
    private Authorization authorization;

    public SettingsController() throws IOException
    {
        authorization = new Authorization();
    }

    public void CheckedSaveZip() throws IOException
    {
        authorization.setZip(cbxSaveZip.isSelected());
    }

    public void CheckedOpenLocation() throws IOException
    {
        authorization.setOpen(cbxOpenPath.isSelected());
    }

    public void BrowseActive() throws IOException
    {
        Stage stage = (Stage) butBrowse.getScene().getWindow();
        DirectoryChooser directoryChooserChooser = new DirectoryChooser();
        directoryChooserChooser.setInitialDirectory(javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory());
        File location = directoryChooserChooser.showDialog(stage);
        if(location != null)
        {
            String path = location.getPath();
            txtDefaultPath.setText(path);
            authorization.setPath(path);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        ddlTimeout.setText("Timeout: "+authorization.getTimeout());
        MenuItem[] menuItems = new MenuItem[10];
        for (int i =1;i<= 10; i++)
        {
            menuItems[i-1] = new MenuItem(Integer.toString(i));
            menuItems[i-1].setOnAction(e -> {
                MenuItem mItem = (MenuItem) e.getSource();
                try
                {
                    authorization.setTimeout(Integer.parseInt(mItem.getText()));
                    ddlTimeout.setText("Timeout: "+authorization.getTimeout());
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            });
        }
        txtDefaultPath.setText(authorization.getPath());
        cbxSaveZip.setSelected(authorization.getZip());
        cbxOpenPath.setSelected(authorization.getOpen());
        ddlTimeout.getItems().addAll(menuItems);
    }
}