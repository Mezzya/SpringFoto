package ua.kiev.prog;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/")
public class MyController {

    private Map<Long, byte[]> photos = new HashMap<Long, byte[]>();

    @RequestMapping("/")
    public String onIndex() {
        return "index";
    }

    @RequestMapping(value = "/add_photo", method = RequestMethod.POST)
    public String onAddPhoto(Model model, @RequestParam MultipartFile photo) {
        if (photo.isEmpty())
            throw new PhotoErrorException();

        try {
            long id = System.currentTimeMillis();
            photos.put(id, photo.getBytes());

            model.addAttribute("photo_id", id);
            return "result";
        } catch (IOException e) {
            throw new PhotoErrorException();
        }
    }

    @RequestMapping("/photo/{photo_id}")
    public ResponseEntity<byte[]> onPhoto(@PathVariable("photo_id") long id) {
        return photoById(id);
    }

    @RequestMapping(value = "/view", method = RequestMethod.POST)
    public ResponseEntity<byte[]> onView(@RequestParam("photo_id") long id) {
        return photoById(id);
    }

    @RequestMapping("/delete/{photo_id}")
    public String onDelete(@PathVariable("photo_id") long id) {
        if (photos.remove(id) == null)
            throw new PhotoNotFoundException();
        else
            return "index";
    }

    @RequestMapping("/view_all")
    public String allView(Model model)
    {
        List<Long> result = new ArrayList<Long>(photos.keySet());
        model.addAttribute("list",result);
        return "all";

    }

    @RequestMapping(value = "/del_many", method = RequestMethod.POST)
    public String delChecked(@RequestParam("id") long[] id)
    {
        for (int i = 0; i < id.length ; i++) {
            photos.remove(id[i]);
        }
        return "index";
    }

    @RequestMapping( value = "/getzip", produces="application/zip", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getZip(@RequestParam("id") long id)
    {
        byte[] result = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(bos))
        {
            ZipEntry zipEntry = new ZipEntry(""+id);
            zipEntry.setSize(photos.get(id).length);

            zos.putNextEntry(zipEntry);
            zos.write(photos.get(id));
            zos.closeEntry();
            zos.close();
            result = bos.toByteArray();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("KU!!!");
        return ResponseEntity
                .ok()
                .contentLength(result.length)
                .body(result);

    }

    private ResponseEntity<byte[]> photoById(long id) {
        byte[] bytes = photos.get(id);
        if (bytes == null)
            throw new PhotoNotFoundException();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
    }
}
