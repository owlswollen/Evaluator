/*
 * Created by Osman Balci on 2022.1.6
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.globals;

public final class Constants {
    /*
    ==================================================
    |   Use of Class Variables as Global Constants   |
    ==================================================
    All of the variables in this class are Class Variables (typed with the "static" keyword)
    with Constant values, which can be accessed in any class in the project by specifying
    Constants.CONSTANTNAME, i.e., ClassName.ClassVariableNameInCaps

    Constants are specified in capital letters.
    
    =====================================================
    |   Our Design Decision for Use of External Files   |
    =====================================================
    We decided to use directories external to our application for the storage and retrieval of user's files.
    We do not want to store/retrieve external files into/from our database for the following reasons:
    
        (a) Database storage and retrieval of large files as BLOB (Binary Large OBject) degrades performance.
        (b) BLOBs increase the database complexity.
        (c) The operating system handles the external files instead of the database management system.
    
    WildFly provides an internal web server, named Undertow, to access and display external files.
     */

    //---------------
    // To run locally
    //---------------

    // Windows
//    public static final String FILES_ABSOLUTE_PATH  = "C:/Users/gokceonen/DocRoot/EvaluatorStorage/FileStorage/";
//    public static final String PHOTOS_ABSOLUTE_PATH = "C:/Users/gokceonen/DocRoot/EvaluatorStorage/PhotoStorage/";

    // Unix (macOS) or Linux
//    public static final String FILES_ABSOLUTE_PATH  = "/Users/Gokce/DocRoot/EvaluatorStorage/FileStorage/";
//    public static final String PHOTOS_ABSOLUTE_PATH = "/Users/Gokce/DocRoot/EvaluatorStorage/PhotoStorage/";

    //-------------------------------------------------------------------
    // To run on your AWS EC2 instance, on VENUS or JUPITER course server
    //-------------------------------------------------------------------
    public static final String FILES_ABSOLUTE_PATH  = "/opt/wildfly/DocRoot/EvaluatorStorage/FileStorage/";
    public static final String PHOTOS_ABSOLUTE_PATH = "/opt/wildfly/DocRoot/EvaluatorStorage/PhotoStorage/";

    /*
     ---------------------------------
     To Deploy to Your AWS EC2 server:
     ---------------------------------
     STEP 1: Comment out the two constants under "To run locally" above.
     STEP 2: Uncomment the two constants under "To run on your AWS EC2 instance" above.

     STEP 3: Comment out the two constants under "To run locally" below.
     STEP 4: Uncomment the two constants under "To run on your AWS EC2 instance with your IP address" below.
     STEP 5: Replace 54.92.194.218 with the public IP address of your AWS EC2 instance.

     STEP 6: Select Build --> Rebuild Project.
     STEP 7: Run your app to generate the WAR file. Do not use the app locally!
     STEP 8: Use the generated WAR file to deploy your app to your AWS EC2 server.

     STEP 9: Undo the above changes to run the app locally.
     */

    /*
    =================================================================================================
    |   For displaying external files to the user in an XHTML page, we use the Undertow subsystem.  |
    =================================================================================================
     We configured WildFly Undertow subsystem so that
     http://localhost:8080/evaluatorfiles/f  displays file f from /Users/Balci/DocRoot/EvaluatorStorage/FileStorage/
     http://localhost:8080/evaluatorphotos/p displays file p from /Users/Balci/DocRoot/EvaluatorStorage/PhotoStorage/
     */

    //---------------
    // To run locally
    //---------------
//    public static final String FILES_URI  = "http://localhost:8080/evaluatorfiles/";
//    public static final String PHOTOS_URI = "http://localhost:8080/evaluatorphotos/";

    //-----------------------------------------------------
    // To run on your AWS EC2 instance with your IP address
    //-----------------------------------------------------
//    public static final String FILES_URI  = "http://54.237.34.21:8080/evaluatorfiles/";
//    public static final String PHOTOS_URI = "http://54.237.34.21:8080/evaluatorphotos/";

    //-----------------------------------------
    // To run on VENUS or JUPITER course server
    //-----------------------------------------
//    public static final String FILES_URI  = "https://venus.cs.vt.edu/evaluatorfiles/";
//    public static final String PHOTOS_URI = "https://venus.cs.vt.edu/evaluatorphotos/";

    //-----------------------------------------
    // To run on SHARK server
    //-----------------------------------------
    public static final String FILES_URI  = "https://shark.cs.vt.edu/evaluatorfiles/";
    public static final String PHOTOS_URI = "https://shark.cs.vt.edu/evaluatorphotos/";

    /* 
    =============================================
    |   Our Design Decision for Profile Photo   |
    =============================================
    We do not want to use the uploaded user profile photo as is, which may be very large
    degrading performance. We scale it down to size 200x200 called the Thumbnail photo size.
     */
    public static final Integer THUMBNAIL_SIZE = 200;

    /* 
     United States postal state abbreviations (codes)
     */
    public static final String[] STATES = {"AK", "AL", "AR", "AZ", "CA", "CO", "CT",
        "DC", "DE", "FL", "GA", "GU", "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA",
        "MD", "ME", "MH", "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", "NH", "NJ", "NM",
        "NV", "NY", "OH", "OK", "OR", "PA", "PR", "PW", "RI", "SC", "SD", "TN", "TX", "UT",
        "VA", "VI", "VT", "WA", "WI", "WV", "WY"};

    /* 
     A security question is selected and answered by the user at the time of account creation.
     The selected question/answer is used as a second level of authentication for
     (a) resetting user's password, and (b) deleting user's account.
     */
    public static final String[] SECURITY_QUESTIONS = {
        "In what city or town did your mother and father meet?",
        "In what city or town were you born?",
        "What did you want to be when you grew up?",
        "What do you remember most from your childhood?",
        "What is the name of the boy or girl that you first kissed?",
        "What is the name of the first school you attended?",
        "What is the name of your favorite childhood friend?",
        "What is the name of your first pet?",
        "What is your mother's maiden name?",
        "What was your favorite place to visit as a child?"
    };

}
