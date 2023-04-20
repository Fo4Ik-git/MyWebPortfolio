package com.fo4ik.mySite.controllers.settings;

import com.fo4ik.mySite.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings/files")
public class FileController {

    private static final String FILE_PAGE = "pages/settings/files";

    @GetMapping("")
    public String filePage(@AuthenticationPrincipal User user, Model model) {
        return FILE_PAGE;
    }

}
