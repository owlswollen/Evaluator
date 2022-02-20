/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.managers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;

public class DatabaseSerializationManager {

    //=================
    // Static Variables
    //=================

    private static final String SQL_SERIALIZE_OBJECT = "INSERT INTO serialized_java_objects(object_name, serialized_object) VALUES (?, ?)";
    private static final String SQL_DESERIALIZE_OBJECT = "SELECT serialized_object FROM serialized_java_objects WHERE serialized_id = ?";

    //===============
    // Static Methods
    //===============

    // Serialize a java object to database
    public static long serializeJavaObjectToDB(Connection connection,
                                               Object objectToSerialize) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(SQL_SERIALIZE_OBJECT, Statement.RETURN_GENERATED_KEYS);

        // Setting the class name
        preparedStatement.setString(1, objectToSerialize.getClass().getName());
        preparedStatement.setObject(2, objectToSerialize);
        preparedStatement.executeUpdate();
        ResultSet rs = preparedStatement.getGeneratedKeys();
        int serialized_id = -1;
        if (rs.next()) {
            serialized_id = rs.getInt(1);
        }
        rs.close();
        preparedStatement.close();
        System.out.println("Java object serialized to database. Object: "
                + objectToSerialize);
        return serialized_id;
    }

    // De-serialize a java object from database
    public static Object deSerializeJavaObjectFromDB(Connection connection,
                                                     long serialized_id) throws SQLException, IOException,
            ClassNotFoundException {
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_DESERIALIZE_OBJECT, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setLong(1, serialized_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        // Object object = resultSet.getObject(1);

        byte[] buf = resultSet.getBytes(1);
        ObjectInputStream objectIn = null;
        if (buf != null)
            objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

        Object deSerializedObject = objectIn.readObject();

        resultSet.close();
        preparedStatement.close();

        System.out.println("Java object de-serialized from database. Object: "
                + deSerializedObject + " Classname: "
                + deSerializedObject.getClass().getName());
        return deSerializedObject;
    }
}