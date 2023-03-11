package com.fo4ik.mySite.service;

import com.fo4ik.mySite.repo.CvRepo;
import org.springframework.stereotype.Service;

@Service
public class CvService {
    private final CvRepo cvRepo;

    public CvService(CvRepo cvRepo) {
        this.cvRepo = cvRepo;
    }

    public void saveCv() {



    }
}
