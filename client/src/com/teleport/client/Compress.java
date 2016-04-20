package com.teleport.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compress
{
    public static boolean CheckAlreadyCompress(String input)
    {
        if (input.lastIndexOf(".") > 0)
        {
            return !(input.substring(input.lastIndexOf(".")).equals(".zip"));
        }
        else
        {
            return true;
        }
    }

    public static String ParseFileName(String inputFile)
    {
        File file = new File(inputFile);
        System.out.println(inputFile);
        if (CheckAlreadyCompress(inputFile))
        {
            if (file.isFile())
            {
                return inputFile.substring(0, inputFile.lastIndexOf(".")) + ".zip";
            }
            else if (file.isDirectory())
            {
                return inputFile + ".zip";
            }
        }
        return inputFile;
    }

    public static String Compression(String inputPath) throws IOException
    {
        FileOutputStream fileOutputStream = null;
        String outputCompressFile = ParseFileName(inputPath);
        if(CheckAlreadyCompress(inputPath))
        {
            fileOutputStream = new FileOutputStream(outputCompressFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            File inputFile = new File(inputPath);
            if (inputFile.isFile())
            {
                compressFile(inputFile, "", zipOutputStream);
            }
            else if (inputFile.isDirectory())
            {
                compressFolder(zipOutputStream, inputFile, "");
            }
            zipOutputStream.close();
        }
        return outputCompressFile;
    }

    public static void compressFolder(ZipOutputStream zipOutputStream, File inputFolder, String parentName) throws IOException
    {
        String myname = parentName + inputFolder.getName() + "\\";
        ZipEntry folderZipEntry = new ZipEntry(myname);
        zipOutputStream.putNextEntry(folderZipEntry);
        File[] dir = inputFolder.listFiles();
        for (File file : dir)
        {
            if (file.exists())
            {
                if (file.isFile())
                {
                    compressFile(file, myname, zipOutputStream);
                }
                else if (file.isDirectory())
                {
                    compressFolder(zipOutputStream, file, myname);
                }
            }
        }
        zipOutputStream.closeEntry();
    }

    public static void compressFile(File inputFile, String parentName, ZipOutputStream zipOutputStream) throws IOException
    {
        ZipEntry zipEntry = new ZipEntry(parentName + inputFile.getName());
        zipOutputStream.putNextEntry(zipEntry);
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        byte[] buf = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buf)) > 0)
        {
            zipOutputStream.write(buf, 0, bytesRead);
        }
        zipOutputStream.closeEntry();
    }
}
