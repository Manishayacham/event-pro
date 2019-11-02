package com.eventpro.app.controller;

import com.eventpro.app.model.Asset;
import com.eventpro.app.security.JwtTokenProvider;
import com.eventpro.app.service.AssetService;
import com.eventpro.app.util.Utils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** @author choang on 10/23/19 */
@CrossOrigin("*")
@RestController
@RequestMapping("/assets")
@Slf4j
@RequiredArgsConstructor
public class AssetController {
  private final JwtTokenProvider tokenProvider;
  private final AssetService assetService;

  @PostMapping("/upload")
  public ResponseEntity<Asset> createAsset(
      @RequestParam(value = "file") MultipartFile file,
      @RequestParam String description,
      HttpServletRequest request) {
    String username = tokenProvider.getUserLogin(tokenProvider.resolveToken(request));

    String fileName = Utils.getUUID(7);

    return ResponseEntity.ok(assetService.createAsset(fileName, description, file, username));
  }

  @GetMapping("/users")
  public ResponseEntity<List<Asset>> listAssets(HttpServletRequest request) {
    String username = tokenProvider.getUserLogin(tokenProvider.resolveToken(request));

    return ResponseEntity.ok(assetService.listAssets(username));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Asset> deleteAsset(@PathVariable long id, HttpServletRequest request) {
    String username = tokenProvider.getUserLogin(tokenProvider.resolveToken(request));

    // TODO Verify the actual user or admin before delete, pass username to service
    assetService.deleteAsset(id);

    return ResponseEntity.ok(Asset.builder().id(id).build());
  }
}
