##### KV Store with storage engine as SQL database.
![K-V Store](design.jpg)


Operations:
1) PUT (K, V, TTL)
   ```
   INSERT INTO kvstore (key, value, expiry) VALUES (?, ?, ?) ON CONFLICT(key) DO UPDATE SET value = ?, expiry = ?
   ```
2) GET(K)
   ```
   SELECT value FROM kvstore WHERE key=? and expiry > now()
   ```
3) DEL(K)
    ```
    UPDATE kvstore SET expiry = null WHERE key = ? and expiry > now()
    ```
4) Periodic Cleanup (Batch delete)
    ```
   DELETE FROM kvstore WHERE key IN
       (SELECT key FROM kvstore WHERE expiry is null or expiry <= now() limit 100)
   ```
