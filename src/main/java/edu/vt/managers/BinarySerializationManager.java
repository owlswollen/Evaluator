/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.managers;

import edu.vt.pojo.IndicatorsGraph;

import java.io.*;

public class BinarySerializationManager {

    //=================
    // Static Methods
    //=================

    public static void storeGraph(IndicatorsGraph indicatorsGraph) {
        try {
            FileOutputStream fileOut = new FileOutputStream("GraphData.bin");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(indicatorsGraph);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in GraphData.bin");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static IndicatorsGraph retrieveGraph() {
        try {
            FileInputStream fileIn = new FileInputStream("GraphData.bin");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            IndicatorsGraph indicatorsGraph = (IndicatorsGraph) in.readObject();
            in.close();
            fileIn.close();
            return indicatorsGraph;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("IndicatorsGraph class not found");
            c.printStackTrace();
            return null;
        }
    }
}
