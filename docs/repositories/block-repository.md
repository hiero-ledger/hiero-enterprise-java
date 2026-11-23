# Block Repository

BlockRepository provides APIs for querying block information from a Hiero Mirror Node, including retrieving all blocks and searching blocks by block number or hash.

---

## Methods

| Method | Description |
|:-------|:------------|
| `findAll()` | Retrieves a paginated list of blocks from the Mirror Node. |
| `findByNumber(long number)` | Retrieves a block using its block number. |
| `findByHash(String hash)` | Retrieves a block using its hash value. |

---

## Find All Blocks

```java title="findAll()"
Page<Block> blocks =
    blockRepository.findAll();
```

---

## Find Block By Number

```java title="findByNumber(long number)"
Optional<Block> block =
    blockRepository.findByNumber(100);
```

---

## Find Block By Hash

```java title="findByHash(String hash)"
Optional<Block> block =
    blockRepository.findByHash(
        "0x123456789abcdef"
    );
```