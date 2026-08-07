# File Client

`FileClient` provides APIs for managing files on a Hiero network, including file creation, reading, updating, deletion, and metadata operations.

!!! note

    File operations that submit transactions to the Hiero network require HBAR to pay transaction fees.
    The configured operator account is used as the transaction payer and must have sufficient HBAR balance.

---

## Methods

| Method | Description |
|:-------|:------------|
| `createFile(byte[] contents)` | Creates a new file with the provided content. |
| `createFile(byte[] contents, Instant expirationTime)` | Creates a new file with content and expiration time. |
| `readFile(FileId fileId)` | Reads file contents using a file ID. |
| `readFile(String fileId)` | Reads file contents using a file ID string. |
| `deleteFile(FileId fileId)` | Deletes an existing file. |
| `deleteFile(String fileId)` | Deletes a file using a file ID string. |
| `updateFile(FileId fileId, byte[] content)` | Updates the contents of an existing file. |
| `updateExpirationTime(FileId fileId, Instant expirationTime)` | Updates the file expiration time. |
| `isDeleted(FileId fileId)` | Checks whether a file has been deleted. |
| `getSize(FileId fileId)` | Retrieves the file size. |
| `getExpirationTime(FileId fileId)` | Retrieves the file expiration time. |

---

## Create File

Creates a new file on the Hiero network.

```java title="createFile(byte[] contents)"
byte[] content = "Hello Hiero".getBytes();

FileId fileId = fileClient.createFile(content);
```

```java title="createFile(byte[] contents, Instant expirationTime)"
byte[] content = "Temporary file".getBytes();

FileId fileId = fileClient.createFile(
    content,
    Instant.now().plusSeconds(86400)
);
```

---

## Read 

Reads the contents of an existing file.

```java title="readFile(FileId fileId)"
FileId fileId = FileId.fromString("0.0.1234");

byte[] content = fileClient.readFile(fileId);
String contentString = new String(content);
```

---

## Delete File

Deletes an existing file.

```java title="deleteFile(FileId fileId)"
FileId fileId = FileId.fromString("0.0.1234");

fileClient.deleteFile(fileId);
```

---

## Update File

Updates file contents or metadata.

```java title="updateFile(FileId fileId, byte[] content)"
FileId fileId = FileId.fromString("0.0.1234");

fileClient.updateFile(
    fileId,
    "Updated content".getBytes()
);
```

```java title="updateExpirationTime(FileId fileId, Instant expirationTime)"
FileId fileId = FileId.fromString("0.0.1234");

fileClient.updateExpirationTime(
    fileId,
    Instant.now().plusSeconds(172800)
);
```

---

## File Metadata

Retrieves file status and metadata information.

```java title="isDeleted(FileId fileId)"
FileId fileId = FileId.fromString("0.0.1234");

boolean deleted = fileClient.isDeleted(fileId);
```

```java title="getSize(FileId fileId)"
FileId fileId = FileId.fromString("0.0.1234");

int size = fileClient.getSize(fileId);
```

```java title="getExpirationTime(FileId fileId)"
FileId fileId = FileId.fromString("0.0.1234");

Instant expiration = fileClient.getExpirationTime(fileId);
```
