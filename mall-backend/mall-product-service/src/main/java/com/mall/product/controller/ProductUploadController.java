package com.mall.product.controller;

import com.mall.common.BizException;
import com.mall.common.Result;
import com.mall.common.config.MinioUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 商品图片上传（商家 type=2；上传至 MinIO，返回公开 URL 由前端写入商品表单 main_img/img）。
 * 网关白名单已含 /api/portal/**；商家侧前端经 /api/product 携带 token 直达。
 */
@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductUploadController {

    private final MinioUploader minioUploader;

    @PostMapping("/upload/image")
    public Result<Map<String, String>> uploadImage(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                   @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("请选择要上传的图片");
        }
        // 仅允许图片类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BizException("仅支持图片文件（jpg/png/webp 等）");
        }
        String ext = "";
        String ct = contentType;
        if (contentType.equals("image/jpeg")) {
            ext = "jpg";
        } else if (contentType.equals("image/png")) {
            ext = "png";
        } else if (contentType.equals("image/webp")) {
            ext = "webp";
        } else {
            ext = "png";
        }
        try {
            String url = minioUploader.upload(file.getInputStream(), file.getSize(), ct, ext);
            return Result.ok(Map.of("url", url));
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("[商品图片] 上传处理失败", e);
            throw new BizException("图片上传失败，请稍后重试");
        }
    }
}
