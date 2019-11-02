package com.eventpro.app.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.eventpro.app.exception.SystemException;
import com.eventpro.app.model.Asset;
import com.eventpro.app.model.User;
import com.eventpro.app.config.AwsProperties;
import com.eventpro.app.service.AmazonS3Service;
import com.eventpro.app.service.UserService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** @author choang on 10/23/19 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {
  private final AmazonS3 amazonS3;
  private final AwsProperties awsProperties;
  private final UserService userService;

  @Override
  public Asset uploadFile(String fileName, String filePath, String username) {
    User user = userService.getUserByUsername(username);

    Path p = Paths.get(filePath);
    String rootFileName = p.getFileName().toString();

    fileName = String.format("%s-%s", fileName, rootFileName);

    String s3Key = resolveS3Key(username, fileName, rootFileName);

    PutObjectResult putObjectResult =
        amazonS3.putObject(
            new PutObjectRequest(awsProperties.getS3Bucket(), fileName, filePath)
                .withCannedAcl(CannedAccessControlList.PublicRead));

    String s3Url = String.format("%s/%s", awsProperties.getS3Url(), fileName);
    String cloudFrontUrl = String.format("%s/%s", awsProperties.getCloudFrontUrl(), fileName);
    String accelerateUrl = String.format("%s/%s", awsProperties.getS3AccelerateUrl(), fileName);

    return Asset.builder()
        .s3Key(s3Key)
        .fileName(fileName)
        .s3Url(s3Url)
        .cloudFrontUrl(cloudFrontUrl)
        .accelerationTransferUrl(accelerateUrl)
        .version(putObjectResult.getVersionId())
        .user(user)
        .createdBy(username)
        .lastModifiedBy(username)
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();
  }

  private String resolveS3Key(String username, String fileName, String rootFileName) {
    return String.format("%s/%s-%s", username, fileName, rootFileName);
  }

  @Override
  public Asset uploadFile(String fileName, MultipartFile multipartFile, String username) {
    User user = userService.getUserByUsername(username);

    fileName = String.format("%s-%s", fileName, multipartFile.getOriginalFilename());

    String s3Key = resolveS3Key(username, fileName, multipartFile.getOriginalFilename());

    File file = convertMultipartToFile(multipartFile);

    PutObjectResult putObjectResult =
        amazonS3.putObject(
            new PutObjectRequest(awsProperties.getS3Bucket(), fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));

    file.delete();

    String s3Url = String.format("%s/%s", awsProperties.getS3Url(), fileName);
    String cloudFrontUrl = String.format("%s/%s", awsProperties.getCloudFrontUrl(), fileName);
    String accelerateUrl = String.format("%s/%s", awsProperties.getS3AccelerateUrl(), fileName);

    return Asset.builder()
        .s3Key(s3Key)
        .fileName(fileName)
        .s3Url(s3Url)
        .cloudFrontUrl(cloudFrontUrl)
        .accelerationTransferUrl(accelerateUrl)
        .version(putObjectResult.getVersionId())
        .user(user)
        .createdBy(username)
        .lastModifiedBy(username)
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();
  }

  private File convertMultipartToFile(MultipartFile multipartFile) {
    File file = new File(multipartFile.getOriginalFilename());

    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(multipartFile.getBytes());
    } catch (IOException ex) {
      log.error("Error getting file from system", ex);
      throw new SystemException(
          "Error getting file from system: " + multipartFile.getOriginalFilename(),
          HttpStatus.BAD_REQUEST);
    }
    return file;
  }

  @Override
  public Asset downloadFile(String fileName) {
    return null;
  }

  @Override
  public Asset deleteFile(String fileName) {
    try {
      amazonS3.deleteObject(new DeleteObjectRequest(awsProperties.getS3Bucket(), fileName));
    } catch (AmazonServiceException ex) {
      log.error("Error getting file from system", ex);
      throw new SystemException("Error deleting file from S3: " + fileName, HttpStatus.BAD_REQUEST);
    }

    return Asset.builder().fileName(fileName).build();
  }

  @Override
  public List<Asset> listFiles(String username) {
    return null;
  }
}
