/*
 * Created by Osman Balci on 2022.1.8
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.controllers;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

/*
 ========================================
 PrimeFaces HTML Text Editor UI Component
 ========================================
 */
@Named("editorController")
@SessionScoped
public class EditorController implements Serializable {
    /*
     ============================
     Instance Variable (Property)
     ============================
     The editorContent property is a String containing the entire editor
     content created by the user in HTML format as a String of characters.
     */
    private String editorContent;

    /*
    ==================
    Constructor Method
    ==================
     */
    public EditorController() {
        editorContent = "";      // Initialize editor content to empty
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getEditorContent() {
        return editorContent;
    }

    public void setEditorContent(String editorContent) {
        this.editorContent = editorContent;
    }

    /*
    ====================
    Clear Editor Content
    ====================
     */
    public void clearEditorContent() {
        editorContent = "";
    }

}
