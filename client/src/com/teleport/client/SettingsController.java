package com.teleport.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

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
        txtDefaultPath.setText(authorization.getPath());
        cbxSaveZip.setSelected(authorization.getZip());
        cbxOpenPath.setSelected(authorization.getOpen());
    }
}