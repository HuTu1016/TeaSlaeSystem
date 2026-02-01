package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.teasystem.common.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    // 上传文件存储的基础路径（从配置读取，默认为项目根目录下的 uploads）
    @Value("${file.upload-path:uploads}")
    private String uploadPath;

    // 文件访问的URL前缀
    @Value("${file.access-url:/api/uploads}")
    private String accessUrl;

    // 绝对路径（初始化时计算）
    private Path absoluteUploadPath;

    @PostConstruct
    public void init() {
        // 将相对路径转换为绝对路径
        Path path = Paths.get(uploadPath);
        if (!path.isAbsolute()) {
            // 相对路径：基于当前工作目录（项目根目录）
            absoluteUploadPath = Paths.get(System.getProperty("user.dir"), uploadPath).toAbsolutePath();
        } else {
            absoluteUploadPath = path;
        }

        // 确保上传目录存在
        try {
            if (!Files.exists(absoluteUploadPath)) {
                Files.createDirectories(absoluteUploadPath);
            }
            System.out.println("文件上传目录: " + absoluteUploadPath);
        } catch (IOException e) {
            System.err.println("创建上传目录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail(400, "请选择要上传的文件");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.fail(400, "只允许上传图片文件");
        }

        // 验证文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.fail(400, "图片大小不能超过5MB");
        }

        try {
            // 按日期创建子目录
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path uploadDir = absoluteUploadPath.resolve(dateDir);

            // 确保目录存在
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 保存文件（使用绝对路径）
            Path filePath = uploadDir.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 返回访问URL
            String fileUrl = accessUrl + "/" + dateDir + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", newFilename);

            return Result.success(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail(500, "文件上传失败：" + e.getMessage());
        }
    }
}
