package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.FileId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.util.HtmlUtils;

/**
 * REST controller for Hiero file operations. Provides endpoints for creating, deleting, updating
 * and querying files on the network.
 */
@Tag(name = "Files", description = "Operations related to Hiero File Service (HFS)")
@RestController
@RequestMapping("/api/v1/hiero/files")
public class FileController {

  private final FileClient fileClient;

  public FileController(final FileClient fileClient) {
    this.fileClient = Objects.requireNonNull(fileClient, "fileClient must not be null");
  }

  /** Creates a new file. */
  @Operation(
      summary = "Create a new file",
      description = "Creates a new file on the Hiero network with the provided content.")
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

  /** Deletes a file. */
  @Operation(
      summary = "Delete a file",
      description = "Deletes an existing file from the Hiero network.")
  @DeleteMapping("/{fileId}")
  public String deleteFile(@PathVariable("fileId") final String fileId) {
    try {
      fileClient.deleteFile(fileId.trim());
      return "File " + HtmlUtils.htmlEscape(fileId) + " deleted successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to delete file: " + fileId, e);
    }
  }

  /** Updates an existing file (Content and/or Expiration Time). */
  @Operation(
      summary = "Update a file",
      description = "Updates the content or expiration time of an existing file.")
  @PostMapping("/{fileId}")
  public String updateFile(
      @PathVariable("fileId") final String fileId, @RequestBody final FileUpdateRequest request) {
    try {
      final FileId fid = FileId.fromString(fileId.trim());
      if (request.content() != null) {
        fileClient.updateFile(fid, request.getDecodedContent());
      }
      if (request.expirationTime() != null) {
        fileClient.updateExpirationTime(fid, request.getExpirationInstant());
      }
      return "File " + HtmlUtils.htmlEscape(fileId) + " updated successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to update file: " + fileId, e);
    }
  }

  /** Retrieves the content of a file (returned as Base64 string). */
  @Operation(
      summary = "Get file content",
      description = "Retrieves the byte content of a file, encoded as a Base64 string.")
  @GetMapping("/{fileId}/content")
  public String getFileContent(@PathVariable("fileId") final String fileId) {
    try {
      final byte[] content = fileClient.readFile(fileId.trim());
      return Base64.getEncoder().encodeToString(content);
    } catch (final Exception e) {
      throw new RuntimeException("Failed to read file content: " + fileId, e);
    }
  }

  /** Retrieves the size of a file in bytes. */
  @Operation(summary = "Get file size", description = "Retrieves the size of a file in bytes.")
  @GetMapping("/{fileId}/size")
  public Integer getFileSize(@PathVariable("fileId") final String fileId) {
    try {
      return fileClient.getSize(FileId.fromString(fileId.trim()));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to get file size: " + fileId, e);
    }
  }
}
