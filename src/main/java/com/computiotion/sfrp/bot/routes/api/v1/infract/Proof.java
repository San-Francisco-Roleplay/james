package com.computiotion.sfrp.bot.routes.api.v1.infract;

import com.computiotion.sfrp.bot.Error;
import com.computiotion.sfrp.bot.*;
import com.computiotion.sfrp.bot.infractions.InfractionMessageUtils;
import com.computiotion.sfrp.bot.infractions.InfractionReference;
import com.computiotion.sfrp.bot.infractions.QueuedInfraction;
import com.computiotion.sfrp.bot.reference.ReferenceDataImpl;
import com.computiotion.sfrp.bot.reference.ReferenceManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.computiotion.sfrp.bot.Hooks.getAuth;

@RestController
public class Proof {
    public static final String[] ALLOWED_FILE_FORMATS = new String[]{"png", "jpg", "webp", "webm", "mov", "mp4", "gif"};
    private static final S3Client s3 = Generators.getS3();
    private static final Snowflake snowflake = new Snowflake();
    private static final Log log = LogFactory.getLog(Proof.class);

    @GetMapping(value = "/v1/infract/{id}/proof/{img}")
public ResponseEntity<?> getProofImage(@PathVariable("id") String id, @PathVariable("img") String image, @RequestParam(value = "token", required = false) String auth) {
    String token = getAuth(auth);

    if (token == null) {
        return Error.MissingAuth.getResponse();
    }

    QueuedInfraction collection = QueuedInfraction.getCollection(id);
    if (collection == null) {
        log.trace("No collection found for ID: " + id);
        return Error.ResourceNotFound.getResponse();
    }

    if (!collection.getToken().equals(token)) {
        return Error.Unauthorized.getResponse();
    }

    try {
        String fileExtension = image.substring(image.lastIndexOf('.') + 1).toLowerCase();
        String key = "IP-" + id + "-" + image;
        log.trace("Key ID: " + key);
        byte[] imageBytes = s3.getObject(builder -> builder.bucket(ConfigManager.getR2Bucket()).key(key)).readAllBytes();
        MediaType mediaType = switch (fileExtension) {
            case "png" -> MediaType.IMAGE_PNG;
            case "webp" -> MediaType.valueOf("image/webp");
            case "webm" -> MediaType.valueOf("video/webm");
            case "mov" -> MediaType.valueOf("video/quicktime");
            case "mp4" -> MediaType.valueOf("video/mp4");
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        return ResponseEntity.ok().contentType(mediaType).body(imageBytes);
    } catch (Exception e) {
        log.error(e);
        return Error.ResourceNotFound.getResponse();
    }
}

    @PostMapping(value = "/v1/infract/{id}/proof", produces = "application/json", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> getProof(@PathVariable("id") String id, MultipartHttpServletRequest request) {
        String token = getAuth(Objects.requireNonNullElse(request.getRequestHeaders().get("Authorization"), null).getFirst());

        if (token == null) {
            return Error.MissingAuth.getResponse();
        }

        QueuedInfraction collection = QueuedInfraction.getCollection(id);
        if (collection == null) {
            return Error.ResourceNotFound.getResponse();
        }

        if (!collection.getToken().equals(token)) {
            return Error.Unauthorized.getResponse();
        }

        if (!collection.getProofIds().isEmpty() || collection.getProofMessage() != null) {
            return Error.ProofAlreadyExists.getResponse();
        }

        String message = request.getParameter("message");
        if (message == null) {
            message = "";
        }

        message = message.trim();

        Iterator<String> fileNames = request.getFileNames();
        List<MultipartFile> files = new ArrayList<>();
        while (fileNames.hasNext()) {
            String fileName = fileNames.next();
            MultipartFile file = request.getFile(fileName);

            if (file != null) {
                String fileExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.') + 1).toLowerCase();
                if (Arrays.stream(ALLOWED_FILE_FORMATS).noneMatch(fileExtension::equals)) {
                    return Error.ProofInvalidFileFormat.getResponse(String.join(", ", ALLOWED_FILE_FORMATS), fileExtension);
                }
                files.add(file);
            }
        }

        if (files.size() > 10) {
            return Error.TooMuchProof.getResponse();
        }

        AtomicLong totalSize = new AtomicLong();
        files.stream().map(MultipartFile::getSize).toList().forEach(totalSize::addAndGet);

        if (totalSize.get() > 3L * 1024 * 1024 * 1024) {
            return Error.ProofFileTooLarge.getResponse(totalSize.get() + " bytes");
        }

        if (message.length() > 4000) {
            return Error.ProofMessageTooLong.getResponse(message.length() + " characters");
        }

        if (message.isEmpty() && files.isEmpty()) {
            return Error.ProofNoContent.getResponse();
        }

        List<String> keys = new ArrayList<>();
        List<String> uploadedFiles = files.stream().map(file -> {
            String ext = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.') + 1);
            String key = snowflake.nextId();
            keys.add(key + "." + ext);
            try {
                s3.putObject(builder -> builder.bucket(ConfigManager.getR2Bucket()).key("IP-" + collection.getId() + "-" + key + "." + ext).contentType(file.getContentType()).contentLength(file.getSize()).build(),
                        RequestBody.fromBytes(file.getBytes()));
                return key;
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
            }
        }).toList();

        // Add the uploaded file keys and message to the infraction proof
        collection.addProof(keys.toArray(String[]::new));
        collection.setProofMessage(message.isEmpty() ? null : message);
        collection.regenToken();

        JDA jda = BotApplication.getJda();

        Message initial = Objects.requireNonNull(jda.getChannelById(MessageChannel.class, collection.getBuilderChannelId()))
                .retrieveMessageById(collection.getBuilderMessageId()).complete();

        Objects.requireNonNull(jda.getUserById(collection.getLeader())).openPrivateChannel().queue(channel -> {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("Proof Uploaded")
                    .setDescription("Proof has been uploaded for the infraction with ID `" + collection.getId() + "`. Please return to the [original builder](" + initial.getJumpUrl() + ") to complete the infraction. You may view the uploaded proof by replying to the original infraction message with \n```proof\n``` and may re-upload the proof with `proof remove` and running `proof link` again.")
                    .setColor(Colors.Green.getColor()).build()).queue();
        });

        EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(collection);
        assert queuedMessage != null;

        queuedMessage.appendDescription("**CMD:** Successfully uploaded proof for infraction.");

        initial.editMessageEmbeds(queuedMessage.build())
                .queue();

        ReferenceManager.registerData(new ReferenceDataImpl("infract_queue", collection.getBuilderMessageId(), new InfractionReference(collection.getId())));

        return ResponseEntity.status(204).build();
    }
}
