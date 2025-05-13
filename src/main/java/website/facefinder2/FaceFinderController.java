package website.facefinder2;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api")
public class FaceFinderController {

    private final String BASE_DIR = System.getProperty("user.dir");
    private final String UPLOAD_DIR = BASE_DIR + File.separator + "uploads";
    private final String SELFIE_DIR = UPLOAD_DIR + File.separator + "selfie";
    private final String PHOTOS_DIR = UPLOAD_DIR + File.separator + "photos";
    private final String RESULTS_DIR = UPLOAD_DIR + File.separator + "results";
    private final String RESULTS_ZIP = UPLOAD_DIR + File.separator + "results.zip";

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(
            @RequestParam("selfie") MultipartFile selfie,
            @RequestParam("photos") MultipartFile[] photos) {

        try {
            // 1. Ensure directories exist
            Files.createDirectories(Paths.get(SELFIE_DIR));
            Files.createDirectories(Paths.get(PHOTOS_DIR));
            Files.createDirectories(Paths.get(RESULTS_DIR));

            // 2. Clean previous uploads
            cleanDirectory(SELFIE_DIR);
            cleanDirectory(PHOTOS_DIR);
            cleanDirectory(RESULTS_DIR);

            // 3. Save selfie
            String selfiePath = SELFIE_DIR + "/" + selfie.getOriginalFilename();
            selfie.transferTo(new File(selfiePath));

            // 4. Save photos
            for (MultipartFile photo : photos) {
                String photoPath = PHOTOS_DIR + "/" + photo.getOriginalFilename();
                photo.transferTo(new File(photoPath));
            }

            // 5. Call Python script
            ProcessBuilder pb = new ProcessBuilder("python", "C:/Users/KIIT/Desktop/facefinder2/scripts/face_finder.py");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Capture output for debugging
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitCode = process.waitFor();

            // 6. List matched images
            File resultsFolder = new File(RESULTS_DIR);
            String[] matchedImages = resultsFolder.list();

            Map<String, Object> response = new HashMap<>();
            response.put("status", exitCode == 0 ? "success" : "error");
            response.put("output", output.toString());
            response.put("matchedImages", matchedImages);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("output", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/download-results")
    public ResponseEntity<Resource> downloadResults() throws IOException {
        // Zip the results folder
        zipResultsFolder();

        FileSystemResource resource = new FileSystemResource(RESULTS_ZIP);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=results.zip")
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private void zipResultsFolder() throws IOException {
        File dir = new File(RESULTS_DIR);
        File zipFile = new File(RESULTS_ZIP);

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isFile()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zos.putNextEntry(zipEntry);
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zos.write(bytes, 0, length);
                        }
                    }
                }
            }
        }
    }

    private void cleanDirectory(String dirPath) throws IOException {
        Files.walk(Paths.get(dirPath))
                .filter(path -> !path.equals(Paths.get(dirPath)))
                .map(Path::toFile)
                .forEach(File::delete);
    }
}