package com.literacyhub.controller;

import com.literacyhub.dao.ResourceDAO;
import com.literacyhub.entity.Resource;
import com.literacyhub.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class ResourceController {
    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private ServletContext servletContext;

    // STUDENT: view with filter + search
    @GetMapping("/student/resources")
    public String studentResources(@RequestParam(value = "q", required = false) String q,
                                   @RequestParam(value = "category", required = false) String category,
                                   HttpSession session,
                                   Model model) {
        if (sessionExpired(session, "STUDENT")) return "redirect:/login?error=denied";

        List<Resource> resources = (q != null || (category != null && !category.isEmpty()))
                ? resourceDAO.search(q, category)
                : resourceDAO.findAll();
        List<String> categories = resourceDAO.findDistinctCategories();
        model.addAttribute("resources", resources);
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("categories", categories);
        return "student/resources";
    }

    @GetMapping("/student/resources/{id}")
    public String studentResourceDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (sessionExpired(session, "STUDENT")) return "redirect:/login?error=denied";

        Resource res = resourceDAO.findById(id);
        if (res == null) return "redirect:/student/resources?error=notfound";
        model.addAttribute("resource", res);
        return "student/resource-detail";
    }

    // PROFESSIONAL: list with manage actions
    @GetMapping("/professional/resources")
    public String professionalResources(@RequestParam(value = "q", required = false) String q,
                                        @RequestParam(value = "category", required = false) String category,
                                        HttpSession session,
                                        Model model) {
        if (sessionExpired(session, "PROFESSIONAL")) return "redirect:/login?error=denied";

        User user = (User) session.getAttribute("user");
        List<Resource> resources = (q != null || (category != null && !category.isEmpty()))
                ? resourceDAO.searchByOwner(q, category, user.getEmail())
                : resourceDAO.findByOwner(user.getEmail());
        List<String> categories = resourceDAO.findDistinctCategories();
        model.addAttribute("resources", resources);
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("categories", categories);
        return "professional/resources";
    }

    @GetMapping("/professional/resources/upload")
    public String showUpload(HttpSession session, Model model) {
        if (sessionExpired(session, "PROFESSIONAL")) return "redirect:/login?error=denied";
        model.addAttribute("resource", new Resource());
        return "professional/upload-resource";
    }

    @PostMapping("/professional/resources/upload")
    public String handleUpload(@ModelAttribute("resource") Resource resource,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session,
                               Model model) throws IOException {
        if (sessionExpired(session, "PROFESSIONAL")) return "redirect:/login?error=denied";

        User user = (User) session.getAttribute("user");

        // Use absolute path approach
        String uploadsDir = servletContext.getRealPath("/uploads");
        if (uploadsDir == null) {
            uploadsDir = servletContext.getRealPath("/") + "uploads";
        }
        File dir = new File(uploadsDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = UUID.randomUUID() + "_" + (file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        File dest = new File(dir, filename);
        if (!file.isEmpty()) {
            file.transferTo(dest);
            resource.setFileURL("/uploads/" + filename);
        }

        resource.setUploadedBy(user.getEmail());
        resource.setUploadedByName(user.getFirstName() + " " + user.getLastName());
        resource.setUpdatedAt(LocalDateTime.now());
        resourceDAO.save(resource);

        return "redirect:/professional/resources?success=uploaded";
    }

    @GetMapping("/professional/resources/{id}/edit")
    public String editResource(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (sessionExpired(session, "PROFESSIONAL")) return "redirect:/login?error=denied";
        
        User user = (User) session.getAttribute("user");
        Resource res = resourceDAO.findById(id);
        if (res == null) return "redirect:/professional/resources?error=notfound";
        
        // Check ownership
        if (!res.getUploadedBy().equals(user.getEmail())) {
            return "redirect:/professional/resources?error=unauthorized";
        }
        
        model.addAttribute("resource", res);
        return "professional/edit-resource";
    }

    @PostMapping("/professional/resources/{id}/edit")
    public String handleEdit(@PathVariable("id") Long id,
                             @ModelAttribute("resource") Resource form,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             HttpSession session) throws IOException {
        if (sessionExpired(session, "PROFESSIONAL")) return "redirect:/login?error=denied";

        User user = (User) session.getAttribute("user");
        Resource res = resourceDAO.findById(id);
        if (res == null) return "redirect:/professional/resources?error=notfound";

        // Check ownership
        if (!res.getUploadedBy().equals(user.getEmail())) {
            return "redirect:/professional/resources?error=unauthorized";
        }

        res.setTitle(form.getTitle());
        res.setDescription(form.getDescription());
        res.setCategory(form.getCategory());

        if (file != null && !file.isEmpty()) {
            String uploadsDir = servletContext.getRealPath("/uploads");
            if (uploadsDir == null) {
                uploadsDir = servletContext.getRealPath("/") + "uploads";
            }
            File dir = new File(uploadsDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filename = UUID.randomUUID() + "_" + (file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
            File dest = new File(dir, filename);
            file.transferTo(dest);
            res.setFileURL("/uploads/" + filename);
        }

        res.setUpdatedAt(LocalDateTime.now());
        resourceDAO.update(res);

        return "redirect:/professional/resources?success=updated";
    }

    @PostMapping("/professional/resources/{id}/delete")
    public String deleteResource(@PathVariable("id") Long id, HttpSession session) {
        if (sessionExpired(session, "PROFESSIONAL")) return "redirect:/login?error=denied";
        
        User user = (User) session.getAttribute("user");
        Resource res = resourceDAO.findById(id);
        if (res == null) return "redirect:/professional/resources?error=notfound";
        
        // Check ownership
        if (!res.getUploadedBy().equals(user.getEmail())) {
            return "redirect:/professional/resources?error=unauthorized";
        }
        
        resourceDAO.delete(id);
        return "redirect:/professional/resources?success=deleted";
    }

    @GetMapping("/resource/download/{id}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadResource(@PathVariable Long id) {
        try {
            Resource resource = resourceDAO.findById(id);
            if (resource == null || resource.getFileURL() == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Extract filename from URL
            String fileUrl = resource.getFileURL();
            String filename = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            
            String uploadsDir = servletContext.getRealPath("/uploads");
            if (uploadsDir == null) {
                uploadsDir = servletContext.getRealPath("/") + "uploads";
            }
            
            Path filePath = Paths.get(uploadsDir, filename);
            File file = filePath.toFile();
            
            if (file.exists() && file.isFile() && file.canRead()) {
                org.springframework.core.io.Resource fileResource = new FileSystemResource(file);
                
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                        .body(fileResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public ResponseEntity<org.springframework.core.io.Resource> serveFile(@PathVariable String filename) {
        try {
            String uploadsDir = servletContext.getRealPath("/uploads");
            if (uploadsDir == null) {
                uploadsDir = servletContext.getRealPath("/") + "uploads";
            }
            
            Path filePath = Paths.get(uploadsDir, filename);
            File file = filePath.toFile();
            
            if (file.exists() && file.isFile() && file.canRead()) {
                org.springframework.core.io.Resource resource = new FileSystemResource(file);
                
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/debug/uploads")
    @ResponseBody
    public String debugUploads() {
        StringBuilder debug = new StringBuilder();
        debug.append("<html><head><title>Upload Debug Info</title>");
        debug.append("<style>body{font-family:Arial;margin:20px;}h2{color:#333;}pre{background:#f5f5f5;padding:10px;border:1px solid #ddd;}</style></head><body>");
        debug.append("<h1>Upload Directory Debug Information</h1>");
        
        try {
            String uploadsDir1 = servletContext.getRealPath("/uploads");
            String uploadsDir2 = servletContext.getRealPath("/");
            String actualUploadsDir = uploadsDir1 != null ? uploadsDir1 : (uploadsDir2 + "uploads");
            
            debug.append("<h2>Path Information:</h2>");
            debug.append("<pre>");
            debug.append("getRealPath('/uploads'): ").append(uploadsDir1 != null ? uploadsDir1 : "NULL").append("\n");
            debug.append("getRealPath('/'): ").append(uploadsDir2).append("\n");
            debug.append("Actual uploads directory: ").append(actualUploadsDir).append("\n");
            debug.append("</pre>");
            
            File uploadsDir = new File(actualUploadsDir);
            debug.append("<h2>Directory Status:</h2>");
            debug.append("<pre>");
            debug.append("Directory exists: ").append(uploadsDir.exists()).append("\n");
            debug.append("Is directory: ").append(uploadsDir.isDirectory()).append("\n");
            debug.append("Can read: ").append(uploadsDir.canRead()).append("\n");
            debug.append("Absolute path: ").append(uploadsDir.getAbsolutePath()).append("\n");
            debug.append("</pre>");
            
            if (uploadsDir.exists() && uploadsDir.isDirectory()) {
                debug.append("<h2>Files in Directory:</h2>");
                File[] files = uploadsDir.listFiles();
                if (files != null && files.length > 0) {
                    debug.append("<ul>");
                    for (File file : files) {
                        debug.append("<li>");
                        debug.append("<strong>").append(file.getName()).append("</strong>");
                        debug.append(" - Size: ").append(file.length()).append(" bytes");
                        debug.append(" - <a href='/uploads/").append(file.getName()).append("' target='_blank'>Try Open</a>");
                        debug.append("</li>");
                    }
                    debug.append("</ul>");
                } else {
                    debug.append("<p>No files found in uploads directory.</p>");
                }
            }
            
            // List all resources from DB
            List<Resource> resources = resourceDAO.findAll();
            debug.append("<h2>Resources in Database:</h2>");
            if (resources.isEmpty()) {
                debug.append("<p>No resources in database.</p>");
            } else {
                debug.append("<table border='1' cellpadding='5' style='border-collapse:collapse;'>");
                debug.append("<tr><th>ID</th><th>Title</th><th>File URL</th><th>Action</th></tr>");
                for (Resource r : resources) {
                    debug.append("<tr>");
                    debug.append("<td>").append(r.getResourceId()).append("</td>");
                    debug.append("<td>").append(r.getTitle()).append("</td>");
                    debug.append("<td>").append(r.getFileURL()).append("</td>");
                    debug.append("<td><a href='").append(r.getFileURL()).append("' target='_blank'>Open</a></td>");
                    debug.append("</tr>");
                }
                debug.append("</table>");
            }
            
        } catch (Exception e) {
            debug.append("<h2>Error:</h2><pre>").append(e.getMessage()).append("</pre>");
            e.printStackTrace();
        }
        
        debug.append("</body></html>");
        return debug.toString();
    }

    private boolean sessionExpired(HttpSession session, String requiredRole) {
        User user = (User) session.getAttribute("user");
        return user == null || !requiredRole.equalsIgnoreCase(user.getUserRole());
    }
}
