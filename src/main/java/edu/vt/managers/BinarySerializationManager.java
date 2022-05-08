/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.managers;

import edu.vt.pojo.IndicatorsGraph;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.swing.*;
import java.io.*;

public class BinarySerializationManager {

    //=================
    // Static Methods
    //=================

    public static void exportGraph(IndicatorsGraph indicatorsGraph) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showSaveDialog(null);
            // If the user selects a file
            if (response == JFileChooser.APPROVE_OPTION) {
                FileOutputStream fileOut = new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath());
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(indicatorsGraph);
                out.close();
                fileOut.close();
                FacesMessage message = new FacesMessage("Indicators Graph was successfully exported.", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static IndicatorsGraph importGraph() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(null);
            IndicatorsGraph indicatorsGraph = null;
            // If the user selects a file
            if (response == JFileChooser.APPROVE_OPTION) {
                FileInputStream fileIn = new FileInputStream(fileChooser.getSelectedFile().getAbsolutePath());
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
