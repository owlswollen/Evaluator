/*
 * Created by Osman Balci on 2021.11.12
 * Copyright © 2021 Osman Balci. All rights reserved.
 */

package edu.vt.galleria;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@Named(value = "photoService")
@ApplicationScoped
public class PhotoService {
    /*
    ============================
    Instance Variable (Property)
    ============================
     */
    private List<Photo> listOfPhotos;

    /*
    The PostConstruct annotation is used on a method that needs to be executed after
    dependency injection is done to perform any initialization. The initialization
    method init() is the first method invoked before this class is put into service.
     */
    @PostConstruct
    public void init() {
        listOfPhotos = new ArrayList<>();

        listOfPhotos.add(new Photo("/resources/images/photos/photo1.jpg", "/resources/images/photos/photo1s.jpg",
                "Description for Photo 1", "Cybersecurity Evaluation"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo2.jpg", "/resources/images/photos/photo2s.jpg",
                "Description for Photo 2", "Software Quality Evaluation"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo3.jpg", "/resources/images/photos/photo3s.jpg",
                "Description for Photo 3", "Software Verification, Validation and Testing"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo4.jpg", "/resources/images/photos/photo4s.jpg",
                "Description for Photo 4", "Data Quality and Credibility Assessment"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo5.jpg", "/resources/images/photos/photo5s.jpg",
                "Description for Photo 5", "Enterprise Architecture Evaluation"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo6.jpg", "/resources/images/photos/photo6s.jpg",
                "Description for Photo 6", "System Interoperability Evaluation"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo7.jpg", "/resources/images/photos/photo7s.jpg",
                "Description for Photo 7", "Digital Educational Game Software Quality Evaluation"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo8.jpg", "/resources/images/photos/photo8s.jpg",
                "Description for Photo 8", "Requirements Credibility Assessment"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo9.jpg", "/resources/images/photos/photo9s.jpg",
                "Description for Photo 9", "Emergency Response Management Quality Evaluation"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo10.jpg", "/resources/images/photos/photo10s.jpg",
                "Description for Photo 10", "Software Application Evaluation for Certification"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo11.jpg", "/resources/images/photos/photo11s.jpg",
                "Description for Photo 11", "Server Security Evaluation"));
        listOfPhotos.add(new Photo("/resources/images/photos/photo12.jpg", "/resources/images/photos/photo12s.jpg",
                "Description for Photo 12", "Risk Assessment"));
    }

    /*
    =============
    Getter Method
    =============
     */
    public List<Photo> getListOfPhotos() {
        return listOfPhotos;
    }
}
