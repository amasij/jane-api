package com.jane.controllers;

import com.jane.ImageRepository;
import com.jane.entity.Image;
import com.jane.enumeration.GenericStatusConstant;
import com.jane.exception.ErrorResponse;
import com.jane.security.constraint.Public;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping("files")
@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageRepository imageRepository;

    @Public
    @GetMapping("/{id:[0-9]+}")
    public RedirectView downloadFile(@PathVariable("id")
                                             long id, HttpServletResponse response) throws IOException {
        Image image = imageRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, "File not found"));
        response.setContentType(image.getContentType());
        IOUtils.write(image.getData(), response.getOutputStream());
        response.flushBuffer();
        return null;
    }

    @Transactional
    @Public
    @PostMapping("/upload")
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Image image = new Image();
        image.setContentType(file.getContentType());
        image.setDateCreated(LocalDateTime.now());
        image.setStatus(GenericStatusConstant.ACTIVE);
        image.setData(file.getBytes());
        Image savedImage = imageRepository.save(image);
        return ResponseEntity.ok(savedImage.getId());
    }


}
