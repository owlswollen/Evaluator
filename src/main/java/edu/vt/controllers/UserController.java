/*
 * Created by Osman Balci on 2022.1.7
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserFile;
import edu.vt.EntityBeans.UserPhoto;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.FacadeBeans.UserFileFacade;
import edu.vt.FacadeBeans.UserPhotoFacade;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import edu.vt.globals.Password;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@Named("userController")
@SessionScoped
public class UserController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;
    private String confirmPassword;

    private String firstName;
    private String middleName;
    private String lastName;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipcode;

    private String selectedSecurityQuestion;
    private String answerToSecurityQuestion;

    private String email;

    // Two-Factor Authentication is on by sending random code via email
    private Boolean twoFAonViaEmail = false;
    // Two-Factor Authentication is on by sending random code via Short Message Service (SMS) a.k.a. text message
    private Boolean twoFAonViaSMS = false;

    private String cellPhoneNumber = "";
    private String cellPhoneCarrier = "";

    private User selected;

    // 'allUsers' is a List containing object references of all Users in the database
    private List<User> allUsers = null;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    UserFacade bean into the instance variable 'userFacade' after it is instantiated at runtime.
     */
    @EJB
    private UserFacade userFacade;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    UserPhotoFacade bean into the instance variable 'userPhotoFacade' after it is instantiated at runtime.
     */
    @EJB
    private UserPhotoFacade userPhotoFacade;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    UserFileFacade bean into the instance variable 'userFileFacade' after it is instantiated at runtime.
     */
    @EJB
    private UserFileFacade userFileFacade;

    @Inject
    private ProjectController projectController;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getSelectedSecurityQuestion() {
        return selectedSecurityQuestion;
    }

    public void setSelectedSecurityQuestion(String selectedSecurityQuestion) {
        this.selectedSecurityQuestion = selectedSecurityQuestion;
    }

    public String getAnswerToSecurityQuestion() {
        return answerToSecurityQuestion;
    }

    public void setAnswerToSecurityQuestion(String answerToSecurityQuestion) {
        this.answerToSecurityQuestion = answerToSecurityQuestion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getTwoFAonViaEmail() {
        return twoFAonViaEmail;
    }

    public void setTwoFAonViaEmail(Boolean twoFAonViaEmail) {
        this.twoFAonViaEmail = twoFAonViaEmail;
        if (twoFAonViaEmail) {
            this.twoFAonViaSMS = false;
        }
    }

    public Boolean getTwoFAonViaSMS() {
        return twoFAonViaSMS;
    }

    public void setTwoFAonViaSMS(Boolean twoFAonViaSMS) {
        this.twoFAonViaSMS = twoFAonViaSMS;
        if (twoFAonViaSMS) {
            this.twoFAonViaEmail = false;
        }
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public String getCellPhoneCarrier() {
        return cellPhoneCarrier;
    }

    public void setCellPhoneCarrier(String cellPhoneCarrier) {
        this.cellPhoneCarrier = cellPhoneCarrier;
    }

    public User getSelected() {
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    public List<User> getAllUsers() {
        if (allUsers == null) {
            allUsers = userFacade.findAll();
        }
        return allUsers;
    }

    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    /*
    ================
    Instance Methods
    ================
    */

    public User getSignedInUser() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        /*
         "user", the object reference of the signed-in user, was put into the SessionMap
         in the initializeSessionMap() method of LoginManager upon user's sign in.
         */

        // Return the object reference of the signed-in User object
        return (User) sessionMap.get("user");
    }

    /*
    **********************************
    Return True if a User is Signed In
    **********************************
     */
    public boolean userIsSignedIn() {
        return getSignedInUser() != null;
    }

    /*
    ******************************************
    Return True if Signed-In User is SuperUser
    ******************************************
     */
    public boolean isSuperUser() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        /*
        'username' of the signed-in user, was put into the SessionMap
        in the initializeSessionMap() method in LoginManager upon user's sign in.
         */
        String user_name = (String) sessionMap.get("username");

        if (user_name != null) {
            return user_name.equals("SuperUser");
        }

        return false;
    }

    /*
    **************************************
    Return List of U.S. State Postal Codes
    **************************************
     */
    public String[] listOfStates() {
        return Constants.STATES;
    }

    /*
    *********************************
    Return List of Security Questions
    *********************************
     */
    public String[] securityQuestions() {
        return Constants.SECURITY_QUESTIONS;
    }

    /*
    *********************************************
    Process Submitted Answer to Security Question
    *********************************************
     */
    public void securityAnswerSubmit() {
        String actualSecurityAnswer = getSignedInUser().getSecurityAnswer();

        // getAnswerToSecurityQuestion() is the Getter method for the property 'answerToSecurityQuestion'
        String enteredSecurityAnswer = getAnswerToSecurityQuestion();

        if (actualSecurityAnswer.equals(enteredSecurityAnswer)) {
            // Answer to the security question is correct; Delete the user's account.
            // deleteAccount() method is given below.
            deleteAccount();
        } else {
            Methods.showMessage("Error",
                    "Answer to the Security Question is Incorrect!", "");
        }
    }

    /*
    **********************************************************
    Create User's Account and Redirect to Show the SignIn Page
    **********************************************************
     */
    public String createAccount() {
        /*
        ----------------------------------------------------------------
        Password and Confirm Password are validated under 3 tests:
        
        <1> Non-empty (tested with required="true" in JSF page)
        <2> Correct composition satisfying the regex rule.
            (tested with <f:validator validatorId="passwordValidator" /> in the JSF page)
        <3> Password and Confirm Password must match (tested below)
        ----------------------------------------------------------------
         */
        if (!password.equals(confirmPassword)) {
            Methods.showMessage("Fatal Error", "Unmatched Passwords!",
                    "Password and Confirm Password must Match!");
            return "";
        }

        //--------------------------------------------
        // Password and Confirm Password are Validated
        //--------------------------------------------

        /*
        Redirecting to show a JSF page involves more than one subsequent requests and
        the messages would die from one request to another if not kept in the Flash scope.
        Since we will redirect to show the SignIn page, we invoke preserveMessages().
         */
        Methods.preserveMessages();

        //-----------------------------------------------------------
        // First, check if the entered username is already being used
        //-----------------------------------------------------------
        // Obtain the object reference of a User object with the username entered by the user
        User aUser = userFacade.findByUsername(username);

        if (aUser != null) {
            // A user already exists with the username entered by the user
            username = "";
            Methods.showMessage("Fatal Error", "Username Already Exists!",
                    "Please Select a Different One!");
            return "";
        }

        //----------------------------------
        // The entered username is available
        //----------------------------------
        try {
            // Instantiate a new User object
            User newUser = new User();

            /*
             Set the properties of the newly created newUser object with the values
             entered by the user in CreateAccount.xhtml
             */
            newUser.setFirstName(firstName);
            newUser.setMiddleName(middleName);
            newUser.setLastName(lastName);
            newUser.setAddress1(address1);
            newUser.setAddress2(address2);
            newUser.setCity(city);
            newUser.setState(state);
            newUser.setZipcode(zipcode);
            newUser.setSecurityQuestion(selectedSecurityQuestion);
            newUser.setSecurityAnswer(answerToSecurityQuestion);
            newUser.setEmail(email);
            newUser.setUsername(username);

            /*
             Two-Factor Authentication Status:
                 = 0 Off
                 = 1 Send random code via email
                 = 2 Send random code via text message
             */
            if (twoFAonViaEmail) {
                newUser.setTwoFaStatus(1);  // Send random code via Email
            } else if (twoFAonViaSMS) {
                newUser.setTwoFaStatus(2);  // Send random code via text message
            } else {
                newUser.setTwoFaStatus(0);  // 2FA is OFF
            }

            newUser.setCellPhoneNumber(cellPhoneNumber);
            newUser.setCellPhoneCarrier(cellPhoneCarrier);

            /*
            Invoke class Password's createHash() method to convert the user-entered String
            password to a String containing the following parts
                  "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
            for secure storage and retrieval with Key Stretching to prevent brute-force attacks.
             */
            String parts = Password.createHash(password);
            newUser.setPassword(parts);

            // Create the user in the database
            userFacade.create(newUser);

        } catch (EJBException | Password.CannotPerformOperationException ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while creating user's account!",
                    "See: " + ex.getMessage());
            return "";
        }

        Methods.showMessage("Information", "Success!",
                "User Account is Successfully Created!");

        return "/signIn/SignIn?faces-redirect=true";
    }

    /*
    *********************************************
    Set 2FA Flags before Showing EditAccount Page
    *********************************************
     */
    public String prepareForEdit() {
        switch(getSignedInUser().getTwoFaStatus()) {
            // 2FA is turned off
            case 0:
                twoFAonViaEmail = false;
                twoFAonViaSMS = false;
                break;
            // Send 2FA Code via Email
            case 1:
                twoFAonViaEmail = true;
                twoFAonViaSMS = false;
                break;
            // Send 2FA Code via SMS
            case 2:
                twoFAonViaEmail = false;
                twoFAonViaSMS = true;
                break;
            default:
        }
        return "/userAccount/EditAccount?faces-redirect=true";
    }

    /*
    ***********************************************************
    Update User's Account and Redirect to Show the Profile Page
    ***********************************************************
     */
    public String updateAccount() {
        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();
        /*
         Signed-in user's properties are changed directly in EditAccount.xhtml
         */
        try {
            /*
             Two-Factor Authentication Status:
                 = 0 Off
                 = 1 Send random code via email
                 = 2 Send random code via text message
             */
            if (twoFAonViaEmail) {
                getSignedInUser().setTwoFaStatus(1);  // Send random code via Email
            } else if (twoFAonViaSMS) {
                getSignedInUser().setTwoFaStatus(2);  // Send random code via text message
            } else {
                getSignedInUser().setTwoFaStatus(0);  // 2FA is OFF
            }

            // Store the changes in the database
            userFacade.edit(getSignedInUser());

            Methods.showMessage("Information", "Success!",
                    "User's Account is Successfully Updated!");

        } catch (EJBException ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while updating user's profile!",
                    "See: " + ex.getMessage());
            return "";
        }

        // Account update is completed, redirect to show the Profile page.
        return "/userAccount/Profile?faces-redirect=true";
    }

    /*
    *****************************************************************
    Delete User's Account, Logout, and Redirect to Show the Home Page
    *****************************************************************
     */
    public void deleteAccount() {
        Methods.preserveMessages();
        /*
        The database primary key of the signed-in User object was put into the SessionMap
        in the initializeSessionMap() method of LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        int userPrimaryKey = (int) sessionMap.get("user_id");
        String username = (String) sessionMap.get("username");

        try {
            // Delete all photo files associated with signed-in user whose primary key is userPrimaryKey
            // deleteAllUserPhotos() is given below
            deleteAllUserPhotos(userPrimaryKey);

            // Delete all user files associated with signed-in user whose primary key is userPrimaryKey
            // deleteAllUserFiles() is given below
            deleteAllUserFiles(userPrimaryKey);

            // Delete the User entity from the database
            userFacade.deleteUser(userPrimaryKey);

            /*
             Username of the deleted User must be removed from the adminUsernames
             and evaluatorUsernames attributes of all projects if it is included.
             */
            projectController.updateAllProjectsAfterAUserIsDeleted(username);

            Methods.showMessage("Information", "Success!",
                    "Your Account is Successfully Deleted!");

        } catch (EJBException ex) {
            username = "";
            Methods.showMessage("Fatal Error",
                    "Something went wrong while deleting user's account!",
                    "See: " + ex.getMessage());
            return;
        }

        // Execute the logout() method given below
        logout();
    }

    /*
    **********************************************
    Logout User and Redirect to Show the Home Page
    **********************************************
     */
    public void logout() {

        // Clear the signed-in User's session map
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.clear();

        // Reset the signed-in User's properties
        username = password = confirmPassword = "";
        firstName = middleName = lastName = "";
        address1 = address2 = city = state = zipcode = "";
        selectedSecurityQuestion = answerToSecurityQuestion = email = "";
        cellPhoneNumber = cellPhoneCarrier = "";

        // Since we will redirect to show the home page, invoke preserveMessages()
        Methods.preserveMessages();

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            // Invalidate the signed-in User's session
            externalContext.invalidateSession();

            /*
            getRequestContextPath() returns the URI of the webapp directory of the application.
            Obtain the URI of the index (home) page to redirect to.
             */
            String redirectPageURI = externalContext.getRequestContextPath() + "/index.xhtml";

            // Redirect to show the index (home) page
            externalContext.redirect(redirectPageURI);

            /*
            NOTE: We cannot use: return "/index?faces-redirect=true"; here because the user's session is invalidated.
             */
        } catch (IOException ex) {
            Methods.showMessage("Fatal Error",
                    "Unable to redirect to the index (home) page!",
                    "See: " + ex.getMessage());
        }
    }

    /*
    ***************************************
    Return Signed-In User's Thumbnail Photo
    ***************************************
     */
    public String userPhoto() {
        /*
        The database primary key of the signed-in User object was put into the SessionMap
        in the initializeSessionMap() method of LoginManager upon user's sign in.
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Integer primaryKey = (Integer) sessionMap.get("user_id");

        List<UserPhoto> photoList = userPhotoFacade.findPhotosByUserPrimaryKey(primaryKey);

        if (photoList.isEmpty()) {
            // No user photo exists. Return defaultUserPhoto.png.
            return Constants.PHOTOS_URI + "defaultUserPhoto.png";
        }

        /*
        photoList.get(0) returns the object reference of the first Photo object in the list.
        getThumbnailFileName() message is sent to that Photo object to retrieve its
        thumbnail image file name, e.g., 5_thumbnail.jpeg
         */
        String thumbnailFileName = photoList.get(0).getThumbnailFileName();

        return Constants.PHOTOS_URI + thumbnailFileName;
    }

    /*
    ******************************************
    Thumbnail Photo of a User with Primary Key
    ******************************************
     */
    public String photoOfUser(Integer primaryKey) {

        List<UserPhoto> photoList = userPhotoFacade.findPhotosByUserPrimaryKey(primaryKey);

        if (photoList.isEmpty()) {
            // No user photo exists. Return defaultUserPhoto.png.
            return Constants.PHOTOS_URI + "defaultUserPhoto.png";
        }

        /*
        photoList.get(0) returns the object reference of the first Photo object in the list.
        getThumbnailFileName() message is sent to that Photo object to retrieve its
        thumbnail image file name, e.g., 5_thumbnail.jpeg
         */
        String thumbnailFileName = photoList.get(0).getThumbnailFileName();

        return Constants.PHOTOS_URI + thumbnailFileName;
    }

    /*
    ********************************************************
    Delete the photo, thumbnail, and tempFile that belong to
    the User object whose database primary key is primaryKey
    ********************************************************
     */
    public void deleteAllUserPhotos(int primaryKey) {
        /*
        Obtain the list of Photo objects that belong to the User whose
        database primary key is userId.
         */
        List<UserPhoto> photoList = userPhotoFacade.findPhotosByUserPrimaryKey(primaryKey);

        // photoList.isEmpty implies that the user has never uploaded a photo file
        if (!photoList.isEmpty()) {

            // Obtain the object reference of the first Photo object in the list.
            UserPhoto photo = photoList.get(0);
            try {
                /*
                NOTE: Files and Paths are imported as
                        import java.nio.file.Files;
                        import java.nio.file.Paths;

                 Delete the user's photo if it exists. Each user has only one profile photo.
                 getPhotoFilePath() is given in UserPhoto entity bean file.
                 */
                Files.deleteIfExists(Paths.get(photo.getPhotoFilePath()));

                /*
                 Delete the user's thumbnail image if it exists. Each user has only one thumbnail photo.
                 getThumbnailFilePath() is given in UserPhoto entity bean file.
                 */
                Files.deleteIfExists(Paths.get(photo.getThumbnailFilePath()));

                // Delete the photo file record from the database.
                // UserPhotoFacade inherits the remove() method from AbstractFacade.
                userPhotoFacade.remove(photo);

                /*
                 Delete the user's captured photo file if it exists.
                 The file is named "user's primary key_tempFile".
                 */
                String capturedPhotoFilepath = Constants.PHOTOS_ABSOLUTE_PATH + primaryKey + "_tempFile";
                Files.deleteIfExists(Paths.get(capturedPhotoFilepath));

            } catch (IOException ex) {
                Methods.showMessage("Fatal Error",
                        "Something went wrong while deleting user's photo!",
                        "See: " + ex.getMessage());
            }
        }
    }

    /*
    ***********************************************
    Delete all the files that belong to the User
    object whose database primary key is primaryKey
    ***********************************************
     */
    public void deleteAllUserFiles(int primaryKey) {

        // Obtain the List of files that belongs to the user with primaryKey
        List<UserFile> userFilesList = userFileFacade.findUserFilesByUserPrimaryKey(primaryKey);

        if (!userFilesList.isEmpty()) {
            // Java looping over a list with lambda
            userFilesList.forEach(userFile -> {
                try {
                    /*
                    Delete the user file if it exists.
                    getFilePath() is given in UserFile entity bean file.
                     */
                    Files.deleteIfExists(Paths.get(userFile.getFilePath()));

                    // Remove the user's file record from the database
                    userFileFacade.remove(userFile);

                } catch (IOException ex) {
                    Methods.showMessage("Fatal Error",
                            "Something went wrong while deleting user files!",
                            "See: " + ex.getMessage());
                }
            });
        }
    }

}
