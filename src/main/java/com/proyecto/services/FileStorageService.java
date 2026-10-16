package com.proyecto.services;

import com.proyecto.security.BusinessException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
  private static final Set<String> BUCKETS =
      Set.of("imagenes", "carta", "productos", "categorias_comidas", "promociones");
  private final Path root;
  private final long maxBytes;

  public FileStorageService(
      @Value("${app.storage-root:RIDE_MEAL}") String root,
      @Value("${app.storage-max-bytes:5368709120}") long maxBytes) {
    this.root = Paths.get(root).toAbsolutePath().normalize();
    this.maxBytes = maxBytes;
  }

  public final class StoredImage implements AutoCloseable {
    private final Path file;
    private boolean committed;

    private StoredImage(Path file) {
      this.file = file;
    }

    public String filename() {
      return file == null ? null : file.getFileName().toString();
    }

    public void commit() {
      committed = true;
    }

    public void close() throws IOException {
      if (!committed && file != null) Files.deleteIfExists(file);
    }
  }

  public synchronized StoredImage storeImage(MultipartFile upload, String bucket)
      throws IOException {
    if (upload == null || upload.isEmpty()) return new StoredImage(null);
    if (!BUCKETS.contains(bucket) || upload.getSize() > 10L * 1024 * 1024)
      throw BusinessException.invalid("La imagen supera el límite de 10 MB.");
    Files.createDirectories(root);
    Path dir = root.resolve(bucket);
    Files.createDirectories(dir);
    if (Files.isSymbolicLink(root) || Files.isSymbolicLink(dir))
      throw new IOException("Directorio de almacenamiento no válido");
    try (var paths = Files.walk(root)) {
      long used =
          paths
              .filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS))
              .mapToLong(
                  p -> {
                    try {
                      return Files.size(p);
                    } catch (IOException e) {
                      throw new UncheckedIOException(e);
                    }
                  })
              .sum();
      if (used + 10L * 1024 * 1024 > maxBytes)
        throw BusinessException.invalid(
            "Se alcanzó el límite de almacenamiento. Contacta al administrador.");
    }
    if (Files.getFileStore(dir).getUsableSpace() < 100L * 1024 * 1024)
      throw new IOException("Espacio insuficiente");
    BufferedImage image;
    try (InputStream raw = upload.getInputStream();
        ImageInputStream in = ImageIO.createImageInputStream(raw)) {
      if (in == null) throw BusinessException.invalid("El archivo no es una imagen válida.");
      var readers = ImageIO.getImageReaders(in);
      if (!readers.hasNext())
        throw BusinessException.invalid("Solo se permiten imágenes PNG o JPEG.");
      var reader = readers.next();
      try {
        reader.setInput(in, true, true);
        String format = reader.getFormatName().toLowerCase(Locale.ROOT);
        int width = reader.getWidth(0), height = reader.getHeight(0);
        if (!Set.of("png", "jpeg", "jpg").contains(format)
            || width < 1
            || height < 1
            || width > 6000
            || height > 6000
            || (long) width * height > 12000000)
          throw BusinessException.invalid(
              "Usa una imagen PNG/JPEG de hasta 12 megapíxeles y 6000 píxeles por lado.");
        image = reader.read(0);
        if (image == null) throw BusinessException.invalid("La imagen está dañada.");
      } finally {
        reader.dispose();
      }
    }
    Path temp = Files.createTempFile(dir, "upload-", ".tmp"),
        dest = dir.resolve(UUID.randomUUID() + ".png");
    try {
      try (OutputStream file = Files.newOutputStream(temp);
          OutputStream out =
              new FilterOutputStream(file) {
                long count;

                public void write(int b) throws IOException {
                  if (++count > 10L * 1024 * 1024) throw new IOException("Imagen demasiado grande");
                  out.write(b);
                }

                public void write(byte[] b, int off, int len) throws IOException {
                  count += len;
                  if (count > 10L * 1024 * 1024) throw new IOException("Imagen demasiado grande");
                  out.write(b, off, len);
                }
              }) {
        if (!ImageIO.write(image, "png", out)) throw new IOException("No se pudo codificar PNG");
      }
      try {
        Files.move(temp, dest, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(temp, dest);
      }
      return new StoredImage(dest);
    } finally {
      image.flush();
      Files.deleteIfExists(temp);
    }
  }
}
