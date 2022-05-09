/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.managers;

import com.lowagie.text.Document;
import edu.vt.pojo.IndicatorsGraph;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class BinarySerializationManager {

    //=================
    // Static Methods
    //=================

    public static void exportGraph(IndicatorsGraph indicatorsGraph) {
        try {
            FileDialog fileDialog = new FileDialog(new Frame(), "Export...", FileDialog.SAVE);
            fileDialog.setDirectory(System.getProperty("user.dir"));
            fileDialog.setFile("IndicatorsGraph.bin");
            fileDialog.setVisible(true);
            String fileName = fileDialog.getFile();
            if (fileName != null) {
                if (!fileName.endsWith(".bin") && !fileName.endsWith(".txt")) {
                    fileName = fileName + ".bin";
                }
                File file = new File(fileDialog.getDirectory(), fileName);
                FileOutputStream fileOut = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(indicatorsGraph);
                out.close();
                fileOut.close();
                FacesMessage message = new FacesMessage("Indicators Graph was successfully exported.", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static IndicatorsGraph importGraph() {
        try {
            FileDialog fileDialog = new FileDialog(new Frame(), "Import...", FileDialog.LOAD);
            fileDialog.setDirectory("user.dir");
            fileDialog.setFile("*.bin");
            fileDialog.setVisible(true);
            String filename = fileDialog.getFile();
            IndicatorsGraph indicatorsGraph = null;
            if (filename != null) {
                File file = new File(fileDialog.getDirectory(), fileDialog.getFile());
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                indicatorsGraph = (IndicatorsGraph) in.readObject();
                in.close();
                fileIn.close();
            }
            return indicatorsGraph;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("IndicatorsGraph class was not found.");
            c.printStackTrace();
            return null;
        }
    }
}
