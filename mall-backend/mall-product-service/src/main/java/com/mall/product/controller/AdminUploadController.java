package com.mall.product.controller;

import com.mall.common.BizException;
import com.mall.common.Result;
import com.mall.common.config.MinioUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/** 平台运营图片上传（type=1；广告图），上传 MinIO 返回公开 URL，数据库仅存 URL。 */
@Slf4j
@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
public class AdminUploadController {

    private final MinioUploader minioUploader;

    @PostMapping("/image")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("请选择要上传的图片");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new BizException("仅支持图片文件（jpg/png/webp 等）");
        }
        String ext = switch (ct) {
            case "image/jpeg" -> "jpg";
            case "image/webp" -> "webp";
            default -> "png";
        };
        try {
            String url = minioUploader.upload(file.getInputStream(), file.getSize(), ct, ext);
            return Result.ok(Map.of("url", url));
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("[平台图片] 上传处理失败", e);
            throw new BizException("图片上传失败，请稍后重试");
        }
    }
}
