package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.FileId;
import java.util.Base64;
import java.util.Objects;
import org.hiero.base.FileClient;
import org.hiero.spring.sample.dto.file.FileCreateRequest;
import org.hiero.spring.sample.dto.file.FileUpdateRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Hiero file operations.
 * Provides endpoints for creating, deleting, updating and querying files on the network.
 */
@RestController
@RequestMapping("/api/v1/hiero/files")
public class FileController {

  private final FileClient fileClient;

  public FileController(final FileClient fileClient) {
    this.fileClient = Objects.requireNonNull(fileClient, "fileClient must not be null");
  }

  /**
   * Creates a new file.
   */
  @PostMapping
  public String createFile(@RequestBody final FileCreateRequest request) {
    try {
      final FileId fileId;
      if (request.expirationTime() != null) {
        fileId = fileClient.createFile(request.getDecodedContent(), request.getExpirationInstant());
      } else {
        fileId = fileClient.createFile(request.getDecodedContent());
      }
      return "File " + fileId + " created successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to create file", e);
    }
  }

  /**
   * Deletes a file.
   */
  @DeleteMapping("/{fileId}")
  public String deleteFile(@PathVariable("fileId") final String fileId) {
    try {
      fileClient.deleteFile(fileId);
      return "File " + fileId + " deleted successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to delete file: " + fileId, e);
    }
  }

  /**
   * Updates an existing file (Content and/or Expiration Time).
   */
  @PostMapping("/{fileId}")
  public String updateFile(
      @PathVariable("fileId") final String fileId,
      @RequestBody final FileUpdateRequest request) {
    try {
      final FileId fid = FileId.fromString(fileId);
      if (request.content() != null) {
        fileClient.updateFile(fid, request.getDecodedContent());
      }
      if (request.expirationTime() != null) {
        fileClient.updateExpirationTime(fid, request.getExpirationInstant());
      }
      return "File " + fileId + " updated successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to update file: " + fileId, e);
    }
  }

  /**
   * Retrieves the content of a file (returned as Base64 string).
   */
  @GetMapping("/{fileId}/content")
  public String getFileContent(@PathVariable("fileId") final String fileId) {
    try {
      final byte[] content = fileClient.readFile(fileId);
      return Base64.getEncoder().encodeToString(content);
    } catch (final Exception e) {
      throw new RuntimeException("Failed to read file content: " + fileId, e);
    }
  }

  /**
   * Retrieves the size of a file in bytes.
   */
  @GetMapping("/{fileId}/size")
  public Integer getFileSize(@PathVariable("fileId") final String fileId) {
    try {
      return fileClient.getSize(FileId.fromString(fileId));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to get file size: " + fileId, e);
    }
  }
}
