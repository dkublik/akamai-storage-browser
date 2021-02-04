package pl.dk.akamaibrowser;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.dk.akamaibrowser.model.File;
import reactor.core.publisher.Mono;

import static org.springframework.util.StringUtils.isEmpty;

@Controller
@AllArgsConstructor
class ContentController {

    private final AkamaiBrowser akamaiBrowser;

    // http://localhost:8082/browse?path=images/2021/2/
    @GetMapping("/browse")
    public Mono<String> content(@RequestParam String path, Model model) {
        return akamaiBrowser.dir(path)
                     .map(stat -> {
                         String contentHtml = "";
                         contentHtml += "<tr class='dir'>";
                         String aHref = upUrl(path);
                         contentHtml += "<td><a href='" + aHref  + "'>..</a></td>";
                         contentHtml += "<td/><td/><td/><td/><td/><td/>";
                         contentHtml += "</tr>";
                         for (File file: stat.getFiles()) {
                             if (file.getType().equals("dir")) {
                                 contentHtml += "<tr class='dir'>";
                                 aHref = browseUrl(path, file.getName());
                             } else {
                                 contentHtml += "<tr class='file'>";
                                 aHref = previewUrl(path, file.getName());
                             }

                             contentHtml += "<td><a href='" + aHref  + "'>" + file.getName() + "</a></td>";
                             contentHtml += "<td>" + file.getType() + "</td>";
                             contentHtml += "<td>" + (file.getSize() != 0 ? file.getSize() : file.getBytes()) + "</td>";
                             contentHtml += "<td>" + file.getFiles() + "</td>";
                             contentHtml += "<td>" + file.getMtime() + "</td>";
                             contentHtml += "<td>" + file.getMd5() + "</td>";
                             contentHtml += "<td>" + file.isImplicit() + "</td>";
                             contentHtml += "</tr>";
                         }

                         model.addAttribute("contentHtml", contentHtml);
                         return "content";
                     });
    }

    private String upUrl(String path) {
        if (isEmpty(path)) {
            return "/browse?path=";
        }
        return "/browse?path=" + path.substring(0, path.lastIndexOf("/", path.length() - 2) + 1);
    }

    private String browseUrl(String path, String fileName) {
        return "/browse?path=" + path + fileName + "/";
    }

    private String previewUrl(String path, String fileName) {
        return "https://paramountnetwork.mtvnimages.com/uri/mgid:file:akamai:lumen:/" + path + "/" + fileName;
    }
}
