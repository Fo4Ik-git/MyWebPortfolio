package com.fo4ik.mySite.service;

import com.fo4ik.mySite.model.Footer;
import com.fo4ik.mySite.repo.FooterRepo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FooterService {

    private final FooterRepo footerRepo;


    public FooterService(FooterRepo footerRepo) {
        this.footerRepo = footerRepo;
    }

    public void createFooter(Map<String, String> link) {
        Footer footer = new Footer(link);
        footerRepo.save(footer);
    }
}
